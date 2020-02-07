package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.tools.IndentPrintStream;
import org.apache.log4j.Logger;

import java.io.PrintStream;

/**
 * Empty main Deca program
 *
 * @author gl01
 * @date 01/01/2020
 */
public class EmptyMain extends AbstractMain {
    private static final Logger LOG = Logger.getLogger(EmptyMain.class);

    @Override
    protected void verifyMain(DecacCompiler compiler) throws ContextualError {
        LOG.trace("verify EmptyMain: start");
        // nothing to do
        LOG.trace("verify EmptyMain: end");
    }

    @Override
    protected void codeGenMain(DecacCompiler compiler) {
        // nothing to do
    }

    /**
     * Contains no real information : nothing to check.
     */
    @Override
    protected void checkLocation() {
        // nothing
    }
    
    @Override
    public void decompile(IndentPrintStream s) {
        // no main program => nothing
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
