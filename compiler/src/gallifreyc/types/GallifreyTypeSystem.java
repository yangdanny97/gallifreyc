package gallifreyc.types;

import java.util.List;
import java.util.Set;

import gallifreyc.ast.RefQualification;
import gallifreyc.ast.RestrictionId;
import polyglot.ast.Expr;
import polyglot.ext.jl5.types.TypeVariable;
import polyglot.ext.jl7.types.JL7TypeSystem;
import polyglot.types.ClassType;
import polyglot.types.Flags;
import polyglot.types.ReferenceType;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.util.Position;

public interface GallifreyTypeSystem extends JL7TypeSystem {

    public void addRestrictionMapping(String restriction, String cls) throws SemanticException;

    public String getClassNameForRestriction(String restriction);

    public void addUnionRestriction(String union, Set<String> restrictions);

    public Set<String> getVariantRestrictions(String restriction);

    public boolean isUnionRestriction(String restriction);
    
    public void addAllowedMethod(String restriction, String method);
    
    public Set<String> getAllowedMethods(RestrictionId restriction);
    
    public Set<String> getAllowedMethods(String rName);
    
    public void addRestrictionClassType(String restriction, ClassType cls);
    
    public ClassType getRestrictionClassType(String restriction);
    
    public boolean canBeShared(String className);

    public GallifreyMethodInstance methodInstance(Position pos, ReferenceType container, Flags flags, Type returnType,
            String name, List<? extends Type> argTypes, List<? extends Type> excTypes, List<RefQualification> inputQ,
            RefQualification returnQ);

    public GallifreyMethodInstance methodInstance(Position pos, ReferenceType container, Flags flags, Type returnType,
            String name, List<? extends Type> argTypes, List<? extends Type> excTypes, List<TypeVariable> typeParams,
            List<RefQualification> inputQ, RefQualification returnQ);

    public GallifreyLocalInstance localInstance(Position pos, Flags flags, Type type, String name, RefQualification q);

    public GallifreyFieldInstance fieldInstance(Position pos, ReferenceType container, Flags flags, Type type,
            String name, RefQualification q);

    public GallifreyConstructorInstance constructorInstance(Position pos, ClassType container, Flags flags,
            List<? extends Type> argTypes, List<? extends Type> excTypes, List<TypeVariable> typeParams,
            List<RefQualification> inputQ);

    // check args of a function call, calculate the qualification of the returned
    // value
    public GallifreyType checkArgs(GallifreyProcedureInstance pi, List<Expr> args) throws SemanticException;

    // check qualifications as if we were doing an assignment of toType = fromType
    public boolean checkQualifications(GallifreyType fromType, GallifreyType toType);
    
    public List<RefQualification> normalizeLocals(List<RefQualification> qualifications);
}
