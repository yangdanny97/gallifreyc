package gallifreyc.types;

import java.util.*;
import gallifreyc.ast.*;
import polyglot.ast.Expr;
import polyglot.ast.Formal;
import polyglot.ext.jl5.types.*;
import polyglot.ext.jl7.types.*;
import polyglot.types.*;
import polyglot.util.*;

public class GallifreyTypeSystem_c extends JL7TypeSystem_c implements GallifreyTypeSystem {
	public Map<String, String> restrictionMap;
	public Map<String, List<String>> restrictionUnionMap;
	
    public GallifreyTypeSystem_c() {
		super();
		restrictionMap = new HashMap<>();
		restrictionUnionMap = new HashMap<>();
	}
    
    //METHOD INSTANCE

	@Override
	public MethodInstance methodInstance(Position pos, ReferenceType container, Flags flags, Type returnType,
			String name, List<? extends Type> argTypes, List<? extends Type> excTypes) {
		return methodInstance(pos, container, flags, returnType, name, argTypes, excTypes, Collections.<TypeVariable> emptyList(), null);
	}

	@Override
	public GallifreyMethodInstance methodInstance(Position pos, ReferenceType container, Flags flags, Type returnType,
			String name, List<? extends Type> argTypes, List<? extends Type> excTypes, 
			List<TypeVariable> typeParams) {
		return methodInstance(pos, container, flags, returnType, name, argTypes, excTypes, typeParams, null);
	}
    
	@Override
	public GallifreyMethodInstance methodInstance(Position pos, ReferenceType container, Flags flags, Type returnType,
			String name, List<? extends Type> argTypes, List<? extends Type> excTypes, RefQualification returnQ) {
		return methodInstance(pos, container, flags, returnType, name, argTypes, excTypes, Collections.<TypeVariable> emptyList(), returnQ);
	}

	@Override
	public GallifreyMethodInstance methodInstance(Position pos, ReferenceType container, Flags flags, Type returnType,
			String name, List<? extends Type> argTypes, List<? extends Type> excTypes, 
			List<TypeVariable> typeParams, RefQualification returnQ) {
		return new GallifreyMethodInstance_c(this, pos, container, flags, returnType, name, argTypes, excTypes, typeParams, returnQ);
	}
	
	//CONSTRUCTOR INSTANCE

	@Override
	public GallifreyConstructorInstance constructorInstance(Position pos, ClassType container, Flags flags,
			List<? extends Type> argTypes, List<? extends Type> excTypes) {
		return constructorInstance(pos, container, flags, argTypes, excTypes, Collections.<TypeVariable> emptyList());
	}
	
    @Override
    public GallifreyConstructorInstance constructorInstance(Position pos,
            ClassType container, Flags flags, List<? extends Type> argTypes,
            List<? extends Type> excTypes, List<TypeVariable> typeParams) {
    	return new GallifreyConstructorInstance_c(this, pos, container, flags, argTypes, excTypes, typeParams);
    }
	
	//LOCAL INSTANCE
	
	@Override
	public GallifreyLocalInstance localInstance(Position pos, Flags flags, Type type, String name) {
		//null qualification for now, fill in later
		return new GallifreyLocalInstance_c(this, pos, flags, type, name, null);
	}

	@Override
	public GallifreyLocalInstance localInstance(Position pos, Flags flags, Type type, String name, RefQualification q) {
		return new GallifreyLocalInstance_c(this, pos, flags, type, name, q);
	}
	
	//FIELD INSTANCE
	
	@Override
	public GallifreyFieldInstance fieldInstance(Position pos, ReferenceType container, 
			Flags flags, Type type, String name) {
		return new GallifreyFieldInstance_c(this, pos, container, flags, type, name, null);
	}

	@Override
	public GallifreyFieldInstance fieldInstance(Position pos, ReferenceType container, 
			Flags flags, Type type, String name, RefQualification q) {
		return new GallifreyFieldInstance_c(this, pos, container, flags, type, name, q);
	}
	
	//RESTRICTIONS

	@Override
	public void addRestrictionMapping(String restriction, String cls) {
		restrictionMap.put(restriction, cls);
	}
	
	@Override
	public String getClassNameForRestriction(String restriction) {
		if (!restrictionMap.containsKey(restriction)) {
			return null;
		}
		return restrictionMap.get(restriction);
	}
	
	@Override
	public void addUnionRestriction(String union, List<String> restrictions) {
		restrictionUnionMap.put(union, restrictions);
	}
	
	@Override
	public List<String> getVariantRestrictions(String restriction) {
		if (!restrictionUnionMap.containsKey(restriction)) {
			return null;
		}
		return restrictionUnionMap.get(restriction);
	}
	
	@Override
	public boolean isUnionRestriction(String restriction) {
		return restrictionUnionMap.containsKey(restriction);
	}
	
	//check args and get return qualification for some function call
	
	
	
	public GallifreyType checkArgs(List<Formal> params, List<Expr> args) {
		//TODO
		return null;
	}
	
	// Casting

	@Override
	public boolean isSubtype(Type t1, Type t2) {
		// TODO Auto-generated method stub
		return super.isSubtype(t1, t2);
	}

	@Override
	public boolean isImplicitCastValid(Type fromType, Type toType) {
		// TODO Auto-generated method stub
		return super.isImplicitCastValid(fromType, toType);
	}

	@Override
	public LinkedList<Type> isImplicitCastValidChain(Type fromType, Type toType) {
		// TODO Auto-generated method stub
		return super.isImplicitCastValidChain(fromType, toType);
	}

	@Override
	public boolean isCastValid(Type fromType, Type toType) {
		// TODO Auto-generated method stub
		return super.isCastValid(fromType, toType);
	}

	@Override
	protected boolean isCastValidFromClass(ClassType fromType, Type toType) {
		// TODO Auto-generated method stub
		return super.isCastValidFromClass(fromType, toType);
	}

	@Override
	protected boolean isCastValidFromInterface(ClassType fromType, Type toType) {
		// TODO Auto-generated method stub
		return super.isCastValidFromInterface(fromType, toType);
	}

	@Override
	protected boolean isCastValidFromArray(ArrayType arrayType, Type toType) {
		// TODO Auto-generated method stub
		return super.isCastValidFromArray(arrayType, toType);
	}
}
