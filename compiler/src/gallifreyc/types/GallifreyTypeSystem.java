package gallifreyc.types;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import gallifreyc.ast.MergeDecl;
import gallifreyc.ast.RefQualification;
import gallifreyc.ast.RestrictionId;
import polyglot.ast.Expr;
import polyglot.ast.MethodDecl;
import polyglot.ext.jl5.types.TypeVariable;
import polyglot.ext.jl7.types.JL7TypeSystem;
import polyglot.types.ClassType;
import polyglot.types.Flags;
import polyglot.types.ReferenceType;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.util.Position;

public interface GallifreyTypeSystem extends JL7TypeSystem, HeapContext<Region_c, RegionFunctionType_c> {

    // restriction name -> class name

    void addRestrictionMapping(String restriction, String cls) throws SemanticException;

    String getClassNameForRestriction(String restriction);

    // RVs

    void addRV(String union, List<String> restrictions);

    List<String> getRestrictionsForRV(String rv);

    boolean isRV(String restriction);

    Set<String> getRVsForRestriction(String restriction);

    // restriction name -> allowed methods

    void addAllowedMethod(String restriction, String method);

    Set<String> getAllowedMethods(RestrictionId restriction);

    Set<String> getAllowedMethods(String name);

    // allowed test methods (includes allowed methods)

    void addAllowedTestMethod(String restriction, String method);

    Set<String> getAllowedTestMethods(RestrictionId restriction);

    Set<String> getAllowedTestMethods(String name);

    boolean restrictionExists(String name);

    // restriction name -> class type

    void addRestrictionClassType(String restriction, ClassType cls);

    ClassType getRestrictionClassType(String restriction);

    // merge decls

    public void addMergeDecl(String restriction, MergeDecl md);

    public Set<MergeDecl> getMergeDecls(String restriction);

    public boolean hasComparator(String restriction);
    
    // test methods
    
    public void addTestMethod(String restriction, GallifreyMethodInstance mi, MethodDecl md);
    
    public Set<GallifreyMethodInstance> getTestMethods(RestrictionId restriction);
    
    public Set<GallifreyMethodInstance> getTestMethods(String restriction);
    
    public GallifreyMethodInstance getTestMethod(RestrictionId restriction, String methodName);
    
    public GallifreyMethodInstance getTestMethod(String restriction, String methodName);
    
    public void testMethod(GallifreyMethodInstance mi);

    // instances

    GallifreyMethodInstance methodInstance(Position pos, ReferenceType container, Flags flags, Type returnType,
            String name, List<? extends Type> argTypes, List<? extends Type> excTypes, List<RefQualification> inputQ,
            RefQualification returnQ);

    GallifreyMethodInstance methodInstance(Position pos, ReferenceType container, Flags flags, Type returnType,
            String name, List<? extends Type> argTypes, List<? extends Type> excTypes, List<TypeVariable> typeParams,
            List<RefQualification> inputQ, RefQualification returnQ);

    GallifreyLocalInstance localInstance(Position pos, Flags flags, Type type, String name, RefQualification q);

    GallifreyFieldInstance fieldInstance(Position pos, ReferenceType container, Flags flags, Type type, String name,
            RefQualification q);

    GallifreyConstructorInstance constructorInstance(Position pos, ClassType container, Flags flags,
            List<? extends Type> argTypes, List<? extends Type> excTypes, List<TypeVariable> typeParams,
            List<RefQualification> inputQ);

    // utils

    // check args of a function call, calculate the qualification of the returned
    // value
    GallifreyType checkArgs(GallifreyProcedureInstance pi, List<Expr> args) throws SemanticException;

    // check qualifications as if we were doing an assignment of toType = fromType
    boolean checkQualifications(GallifreyType fromType, GallifreyType toType);

    List<RefQualification> normalizeLocals(List<RefQualification> qualifications);

    boolean canBeShared(String className);

    void push_regionContext();

    RegionContext pop_regionContext();

    RegionContext region_context();

    RegionContext region_context(RegionContext region_context);

}
