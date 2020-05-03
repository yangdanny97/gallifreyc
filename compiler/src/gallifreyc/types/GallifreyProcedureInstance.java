package gallifreyc.types;

import java.util.List;

import polyglot.ext.jl5.types.JL5ProcedureInstance;

public interface GallifreyProcedureInstance extends JL5ProcedureInstance {
    public List<GallifreyType> gallifreyInputTypes();

    public GallifreyProcedureInstance gallifreyInputTypes(List<GallifreyType> in);
    
    public GallifreyType gallifreyReturnType();
}
