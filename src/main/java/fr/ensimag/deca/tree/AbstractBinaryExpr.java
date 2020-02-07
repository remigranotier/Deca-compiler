package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.tools.IndentPrintStream;
import java.io.PrintStream;
import org.apache.commons.lang.Validate;

/**
 * Binary expressions.
 *
 * @author gl01
 * @date 01/01/2020
 */
public abstract class AbstractBinaryExpr extends AbstractExpr {

    /**
     * Return the left operand
     * @return
     */
    public AbstractExpr getLeftOperand() {
        return leftOperand;
    }

    /**
     * Return the right operand
     * @return
     */
    public AbstractExpr getRightOperand() {
        return rightOperand;
    }

    /**
     * Sets the left operand
     * @param leftOperand
     */
    protected void setLeftOperand(AbstractExpr leftOperand) {
        Validate.notNull(leftOperand);
        this.leftOperand = leftOperand;
    }

    /**
     * Sets the right operand
     * @param rightOperand
     */
    protected void setRightOperand(AbstractExpr rightOperand) {
        Validate.notNull(rightOperand);
        this.rightOperand = rightOperand;
    }

    private AbstractExpr leftOperand;
    private AbstractExpr rightOperand;


    public AbstractBinaryExpr(AbstractExpr leftOperand,
            AbstractExpr rightOperand) {
        Validate.notNull(leftOperand, "left operand cannot be null");
        Validate.notNull(rightOperand, "right operand cannot be null");
        Validate.isTrue(leftOperand != rightOperand, "Sharing subtrees is forbidden");
        this.leftOperand = leftOperand;
        this.rightOperand = rightOperand;
    }

    /**
     * Decompile the expr
     * @param s Buffer to which the result will be written.
     */
    @Override
    public void decompile(IndentPrintStream s) {
        s.print("(");
        getLeftOperand().decompile(s);
        s.print(" " + getOperatorName() + " ");
        getRightOperand().decompile(s);
        s.print(")");
    }

    /**
     * gets the operator name
     * @return
     */
    abstract protected String getOperatorName();

    /**
     * Iterator on the children of the class
     * @param f
     */
    @Override
    protected void iterChildren(TreeFunction f) {
        leftOperand.iter(f);
        rightOperand.iter(f);
    }

    /**
     * Prints the tree of children. Useful for debugging.
     * @param s
     * @param prefix
     */
    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        leftOperand.prettyPrint(s, prefix, false);
        rightOperand.prettyPrint(s, prefix, true);
    }

}
