package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.RegisterOffset;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import org.apache.log4j.Logger;

import java.io.PrintStream;

/**
 * This keyword, referring to current class within classes
 * @author gl01
 * @date 20/01/2020
 */
public class This extends AbstractExpr{
    private static final Logger LOG = Logger.getLogger(This.class);

    /**
     * Implements terminal this of pass 3
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
        LOG.debug("verify this : start");
        if (currentClass == null) {
            throw new ContextualError("Trying to use \"this\" in main (3.43)", getLocation());
        }
        setType(currentClass.getType());
        LOG.debug("verify this : end");
        return currentClass.getType();
    }

    /**
     *
     * @param s Buffer to which the result will be written.
     */
    @Override
    public void decompile(IndentPrintStream s) {
        s.print("this");
    }



    @Override
    protected void codeGenInst(DecacCompiler compiler) {
        int n = compiler.getFirstRegisterNumber();
        compiler.addInstruction(new LOAD(new RegisterOffset(-2, Register.LB), Register.getR(n)));
        compiler.addToFirstRegisterNumber(1);
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        //Leaf node : nothing to do
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        //leaf node : nothing to do
    }
}
