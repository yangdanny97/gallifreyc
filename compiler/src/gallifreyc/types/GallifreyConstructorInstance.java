package gallifreyc.types;

import java.util.List;

import polyglot.ext.jl5.types.JL5ConstructorInstance;

public interface GallifreyConstructorInstance extends JL5ConstructorInstance, GallifreyProcedureInstance {
    public GallifreyConstructorInstance gallifreyReturnType(GallifreyType returnType);
    
    @Override
    public GallifreyConstructorInstance gallifreyInputTypes(List<GallifreyType> in);
}
