package fr.ensimag.deca.tree;


import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.instructions.BGT;
import fr.ensimag.ima.pseudocode.instructions.BLT;

/**
 * "lower than" operator
 * @author gl01
 * @date 01/01/2020
 */
public class Lower extends AbstractOpIneq {

    public Lower(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }


    @Override
    protected String getOperatorName() {
        return "<";
    }


    @Override
    protected void controlFlow(DecacCompiler compiler, boolean b, Label E) {
        super.controlFlow(compiler, b, E);
        if (b) {
            if (getLeftOperand().isImmediate()) {
                compiler.addInstruction(new BGT(E));

            } else if (getRightOperand().isImmediate()) {
                compiler.addInstruction(new BLT(E));

            } else {
                compiler.addInstruction(new BLT(E));
            }
        } else {
            (new GreaterOrEqual(getLeftOperand(), getRightOperand())).controlFlow(compiler, true, E);
        }
    }

}
