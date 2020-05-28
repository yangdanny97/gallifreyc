package gallifreyc.types;

import java.util.Collections;

import gallifreyc.ast.MoveRef;
import polyglot.ext.jl5.types.JL5ArrayType_c;
import polyglot.types.ArrayType;
import polyglot.types.FieldInstance;
import polyglot.types.MethodInstance;
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
        if (!toType.isReference())
            return false;

        if (toType.isArray()) {
            Type fromBase = base();
            Type toBase = toType.toArray().base();

            if (fromBase.isPrimitive())
                return ts.typeEquals(toBase, fromBase);
            if (toBase.isPrimitive())
                return false;

            if (fromBase.isNull())
                return false;
            if (toBase.isNull())
                return false;

            return ts.typeEquals(fromBase, toBase);
        }

        return ts.isSubtype(this, toType);
    }

    @Override
    protected MethodInstance createCloneMethodInstance() {
        GallifreyTypeSystem ts = (GallifreyTypeSystem) this.ts;
        // TODO
        return ts.methodInstance(position(), this, ts.Public(), this, // clone returns this type
                "clone", Collections.<Type>emptyList(), Collections.<Type>emptyList());
    }

    @Override
    protected FieldInstance createLengthFieldInstance() {
        GallifreyTypeSystem ts = (GallifreyTypeSystem) this.ts;
        FieldInstance fi = ts.fieldInstance(position(), this, ts.Public().Final(), ts.Int(), "length",
                new MoveRef(Position.COMPILER_GENERATED));
        fi.setNotConstant();
        return fi;
    }

}
