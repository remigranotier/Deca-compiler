package fr.ensimag.deca.context;

import fr.ensimag.deca.tools.SymbolTable.Symbol;
import fr.ensimag.deca.tree.AbstractIdentifier;
import fr.ensimag.deca.tree.Location;

/**
 * Type defined by a class.
 *
 * @author gl01
 * @date 01/01/2020
 */
public class ClassType extends Type {
    
    protected ClassDefinition definition;
    
    public ClassDefinition getDefinition() {
        return this.definition;
    }
            
    @Override
    public ClassType asClassType(String errorMessage, Location l) {
        return this;
    }

    @Override
    public boolean isClass() {
        return true;
    }

    @Override
    public boolean isClassOrNull() {
        return true;
    }

    /**
     * Standard creation of a type class. Also creates its ClassDefinition.
     */
    public ClassType(Symbol className, Location location, AbstractIdentifier superClass) {
        super(className);
        this.definition = new ClassDefinition(this, location, superClass);
    }

    /**
     * Creates a type representing a class className.
     * (To be used by subclasses only)
     */
    protected ClassType(Symbol className) {
        super(className);
    }
    

    @Override
    public boolean sameType(Type otherType) {
        return otherType.isClass() && (this.getName() == otherType.getName());
    }

    /**
     * Return true if potentialSuperClass is a superclass of this class.
     */
    public boolean isSubClassOf(ClassType potentialSuperClass) {
        ClassDefinition current = this.definition.getSuperClass().getClassDefinition();
        while (current != null) {
            if (current.getType().sameType(potentialSuperClass)) {
                return true;
            }
            current = current.getSuperClass().getClassDefinition();
        }
        return false;
    }


}
