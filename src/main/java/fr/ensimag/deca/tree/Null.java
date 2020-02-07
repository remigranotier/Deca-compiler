package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.*;
import fr.ensimag.deca.tools.IndentPrintStream;
import org.apache.log4j.Logger;

import java.io.PrintStream;

/**
 * Null class keyword
 * @author gl01
 * @date 20/01/2020
 */
public class Null extends AbstractExpr{
    private static final Logger LOG = Logger.getLogger(Null.class);

    /**
     * Implements terminal Null of pass 3
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
        LOG.trace("verify null : start");
        Type result = new NullType(compiler.getSymbolTable().create("null"));
        setType(result);
        LOG.trace("verify null : end");
        return result;
    }

    @Override
    public void decompile(IndentPrintStream s) {
        s.print("null");
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        //Leaf node : nothing to do
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        // Leaf node : nothing to do
    }
}
