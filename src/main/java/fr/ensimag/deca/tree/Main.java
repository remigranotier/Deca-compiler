package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.context.VoidType;
import fr.ensimag.deca.tools.IndentPrintStream;
import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;

import java.io.PrintStream;

/**
 * Main part of a program (after classes declarations)
 * @author gl01
 * @date 01/01/2020
 */
public class Main extends AbstractMain {
    private static final Logger LOG = Logger.getLogger(Main.class);
    
    private ListDeclVar declVariables;
    private ListInst insts;
    public Main(ListDeclVar declVariables,
            ListInst insts) {
        Validate.notNull(declVariables);
        Validate.notNull(insts);
        this.declVariables = declVariables;
        this.insts = insts;
    }

    /**
     * Implements non terminal main in pass 3
     * @param compiler
     * @throws ContextualError
     */
    @Override
    protected void verifyMain(DecacCompiler compiler) throws ContextualError {
        LOG.debug("verify Main: start");
        EnvironmentExp environmentExp = new EnvironmentExp(null);
        declVariables.verifyListDeclVariable(compiler, environmentExp, null);
        Type voidType = new VoidType(compiler.getSymbolTable().create("void"));
        insts.verifyListInst(compiler, environmentExp, null, voidType);
        LOG.debug("verify Main: end");
    }

    @Override
    protected void codeGenMain(DecacCompiler compiler) {

        compiler.addComment("Global variables initialization");
        declVariables.codeGenListDeclVar(compiler);
        compiler.genInitialSPOffset();

        compiler.addComment("Beginning of main instructions:");
        insts.codeGenListInst(compiler);

        compiler.getErrorManager().genStackOverflowError();
    }

    /**
     *
     * @param s Buffer to which the result will be written.
     */
    @Override
    public void decompile(IndentPrintStream s) {
        s.println("{");
        s.indent();
        declVariables.decompile(s);
        insts.decompile(s);
        s.unindent();
        s.println("}");
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        declVariables.iter(f);
        insts.iter(f);
    }
 
    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        declVariables.prettyPrint(s, prefix, false);
        insts.prettyPrint(s, prefix, true);
    }
}
