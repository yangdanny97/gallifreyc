package gallifreyc.types;

import polyglot.ext.jl5.types.JL5LocalInstance;

public interface GallifreyLocalInstance extends JL5LocalInstance {
    public GallifreyLocalInstance gallifreyType(GallifreyType t);

    public GallifreyType gallifreyType();
}
