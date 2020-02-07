package fr.ensimag.ima.pseudocode.instructions;

import fr.ensimag.ima.pseudocode.*;

/**
 *
 * @author Ensimag
 * @date 01/01/2020
 */
public class CMP extends BinaryInstructionDValToReg {

    public CMP(DVal op1, GPRegister op2) {
        super(op1, op2);
    }

    public CMP(int val, GPRegister op2) {
        this(new ImmediateInteger(val), op2);
    }

    public CMP(float val, GPRegister op2) {
        this(new ImmediateFloat(val), op2);
    }

}