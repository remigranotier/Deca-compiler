package fr.ensimag.deca.tree;


import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.BGT;
import fr.ensimag.ima.pseudocode.instructions.BLE;
import fr.ensimag.ima.pseudocode.instructions.BLT;
import fr.ensimag.ima.pseudocode.instructions.CMP;

/**
 * "greater than" operator
 * @author gl01
 * @date 01/01/2020
 */
public class Greater extends AbstractOpIneq {

    public Greater(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }


    @Override
    protected String getOperatorName() {
        return ">";
    }

    @Override
    protected void controlFlow(DecacCompiler compiler, boolean b, Label E) {
        super.controlFlow(compiler, b, E);
        if (b) {
            if (getLeftOperand().isImmediate()) {
                compiler.addInstruction(new BLT(E));

            } else if (getRightOperand().isImmediate()) {
                compiler.addInstruction(new BGT(E));

            } else {
                compiler.addInstruction(new BGT(E));
            }
        } else {
            (new LowerOrEqual(getLeftOperand(), getRightOperand())).controlFlow(compiler, true, E);
        }
    }

}
