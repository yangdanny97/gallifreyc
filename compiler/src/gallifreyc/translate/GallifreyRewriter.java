package gallifreyc.translate;

import polyglot.ast.*;
import polyglot.ext.jl5.ast.AnnotationElem;
import polyglot.ext.jl5.ast.ParamTypeNode;
import polyglot.ext.jl5.types.TypeVariable;
import polyglot.frontend.ExtensionInfo;
import polyglot.frontend.Job;
import polyglot.types.ClassType;
import polyglot.types.Flags;
import polyglot.types.MethodInstance;
import polyglot.types.NullType;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.util.Position;
import gallifreyc.ast.*;
import gallifreyc.extension.GallifreyExprExt;
import gallifreyc.extension.GallifreyExt;
import gallifreyc.types.GallifreyMethodInstance;
import gallifreyc.types.GallifreyTypeSystem;

import java.util.*;

public class GallifreyRewriter extends GRewriter {
    public final String VALUE = "VALUE";
    public final String RES = "RESTRICTION";
    public final String TEMP = "TEMP";
    public final String SHARED = "sharedObj";
    public final String HOLDER = "holder";
    public final String LOCK = "lock";

    public List<ClassDecl> generatedClasses = new ArrayList<>();

    public GallifreyRewriter(Job job, ExtensionInfo from_ext, ExtensionInfo to_ext) {
        super(job, from_ext, to_ext);
    }

    @Override
    public TypeNode typeToJava(Type t, Position pos) {
        return super.typeToJava(t, pos);
    }

    public MethodDecl genRestrictionMethodSignature(MethodInstance i) {
        return (MethodDecl) this.genRestrictionMethod(i, false).body(null);
    }

    public MethodDecl genRestrictionMethod(MethodInstance inst, boolean suppressUnchecked) {
        GallifreyNodeFactory nf = (GallifreyNodeFactory) nodeFactory();
        typeSystem();
        GallifreyMethodInstance mi = (GallifreyMethodInstance) inst;

        Position p = Position.COMPILER_GENERATED;

        List<Expr> args = new ArrayList<>();
        List<Formal> formals = new ArrayList<>();
        for (int i = 0; i < mi.formalTypes().size(); i++) {
            Type t = mi.formalTypes().get(i);
            RefQualification q = mi.gallifreyInputTypes().get(i).qualification;
            String fresh = lang().freshVar();
            Id name = nf.Id(p, fresh);
            // wrappers for shared and unique
            if (q instanceof UniqueRef) {
                TypeNode tn = nf.TypeNodeFromQualifiedName(p, "Unique<" + t.toString() + ">");
                formals.add(nf.Formal(p, Flags.NONE, tn, (Id) name.copy()));
            } else if (q instanceof SharedRef) {
                SharedRef s = (SharedRef) q;
                RestrictionId rid = s.restriction();
                TypeNode tn = nf.TypeNodeFromQualifiedName(p, rid.getWrapperName());
                formals.add(nf.Formal(p, Flags.NONE, tn, (Id) name.copy()));
            } else {
                formals.add(nf.Formal(p, Flags.NONE, nf.CanonicalTypeNode(p, t), (Id) name.copy()));
            }
            // TODO varargs
            args.add(nf.Local(p, (Id) name.copy()));
        }

        List<Stmt> methodStmts = new ArrayList<>();
        Type returnType = mi.returnType();
        TypeNode genReturnType = nf.CanonicalTypeNode(p, returnType);

        // don't allow type vars
        if (returnType instanceof TypeVariable) {
            genReturnType = nf.TypeNodeFromQualifiedName(p, "Object");
        }
        // wrappers for shared and unique
        RefQualification q = mi.gallifreyReturnType().qualification;
        if (q instanceof UniqueRef) {
            genReturnType = nf.TypeNodeFromQualifiedName(p, "Unique<" + genReturnType.toString() + ">");
        }
        if (q instanceof SharedRef) {
            SharedRef s = (SharedRef) q;
            RestrictionId rid = s.restriction();
            genReturnType = nf.TypeNodeFromQualifiedName(p, rid.getInterfaceName());
        }

        // void f(T1 x, T2 y) -----> this.sharedObject.void_call("f", new
        // ArrayList<Object>(Arrays.asList(x))

        // T f(T1 x, T2 y) -----> return (T) this.sharedObject.const_call("f", new
        // ArrayList<Object>(Arrays.asList(x))
        Id fname = nf.Id(p, returnType.isVoid() ? "void_call" : "const_call");
        List<Expr> callArgs = new ArrayList<>();
        // function name
        callArgs.add(nf.StringLit(p, mi.name()));
        // array shenanigans
        callArgs.add(nf.New(p, nf.TypeNodeFromQualifiedName(p, "ArrayList<Object>"), new ArrayList<Expr>(
                Arrays.asList(nf.Call(p, nf.TypeNodeFromQualifiedName(p, "Arrays"), nf.Id(p, "asList"), args)))));

        Expr call = nf.Call(p, nf.Field(p, nf.This(p), nf.Id(p, SHARED)), fname, callArgs);
        if (returnType.isVoid()) {
            methodStmts.add(nf.Eval(p, call));
            methodStmts.add(nf.Return(p));
        } else {
            // cast return bc const_call returns Object
            methodStmts.add(nf.Return(p, nf.Cast(p, genReturnType, call)));
        }

        List<TypeNode> throwTypes = new ArrayList<>();
        for (Type t : mi.throwTypes()) {
            throwTypes.add(nf.CanonicalTypeNode(p, t));
        }

        List<ParamTypeNode> paramTypes = new ArrayList<>(); // TODO

        List<AnnotationElem> annotations = new ArrayList<AnnotationElem>();
        if (suppressUnchecked) {
            annotations.add(nf.SingleElementAnnotationElem(p, nf.TypeNodeFromQualifiedName(p, "SuppressWarnings"),
                    nf.StringLit(p, "unchecked")));
        }
        return nf.MethodDecl(p, Flags.PUBLIC, annotations, genReturnType, nf.Id(p, mi.name()), formals, throwTypes,
                nf.Block(p, methodStmts), paramTypes,
                nf.Javadoc(p, "// Wrapper method for " + mi.container().toString() + "." + mi.name()));
    }

