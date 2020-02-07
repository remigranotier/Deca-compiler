package fr.ensimag.deca.tree;


import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.instructions.BEQ;

/**
 * ==, basically
 * @author gl01
 * @date 01/01/2020
 */
public class Equals extends AbstractOpExactCmp {

    public Equals(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }

    @Override
    protected void controlFlow(DecacCompiler compiler, boolean b, Label E) {
        super.controlFlow(compiler, b, E);
        if (b) {
            compiler.addInstruction(new BEQ(E));
        } else {
            (new NotEquals(getLeftOperand(), getRightOperand())).controlFlow(compiler, true, E);
        }
    }

    @Override
    protected String getOperatorName() {
        return "==";
    }    
    
}
