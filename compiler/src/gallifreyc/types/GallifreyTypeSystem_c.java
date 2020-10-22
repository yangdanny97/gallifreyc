package gallifreyc.types;

import java.util.*;
import java.util.Map.Entry;

import gallifreyc.ast.*;
import gallifreyc.extension.GallifreyExprExt;
import polyglot.ast.Expr;
import polyglot.ast.MethodDecl;
import polyglot.ext.jl5.types.*;
import polyglot.ext.jl7.types.*;
import polyglot.types.*;
import polyglot.util.*;

public class GallifreyTypeSystem_c extends JL7TypeSystem_c implements GallifreyTypeSystem {

    // restriction name -> class name
    public Map<String, String> restrictionClassNameMap = new HashMap<>();

    // restriction name -> class type
    public Map<String, ClassType> restrictionClassTypeMap = new HashMap<>();

    // restriction name -> allowed methods
    public Map<String, Set<String>> allowedMethodsMap = new HashMap<>();

    // restriction name -> allowed test methods (the ones only allowed as tests)
    public Map<String, Set<String>> allowedTestMethodsMap = new HashMap<>();

    // RV names -> governed restriction names
    public Map<String, List<String>> restrictionUnionMap = new HashMap<>();

    // restriction name -> set of merge decls in restriction
    public Map<String, Set<MergeDecl>> mergeDecls = new HashMap<>();

    // restriction name -> set of test methods declared in restriction
    public Map<String, Set<GallifreyMethodInstance>> testMethods = new HashMap<>();
    public Map<String, Set<MethodDecl>> testMethodDecls = new HashMap<>();

    public RegionContext region_context = new RegionContext();

    public GallifreyMethodInstance testMethodInstance = null;

    public GallifreyTypeSystem_c() {
        super();
    }

    public void testMethod(GallifreyMethodInstance mi) {
        this.testMethodInstance = mi;
    }

    // ARRAY TYPES

    @Override
    protected ArrayType createArrayType(Position pos, Type type, boolean isVarargs) {
        return new GallifreyArrayType(this, pos, type, isVarargs);
    }

    @Override
    protected ArrayType createArrayType(Position pos, Type type) {
        return new GallifreyArrayType(this, pos, type, false);
    }

    // METHOD INSTANCE

    @Override
    public MethodInstance methodInstance(Position pos, ReferenceType container, Flags flags, Type returnType,
            String name, List<? extends Type> argTypes, List<? extends Type> excTypes) {
        return methodInstance(pos, container, flags, returnType, name, argTypes, excTypes,
                Collections.<TypeVariable>emptyList(), new ArrayList<RefQualification>(), null);
    }

    @Override
    public GallifreyMethodInstance methodInstance(Position pos, ReferenceType container, Flags flags, Type returnType,
            String name, List<? extends Type> argTypes, List<? extends Type> excTypes, List<TypeVariable> typeParams) {
        return methodInstance(pos, container, flags, returnType, name, argTypes, excTypes, typeParams,
                new ArrayList<RefQualification>(), null);
    }

    @Override
    public GallifreyMethodInstance methodInstance(Position pos, ReferenceType container, Flags flags, Type returnType,
            String name, List<? extends Type> argTypes, List<? extends Type> excTypes, List<RefQualification> inputQ,
            RefQualification returnQ) {
        return methodInstance(pos, container, flags, returnType, name, argTypes, excTypes,
                Collections.<TypeVariable>emptyList(), inputQ, returnQ);
    }

    @Override
    public GallifreyMethodInstance methodInstance(Position pos, ReferenceType container, Flags flags, Type returnType,
            String name, List<? extends Type> argTypes, List<? extends Type> excTypes, List<TypeVariable> typeParams,
            List<RefQualification> inputQ, RefQualification returnQ) {
        return new GallifreyMethodInstance_c(this, pos, container, flags, returnType, name, argTypes, excTypes,
                typeParams, inputQ, returnQ);
    }

    // CONSTRUCTOR INSTANCE

