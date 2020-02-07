package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.*;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.ImmediateFloat;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.instructions.WFLOAT;
import fr.ensimag.ima.pseudocode.instructions.WFLOATX;
import fr.ensimag.ima.pseudocode.instructions.WSTR;
import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;

import java.io.PrintStream;
import java.util.zip.DataFormatException;

/**
 * Single precision, floating-point literal
 *
 * @author gl01
 * @date 01/01/2020
 */
public class FloatLiteral extends AbstractExpr {
    private static final Logger LOG = Logger.getLogger(FloatLiteral.class);
    public float getValue() {
        return value;
    }

    private float value;

    public FloatLiteral(float value) {
        Validate.isTrue(!Float.isInfinite(value),
                "literal values cannot be infinite");
        Validate.isTrue(!Float.isNaN(value),
                "literal values cannot be NaN");
        this.value = value;
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
        LOG.trace("verify FloatLiteral: start");
        Type result = new FloatType(compiler.getSymbolTable().create("float"));
        setType(result);
        LOG.trace("verify FloatLiteral: end");
        return result;
    }

    @Override
    protected boolean isFloatLiteral() {
        return true;
    }
    protected boolean isImmediate() {return true; }


    @Override
    protected void codeGenPrint(DecacCompiler compiler, boolean printHex) {
        compiler.addInstruction(new LOAD(new ImmediateFloat(value), Register.R1));
        if (printHex) {
            compiler.addInstruction(new WFLOATX());
        } else {
            compiler.addInstruction(new WFLOAT());
        }
    }

    @Override
    protected void codeGenInst(DecacCompiler compiler) {
        compiler.addInstruction(new LOAD(new ImmediateFloat(value), Register.getR(compiler.getFirstRegisterNumber())));
        compiler.addToFirstRegisterNumber(1);
    }

    /**
     *
     * @param s Buffer to which the result will be written.
     */
    @Override
    public void decompile(IndentPrintStream s) {
        s.print(java.lang.Float.toString(value));
    }

    @Override
    String prettyPrintNode() {
        return "Float (" + getValue() + ")";
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
