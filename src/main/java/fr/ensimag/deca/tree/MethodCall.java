package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.*;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.RegisterOffset;
import fr.ensimag.ima.pseudocode.instructions.*;
import org.apache.log4j.Logger;

import java.io.PrintStream;

/**
 * Call of a class' method
 * @author gl01
 * @date 20/01/01
 */
public class MethodCall extends AbstractExpr {
    private final AbstractExpr className;
    private final AbstractIdentifier methodName;
    private final ListExpr listExpr;
    private static final Logger LOG = Logger.getLogger(MethodCall.class);
    public MethodCall(AbstractExpr className, AbstractIdentifier methodName, ListExpr listExpr) {
        this.className = className;
        this.methodName = methodName;
        this.listExpr = listExpr;
    }


    @Override
    public boolean isMethodCall(){
        return true;
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
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv, ClassDefinition currentClass) throws ContextualError {
        LOG.debug("verify method call of " + methodName.getName() + " :start");
        Type result = className.verifyExpr(compiler, localEnv, currentClass);
        EnvironmentExp envExp2 = result.asClassType("Trying to call method" +
                        " on something that is not a class (3.71)",
                getLocation()).getDefinition().getMembers();
        methodName.verifyExpr(compiler, envExp2, currentClass);
        String nature = methodName.getDefinition().getNature();
        if (methodName.getDefinition().getNature() != "method") {
            throw new ContextualError("Trying to call "+nature+" "+
                    methodName.getName()+" on class instead of method (3.72)",
                    getLocation());
        }
        Signature sig = methodName.getMethodDefinition().getSignature();
        Type callType = methodName.getMethodDefinition().getType();
        for (int i = 0; i < sig.size(); i++) {
            Type type = sig.paramNumber(i);
            listExpr.getList().get(i).verifyRValue(compiler, localEnv, currentClass, type);
        }
        LOG.debug("verify method call of " + methodName.getName() + " :end");
        setType(callType);
        return callType;
    }

    /**
     *
     * @param s Buffer to which the result will be written.
     */
    @Override
    public void decompile(IndentPrintStream s) {
        if (className.isFloatLiteral());
        String potentiallyEmpty = className.decompile();
        className.decompile(s);
        if (!potentiallyEmpty.equals("")) {
            s.print(".");
        }
        methodName.decompile(s);
        s.print("(");
        listExpr.decompile(s);
        s.print(")");
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        className.prettyPrint(s, prefix, false);
        methodName.prettyPrint(s, prefix, false);
        listExpr.prettyPrint(s, prefix, true);
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        className.iter(f);
        methodName.iter(f);
        listExpr.iter(f);
    }

    @Override
    protected void codeGenInst(DecacCompiler compiler) {
        int n = compiler.getFirstRegisterNumber();
        codeGenMethodCall(compiler);
        compiler.addInstruction(new LOAD(Register.R0, Register.getR(n)));
        compiler.addToFirstRegisterNumber(1);
    }


    /**
     * Generates the code of a method call
     * @param compiler
     */
    protected void codeGenMethodCall(DecacCompiler compiler) {
        int n = compiler.getFirstRegisterNumber();

        //first we solve the other recursive calls on the left
        className.codeGenInst(compiler); //This gives back the addr of the object on which we will call the method in Rn

        //we get the index of the called method
        int methodIndex = methodName.getMethodDefinition().getIndex();

        //we get the number of params to store for the method call and ADDSP accordingly
        int nbOfParams = listExpr.size();
        compiler.addComment("Beginning of a method call : method at index " + methodIndex + " ; with " + nbOfParams + " params.");
        compiler.addInstruction(new ADDSP(nbOfParams + 1)); //+1 because the object is also an imlplicit parameter

        //Storing the implicit parameter (the object itself)
        compiler.addInstruction(new STORE(Register.getR(n), new RegisterOffset(0, Register.SP)));
        //freeing the register
        compiler.addToFirstRegisterNumber(-1);

        for (int i = 0 ; i < nbOfParams ; i++) {
            //First we compute the expr of the param ; result stored in Rn
            AbstractExpr expr = listExpr.getList().get(i);
            expr.codeGenInst(compiler);
            //then we store it
            compiler.addInstruction(new STORE(Register.getR(n), new RegisterOffset(-(i + 1), Register.SP)));

            //freeing the register
            compiler.addToFirstRegisterNumber(-1);

        }

        compiler.addInstruction(new LOAD(new RegisterOffset(0, Register.SP), Register.getR(n)));
        //used a new register
        compiler.addToFirstRegisterNumber(1);
        compiler.getErrorManager().getNullDereferencement().genBranch();
        //no longer need it
        compiler.addToFirstRegisterNumber(-1);

        //starting the method call itself
        //getting method table addr
        compiler.addInstruction(new LOAD(new RegisterOffset(0, Register.getR(n)), Register.getR(n)));

        //jumping on the method : index +1 because of object.equals
        compiler.addInstruction(new BSR(new RegisterOffset(methodIndex + 1, Register.getR(n))));

        //we got the result in R0

        //we pushed nbOfParams + 1 (object implicit parameter) + 2 (BSR)
        compiler.addToCurrentNumberOfRegisterPushed(nbOfParams + 3);

        //unstacking the parameters
        compiler.addInstruction(new SUBSP(nbOfParams + 1));

        //we unstacked nbParams + 3 elements
        compiler.addToCurrentNumberOfRegisterPushed(- (nbOfParams + 3));
    }

    @Override
    protected void controlFlow(DecacCompiler compiler, boolean b, Label E) {
        codeGenMethodCall(compiler); //we got the resulting boolean in R0
        compiler.addInstruction(new CMP(0, Register.R0));
        if (b) {
            compiler.addInstruction(new BNE(E));
        } else {
            compiler.addInstruction(new BEQ(E));
        }
    }

    @Override
    protected void codeGenPrint(DecacCompiler compiler) {
        codeGenMethodCall(compiler);
        compiler.addInstruction(new LOAD(Register.R0, Register.R1));
        compiler.addInstruction(new WINT());

    }

    @Override
    protected void codeGenPrint(DecacCompiler compiler, boolean printHex) {
        codeGenMethodCall(compiler);
        compiler.addInstruction(new LOAD(Register.R0, Register.R1));
        if (printHex) {
            compiler.addInstruction(new WFLOATX());
        } else {
            compiler.addInstruction(new WFLOAT());
        }
    }

}
