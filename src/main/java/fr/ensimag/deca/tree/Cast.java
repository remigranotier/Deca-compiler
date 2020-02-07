package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.*;
import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;

/**
 * Type casting operator
 * @author gl01
 * @date 24/01/2020
 */
public class Cast extends AbstractUnaryExpr {
    final private AbstractIdentifier type;
    private static final Logger LOG = Logger.getLogger(Cast.class);
    public Cast(AbstractIdentifier type, AbstractExpr operand) {
        super(operand);
        Validate.notNull(type);
        this.type = type;
    }

    /**
     *
     * @param s Buffer to which the result will be written.
     */
    @Override
    public void decompile(IndentPrintStream s) {
        s.print(getOperatorName());
        s.print("(");
        getOperand().decompile(s);
        s.print(")");
    }

    @Override
    protected String getOperatorName() {
        return "("+type.getName()+") ";
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
     * @return this expression's type, also decorating the tree with it
     * @throws ContextualError
     */
    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv, ClassDefinition currentClass) throws ContextualError {
        LOG.debug("verify cast to type "+type.getName()+" : start");
        Type type1 = type.verifyType(compiler);
        Type type2 = getOperand().verifyExpr(compiler, localEnv, currentClass);
        if (!compiler.getEnvTypes().castCompatible(type2, type1)) {
            throw new ContextualError("Cannot cast "+type2.getName()+" type to "+type1.getName()+" type (3.39)", getLocation());
        }
        setType(type1);
        LOG.debug("verify cast to type "+type.getName()+" : end");
        return type1;
    }


    /**
     * Generates the code to execute the cast instruction.
     * @param compiler
     */
    @Override
    protected void codeGenInst(DecacCompiler compiler) {
        LOG.debug("Started generating the code for a cast");

        int n = compiler.getFirstRegisterNumber();

        if (type.getType().sameType(getOperand().getType())) {
            //we are casting something to its own type : we have nothing to do.
            getOperand().codeGenInst(compiler);

        } else if (type.getType().isInt() && getOperand().getType().isFloat()) {
            // (int) floatNumber
            getOperand().codeGenInst(compiler); //we got the result in Rn
            compiler.addInstruction(new INT(Register.getR(n), Register.getR(n)));

        } else if (type.getType().isFloat() && getOperand().getType().isInt()) {
            // (float) intNumber
            getOperand().codeGenInst(compiler); //we got the result in Rn
            compiler.addInstruction(new FLOAT(Register.getR(n), Register.getR(n)));

        } else {
            //else : we have (class1) objectOfClass2 : we have to check is objectOfClass2 is an instanceof class1
            AbstractExpr object = getOperand();
            AbstractIdentifier classCasted = type;

            Label castSuccess = Label.getNewCastSuccess();
            (new InstanceOf(object, classCasted)).castControlFlow(compiler, true, castSuccess);

            genCastFailedError(compiler, classCasted.getType(), object.getType());

            compiler.addLabel(castSuccess);

            //getting back the result of the expression stored by castControlFlow in InstanceOf in Rn
            compiler.addInstruction(new POP(Register.getR(n)));
            compiler.addToFirstRegisterNumber(1);
            compiler.decrementCurrentNumberOfRegisterPushed();
        }
        compiler.addComment("Expression of type " + getOperand().getType() + " casted to type " + type.getType());

    }

    /**
     * Error to generate if the casts fails.
     * It is generated here directly so the error message can show a more precise description of the problem, instead
     * of a shared "cast failed" error.
     * @param compiler
     * @param castedType
     * @param objectType
     */
    private void genCastFailedError(DecacCompiler compiler, Type castedType, Type objectType) {
        compiler.addInstruction(new WSTR("ERROR : cannot cast " + objectType + " type object to " + castedType));
        compiler.addInstruction(new WNL());
        compiler.addInstruction(new ERROR());
    }


    @Override
    protected void codeGenPrint(DecacCompiler compiler) {
        int n = compiler.getFirstRegisterNumber();
        codeGenInst(compiler);
        compiler.addInstruction(new LOAD(Register.getR(n), Register.R1));
        compiler.addToFirstRegisterNumber(-1);
        compiler.addInstruction(new WINT());
    }

    @Override
    protected void codeGenPrint(DecacCompiler compiler, boolean printHex) {
        int n = compiler.getFirstRegisterNumber();
        codeGenInst(compiler);
        compiler.addInstruction(new LOAD(Register.getR(n), Register.R1));
        compiler.addToFirstRegisterNumber(-1);
        if (printHex) {
            compiler.addInstruction(new WFLOATX());
        } else {
            compiler.addInstruction(new WFLOAT());
        }
    }

}
