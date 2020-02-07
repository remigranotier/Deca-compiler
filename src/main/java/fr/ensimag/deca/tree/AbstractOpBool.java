package fr.ensimag.deca.tree;

import com.sun.tools.corba.se.idl.constExpr.ShiftLeft;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.*;
import org.apache.log4j.Logger;

/**
 * Operations on boolean expressions
 * @author gl01
 * @date 01/01/2020
 */
public abstract class AbstractOpBool extends AbstractBinaryExpr {
    private static final Logger LOG = Logger.getLogger(AbstractOpBool.class);
    public AbstractOpBool(AbstractExpr leftOperand, AbstractExpr rightOperand) {
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
        LOG.debug("verify "+ getOperatorName() + ": start");
        Type leftType = getLeftOperand().verifyExpr(compiler, localEnv, currentClass);
        Type rightType = getRightOperand().verifyExpr(compiler, localEnv, currentClass);
        if (!leftType.isBoolean() || !rightType.isBoolean()) {
            throw new ContextualError("Boolean operators operands must be boolean but are " +
                    leftType + " and " + rightType + "(3.33)", getLocation());
        }
        LOG.debug("verify " + getOperatorName() + ": end");
        setType(leftType);
        return leftType;
    }

    @Override
    protected boolean mustNotGenCode() {
        return true;
    }
}
