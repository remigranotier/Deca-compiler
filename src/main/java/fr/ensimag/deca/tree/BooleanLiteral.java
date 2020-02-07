package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.*;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.ImmediateInteger;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.BRA;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.instructions.WSTR;
import org.apache.log4j.Logger;

import java.io.PrintStream;

/**
 * true or false, basically
 * @author gl01
 * @date 01/01/2020
 */
public class BooleanLiteral extends AbstractExpr {
    private static final Logger LOG = Logger.getLogger(BooleanLiteral.class);
    private boolean value;
    private int intValue;

    public BooleanLiteral(boolean value) {
        this.value = value;
        if (value == false) {
            this.intValue = 0;
        }
        else { this.intValue = 1; }
    }

    public boolean getValue() {
        return value;
    }
    public int getIntValue() {
        return intValue;
    }

    protected boolean isBooleanLiteral() {
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
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
        LOG.trace("verify BooleanLiteral: start");
        Type result = new BooleanType(compiler.getSymbolTable().create("boolean"));
        setType(result);
        LOG.trace("verify BooleanLiteral: end");
        return result;
    }

    @Override
    protected void codeGenPrint(DecacCompiler compiler) {
        compiler.addInstruction(new WSTR(String.valueOf(value)));
    }

    @Override
    protected void codeGenInst(DecacCompiler compiler) {
        compiler.addInstruction(new LOAD(new ImmediateInteger(intValue), Register.getR(compiler.getFirstRegisterNumber())));
        compiler.addToFirstRegisterNumber(1);
    }

    /**
     * Generates the control flow for a boolean literal.
     * @param compiler
     * @param b
     * @param E
     */
    @Override
    protected void controlFlow(DecacCompiler compiler, boolean b, Label E) {
        compiler.addComment("Beginning of a Boolean Literal control flow : value : " + value + " ; b = " + b);
        if (value && b) {
            compiler.addInstruction(new BRA(E));
        }
        else if (!value){
            value = true;
            controlFlow(compiler, !b, E);
        }
        compiler.addComment("End of a Boolean Literal control flow");
    }

    @Override
    public void decompile(IndentPrintStream s) {
        s.print(Boolean.toString(value));
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
    String prettyPrintNode() {
        return "BooleanLiteral (" + value + ")";
    }

    @Override
    protected boolean mustNotGenCode() {
        return true;
    }
}
