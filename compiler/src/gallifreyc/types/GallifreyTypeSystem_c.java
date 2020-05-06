package gallifreyc.types;

import java.util.*;
import gallifreyc.ast.*;
import gallifreyc.extension.GallifreyExprExt;
import polyglot.ast.Expr;
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
    public ConstructorInstance defaultConstructor(Position pos,
            ClassType container) {
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
        return constructorInstance(pos,
                                   container,
                                   access,
                                   Collections.<Type> emptyList(),
                                   Collections.<Type> emptyList(),
                                   Collections.<TypeVariable> emptyList(),
                                   Collections.<RefQualification> emptyList());
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

    // checking qualifications

    public GallifreyType checkArgs(GallifreyProcedureInstance pi, List<Expr> args) throws SemanticException {
        boolean allMoves = true;
        List<GallifreyType> params = pi.gallifreyInputTypes();
        List<GallifreyType> argTypes = new ArrayList<>();
        
        //TODO check owners
        
        for (Expr e : args) {
            GallifreyType gt = GallifreyExprExt.ext(e).gallifreyType();
            if (!(gt.qualification() instanceof MoveRef)) {
                allMoves = false;
            }
            argTypes.add(gt);
        }
        
        // First check that the number of arguments is reasonable
        if (argTypes.size() != pi.formalTypes().size()) {
            // the actual args don't match the number of the formal args.
            if (!(pi.isVariableArity() && argTypes.size() >= pi.formalTypes()
                                                               .size() - 1)) {
                // the last (variable) argument can consume 0 or more of the actual arguments.
                throw new SemanticException("invalid number of arguments");
            }
        }
        
        // HACK: assume imported functions take in all-locals
        if (params.size() == 0) {
            params.add(new GallifreyType(new LocalRef(Position.COMPILER_GENERATED)));
        }
        
        for (int i = 0; i < argTypes.size(); i++) {
            GallifreyType argType = argTypes.get(i);
            GallifreyType paramType = params.get(Math.max(i, params.size() - 1));
            
            if (!checkQualifications(argType, paramType)) {
                throw new SemanticException("invalid argument qualification - expected: " + paramType.qualification + 
                        ", got: " + argType.qualification);
            }
        }
        
        if (allMoves) {
            return new GallifreyType(new MoveRef(Position.COMPILER_GENERATED));
        } else {
            return new GallifreyType(new LocalRef(Position.COMPILER_GENERATED));
        }
    }

    public boolean checkQualifications(GallifreyType fromType, GallifreyType toType) {
        if (fromType.qualification instanceof MoveRef || toType.qualification instanceof AnyRef) {
            return true;
        }

        if (fromType.qualification instanceof LocalRef && toType.qualification instanceof LocalRef) {
            return true;
        }

        return false;
    }

    // Casting

    protected boolean isCastValidFromArray(ArrayType arrayType, Type toType) {
        if (toType.isArray()) {
            ArrayType toArrayType = toType.toArray();
            if (arrayType.base().isPrimitive() && arrayType.base().equals(toArrayType.base())) {
                return true;
            }
            if (arrayType.base().isReference() && toArrayType.base().isReference()) {
                // modified from JL5TypeSystem
                return typeEquals(arrayType.base(), toArrayType.base());
            }
        }
        return super.isCastValidFromArray(arrayType, toType);
    }
    
    public List<RefQualification> normalizeLocals(List<RefQualification> qualifications) {
        int counter = 0;
        Map<String, Integer> ownerMap = new HashMap<>();
        List<RefQualification> result = new ArrayList<>();
        for (RefQualification q : qualifications) {
            if (q instanceof LocalRef) {
                LocalRef l = (LocalRef) q;
                if (!ownerMap.containsKey(l.ownerAnnotation)) {
                    counter++;
                    ownerMap.put(l.ownerAnnotation, counter);
                }
                String ownerName = "OWNER_"+ownerMap.get(l.ownerAnnotation);
                result.add(new LocalRef(l.position(), ownerName));
            } else {
                result.add(q);
            }
        }
        return result;
    }
}
