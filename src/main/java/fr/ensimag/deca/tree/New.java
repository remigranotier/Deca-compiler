package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.RegisterOffset;
import fr.ensimag.ima.pseudocode.instructions.*;
import org.apache.log4j.Logger;

import java.io.PrintStream;

/**
 * New keyword, creation and allocation operator
 * @author gl01
 * @date 20/01/2020
 */
public class New extends AbstractExpr{
    private final AbstractIdentifier className;
    private static final Logger LOG = Logger.getLogger(New.class);
    public New(AbstractIdentifier className) {
        this.className = className;
    }

    /**
     * Implements terminal New of pass 3
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
        LOG.trace("verify new :start");
        Type result = className.verifyType(compiler).asClassType("Type in \"new\" is not a class ",
                getLocation());
        setType(result);
        LOG.trace("verify new :end");
        return result;
    }

    /**
     *
     * @param s Buffer to which the result will be written.
     */
    @Override
    public void decompile(IndentPrintStream s) {
        s.print("new ");
        className.decompile(s);
        s.print("()");
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        className.prettyPrint(s, prefix, true);
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        className.iter(f);
    }

    /**
     * Generates the code for a new instruction
     * @param compiler
     */
    protected void codeGenInst(DecacCompiler compiler) {

        int N = compiler.getFirstRegisterNumber();
        compiler.addInstruction(new NEW(className.getClassDefinition().getNumberOfFields() + 1, Register.getR(N)));
        compiler.getErrorManager().getHeapFull().genBranch();
        compiler.addToFirstRegisterNumber(1);
        compiler.addInstruction(new LEA(className.getClassDefinition().getMethodTableAddr(), Register.R0));

        compiler.addToCurrentNumberOfRegisterPushed(2);
        compiler.addInstruction(new STORE(Register.R0, new RegisterOffset(0, Register.getR(N))));
        compiler.addInstruction(new PUSH(Register.getR(N)));

        compiler.addToCurrentNumberOfRegisterPushed(2);
        compiler.addInstruction(new BSR(new Label("init." + className.getName())));
        compiler.addInstruction(new POP(Register.getR(N)));
        compiler.addToCurrentNumberOfRegisterPushed(-3);

    }

}
