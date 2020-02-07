package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.*;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.*;
import org.apache.log4j.Logger;

/**
 * Comparison operations
 * @author gl01
 * @date 01/01/2020
 */
public abstract class AbstractOpCmp extends AbstractBinaryExpr {
    private static final Logger LOG = Logger.getLogger(AbstractOpCmp.class);
    public AbstractOpCmp(AbstractExpr leftOperand, AbstractExpr rightOperand) {
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
        if ((!(leftType.isFloat() || leftType.isInt())) || (!(rightType.isFloat() || rightType.isInt()))) {
            throw new ContextualError("Comparison operands must be int or float and are " +
                    leftType + " and " + rightType + "(3.33)", getLocation());
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


    /**
     * Generates the control flow for a comparison.
     * The exact comparison to execute is computed in this sub-classes
     * @param compiler
     * @param b
     * @param E
     */
    @Override
    protected void controlFlow(DecacCompiler compiler, boolean b, Label E) {
        if (b) {
            compiler.addComment("Beginning of a comparison in a control flow");

            int n = compiler.getFirstRegisterNumber();

            if (getLeftOperand().isIntLiteral() || getLeftOperand().isFloatLiteral()) {
                getRightOperand().codeGenInst(compiler);

                if (getLeftOperand().isIntLiteral()) {
                    int val = ((IntLiteral) getLeftOperand()).getValue();
                    compiler.addInstruction(new CMP(val, Register.getR(n)));
                } else {
                    float val = ((FloatLiteral) getLeftOperand()).getValue();
                    compiler.addInstruction(new CMP(val, Register.getR(n)));
                }
                //We no longer need Rn
                compiler.addToFirstRegisterNumber(-1);
                //the jump we have to do depends on the dynamic type : we will do that in concrete classes

            } else if (getRightOperand().isIntLiteral() || getRightOperand().isFloatLiteral()) {
                getLeftOperand().codeGenInst(compiler);

                if (getRightOperand().isIntLiteral()) {
                    int val = ((IntLiteral) getRightOperand()).getValue();
                    compiler.addInstruction(new CMP(val, Register.getR(n)));
                } else {
                    float val = ((FloatLiteral) getRightOperand()).getValue();
                    compiler.addInstruction(new CMP(val, Register.getR(n)));
                }

                //We no longer need Rn
                compiler.addToFirstRegisterNumber(-1);
                //the jump we have to do depends on the dynamic type : we will do that in concrete classes

            } else {
                if (n == compiler.getCompilerOptions().getMaxRegister()) {

                    getLeftOperand().codeGenInst(compiler);
                    compiler.addInstruction(new PUSH(Register.getR(n)));
                    //We keep an eye on the number of register pushed to the stack to generate TSTO instruction
                    //a register was stacked :
                    compiler.incrementCurrentNumberOfRegisterPushed();

                    //Push freed the last register
                    compiler.addToFirstRegisterNumber(-1);

                    getRightOperand().codeGenInst(compiler);
                    compiler.addInstruction(new LOAD(Register.getR(n), Register.R0));
                    compiler.addInstruction(new POP(Register.getR(n)));
                    //A register was un-stacked :
                    compiler.decrementCurrentNumberOfRegisterPushed();

                    compiler.addInstruction(new CMP(Register.R0, Register.getR(n)));
                    //After CMP we no longer need Rn
                    compiler.addToFirstRegisterNumber(-1);


                } else {
                    getLeftOperand().codeGenInst(compiler);
                    getRightOperand().codeGenInst(compiler);

                    compiler.addInstruction(new CMP(Register.getR(n + 1), Register.getR(n)));
                    //We no longer need Rn and Rn+1
                    compiler.addToFirstRegisterNumber(-2);
                    //the jump we have to do depends on the dynamic type : we will do that in concrete classes
                }
            }
        }
        // the case !b will be treated in concrete classes : we will call the opposite comparison
    }

    @Override
    protected boolean mustNotGenCode() {
        return true;
    }

}
