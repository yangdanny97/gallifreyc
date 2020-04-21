package gallifreyc.types;

import java.util.List;

import gallifreyc.ast.RefQualification;
import polyglot.ext.jl5.types.JL5MethodInstance_c;
import polyglot.ext.jl5.types.JL5TypeSystem;
import polyglot.ext.jl5.types.TypeVariable;
import polyglot.types.Flags;
import polyglot.types.ReferenceType;
import polyglot.types.Type;
import polyglot.util.Position;
import polyglot.util.SerialVersionUID;

public class GallifreyMethodInstance_c extends JL5MethodInstance_c implements GallifreyMethodInstance {
	private static final long serialVersionUID = SerialVersionUID.generate();
	
	public GallifreyType gallifreyReturnType;

	public GallifreyMethodInstance_c(GallifreyTypeSystem ts, Position pos, ReferenceType container, Flags flags,
			Type returnType, String name, List<? extends Type> argTypes, List<? extends Type> excTypes,
			List<? extends TypeVariable> typeParams, RefQualification q) {
		super(ts, pos, container, flags, returnType, name, argTypes, excTypes, typeParams);
		this.gallifreyReturnType = new GallifreyType(q);
	}

}