    // class R_impl extends ... {...}
    public ClassDecl genRestrictionImplClass(RestrictionDecl d) {
        // generate a classDecl for each restrictionDecl
        // restriction R for C
        GallifreyNodeFactory nf = this.nodeFactory();
        GallifreyTypeSystem ts = this.typeSystem();

        TypeNode CTypeNode = d.forClass();
        String rName = d.name();

        Position p = Position.COMPILER_GENERATED;
        TypeNode sharedT = nf.TypeNodeFromQualifiedName(p, "SharedObject");

        List<ClassMember> sharedMembers = new ArrayList<>();
        // public SharedObject SHARED;
        FieldDecl f = nf.FieldDecl(p, Flags.PUBLIC, sharedT, nf.Id(p, this.SHARED));

        // private static final long serialVersionUID = 1;
        FieldDecl f2 = nf.FieldDecl(p, Flags.PRIVATE.Static().Final(), nf.CanonicalTypeNode(p, ts.Long()),
                nf.Id(p, "serialVersionUID"), nf.IntLit(p, IntLit.INT, 1));

        // FIRST CONSTRUCTOR

        List<Formal> constructorFormals = new ArrayList<>();
        // public R(C obj)
        constructorFormals.add(nf.Formal(p, Flags.NONE, (TypeNode) CTypeNode.copy(), nf.Id(p, "obj")));

        List<Stmt> constructorStmts = new ArrayList<>();
        // this.SHARED = new SharedObject(obj);
        Expr constructorRHS = nf.New(p, (TypeNode) sharedT.copy(),
                new ArrayList<Expr>(Arrays.asList(nf.AmbExpr(p, nf.Id(p, "obj")))));
        constructorStmts.add(nf.Eval(p,
                nf.FieldAssign(p, nf.Field(p, nf.This(p), nf.Id(p, this.SHARED)), Assign.ASSIGN, constructorRHS)));

        ConstructorDecl c = nf.ConstructorDecl(p, Flags.PUBLIC, nf.Id(p, rName + "_impl"), constructorFormals,
                new ArrayList<TypeNode>(), nf.Block(p, constructorStmts), nf.Javadoc(p, ""));

        // SECOND CONSTRUCTOR (FOR TRANSITIONS)

        List<Formal> constructorFormals2 = new ArrayList<>();
        // public R(SharedObject obj)
        constructorFormals2
                .add(nf.Formal(p, Flags.NONE, nf.TypeNodeFromQualifiedName(p, "SharedObject"), nf.Id(p, "obj")));

        List<Stmt> constructorStmts2 = new ArrayList<>();
        // this.SHARED = obj;
        constructorStmts2.add(nf.Eval(p, nf.FieldAssign(p, nf.Field(p, nf.This(p), nf.Id(p, this.SHARED)),
                Assign.ASSIGN, nf.AmbExpr(p, nf.Id(p, "obj")))));

        ConstructorDecl c2 = nf.ConstructorDecl(p, Flags.PUBLIC, nf.Id(p, rName + "_impl"), constructorFormals2,
                new ArrayList<TypeNode>(), nf.Block(p, constructorStmts2), nf.Javadoc(p, ""));

        sharedMembers.add(f2);
        sharedMembers.add(f);
        sharedMembers.add(c);
        sharedMembers.add(c2);

        // generate overrides for all the allowed methods
        Set<String> allowedMethods = ts.getAllowedMethods(rName);
        ClassType CType = (ClassType) CTypeNode.type();
        for (String name : allowedMethods) {
            for (MethodInstance method : CType.methodsNamed(name)) {
                sharedMembers.add(this.genRestrictionMethod(method, true));
            }
        }

        // getter for sharedObj field
        List<Formal> formals = new ArrayList<>();
        List<TypeNode> throwTypes = new ArrayList<>();
        List<ParamTypeNode> paramTypes = new ArrayList<>();
        List<Stmt> methodStmts = new ArrayList<>();
        methodStmts.add(nf.Return(p, nf.Field(p, nf.This(p), nf.Id(p, this.SHARED))));

        sharedMembers.add(nf.MethodDecl(p, Flags.PUBLIC, new ArrayList<AnnotationElem>(),
                nf.TypeNodeFromQualifiedName(p, "SharedObject"), nf.Id(p, this.SHARED), formals, throwTypes,
                nf.Block(p, methodStmts), paramTypes, nf.Javadoc(p, "")));

        List<TypeNode> interfaces = new ArrayList<>();
        interfaces.add(nf.TypeNodeFromQualifiedName(p, rName));
        for (String rv : typeSystem().getRVsForRestriction(rName)) {
            interfaces.add(nf.TypeNodeFromQualifiedName(p, rv + "_" + rName));
        }

        ClassBody sharedBody = nf.ClassBody(p, sharedMembers);

        // class R extends Shared implements Serializable (flags are same as C)
        ClassDecl sharedDecl = nf.ClassDecl(p, Flags.NONE, nf.Id(p, rName + "_impl"), null, interfaces, sharedBody,
                nf.Javadoc(p, "// Concrete restriction class for " + rName));

        return sharedDecl;
    }

