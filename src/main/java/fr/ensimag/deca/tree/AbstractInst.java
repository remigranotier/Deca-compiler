package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.Label;

/**
 * Instruction
 *
 * @author gl01
 * @date 01/01/2020
 */
public abstract class AbstractInst extends Tree {


    public boolean isAssign() { return false; }
    public boolean isReturn() { return false; }
    public boolean isMethodCall() { return false; }

    /**
     * Implements non-terminal "inst" of [SyntaxeContextuelle] in pass 3
     * @param compiler contains the "env_types" attribute
     * @param localEnv corresponds to the "env_exp" attribute
     * @param currentClass 
     *          corresponds to the "class" attribute (null in the main bloc).
     * @param returnType
     *          corresponds to the "return" attribute (void in the main bloc).
     * @throws ContextualError
     */    
    protected abstract void verifyInst(DecacCompiler compiler,
            EnvironmentExp localEnv, ClassDefinition currentClass, Type returnType) throws ContextualError;

    /**
     * Generate assembly code for the instruction.
     * 
     * @param compiler
     */
    protected abstract void codeGenInst(DecacCompiler compiler);

    /**
     * Generates the code for the instruction if it is in an "else" statement.
     * Generated in a separate function because it need the Label of the end of the statement as an argument.
     * @param compiler
     * @param end
     */
    protected void codeGenElse(DecacCompiler compiler, Label end) {
        codeGenInst(compiler);
    }

    /**
     *
     * @return boolean stating if an instruction must generate code or not.
     *      Is useful to delete useless instructions when the instruction is 3+4 for example
     */
    protected boolean mustNotGenCode() {
        return false;
    }

    /**
     * Decompile the tree, considering it as an instruction.
     *
     * In most case, this simply calls decompile(), but it may add a semicolon if needed
     */
    protected void decompileInst(IndentPrintStream s) {
        decompile(s);
    }
}
