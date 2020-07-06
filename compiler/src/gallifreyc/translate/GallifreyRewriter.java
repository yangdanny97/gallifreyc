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
    public final String MERGE_STRATEGY = "merge_strategy";

    public List<ClassDecl> generatedClasses = new ArrayList<>();

    public GallifreyRewriter(Job job, ExtensionInfo from_ext, ExtensionInfo to_ext) {
        super(job, from_ext, to_ext);
    }

    @Override
    public TypeNode typeToJava(Type t, Position pos) {
        return super.typeToJava(t, pos);
    }

    public MethodDecl genTestMethodSignature(GallifreyMethodInstance mi) {
        return (MethodDecl) this.genTestMethodWrapper(mi).body(null);
    }

    public MethodDecl genTestMethodWrapper(GallifreyMethodInstance mi) {
        Position p = Position.COMPILER_GENERATED;
        GallifreyTypeSystem ts = typeSystem();
        GallifreyNodeFactory nf = nodeFactory();

        String name = "__test__" + mi.name();
        List<Formal> formals = new ArrayList<>();
        List<Expr> args = new ArrayList<>();

        // get formals from method instance types/gallifreyTypes + fresh temps
        for (int i = 0; i < mi.formalTypes().size(); i++) {
            Type t = mi.formalTypes().get(i);
            RefQualification q = mi.gallifreyInputTypes().get(i).qualification;
            String fresh = lang().freshVar();
            // wrappers for shared and unique
            if (q.isUnique()) {
                formals.add(nf.Formal("Unique<" + t.toString() + ">", fresh));
            } else if (q.isShared()) {
                SharedRef s = (SharedRef) q;
                RestrictionId rid = s.restriction();
                formals.add(nf.Formal(rid.getWrapperName(), fresh));
            } else {
                formals.add(nf.Formal(p, Flags.NONE, nf.CanonicalTypeNode(p, t), nf.Id(fresh)));
            }
            // TODO varargs
            args.add(nf.Local(fresh));
        }

        formals.add(nf.Formal("RunAfterTest", "rat"));

        List<Stmt> methodBody = new ArrayList<>();

        List<Stmt> tryBlock = new ArrayList<>();
        // sleep for 1 ms
        tryBlock.add(nf.Eval(p, nf.Call(p, nf.TypeNode("Thread"), nf.Id("sleep"), nf.IntLit(p, IntLit.INT, 1))));

        List<Catch> catchBlocks = new ArrayList<>();
        catchBlocks.add(nf.Catch(p, nf.Formal("InterruptedException", "e"), nf.Block(p, new ArrayList<Stmt>())));

        Try trycatch = nf.Try(p, nf.Block(p, tryBlock), catchBlocks);
        While whileStmt = nf.While(p, nf.Unary(p, Unary.NOT, nf.Call(p, nf.Id(mi.name()), args)), trycatch);

        methodBody.add(whileStmt);
        methodBody.add(nf.Eval(p, nf.Call(p, nf.Local("rat"), nf.Id("run"), new ArrayList<Expr>())));

        Block bodyBlock = nf.Block(p, methodBody);
        List<TypeNode> throwTypes = new ArrayList<>();
        for (Type t : mi.throwTypes()) {
            throwTypes.add(nf.CanonicalTypeNode(p, t));
        }
        TypeNode returns = nf.CanonicalTypeNode(p, ts.Void());
        Javadoc jd = nf.Javadoc(p, "// restriction-defined test method");
        return nf.MethodDecl(p, Flags.PUBLIC, returns, nf.Id(name), formals, throwTypes, bodyBlock, jd);
    }

    public List<SwitchElement> genMergeComparatorBranch(MergeDecl d) {
        Position p = Position.COMPILER_GENERATED;
        GallifreyNodeFactory nf = this.nodeFactory();

        List<SwitchElement> elements = new ArrayList<>();
        elements.add(nf.Case(p, nf.StringLit(p, d.method1().id() + " " + d.method2().id())));
        List<Stmt> statements = new ArrayList<>();
        for (int i = 0; i < d.method1Formals().size(); i++) {
            // T x = (T) __f1.getArguments().get(0)
            Formal f = d.method1Formals().get(i);
            Expr rhs = nf.Cast(p, f.type(),
                    nf.Call(p, nf.Call(p, nf.Local("__f1"), nf.Id("getArguments"), new ArrayList<Expr>()), nf.Id("get"),
                            nf.IntLit(p, IntLit.INT, i)));
            statements.add(nf.LocalDecl(p, Flags.NONE, f.type(), f.id(), rhs));
        }
        for (int i = 0; i < d.method2Formals().size(); i++) {
            // T x = (T) __f2.getArguments().get(0)
            Formal f = d.method2Formals().get(i);
            Expr rhs = nf.Cast(p, f.type(),
                    nf.Call(p, nf.Call(p, nf.Local("__f2"), nf.Id("getArguments"), new ArrayList<Expr>()), nf.Id("get"),
                            nf.IntLit(p, IntLit.INT, i)));
            statements.add(nf.LocalDecl(p, Flags.NONE, f.type(), f.id(), rhs));
        }
        statements.add(d.body());
        // merge bodies always return, so no need to break

        // use block inside switch block to avoid shadowing problems
        List<Stmt> switchBlockStmts = new ArrayList<>();
        switchBlockStmts.add(nf.Block(p, statements));
        elements.add(nf.SwitchBlock(p, switchBlockStmts));
        return elements;
    }

    public ClassDecl genMergeClass(RestrictionDecl restriction) {
        // generate RComparator class for restriction R
        String name = restriction.name();
        GallifreyTypeSystem ts = this.typeSystem();
        GallifreyNodeFactory nf = this.nodeFactory();

        Set<MergeDecl> merges = ts.getMergeDecls(name);
        if (merges.size() == 0)
            return null;
        Position p = Position.COMPILER_GENERATED;
        List<ClassMember> members = new ArrayList<>();

        // empty constructor
        List<Formal> constructorFormals = new ArrayList<>();
        List<Stmt> constructorStmts = new ArrayList<>();
        ConstructorDecl c = nf.ConstructorDecl(p, Flags.PUBLIC, nf.Id(name + "Comparator"), constructorFormals,
                new ArrayList<TypeNode>(), nf.Block(p, constructorStmts), nf.Javadoc(p, ""));
        members.add(c);

        // merge method
        List<Stmt> methodStmts = new ArrayList<>();
        List<ParamTypeNode> paramTypes = new ArrayList<>();
        List<AnnotationElem> annotations = new ArrayList<AnnotationElem>();
        List<Formal> formals = new ArrayList<>();
        List<TypeNode> throwTypes = new ArrayList<>();
        formals.add(nf.Formal("GenericFunction", "__f1"));
        formals.add(nf.Formal("GenericFunction", "__f2"));
        // String __fname = __f1.getFunctionName() + " " + __f2.getFunctionName();
        methodStmts.add(nf.LocalDecl(p, Flags.NONE, nf.CanonicalTypeNode(p, ts.String()), nf.Id("__fname"),
                nf.Binary(p, nf.Call(p, nf.Local("__f1"), nf.Id("getFunctionName"), new ArrayList<Expr>()), Binary.ADD,
                        nf.Binary(p, nf.StringLit(p, " "), Binary.ADD,
                                nf.Call(p, nf.Local("__f2"), nf.Id("getFunctionName"), new ArrayList<Expr>())))));
        List<SwitchElement> elements = new ArrayList<>();
        for (MergeDecl d : merges) {
            elements.addAll(this.genMergeComparatorBranch(d));
        }
        methodStmts.add(nf.Switch(p, nf.Local("__fname"), elements));
        methodStmts.add(nf.Return(p, nf.IntLit(p, IntLit.INT, 0)));

        MethodDecl merge = nf.MethodDecl(p, Flags.PUBLIC, annotations, nf.CanonicalTypeNode(p, ts.Int()),
                nf.Id("compare"), formals, throwTypes, nf.Block(p, methodStmts), paramTypes,
                nf.Javadoc(p, "// Merge comparator method for " + name));
        members.add(merge);

        ClassBody body = nf.ClassBody(p, members);

        List<TypeNode> interfaces = new ArrayList<>();
        interfaces.add(nf.TypeNode("MergeComparator"));
        ClassDecl comparator = nf.ClassDecl(p, Flags.PUBLIC, nf.Id(name + "Comparator"), null, interfaces, body,
                nf.Javadoc(p, "// Merge comparator class for " + name));
        this.generatedClasses.add(comparator);
        return comparator;
    }

    public MethodDecl genRestrictionMethodSignature(MethodInstance i) {
        return (MethodDecl) this.genRestrictionMethod(i, false).body(null);
    }

    public MethodDecl genRVForwardMethod(MethodInstance inst, String rv, String restriction,
            boolean suppressUnchecked) {
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
            // wrappers for shared and unique
            if (q.isUnique()) {
                formals.add(nf.Formal("Unique<" + t.toString() + ">", fresh));
            } else if (q.isShared()) {
                SharedRef s = (SharedRef) q;
                RestrictionId rid = s.restriction();
                formals.add(nf.Formal(rid.getWrapperName(), fresh));
            } else {
                formals.add(nf.Formal(p, Flags.NONE, nf.CanonicalTypeNode(p, t), nf.Id(fresh)));
            }
            // TODO varargs
            args.add(nf.Local(p, nf.Id(fresh)));
        }

        List<Stmt> methodStmts = new ArrayList<>();
        Type returnType = mi.returnType();
        TypeNode genReturnType = nf.CanonicalTypeNode(p, returnType);

        // don't allow type vars
        if (returnType instanceof TypeVariable) {
            genReturnType = nf.TypeNode("Object");
        }
        // wrappers for shared and unique
        RefQualification q = mi.gallifreyReturnType().qualification;
        if (q.isUnique()) {
            genReturnType = nf.TypeNode("Unique<" + genReturnType.toString() + ">");
        }
        if (q.isShared()) {
            SharedRef s = (SharedRef) q;
            RestrictionId rid = s.restriction();
            genReturnType = nf.TypeNode(rid.getInterfaceName());
        }

        // return ((RV_R) this.holder).method();
        Expr call = nf.Call(p, nf.Cast(p, nf.TypeNode(rv + "_" + restriction), nf.Field(nf.This(p), HOLDER)),
                nf.Id(inst.name()), args);

        if (returnType.isVoid()) {
            methodStmts.add(nf.Eval(p, call));
        } else {
            methodStmts.add(nf.Return(p, call));
        }

        List<TypeNode> throwTypes = new ArrayList<>();
        for (Type t : mi.throwTypes()) {
            throwTypes.add(nf.CanonicalTypeNode(p, t));
        }

        List<ParamTypeNode> paramTypes = new ArrayList<>(); // TODO

        List<AnnotationElem> annotations = new ArrayList<AnnotationElem>();
        if (suppressUnchecked) {
            annotations.add(
                    nf.SingleElementAnnotationElem(p, nf.TypeNode("SuppressWarnings"), nf.StringLit(p, "unchecked")));
        }
        return nf.MethodDecl(p, Flags.PUBLIC, annotations, genReturnType, nf.Id(mi.name()), formals, throwTypes,
                nf.Block(p, methodStmts), paramTypes,
                nf.Javadoc(p, "// Wrapper method for " + mi.container().toString() + "." + mi.name()));
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
            Id name = nf.Id(fresh);
            // wrappers for shared and unique
            if (q.isUnique()) {
                formals.add(nf.Formal("Unique<" + t.toString() + ">", name.id()));
            } else if (q.isShared()) {
                SharedRef s = (SharedRef) q;
                RestrictionId rid = s.restriction();
                formals.add(nf.Formal(rid.getWrapperName(), name.id()));
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
            genReturnType = nf.TypeNode("Object");
        }
        // wrappers for shared and unique
        RefQualification q = mi.gallifreyReturnType().qualification;
        if (q.isUnique()) {
            genReturnType = nf.TypeNode("Unique<" + genReturnType.toString() + ">");
        }
        if (q.isShared()) {
            SharedRef s = (SharedRef) q;
            RestrictionId rid = s.restriction();
            genReturnType = nf.TypeNode(rid.getInterfaceName());
        }

        // void f(T1 x, T2 y) -----> this.sharedObject.void_call("f", new
        // ArrayList<Object>(Arrays.asList(x))

        // T f(T1 x, T2 y) -----> return (T) this.sharedObject.const_call("f", new
        // ArrayList<Object>(Arrays.asList(x))
        Id fname = nf.Id(returnType.isVoid() ? "void_call" : "const_call");
        List<Expr> callArgs = new ArrayList<>();
        // function name
        callArgs.add(nf.StringLit(p, mi.name()));
        // array shenanigans
        callArgs.add(nf.New(p, nf.TypeNode("ArrayList<Object>"),
                new ArrayList<Expr>(Arrays.asList(nf.Call(p, nf.TypeNode("Arrays"), nf.Id("asList"), args)))));

        Expr call = nf.Call(p, nf.Field(nf.This(p), SHARED), fname, callArgs);
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
            annotations.add(
                    nf.SingleElementAnnotationElem(p, nf.TypeNode("SuppressWarnings"), nf.StringLit(p, "unchecked")));
        }
        return nf.MethodDecl(p, Flags.PUBLIC, annotations, genReturnType, nf.Id(mi.name()), formals, throwTypes,
                nf.Block(p, methodStmts), paramTypes,
                nf.Javadoc(p, "// Wrapper method for " + mi.container().toString() + "." + mi.name()));
    }

    // class R_impl extends ... {...}
    public ClassDecl genRestrictionImplClass(RestrictionDecl d) {
        // generate a classDecl for each restrictionDecl
        // restriction R for C
        GallifreyNodeFactory nf = this.nodeFactory();
        GallifreyTypeSystem ts = this.typeSystem();

        TypeNode ctNode = d.forClass();
        String restriction = d.name();

        Position p = Position.COMPILER_GENERATED;
        TypeNode sharedT = nf.TypeNode("SharedObject");

        List<ClassMember> members = new ArrayList<>();
        // public SharedObject SHARED;
        FieldDecl f = nf.FieldDecl(p, Flags.PUBLIC, sharedT, nf.Id(this.SHARED));

        // private static final long serialVersionUID = 1;
        FieldDecl f2 = nf.FieldDecl(p, Flags.PRIVATE.Static().Final(), nf.CanonicalTypeNode(p, ts.Long()),
                nf.Id("serialVersionUID"), nf.IntLit(p, IntLit.INT, 1));

        // FIRST CONSTRUCTOR

        List<Formal> constructorFormals = new ArrayList<>();
        // public R(C obj)
        constructorFormals.add(nf.Formal(p, Flags.NONE, (TypeNode) ctNode.copy(), nf.Id("obj")));

        List<Stmt> constructorStmts = new ArrayList<>();
        // this.SHARED = new SharedObject(obj);
        Expr constructorRHS = nf.New(p, (TypeNode) sharedT.copy(),
                new ArrayList<Expr>(Arrays.asList(nf.AmbExpr(p, nf.Id("obj")))));
        constructorStmts
                .add(nf.Eval(p, nf.FieldAssign(p, nf.Field(nf.This(p), this.SHARED), Assign.ASSIGN, constructorRHS)));

        // add MergeComparator: this.SHARED.merge_strategy = new RComparator()
        if (ts.getMergeDecls(restriction).size() > 0) {
            constructorStmts.add(nf.Eval(p,
                    nf.FieldAssign(p, nf.Field(p, nf.Field(nf.This(p), this.SHARED), nf.Id(this.MERGE_STRATEGY)),
                            Assign.ASSIGN, nf.New(p, nf.TypeNode(restriction + "Comparator"), new ArrayList<Expr>()))));
        }

        ConstructorDecl c = nf.ConstructorDecl(p, Flags.PUBLIC, nf.Id(restriction + "_impl"), constructorFormals,
                new ArrayList<TypeNode>(), nf.Block(p, constructorStmts), nf.Javadoc(p, ""));

        // SECOND CONSTRUCTOR (FOR TRANSITIONS)

        List<Formal> constructorFormals2 = new ArrayList<>();
        // public R(SharedObject obj)
        constructorFormals2.add(nf.Formal("SharedObject", "obj"));

        List<Stmt> constructorStmts2 = new ArrayList<>();
        // this.SHARED = obj;
        constructorStmts2.add(nf.Eval(p,
                nf.FieldAssign(p, nf.Field(nf.This(p), this.SHARED), Assign.ASSIGN, nf.AmbExpr(p, nf.Id("obj")))));
        // add MergeComparator: this.SHARED.merge_strategy = new RComparator()
        if (ts.getMergeDecls(restriction).size() > 0) {
            constructorStmts2.add(nf.Eval(p,
                    nf.FieldAssign(p, nf.Field(p, nf.Field(nf.This(p), this.SHARED), nf.Id(this.MERGE_STRATEGY)),
                            Assign.ASSIGN, nf.New(p, nf.TypeNode(restriction + "Comparator"), new ArrayList<Expr>()))));
        }

        ConstructorDecl c2 = nf.ConstructorDecl(p, Flags.PUBLIC, nf.Id(restriction + "_impl"), constructorFormals2,
                new ArrayList<TypeNode>(), nf.Block(p, constructorStmts2), nf.Javadoc(p, ""));

        members.add(f2);
        members.add(f);
        members.add(c);
        members.add(c2);

        // generate overrides for all the allowed methods
        Set<String> allowedMethods = ts.getAllowedMethods(restriction);
        allowedMethods.addAll(ts.getAllowedTestMethods(restriction));

        ClassType ct = (ClassType) ctNode.type();
        for (String name : allowedMethods) {
            for (MethodInstance method : ct.methodsNamed(name)) {
                members.add(this.genRestrictionMethod(method, true));
            }
        }
        for (MethodInstance method : ts.getTestMethods(restriction)) {
            members.add(this.genRestrictionMethod(method, true));
        }

        for (GallifreyMethodInstance mi : ts.getAllTestMethodInstances(restriction, ct)) {
            members.add(this.genTestMethodWrapper(mi));
        }

        // getter for sharedObj field
        List<Formal> formals = new ArrayList<>();
        List<TypeNode> throwTypes = new ArrayList<>();
        List<ParamTypeNode> paramTypes = new ArrayList<>();
        List<Stmt> methodStmts = new ArrayList<>();
        methodStmts.add(nf.Return(p, nf.Field(nf.This(p), this.SHARED)));

        members.add(nf.MethodDecl(p, Flags.PUBLIC, new ArrayList<AnnotationElem>(), nf.TypeNode("SharedObject"),
                nf.Id(this.SHARED), formals, throwTypes, nf.Block(p, methodStmts), paramTypes, nf.Javadoc(p, "")));

        List<TypeNode> interfaces = new ArrayList<>();
        interfaces.add(nf.TypeNode(restriction));
        for (String rv : typeSystem().getRVsForRestriction(restriction)) {
            interfaces.add(nf.TypeNode(rv + "_" + restriction));
        }

        ClassBody body = nf.ClassBody(p, members);

        // class R extends Shared implements Serializable (flags are same as C)
        ClassDecl decl = nf.ClassDecl(p, Flags.PUBLIC, nf.Id(restriction + "_impl"), null, interfaces, body,
                nf.Javadoc(p, "// Concrete restriction class for " + restriction));

        this.generatedClasses.add(decl);
        return decl;
    }

    // interface R extends Shared {...}
    public ClassDecl genRestrictionInterface(RestrictionDecl d) {
        GallifreyNodeFactory nf = this.nodeFactory();
        GallifreyTypeSystem ts = this.typeSystem();

        TypeNode ctNode = d.forClass();
        String restriction = d.name();

        Position p = Position.COMPILER_GENERATED;

        List<ClassMember> members = new ArrayList<>();

        // signatures for allowed methods
        Set<String> allowedMethods = ts.getAllowedMethods(restriction);
        allowedMethods.addAll(ts.getAllowedTestMethods(restriction));

        ClassType ct = (ClassType) ctNode.type();

        for (String name : allowedMethods) {
            for (MethodInstance method : ct.methodsNamed(name)) {
                members.add(this.genRestrictionMethodSignature(method));
            }
        }
        for (MethodInstance method : ts.getTestMethods(restriction)) {
            members.add(this.genRestrictionMethodSignature(method));
        }

        for (GallifreyMethodInstance mi : ts.getAllTestMethodInstances(restriction, ct)) {
            members.add(this.genTestMethodSignature(mi));
        }

        // getter for sharedObj field
        List<Formal> formals = new ArrayList<>();
        List<TypeNode> throwTypes = new ArrayList<>();
        List<ParamTypeNode> paramTypes = new ArrayList<>();
        members.add(nf.MethodDecl(p, Flags.PUBLIC, new ArrayList<AnnotationElem>(), nf.TypeNode("SharedObject"),
                nf.Id(this.SHARED), formals, throwTypes, null, paramTypes, nf.Javadoc(p, "")));

        List<TypeNode> interfaces = new ArrayList<>();
        interfaces.add(nf.TypeNode("Serializable"));
        interfaces.add(nf.TypeNode("Shared"));

        ClassBody body = nf.ClassBody(p, members);

        ClassDecl RInterface = nf.ClassDecl(p, Flags.INTERFACE, nf.Id(restriction), null, interfaces, body,
                nf.Javadoc(p, "// Restriction interface class for " + restriction));

        this.generatedClasses.add(RInterface);
        return RInterface;
    }

    // class RV_R_impl extends RV implements RV_R {...}
    public ClassDecl genRVSubrestrictionImpl(String rv, String restriction) {
        GallifreyNodeFactory nf = this.nodeFactory();
        GallifreyTypeSystem ts = this.typeSystem();
        String forclass = ts.getClassNameForRestriction(restriction);

        Position p = Position.COMPILER_GENERATED;

        List<ClassMember> members = new ArrayList<>();

        // FIRST CONSTRUCTOR
        List<Formal> constructorFormals = new ArrayList<>();
        // public RV_R_impl(C obj)
        constructorFormals.add(nf.Formal(forclass, "obj"));

        List<Stmt> constructorStmts = new ArrayList<>();
        List<Expr> args = new ArrayList<>();
        args.add(nf.Local("obj"));
        // super(obj);
        constructorStmts.add(nf.ConstructorCall(p, ConstructorCall.SUPER, args));

        members.add(nf.ConstructorDecl(p, Flags.PUBLIC, nf.Id(rv + "_" + restriction + "_impl"), constructorFormals,
                new ArrayList<TypeNode>(), nf.Block(p, constructorStmts), nf.Javadoc(p, "")));

        // SECOND CONSTRUCTOR
        constructorFormals = new ArrayList<>();
        // public RV_R_impl(RV rv)
        constructorFormals.add(nf.Formal(rv, "rv"));
        args = new ArrayList<>();
        args.add(nf.Local("rv"));
        // super(rv);
        constructorStmts = new ArrayList<>();
        constructorStmts.add(nf.ConstructorCall(p, ConstructorCall.SUPER, args));
        members.add(nf.ConstructorDecl(p, Flags.PUBLIC, nf.Id(rv + "_" + restriction + "_impl"), constructorFormals,
                new ArrayList<TypeNode>(), nf.Block(p, constructorStmts), nf.Javadoc(p, "")));

        // forward for allowed methods
        Set<String> allowedMethods = ts.getAllowedMethods(restriction);
        allowedMethods.addAll(ts.getAllowedTestMethods(restriction));
        allowedMethods.addAll(ts.getTestMethodNames(restriction));

        ClassType ct = (ClassType) ts.getRestrictionClassType(restriction);
        for (String name : allowedMethods) {
            for (MethodInstance method : ct.methodsNamed(name)) {
                members.add(this.genRVForwardMethod(method, rv, restriction, true));
            }
        }

        for (GallifreyMethodInstance mi : ts.getAllTestMethodInstances(restriction, ct)) {
            members.add(this.genTestMethodSignature(mi));
        }

        // getter for sharedObj field
        List<Formal> formals = new ArrayList<>();
        List<TypeNode> throwTypes = new ArrayList<>();
        List<ParamTypeNode> paramTypes = new ArrayList<>();
        List<Stmt> methodStmts = new ArrayList<>();
        methodStmts.add(nf.Return(p, nf.Call(p, nf.Field(nf.This(p), this.HOLDER), nf.Id(this.SHARED))));
        members.add(nf.MethodDecl(p, Flags.PUBLIC, new ArrayList<AnnotationElem>(), nf.TypeNode("SharedObject"),
                nf.Id(this.SHARED), formals, throwTypes, nf.Block(p, methodStmts), paramTypes, nf.Javadoc(p, "")));

        List<TypeNode> interfaces = new ArrayList<>();
        interfaces.add(nf.TypeNode("Serializable"));
        interfaces.add(nf.TypeNode("Shared"));
        interfaces.add(nf.TypeNode(rv + "_" + restriction));

        ClassBody body = nf.ClassBody(p, members);

        ClassDecl RVImpl = nf.ClassDecl(p, Flags.PUBLIC, nf.Id(rv + "_" + restriction + "_impl"), nf.TypeNode(rv),
                interfaces, body, nf.Javadoc(p, "// Restriction class for " + rv + "::" + restriction));

        this.generatedClasses.add(RVImpl);
        return RVImpl;
    }

    // interface RV_holder extends Shared {...}
    public ClassDecl genRVHolderInterface(RestrictionUnionDecl d) {
        GallifreyNodeFactory nf = this.nodeFactory();
        Position p = Position.COMPILER_GENERATED;
        List<ClassMember> members = new ArrayList<>();

        List<TypeNode> interfaces = new ArrayList<>();
        interfaces.add(nf.TypeNode("Serializable"));
        interfaces.add(nf.TypeNode("Shared"));

        ClassBody body = nf.ClassBody(p, members);
        ClassDecl rvHolder = nf.ClassDecl(p, Flags.PUBLIC.Interface(), nf.Id(d.name() + "_holder"), null, interfaces,
                body, nf.Javadoc(p, "// RV holder interface class for " + d.name()));

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
        TypeNode holderT = nf.TypeNode(name + "_holder");
        List<ClassMember> members = new ArrayList<>();

        ClassType ct = ts.getRestrictionClassType(d.name());
        TypeNode ctNode = nf.CanonicalTypeNode(p, ct);
        String defaultRimpl = ts.getRestrictionsForRV(name).iterator().next() + "_impl";

        // public RV_holder holder = null;
        members.add(nf.FieldDecl(p, Flags.PUBLIC, holderT, nf.Id(this.HOLDER), nf.NullLit(p)));
        // public int lock = 0;
        members.add(nf.FieldDecl(p, Flags.PUBLIC, nf.CanonicalTypeNode(p, ts.Int()), nf.Id(this.LOCK),
                nf.IntLit(p, IntLit.INT, 0)));

        // FIRST CONSTRUCTOR
        List<Formal> constructorFormals = new ArrayList<>();
        // public RV(C obj)
        constructorFormals.add(nf.Formal(p, Flags.NONE, (TypeNode) ctNode.copy(), nf.Id("obj")));

        List<Stmt> constructorStmts = new ArrayList<>();
        // this.holder = defaultR(obj)
        constructorStmts.add(nf.Eval(p, nf.FieldAssign(p, nf.Field(nf.This(p), this.HOLDER), Assign.ASSIGN,
                nf.New(p, nf.TypeNode(defaultRimpl), Arrays.<Expr>asList(nf.AmbExpr(p, nf.Id("obj")))))));

        members.add(nf.ConstructorDecl(p, Flags.PUBLIC, nf.Id(name), constructorFormals, new ArrayList<TypeNode>(),
                nf.Block(p, constructorStmts), nf.Javadoc(p, "")));

        // SECOND CONSTRUCTOR (FOR ASSIGNMENTS)
        constructorFormals = new ArrayList<>();
        // public RV(RV rv)
        constructorFormals.add(nf.Formal(name, "rv"));

        constructorStmts = new ArrayList<>();
        // this.holder = rv.holder;
        constructorStmts.add(nf.Eval(p, nf.FieldAssign(p, nf.Field(nf.This(p), this.HOLDER), Assign.ASSIGN,
                nf.Field(p, nf.AmbExpr(p, nf.Id("rv")), nf.Id(this.HOLDER)))));
        // this.lock = rv.lock;
        constructorStmts.add(nf.Eval(p, nf.FieldAssign(p, nf.Field(nf.This(p), this.LOCK), Assign.ASSIGN,
                nf.Field(p, nf.AmbExpr(p, nf.Id("rv")), nf.Id(this.LOCK)))));
        members.add(nf.ConstructorDecl(p, Flags.PUBLIC, nf.Id(name), constructorFormals, new ArrayList<TypeNode>(),
                nf.Block(p, constructorStmts), nf.Javadoc(p, "")));

        // transition void transition(Class<?> cls) {...}
        List<Formal> formals = new ArrayList<>();
        formals.add(nf.Formal("Class<?>", "cls"));
        List<TypeNode> throwTypes = new ArrayList<>();
        List<ParamTypeNode> paramTypes = new ArrayList<>();
        List<Stmt> methodStmts = new ArrayList<>();

        Expr constructor = nf.Call(p, nf.Local("cls"), nf.Id("getConstructor"),
                this.qq().parseExpr("SharedObject.class"));
        Expr newInstance = nf.Call(p, constructor, nf.Id("newInstance"),
                this.qq().parseExpr("new Object[] {%E.sharedObj().transition(cls.getName())}", nf.Field(nf.This(p), this.HOLDER)));
        Stmt assign = nf.Eval(p, nf.Assign(p, nf.Field(nf.This(p), this.HOLDER), Assign.ASSIGN,
                nf.Cast(p, nf.TypeNode(name + "_holder"), newInstance)));
        // if (this.lock > 0) return;
        Stmt lockcheck = nf.If(p, nf.Binary(p, nf.Field(nf.This(p), this.LOCK), Binary.GT, nf.IntLit(p, IntLit.INT, 0)),
                nf.Return(p));
        List<Catch> catches = new ArrayList<>();
        // currently transitions fail silently
        catches.add(nf.Catch(p, nf.Formal("Exception", "e"), nf.Block(p)));
        methodStmts.add(nf.Try(p, nf.Block(p, lockcheck, assign), catches));
        members.add(nf.MethodDecl(p, Flags.PUBLIC, new ArrayList<AnnotationElem>(),
                nf.CanonicalTypeNode(p, typeSystem().Void()), nf.Id("transition"), formals, throwTypes,
                nf.Block(p, methodStmts), paramTypes, nf.Javadoc(p, "")));

        // getter for holder field
        formals = new ArrayList<>();
        throwTypes = new ArrayList<>();
        paramTypes = new ArrayList<>();
        methodStmts = new ArrayList<>();
        methodStmts.add(nf.Return(p, nf.Field(nf.This(p), this.HOLDER)));
        members.add(nf.MethodDecl(p, Flags.PUBLIC, new ArrayList<AnnotationElem>(), holderT, nf.Id(this.HOLDER),
                formals, throwTypes, nf.Block(p, methodStmts), paramTypes, nf.Javadoc(p, "")));

        List<TypeNode> interfaces = new ArrayList<>();
        interfaces.add(nf.TypeNode("Serializable"));
        interfaces.add(nf.TypeNode("Shared"));

        // getter for sharedObj field
        methodStmts = new ArrayList<>();
        formals = new ArrayList<>();
        throwTypes = new ArrayList<>();
        paramTypes = new ArrayList<>();
        methodStmts.add(nf.Return(p, nf.Call(p, nf.Field(nf.This(p), this.HOLDER), nf.Id(this.SHARED))));

        members.add(nf.MethodDecl(p, Flags.PUBLIC, new ArrayList<AnnotationElem>(), nf.TypeNode("SharedObject"),
                nf.Id(this.SHARED), formals, throwTypes, nf.Block(p, methodStmts), paramTypes, nf.Javadoc(p, "")));

        ClassBody body = nf.ClassBody(p, members);

        ClassDecl RInterface = nf.ClassDecl(p, Flags.PUBLIC, nf.Id(name), null, interfaces, body,
                nf.Javadoc(p, "// Restriction interface class for " + name));

        this.generatedClasses.add(RInterface);
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
        allowedMethods.addAll(ts.getAllowedTestMethods(restriction));
        
        ClassType ct = (ClassType) ts.getRestrictionClassType(restriction);
        for (String name : allowedMethods) {
            for (MethodInstance method : ct.methodsNamed(name)) {
                members.add(this.genRestrictionMethodSignature(method));
            }
        }
        for (MethodInstance method : ts.getTestMethods(restriction)) {
            members.add(this.genRestrictionMethodSignature(method));
        }

        // getter for sharedObj field
        List<Formal> formals = new ArrayList<>();
        List<TypeNode> throwTypes = new ArrayList<>();
        List<ParamTypeNode> paramTypes = new ArrayList<>();
        members.add(nf.MethodDecl(p, Flags.PUBLIC, new ArrayList<AnnotationElem>(), nf.TypeNode("SharedObject"),
                nf.Id(this.SHARED), formals, throwTypes, null, paramTypes, nf.Javadoc(p, "")));

        List<TypeNode> interfaces = new ArrayList<>();
        interfaces.add(nf.TypeNode("Serializable"));
        interfaces.add(nf.TypeNode("Shared"));
        interfaces.add(nf.TypeNode(rv + "_holder"));

        ClassBody body = nf.ClassBody(p, members);

        ClassDecl RVInterface = nf.ClassDecl(p, Flags.PUBLIC.Interface(), nf.Id(rv + "_" + restriction), null,
                interfaces, body, nf.Javadoc(p, "// Restriction interface class for " + rv + "::" + restriction));

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
            // rhs is guaranteed to be either shared[RV] or shared[RV::R] or nonshared
            if (!(ext.gallifreyType.isShared())) {
                rhs = this.qq().parseExpr("new " + r.getWrapperName() + "(%E)", rhs);
            }
        } else if (r.isRvQualified()) {
            // lhs is shared[RV::R]
            // rhs is guaranteed to be shared[RV::R] or nonshared
            // handled the same as RV
            if (!(ext.gallifreyType.isShared())) {
                rhs = this.qq().parseExpr("new " + r.getWrapperName() + "(%E)", rhs);
            }
        } else {
            // lhs is shared[R] (not RV)
            // rhs is guaranteed to be shared[R] or nonshared
            if (!(ext.gallifreyType.isShared())) {
                rhs = this.qq().parseExpr("new " + r.getWrapperName() + "_impl(%E)", rhs);
            }
        }
        return rhs;
    }

    public TypeNode getFormalTypeNode(RestrictionId rid) {
        return nodeFactory().TypeNode(rid.getWrapperName());
    }

    // wrap unique refs with .value, AFTER rewriting
    public Node wrapExpr(Expr e) {
        GallifreyNodeFactory nf = this.nodeFactory();
        GallifreyExprExt ext = GallifreyExprExt.ext(e);
        RefQualification q = ext.gallifreyType.qualification();
        if (q.isUnique()) {
            Expr new_e = nf.Field(e, VALUE);
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
                        && GallifreyExprExt.ext(f.target()).gallifreyType.qualification().isUnique()
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
                        && GallifreyExprExt.ext(f.target()).gallifreyType.qualification().isUnique()
                        && f.name().equals(VALUE)) {
                    n = nf.Return(n.position(), (Expr) f.target());
                    return GallifreyExt.ext(n).gallifreyRewrite(this);
                }
            }

        }
        return GallifreyExt.ext(n).gallifreyRewrite(this);
    }
}
