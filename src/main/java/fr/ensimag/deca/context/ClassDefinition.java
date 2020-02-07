package fr.ensimag.deca.context;

import fr.ensimag.deca.tree.AbstractIdentifier;
import fr.ensimag.deca.tree.Location;
import fr.ensimag.ima.pseudocode.LabelOperand;
import fr.ensimag.ima.pseudocode.RegisterOffset;
import org.apache.commons.lang.Validate;

import java.util.HashMap;

/**
 * Definition of a class.
 *
 * @author gl01
 * @date 01/01/2020
 */
public class ClassDefinition extends TypeDefinition {

    // Getters and setters
    public RegisterOffset getMethodTableAddr() {
        return methodTableAddr;
    }

    public void setMethodTableAddr(RegisterOffset methodTableAddr) {
        this.methodTableAddr = methodTableAddr;
    }

    private RegisterOffset methodTableAddr;

    public void setLabelTable(HashMap<Integer, LabelOperand> labelTable) {
        this.labelTable = labelTable;
    }

    private HashMap<Integer, LabelOperand> labelTable;

    public HashMap<Integer, LabelOperand> getLabelTable() {
        return labelTable;
    }

    public void setNumberOfFields(int numberOfFields) {
        this.numberOfFields = numberOfFields;
    }

    public int getNumberOfFields() {
        return numberOfFields;
    }

    /**
     * Increments numberOfFields field
     */
    public void incNumberOfFields() {
        this.numberOfFields++;
    }

    public int getNumberOfMethods() {
        return numberOfMethods;
    }

    public void setNumberOfMethods(int n) {
        Validate.isTrue(n >= 0);
        numberOfMethods = n;
    }

    /**
     * Increments numberOfMethods field
     * @return the incremented field
     */
    public int incNumberOfMethods() {
        numberOfMethods++;
        return numberOfMethods;
    }

    private int numberOfFields = 0;
    private int numberOfMethods = 0;
    
    @Override
    public boolean isClass() {
        return true;
    }
    
    @Override
    public ClassType getType() {
        // Cast succeeds by construction because the type has been correctly set
        // in the constructor.
        return (ClassType) super.getType();
    };

    public AbstractIdentifier getSuperClass() {
        return superClass;
    }

    private final EnvironmentExp members;
    private final AbstractIdentifier superClass;

    public EnvironmentExp getMembers() {
        return members;
    }

    public ClassDefinition(ClassType type, Location location, AbstractIdentifier superClass) {
        super(type, location);
        EnvironmentExp parent;
        if (superClass != null) {
            parent = superClass.getClassDefinition().getMembers();
        } else {
            parent = null;
        }
        members = new EnvironmentExp(parent);
        this.superClass = superClass;
    }
    
}
