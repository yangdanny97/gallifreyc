package gallifreyc.types;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import gallifreyc.ast.*;
import polyglot.ext.jl7.types.JL7TypeSystem_c;
import polyglot.types.*;
import polyglot.util.InternalCompilerError;
import polyglot.util.Position;

public class GallifreyTypeSystem_c extends JL7TypeSystem_c implements GallifreyTypeSystem {
	public Map<String, String> restrictionMap;
	
    public GallifreyTypeSystem_c() {
		super();
		restrictionMap = new HashMap<>();
	}

	public RefQualifiedType refQualifiedTypeOf(Position pos, Type base, RefQualification q) {
        return new RefQualifiedType_c(this, pos, base, q);
    }
    
    @Override
    public boolean typeEquals(Type type1, Type type2) {
        if (type1 instanceof RefQualifiedType && type2 instanceof RefQualifiedType) {
            return type1.typeEqualsImpl(type2) && type2.typeEqualsImpl(type1);
        } else if (type1 instanceof RefQualifiedType || type2 instanceof RefQualifiedType) {
        	return false;
        }
        else {
            return super.typeEquals(type1, type2);
        }
    }

    // Override JL5 Type system things
    @Override
    public boolean isImplicitCastValid(Type fromType, Type toType) {
        if (fromType instanceof RefQualifiedType && toType instanceof RefQualifiedType) {
            RefQualifiedType refFromType = (RefQualifiedType) fromType;
            RefQualifiedType refToType = (RefQualifiedType) toType;
            // move -> shared/unique
            if (refFromType.refQualification() instanceof MoveRef) {
            	return super.isImplicitCastValid(refFromType.base(), refToType.base());
            }
            return typeEquals(fromType, toType);
        }
        if (fromType instanceof RefQualifiedType) {
            RefQualifiedType refFromType = (RefQualifiedType) fromType;
            // move -> local
            if (refFromType.refQualification() instanceof MoveRef) {
            	return super.isImplicitCastValid(refFromType.base(), toType);
            }
            return typeEquals(fromType, toType);
        }
        //TODO make consts treated like Moves
        return true; //super.isImplicitCastValid(fromType, toType);
    }

	@Override
	public void addRestrictionMapping(String restriction, String cls) {
		restrictionMap.put(restriction, cls);
	}
	
	@Override
	public String classNameForRestriction(String restriction) {
		if (!restrictionMap.containsKey(restriction)) {
			return null;
		}
		return restrictionMap.get(restriction);
	}
    
}
