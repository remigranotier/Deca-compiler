package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.Type;
import org.apache.log4j.Logger;

/**
 * Rest of euclidean division operator (modulo)
 * @author gl01
 * @date 01/01/2020
 */
public class Modulo extends AbstractOpArith {
    private static final Logger LOG = Logger.getLogger(Modulo.class);
    public Modulo(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }

    /**
     * Implements terminal Modulo
     * @param compiler  (contains the "env_types" attribute)
     * @param localEnv
     *            Environment in which the expression should be checked
     *            (corresponds to the "env_exp" attribute)
     * @param currentClass
     *            Definition of the class containing the expression
     *            (corresponds to the "class" attribute)
     *             is null in the main bloc.
     * @return this expression's type, also decorating the tree with it
     * @throws ContextualError
     */
    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
        LOG.trace("verify Modulo: start");
        Type leftType = getLeftOperand().verifyExpr(compiler, localEnv, currentClass);
        Type rightType = getRightOperand().verifyExpr(compiler, localEnv, currentClass);
        if (!leftType.isInt() || !rightType.isInt()) {
            throw new ContextualError("Modulo operands must be int and are " +
                    leftType + " and " + rightType + "(3.33)", getLocation());
        }
        LOG.trace("verify Modulo: end");
        setType(leftType);
        return leftType;
    }


    @Override
    protected String getOperatorName() {
        return "%";
    }

}
