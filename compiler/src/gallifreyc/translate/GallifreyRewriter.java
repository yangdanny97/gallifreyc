package gallifreyc.translate;

import polyglot.ast.*;
import polyglot.ext.jl5.ast.AnnotationElem;
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
import gallifreyc.ast.*;
import gallifreyc.extension.GallifreyExprExt;
import gallifreyc.extension.GallifreyExt;
import gallifreyc.types.GallifreyMethodInstance;
import gallifreyc.types.GallifreyTypeSystem;

import java.util.*;

// move a-normalization to earlier pass, translations for transition and match
public class GallifreyRewriter extends GRewriter {
    public final String VALUE = "VALUE";
    public final String RES = "RESTRICTION";
    public final String TEMP = "TEMP";
    public final String SHARED = "sharedObj";

    public GallifreyRewriter(Job job, ExtensionInfo from_ext, ExtensionInfo to_ext) {
        super(job, from_ext, to_ext);
    }

    @Override
    public TypeNode typeToJava(Type t, Position pos) {
        return super.typeToJava(t, pos);
    }

    // add CRDT fields and serialVersionUID to class decls
    public ClassDecl rewriteDecl(ClassDecl cd) throws SemanticException {
        GallifreyTypeSystem ts = (GallifreyTypeSystem) typeSystem();
        if (!ts.canBeShared(cd.name())) {
            return cd;
        }
        ClassBody body = cd.body();
        List<ClassMember> members = new ArrayList<>(body.members());
        Position p = Position.COMPILER_GENERATED;

        // check for presence of serialVersionUID field
        boolean serializable = false;
        for (ClassMember m : body.members()) {
            if (m instanceof FieldDecl) {
                if (((FieldDecl) m).name().equals("serialVersionUID")) {
                    serializable = true;
                }
            }
            if (m instanceof MethodDecl) {
                MethodDecl md = (MethodDecl) m;
                if (md.formals().size() == 0) {
                    continue;
                }
                List<Expr> elements = new ArrayList<>();
                for (Formal f : md.formals()) {
                    TypeNode t = f.type();
                    if (t instanceof ParamTypeNode) {
                        elements.add(nf.Field(p, nf.TypeNodeFromQualifiedName(p, "Object"), nf.Id(p, "class")));
                    } else if (t instanceof ArrayTypeNode) {
                        elements.add(nf.Field(p, nf.TypeNodeFromQualifiedName(p, t.name()), nf.Id(p, "class")));
                    } else if (t instanceof CanonicalTypeNode) {
                        Type ctype = ((CanonicalTypeNode) t).type();
                        // use wrapper types for primitives
                        if (ctype.isPrimitive()) {
                            elements.add(nf.Field(p,
                                    nf.CanonicalTypeNode(p, ts.wrapperClassOfPrimitive((PrimitiveType) ctype)),
                                    nf.Id(p, "class")));
                        } else {
                            elements.add(nf.Field(p, nf.TypeNodeFromQualifiedName(p, t.name()), nf.Id(p, "class")));
                        }

                    } else {
                        elements.add(nf.Field(p, nf.TypeNodeFromQualifiedName(p, "Object"), nf.Id(p, "class")));
                    }
                }
                Expr rhs = nf.NewArray(p, nf.TypeNodeFromQualifiedName(p, "Class"), 1, nf.ArrayInit(p, elements));
                members.add(nf.FieldDecl(p, Flags.PUBLIC.Final(), nf.TypeNodeFromQualifiedName(p, "Class<?>[]"),
                        nf.Id(p, md.name()), rhs));
            }
        }

        // make sure that the class is serializable
        if (!serializable) {
            members.add(nf.FieldDecl(p, Flags.PRIVATE.Static().Final(), nf.CanonicalTypeNode(p, ts.Long()),
                    nf.Id(p, "serialVersionUID"), nf.IntLit(p, IntLit.INT, 1)));
            List<TypeNode> interfaces = new ArrayList<>(cd.interfaces());
            interfaces.add(nf.TypeNodeFromQualifiedName(p, "Serializable"));
            cd = cd.interfaces(interfaces);
        }
        body = body.members(members);
        return cd.body(body);
    }

    public MethodDecl genRestrictionMethod(MethodInstance i) {
        GallifreyNodeFactory nf = (GallifreyNodeFactory) nodeFactory();
        typeSystem();
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

    public ClassDecl genRestrictionClass(RestrictionDecl r) {
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
    public Node wrapExpr(Expr e) {
        GallifreyExprExt ext = GallifreyExprExt.ext(e);
        RefQualification q;
        q = ext.gallifreyType.qualification();
        if (q instanceof UniqueRef) {
            Expr new_e = qq().parseExpr("(%E)." + VALUE, e);
            return new_e;
        }
        return e;
    }

    @Override
    public Node extRewrite(Node n) throws SemanticException {
        if (n instanceof Expr) {
            return wrapExpr((Expr) GallifreyExt.ext(n).gallifreyRewrite(this));
        }
        return GallifreyExt.ext(n).gallifreyRewrite(this);
    }
}