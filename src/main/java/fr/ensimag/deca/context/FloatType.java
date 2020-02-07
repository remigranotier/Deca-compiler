package fr.ensimag.deca.context;

import fr.ensimag.deca.tools.SymbolTable;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.ImmediateFloat;

/**
 * Type of float expressions
 * @author Ensimag
 * @date 01/01/2020
 */
public class FloatType extends Type {

    public FloatType(SymbolTable.Symbol name) {
        super(name);
    }

    @Override
    public boolean isFloat() {
        return true;
    }

    @Override
    public boolean sameType(Type otherType) {
        return otherType.isFloat();
    }


}
