package fr.ensimag.deca.tree;


import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.instructions.BGE;
import fr.ensimag.ima.pseudocode.instructions.BLE;

/**
 * "lower or equal than" operator
 * @author gl01
 * @date 01/01/2020
 */
public class LowerOrEqual extends AbstractOpIneq {
    public LowerOrEqual(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }

    @Override
    protected void controlFlow(DecacCompiler compiler, boolean b, Label E) {
        super.controlFlow(compiler, b, E);
        if (b) {
            if (getLeftOperand().isImmediate()) {
                compiler.addInstruction(new BGE(E));

            } else if (getRightOperand().isImmediate()) {
                compiler.addInstruction(new BLE(E));

            } else {
                compiler.addInstruction(new BLE(E));
            }
        } else {
            (new Greater(getLeftOperand(), getRightOperand())).controlFlow(compiler, true, E);
        }
    }

    @Override
    protected String getOperatorName() {
        return "<=";
    }

}
