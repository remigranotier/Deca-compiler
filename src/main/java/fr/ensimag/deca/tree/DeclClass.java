package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.*;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.*;
import fr.ensimag.ima.pseudocode.instructions.LEA;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.instructions.RTS;
import fr.ensimag.ima.pseudocode.instructions.STORE;
import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;

import java.io.PrintStream;
import java.util.HashMap;

/**
 * Declaration of a class (<code>class name extends superClass {members}</code>).
 * 
 * @author gl01
 * @date 01/01/2020
 */
public class DeclClass extends AbstractDeclClass {
    private static final Logger LOG = Logger.getLogger(DeclClass.class);
    final private AbstractIdentifier name;
    final private AbstractIdentifier superclass;
    final private ListDeclMethod listDeclMethod;
    final private ListDeclField listDeclField;

    public DeclClass(AbstractIdentifier name, AbstractIdentifier superclass, ListDeclMethod listDeclMethod, ListDeclField listDeclField) {
        Validate.notNull(name);
        Validate.notNull(superclass);
        this.name = name;
        this.superclass = superclass;
        this.listDeclMethod = listDeclMethod;
        this.listDeclField = listDeclField;
    }

    /**
     *
     * @param s Buffer to which the result will be written.
     */
    @Override
    public void decompile(IndentPrintStream s) {
        s.print("class ");
        name.decompile(s);
        s.print(" extends ");
        superclass.decompile(s);
        s.println(" {");
        s.indent();
        listDeclField.decompile(s);
        listDeclMethod.decompile(s);
        s.unindent();
        s.print("}");
    }

    /**
     * Implements non terminal decl_class of pass 1 (rule 1.3)
     * @param compiler contains env_types
     * @throws ContextualError
     */
    @Override
    protected void verifyClass(DecacCompiler compiler) throws ContextualError {
        LOG.debug("verify class " + name.getName() + " pass 1: start" );
        superclass.setDefinition(compiler.getEnvTypes().get(superclass.getName()));
        // Si on ne fait pas le setDefinition ici, on est obligés de le faire dans DecaParser et c'est pas propre
        TypeDefinition superclassType = compiler.getEnvTypes().get(superclass.getName());
        if (superclassType == null || !superclassType.isClass()) {
            throw new ContextualError("Class " + superclass.getName() + " does not exist in current context (1.3)", getLocation());
        }
        ClassType nameClass = new ClassType(name.getName(), getLocation(),
                superclass);
        ClassDefinition nameDef = nameClass.getDefinition();
        name.setDefinition(nameDef);
        name.setType(nameClass);
        try {
            compiler.getEnvTypes().declare(name.getName(), nameDef);
            LOG.debug("verify class " + name.getName() + " pass 1: end" );
        } catch (EnvironmentType.DoubleDefException e) {
            throw new ContextualError("Class " + name.getName() + " is already declared in this context (1.3)", getLocation());
        }
    }

    /**
     * Implements non terminal decl_class of pass 2 (rule 2.3)
     * @param compiler
     * @throws ContextualError
     */
    @Override
    protected void verifyClassMembers(DecacCompiler compiler)
            throws ContextualError {
        LOG.debug("verify class " + name.getName() + " pass 2: start");
        if (compiler.getEnvTypes().get(superclass.getName()) == null ||
                !compiler.getEnvTypes().get(superclass.getName()).isClass()) {
            throw new ContextualError("Superclass " +
                    superclass.getName() + " is not defined in current context (2.3) [SHOULD NOT APPEAR]", getLocation());
        }
        listDeclField.verifyListDeclFieldPass2(compiler, superclass, name);
        listDeclMethod.verifyListDeclMethodPass2(compiler, superclass, name);
        LOG.debug("verify class " + name.getName() + " pass 2: end");
    }

    /**
     * Implements non terminal decl_class of pass 3
     * @param compiler
     * @throws ContextualError
     */
    @Override
    protected void verifyClassBody(DecacCompiler compiler) throws ContextualError {
        LOG.debug("verify class " + name.getName() + " pass 3: start");
        EnvironmentExp classEnv = name.getClassDefinition().getMembers();
        listDeclField.verifyListDeclFieldPass3(compiler, classEnv, name);
        listDeclMethod.verifyListDeclMethodPass3(compiler, classEnv, name);
        LOG.debug("verify class " + name.getName() + " pass 3: end");
    }