    // interface R extends Shared {...}
    public ClassDecl genRestrictionInterface(RestrictionDecl d) {
        GallifreyNodeFactory nf = this.nodeFactory();
        GallifreyTypeSystem ts = this.typeSystem();

        TypeNode CTypeNode = d.forClass();
        String rName = d.name();

        Position p = Position.COMPILER_GENERATED;

        List<ClassMember> members = new ArrayList<>();

        // signatures for allowed methods
        Set<String> allowedMethods = ts.getAllowedMethods(rName);
        ClassType CType = (ClassType) CTypeNode.type();

        for (String name : allowedMethods) {
            for (MethodInstance method : CType.methodsNamed(name)) {
                members.add(this.genRestrictionMethodSignature(method));
            }
        }

        // getter for sharedObj field
        List<Formal> formals = new ArrayList<>();
        List<TypeNode> throwTypes = new ArrayList<>();
        List<ParamTypeNode> paramTypes = new ArrayList<>();
        members.add(nf.MethodDecl(p, Flags.PUBLIC, new ArrayList<AnnotationElem>(),
                nf.TypeNodeFromQualifiedName(p, "SharedObject"), nf.Id(p, this.SHARED), formals, throwTypes, null,
                paramTypes, nf.Javadoc(p, "")));

        List<TypeNode> interfaces = new ArrayList<>();
        interfaces.add(nf.TypeNodeFromQualifiedName(p, "Serializable"));
        interfaces.add(nf.TypeNodeFromQualifiedName(p, "Shared"));

        ClassBody body = nf.ClassBody(p, members);

        ClassDecl RInterface = nf.ClassDecl(p, Flags.INTERFACE, nf.Id(p, rName), null, interfaces, body,
                nf.Javadoc(p, "// Restriction interface class for " + rName));

        this.generatedClasses.add(RInterface);
        return RInterface;
    }

