package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.IMAProgram;
import org.apache.log4j.Logger;

import java.io.PrintStream;

/**
 * Method written directly in IMA assembly
 * @author gl01
 * @date 20/01/2020
 */
public class AsmMethodBody extends AbstractMethodBody{
    private static final Logger LOG = Logger.getLogger(AsmMethodBody.class);
    private final StringLiteral instructions;

    public AsmMethodBody(StringLiteral instructions) {
        this.instructions = instructions;
    }

    /**
     *
     * @param s Buffer to which the result will be written.
     */
    @Override
    public void decompile(IndentPrintStream s) {
        s.print(" ");
        s.print("asm(");
        instructions.decompile(s);
        s.print(");");
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        //leaf node : nothing to do
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        instructions.iter(f);
    }

    /**
     * Implements terminal MethodAsmBody of rule 3.15 in pass 3 in verification step
     * @param compiler contains env_types
     * @param envExp environment of class
     * @param envParams environment of parameters
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
        LOG.trace("verify AsmMethodBody : start");
        instructions.verifyExpr(compiler, null, currentClass.getClassDefinition());
        LOG.trace("verify AsmMethodBody : end");
    }

    /**
     * Writes the given assembly code as the method code.
     * @param compiler
     * @param className
     * @param methodName
     */
    @Override
    public void codeGenMethod(DecacCompiler compiler, AbstractIdentifier className, AbstractIdentifier methodName) {
        String instructions = this.instructions.getValue();
        String[] lines = instructions.split("\\n");
        for (String line : lines) {
            compiler.addLine(line);
        }
    }
}
