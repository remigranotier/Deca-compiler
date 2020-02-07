package fr.ensimag.deca.tree;


import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.instructions.BGE;
import fr.ensimag.ima.pseudocode.instructions.BLE;

/**
 * Operator "x greater or equal than y"
 * 
 * @author gl01
 * @date 01/01/2020
 */
public class GreaterOrEqual extends AbstractOpIneq {

    public GreaterOrEqual(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }


    @Override
    protected String getOperatorName() {
        return ">=";
    }

    @Override
    protected void controlFlow(DecacCompiler compiler, boolean b, Label E) {
        super.controlFlow(compiler, b, E);
        if (b) {
            if (getLeftOperand().isImmediate()) {
                compiler.addInstruction(new BLE(E));

            } else if (getRightOperand().isImmediate()) {
                compiler.addInstruction(new BGE(E));

            } else {
                compiler.addInstruction(new BGE(E));
            }
        } else {
            (new Lower(getLeftOperand(), getRightOperand())).controlFlow(compiler, true, E);
        }
    }
}