    @Override
    public GallifreyConstructorInstance constructorInstance(Position pos, ClassType container, Flags flags,
            List<? extends Type> argTypes, List<? extends Type> excTypes) {
        return constructorInstance(pos, container, flags, argTypes, excTypes, Collections.<TypeVariable>emptyList(),
                new ArrayList<RefQualification>());
    }

    @Override
    public GallifreyConstructorInstance constructorInstance(Position pos, ClassType container, Flags flags,
            List<? extends Type> argTypes, List<? extends Type> excTypes, List<TypeVariable> typeParams) {
        return new GallifreyConstructorInstance_c(this, pos, container, flags, argTypes, excTypes, typeParams,
                new ArrayList<RefQualification>());
    }

    @Override
    public GallifreyConstructorInstance constructorInstance(Position pos, ClassType container, Flags flags,
            List<? extends Type> argTypes, List<? extends Type> excTypes, List<TypeVariable> typeParams,
            List<RefQualification> inputQ) {
        return new GallifreyConstructorInstance_c(this, pos, container, flags, argTypes, excTypes, typeParams, inputQ);
    }

    @Override
    public ConstructorInstance defaultConstructor(Position pos, ClassType container) {
        assert_(container);

        // access for the default constructor is determined by the
        // access of the containing class. See the JLS, 2nd Ed., 8.8.7.
        Flags access = Flags.NONE;
        if (container.flags().isPrivate()) {
            access = access.Private();
        }
        if (container.flags().isProtected()) {
            access = access.Protected();
        }
        if (container.flags().isPublic()) {
            access = access.Public();
        }
        return constructorInstance(pos, container, access, Collections.<Type>emptyList(), Collections.<Type>emptyList(),
                Collections.<TypeVariable>emptyList(), Collections.<RefQualification>emptyList());
    }

    // LOCAL INSTANCE

    @Override
    public GallifreyLocalInstance localInstance(Position pos, Flags flags, Type type, String name) {
        // null qualification for now, fill in later
        return new GallifreyLocalInstance_c(this, pos, flags, type, name, null);
    }

    @Override
    public GallifreyLocalInstance localInstance(Position pos, Flags flags, Type type, String name, RefQualification q) {
        return new GallifreyLocalInstance_c(this, pos, flags, type, name, q);
    }

    // FIELD INSTANCE

    @Override
    public GallifreyFieldInstance fieldInstance(Position pos, ReferenceType container, Flags flags, Type type,
            String name) {
        return new GallifreyFieldInstance_c(this, pos, container, flags, type, name, null);
    }

    @Override
    public GallifreyFieldInstance fieldInstance(Position pos, ReferenceType container, Flags flags, Type type,
            String name, RefQualification q) {
        return new GallifreyFieldInstance_c(this, pos, container, flags, type, name, q);
    }

    // RESTRICTIONS

    @Override
    public void addRestrictionMapping(String restriction, String cls) throws SemanticException {
        if (restrictionClassNameMap.containsKey(restriction)) {
            throw new SemanticException("restriction " + restriction + " already exists!");
        }
        restrictionClassNameMap.put(restriction, cls);
        allowedMethodsMap.put(restriction, new HashSet<String>());
    }

    @Override
    public String getClassNameForRestriction(String restriction) {
        if (!restrictionClassNameMap.containsKey(restriction)) {
            return null;
        }
        return restrictionClassNameMap.get(restriction);
    }

    @Override
    public void addRV(String union, List<String> restrictions) {
        restrictionUnionMap.put(union, restrictions);
    }

    @Override
    public Set<String> getRVsForRestriction(String restriction) {
        Set<String> rvs = new HashSet<>();
        for (Entry<String, List<String>> pair : restrictionUnionMap.entrySet()) {
            if (pair.getValue().contains(restriction)) {
                rvs.add(pair.getKey());
            }
        }
        return rvs;
    }

    @Override
    public List<String> getRestrictionsForRV(String rv) {
        if (!restrictionUnionMap.containsKey(rv)) {
            return null;
        }
        return restrictionUnionMap.get(rv);
    }

    @Override
    public boolean isRV(String restriction) {
        return restrictionUnionMap.containsKey(restriction);
    }

