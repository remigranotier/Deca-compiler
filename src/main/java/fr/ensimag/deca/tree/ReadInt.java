package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.*;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.*;
import org.apache.log4j.Logger;

import java.io.PrintStream;

import static fr.ensimag.ima.pseudocode.Register.R1;

/**
 * Input reading int entered by user
 * @author gl01
 * @date 01/01/2020
 */
public class ReadInt extends AbstractReadExpr {
    private static final Logger LOG = Logger.getLogger(ReadInt.class);

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
     * @return expression's type, also decorating the tree with it
     * @throws ContextualError
     */
    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
        LOG.trace("verify ReadInt: start");
        Type result = new IntType(compiler.getSymbolTable().create("int"));
        LOG.trace("verify ReadInt: end");
        setType(result);
        return result;
    }


    @Override
    public void codeGenInst(DecacCompiler compiler){
        compiler.addInstruction(new RINT());
        compiler.getErrorManager().getRintWithFloat().genBranch();
        compiler.addInstruction(new LOAD(R1, Register.getR(compiler.getFirstRegisterNumber())));
        compiler.addToFirstRegisterNumber(1);
    };

    @Override
    protected void codeGenPrint(DecacCompiler compiler) {
        codeGenInst(compiler);
        compiler.addInstruction(new WINT());
    }


    /**
     *
     * @param s Buffer to which the result will be written.
     */
    @Override
    public void decompile(IndentPrintStream s) {
        s.print("readInt()");
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
