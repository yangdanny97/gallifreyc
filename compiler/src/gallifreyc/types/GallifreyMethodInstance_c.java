package gallifreyc.types;

import java.util.ArrayList;
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
	public List<GallifreyType> gallifreyInputs;

	public GallifreyMethodInstance_c(GallifreyTypeSystem ts, Position pos, ReferenceType container, Flags flags,
			Type returnType, String name, List<? extends Type> argTypes, List<? extends Type> excTypes,
			List<? extends TypeVariable> typeParams, List<RefQualification> in, RefQualification out) {
		super(ts, pos, container, flags, returnType, name, argTypes, excTypes, typeParams);
		this.gallifreyInputs = new ArrayList<>();
		for (RefQualification i : in) {
			gallifreyInputs.add(new GallifreyType(i));
		}
		this.gallifreyReturnType = new GallifreyType(out);
	}
	
	public GallifreyType gallifreyReturnType() {
		return gallifreyReturnType;
	}
	
	public GallifreyMethodInstance gallifreyReturnType(GallifreyType returnType) {
		gallifreyReturnType = returnType;
		return this;
	}
	
	public List<GallifreyType> gallifreyInputTypes() {
		return gallifreyInputs;
	}
	
	public GallifreyMethodInstance gallifreyInputTypes(List<GallifreyType> in) {
		gallifreyInputs = in;
		return this;
	}

}
