package gallifreyc.types;

import java.util.List;

import polyglot.ext.jl5.types.JL5MethodInstance;

public interface GallifreyMethodInstance extends JL5MethodInstance {
	public GallifreyType gallifreyReturnType();
	public GallifreyMethodInstance gallifreyReturnType(GallifreyType returnType);
	
	public List<GallifreyType> gallifreyInputTypes();
	public GallifreyMethodInstance gallifreyInputTypes(List<GallifreyType> in);
}
