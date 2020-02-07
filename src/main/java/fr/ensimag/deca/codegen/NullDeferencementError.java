package fr.ensimag.deca.codegen;

import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.NullOperand;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.BEQ;
import fr.ensimag.ima.pseudocode.instructions.CMP;

public class NullDeferencementError extends Error {
    public NullDeferencementError(ErrorManager errorManager, Label nullDereferencementLabel, boolean b, String s, boolean b1) {
        super(errorManager, nullDereferencementLabel, b, s, b1);
    }



    public void genBranch() {
        if (!isNoCheckInfluence() || !getErrorManager().getCompiler().getCompilerOptions().getNoCheck()) {
            int n = getErrorManager().getCompiler().getFirstRegisterNumber() - 1 ;

            getErrorManager().getCompiler().addInstruction(new CMP(new NullOperand(), Register.getR(n)));
            getErrorManager().getCompiler().addInstruction(new BEQ(this.getErrorLabel()));
            setErrorPresent(true);
        }
    }
}
