package gallifreyc.types;

import java.util.HashMap;
import java.util.Map;

import gallifreyc.ast.RefQualification;
import polyglot.ext.jl7.types.JL7TypeSystem_c;
import polyglot.types.*;
import polyglot.util.Position;

public class GallifreyTypeSystem_c extends JL7TypeSystem_c implements GallifreyTypeSystem {
    // TODO: implement new methods in GallifreyTypeSystem.
    Map<Type, RefQualifiedType> refQualifiedTypeCache = new HashMap<>();
    public RefQualifiedType refQualifiedTypeOf(Position pos, Type base, RefQualification q) {
        assert_(base);
        return refQualifiedType(pos, base, q);
    }
    
    protected RefQualifiedType refQualifiedType(Position pos, Type base, RefQualification q) {
        RefQualifiedType t = refQualifiedTypeCache.get(base);
        if (t == null) {
            t = createRefQualifiedType(pos, base, q);
            refQualifiedTypeCache.put(base, t);
        }
        return t;

    }
    
    protected RefQualifiedType createRefQualifiedType(Position pos, Type base, RefQualification q) {
        return new RefQualifiedType_c(this, pos, base, q);
    }
    
//    public RefQualifiedType refQualifiedTypeOf(Position pos, Type base, RefQualification q) {
////        assert_(base);
////        return refQualifiedType(pos, base, q);
//        return new RefQualifiedType_c(this, pos, base, q);
//    }
    

    // TODO: override methods as needed from TypeSystem_c.
}
