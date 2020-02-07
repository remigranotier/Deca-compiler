package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.IndentPrintStream;

/**
 * List of method declarations
 * @author gl01
 * @date 20/01/2020
 */
public class ListDeclMethod extends TreeList<DeclMethod>{

    /**
     *
     * @param s Buffer to which the result will be written.
     */
    @Override
    public void decompile(IndentPrintStream s) {
        for (DeclMethod c : getList()) {
            c.decompile(s);
            s.println();
        }
    }

    /**
     * Implements non terminal list_decl_method of pass 2
     * @param compiler contains env_types
     * @param superclass
     * @param currentClass
     * @throws ContextualError
     */
    public void verifyListDeclMethodPass2(DecacCompiler compiler,
                                          AbstractIdentifier superclass,
                                          AbstractIdentifier currentClass) throws ContextualError {
        for (int i = 0; i < superclass.getClassDefinition().getNumberOfMethods(); i++) {
            currentClass.getClassDefinition().incNumberOfMethods();
        }

        for (DeclMethod decl : getList()) {
            boolean isRedefinition = decl.verifyDeclMethodPass2(compiler, superclass, currentClass);
            if (!isRedefinition){
                currentClass.getClassDefinition().incNumberOfMethods();
            }
        }
    }

    /**
     * Implements non terminal list_decl_method of pass 3
     * @param compiler contains env_types
     * @param envExp
     * @param currentClass
     * @throws ContextualError
     */
    public void verifyListDeclMethodPass3(DecacCompiler compiler,
                                          EnvironmentExp envExp,
                                          AbstractIdentifier currentClass) throws ContextualError {
        for (DeclMethod decl : getList()) {
            decl.verifyDeclMethodPass3(compiler, envExp, currentClass);
        }
    }
}