    // interface RV_holder extends Shared {...}
    public ClassDecl genRVHolderInterface(RestrictionUnionDecl d) {
        GallifreyNodeFactory nf = this.nodeFactory();
        Position p = Position.COMPILER_GENERATED;
        List<ClassMember> members = new ArrayList<>();

        List<TypeNode> interfaces = new ArrayList<>();
        interfaces.add(nf.TypeNodeFromQualifiedName(p, "Serializable"));
        interfaces.add(nf.TypeNodeFromQualifiedName(p, "Shared"));

        ClassBody body = nf.ClassBody(p, members);
        ClassDecl rvHolder = nf.ClassDecl(p, Flags.INTERFACE, nf.Id(p, d.name() + "_holder"), null, interfaces, body,
                nf.Javadoc(p, "// RV holder interface class for " + d.name()));

        // add to generated classes
        this.generatedClasses.add(rvHolder);
        return rvHolder;
    }

    // class RV {...}
    public ClassDecl genRVClass(RestrictionUnionDecl d) {
        GallifreyNodeFactory nf = this.nodeFactory();
        GallifreyTypeSystem ts = this.typeSystem();

        String name = d.name();

        Position p = Position.COMPILER_GENERATED;
        TypeNode holderT = nf.TypeNodeFromQualifiedName(p, name + "_holder");
        List<ClassMember> members = new ArrayList<>();

        ClassType CType = ts.getRestrictionClassType(d.name());
        TypeNode CTypeNode = nf.CanonicalTypeNode(p, CType);
        String defaultRimpl = ts.getRestrictionsForRV(name).iterator().next() + "_impl";

        // public RV_holder holder = null;
        members.add(nf.FieldDecl(p, Flags.PUBLIC, holderT, nf.Id(p, this.HOLDER), nf.NullLit(p)));
        // public int lock = 0;
        members.add(nf.FieldDecl(p, Flags.PUBLIC, nf.CanonicalTypeNode(p, ts.Int()), nf.Id(p, this.LOCK),
                nf.IntLit(p, IntLit.INT, 0)));

        // FIRST CONSTRUCTOR
        List<Formal> constructorFormals = new ArrayList<>();
        // public RV(C obj)
        constructorFormals.add(nf.Formal(p, Flags.NONE, (TypeNode) CTypeNode.copy(), nf.Id(p, "obj")));

        List<Stmt> constructorStmts = new ArrayList<>();
        // this.holder = defaultR(obj)
        constructorStmts.add(nf.Eval(p,
                nf.FieldAssign(p, nf.Field(p, nf.This(p), nf.Id(p, this.HOLDER)), Assign.ASSIGN,
                        nf.New(p, nf.TypeNodeFromQualifiedName(p, defaultRimpl),
                                Arrays.<Expr>asList(nf.AmbExpr(p, nf.Id(p, "obj")))))));

        members.add(nf.ConstructorDecl(p, Flags.PUBLIC, nf.Id(p, name), constructorFormals, new ArrayList<TypeNode>(),
                nf.Block(p, constructorStmts), nf.Javadoc(p, "")));

        // SECOND CONSTRUCTOR (FOR ASSIGNMENTS)
        constructorFormals = new ArrayList<>();
        // public RV(RV rv)
        constructorFormals.add(nf.Formal(p, Flags.NONE, nf.TypeNodeFromQualifiedName(p, name), nf.Id(p, "rv")));

        constructorStmts = new ArrayList<>();
        // this.holder = rv.holder;
        constructorStmts.add(nf.Eval(p, nf.FieldAssign(p, nf.Field(p, nf.This(p), nf.Id(p, this.HOLDER)), Assign.ASSIGN,
                nf.Field(p, nf.AmbExpr(p, nf.Id(p, "rv")), nf.Id(p, this.HOLDER)))));
        members.add(nf.ConstructorDecl(p, Flags.PUBLIC, nf.Id(p, name), constructorFormals, new ArrayList<TypeNode>(),
                nf.Block(p, constructorStmts), nf.Javadoc(p, "")));

        // transition void transition(Class<?> cls) {...}
        List<Formal> formals = new ArrayList<>();
        formals.add(nf.Formal(p, Flags.NONE, nf.TypeNodeFromQualifiedName(p, "Class<?>"), nf.Id(p, "cls")));
        List<TypeNode> throwTypes = new ArrayList<>();
        List<ParamTypeNode> paramTypes = new ArrayList<>();
        List<Stmt> methodStmts = new ArrayList<>();

        Expr constructor = nf.Call(p, nf.Local(p, nf.Id(p, "cls")), nf.Id(p, "getConstructor"),
                this.qq().parseExpr("SharedObject.class"));
        Expr newInstance = nf.Call(p, constructor, nf.Id(p, "newInstance"),
                this.qq().parseExpr("new Object[] {%E.sharedObj()}", nf.Field(p, nf.This(p), nf.Id(p, this.HOLDER))));
        Stmt assign = nf.Eval(p, nf.Assign(p, nf.Field(p, nf.This(p), nf.Id(p, this.HOLDER)), Assign.ASSIGN,
                nf.Cast(p, nf.TypeNodeFromQualifiedName(p, name + "_holder"), newInstance)));
        // if (this.lock > 0) return;
        Stmt lockcheck = nf.If(p,
                nf.Binary(p, nf.Field(p, nf.This(p), nf.Id(p, this.LOCK)), Binary.GT, nf.IntLit(p, IntLit.INT, 0)),
                nf.Return(p));
        List<Catch> catches = new ArrayList<>();
        // currently transitions fail silently
        catches.add(nf.Catch(p, nf.Formal(p, Flags.NONE, nf.TypeNodeFromQualifiedName(p, "Exception"), nf.Id(p, "e")),
                nf.Block(p)));
        methodStmts.add(nf.Try(p, nf.Block(p, lockcheck, assign), catches));
        members.add(nf.MethodDecl(p, Flags.PUBLIC, new ArrayList<AnnotationElem>(),
                nf.CanonicalTypeNode(p, typeSystem().Void()), nf.Id(p, "transition"), formals, throwTypes,
                nf.Block(p, methodStmts), paramTypes, nf.Javadoc(p, "")));

        // getter for holder field
        formals = new ArrayList<>();
        throwTypes = new ArrayList<>();
        paramTypes = new ArrayList<>();
        methodStmts = new ArrayList<>();
        methodStmts.add(nf.Return(p, nf.Field(p, nf.This(p), nf.Id(p, this.HOLDER))));
        members.add(nf.MethodDecl(p, Flags.PUBLIC, new ArrayList<AnnotationElem>(), holderT, nf.Id(p, this.HOLDER),
                formals, throwTypes, nf.Block(p, methodStmts), paramTypes, nf.Javadoc(p, "")));

        List<TypeNode> interfaces = new ArrayList<>();
        interfaces.add(nf.TypeNodeFromQualifiedName(p, "Serializable"));
        interfaces.add(nf.TypeNodeFromQualifiedName(p, "Shared"));

        // getter for sharedObj field
        methodStmts = new ArrayList<>();
        formals = new ArrayList<>();
        throwTypes = new ArrayList<>();
        paramTypes = new ArrayList<>();
        methodStmts
                .add(nf.Return(p, nf.Call(p, nf.Field(p, nf.This(p), nf.Id(p, this.HOLDER)), nf.Id(p, this.SHARED))));

        members.add(nf.MethodDecl(p, Flags.PUBLIC, new ArrayList<AnnotationElem>(),
                nf.TypeNodeFromQualifiedName(p, "SharedObject"), nf.Id(p, this.SHARED), formals, throwTypes,
                nf.Block(p, methodStmts), paramTypes, nf.Javadoc(p, "")));

        ClassBody body = nf.ClassBody(p, members);

        ClassDecl RInterface = nf.ClassDecl(p, Flags.NONE, nf.Id(p, name), null, interfaces, body,
                nf.Javadoc(p, "// Restriction interface class for " + name));

        return RInterface;
    }

