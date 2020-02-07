package fr.ensimag.deca.tree;


import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.*;

/**
 * And operator (boolean)
 * @author gl01
 * @date 01/01/2020
 */
public class And extends AbstractOpBool {

    public And(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }


    /**
     * Generates the code for and 'And' 
     * @param compiler
     * @param b
     * @param E
     */
    @Override
    protected void controlFlow(DecacCompiler compiler, boolean b, Label E) {

        compiler.addComment("Beginning of a 'and' Control Flow : b = " + b + " E = " + E);

        if(b) {
            Label endOfAnd = Label.getNewEndOfBoolExpr();
            compiler.addComment("Beginning of the left op control flow");
            getLeftOperand().controlFlow(compiler, false, endOfAnd);
            compiler.addComment("Beginning of the right op control flow");
            getRightOperand().controlFlow(compiler, true, E);

            compiler.addLabel(endOfAnd);
        } else {
            compiler.addComment("Beginning of the left op control flow");
            getLeftOperand().controlFlow(compiler, false, E);
            compiler.addComment("Beginning of the right op control flow");
            getRightOperand().controlFlow(compiler, false, E);
        }
        compiler.addComment("End of a 'and' Control Flow : b = " + b + " E = " + E);
    }

    @Override
    protected String getOperatorName() {
        return "&&";
    }


}
