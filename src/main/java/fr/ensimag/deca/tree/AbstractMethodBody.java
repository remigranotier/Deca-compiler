package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.Type;
import fr.ensimag.ima.pseudocode.AbstractLine;
import fr.ensimag.ima.pseudocode.IMAProgram;

/**
 * Can be either DecaMethodBody or AsmMethodBody
 * Used only for verifyMethodBody method
 * @author gl01
 * @date 20/01/2020
 */
public abstract class AbstractMethodBody extends AbstractSubProgram {
    protected abstract void verifyMethodBody(DecacCompiler compiler,
                                             EnvironmentExp envExp,
                                             EnvironmentExp envParams,
                                             AbstractIdentifier currentClass,
                                             Type returnType) throws ContextualError;


    /**
     * Generates the code for the method.
     * @param compiler
     * @param className
     * @param methodName
     */
    public abstract void codeGenMethod(DecacCompiler compiler, AbstractIdentifier className, AbstractIdentifier methodName);
}