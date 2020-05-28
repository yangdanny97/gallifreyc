package gallifreyc.types;

import java.util.List;

import polyglot.ext.jl5.types.JL5MethodInstance;

public interface GallifreyMethodInstance extends JL5MethodInstance, GallifreyProcedureInstance {
    public GallifreyMethodInstance gallifreyReturnType(GallifreyType returnType);

    @Override
    public GallifreyMethodInstance gallifreyInputTypes(List<GallifreyType> in);
}