    @Override
    public void addAllowedTestMethod(String restriction, String method) {
        if (!allowedTestMethodsMap.containsKey(restriction)) {
            allowedTestMethodsMap.put(restriction, new HashSet<String>());
        }
        allowedTestMethodsMap.get(restriction).add(method);
    }

    @Override
    public Set<String> getAllowedTestMethods(RestrictionId restriction) {
        String rName = restriction.restriction().id();
        return getAllowedTestMethods(rName);
    }

    @Override
    public Set<String> getAllowedTestMethods(String rName) {
        if (restrictionUnionMap.containsKey(rName)) {
            return new HashSet<String>();
        }

        Set<String> r = allowedTestMethodsMap.get(rName);
        if (r == null)
            return new HashSet<String>();
        return r;
    }

    @Override
    public void addAllowedMethod(String restriction, String method) {
        if (!allowedMethodsMap.containsKey(restriction)) {
            allowedMethodsMap.put(restriction, new HashSet<String>());
        }
        allowedMethodsMap.get(restriction).add(method);
    }

    @Override
    public Set<String> getAllowedMethods(RestrictionId restriction) {
        String rName = restriction.restriction().id();
        return getAllowedMethods(rName);
    }

    @Override
    public Set<String> getAllowedMethods(String rName) {
        Set<String> r = allowedMethodsMap.get(rName);
        if (r == null)
            return new HashSet<String>();
        return r;
    }

    @Override
    public List<GallifreyMethodInstance> getAllTestMethodInstances(String restriction, ClassType ct) {
        List<GallifreyMethodInstance> testInstances = new ArrayList<>();
        for (String mName : this.getAllowedTestMethods(restriction)) {
            List<MethodInstance> mInstances = new ArrayList<>();
            mInstances.addAll(ct.methodsNamed(mName));
            for (MethodInstance m : mInstances) {
                testInstances.add((GallifreyMethodInstance) m);
            }
        }
        testInstances.addAll(this.getTestMethods(restriction));
        return testInstances;
    }

    @Override
    public void addMergeDecl(String restriction, MergeDecl md) {
        if (!mergeDecls.containsKey(restriction)) {
            mergeDecls.put(restriction, new HashSet<MergeDecl>());
        }
        mergeDecls.get(restriction).add(md);
    }

    @Override
    public Set<MergeDecl> getMergeDecls(String restriction) {
        if (!mergeDecls.containsKey(restriction)) {
            return new HashSet<MergeDecl>();
        }
        return mergeDecls.get(restriction);
    }

    @Override
    public boolean hasComparator(String restriction) {
        return this.getMergeDecls(restriction).size() > 0;
    }

    @Override
    public boolean restrictionExists(String name) {
        return restrictionClassNameMap.containsKey(name) || restrictionUnionMap.containsKey(name);
    }

    @Override
    public void addRestrictionClassType(String restriction, ClassType cls) {
        restrictionClassTypeMap.put(restriction, cls);
    }

    @Override
    public ClassType getRestrictionClassType(String restriction) {
        if (isRV(restriction)) {
            return getRestrictionClassType(restrictionUnionMap.get(restriction).iterator().next());
        }
        return restrictionClassTypeMap.get(restriction);
    }

