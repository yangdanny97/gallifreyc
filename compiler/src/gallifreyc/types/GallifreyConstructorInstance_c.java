package gallifreyc.types;

import java.util.List;

import gallifreyc.ast.MoveRef;
import gallifreyc.ast.RefQualification;
import polyglot.ext.jl5.types.JL5ConstructorInstance_c;
import polyglot.ext.jl5.types.TypeVariable;
import polyglot.types.ClassType;
import polyglot.types.Flags;
import polyglot.types.Type;
import polyglot.util.Position;
import polyglot.util.SerialVersionUID;

public class GallifreyConstructorInstance_c extends JL5ConstructorInstance_c implements GallifreyConstructorInstance {
	private static final long serialVersionUID = SerialVersionUID.generate();
	
	public GallifreyType gallifreyReturnType;
	public List<GallifreyType> gallifreyInputs;
	
	public GallifreyConstructorInstance_c(GallifreyTypeSystem ts, Position pos, ClassType container, Flags flags,
			List<? extends Type> argTypes, List<? extends Type> excTypes, List<? extends TypeVariable> typeParams, 
			List<RefQualification> in) {
		super(ts, pos, container, flags, argTypes, excTypes, typeParams);
		this.gallifreyReturnType = new GallifreyType(new MoveRef(Position.COMPILER_GENERATED));
		for (RefQualification i : in) {
			gallifreyInputs.add(new GallifreyType(i));
		}
	}

	public GallifreyType gallifreyReturnType() {
		return gallifreyReturnType;
	}
	
	public GallifreyConstructorInstance gallifreyReturnType(GallifreyType returnType) {
		gallifreyReturnType = returnType;
		return this;
	}
	
	public List<GallifreyType> gallifreyInputTypes() {
		return gallifreyInputs;
	}
	
	public GallifreyConstructorInstance gallifreyInputTypes(List<GallifreyType> in) {
		gallifreyInputs = in;
		return this;
	}
}