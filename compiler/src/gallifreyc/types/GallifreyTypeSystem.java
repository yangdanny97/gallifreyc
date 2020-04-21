package gallifreyc.types;

import java.util.List;
import java.util.Map;

import gallifreyc.ast.RefQualification;
import polyglot.ext.jl5.types.JL5ConstructorInstance;
import polyglot.ext.jl5.types.JL5FieldInstance;
import polyglot.ext.jl5.types.TypeVariable;
import polyglot.ext.jl7.types.JL7TypeSystem;
import polyglot.types.*;
import polyglot.util.Position;

public interface GallifreyTypeSystem extends JL7TypeSystem {
    RefQualifiedType refQualifiedTypeOf(Position pos, Type base, RefQualification q);
    
    public void addRestrictionMapping(String restriction, String cls);
    public String getClassNameForRestriction(String restriction);
    
    public void addUnionRestriction(String union, List<String> restrictions);
    public List<String> getVariantRestrictions(String restriction);
    public boolean isUnionRestriction(String restriction);
    
    public GallifreyConstructorInstance constructorInstance(Position pos, ClassType container, Flags flags,
			List<? extends Type> argTypes, List<? extends Type> excTypes, 
			List<TypeVariable> typeParams);
    
	public MethodInstance methodInstance(Position pos, ReferenceType container, Flags flags, Type returnType,
			String name, List<? extends Type> argTypes, List<? extends Type> excTypes, RefQualification q);
    public GallifreyMethodInstance methodInstance(Position pos, ReferenceType container, Flags flags, Type returnType,
			String name, List<? extends Type> argTypes, List<? extends Type> excTypes, 
			List<TypeVariable> typeParams, RefQualification q);
    
    public LocalInstance localInstance(Position pos, Flags flags, Type type, String name, RefQualification q);
    public GallifreyFieldInstance fieldInstance(Position pos, ReferenceType container, 
    		Flags flags, Type type, String name, RefQualification q);
}
