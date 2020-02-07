package fr.ensimag.deca.context;

import fr.ensimag.deca.tools.SymbolTable.Symbol;

import java.util.HashMap;

/**
 * Dictionary associating identifier's TypeDefinition to their names.
 *
 * @author gl01
 * @date 10/01/2020
 */
public class EnvironmentType {
    HashMap<Symbol, TypeDefinition> association = new HashMap<Symbol, TypeDefinition>();

    public static class DoubleDefException extends Exception {
        private static final long serialVersionUID = -2733379901827316441L;
    }

    /**
     * @param key The symbol to get
     * @return the definition of the symbol in the environment, or null if the
     * symbol is undefined.
     */
    public TypeDefinition get(Symbol key) {
        return association.getOrDefault(key, null);
    }

    /**
     * @param t1 Destination type
     * @param t2 Value type
     * @return A boolean describing if we can affect a t2 value to a t1 object
     * @throws ContextualError if there is a contextual error thrown during this function call
     */
    public boolean assignCompatible(Type t1, Type t2) throws ContextualError{
        if (t1.isFloat() && t2.isInt()) {
            return true;
        }
        return this.subType(t2, t1);
    }

    /**
     * @param t1 Potential child type
     * @param t2 Potential parent type
     * @return Boolean stating if t1 is a subtype of t2
     * @throws ContextualError if there is a contextual error thrown during this function call
     */
    public boolean subType(Type t1, Type t2) throws ContextualError{
        // If the types are the same
        if (t1.sameType(t2)) {
            return true;
        }
        // If one of them is null
        if(t1 == null || t2 == null) {
            return false;
        }
        // If the potential child type is object and the types are different
        if (t1.isClass() && t1.getName().toString().equals("Object")) {
            return false;
        }
        // If the types are not the same and not classes
        if (!t1.isClassOrNull() || !t2.isClassOrNull()) {
            return false;
        }
        // If the potential parent type is Object and the potential child a cladd
        if (t1.isClassOrNull() && t2.isClass() && t2.getName().toString().equals("Object")) {
            return true;
        }
        // If the potential child type is Null
        if (t1.isNull() && t2.isClassOrNull()) {
            return true;
        }

        ClassDefinition sup1 = t1.asClassType("Type " + t1 +
                " is not class", get(t1.getName()).getLocation()).getDefinition().getSuperClass().getClassDefinition();
        ClassDefinition sup2 = t2.asClassType("Type " + t2 +
                " is not class", get(t2.getName()).getLocation()).getDefinition();
        if (sup1.equals(sup2)) {
            return true;
        }
        if (subType(sup1.getType(), t2)) {
            return true;
        }
        return false;
    }

    /**
     * @param t1 Initial type
     * @param t2 Destination type
     * @return Boolean stating if a type t1 variable can be casted to type t2
     * @throws ContextualError if such an error is thrown during this function call
     */
    public boolean castCompatible(Type t1, Type t2) throws ContextualError {
        return !t1.isVoid() && ( assignCompatible(t1, t2) || assignCompatible(t2, t1) );
    }

    /**
     * Add the definition def associated to the symbol name in the environment.
     *
     * Adding a symbol which is already defined in the environment,
     * - throws DoubleDefException if the symbol is in the "current" dictionary
     * - or, hides the previous declaration otherwise.
     *
     * @param name
     *            Name of the symbol to define
     * @param def
     *            Definition of the symbol
     * @throws DoubleDefException
     *             if the symbol is already defined at the "current" dictionary
     *
     */
    public void declare(Symbol name, TypeDefinition def) throws DoubleDefException {
        if (association.containsKey(name)) {
            throw new DoubleDefException();
        }
        association.put(name, def);
    }
}
