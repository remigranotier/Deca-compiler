package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.*;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.instructions.RTS;
import fr.ensimag.ima.pseudocode.instructions.STORE;
import org.apache.log4j.Logger;

import java.util.HashMap;

/**
 * List of class declarations
 * @author gl01
 * @date 01/01/2020
 */
public class ListDeclClass extends TreeList<AbstractDeclClass> {
    private static final Logger LOG = Logger.getLogger(ListDeclClass.class);

    /**
     *
     * @param s Buffer to which the result will be written.
     */
    @Override
    public void decompile(IndentPrintStream s) {
        for (AbstractDeclClass c : getList()) {
            c.decompile(s);
            s.println();
        }
    }

    /**
     * Non terminal list_class in Pass 1 of [SyntaxeContextuelle]
     */
    void verifyListClass(DecacCompiler compiler) throws ContextualError {
        LOG.debug("verify listClass pass 1: start");
        for (AbstractDeclClass c : getList()) {
            c.verifyClass(compiler);
        }
        LOG.debug("verify listClass pass 1: end");
    }

    /**
     * Non terminal list_class in Pass 2 of [SyntaxeContextuelle]
     */
    public void verifyListClassMembers(DecacCompiler compiler) throws ContextualError {
        LOG.debug("verify listClass pass 2: start");
        for (AbstractDeclClass c : getList()) {
            c.verifyClassMembers(compiler);
        }
        LOG.debug("verify listClass pass 2: end");
    }
    
    /**
     * Non terminal list_class in Pass 3 of [SyntaxeContextuelle]
     */
    public void verifyListClassBody(DecacCompiler compiler) throws ContextualError {
        LOG.debug("verify listClass pass 3: start");
        for (AbstractDeclClass c : getList()) {
            c.verifyClassBody(compiler);
        }
        LOG.debug("verify listClass pass 3: end");
    }


    /**
     * Generates the method table for each of the classes's methods
     * @param compiler
     */
    public void genMethodTables(DecacCompiler compiler) {
        //First we generate object method table
        genObjectTable(compiler);

        int offset = 3; //3 offset due to the object method table + start at 1
        for (AbstractDeclClass c : getList()) {
            HashMap<Integer, LabelOperand> labelTable = c.getLabelTable();
            c.genMethodTable(compiler, offset, labelTable);

            //+1 due to the fact that we store the superclass pointer too
            offset += labelTable.size() + 1;
        }

        compiler.setMethodTableSize(offset - 1); //size of the method table
        compiler.setGBOffset(offset);
    }

    /**
     * Generates the Object method table
     * @param compiler
     */
    private void genObjectTable(DecacCompiler compiler) {
        compiler.addComment("Generation of the method table of Object");
        compiler.addInstruction(new LOAD(new NullOperand(), Register.R0));
        compiler.addInstruction(new STORE(Register.R0, new RegisterOffset(1, Register.GB)));
        compiler.addInstruction(new LOAD(Label.getMethodLabel("Object", "equals"), Register.R0));
        compiler.addInstruction(new STORE(Register.R0, new RegisterOffset(2, Register.GB)));
    }

    /**
     * Generates the initialization subprocesses for each of the classes
     * @param compiler
     */
    protected void codeGenListDeclClass(DecacCompiler compiler) {

        compiler.addLabel(new Label("init.object"));
        compiler.addInstruction(new RTS());

        for (AbstractDeclClass c : getList()) {
            c.codeGenDeclClass(compiler);
        }
    }

    /**
     * Generates the method code for each of the classes.
     * @param compiler
     */
    public void genMethodCode(DecacCompiler compiler) {
        for (AbstractDeclClass c : getList()) {
            c.codeGenMethod(compiler);
        }
    }

}
