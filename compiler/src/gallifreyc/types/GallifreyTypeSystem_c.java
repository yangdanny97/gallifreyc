package gallifreyc.types;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import gallifreyc.ast.nodes.LocalRef;
import gallifreyc.ast.nodes.LocalRef_c;
import gallifreyc.ast.nodes.RefQualification;
import polyglot.ext.jl7.types.JL7TypeSystem_c;
import polyglot.types.*;
import polyglot.util.InternalCompilerError;
import polyglot.util.Position;

public class GallifreyTypeSystem_c extends JL7TypeSystem_c implements GallifreyTypeSystem {
    private final String defaultRefQualification = "local";
    
    public RefQualifiedType refQualifiedTypeOf(Position pos, Type base, RefQualification q) {
        return new RefQualifiedType_c(this, pos, base, q);
    }
    
    @Override
    public boolean typeEquals(Type type1, Type type2) {
        if (type1 instanceof RefQualifiedType && type2 instanceof RefQualifiedType) {
            return type1.typeEqualsImpl(type2) || type2.typeEqualsImpl(type1);
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
        if (fromType instanceof RefQualifiedType) {
            RefQualifiedType refFromType = (RefQualifiedType) fromType;
            return (refFromType.refQualification() instanceof LocalRef)
                    && super.isImplicitCastValid(refFromType.base(), toType);
        } else {
            return super.isImplicitCastValid(fromType, toType);
        }
    }

    // TODO(chrisroman): I have a suspicion that a lot of other type system methods
    // must be overridden to handle cases when we have a RefQualifiedType, because it
    // is basically a wrapper around the actual type we'd like to do things with. 
    // Not exactly sure how to deal with this; seems like a lot of overhead and
    // potentially the wrong way to do it.
    
}
