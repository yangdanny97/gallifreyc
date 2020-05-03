package gallifreyc.types;

import polyglot.ext.jl5.types.JL5MethodInstance;

public interface GallifreyMethodInstance extends JL5MethodInstance, GallifreyProcedureInstance {
    public GallifreyMethodInstance gallifreyReturnType(GallifreyType returnType);
}
