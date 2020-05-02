package gallifreyc.types;

import gallifreyc.ast.RefQualification;
import polyglot.ext.jl5.types.JL5LocalInstance_c;
import polyglot.types.Flags;
import polyglot.types.Type;
import polyglot.util.Position;
import polyglot.util.SerialVersionUID;

public class GallifreyLocalInstance_c extends JL5LocalInstance_c implements GallifreyLocalInstance {
    private static final long serialVersionUID = SerialVersionUID.generate();

    public GallifreyType gallifreyType;

    public GallifreyLocalInstance_c(GallifreyTypeSystem ts, Position pos, Flags flags, Type type, String name,
            RefQualification q) {
        super(ts, pos, flags, type, name);
        this.gallifreyType = new GallifreyType(q);
        // Note: sometimes q is null, it gets filled in later
    }

    public GallifreyLocalInstance gallifreyType(GallifreyType t) {
        gallifreyType = t;
        return this;
    };

    public GallifreyType gallifreyType() {
        return gallifreyType;
    };
}
