package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.*;
import fr.ensimag.deca.tools.DecacInternalError;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.NullOperand;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.RegisterOffset;
import fr.ensimag.ima.pseudocode.instructions.*;
import org.apache.log4j.Logger;

/**
 * instanceof operator
 * @author gl01
 * @date 24/01/2020
 */
public class InstanceOf extends AbstractBinaryExpr {
    private static final Logger LOG = Logger.getLogger(InstanceOf.class);

    public InstanceOf(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }

    @Override
    protected String getOperatorName() {
        return " instanceof ";
    }

    /**
     * Implements terminal InstanceOf of pass 3
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
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv, ClassDefinition currentClass) throws ContextualError {
        LOG.debug("verify instanceof : start");
        Type type1 = getLeftOperand().verifyExpr(compiler, localEnv, currentClass);
        try {
            AbstractIdentifier rightOp = (AbstractIdentifier) getRightOperand();
            Type type2 = rightOp.verifyType(compiler);
            if(type1.isClassOrNull() && type2.isClass()) {
                Type typeResult = new BooleanType(compiler.getSymbolTable().create("boolean"));
                setType(typeResult);
                LOG.debug("verify instanceof : start");
                return typeResult;
            }
            throw new ContextualError("Cannot apply operator instanceof on types "+
                    type1.getName()+" and "+type2.getName()+" (3.40)", getLocation());
        } catch (ClassCastException e) {
            throw new ContextualError("Right operand of instanceof must be Identifier " +
                    "but is "+ getRightOperand().getClass()+" (3.40)", getLocation());
        }
    }

    @Override
    protected void controlFlow(DecacCompiler compiler, boolean b, Label E) {
        compiler.addComment("Start of an instanceof");

        getLeftOperand().codeGenInst(compiler); // getting back the object addr in Rn

        checkAddrLoop(compiler, b, E);
    }


    /**
     * Function that generates the code of the loop that checks if the addr contained in Rn is the same than the
     * right operand.
     * @param compiler
     * @param b
     * @param E
     */
    private void checkAddrLoop(DecacCompiler compiler, boolean b, Label E) {
        int n = compiler.getFirstRegisterNumber();
        //Getting back the right op's method table addr to compare it
        RegisterOffset rightOpAddr = ((AbstractIdentifier) getRightOperand()).getClassDefinition().getMethodTableAddr();
        compiler.addInstruction(new LEA(rightOpAddr, Register.R0));

        //we have the result of the right expr in Rn-1


        Label start = Label.getNewControlFlow();
        compiler.addLabel(start);
        //getting back its method table addr
        compiler.addInstruction(new LOAD(new RegisterOffset(0, Register.getR(n - 1)), Register.getR(n - 1)));
        compiler.addInstruction(new CMP(Register.R0, Register.getR(n - 1)));

        if (b) {
            compiler.addInstruction(new BEQ(E)); //condition validee : instanceof vrai
            compiler.addInstruction(new CMP(new NullOperand(), Register.getR(n - 1)));
            //if equal, We got all the way up to object method table : instanceof is false.
            compiler.addInstruction(new BNE(start)); //if we are not at object yet, start again
        } else {
            Label end = Label.getNewEndOfBoolExpr();

            compiler.addInstruction(new BEQ(end)); //instance of true, but b=false : we don't jump to E
            compiler.addInstruction(new CMP(new NullOperand(), Register.getR(n - 1)));
            //We got all the way up to object method table : instanceof is false : we jump
            compiler.addInstruction(new BEQ(E)); //if we are not at object yet, start again
            compiler.addInstruction(new BRA(start));

            compiler.addLabel(end);
        }
        compiler.addToFirstRegisterNumber(-1);
        compiler.addComment("End of an instanceof");

    }

    /**
     * We adapt a little the classic control flow in the case it is used in a cast : in a cast, we want to keep the
     * result of the expression in Rn, unlike "classic" control flow where once calculated, we don't need it anymore.
     * @param compiler
     * @param b
     * @param E
     */
    protected void castControlFlow(DecacCompiler compiler, boolean b, Label E) {
        int n = compiler.getFirstRegisterNumber();
        compiler.addComment("Start of an instanceof");

        getLeftOperand().codeGenInst(compiler); // getting back the object addr in Rn
        //we save it for later
        compiler.addInstruction(new PUSH(Register.getR(n)));
        //the register is still usefull : we do not free it here
        compiler.incrementCurrentNumberOfRegisterPushed();

        checkAddrLoop(compiler, b, E);

    }


}
