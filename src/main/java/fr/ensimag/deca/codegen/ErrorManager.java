package fr.ensimag.deca.codegen;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.Type;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.instructions.*;

import fr.ensimag.ima.pseudocode.instructions.ERROR;
import fr.ensimag.ima.pseudocode.instructions.WNL;
import fr.ensimag.ima.pseudocode.instructions.WSTR;
import org.apache.log4j.Logger;

import java.util.HashSet;

/**
 * Class to manage execution errors more easily.
 * @author gl01
 * @date 17/01/2020
 */
public class ErrorManager {
    private static final Logger LOG = Logger.getLogger(ErrorManager.class);

    private HashSet<Error> errors;
    private Error divZero;
    private Error moduloZero;
    private Error stackOverflow;
    private Error rintWithFloat;
    private Error rfloatWithInt;
    private Error arithOverflow;
    private Error nullDereferencement;

    private Error heapFull;
    private DecacCompiler compiler;

    public DecacCompiler getCompiler() {
        return compiler;
    }

    /**
     * Initializes all possible execution errors.
     * @param compiler
     */
    public ErrorManager(DecacCompiler compiler) {
            errors = new HashSet<Error>();

            Label divisionByZero = new Label("divisionByZero");
            divZero = new Error(this, divisionByZero, false, "ERROR : Can't divide by 0 !", false);
            errors.add(divZero);

            Label moduloByZero = new Label("moduloByZero");
            moduloZero = new Error(this, moduloByZero, false, "ERROR : Can't do a modulo by 0 !", false);
            errors.add(moduloZero);

            Label readIntWrongType = new Label("readIntWrongType");
            rintWithFloat = new Error(this, readIntWrongType, false, "ERROR : You have given a Float instead of an Integer, or there has been an overflow.", true);
            errors.add(rintWithFloat);

            Label readFloatWrongType = new Label("readFloatWrongType");
            rfloatWithInt = new Error(this, readFloatWrongType, false, "ERROR : You have given a Integer instead of an Float, or there has been an overflow.", true);
            errors.add(rfloatWithInt);

            Label stackOverflowLabel = new Label("stackOverflow");
            stackOverflow = new Error(this, stackOverflowLabel, false, "ERROR : stack overflow", true);
            errors.add(stackOverflow);

            Label arithOverflowLabel = new Label("arithOverflow");
            arithOverflow = new Error(this, arithOverflowLabel, false, "ERROR : You have used an arithmetic operation on Floats that triggered an overflow.", true);
            errors.add(arithOverflow);

            Label nullDereferencementLabel = new Label("nullDereferencement");
            nullDereferencement = new NullDeferencementError(this, nullDereferencementLabel, false, "ERROR : null dereferencement", true);
            errors.add(nullDereferencement);

            Label heapFullLabel = new Label("heapFull");
            heapFull = new Error(this, heapFullLabel, false, "ERROR : allocation failed because of full heap", false);
            errors.add(heapFull);

            this.compiler = compiler;

    }


    /**
     * Generates the error messages and labels to jump on when errors occurs in the program.
     * @param compiler
     */
    public void codeGenErrors(DecacCompiler compiler) {
            for (Error e : this.errors) {
                LOG.debug(e.isErrorPresent() + " ; " + e.getErrorLabel());
                if (e.isErrorPresent() && ( !e.isNoCheckInfluence() || (e.isNoCheckInfluence() && !compiler.getCompilerOptions().getNoCheck()) )) {
                    compiler.addLabel(e.getErrorLabel());
                    compiler.addInstruction(new WSTR(e.getErrorMessage()));
                    compiler.addInstruction(new WNL());
                    compiler.addInstruction(new ERROR());
                }
            }
        }


    /**
     * Generates the Stack Overflow Error.
     * It is handled separately because of its particular nature ; we have to get back some counters from compiler
     * to know how many words were stored on the stack.
     */
    public void genStackOverflowError() {
        if (!compiler.getCompilerOptions().getNoCheck()) {
            int maxRegisterStored = compiler.getMaxNumberOfRegisterPushed();

            int methodTableSize;
            if (!getCompiler().isInMethodMode()) {
                methodTableSize = compiler.getMethodTableSize();
            } else {
                methodTableSize = 0;
            }
            int nbVar = compiler.getNbVar();
            int stackUsed =  methodTableSize + maxRegisterStored + nbVar;
            LOG.debug(nbVar + " ; " + methodTableSize + " ; " + maxRegisterStored);

            if (stackUsed != 0) {
                stackOverflow.setErrorPresent(true);

                Label overflowLabel = new Label("stackOverflow");
                compiler.addFirstInstruction(new BOV(overflowLabel));
                compiler.addFirstInstruction(new TSTO(stackUsed), "Max nb of stored register : " + maxRegisterStored +
                        " , ADDSP : " + (nbVar + methodTableSize));
            } else {
                compiler.addFirstComment("Nothing stacked : can't have a stack overflow");
            }
        }
    }

    /**
     * Generates the error threw when we exit a method without a return instruction. Generated at each non-void method
     * to allow the display of a more explicit error message (with the method name).
     * @param methodName
     * @param returnType
     */
    public void genExitMethodWithoutReturn(String methodName, Type returnType) {
        if (returnType.getName().toString() != "void") {
            compiler.addInstruction(new WSTR("ERROR : exited method " + methodName + " with non-void return type" +
                    " without a return instruction."));
            compiler.addInstruction(new WNL());
            compiler.addInstruction(new ERROR());
        }
    }

    public Error getHeapFull() {
        return heapFull;
    }

    public Error getDivZero() {
        return this.divZero;
    }

    public Error getModuloZero() {
        return this.moduloZero;
    }

    public Error getRintWithFloat() {
        return this.rintWithFloat;
    }

    public Error getRfloatWithInt() {
        return this.rfloatWithInt;
    }

    public Error getArithOverflow() {
        return this.arithOverflow;
    }

    public Error getNullDereferencement() {
        return nullDereferencement;
    }

}
