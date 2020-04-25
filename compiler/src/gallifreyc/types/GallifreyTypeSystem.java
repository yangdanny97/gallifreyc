package gallifreyc.types;

import java.util.List;

import gallifreyc.ast.RefQualification;
import polyglot.ext.jl5.types.JL5ConstructorInstance;
import polyglot.ext.jl5.types.JL5FieldInstance;
import polyglot.ext.jl5.types.TypeVariable;
import polyglot.ext.jl7.types.JL7TypeSystem;
import polyglot.types.ClassType;
import polyglot.types.Flags;
import polyglot.types.LocalInstance;
import polyglot.types.MethodInstance;
import polyglot.types.ReferenceType;
import polyglot.types.Type;
import polyglot.util.Position;

public interface GallifreyTypeSystem extends JL7TypeSystem {
    
    public void addRestrictionMapping(String restriction, String cls);
    public String getClassNameForRestriction(String restriction);
    
    public void addUnionRestriction(String union, List<String> restrictions);
    public List<String> getVariantRestrictions(String restriction);
    public boolean isUnionRestriction(String restriction);
    
	public GallifreyMethodInstance methodInstance(Position pos, ReferenceType container, Flags flags, Type returnType,
			String name, List<? extends Type> argTypes, List<? extends Type> excTypes, RefQualification q);
    public GallifreyMethodInstance methodInstance(Position pos, ReferenceType container, Flags flags, Type returnType,
			String name, List<? extends Type> argTypes, List<? extends Type> excTypes, 
			List<TypeVariable> typeParams, RefQualification q);
    
    public GallifreyLocalInstance localInstance(Position pos, Flags flags, Type type, String name, RefQualification q);
    public GallifreyFieldInstance fieldInstance(Position pos, ReferenceType container, 
    		Flags flags, Type type, String name, RefQualification q);
}
