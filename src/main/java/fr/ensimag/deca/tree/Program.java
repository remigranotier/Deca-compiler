package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.RegisterOffset;
import fr.ensimag.ima.pseudocode.instructions.*;
import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;

import java.io.PrintStream;

/**
 * Deca complete program (class definition plus main block)
 *
 * @author gl01
 * @date 01/01/2020
 */
public class Program extends AbstractProgram {
    private static final Logger LOG = Logger.getLogger(Program.class);

    public Program(ListDeclClass classes, AbstractMain main) {
        Validate.notNull(classes);
        Validate.notNull(main);
        this.classes = classes;
        this.main = main;
    }
    public ListDeclClass getClasses() {
        return classes;
    }
    public AbstractMain getMain() {
        return main;
    }
    private ListDeclClass classes;
    private AbstractMain main;

    /**
     * Implements axiom Program
     * @param compiler
     * @throws ContextualError
     */
    @Override
    public void verifyProgram(DecacCompiler compiler) throws ContextualError {
        LOG.trace("verify program: start");

        // PASS 1
        classes.verifyListClass(compiler);

        // PASS 2
        classes.verifyListClassMembers(compiler);

        // PASS 3
        classes.verifyListClassBody(compiler);
        main.verifyMain(compiler);
        LOG.trace("verify program: end");
    }




    @Override
    public void codeGenProgram(DecacCompiler compiler) {
        String decompil = decompileIfDebug(compiler);
        if (!decompil.equals("")) {
            LOG.debug(decompil);
        }
        classes.genMethodTables(compiler); //passe 1


        //passe 2

        compiler.addComment("Main program");

        main.codeGenMain(compiler);
        compiler.addInstruction(new HALT());
        classes.codeGenListDeclClass(compiler); //construct initialization subprograms of classes
        genObjectEquals(compiler); //gen builtin Object.equals method code
        classes.genMethodCode(compiler); //construct method code

        compiler.getErrorManager().codeGenErrors(compiler);


    }

    /**
     * Generates the code of Object.equals
     * @param compiler
     */
    private void genObjectEquals(DecacCompiler compiler) {
        compiler.addLabel(new Label("Code.Object.equals"));
        //loading 'this' into R0
        compiler.addInstruction(new LOAD(new RegisterOffset(-2, Register.LB), Register.R0));
        //loading the 'other' object (only parameter of the method)
        compiler.addInstruction(new LOAD(new RegisterOffset(-3, Register.LB), Register.R1));
        //comparing addr
        compiler.addInstruction(new CMP(Register.R0, Register.R1));
        //putting the result in R0
        compiler.addInstruction(new SEQ(Register.R0));
        //end of the method
        compiler.addInstruction(new RTS());
    }



    @Override
    public void decompile(IndentPrintStream s) {
        getClasses().decompile(s);
        getMain().decompile(s);
    }
    
    @Override
    protected void iterChildren(TreeFunction f) {
        classes.iter(f);
        main.iter(f);
    }
    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        classes.prettyPrint(s, prefix, false);
        main.prettyPrint(s, prefix, true);
    }
}
