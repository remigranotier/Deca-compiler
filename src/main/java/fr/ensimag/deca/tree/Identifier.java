package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.context.ClassType;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.Definition;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.FieldDefinition;
import fr.ensimag.deca.context.MethodDefinition;
import fr.ensimag.deca.context.ExpDefinition;
import fr.ensimag.deca.context.VariableDefinition;
import fr.ensimag.deca.tools.DecacInternalError;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.deca.tools.SymbolTable.Symbol;
import java.io.PrintStream;

import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.*;
import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;

import javax.naming.Context;
import javax.naming.Name;

/**
 * Deca Identifier
 *
 * @author gl01
 * @date 01/01/2020
 */
public class Identifier extends AbstractIdentifier {
    private static final Logger LOG = Logger.getLogger(Identifier.class);

    @Override
    protected void checkDecoration() {
        if (getDefinition() == null) {
            throw new DecacInternalError("Identifier " + this.getName() + " has no attached Definition");
        }
    }

    @Override
    public Definition getDefinition() {
        return definition;
    }

    /**
     * Like {@link #getDefinition()}, but works only if the definition is a
     * ClassDefinition.
     * 
     * This method essentially performs a cast, but throws an explicit exception
     * when the cast fails.
     * 
     * @throws DecacInternalError
     *             if the definition is not a class definition.
     */
    @Override
    public ClassDefinition getClassDefinition() {
        try {
            return (ClassDefinition) definition;
        } catch (ClassCastException e) {
            throw new DecacInternalError(
                    "Identifier "
                            + getName()
                            + " is not a class identifier, you can't call getClassDefinition on it");
        }
    }

    /**
     * Like {@link #getDefinition()}, but works only if the definition is a
     * MethodDefinition.
     * 
     * This method essentially performs a cast, but throws an explicit exception
     * when the cast fails.
     * 
     * @throws DecacInternalError
     *             if the definition is not a method definition.
     */
    @Override
    public MethodDefinition getMethodDefinition() {
        try {
            return (MethodDefinition) definition;
        } catch (ClassCastException e) {
            throw new DecacInternalError(
                    "Identifier "
                            + getName()
                            + " is not a method identifier, you can't call getMethodDefinition on it");
        }
    }

    /**
     * Like {@link #getDefinition()}, but works only if the definition is a
     * FieldDefinition.
     * 
     * This method essentially performs a cast, but throws an explicit exception
     * when the cast fails.
     * 
     * @throws DecacInternalError
     *             if the definition is not a field definition.
     */
    @Override
    public FieldDefinition getFieldDefinition() {
        try {
            return (FieldDefinition) definition;
        } catch (ClassCastException e) {
            throw new DecacInternalError(
                    "Identifier "
                            + getName()
                            + " is not a field identifier, you can't call getFieldDefinition on it");
        }
    }

    /**
     * Like {@link #getDefinition()}, but works only if the definition is a
     * VariableDefinition.
     * 
     * This method essentially performs a cast, but throws an explicit exception
     * when the cast fails.
     * 
     * @throws DecacInternalError
     *             if the definition is not a field definition.
     */
    @Override
    public VariableDefinition getVariableDefinition() {
        try {
            return (VariableDefinition) definition;
        } catch (ClassCastException e) {
            throw new DecacInternalError(
                    "Identifier "
                            + getName()
                            + " is not a variable identifier, you can't call getVariableDefinition on it");
        }
    }

    /**
     * Like {@link #getDefinition()}, but works only if the definition is a ExpDefinition.
     * 
     * This method essentially performs a cast, but throws an explicit exception
     * when the cast fails.
     * 
     * @throws DecacInternalError
     *             if the definition is not a field definition.
     */
    @Override
    public ExpDefinition getExpDefinition() {
        try {
            return (ExpDefinition) definition;
        } catch (ClassCastException e) {
            throw new DecacInternalError(
                    "Identifier "
                            + getName()
                            + " is not a Exp identifier, you can't call getExpDefinition on it");
        }
    }

    @Override
    public void setDefinition(Definition definition) {
        this.definition = definition;
    }

    @Override
    public Symbol getName() {
        return name;
    }

    private Symbol name;

    public Identifier(Symbol name) {
        Validate.notNull(name);
        this.name = name;
    }

