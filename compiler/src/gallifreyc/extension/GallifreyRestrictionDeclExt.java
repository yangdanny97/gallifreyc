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

        sharedMembers.add(f2);
        sharedMembers.add(f);
        sharedMembers.add(c);

        // generate overrides for all the allowed methods
        Set<String> allowedMethods = ts.getAllowedMethods(rName);
        ClassType CType = (ClassType) CTypeNode.type();
        for (String name : allowedMethods) {
            for (MethodInstance method : CType.methodsNamed(name)) {
                sharedMembers.add(rw.genRestrictionMethod(method));
            }
        }

        ClassBody sharedBody = nf.ClassBody(p, sharedMembers);
        // class R extends Shared implements Serializable (flags are same as C)
        ClassDecl sharedDecl = nf.ClassDecl(p, Flags.NONE, nf.Id(p, rName),
                nf.TypeNodeFromQualifiedName(p, "Shared"),
                new ArrayList<TypeNode>(Arrays.asList(nf.TypeNodeFromQualifiedName(p, "Serializable"))), sharedBody,
                nf.Javadoc(p, ""));

        return sharedDecl;
    }

}
