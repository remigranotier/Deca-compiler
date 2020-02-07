package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.Type;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.OPP;
import fr.ensimag.ima.pseudocode.instructions.WFLOAT;
import fr.ensimag.ima.pseudocode.instructions.WFLOATX;
import fr.ensimag.ima.pseudocode.instructions.WINT;
import org.apache.log4j.Logger;

import static fr.ensimag.ima.pseudocode.Register.R1;

/**
 * Unary minus arithmetical operand
 * @author gl01
 * @date 01/01/2020
 */
public class UnaryMinus extends AbstractUnaryExpr {
    private static final Logger LOG = Logger.getLogger(UnaryMinus.class);
    public UnaryMinus(AbstractExpr operand) {
        super(operand);
    }

    /**
     * Implements terminal UnaryMinus of pass 3
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
        LOG.trace("verify UnaryMinus: start");
        Type operandType = getOperand().verifyExpr(compiler, localEnv, currentClass);
        if (!operandType.isInt() && !operandType.isFloat()) {
            throw new ContextualError("Unary minus operand must be int or float but is " +
                    operandType + "(3.33)", getLocation());
        }
        LOG.trace("verify UnaryMinus: end");
        setType(operandType);
        return operandType;
    }

    @Override
    public void codeGenInst(DecacCompiler compiler){
        getOperand().codeGenInst(compiler);
        compiler.addInstruction(new OPP(Register.getR(compiler.getFirstRegisterNumber() - 1), Register.getR(compiler.getFirstRegisterNumber() - 1)));
    };

    @Override
    protected void codeGenPrint(DecacCompiler compiler) {

        getOperand().codeGenInst(compiler);
        compiler.addInstruction(new OPP(Register.getR(compiler.getFirstRegisterNumber() - 1), R1));
        // We no longer need R1 : we free it
        compiler.addToFirstRegisterNumber(-1);

        compiler.addInstruction(new WINT());

    }

    @Override
    protected void codeGenPrint(DecacCompiler compiler, boolean printHex) {
        getOperand().codeGenInst(compiler);
        compiler.addInstruction(new OPP(Register.getR(compiler.getFirstRegisterNumber() - 1), R1));
        // We no longer need R1 : we free it
        compiler.addToFirstRegisterNumber(-1);

        if (printHex) {
            compiler.addInstruction(new WFLOATX());
        } else {
            compiler.addInstruction(new WFLOAT());
        }

    }

    @Override
    protected String getOperatorName() {
        return "-";
    }

}
