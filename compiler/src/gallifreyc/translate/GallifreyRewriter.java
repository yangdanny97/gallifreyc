package gallifreyc.translate;

import polyglot.ast.*;
import polyglot.ext.jl5.ast.AnnotationElem;
import polyglot.ext.jl5.ast.JL5Ext;
import polyglot.ext.jl5.ast.JL5FormalExt;
import polyglot.ext.jl5.ast.ParamTypeNode;
import polyglot.frontend.ExtensionInfo;
import polyglot.frontend.Job;
import polyglot.types.ClassType;
import polyglot.types.Flags;
import polyglot.types.MethodInstance;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.PrimitiveType;
import polyglot.util.Position;
import polyglot.visit.NodeVisitor;
import gallifreyc.ast.*;
import gallifreyc.extension.GallifreyExprExt;
import gallifreyc.extension.GallifreyExt;
import gallifreyc.extension.GallifreyFieldDeclExt;
import gallifreyc.extension.GallifreyFormalExt;
import gallifreyc.extension.GallifreyLang;
import gallifreyc.extension.GallifreyLocalDeclExt;
import gallifreyc.types.GallifreyMethodInstance;
import gallifreyc.types.GallifreyType;
import gallifreyc.types.GallifreyTypeSystem;

import java.util.*;

// move a-normalization to earlier pass, translations for transition and match
public class GallifreyRewriter extends GRewriter_c implements GRewriter {
    final String VALUE = "VALUE";
    final String RES = "RESTRICTION";
    final String TEMP = "TEMP";
    final String SHARED = "sharedObj";

    @Override
    public GallifreyLang lang() {
        return (GallifreyLang) super.lang();
    }

    @Override
    public GallifreyNodeFactory nodeFactory() {
        return (GallifreyNodeFactory) super.nodeFactory();
    }

    public GallifreyRewriter(Job job, ExtensionInfo from_ext, ExtensionInfo to_ext) {
        super(job, from_ext, to_ext);
    }

    @Override
    public TypeNode typeToJava(Type t, Position pos) {
        return super.typeToJava(t, pos);
    }

    @Override
    public Node leaveCall(Node old, Node n, NodeVisitor v) throws SemanticException {
        return super.leaveCall(old, n, v);
    }

    private MethodDecl genRestrictionMethod(MethodInstance i) {
        GallifreyNodeFactory nf = (GallifreyNodeFactory) nodeFactory();
        GallifreyTypeSystem ts = (GallifreyTypeSystem) typeSystem();
        GallifreyMethodInstance mi = (GallifreyMethodInstance) i;

        Position p = Position.COMPILER_GENERATED;

        List<Expr> args = new ArrayList<>();
        List<Formal> formals = new ArrayList<>();
        for (Type t : mi.formalTypes()) {
            String fresh = lang().freshVar();
            Id name = nf.Id(p, fresh);
            Formal f = nf.Formal(p, Flags.NONE, nf.CanonicalTypeNode(p, t), (Id) name.copy());
            formals.add(f);
            // TODO varargs
            args.add(nf.Local(p, (Id) name.copy()));
        }

        List<Stmt> methodStmts = new ArrayList<>();
        Type returnType = mi.returnType();

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
            methodStmts.add(nf.Return(p, nf.Cast(p, nf.CanonicalTypeNode(p, returnType), call)));
        }

        List<TypeNode> throwTypes = new ArrayList<>();
        for (Type t : mi.throwTypes()) {
            throwTypes.add(nf.CanonicalTypeNode(p, t));
        }

        List<ParamTypeNode> paramTypes = new ArrayList<>();
        // TODO unsure how to handle these

