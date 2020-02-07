package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.*;
import fr.ensimag.deca.DecacCompiler;

/**
 * Left-hand side value of an assignment.
 * 
 * @author gl01
 * @date 01/01/2020
 */
public abstract class AbstractLValue extends AbstractExpr {
    public abstract Type verifyLValue(DecacCompiler compiler, EnvironmentExp envExp,
                                      ClassDefinition currentClass) throws ContextualError;
}
