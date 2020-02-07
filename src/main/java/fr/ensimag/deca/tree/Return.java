package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.DecacFatalError;
import fr.ensimag.deca.context.*;
import fr.ensimag.deca.tools.DecacInternalError;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.BRA;
import fr.ensimag.ima.pseudocode.instructions.LOAD;

import java.io.PrintStream;

/**
 * Return operator for methods
 * @author gl01
 * @date 20/01/2020
 */
public class Return extends AbstractInst{
    private final AbstractExpr expr;

    public Return(AbstractExpr expr) {
        this.expr = expr;
    }

    @Override
    public boolean isReturn() { return true; }

    /**
     * Implements non terminal inst in pass 3
     * @param compiler contains the "env_types" attribute
     * @param localEnv corresponds to the "env_exp" attribute
     * @param currentClass
     *          corresponds to the "class" attribute (null in the main bloc).
     * @param returnType
     *          corresponds to the "return" attribute (void in the main bloc).
     * @throws ContextualError
     */
    @Override
    protected void verifyInst(DecacCompiler compiler,
            EnvironmentExp localEnv, ClassDefinition currentClass, Type returnType) throws ContextualError {
        if (returnType.isVoid()) {
            throw new ContextualError("Cannot return in type void instruction (3.24)", getLocation());
        }
        expr.verifyRValue(compiler, localEnv, currentClass, returnType);
    }

    /**
     *
     * @param s Buffer to which the result will be written.
     */
    @Override
    public void decompile(IndentPrintStream s) {
        s.print("return ");
        expr.decompile(s);
        s.print(";");
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        expr.prettyPrint(s, prefix, true);
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        expr.iter(f);
    }

    protected void codeGenInst(DecacCompiler compiler) {
        int n = compiler.getFirstRegisterNumber();

        expr.codeGenInst(compiler);

        compiler.addInstruction(new LOAD(Register.getR(n), Register.R0));
        compiler.addToFirstRegisterNumber(-1);
        compiler.addInstruction(new BRA(compiler.getEndOfCurrentMethod()));
    }
}
