package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.BEQ;
import fr.ensimag.ima.pseudocode.instructions.BRA;
import fr.ensimag.ima.pseudocode.instructions.CMP;
import fr.ensimag.ima.pseudocode.instructions.WSTR;
import org.apache.log4j.Logger;

/**
 * Binary not operator
 * @author gl01
 * @date 01/01/2020
 */
public class Not extends AbstractUnaryExpr {
    private static final Logger LOG = Logger.getLogger(Not.class);
    public Not(AbstractExpr operand) {
        super(operand);
    }

    /**
     * Implements terminal Not of pass 3
     * @param compiler  (contains the "env_types" attribute)
     * @param localEnv
     *            Environment in which the expression should be checked
     *            (corresponds to the "env_exp" attribute)
     * @param currentClass
     *            Definition of the class containing the expression
     *            (corresponds to the "class" attribute)
     *             is null in the main bloc.
     * @return
     * @throws ContextualError
     */
    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
        LOG.trace("verify Not: start");
        Type operandType = getOperand().verifyExpr(compiler, localEnv, currentClass);
        if (!operandType.isBoolean()) {
            throw new ContextualError("Not operand must be boolean but is " + operandType + "(3.33)", getLocation());
        }
        LOG.trace("verify Not: end");
        setType(operandType);
        return operandType;
    }



    @Override
    protected void  controlFlow(DecacCompiler compiler, boolean b, Label E) {
        getOperand().controlFlow(compiler, !b, E);
    }

/*
    protected void codeGenPrint(DecacCompiler compiler) {
        int n = compiler.getFirstRegisterNumber();
        Label printFalse = Label.getNewPrintFalse();
        Label endOfPrint = Label.getNewEndOfPrintBool();

        compiler.addComment("Printing an boolean expression : computing the instruction");
        codeGenInst(compiler);
        //We will no longer need RN : free it
        compiler.addToFirstRegisterNumber(-1);


        compiler.addComment("Printing the result of boolean expression : checking if the result is false :");
        compiler.addInstruction(new CMP(0, Register.getR(n)));
        compiler.addInstruction(new BEQ(printFalse));

        compiler.addInstruction(new WSTR("true"));
        compiler.addInstruction(new BRA(endOfPrint));

        compiler.addLabel(printFalse);
        compiler.addInstruction(new WSTR("false"));

        compiler.addLabel(endOfPrint);

    }
*/

    @Override
    protected String getOperatorName() {
        return "!";
    }
}
