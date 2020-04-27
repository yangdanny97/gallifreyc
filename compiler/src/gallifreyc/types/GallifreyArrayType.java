package gallifreyc.types;

import polyglot.ext.jl5.types.JL5ArrayType_c;
import polyglot.types.ArrayType;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.util.Position;
import polyglot.util.SerialVersionUID;

public class GallifreyArrayType extends JL5ArrayType_c implements ArrayType {
	private static final long serialVersionUID = SerialVersionUID.generate();

	public GallifreyArrayType(TypeSystem ts, Position pos, Type base, boolean isVarargs) {
		super(ts, pos, base, isVarargs);
	}
	
	// invariant subtyping - modified from ArrayType_c
	
    @Override
    public boolean isSubtypeImpl(Type t) {
    	return ts.typeEquals(this, t);
    }
    
    @Override
    public boolean isImplicitCastValidImpl(Type toType) {
        if (toType.isArray()) {
            return ts.typeEquals(base(), toType.toArray().base());
        }

        return ts.isSubtype(this, toType);
    }
    
    @Override
    public boolean isCastValidImpl(Type toType) {
        if (!toType.isReference()) return false;

        if (toType.isArray()) {
            Type fromBase = base();
            Type toBase = toType.toArray().base();

            if (fromBase.isPrimitive()) return ts.typeEquals(toBase, fromBase);
            if (toBase.isPrimitive()) return false;

            if (fromBase.isNull()) return false;
            if (toBase.isNull()) return false;

            return ts.typeEquals(fromBase, toBase);
        }

        return ts.isSubtype(this, toType);
    }

}
