package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.*;
import fr.ensimag.deca.tools.DecacInternalError;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.*;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.instructions.STORE;
import org.apache.log4j.Logger;

import java.io.PrintStream;
import static fr.ensimag.ima.pseudocode.Register.R0;
import static fr.ensimag.ima.pseudocode.Register.R1;


/**
 * Declaration of a class' field
 * @author gl01
 * @date 20/01/2020
 */
public class DeclField extends Tree {
    final private Visibility visibility;
    final private AbstractIdentifier type;
    final private AbstractIdentifier fieldName;
    final private AbstractInitialization initialization;
    private static final Logger LOG = Logger.getLogger(DeclField.class);
    private final AbstractExpr exprInit;

    public DeclField(Visibility visibility, AbstractIdentifier type,
                     AbstractIdentifier fieldName, AbstractInitialization initialization) {
        this.visibility = visibility;
        this.type = type;
        this.fieldName = fieldName;
        this.initialization = initialization;
        this.exprInit = initialization.getExpression();
    }

    public Type getFieldType() {
        return type.getType();
    }

    public AbstractIdentifier getFieldName() {
        return fieldName;
    }


    public AbstractInitialization getInitialization() {
        return initialization;
    }

    /**
     *
     * @param s Buffer to which the result will be written.
     */
    @Override
    public void decompile(IndentPrintStream s) {
        if (visibility.equals(Visibility.PROTECTED)) {
            s.print("protected ");
        }
        type.decompile(s);
        s.print(" ");
        fieldName.decompile(s);
        initialization.decompile(s);
        s.print(";");
    }


    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        s.println(prefix + "+> " + visibility);
        type.prettyPrint(s, prefix, false);
        fieldName.prettyPrint(s, prefix, false);
        initialization.prettyPrint(s, prefix, true);

    }

    @Override
    protected void iterChildren(TreeFunction f) {
        type.iter(f);
        fieldName.iter(f);
        initialization.iter(f);
    }

    /**
     * Implements non terminal decl_field of pass 2
     * @param compiler contains env_types
     * @param superclass
     * @param currentClass
     * @return boolean stating if the current field declaration is a redefinition or not (for indexes)
     * @throws ContextualError
     */
    protected boolean verifyDeclFieldPass2(DecacCompiler compiler, AbstractIdentifier superclass,
                                        AbstractIdentifier currentClass) throws ContextualError {
        boolean isRedefinition = false;

        LOG.debug("verify decl field "+ fieldName.getName()+ " pass 2: start");
        Type typeObtained = type.verifyType(compiler);
        int index = currentClass.getClassDefinition().getNumberOfFields()+1;
        if (typeObtained.isVoid()) {
            throw new ContextualError("Field type cannot be void but is (2.5)", getLocation());
        }
        EnvironmentExp envExpSuper = superclass.getClassDefinition().getMembers();
        Definition def = envExpSuper.get(fieldName.getName());
        if (def != null) {
            FieldDefinition fieldDef = def.asFieldDefinition("Identifier " + fieldName.getName() +
                    " defined in superclass but is not field (2.5)", getLocation());
            index = fieldDef.getIndex();
            isRedefinition = true;
        }

        try {
            FieldDefinition newDef = new FieldDefinition(typeObtained, getLocation(), visibility,
                            currentClass.getClassDefinition(), index);
            fieldName.setDefinition(newDef);
            fieldName.setType(typeObtained);
            currentClass.getClassDefinition().getMembers().declare(fieldName.getName(), newDef);
            LOG.debug("verify decl field "+ fieldName.getName()+"pass 2 : end");
            return isRedefinition;
        } catch (EnvironmentExp.DoubleDefException e) {
            throw new ContextualError("field " + fieldName.getName() +
                    " is already declared in class " + currentClass.getName() + " (2.5)", getLocation());
        }
    }

    /**
     * implements non terminal decl_field of pass 3
     * @param compiler contains env_types
     * @param envExp class environment
     * @param currentClass
     * @throws ContextualError
     */
    protected void verifyDeclFieldPass3(DecacCompiler compiler,
                                        EnvironmentExp envExp,
                                        AbstractIdentifier currentClass) throws ContextualError {
        LOG.debug("verify decl field " + fieldName.getName() + " pass 3: start");
        Type typeObtained = type.verifyType(compiler);
        initialization.verifyInitialization(compiler, typeObtained, envExp, currentClass.getClassDefinition());
        LOG.debug("verify decl field " + fieldName.getName() + " pass 3: end");
    }

    /**
     * Generates the code to initialize an object's fields with 0, 0.0, or Null
     * @param compiler
     */
    public void codeGenDeclFieldZero(DecacCompiler compiler) {
        if (type.getType().isInt()) {
            compiler.addInstruction(new LOAD(0, Register.R0));
        }
        else if (type.getType().isFloat()) {
            compiler.addInstruction(new LOAD(new ImmediateFloat((float) 0.0), Register.R0));
        }
        else if (type.getType().isBoolean()) {
            compiler.addInstruction(new LOAD(0, Register.R0));
        }
        else {
            compiler.addInstruction(new LOAD(new NullOperand(), Register.R0));
        }

        compiler.addInstruction(new STORE(R0, new RegisterOffset(fieldName.getFieldDefinition().getIndex(), R1)));

    }

    /**
     * Generates the code to initialize an object's fields with its initialization value, or 0, 0.0, null
     * @param compiler
     */
    public void codeGenDeclFieldVal(DecacCompiler compiler) {

        if (exprInit == null) {
            throw new DecacInternalError("Field not explicitly initialized is treated as though it had");
        }


        if (exprInit.isIntLiteral()) {
            compiler.addInstruction(new LOAD(((IntLiteral) exprInit).getValue(), Register.R0));
        }
        else if (exprInit.isFloatLiteral()) {
            compiler.addInstruction(new LOAD(new ImmediateFloat(((FloatLiteral) exprInit).getValue()), Register.R0));
        }
        else if (exprInit.isBooleanLiteral()) {
            compiler.addInstruction(new LOAD(new ImmediateInteger(((BooleanLiteral) exprInit).getIntValue()), Register.R0));
        }
        // The explicit initialization is NOT an Immediate :
        else {
            exprInit.codeGenInst(compiler);
            compiler.addToFirstRegisterNumber(-1);
            compiler.addInstruction(new LOAD(Register.getR(compiler.getFirstRegisterNumber()), Register.R0));
        }

        compiler.addInstruction(new STORE(R0, new RegisterOffset(fieldName.getFieldDefinition().getIndex(), R1)));

    }


    /**
     * Code that generates the initialization of the fields
     * @param compiler
     */
    public void codeGenDeclField(DecacCompiler compiler) {
        compiler.addComment("Initialization of field " + fieldName.getName().toString());

        compiler.addInstruction(new LOAD(new RegisterOffset(-2, Register.LB), R1));

        // Case in which there is no explicit initialization given
        if (exprInit == null) {
            codeGenDeclFieldZero(compiler);
        }
        // Case in which an explicit initialization is given
        else {
            codeGenDeclFieldVal(compiler);

        }

    }

}