    /**
     *
     * @param compiler  (contains the "env_types" attribute)
     * @param localEnv
     *            Environment in which the expression should be checked
     *            (corresponds to the "env_exp" attribute)
     * @param currentClass
     *            Definition of the class containing the expression
     *            (corresponds to the "class" attribute)
     *             is null in the main bloc.
     * @return returns this expression's type, also decorating the tree with it
     * @throws ContextualError
     */
    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
        LOG.debug("verify exp Identifier "+ name +": start");
        if (localEnv.get(name) == null) {
            throw new ContextualError("Identifier \"" + this.name.getName() +
                     "\" is not defined in current context (0.1)", getLocation());
        }
        setDefinition(localEnv.get(name));
        Type result = localEnv.get(name).getType();
        setType(result);
        LOG.debug("verify exp Identifier: end");
        return result;
    }

    /**
     * Implements non-terminal "type" of [SyntaxeContextuelle] in the 3 passes
     * @param compiler contains "env_types" attribute
     */
    @Override
    public Type verifyType(DecacCompiler compiler) throws ContextualError {
        LOG.debug("verify type Identifier "+name+": start");
        if (compiler.getEnvTypes().get(name) == null) {
            throw new ContextualError("Identifier \"" + this.name.getName()
                    + "\" is not defined in current context (0.1)", getLocation());
        }
        setDefinition(compiler.getEnvTypes().get(name));
        Type result = compiler.getEnvTypes().get(name).getType();
        setType(result);
        LOG.debug("verify type Identifier: end");
        return result;
    }

    /**
     * implements non terminal Lvalue
     * @param compiler contains env_types
     * @param envExp environment of class
     * @param currentClass
     * @return this Lvalue's type
     * @throws ContextualError
     */
    public Type verifyLValue(DecacCompiler compiler, EnvironmentExp envExp, ClassDefinition currentClass) throws ContextualError {
        Type result = verifyExpr(compiler, envExp, currentClass);
        boolean isCompatible = getDefinition().isExpression();
        if (isCompatible) {
            return result;
        }
        throw new ContextualError("Lvalue cannot be a " + getDefinition().getNature() + " identifier. (3.64)", getLocation());
    }

    private Definition definition;


    @Override
    protected void iterChildren(TreeFunction f) {
        // leaf node => nothing to do
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        // leaf node => nothing to do
    }

    /**
     *
     * @param s Buffer to which the result will be written.
     */
    @Override
    public void decompile(IndentPrintStream s) {
        s.print(name.toString());
    }

    @Override
    String prettyPrintNode() {
        return "Identifier (" + getName() + ")";
    }

    @Override
    protected void prettyPrintType(PrintStream s, String prefix) {
        Definition d = getDefinition();
        if (d != null) {
            s.print(prefix);
            s.print("definition: ");
            s.print(d);
            s.println();
        }
    }

    @Override
    protected void codeGenInst(DecacCompiler compiler) {
        if (getDefinition().isField()) {
            //We have an implicit 'this' : we generate code like if it was written 'this.x' instead of 'x'
            LOG.debug("implicit 'this' in field selection");
            (new FieldSelection(new This(), this)).codeGenInstLastSelection(compiler);
        } else {
            int N = compiler.getFirstRegisterNumber();
            compiler.addInstruction(new LOAD(getExpDefinition().getOperand(), Register.getR(N)));
            compiler.addToFirstRegisterNumber(1);

            if (this.getType().isClassOrNull()) {
                compiler.getErrorManager().getNullDereferencement().genBranch();
            }
        }
    }

    @Override
    protected void codeGenPrint(DecacCompiler compiler) {
        compiler.addInstruction(new LOAD(getExpDefinition().getOperand(), Register.R1));
        compiler.addInstruction(new WINT());
    }


    @Override
    protected void codeGenPrint(DecacCompiler compiler, boolean printHex) {
        compiler.addInstruction(new LOAD(getExpDefinition().getOperand(), Register.R1));
        if (printHex) {
            compiler.addInstruction(new WFLOATX());
        } else {
            compiler.addInstruction(new WFLOAT());
        }
    }

    @Override
    protected void controlFlow(DecacCompiler compiler, boolean b, Label E) throws DecacInternalError {
        if (getDefinition().isField()){
            (new FieldSelection(new This(), this)).controlFlow(compiler, b, E);
        } else {

            compiler.addInstruction(new LOAD(getExpDefinition().getOperand(), Register.R0));
            compiler.addInstruction(new CMP(0, Register.R0));
            if (b) {
                compiler.addInstruction(new BNE(E));
            } else {
                compiler.addInstruction(new BEQ(E));
            }
        }
    }

    @Override
    protected boolean mustNotGenCode() {
        return true;
    }
}
