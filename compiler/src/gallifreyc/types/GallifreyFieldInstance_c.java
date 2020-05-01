package gallifreyc.types;

import gallifreyc.ast.RefQualification;
import polyglot.ext.jl5.types.JL5FieldInstance_c;
import polyglot.ext.jl5.types.JL5TypeSystem;
import polyglot.types.Flags;
import polyglot.types.ReferenceType;
import polyglot.types.Type;
import polyglot.util.Position;
import polyglot.util.SerialVersionUID;

public class GallifreyFieldInstance_c extends JL5FieldInstance_c implements GallifreyFieldInstance {
	private static final long serialVersionUID = SerialVersionUID.generate();
	
	public GallifreyType gallifreyType;
	
	public GallifreyFieldInstance_c(JL5TypeSystem ts, Position pos, ReferenceType container, Flags flags, Type type,
			String name, RefQualification q) {
		super(ts, pos, container, flags, type, name);
		this.gallifreyType = new GallifreyType(q);
	}
	
	public GallifreyFieldInstance gallifreyType(GallifreyType t) {
		gallifreyType = t;
		return this;
	};
	
	public GallifreyType gallifreyType() {
		return gallifreyType;
	};
}