    // class RV_R extends RV_holder, Shared {...}
    public ClassDecl genRVSubrestrictionInterface(String rv, String restriction) {
        GallifreyNodeFactory nf = this.nodeFactory();
        GallifreyTypeSystem ts = this.typeSystem();

        Position p = Position.COMPILER_GENERATED;

        List<ClassMember> members = new ArrayList<>();

        // signatures for allowed methods
        Set<String> allowedMethods = ts.getAllowedMethods(restriction);
        ClassType CType = (ClassType) ts.getRestrictionClassType(restriction);
        for (String name : allowedMethods) {
            for (MethodInstance method : CType.methodsNamed(name)) {
                members.add(this.genRestrictionMethodSignature(method));
            }
        }

        // getter for sharedObj field
        List<Formal> formals = new ArrayList<>();
        List<TypeNode> throwTypes = new ArrayList<>();
        List<ParamTypeNode> paramTypes = new ArrayList<>();
        members.add(nf.MethodDecl(p, Flags.PUBLIC, new ArrayList<AnnotationElem>(),
                nf.TypeNodeFromQualifiedName(p, "SharedObject"), nf.Id(p, this.SHARED), formals, throwTypes, null,
                paramTypes, nf.Javadoc(p, "")));

        List<TypeNode> interfaces = new ArrayList<>();
        interfaces.add(nf.TypeNodeFromQualifiedName(p, "Serializable"));
        interfaces.add(nf.TypeNodeFromQualifiedName(p, "Shared"));
        interfaces.add(nf.TypeNodeFromQualifiedName(p, rv + "_holder"));

        ClassBody body = nf.ClassBody(p, members);

        ClassDecl RVInterface = nf.ClassDecl(p, Flags.INTERFACE, nf.Id(p, rv + "_" + restriction), null, interfaces,
                body, nf.Javadoc(p, "// Restriction interface class for " + rv + "::" + restriction));

        this.generatedClasses.add(RVInterface);
        return RVInterface;
    }

