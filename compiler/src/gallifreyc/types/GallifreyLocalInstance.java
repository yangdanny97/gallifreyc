package gallifreyc.types;

import gallifreyc.ast.RefQualification;
import polyglot.ext.jl5.types.JL5LocalInstance;

public interface GallifreyLocalInstance extends JL5LocalInstance {
	public GallifreyLocalInstance gallifreyType(GallifreyType t);
	public GallifreyType gallifreyType();
}
