package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.*;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.ImmediateInteger;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.instructions.WINT;
import fr.ensimag.ima.pseudocode.instructions.WSTR;
import org.apache.log4j.Logger;

import java.io.PrintStream;

/**
 * Integer literal
 *
 * @author gl01
 * @date 01/01/2020
 */
public class IntLiteral extends AbstractExpr {
    private static final Logger LOG = Logger.getLogger(IntLiteral.class);
    public int getValue() {
        return value;
    }

    private int value;

    public IntLiteral(int value) {
        this.value = value;
    }

    /**
     * Implements terminal IntLiteral
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
        LOG.trace("verify IntLiteral: start");
        Type result = new IntType(compiler.getSymbolTable().create("int"));
        setType(result);
        LOG.trace("verify IntLiteral: end");
        return result;
    }

    @Override
    protected boolean isIntLiteral() {
        return true;
    }
    protected boolean isImmediate() {return true; }


    @Override
    String prettyPrintNode() {
        return "Int (" + getValue() + ")";
    }

    @Override
    protected void codeGenPrint(DecacCompiler compiler) {
        compiler.addInstruction(new LOAD(value, Register.R1));
        compiler.addInstruction(new WINT());
    }

    @Override
    protected void codeGenInst(DecacCompiler compiler) {
        compiler.addInstruction(new LOAD(new ImmediateInteger(value), Register.getR(compiler.getFirstRegisterNumber())));
        compiler.addToFirstRegisterNumber(1);
    }

    /**
     *
     * @param s Buffer to which the result will be written.
     */
    @Override
    public void decompile(IndentPrintStream s) {
        s.print(Integer.toString(value));
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        // leaf node => nothing to do
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        // leaf node => nothing to do
    }

    @Override
    protected boolean mustNotGenCode() {
        return true;
    }

}
