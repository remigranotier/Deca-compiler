package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.Signature;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.tools.IndentPrintStream;
import org.apache.log4j.Logger;

/**
 * List of parameter declarations
 * @author gl01
 * @date 20/01/2020
 */
public class ListDeclParam extends TreeList<DeclParam>{
    private static final Logger LOG = Logger.getLogger(ListDeclParam.class);

    /**
     *
     * @param s Buffer to which the result will be written.
     */
    @Override
    public void decompile(IndentPrintStream s) {
        boolean first = true;
        for (DeclParam c : getList()) {
            if (!first) {
                s.print(", ");
            }
            c.decompile(s);
            first = false;
        }
    }

    /**
     * Implements non terminal list_decl_param of pass 2
     * @param compiler contains env_types
     * @return Signature which came out of this verification
     * @throws ContextualError
     */
    protected Signature verifyListDeclParamPass2(DecacCompiler compiler) throws ContextualError {
        LOG.debug("verify listDeclParam pass 2: start");
        Signature newSig = new Signature();
        for (DeclParam param : getList()) {
            Type typeToAdd = param.verifyDeclParamPass2(compiler);
            newSig.add(typeToAdd);
        }
        LOG.debug("verify listDeclParam pass 2: end");
        return newSig;
    }

    /**
     * Implements non terminal list_decl_param of pass 3
     * @param compiler contains env_types
     * @param envExp class environment
     * @return environment of parameters
     * @throws ContextualError
     */
    protected EnvironmentExp verifyListDeclParamPass3(DecacCompiler compiler, EnvironmentExp envExp) throws ContextualError {
        LOG.debug("verify listDeclParam pass 3: start");
        EnvironmentExp envParams = new EnvironmentExp(envExp);
        for (DeclParam param : getList()) {
            param.verifyDeclParamPass3(compiler, envParams);
        }
        LOG.debug("verify listDeclParam pass 3: end");
        return envParams;
    }

    public void setOperands() {
        int index = 1;
        for (DeclParam param : getList()) {
            param.setOperand(index);
            index++;
        }
    }
}
