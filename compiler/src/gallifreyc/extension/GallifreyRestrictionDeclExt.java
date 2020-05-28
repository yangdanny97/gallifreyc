package gallifreyc.extension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import gallifreyc.ast.GallifreyNodeFactory;
import gallifreyc.ast.RestrictionDecl;
import gallifreyc.translate.GallifreyRewriter;
import gallifreyc.types.GallifreyTypeSystem;
import polyglot.ast.Assign;
import polyglot.ast.ClassBody;
import polyglot.ast.ClassDecl;
import polyglot.ast.ClassMember;
import polyglot.ast.ConstructorDecl;
import polyglot.ast.Expr;
import polyglot.ast.FieldDecl;
import polyglot.ast.Formal;
import polyglot.ast.IntLit;
import polyglot.ast.Node;
import polyglot.ast.Stmt;
import polyglot.ast.TypeNode;
import polyglot.ext.jl5.ast.AnnotationElem;
import polyglot.ext.jl5.ast.ParamTypeNode;
import polyglot.types.ClassType;
import polyglot.types.Flags;
import polyglot.types.MethodInstance;
import polyglot.types.SemanticException;
import polyglot.util.Position;
import polyglot.util.SerialVersionUID;

public class GallifreyRestrictionDeclExt extends GallifreyExt {
    
    private static final long serialVersionUID = SerialVersionUID.generate();

    public GallifreyRestrictionDeclExt() {
    }
    
    @Override
    public RestrictionDecl node() {
        return (RestrictionDecl) super.node();
    }

    @Override
    public Node gallifreyRewrite(GallifreyRewriter rw) throws SemanticException {
        // generate a classDecl for each restrictionDecl
        // restriction R for C
        GallifreyNodeFactory nf = rw.nodeFactory();
        GallifreyTypeSystem ts = rw.typeSystem();

        TypeNode CTypeNode = node().forClass();
        String rName = node().name();

        Position p = Position.COMPILER_GENERATED;
        TypeNode sharedT = nf.TypeNodeFromQualifiedName(p, "SharedObject");

        List<ClassMember> sharedMembers = new ArrayList<>();
        // public SharedObject SHARED;
        FieldDecl f = nf.FieldDecl(p, Flags.PUBLIC, sharedT, nf.Id(p, rw.SHARED));

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
                nf.FieldAssign(p, nf.Field(p, nf.This(p), nf.Id(p, rw.SHARED)), Assign.ASSIGN, constructorRHS)));

        ConstructorDecl c = nf.ConstructorDecl(p, Flags.PUBLIC, nf.Id(p, rName), constructorFormals,
                new ArrayList<TypeNode>(), nf.Block(p, constructorStmts), nf.Javadoc(p, ""));
        
        // SECOND CONSTRUCTOR (FOR TRANSITIONS)
        
        List<Formal> constructorFormals2 = new ArrayList<>();
        // public R(SharedObject obj)
        constructorFormals2.add(nf.Formal(p, Flags.NONE, nf.TypeNodeFromQualifiedName(p, "SharedObject"), nf.Id(p, "obj")));
        
        List<Stmt> constructorStmts2 = new ArrayList<>();
        // this.SHARED = obj;
        constructorStmts2.add(nf.Eval(p,
                nf.FieldAssign(p, nf.Field(p, nf.This(p), nf.Id(p, rw.SHARED)), Assign.ASSIGN, nf.AmbExpr(p, nf.Id(p, "obj")))));

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
                sharedMembers.add(rw.genRestrictionMethod(method));
            }
        }
        
        // getter for sharedObj field
        List<Formal> formals = new ArrayList<>();
        List<TypeNode> throwTypes = new ArrayList<>();
        List<ParamTypeNode> paramTypes = new ArrayList<>();
        List<Stmt> methodStmts = new ArrayList<>();
        methodStmts.add(nf.Return(p, nf.Field(p, nf.This(p), "sharedObj")));
        
        sharedMembers.add(nf.MethodDecl(p, Flags.PUBLIC, new ArrayList<AnnotationElem>(), 
                nf.TypeNodeFromQualifiedName(p, "SharedObject"), nf.Id(p, "sharedObj"), 
                formals, throwTypes, nf.Block(p, methodStmts), paramTypes, nf.Javadoc(p, "")));

        ClassBody sharedBody = nf.ClassBody(p, sharedMembers);
        // class R extends Shared implements Serializable (flags are same as C)
        ClassDecl sharedDecl = nf.ClassDecl(p, Flags.NONE, nf.Id(p, rName),
                nf.TypeNodeFromQualifiedName(p, "Shared"),
                new ArrayList<TypeNode>(Arrays.asList(nf.TypeNodeFromQualifiedName(p, "Serializable"))), sharedBody,
                nf.Javadoc(p, ""));

        return sharedDecl;
    }

}
