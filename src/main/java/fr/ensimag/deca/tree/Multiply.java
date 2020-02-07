package fr.ensimag.deca.tree;


/**
 * Multiplication operator
 * @author gl01
 * @date 01/01/2020
 */
public class Multiply extends AbstractOpArith {
    public Multiply(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }


    @Override
    protected String getOperatorName() {
        return "*";
    }

}
