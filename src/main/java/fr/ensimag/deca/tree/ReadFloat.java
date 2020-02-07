package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.*;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.instructions.RFLOAT;
import fr.ensimag.ima.pseudocode.instructions.WFLOAT;
import fr.ensimag.ima.pseudocode.instructions.WFLOATX;
import org.apache.log4j.Logger;

import java.io.PrintStream;

import static fr.ensimag.ima.pseudocode.Register.R1;

/**
 * Input reading float entered by user
 * @author gl01
 * @date 01/01/2020
 */
public class ReadFloat extends AbstractReadExpr {
    private static final Logger LOG = Logger.getLogger(ReadFloat.class);

    /**
     * Implements terminal Read Float
     * @param compiler  (contains the "env_types" attribute)
     * @param localEnv
     *            Environment in which the expression should be checked
     *            (corresponds to the "env_exp" attribute)
     * @param currentClass
     *            Definition of the class containing the expression
     *            (corresponds to the "class" attribute)
     *             is null in the main bloc.
     * @return this expression's type
     * @throws ContextualError
     */
    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
        LOG.trace("verify ReadFloat: start");
        Type result = new FloatType(compiler.getSymbolTable().create("float"));
        LOG.trace("verify ReadFloat: end");
        setType(result);
        return result;
    }

    @Override
    public void codeGenInst(DecacCompiler compiler){
        compiler.addInstruction(new RFLOAT());
        compiler.getErrorManager().getRfloatWithInt().genBranch();
        compiler.addInstruction(new LOAD(R1, Register.getR(compiler.getFirstRegisterNumber())));
        compiler.addToFirstRegisterNumber(1);
    };

    @Override
    protected void codeGenPrint(DecacCompiler compiler, boolean printHex) {
        codeGenInst(compiler);
        if (printHex) {
            compiler.addInstruction(new WFLOATX());
        } else {
            compiler.addInstruction(new WFLOAT());
        }
    }

    @Override
    public void decompile(IndentPrintStream s) {
        s.print("readFloat()");
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        // leaf node => nothing to do
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        // leaf node => nothing to do
    }

}
