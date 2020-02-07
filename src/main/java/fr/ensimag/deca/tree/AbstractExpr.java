package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.tools.DecacInternalError;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.*;
import org.apache.commons.lang.Validate;

import java.io.PrintStream;

/**
 * Expression, i.e. anything that has a value.
 *
 * @author gl01
 * @date 01/01/2020
 */
public abstract class AbstractExpr extends AbstractInst {
    /**
     * @return true if the expression does not correspond to any concrete token
     * in the source code (and should be decompiled to the empty string).
     */
    boolean isImplicit() {
        return false;
    }

    /**
     * Get the type decoration associated to this expression (i.e. the type computed by contextual verification).
     */
    public Type getType() {
        return type;
    }

    protected void setType(Type type) {
        Validate.notNull(type);
        this.type = type;
    }
    private Type type;

    @Override
    protected void checkDecoration() {
        if (getType() == null) {
            throw new DecacInternalError("Expression " + decompile() + " has no Type decoration");
        }
    }


    /**
     * Method returning whether or not the expr is an IntLiteral ( ~ is an immediate int)
     * @return if the object is a int literal
     */
    protected boolean isIntLiteral() {
        return false;
    }
    protected boolean isFloatLiteral() {
        return false;
    }
    protected boolean isImmediate() {return false; }
    protected boolean isBooleanLiteral() {
        return false;
    }
    protected boolean isFieldSelection() { return false; }

    /**
     * Method to construct the control flow of a boolean expression. Has to be Overwritten in boolean subclasses.
     * Throws : DecacInternalError : This method should only be called on boolean expressions on which it has been
     * overwritten : else it throws this error.
     */
    protected void controlFlow(DecacCompiler compiler, boolean b, Label E) throws DecacInternalError {
        throw new DecacInternalError("Error : trying to compile a non-boolean expression as a boolean one.");
    }

    /**
     * Verify the expression for contextual error.
     * 
     * implements non-terminals "expr" and "lvalue" 
     *    of [SyntaxeContextuelle] in pass 3
     *
     * @param compiler  (contains the "env_types" attribute)
     * @param localEnv
     *            Environment in which the expression should be checked
     *            (corresponds to the "env_exp" attribute)
     * @param currentClass
     *            Definition of the class containing the expression
     *            (corresponds to the "class" attribute)
     *             is null in the main bloc.
     * @return the Type of the expression
     *            (corresponds to the "type" attribute)
     */
    public abstract Type verifyExpr(DecacCompiler compiler,
            EnvironmentExp localEnv, ClassDefinition currentClass)
            throws ContextualError;

    /**
     * Verify the expression in right hand-side of (implicit) assignments 
     * 
     * implements non-terminal "rvalue" of [SyntaxeContextuelle] in pass 3
     *
     * @param compiler  contains the "env_types" attribute
     * @param localEnv corresponds to the "env_exp" attribute
     * @param currentClass corresponds to the "class" attribute
     * @param expectedType corresponds to the "type1" attribute            
     * @return this with an additional ConvFloat if needed...
     */
    public AbstractExpr verifyRValue(DecacCompiler compiler,
            EnvironmentExp localEnv, ClassDefinition currentClass, 
            Type expectedType)
            throws ContextualError {
        Type exprType = verifyExpr(compiler, localEnv, currentClass);
        if (!compiler.getEnvTypes().assignCompatible(expectedType, exprType)) {
            throw new ContextualError("Type " + exprType + " cannot be assigned to type " + expectedType + "(3.28)", this.getLocation());
        }
        if (expectedType.isFloat() && exprType.isInt()) {
            ConvFloat conv = new ConvFloat(this);
            conv.verifyExpr(compiler, localEnv, currentClass);
            return conv;
        }
        return this;
    }

    /**
     * Implements non terminal "expr" of rule 3.20 in pass 3
     *
     * @param compiler contains the "env_types" attribute
     * @param localEnv corresponds to the "env_exp" attribute
     * @param currentClass
     *          corresponds to the "class" attribute (null in the main bloc).
     * @param returnType Useless parameter at this tree's level : cf rule (3.20)
     * @throws ContextualError
     */
    @Override
    protected void verifyInst(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass, Type returnType)
            throws ContextualError {
        verifyExpr(compiler, localEnv, currentClass);
    }

    /**
     * Verify the expression as a condition, i.e. check that the type is
     * boolean.
     *
     * @param localEnv
     *            Environment in which the condition should be checked.
     * @param currentClass
     *            Definition of the class containing the expression, or null in
     *            the main program.
     */
    void verifyCondition(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
        Type typeObtained = verifyExpr(compiler, localEnv, currentClass);
        if (!typeObtained.isBoolean()) {
            throw new ContextualError("Condition is not boolean (3.29)", getLocation());
        }
    }

    /**
     * Generate code to print the expression
     *
     * @param compiler
     */
    protected void codeGenPrint(DecacCompiler compiler) {
        throw new UnsupportedOperationException("not yet implemented");

    }

    /**
     * Generate code to print the expression with hex display, for floats.
     *
     * @param compiler
     * @param printHex
     */
    protected void codeGenPrint(DecacCompiler compiler, boolean printHex) {
        throw new UnsupportedOperationException("not yet implemented");

    }


    /**
     * Generates the code for an expression.
     * The computation of boolean operators is factorized here because of the common code between Not, and And/Or that
     * are UnaryExpr and OpBool.
     * @param compiler
     */
    @Override
    protected void codeGenInst(DecacCompiler compiler) {
        if (getType().isBoolean()) {
            compiler.addComment("Beginning of the computation of a boolean expression");
            int n = compiler.getFirstRegisterNumber();
            Label endOfAnd = Label.getNewEndOfBoolExpr();
            Label and2ndBlock = Label.getNewControlFlow();

            controlFlow(compiler, true, and2ndBlock);

            compiler.addComment("Code to be executed if the control flow do NOT jump on the 2nd block");
            compiler.addInstruction(new LOAD(0, Register.getR(n)));
            compiler.addInstruction(new BRA(endOfAnd));

            compiler.addComment("Code to be executed if the control flow DO jump on the 2nd block");
            compiler.addLabel(and2ndBlock);
            compiler.addInstruction(new LOAD(1, Register.getR(n)));

            compiler.addLabel(endOfAnd);
            //result stocked in rn : one more register is used
            compiler.addToFirstRegisterNumber(1);
            compiler.addComment("End of the computation of a boolean expression");
        } else {
            throw new UnsupportedOperationException("not yet implemented");
        }
    }

    @Override
    protected void decompileInst(IndentPrintStream s) {
        decompile(s);
        s.print(";");
    }

    @Override
    protected void prettyPrintType(PrintStream s, String prefix) {
        Type t = getType();
        if (t != null) {
            s.print(prefix);
            s.print("type: ");
            s.print(t);
            s.println();
        }
    }
}
