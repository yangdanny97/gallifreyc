package gallifreyc.types;

import java.util.ArrayList;
import java.util.List;

import gallifreyc.ast.MoveRef;
import gallifreyc.ast.RefQualification;
import polyglot.ext.jl5.types.JL5ConstructorInstance_c;
import polyglot.ext.jl5.types.TypeVariable;
import polyglot.types.ClassType;
import polyglot.types.Flags;
import polyglot.types.ConstructorInstance;
import polyglot.types.Type;
import polyglot.util.Position;
import polyglot.util.SerialVersionUID;

public class GallifreyConstructorInstance_c extends JL5ConstructorInstance_c implements GallifreyConstructorInstance {
    private static final long serialVersionUID = SerialVersionUID.generate();

    public GallifreyType gallifreyReturnType;
    public List<GallifreyType> gallifreyInputs = new ArrayList<>();

    public GallifreyConstructorInstance_c(GallifreyTypeSystem ts, Position pos, ClassType container, Flags flags,
            List<? extends Type> argTypes, List<? extends Type> excTypes, List<? extends TypeVariable> typeParams,
            List<RefQualification> in) {
        super(ts, pos, container, flags, argTypes, excTypes, typeParams);
        this.gallifreyReturnType = new GallifreyType(new MoveRef(Position.COMPILER_GENERATED));
        in = ts.normalizeLocals(in);
        for (RefQualification i : in) {
            gallifreyInputs.add(new GallifreyType(i));
        }
    }

    @Override
    public GallifreyType gallifreyReturnType() {
        return gallifreyReturnType;
    }

    @Override
    public GallifreyConstructorInstance gallifreyReturnType(GallifreyType returnType) {
        gallifreyReturnType = returnType;
        return this;
    }

    @Override
    public List<GallifreyType> gallifreyInputTypes() {
        return gallifreyInputs;
    }

    @Override
    public GallifreyConstructorInstance gallifreyInputTypes(List<GallifreyType> in) {
        gallifreyInputs = in;
        return this;
    }

    @Override
    public boolean isSameConstructorImpl(ConstructorInstance mi) {
        if (!(mi instanceof GallifreyConstructorInstance)) {
            return false;
        }
        List<GallifreyType> gallifreyInputTypes = ((GallifreyConstructorInstance) mi).gallifreyInputTypes();
        if (gallifreyInputTypes.size() != this.gallifreyInputs.size()) {
            return false;
        }
        for (int i = 0; i < this.gallifreyInputs.size(); i++) {
            if (!this.gallifreyInputs.get(i).equals(gallifreyInputTypes.get(i))) {
                return false;
            }
        }
        return super.isSameConstructorImpl(mi);
    }

}
