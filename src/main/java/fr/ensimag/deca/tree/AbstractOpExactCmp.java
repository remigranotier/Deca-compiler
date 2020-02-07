package fr.ensimag.deca.tree;


import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.*;
import org.apache.log4j.Logger;

/**
 * Operatins comparing (in)equalities
 * @author gl01
 * @date 01/01/2020
 */
public abstract class AbstractOpExactCmp extends AbstractOpCmp {
    private static final Logger LOG = Logger.getLogger(AbstractOpExactCmp.class);
    public AbstractOpExactCmp(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }

    /**
     *
     * @param compiler  (contains the "env_types" attribute)
     * @param localEnv
     *            Environment in which the expression should be checked
     *            (corresponds to the "env_exp" attribute)
     * @param currentClass
     *            Definition of the class containing the expression
     *            (corresponds to the "class" attribute)
     *             is null in the main bloc.
     * @return This expression's type, also decorating the tree with it
     * @throws ContextualError
     */
    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
                           ClassDefinition currentClass) throws ContextualError {
        LOG.debug("verify " + getOperatorName() + ": start");
        Type leftType = getLeftOperand().verifyExpr(compiler, localEnv, currentClass);
        Type rightType = getRightOperand().verifyExpr(compiler, localEnv, currentClass);
        if (!((leftType.isBoolean() && rightType.isBoolean())||
                (leftType.isInt() || leftType.isFloat()) && (rightType.isInt() || rightType.isFloat()))) {
            throw new ContextualError("(In)equality operands must be int or float or both booleans and are " +
                    leftType + " and " + rightType + "(3.3)", getLocation());
        }
        LOG.debug("verify " + getOperatorName() + ": end");
        if (leftType.isFloat()) {
            if (rightType.isInt()) {
                ConvFloat conv = new ConvFloat(getRightOperand());
                conv.verifyExpr(compiler, localEnv, currentClass);
                setRightOperand(conv);
            }
        } else {
            if (rightType.isFloat()) {
                ConvFloat conv = new ConvFloat(getLeftOperand());
                conv.verifyExpr(compiler, localEnv, currentClass);
                setLeftOperand(conv);
            }
        }
        Type result = new BooleanType(compiler.getSymbolTable().create("boolean"));
        setType(result);
        return result;
    }
}
