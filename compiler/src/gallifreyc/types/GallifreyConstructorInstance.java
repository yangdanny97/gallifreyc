package gallifreyc.types;

import polyglot.ext.jl5.types.JL5ConstructorInstance;

public interface GallifreyConstructorInstance extends JL5ConstructorInstance, GallifreyProcedureInstance {
    public GallifreyConstructorInstance gallifreyReturnType(GallifreyType returnType);
}
