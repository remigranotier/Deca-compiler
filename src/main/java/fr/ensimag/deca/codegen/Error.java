package fr.ensimag.deca.codegen;

import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.instructions.BOV;

/**
 * Error class, representing an execution error.
 * @author gl01
 * @date 17/01/2020
 */
public class Error {
    private Label errorLabel;
    private boolean errorPresent;
    private String errorMessage;
    private ErrorManager errorManager;
    private boolean noCheckInfluence;

    public Error(ErrorManager errorManager, Label errorLabel, boolean errorPresent, String errorMessage, boolean noCheckInfluence) {
        this.errorManager = errorManager;
        this.errorLabel = errorLabel;
        this.errorPresent = errorPresent;
        this.errorMessage = errorMessage;
        this.noCheckInfluence = noCheckInfluence; /* this boolean indicates whether NoCheck has an influence on the Error ;
        if true, it does, and the Error is not dealt with when NoCheck is activated.  */
    }

    public ErrorManager getErrorManager() {
        return errorManager;
    }


    public void setErrorPresent(boolean errorPresent) {
        this.errorPresent = errorPresent;
    }

    public Label getErrorLabel() {
        return errorLabel;
    }

    public boolean isErrorPresent() {
        return errorPresent;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public boolean isNoCheckInfluence() {
        return noCheckInfluence;
    }

    /**
     * Generates the code that jumps on the error message, if it is needed depending on if the error is present,
     * and if the -n option is used
     */
    public void genBranch() {
        if (!noCheckInfluence || !errorManager.getCompiler().getCompilerOptions().getNoCheck()) {
            errorManager.getCompiler().addInstruction(new BOV(this.errorLabel));
            errorPresent = true;
        }
    }


}
