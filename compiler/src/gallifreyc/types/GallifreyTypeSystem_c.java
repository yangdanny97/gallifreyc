package gallifreyc.types;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import gallifreyc.ast.LocalRef;
import gallifreyc.ast.LocalRef_c;
import gallifreyc.ast.RefQualification;
import polyglot.ext.jl7.types.JL7TypeSystem_c;
import polyglot.types.*;
import polyglot.util.InternalCompilerError;
import polyglot.util.Position;

public class GallifreyTypeSystem_c extends JL7TypeSystem_c implements GallifreyTypeSystem {
    private final String defaultRefQualification = "unique";
    // TODO: implement new methods in GallifreyTypeSystem.
//    Map<Type, RefQualifiedType> refQualifiedTypeCache = new HashMap<>();
//    public RefQualifiedType refQualifiedTypeOf(Position pos, Type base, RefQualification q) {
//        assert_(base);
//        return refQualifiedType(pos, base, q);
//    }
//    
//    protected RefQualifiedType refQualifiedType(Position pos, Type base, RefQualification q) {
//        RefQualifiedType t = refQualifiedTypeCache.get(base);
//        if (t == null) {
//            t = createRefQualifiedType(pos, base, q);
//            refQualifiedTypeCache.put(base, t);
//        }
//        return t;
//
//    }
//    
//    protected RefQualifiedType createRefQualifiedType(Position pos, Type base, RefQualification q) {
//        return new RefQualifiedType_c(this, pos, base, q);
//    }
    
    public RefQualifiedType refQualifiedTypeOf(Position pos, Type base, RefQualification q) {
//        assert_(base);
//        return refQualifiedType(pos, base, q);
        return new RefQualifiedType_c(this, pos, base, q);
    }
    

    // Override methods dealing with types in java.lang.* (like java.lang.String)
    // to be unique 
//    @Override
//    public String wrapperTypeString(PrimitiveType t) {
//        assert_(t);
//
//        if (t.kind() == PrimitiveType.BOOLEAN) {
//            return defaultRefQualification + " java.lang.Boolean";
//        }
//        if (t.kind() == PrimitiveType.CHAR) {
//            return defaultRefQualification + " java.lang.Character";
//        }
//        if (t.kind() == PrimitiveType.BYTE) {
//            return defaultRefQualification + " java.lang.Byte";
//        }
//        if (t.kind() == PrimitiveType.SHORT) {
//            return defaultRefQualification + " java.lang.Short";
//        }
//        if (t.kind() == PrimitiveType.INT) {
//            return defaultRefQualification + " java.lang.Integer";
//        }
//        if (t.kind() == PrimitiveType.LONG) {
//            return defaultRefQualification + " java.lang.Long";
//        }
//        if (t.kind() == PrimitiveType.FLOAT) {
//            return defaultRefQualification + " java.lang.Float";
//        }
//        if (t.kind() == PrimitiveType.DOUBLE) {
//            return defaultRefQualification + " java.lang.Double";
//        }
//        if (t.kind() == PrimitiveType.VOID) {
//            return defaultRefQualification + " java.lang.Void";
//        }
//
//        throw new InternalCompilerError("Unrecognized primitive type.");
//    }

//    @Override
//    public Type typeForName(String name) throws SemanticException {
//        Type t = super.typeForName(name);
//        return new RefQualifiedType_c(this, Position.COMPILER_GENERATED, t, new LocalRef_c(Position.COMPILER_GENERATED));
//    }
    
    @Override
    public boolean typeEquals(Type type1, Type type2) {
        if (type2 instanceof RefQualifiedType) {
            return type1.typeEqualsImpl(type2) || type2.typeEqualsImpl(type1);
        } else {
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
