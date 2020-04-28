package gallifreyc.types;

import java.util.List;

import polyglot.ext.jl5.types.JL5ConstructorInstance;

public interface GallifreyConstructorInstance extends JL5ConstructorInstance {
	public GallifreyType gallifreyReturnType();
	public GallifreyConstructorInstance gallifreyReturnType(GallifreyType returnType);
	
	public List<GallifreyType> gallifreyInputTypes();
	public GallifreyConstructorInstance gallifreyInputTypes(List<GallifreyType> in);
}
