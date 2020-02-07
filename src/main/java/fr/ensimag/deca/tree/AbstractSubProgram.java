package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.ADDSP;
import fr.ensimag.ima.pseudocode.instructions.POP;
import fr.ensimag.ima.pseudocode.instructions.PUSH;
import fr.ensimag.ima.pseudocode.instructions.SUBSP;

import java.io.PrintStream;

/**
 * Abstract class that represent a "sub-program" : it is an independent block of the generated code, like
 * methods or initialization sub-programs.
 * Useful to give access to functions to both of them.
 */
public abstract class AbstractSubProgram extends Tree{

    /**
     * Generates the code to save the registers that are used in the sub-program.
     * @param compiler
     */
    protected void saveRegisters(DecacCompiler compiler) {
        compiler.addComment("restore registers");
        for (int i = 2 ; i < compiler.getMaxRegisterUsed(); i++ ) {
            compiler.addFirstInstruction(new PUSH(Register.getR(i)));
            compiler.addInstruction(new POP(Register.getR(i)));
        }
        compiler.addToCurrentNumberOfRegisterPushed(compiler.getMaxRegisterUsed() - 2);
        compiler.addFirstComment("save registers");
    }

    /**
     * Generates the SPOffset to add at the beginning of the sub-program, due to local variables storing.
     * @param compiler
     */
    protected void genSPOffset(DecacCompiler compiler) {
        int offset = compiler.getNbVar();
        if (offset != 0 ) {
            compiler.addFirstInstruction(new ADDSP(offset));
            compiler.addInstruction(new SUBSP(offset));
        }

    }
}
