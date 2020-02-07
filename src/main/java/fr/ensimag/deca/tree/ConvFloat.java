package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.FloatType;
import fr.ensimag.deca.context.Type;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.FLOAT;
import fr.ensimag.ima.pseudocode.instructions.WFLOAT;
import fr.ensimag.ima.pseudocode.instructions.WFLOATX;

import static fr.ensimag.ima.pseudocode.Register.R1;

/**
 * Conversion of an int into a float. Used for implicit conversions.
 * 
 * @author gl01
 * @date 01/01/2020
 */
public class ConvFloat extends AbstractUnaryExpr {
    public ConvFloat(AbstractExpr operand) {
        super(operand);
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
     * @return The type float, as this is a type conversion
     */
    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) {
        Type result = new FloatType(compiler.getSymbolTable().create("float"));
        setType(result);
        return result;
    }

    /**
     * Generates the code for converting an int number into float
     * @param compiler
     */
    @Override
    public void codeGenInst(DecacCompiler compiler){
        getOperand().codeGenInst(compiler);
        compiler.addInstruction(new FLOAT(Register.getR(compiler.getFirstRegisterNumber() - 1), Register.getR(compiler.getFirstRegisterNumber() - 1)));
    };

    @Override
    protected void codeGenPrint(DecacCompiler compiler, boolean printHex) {

        getOperand().codeGenInst(compiler);
        compiler.addInstruction(new FLOAT(Register.getR(compiler.getFirstRegisterNumber() - 1), R1));
        // We no longer need R1 : we free it
        compiler.addToFirstRegisterNumber(-1);

        if (printHex) {
            compiler.addInstruction(new WFLOATX());
        } else {
            compiler.addInstruction(new WFLOAT());
        }


    }


    @Override
    protected String getOperatorName() {
        return "/* conv float */";
    }

}
