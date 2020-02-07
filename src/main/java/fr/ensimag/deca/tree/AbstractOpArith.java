package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.Type;
import fr.ensimag.ima.pseudocode.*;
import fr.ensimag.ima.pseudocode.instructions.*;
import org.apache.log4j.Logger;

import static fr.ensimag.ima.pseudocode.Register.R0;
import static fr.ensimag.ima.pseudocode.Register.R1;

/**
 * Arithmetic binary operations (+, -, /, ...)
 * 
 * @author gl01
 * @date 01/01/2020
 */
public abstract class AbstractOpArith extends AbstractBinaryExpr {
    private static final Logger LOG = Logger.getLogger(AbstractOpArith.class);
    public AbstractOpArith(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }

    /**
     * @param compiler  (contains the "env_types" attribute)
     * @param localEnv
     *            Environment in which the expression should be checked
     *            (corresponds to the "env_exp" attribute)
     * @param currentClass
     *            Definition of the class containing the expression
     *            (corresponds to the "class" attribute)
     *             is null in the main bloc.
     * @return This expression type, also decorating it in the tree
     * @throws ContextualError
     */
    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
        LOG.debug("verify "+ getOperatorName()+": start");
        Type leftType = getLeftOperand().verifyExpr(compiler, localEnv, currentClass);
        Type rightType = getRightOperand().verifyExpr(compiler, localEnv, currentClass);
        if ((!(leftType.isFloat() || leftType.isInt())) || (!(rightType.isFloat() || rightType.isInt()))) {
            throw new ContextualError("Arithmetic operands must be int or float and are " +
                    leftType + " and " + rightType + "(3.33)", getLocation());
        }
        LOG.debug("verify "+ getOperatorName()+ ": end");
        if (leftType.isFloat()) { // Return float if there's at least one float, else int
            if (rightType.isInt()) {
                ConvFloat conv = new ConvFloat(getRightOperand());
                conv.verifyExpr(compiler, localEnv, currentClass);
                setRightOperand(conv);
            }
            setType(leftType);
            return leftType;
        } else {
            if (rightType.isFloat()) {
                ConvFloat conv = new ConvFloat(getLeftOperand());
                conv.verifyExpr(compiler, localEnv, currentClass);
                setLeftOperand(conv);
            }
            setType(rightType);
            return rightType;
        }
    }

    /**
     * Generates the code that executes this arith expression, given the registers where the operands are stored.
     * @param reg1
     * @param reg2
     * @param compiler
     */
    private void instruction(DVal reg1, GPRegister reg2, DecacCompiler compiler){
        switch (getOperatorName()) {
            case "+":
                compiler.addInstruction(new ADD(reg1, reg2));
                if (this.getType().isFloat()) {
                    compiler.getErrorManager().getArithOverflow().genBranch();
                }
                break;
            case "-":
                compiler.addInstruction(new SUB(reg1, reg2));
                if (this.getType().isFloat()) {
                    compiler.getErrorManager().getArithOverflow().genBranch();
                }
                break;
            case "*":
                compiler.addInstruction(new MUL(reg1, reg2));
                if (this.getType().isFloat()) {
                    compiler.getErrorManager().getArithOverflow().genBranch();
                }
                break;
            case "/":
                if (getLeftOperand().getType().isFloat()) {
                    compiler.addInstruction(new DIV(reg1, reg2));
                    // In the case of a division by zero, we consider it is still a kind of "overflow" :
                    compiler.getErrorManager().getArithOverflow().genBranch();
                }
                if (getLeftOperand().getType().isInt()) {
                    compiler.addInstruction(new QUO(reg1, reg2));
                    compiler.getErrorManager().getDivZero().genBranch();
                }
                break;
            case "%":
                compiler.addInstruction(new REM(reg1, reg2));
                compiler.getErrorManager().getModuloZero().genBranch();

                break;

        }
    }

    /**
     * Generates the assembly code for computing this operation.
     * It computes each operand first, then computes the operation.
     * @param compiler
     */
    @Override
    protected void codeGenInst(DecacCompiler compiler) {

        int N = compiler.getFirstRegisterNumber();


        if (!getRightOperand().isImmediate() && (N != compiler.getCompilerOptions().getMaxRegister())){
            getLeftOperand().codeGenInst(compiler);
            getRightOperand().codeGenInst(compiler);
            compiler.addToFirstRegisterNumber(-1);
            instruction(Register.getR(N + 1), Register.getR(N), compiler);
        }

        else if (getRightOperand().isImmediate()) {
            getLeftOperand().codeGenInst(compiler);
            if (getRightOperand().isIntLiteral()) {
                ImmediateInteger val = new ImmediateInteger(((IntLiteral) getRightOperand()).getValue());
                instruction(val, Register.getR(N), compiler);

            }
            else {
                ImmediateFloat val = new ImmediateFloat(((FloatLiteral) getRightOperand()).getValue());
                instruction(val, Register.getR(N), compiler);

            }
        }

        else if(!getRightOperand().isImmediate() && (N == compiler.getCompilerOptions().getMaxRegister())) {

            getLeftOperand().codeGenInst(compiler);
            compiler.addInstruction(new PUSH(Register.getR(N)));
            //A register was pushed to the stack :
            compiler.incrementCurrentNumberOfRegisterPushed();

            compiler.addToFirstRegisterNumber(-1); // to get back the initial N value in codeGenInst below
            getRightOperand().codeGenInst(compiler);
            compiler.addInstruction(new LOAD(Register.getR(N), R0));
            compiler.addInstruction(new POP(Register.getR(N)));
            //A register was popped from the stack
            compiler.decrementCurrentNumberOfRegisterPushed();
            instruction(R0, Register.getR(N), compiler);

        }


    }

    @Override
    protected void codeGenPrint(DecacCompiler compiler) {
        codeGenInst(compiler);
        compiler.addInstruction(new LOAD(Register.getR(compiler.getFirstRegisterNumber() -1), R1));
        // We no longer need Rn-1 : we free it
        compiler.addToFirstRegisterNumber(-1);

        compiler.addComment("On ecrit ce qui se trouve dans R1 (le resultat)");
        compiler.addInstruction(new WINT());
    }

    @Override
    protected void codeGenPrint(DecacCompiler compiler, boolean printHex) {
        codeGenInst(compiler);
        compiler.addInstruction(new LOAD(Register.getR(compiler.getFirstRegisterNumber() -1), R1));
        // We no longer need Rn-1 : we free it
        compiler.addToFirstRegisterNumber(-1);

        compiler.addComment("On ecrit ce qui se trouve dans R1 (le resultat)");

        if (printHex) {
            compiler.addInstruction(new WFLOATX());
        } else {
            compiler.addInstruction(new WFLOAT());
        }

    }


    @Override
    protected boolean mustNotGenCode() {
        return true;
    }

}
