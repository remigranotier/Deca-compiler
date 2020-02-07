package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.*;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.tools.IndentPrintStream;
import java.io.PrintStream;

import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.RegisterOffset;
import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;

/**
 * Declaration of a global variable in main
 * @author gl01
 * @date 01/01/2020
 */
public class DeclVar extends AbstractDeclVar {
    private static final Logger LOG = Logger.getLogger(DeclVar.class);
    
    final private AbstractIdentifier type;
    final private AbstractIdentifier varName;
    final private AbstractInitialization initialization;

    public DeclVar(AbstractIdentifier type, AbstractIdentifier varName, AbstractInitialization initialization) {
        Validate.notNull(type);
        Validate.notNull(varName);
        Validate.notNull(initialization);
        this.type = type;
        this.varName = varName;
        this.initialization = initialization;
    }

    /**
     * Implements non_terminal decl_var of pass 3
     * @param compiler contains "env_types" attribute
     * @param localEnv
     *   its "parentEnvironment" corresponds to the "env_exp_sup" attribute
     *   in precondition, its "current" dictionary corresponds to
     *      the "env_exp" attribute
     *   in postcondition, its "current" dictionary corresponds to
     *      the synthetized attribute
     * @param currentClass
     * @throws ContextualError
     */
    @Override
    protected void verifyDeclVar(DecacCompiler compiler,
            EnvironmentExp localEnv, ClassDefinition currentClass)
            throws ContextualError {
        LOG.trace("verify DeclVar: start");

        Type thisType = type.verifyType(compiler);
        if (thisType.isVoid()) {
            throw new ContextualError("Variable " +
                    varName.getName() + " cannot be declared as void type (3.17)", getLocation());
        }

        initialization.verifyInitialization(compiler, thisType, localEnv, currentClass);

        try {
            ExpDefinition newDef = new VariableDefinition(thisType, getLocation());
            varName.setDefinition(newDef);
            varName.setType(thisType);
            localEnv.declare(varName.getName(), newDef);
        } catch (EnvironmentExp.DoubleDefException e) {
            throw new ContextualError("Variable " +
                    varName.getName() + " is already declared in current context (3.17)", getLocation());
        }

        LOG.trace("verify DeclVar: end");
    }

    /**
     *
     * @param s Buffer to which the result will be written.
     */
    @Override
    public void decompile(IndentPrintStream s) {
        type.decompile(s);
        s.print(' ');
        varName.decompile(s);
        initialization.decompile(s);
        s.print(";");
        s.println();
    }

    @Override
    protected
    void iterChildren(TreeFunction f) {
        type.iter(f);
        varName.iter(f);
        initialization.iter(f);
    }
    
    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        type.prettyPrint(s, prefix, false);
        varName.prettyPrint(s, prefix, false);
        initialization.prettyPrint(s, prefix, true);
    }

    @Override
    protected void codeGenDeclVar(DecacCompiler compiler, int index, Register register) {
        VariableDefinition definition = varName.getVariableDefinition();
        definition.setOperand(new RegisterOffset(index, register));
        initialization.codeGenInit(compiler, varName);
    }
}