    /**
     * Creates the Label Table of the class
     * @return
     */
    @Override
    public HashMap<Integer, LabelOperand> getLabelTable() {
        HashMap<Integer, LabelOperand> labelTable;

        if (!name.getName().toString().equals("Object")) {
            // Get superclass table, and write on a copy of it : like this, method redefinition (which has same index)
            // will overwrite methods written in superclasses
            labelTable = (HashMap<Integer, LabelOperand>) superclass.getClassDefinition().getLabelTable().clone();

            LabelOperand lab;
            for (DeclMethod method : this.listDeclMethod.getList()) {
                lab = Label.getMethodLabel(name.getName(), method.getMethodName().getName());
                int i = method.getMethodName().getMethodDefinition().getIndex();

                LOG.debug("Code." + name.getName() + "." + method.getMethodName().getName() + " got index " + i);

                labelTable.put(i, lab);
            }

        } else {
            //We are generating Object method table
            labelTable = new HashMap<Integer, LabelOperand>();
            LabelOperand objectEquals = Label.getMethodLabel(name.getName(), "equals");
            labelTable.put(0, objectEquals);
        }
        name.getClassDefinition().setLabelTable(labelTable);

        return labelTable;
    }

    /**
     * Sets the class addr
     * @param compiler
     * @param offset
     */
    private void setClassAddr(DecacCompiler compiler, int offset){
        compiler.addInstruction(new LEA(superclass.getClassDefinition().getMethodTableAddr(), Register.R0));
        compiler.addInstruction(new STORE(Register.R0, new RegisterOffset(offset, Register.GB)));
    }

    /**
     * Generates the code for the method table of the class
     * @param compiler
     * @param offset : GB offset to start from to store the labels of the functions
     * @param labelTable
     */
    @Override
    public void genMethodTable(DecacCompiler compiler, int offset, HashMap<Integer, LabelOperand> labelTable) {
        compiler.addComment("Generation of the method table of : " + name.getName());
        LOG.debug("generating the method table for " + name.getName() + " superclass : " + superclass.getName().toString());

        setClassAddr(compiler, offset);

        for (Integer i : labelTable.keySet()) {
            this.name.getClassDefinition().setMethodTableAddr(new RegisterOffset(offset, Register.GB));
            compiler.addInstruction(new LOAD(labelTable.get(i), Register.R0));
            //+1 due to the fact that we have to store the superclass pointer
            compiler.addInstruction(new STORE(Register.R0, new RegisterOffset(offset + 1 + i, Register.GB)));
        }
    }


    /**
     * Generates the code for the initialization of new classes (used by the 'new' instruction)
     * @param compiler
     */
    public void codeGenDeclClass(DecacCompiler compiler){
        IMAProgram methodCode = new IMAProgram(); //New code block : useful to add things at the beginning of the method
        compiler.setMethodCode(methodCode);

        // beginning of method mode
        compiler.changeMode();

        //make sure all necessary counters are properly initialized
        compiler.resetRegisterCount();
        compiler.resetNbVar();
        compiler.resetFirstRegisterNumber();


        if (!(superclass.getName().equals(compiler.getSymbolTable().create("Object")))){
            listDeclField.codeGenListDeclFieldInherited(compiler, name.getClassDefinition().getSuperClass());
            compiler.addComment("finished dealing with inheritance");
        }

        listDeclField.codeGenListDeclFieldBasic(compiler);
        saveRegisters(compiler);
        compiler.addInstruction(new RTS());
        compiler.getErrorManager().genStackOverflowError();

        methodCode.addFirstLabel(new Label("init." + name.getName()));

        //end of method mode
        compiler.appendProgram(methodCode);
        compiler.changeMode();
    }


    /**
     * Generates the code for each of the class's method
     * @param compiler
     */
    @Override
    protected void codeGenMethod(DecacCompiler compiler) {
        for (DeclMethod m : listDeclMethod.getList()) {
            m.codeGenMethod(compiler, name);
        }
    }


    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        name.prettyPrint(s, prefix, false);
        superclass.prettyPrint(s, prefix, false);
        listDeclField.prettyPrint(s, prefix, true);
        listDeclMethod.prettyPrint(s, prefix, false);
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        name.iter(f);
        superclass.iter(f);
        listDeclField.iter(f);
        listDeclMethod.iter(f);
    }

}
