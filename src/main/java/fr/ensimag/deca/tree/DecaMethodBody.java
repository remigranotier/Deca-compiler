package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.IMAProgram;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.*;
import org.apache.log4j.Logger;

import java.io.PrintStream;

/**
 * Method written in deca, not in assembly
 * @author gl01
 * @date 20/01/2020
 */
public class DecaMethodBody extends AbstractMethodBody{
    private final ListDeclVar listDeclVar;
    private final ListInst listInst;
    private static final Logger LOG = Logger.getLogger(DecaMethodBody.class);
    public DecaMethodBody(ListDeclVar listDeclVar, ListInst listInst) {
        this.listDeclVar = listDeclVar;
        this.listInst = listInst;
    }

    /**
     *
     * @param s Buffer to which the result will be written.
     */
    @Override
    public void decompile(IndentPrintStream s) {
        s.println(" {");
        s.indent();
        listDeclVar.decompile(s);
        listInst.decompile(s);
        s.unindent();
        s.print("}");
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        listDeclVar.prettyPrint(s, prefix, false);
        listInst.prettyPrint(s, prefix, true);
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        listDeclVar.iter(f);
        listInst.iter(f);
    }

    /**
     *
     * @param compiler contains env_types
     * @param envExp class enviroment
     * @param envParams parameters environment
     * @param currentClass
     * @param returnType
     * @throws ContextualError
     */
    @Override
    protected void verifyMethodBody(DecacCompiler compiler,
                                    EnvironmentExp envExp,
                                    EnvironmentExp envParams,
                                    AbstractIdentifier currentClass,
                                    Type returnType) throws ContextualError {
        LOG.debug("verify DecaMethodBody : start");
        listDeclVar.verifyListDeclVariable(compiler, envParams, currentClass.getClassDefinition());
        listInst.verifyListInst(compiler, envParams, currentClass.getClassDefinition(), returnType);
        LOG.debug("verify DecaMethodBody : end");
    }

    /**
     * Generates the code for this method body, written in deca.
     * @param compiler
     * @param className
     * @param methodName
     */
    public void codeGenMethod(DecacCompiler compiler, AbstractIdentifier className, AbstractIdentifier methodName) {
        //label of the end of the method call ; generated now because return inst will need it
        Label endOfMethod = new Label("fin." + className.getName() + "." + methodName.getName());
        compiler.setEndOfCurrentMethod(endOfMethod);

        listDeclVar.codeGenListDeclVarMethod(compiler);

        listInst.codeGenListInst(compiler);

        compiler.getErrorManager().genExitMethodWithoutReturn(className.getName().toString() + "." +
                methodName.getName().toString(), methodName.getType());

        compiler.addLabel(endOfMethod);

        //generating the right ADDSP/SUBSP offset due to nb of local vars
        genSPOffset(compiler);

        saveRegisters(compiler);

        compiler.addInstruction(new RTS());

        compiler.getErrorManager().genStackOverflowError();
    }

}
