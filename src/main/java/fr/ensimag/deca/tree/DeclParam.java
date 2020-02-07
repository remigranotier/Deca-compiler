package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.*;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.RegisterOffset;
import org.apache.log4j.Logger;

import java.io.PrintStream;

/**
 * Declaration of a parameter within a method's signature
 * @author gl01
 * @date 20/01/2020
 */
public class DeclParam extends Tree{
    private final AbstractIdentifier type;
    private final AbstractIdentifier paramName;
    private static final Logger LOG = Logger.getLogger(DeclParam.class);
    public DeclParam(AbstractIdentifier type, AbstractIdentifier paramName) {
        this.type = type;
        this.paramName = paramName;
    }

    /**
     *
     * @param s Buffer to which the result will be written.
     */
    @Override
    public void decompile(IndentPrintStream s) {
        type.decompile(s);
        s.print(" ");
        paramName.decompile(s);
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        type.prettyPrint(s, prefix, false);
        paramName.prettyPrint(s, prefix, true);
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        type.iter(f);
        paramName.iter(f);
    }

    /**
     * Implements non terminal decl_param of pass 2
     * @param compiler
     * @return type of parameter verified
     * @throws ContextualError
     */
    protected Type verifyDeclParamPass2(DecacCompiler compiler) throws ContextualError {
        LOG.debug("verify decl param " + paramName.getName() + " pass 2 : start");
        Type typeObtained = type.verifyType(compiler);
        if (typeObtained.isVoid()) {
            throw new ContextualError("Parameter " + paramName.getName() + " cannot be void type (2.9)", getLocation());
        }
        LOG.debug("verify decl param " + paramName.getName() + " pass 2 : end");
        paramName.setType(typeObtained);
        return typeObtained;
    }

    /**
     * Implements non terminal decl_param of pass 3
     * @param compiler contains env_types
     * @param envParams
     * @throws ContextualError
     */
    protected void verifyDeclParamPass3(DecacCompiler compiler, EnvironmentExp envParams) throws ContextualError {
        LOG.debug("verify decl param "+paramName.getName()+" pass 3: start");
        Type typeObtained = type.verifyType(compiler);
        ExpDefinition paramDef = new ParamDefinition(typeObtained, getLocation());
        paramName.setDefinition(paramDef);
        try {
            envParams.declare(paramName.getName(), paramDef);
            LOG.debug("verify decl param "+paramName.getName()+" pass 3: end");
        } catch (EnvironmentExp.DoubleDefException e) {
            throw new ContextualError("Param " + paramName.getName() + " is already defined" +
                    " in current parameter environment (3.13)", getLocation());
        }
    }

    public void setOperand(int index) {
        RegisterOffset paramAddr = new RegisterOffset(-(2 + index) ,Register.LB);
        ((ParamDefinition) paramName.getDefinition()).setOperand(paramAddr);
    }
}
