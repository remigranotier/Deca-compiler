package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.ima.pseudocode.LabelOperand;

import java.util.HashMap;

/**
 * Class declaration.
 *
 * @author gl01
 * @date 01/01/2020
 */
public abstract class AbstractDeclClass extends AbstractSubProgram {

    /**
     * Pass 1 of [SyntaxeContextuelle]. Verify that the class declaration is OK
     * without looking at its content.
     */
    protected abstract void verifyClass(DecacCompiler compiler)
            throws ContextualError;

    /**
     * Pass 2 of [SyntaxeContextuelle]. Verify that the class members (fields and
     * methods) are OK, without looking at method body and field initialization.
     */
    protected abstract void verifyClassMembers(DecacCompiler compiler)
            throws ContextualError;

    /**
     * Pass 3 of [SyntaxeContextuelle]. Verify that instructions and expressions
     * contained in the class are OK.
     */
    protected abstract void verifyClassBody(DecacCompiler compiler)
            throws ContextualError;

    /**
     * Function that create the HashMap for the Label Table
     * @return
     */
    public abstract HashMap<Integer, LabelOperand> getLabelTable();

    /**
     * Generates the code for the Method Table of the class
     * @param compiler
     * @param offset : GB offset to start from to store the labels of the functions
     * @param labelTable
     */
    public abstract void genMethodTable(DecacCompiler compiler, int offset, HashMap<Integer, LabelOperand> labelTable);

    /**
     * Generates the code of the methods of the class
     * @param compiler
     */
    protected abstract void codeGenMethod(DecacCompiler compiler);


    /**
     * Generates the initialization sub-method for creating an object of this class
     * @param compiler
     */
    public abstract void codeGenDeclClass(DecacCompiler compiler);
}
