package fr.ensimag.deca.tree;


import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.instructions.BNE;

/**
 * "Different than" operator
 * @author gl01
 * @date 01/01/2020
 */
public class NotEquals extends AbstractOpExactCmp {

    public NotEquals(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }

    @Override
    protected void controlFlow(DecacCompiler compiler, boolean b, Label E) {
        super.controlFlow(compiler, b, E);
        if (b) {
            compiler.addInstruction(new BNE(E));
        } else {
            (new Equals(getLeftOperand(), getRightOperand())).controlFlow(compiler, true, E);
        }
    }

    @Override
    protected String getOperatorName() {
        return "!=";
    }

}
