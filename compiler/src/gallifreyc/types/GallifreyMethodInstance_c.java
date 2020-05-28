package gallifreyc.types;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import gallifreyc.ast.RefQualification;
import polyglot.ext.jl5.types.JL5MethodInstance_c;
import polyglot.ext.jl5.types.TypeVariable;
import polyglot.types.Flags;
import polyglot.types.MethodInstance;
import polyglot.types.ReferenceType;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.util.Position;
import polyglot.util.SerialVersionUID;

public class GallifreyMethodInstance_c extends JL5MethodInstance_c implements GallifreyMethodInstance {
    private static final long serialVersionUID = SerialVersionUID.generate();

    public GallifreyType gallifreyReturnType;
    public List<GallifreyType> gallifreyInputs = new ArrayList<>();

    public GallifreyMethodInstance_c(GallifreyTypeSystem ts, Position pos, ReferenceType container, Flags flags,
            Type returnType, String name, List<? extends Type> argTypes, List<? extends Type> excTypes,
            List<? extends TypeVariable> typeParams, List<RefQualification> in, RefQualification out) {
        super(ts, pos, container, flags, returnType, name, argTypes, excTypes, typeParams);
        this.gallifreyInputs = new ArrayList<>();
        in = ts.normalizeLocals(in);
        for (RefQualification i : in) {
            gallifreyInputs.add(new GallifreyType(i));
        }
        this.gallifreyReturnType = new GallifreyType(out);
    }

    @Override
    public GallifreyType gallifreyReturnType() {
        return gallifreyReturnType;
    }

    @Override
    public GallifreyMethodInstance gallifreyReturnType(GallifreyType returnType) {
        gallifreyReturnType = returnType;
        return this;
    }

    @Override
    public List<GallifreyType> gallifreyInputTypes() {
        return gallifreyInputs;
    }

    @Override
    public GallifreyMethodInstance gallifreyInputTypes(List<GallifreyType> in) {
        gallifreyInputs = in;
        return this;
    }

    @Override
    public boolean isSameMethodImpl(MethodInstance mi) {
        if (!(mi instanceof GallifreyMethodInstance)) {
            return false;
        }
        List<GallifreyType> gallifreyInputTypes = ((GallifreyMethodInstance) mi).gallifreyInputTypes();
        if (gallifreyInputTypes.size() != this.gallifreyInputs.size()) {
            return false;
        }
        for (int i = 0; i < this.gallifreyInputs.size(); i++) {
            if (!this.gallifreyInputs.get(i).equals(gallifreyInputTypes.get(i))) {
                return false;
            }
        }
        return super.isSameMethodImpl(mi);
    }

    @Override
    public boolean canOverrideImpl(MethodInstance mj_, boolean quiet) throws SemanticException {
        GallifreyMethodInstance g = (GallifreyMethodInstance) mj_;
        if (!g.gallifreyInputTypes().equals(gallifreyInputs)) {
            return false;
        }
        return super.canOverrideImpl(mj_, quiet);
    }

    @Override
    protected List<MethodInstance> implementedImplAux(ReferenceType rt) {
        if (rt == null) {
            return Collections.<MethodInstance>emptyList();
        }

        List<MethodInstance> l = new LinkedList<>();
        List<MethodInstance> methods = new ArrayList<>();
        for (MethodInstance mi : rt.methods(name, formalTypes)) {
            GallifreyMethodInstance g = (GallifreyMethodInstance) mi;
            if (g.gallifreyInputTypes().equals(gallifreyInputs)) {
                methods.add(mi);
            }
        }
        l.addAll(methods);

        Type superType = rt.superType();
        if (superType != null) {
            l.addAll(implementedImplAux(superType.toReference()));
        }

        List<? extends ReferenceType> ints = rt.interfaces();
        for (ReferenceType rt2 : ints) {
            l.addAll(implementedImplAux(rt2));
        }

        return l;
    }
}