    // returns if a class has restrictions that are declared for it, does not
    // consider parent classes
    @Override
    public boolean canBeShared(String className) {
        for (Entry<String, String> pair : restrictionClassNameMap.entrySet()) {
            if (pair.getValue().equals(className)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void addTestMethod(String restriction, GallifreyMethodInstance mi, MethodDecl md) {
        if (!testMethods.containsKey(restriction)) {
            testMethods.put(restriction, new HashSet<GallifreyMethodInstance>());
        }
        testMethods.get(restriction).add(mi);
        if (!testMethodDecls.containsKey(restriction)) {
            testMethodDecls.put(restriction, new HashSet<MethodDecl>());
        }
        testMethodDecls.get(restriction).add(md);
    }

    @Override
    public Set<GallifreyMethodInstance> getTestMethods(RestrictionId restriction) {
        return getTestMethods(restriction.restriction().id());
    }

    @Override
    public Set<GallifreyMethodInstance> getTestMethods(String restriction) {
        Set<GallifreyMethodInstance> r = testMethods.get(restriction);
        if (r == null)
            return new HashSet<GallifreyMethodInstance>();
        return r;
    }

    @Override
    public Set<String> getTestMethodNames(String restriction) {
        Set<MethodDecl> r = testMethodDecls.get(restriction);
        if (r == null)
            return new HashSet<String>();
        Set<String> names = new HashSet<>();
        for (MethodDecl d : r) {
            names.add(d.name());
        }
        return names;
    }

    @Override
    public GallifreyMethodInstance getTestMethod(RestrictionId restriction, String methodName) {
        return getTestMethod(restriction.restriction().id(), methodName);
    }

    @Override
    public GallifreyMethodInstance getTestMethod(String restriction, String methodName) {
        Set<GallifreyMethodInstance> r = testMethods.get(restriction);
        if (r == null)
            return null;
        for (GallifreyMethodInstance mi : r) {
            if (mi.name().equals(methodName)) {
                return mi;
            }
        }
        return null;
    }

    // checking qualifications

    public GallifreyType checkArgs(GallifreyProcedureInstance pi, List<Expr> args) throws SemanticException {
        boolean allMoves = true;
        List<GallifreyType> params = pi.gallifreyInputTypes();
        List<GallifreyType> argTypes = new ArrayList<>();
        int nParams = params.size();

        // TODO check owners

        for (Expr e : args) {
            GallifreyType gt = GallifreyExprExt.ext(e).gallifreyType();
            if (!(gt.isMove())) {
                allMoves = false;
            }
            argTypes.add(gt);
        }

        // First check that the number of arguments is reasonable
        if (argTypes.size() != pi.formalTypes().size()) {
            // the actual args don't match the number of the formal args.
            if (!(pi.isVariableArity() && argTypes.size() >= pi.formalTypes().size() - 1)) {
                // the last (variable) argument can consume 0 or more of the actual arguments.
                throw new SemanticException("invalid number of arguments");
            }
        }

        // HACK: assume imported functions take in all-locals
        if (nParams == 0) {
            params.add(new GallifreyType(new LocalRef(Position.COMPILER_GENERATED)));
        }

        for (int i = 0; i < argTypes.size(); i++) {
            GallifreyType argType = argTypes.get(i);
            GallifreyType paramType = params.get(Math.min(i, params.size() - 1));

            if (!checkQualifications(argType, paramType)) {
                throw new SemanticException("invalid argument qualification - expected: " + paramType.qualification
                        + ", got: " + argType.qualification);
            }
        }

        if (allMoves || (args.size() == 0 && nParams == 0)) {
            return new GallifreyType(new MoveRef(Position.COMPILER_GENERATED));
        } else {
            return new GallifreyType(new LocalRef(Position.COMPILER_GENERATED));
        }
    }

    // checkQualifications(from,to) returns if b:to = a:from is legal
    public boolean checkQualifications(GallifreyType fromType, GallifreyType toType) {
        if (fromType == null || toType == null) {
            throw new IllegalArgumentException("null GallifreyType");
        }
//        if (fromType.isMove() || toType.qualification.isAny()) {
//            return true;
//        }
//
//        if (fromType.isLocal() && toType.isLocal()) {
//            return true;
//        }

        if (fromType.isShared() && toType.isShared()) {
            RestrictionId from = ((SharedRef) fromType.qualification).restriction();
            RestrictionId to = ((SharedRef) toType.qualification).restriction();
            // RV = RV::R is legal
            if (to.rv() == null && from.rv() != null && to.restriction().id().equals(from.rv().id())) {
                return true;
            }
            return fromType.qualification.equals(toType.qualification);
        }

        // TODO: temporary - until TS is fully implemented,
        // all assignments are legal for isolated/local/move/any
        return true;
    }

    // Casting

    protected boolean isCastValidFromArray(ArrayType arrayType, Type toType) {
        if (toType.isArray()) {
            ArrayType toArrayType = toType.toArray();
            if (arrayType.base().isPrimitive() && arrayType.base().equals(toArrayType.base())) {
                return true;
            }
            if (arrayType.base().isReference() && toArrayType.base().isReference()) {
                // GALLIFREY: changed from JL5TypeSystem; arrays are no longer covariant w/
                // content type
                return typeEquals(arrayType.base(), toArrayType.base());
            }
        }
        return super.isCastValidFromArray(arrayType, toType);
    }

    // normalize local owner annotations s.t. the Nth unique annotation encountered
    // maps to the name OWNER_N
    public List<RefQualification> normalizeLocals(List<RefQualification> qualifications) {
        int counter = 0;
        Map<String, Integer> ownerMap = new HashMap<>();
        List<RefQualification> result = new ArrayList<>();
        for (RefQualification q : qualifications) {
            if (q.isLocal()) {
                LocalRef l = (LocalRef) q;
                if (!ownerMap.containsKey(l.ownerAnnotation)) {
                    counter++;
                    ownerMap.put(l.ownerAnnotation, counter);
                }
                String ownerName = "OWNER_" + ownerMap.get(l.ownerAnnotation);
                result.add(new LocalRef(l.position(), ownerName));
            } else {
                result.add(q);
            }
        }
        return result;
    }

    @Override
    public boolean isValidRegion(Region_c r) {
        return this.region_context.heapctx.isValidRegion(r);
    }

    @Override
    public Region_c trueNew() {
        return this.region_context.heapctx.trueNew();
    }

    @Override
    public void regionAssign(Expr lhs, Region_c lhsRegion, Region_c rhsRegion) {
        this.region_context.heapctx.regionAssign(lhs, lhsRegion, rhsRegion);
    }

    @Override
    public RegionFunctionReturns regionApply(RegionFunctionType_c mi, List<Region_c> inputRegions) {
        return this.region_context.heapctx.regionApply(mi, inputRegions);
    }

    @Override
    public void push_regionContext() {
        region_context = new RegionContext(region_context);
    }

    @Override
    public RegionContext pop_regionContext() {
        RegionContext ret = region_context;
        assert (ret.prev != null);
        region_context = ret.prev;
        return ret;
    }

    @Override
    public RegionContext region_context() {
        return this.region_context;

    }

    @Override
    public RegionContext region_context(RegionContext region_context) {
        this.region_context = region_context;
        return region_context;

    }

    // explicitly handles test methods
    @Override
    protected List<? extends MethodInstance> findAcceptableMethods(ReferenceType container, String name,
            List<? extends Type> argTypes, List<? extends ReferenceType> actualTypeArgs, ClassType currClass,
            Type expectedReturnType, boolean fromClient) throws SemanticException {

        if (testMethodInstance != null) {
            JL5MethodInstance mi = methodCallValid(testMethodInstance, name, argTypes, actualTypeArgs,
                    expectedReturnType);
            if (mi == null) {
                throw new NoMemberException(NoMemberException.METHOD, "No valid test method found for " + name + "("
                        + listToString(argTypes) + ")" + " in " + container + ".");
            }
            testMethodInstance = null;
            List<MethodInstance> r = new ArrayList<>();
            r.add(mi);
            return r;
        }
        return super.findAcceptableMethods(container, name, argTypes, actualTypeArgs, currClass, expectedReturnType,
                fromClient);
    }

    @Override
    public List<String> getRestrictionsForClassName(String cls) {
        List<String> restrictions = new ArrayList<>();
        for (Entry<String, String> entry : restrictionClassNameMap.entrySet()) {
            if (entry.getValue().equals(cls)) {
                restrictions.add(entry.getKey());
            }
        }
        return restrictions;
    }

    @Override
    public List<MethodDecl> getRestrictionTestMethodsForClassName(String cls) {
        List<String> restrictions = getRestrictionsForClassName(cls);
        List<MethodDecl> methods = new ArrayList<>();
        for (String r : restrictions) {
            methods.addAll(testMethodDecls.getOrDefault(r, new HashSet<MethodDecl>()));
        }
        return methods;
    }

    @Override
    public List<String> getAllowedTestMethodsForClassName(String cls) {
        List<String> restrictions = getRestrictionsForClassName(cls);
        List<String> methods = new ArrayList<>();
        for (String r : restrictions) {
            methods.addAll(this.getAllowedTestMethods(r));
        }
        return methods;
    }

}
