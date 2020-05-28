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

    public List<ClassDecl> generatedClasses = new ArrayList<>();

    public GallifreyRewriter(Job job, ExtensionInfo from_ext, ExtensionInfo to_ext) {
        super(job, from_ext, to_ext);
    }

    @Override
    public TypeNode typeToJava(Type t, Position pos) {
        return super.typeToJava(t, pos);
    }

    public MethodDecl genRestrictionMethodSignature(MethodInstance i) {
        return (MethodDecl) this.genRestrictionMethod(i).body(null);
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
        TypeNode genReturnType = nf.CanonicalTypeNode(p, returnType);

        // don't allow type vars
        if (returnType instanceof TypeVariable) {
            genReturnType = nf.TypeNodeFromQualifiedName(p, "Object");
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
            methodStmts.add(nf.Return(p, nf.Cast(p, nf.CanonicalTypeNode(p, returnType), call)));
        }

        List<TypeNode> throwTypes = new ArrayList<>();
        for (Type t : mi.throwTypes()) {
            throwTypes.add(nf.CanonicalTypeNode(p, t));
        }

        List<ParamTypeNode> paramTypes = new ArrayList<>();
        // TODO unsure how to handle these

        return nf.MethodDecl(p, Flags.PUBLIC, new ArrayList<AnnotationElem>(), genReturnType, nf.Id(p, mi.name()),
                formals, throwTypes, nf.Block(p, methodStmts), paramTypes,
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

        ConstructorDecl c = nf.ConstructorDecl(p, Flags.PUBLIC, nf.Id(p, rName), constructorFormals,
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

        ConstructorDecl c2 = nf.ConstructorDecl(p, Flags.PUBLIC, nf.Id(p, rName), constructorFormals2,
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
                sharedMembers.add(this.genRestrictionMethod(method));
            }
        }

        // getter for sharedObj field
        List<Formal> formals = new ArrayList<>();
        List<TypeNode> throwTypes = new ArrayList<>();
        List<ParamTypeNode> paramTypes = new ArrayList<>();
        List<Stmt> methodStmts = new ArrayList<>();
        methodStmts.add(nf.Return(p, nf.Field(p, nf.This(p), nf.Id(p, "sharedObj"))));

        sharedMembers.add(nf.MethodDecl(p, Flags.PUBLIC, new ArrayList<AnnotationElem>(),
                nf.TypeNodeFromQualifiedName(p, "SharedObject"), nf.Id(p, "sharedObj"), formals, throwTypes,
                nf.Block(p, methodStmts), paramTypes, nf.Javadoc(p, "")));

        List<TypeNode> interfaces = new ArrayList<>();
        interfaces.add(nf.TypeNodeFromQualifiedName(p, "Serializable"));
        interfaces.add(nf.TypeNodeFromQualifiedName(p, "Shared"));
        // TODO add more restrictions

        ClassBody sharedBody = nf.ClassBody(p, sharedMembers);

        // class R extends Shared implements Serializable (flags are same as C)
        ClassDecl sharedDecl = nf.ClassDecl(p, Flags.NONE, nf.Id(p, rName), null, interfaces, sharedBody,
                nf.Javadoc(p, "// Concrete restriction class for " + rName));

        return sharedDecl;
    }

    // interface R extends Shared {...}
    public ClassDecl genRestrictionInterface(RestrictionDecl d) {
        return null;
    }

    // interface RV_holder {...}
    public ClassDecl genRVHolderInterface(RestrictionDecl d) {
        return null;
    }

    // class RV {...}
    public ClassDecl genRVClass(RestrictionDecl d) {
        return null;
    }

    // class RV_R extends RV_holder, Shared {...}
    public ClassDecl genRVSubrestrictionInterface(RestrictionDecl d) {
        return null;
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
        return GallifreyExt.ext(n).gallifreyRewrite(this);
    }
}