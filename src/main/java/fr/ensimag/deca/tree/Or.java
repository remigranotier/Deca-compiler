package fr.ensimag.deca.tree;


import com.sun.tools.corba.se.idl.constExpr.BooleanOr;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.*;

/**
 *  Boolean or operator
 * @author gl01
 * @date 01/01/2020
 */
public class Or extends AbstractOpBool {

    public Or(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }


    @Override
    protected void controlFlow(DecacCompiler compiler, boolean b, Label E) {
        AbstractExpr equivalentAnd = new Not(new And(new Not(getLeftOperand()), new Not(getRightOperand())));
        equivalentAnd.controlFlow(compiler, b, E);
    }

    @Override
    protected String getOperatorName() {
        return "||";
    }


}