    // rewrite RHS of assign & localDecl when LHS is shared[r]
    public Expr rewriteRHS(RestrictionId r, Expr rhs) {
        if (rhs.type() instanceof NullType) {
            return this.qq().parseExpr("(%T) %E", nf.TypeNodeFromQualifiedName(rhs.position(), r.getWrapperName()),
                    rhs);
        }
        GallifreyExprExt ext = GallifreyExprExt.ext(rhs);
        if (typeSystem().isRV(r.restriction().id())) {
            // lhs is shared[RV]
            // rhs is guaranteed to be either shared[RV] or shared[RV::R]
            return this.qq().parseExpr("new " + r.getWrapperName() + "(%E)", rhs);
        } else if (r.isRvQualified()) {
            // lhs is shared[RV::R]
            // rhs is guaranteed to be shared[RV::R]
            // handled the same as RV
            return this.qq().parseExpr("new " + r.getWrapperName() + "(%E)", rhs);
        } else if (ext.gallifreyType.qualification instanceof SharedRef) {
            // lhs is not RV, rhs is shared
            // rhs is guaranteed to be shared[r]
            return this.qq().parseExpr("new " + r.getWrapperName() + "_impl(%E.sharedObj())", rhs);
        } else {
            // lhs is not RV, rhs is not shared (regular object)
            return this.qq().parseExpr("new " + r.getWrapperName() + "_impl(%E)", rhs);
        }
    }

    public TypeNode getFormalTypeNode(RestrictionId rid) {
        if (rid.rv() == null) {
            return nodeFactory().TypeNodeFromQualifiedName(Position.COMPILER_GENERATED, rid.getWrapperName());
        } else {
            return nodeFactory().TypeNodeFromQualifiedName(Position.COMPILER_GENERATED, rid.rv().id());
        }
    }

    // wrap unique refs with .value, AFTER rewriting
    public Node wrapExpr(Expr e) {
        GallifreyNodeFactory nf = this.nodeFactory();
        GallifreyExprExt ext = GallifreyExprExt.ext(e);
        RefQualification q = ext.gallifreyType.qualification();
        if (q instanceof UniqueRef) {
            Expr new_e = nf.Field(Position.COMPILER_GENERATED, e, nf.Id(Position.COMPILER_GENERATED, VALUE));
            return new_e;
        }
        return e;
    }

    @Override
    public Node extRewrite(Node n) throws SemanticException {
        if (n instanceof Expr) {
            return wrapExpr((Expr) GallifreyExt.ext(n).gallifreyRewrite(this));
        }
        // no need to wrap Eval-ed expressions w/ .VALUE
        if (n instanceof Eval) {
            Eval e = (Eval) n;
            if (e.expr() instanceof Field) {
                Field f = (Field) e.expr();
                if (f.target() instanceof Expr
                        && GallifreyExprExt.ext(f.target()).gallifreyType.qualification() instanceof UniqueRef
                        && f.name().equals(VALUE)) {
                    n = nf.Eval(n.position(), (Expr) f.target());
                    return GallifreyExt.ext(n).gallifreyRewrite(this);
                }
            }

        }
        if (n instanceof Return) {
            Return r = (Return) n;
            if (r.expr() instanceof Field) {
                Field f = (Field) r.expr();
                if (f.target() instanceof Expr
                        && GallifreyExprExt.ext(f.target()).gallifreyType.qualification() instanceof UniqueRef
                        && f.name().equals(VALUE)) {
                    n = nf.Return(n.position(), (Expr) f.target());
                    return GallifreyExt.ext(n).gallifreyRewrite(this);
                }
            }

        }
        return GallifreyExt.ext(n).gallifreyRewrite(this);
    }
}
