package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.Type;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.RegisterOffset;
import fr.ensimag.ima.pseudocode.instructions.*;
import org.apache.log4j.Logger;

/**
 * Assignment, i.e. lvalue = expr.
 *
 * @author gl01
 * @date 01/01/2020
 */
public class Assign extends AbstractBinaryExpr {

    private static final Logger LOG = Logger.getLogger(Assign.class);
    @Override
    public AbstractLValue getLeftOperand() {
        return (AbstractLValue)super.getLeftOperand();
    }

    @Override
    public boolean isAssign() { return true; }

    public Assign(AbstractLValue leftOperand, AbstractExpr rightOperand) {
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
     * @return this expression's type, also decorating the tree with it
     * @throws ContextualError
     */
    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
        LOG.debug("verify Assign: start");
        Type typeObtained = getLeftOperand().verifyLValue(compiler, localEnv, currentClass);
        setRightOperand(getRightOperand().verifyRValue(compiler, localEnv, currentClass, typeObtained));
        LOG.debug("verify Assign: end");
        setType(typeObtained);
        return typeObtained;
    }

    /**
     * Generates the code that does an assign expr.
     * Separated in several cases depending on the number of free register lefts.
     * @param compiler
     */
    @Override
    protected void codeGenInst(DecacCompiler compiler) {
        int N = compiler.getFirstRegisterNumber();

        if (getLeftOperand().isFieldSelection()) {
            if (N == compiler.getCompilerOptions().getMaxRegister()) {
                //We only have 1 register left

                ((FieldSelection) getLeftOperand()).codeGenInstAssign(compiler); // the addr of a (for a field selection of type a.x) is now in Rn
                //we store it
                compiler.addInstruction(new PUSH(Register.getR(N)));
                compiler.addToFirstRegisterNumber(-1);

                //We compute the Right operand on the last register we have
                getRightOperand().codeGenInst(compiler);

                //We get back the addr of the object in RO
                compiler.addInstruction(new POP(Register.R0));

                //addr of the target field where we have to store the result of the right op
                RegisterOffset addr = new RegisterOffset(((FieldSelection) getLeftOperand()).getFieldName().getFieldDefinition().getIndex(), Register.R0);

                compiler.addInstruction(new STORE(Register.getR(N), addr));

            } else {

                //We have at least 2 register left

                //We have 1 less register after that
                getRightOperand().codeGenInst(compiler);

                ((FieldSelection) getLeftOperand()).codeGenInstAssign(compiler); // the addr of a (for a field selection of type a.x) is now in Rn + 1

                //addr  where we have to store the result
                RegisterOffset addr = new RegisterOffset(((FieldSelection) getLeftOperand()).getFieldName().getFieldDefinition().getIndex(), Register.getR(N + 1));
                compiler.addInstruction(new STORE(Register.getR(N), addr));
                compiler.addToFirstRegisterNumber(-1);
            }


        } else {
            AbstractIdentifier ident = (AbstractIdentifier) getLeftOperand();

            if (ident.getDefinition().isField()) {

                LOG.debug("implicit 'this' as Lvalue of assign expr");

                if (N == compiler.getCompilerOptions().getMaxRegister()) {
                    (new FieldSelection(new This(), ident)).codeGenInstAssign(compiler); //We got the object in Rn

                    //We no longer have free registers
                    //So we push the addr of the object
                    compiler.addInstruction(new PUSH(Register.getR(N)));
                    compiler.incrementCurrentNumberOfRegisterPushed();
                    compiler.addToFirstRegisterNumber(-1);

                    //Now we compute the right expr
                    getRightOperand().codeGenInst(compiler); //The result is in RN

                    //We get back the addr of the object in R0
                    compiler.addInstruction(new POP(Register.R0));
                    compiler.decrementCurrentNumberOfRegisterPushed();

                    //We finally can do the assignment :
                    //first we construct the addr of the field of the object
                    RegisterOffset field = new RegisterOffset(ident.getFieldDefinition().getIndex(), Register.getR(N));
                    //and then we assign
                    compiler.addInstruction(new STORE(Register.getR(N), field));


                } else {
                    //We have 1 less register after that
                    getRightOperand().codeGenInst(compiler); // Result is in RN

                    (new FieldSelection(new This(), ident)).codeGenInstAssign(compiler); //We got the object in Rn+1, but not the field yet
                    RegisterOffset field = new RegisterOffset(ident.getFieldDefinition().getIndex(), Register.getR(N + 1));
                    compiler.addInstruction(new STORE(Register.getR(N), field));

                    compiler.addToFirstRegisterNumber(-1); //we no longer need RN+1

                }

            } else if (ident.getDefinition().isParam()) {
                //We have 1 less register after that
                getRightOperand().codeGenInst(compiler); // Result is in RN
                compiler.addInstruction(new STORE(Register.getR(N), ident.getExpDefinition().getOperand()));
            } else {
                //We have 1 less register after that
                getRightOperand().codeGenInst(compiler); // Result is in RN
                compiler.addInstruction(new STORE(Register.getR(N), ident.getVariableDefinition().getOperand()));
            }


        }
    }


    @Override
    protected void controlFlow(DecacCompiler compiler, boolean b, Label E) {
        int n = compiler.getFirstRegisterNumber();

        codeGenInst(compiler);
        compiler.addInstruction(new CMP(0, Register.getR(n)));
        compiler.addToFirstRegisterNumber(-1);
        if (b) {
            compiler.addInstruction(new BNE(E));
        } else {
            compiler.addInstruction(new BEQ(E));
        }
    }

    @Override
    protected void codeGenPrint(DecacCompiler compiler) {
        codeGenInst(compiler);

    }


    @Override
    protected String getOperatorName() {
        return "=";
    }

}
