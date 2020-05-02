package gallifreyc.types;

import polyglot.ext.jl5.types.JL5FieldInstance;

public interface GallifreyFieldInstance extends JL5FieldInstance {
    public GallifreyFieldInstance gallifreyType(GallifreyType t);

    public GallifreyType gallifreyType();
}