        return nf.MethodDecl(p, Flags.PUBLIC, new ArrayList<AnnotationElem>(), nf.CanonicalTypeNode(p, returnType),
                nf.Id(p, mi.name()), formals, throwTypes, nf.Block(p, methodStmts), paramTypes, nf.Javadoc(p, ""));
    }

    private ClassDecl genRestrictionClass(RestrictionDecl r) {
        // restriction R for C
        GallifreyNodeFactory nf = (GallifreyNodeFactory) nodeFactory();
        GallifreyTypeSystem ts = (GallifreyTypeSystem) typeSystem();

        TypeNode CTypeNode = r.forClass();

        Position p = Position.COMPILER_GENERATED;
        TypeNode sharedT = nf.TypeNodeFromQualifiedName(p, "SharedObject");

        List<ClassMember> sharedMembers = new ArrayList<>();
        // public SharedObject SHARED;
        FieldDecl f = nf.FieldDecl(p, Flags.PUBLIC, sharedT, nf.Id(p, SHARED));

        // private static final long serialVersionUID = 1;
        FieldDecl f2 = nf.FieldDecl(p, Flags.PRIVATE.Static().Final(), nf.CanonicalTypeNode(p, ts.Long()),
                nf.Id(p, "serialVersionUID"), nf.IntLit(p, IntLit.INT, 1));

        List<Formal> constructorFormals = new ArrayList<>();
        // public R(C obj)
        constructorFormals.add(nf.Formal(p, Flags.NONE, (TypeNode) CTypeNode.copy(), nf.Id(p, "obj")));

        List<Stmt> constructorStmts = new ArrayList<>();
        // this.SHARED = new SharedObject(obj);
        Expr constructorRHS = nf.New(p, (TypeNode) sharedT.copy(),
                new ArrayList<Expr>(Arrays.asList(nf.AmbExpr(p, nf.Id(p, "obj")))));
        constructorStmts.add(nf.Eval(p,
                nf.FieldAssign(p, nf.Field(p, nf.This(p), nf.Id(p, SHARED)), Assign.ASSIGN, constructorRHS)));

        ConstructorDecl c = nf.ConstructorDecl(p, Flags.PUBLIC, nf.Id(p, r.name()), constructorFormals,
                new ArrayList<TypeNode>(), nf.Block(p, constructorStmts), nf.Javadoc(p, ""));

        sharedMembers.add(f2);
        sharedMembers.add(f);
        sharedMembers.add(c);

        // generate overrides for all the allowed methods
        Set<String> allowedMethods = ts.getAllowedMethods(r.name());
        ClassType CType = (ClassType) CTypeNode.type();
        for (String name : allowedMethods) {
            for (MethodInstance method : CType.methodsNamed(name)) {
                sharedMembers.add(genRestrictionMethod(method));
            }
        }

        ClassBody sharedBody = nf.ClassBody(p, sharedMembers);
        // class R extends Shared implements Serializable (flags are same as C)
        ClassDecl sharedDecl = nf.ClassDecl(p, Flags.NONE, nf.Id(p, r.name()),
                nf.TypeNodeFromQualifiedName(p, "Shared"),
                new ArrayList<TypeNode>(Arrays.asList(nf.TypeNodeFromQualifiedName(p, "Serializable"))), sharedBody,
                nf.Javadoc(p, ""));

        return sharedDecl;

    }

    // wrap unique/shared refs with .value, AFTER rewriting
    private Node wrapExpr(Expr e) {
        GallifreyExprExt ext = GallifreyExprExt.ext(e);
        RefQualification q;
        q = ext.gallifreyType.qualification();
        if (q instanceof UniqueRef) {
            Expr new_e = qq().parseExpr("(%E)." + VALUE, e);
            return new_e;
        }
        return e;
    }

    private Node rewriteExpr(Node n) throws SemanticException {
        GallifreyNodeFactory nf = nodeFactory();

        // unwrap Moves
        if (n instanceof Move) {
            // move(a) ---> ((a.TEMP = a.value) == (a.value = null)) ? a.TEMP : a.TEMP
            Move m = (Move) n;
            Position p = n.position();
            Expr e = m.expr();
            GallifreyExprExt ext = GallifreyExprExt.ext(e);

            // HACK: re-wrap unique exprs inside of Moves
            if (e instanceof Field) {
                Field f = (Field) e;
                if (f.name().toString().equals(VALUE)) {
                    e = (Expr) f.target();
                }
            }

            Field tempField = nf.Field(p, e, nf.Id(p, TEMP));
            Field tempField2 = nf.Field(p, e, nf.Id(p, TEMP));
            Field tempField3 = nf.Field(p, e, nf.Id(p, TEMP));
            Field valueField = nf.Field(p, e, nf.Id(p, VALUE));
            Field valueField2 = nf.Field(p, e, nf.Id(p, VALUE));

            FieldAssign fa1 = nf.FieldAssign(p, tempField, Assign.ASSIGN, valueField);
            FieldAssign fa2 = nf.FieldAssign(p, valueField2, Assign.ASSIGN, nf.NullLit(p));

            Expr condition = nf.Binary(p, fa1, Binary.EQ, fa2);
            Expr conditional = nf.Conditional(p, condition, tempField2, tempField3);
            GallifreyExprExt condExt = GallifreyExprExt.ext(conditional);
            condExt.gallifreyType(ext.gallifreyType());
            return conditional;
        }

        // add explicit casts to restriction class for Shared objects
        if (n instanceof Call) {
            Call c = (Call) n;
            if (c.target() instanceof Expr) {
                GallifreyType t = GallifreyExprExt.ext(c.target()).gallifreyType();
                if (t.qualification() instanceof SharedRef) {
                    String restriction = ((SharedRef) t.qualification()).restriction().restriction().id();
                    Expr newTarget = nf.Cast(n.position(),
                            nf.TypeNodeFromQualifiedName(Position.COMPILER_GENERATED, restriction), (Expr) c.target());
                    return c.target(newTarget);
                }
            }
            return c;
        }
        return n;
    }

    private Node rewriteStmt(Node n) throws SemanticException {
        if (n instanceof LocalDecl) {
            // rewrite RHS of decls
            LocalDecl l = (LocalDecl) n;
            GallifreyLocalDeclExt lde = (GallifreyLocalDeclExt) GallifreyExt.ext(l);
            Expr rhs = l.init();
            RefQualification q = lde.qualification();
            // shared[R] C x = e ----> R x = new R(e);
            if (q instanceof SharedRef) {
                SharedRef s = (SharedRef) q;
                RestrictionId rid = s.restriction();
                Expr new_rhs = qq().parseExpr("new " + rid.toString() + "(%E)", rhs);
                l = l.type(nf.TypeNodeFromQualifiedName(l.position(), "Shared"));
                l = l.init(new_rhs);
                return l;
            }
            if (q instanceof UniqueRef) {
                Expr new_rhs = nf.New(rhs.position(), nf.TypeNodeFromQualifiedName(l.position(), "Unique<>"),
                        new ArrayList<Expr>(Arrays.asList(rhs)));
                l = l.type(nf.TypeNodeFromQualifiedName(l.position(), "Unique<" + l.type().type().toString() + ">"));
                l = l.init(new_rhs);
                return l;
            }
            return l;
        }
        // translate Transition to java
        if (n instanceof Transition) {
            Transition t = (Transition) n;
            Position p = t.position();
            // transition(c, R) ------> c = new R(c.SHARED);
            Assign fa = nf.Assign(p, (Local) t.expr().copy(), Assign.ASSIGN, nf.New(p,
                    nf.TypeNodeFromQualifiedName(p, t.restriction().restriction().id()),
                    new ArrayList<Expr>(Arrays.asList(nf.Field(p, (Local) t.expr().copy(), nf.Id(p, SHARED))))));
            return nf.Eval(p, fa);
        }
        // translate MatchRestriction to java
        if (n instanceof MatchRestriction) {
            MatchRestriction m = (MatchRestriction) n;
            Expr e = m.expr();
            Position p = m.position();
            If currentif = null;
            List<MatchBranch> branches = new ArrayList<>(m.branches());
            Collections.reverse(branches);

            for (MatchBranch b : branches) {
                LocalDecl d = b.pattern();
                RefQualifiedTypeNode t = (RefQualifiedTypeNode) d.type();
                RestrictionId restriction = ((SharedRef) t.qualification()).restriction();

                Expr field = nf.Field(p, e, nf.Id(p, RES));
                Expr cond = nf.Binary(p, field, Binary.EQ, nf.StringLit(p, restriction.restriction().toString()));

                Block block = nf.Block(p, d.init(e), b.stmt());

                if (currentif != null) {
                    If i = nf.If(p, cond, block, currentif);
                    currentif = i;
                } else {
                    If i = nf.If(p, cond, block);
                    currentif = i;
                }
            }
            return currentif;
        }
        return n;
    }

    public Node rewrite(Node n) throws SemanticException {
        if (n instanceof Expr) {
            Expr e = (Expr) rewriteExpr(n);
            return wrapExpr(e);
        }

        if (n instanceof Stmt && !(n instanceof Block)) {
            Stmt s = (Stmt) rewriteStmt(n);
            return s;
        }

        if (n instanceof Formal) {
            Formal f = (Formal) n;
            GallifreyFormalExt ext = (GallifreyFormalExt) GallifreyExt.ext(n);
            RefQualification q = ext.qualification;
            if (q instanceof UniqueRef) {
                f = f.type(nf.TypeNodeFromQualifiedName(f.position(), "Unique<" + f.type().type().toString() + ">"));
            }
            return f;
        }

        if (n instanceof FieldDecl) {
            FieldDecl f = (FieldDecl) n;
            GallifreyFieldDeclExt fde = (GallifreyFieldDeclExt) GallifreyExt.ext(f);
            RefQualification q = fde.qualification();
            if (q instanceof UniqueRef) {
                return f.type(nf.TypeNodeFromQualifiedName(f.position(), "Unique<" + f.type().type().toString() + ">"));
            }
        }

        // add Unique and Shared decls
        if (n instanceof SourceFile) {
            NodeFactory nf = nodeFactory();
            SourceFile sf = (SourceFile) n;

            List<Import> imports = new ArrayList<>(sf.imports());
            imports.add(0, nf.Import(n.position(), Import.SINGLE_TYPE, "gallifrey.Unique"));
            imports.add(0, nf.Import(n.position(), Import.SINGLE_TYPE, "gallifrey.Shared"));
            imports.add(0, nf.Import(n.position(), Import.SINGLE_TYPE, "gallifrey.SharedObject"));
            imports.add(nf.Import(n.position(), Import.SINGLE_TYPE, "java.io.Serializable"));
            imports.add(nf.Import(n.position(), Import.SINGLE_TYPE, "java.util.Arrays"));
            imports.add(nf.Import(n.position(), Import.SINGLE_TYPE, "java.util.ArrayList"));

            // remove restriction decls
            List<TopLevelDecl> classDecls = new ArrayList<>();
            List<RestrictionDecl> restrictionDecls = new ArrayList<>();
            for (TopLevelDecl d : sf.decls()) {
                if (d instanceof ClassDecl) {
                    classDecls.add(d);
                } else {
                    restrictionDecls.add((RestrictionDecl) d);
                }
            }

            for (RestrictionDecl rd : restrictionDecls) {
                // TODO
                classDecls.add(genRestrictionClass(rd));
            }

            return sf.imports(imports).decls(classDecls);
        }

        return n;
    }

    public NodeVisitor rewriteEnter(Node n) throws SemanticException {
        return n.extRewriteEnter(this);
    }
}