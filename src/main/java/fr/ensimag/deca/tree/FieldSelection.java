package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.DecacFatalError;
import fr.ensimag.deca.context.*;
import fr.ensimag.deca.tools.DecacInternalError;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.RegisterOffset;
import fr.ensimag.ima.pseudocode.instructions.*;
import org.apache.log4j.Logger;
import org.mockito.internal.stubbing.answers.ReturnsArgumentAt;

import java.io.PrintStream;

/**
 * Selection of a field from an expression of class type
 * @author gl01
 * @date 20/01/2020
 */
public class FieldSelection extends AbstractLValue {
    private final AbstractExpr className;
    private final AbstractIdentifier fieldName;
    private static final Logger LOG = Logger.getLogger(FieldSelection.class);


    public AbstractIdentifier getFieldName() {
        return fieldName;
    }

    public FieldSelection(AbstractExpr className, AbstractIdentifier fieldName) {
        this.className = className;
        this.fieldName = fieldName;
    }

    @Override
    protected boolean isFieldSelection() { return true; }

    /**
     * Implements terminal Selection of pass 3
     * @param compiler  (contains the "env_types" attribute)
     * @param localEnv
     *            Environment in which the expression should be checked
     *            (corresponds to the "env_exp" attribute)
     * @param currentClass
     *            Definition of the class containing the expression
     *            (corresponds to the "class" attribute)
     *             is null in the main bloc.
     * @return this expression's type, also decorating the tree with it
     * @throws ContextualError
     */
    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv, ClassDefinition currentClass) throws ContextualError {
        LOG.debug("verify field selection : start");
        Type class2 = className.verifyExpr(compiler, localEnv, currentClass);
        EnvironmentExp envExp2 = class2.asClassType("Trying to get field " + fieldName.getName() +
                        " from expression which is not a class (3.65-66)",
                getLocation()).getDefinition().getMembers();
        fieldName.verifyExpr(compiler, envExp2, currentClass);
        try {
            FieldDefinition fieldDef = fieldName.getFieldDefinition();
            Type result = fieldDef.getType();
            setType(result);
            LOG.debug("verify field selection : end");
            if (fieldDef.getVisibility().equals(Visibility.PUBLIC)) {
                return result;
            } else if (currentClass != null &&
                    compiler.getEnvTypes().subType(class2, currentClass.getType()) &&
                    compiler.getEnvTypes().subType(currentClass.getType(), fieldDef.getContainingClass().getType())) {
                return result;
            }
        } catch (DecacInternalError e) {
            throw new ContextualError("Identifier " + fieldName.getName() + " is not a field (3.70)", getLocation());
        }
        throw new ContextualError("Cannot select field " + fieldName.getName() +" (3.66)", getLocation());
    }

    /**
     * Implements non terminal lvalue of pass 3
     * @param compiler contains env_types
     * @param envExp class environment
     * @param currentClass
     * @return Lvalue's type
     * @throws ContextualError
     */
    @Override
    public Type verifyLValue(DecacCompiler compiler, EnvironmentExp envExp, ClassDefinition currentClass) throws ContextualError {
        return verifyExpr(compiler, envExp, currentClass);
    }

    /**
     *
     * @param s Buffer to which the result will be written.
     */
    @Override
    public void decompile(IndentPrintStream s) {
        className.decompile(s);
        s.print(".");
        fieldName.decompile(s);
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        className.prettyPrint(s, prefix, false);
        fieldName.prettyPrint(s, prefix, true);
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        className.iter(f);
        fieldName.iter(f);
    }

    @Override
    protected void codeGenInst(DecacCompiler compiler) {
        int n = compiler.getFirstRegisterNumber();
        className.codeGenInst(compiler); //recursif : className peut etre un field selection aussi, ou un objet.

        compiler.addInstruction(new LOAD(new RegisterOffset(fieldName.getFieldDefinition().getIndex(), Register.getR(n)), Register.getR(n)));

        //only generate null dereferencement if 'this' is an object, not if it is the final field
        if (this.getType().isClassOrNull()) {
            compiler.getErrorManager().getNullDereferencement().genBranch();
        }

    }

    /**
     * Generates the code to execute in the cas this selection is the last in a a.b.c.....w.x chain
     * @param compiler
     */
    protected void codeGenInstLastSelection(DecacCompiler compiler) {
        int n = compiler.getFirstRegisterNumber();
        className.codeGenInst(compiler); //recursif : className peut etre un field selection aussi, ou un objet.

        compiler.addInstruction(new LOAD(new RegisterOffset(fieldName.getFieldDefinition().getIndex(), Register.getR(n)), Register.getR(n)));

    }



    @Override
    protected void controlFlow(DecacCompiler compiler, boolean b, Label E) {
        //we know the last field selection will be a boolean and not a class
        int n = compiler.getFirstRegisterNumber();
        codeGenInstLastSelection(compiler); //we got the resulting boolean in Rn
        compiler.addInstruction(new CMP(0, Register.getR(n)));
        //we no longer need Rn
        compiler.addToFirstRegisterNumber(-1);
        if (b) {
            compiler.addInstruction(new BNE(E));
        } else {
            compiler.addInstruction(new BEQ(E));
        }
    }

    @Override
    protected void codeGenPrint(DecacCompiler compiler) {
        LOG.debug("printing an int field selection");

        int n = compiler.getFirstRegisterNumber();
        codeGenInstAssign(compiler);
        compiler.addInstruction(new LOAD(new RegisterOffset(fieldName.getFieldDefinition().getIndex(), Register.getR(n)), Register.R1));

        //If we are in this print, we have ints.
        compiler.addInstruction(new WINT());
        compiler.addToFirstRegisterNumber(-1);
        LOG.debug("end of an int field selection print");

    }

    @Override
    protected void codeGenPrint(DecacCompiler compiler, boolean printHex) {
        LOG.debug("printing a float field selection");

        int n = compiler.getFirstRegisterNumber();
        codeGenInstAssign(compiler);
        compiler.addInstruction(new LOAD(new RegisterOffset(fieldName.getFieldDefinition().getIndex(), Register.getR(n)), Register.R1));

        //If we are in this print, we have floats
        if (printHex) {
            compiler.addInstruction(new WFLOATX());
        } else {
            compiler.addInstruction(new WFLOAT());
        }
        compiler.addToFirstRegisterNumber(-1);
        LOG.debug("end of a float field selection print");

    }


    protected void codeGenInstAssign(DecacCompiler compiler) {
        className.codeGenInst(compiler); //recursif : className peut etre un field selection aussi, ou un objet.
        compiler.getErrorManager().getNullDereferencement().genBranch();
    }
}
