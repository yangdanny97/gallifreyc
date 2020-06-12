package gallifreyc.extension;

import polyglot.ast.ClassDeclOps;
import polyglot.ast.ClassMember;
import polyglot.ast.Expr;
import polyglot.ast.Field;
import polyglot.ast.FieldAssign;
import polyglot.ast.FieldDecl;
import polyglot.ast.Formal;
import polyglot.ast.Initializer;
import polyglot.ast.IntLit;
import polyglot.ast.MethodDecl;

import java.util.ArrayList;
import java.util.List;

import gallifreyc.ast.GallifreyNodeFactory;
import gallifreyc.ast.RefQualification;
import gallifreyc.translate.FieldInitRewriter;
import gallifreyc.translate.GallifreyRewriter;
import gallifreyc.types.GallifreyFieldInstance;
import gallifreyc.types.GallifreyType;
import gallifreyc.types.GallifreyTypeSystem;
import polyglot.ast.AmbTypeNode;
import polyglot.ast.ArrayTypeNode;
import polyglot.ast.Assign;
import polyglot.ast.CanonicalTypeNode;
import polyglot.ast.ClassBody;
import polyglot.ast.ClassDecl;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.Stmt;
import polyglot.ast.TypeNode;
import polyglot.ext.jl5.ast.ParamTypeNode;
import polyglot.ext.jl5.types.TypeVariable;
import polyglot.types.ConstructorInstance;
import polyglot.types.Flags;
import polyglot.types.InitializerInstance;
import polyglot.types.PrimitiveType;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.util.CodeWriter;
import polyglot.util.Position;
import polyglot.util.SerialVersionUID;
import polyglot.visit.PrettyPrinter;

public class GallifreyClassDeclExt extends GallifreyExt implements ClassDeclOps {
    private static final long serialVersionUID = SerialVersionUID.generate();

    public GallifreyClassDeclExt() {
    }

    @Override
    public ClassDecl node() {
        return (ClassDecl) super.node();
    }

    @Override
    public void prettyPrintHeader(CodeWriter w, PrettyPrinter tr) {
        superLang().prettyPrintHeader(node(), w, tr);
    }

    @Override
    public void prettyPrintFooter(CodeWriter w, PrettyPrinter tr) {
        superLang().prettyPrintFooter(node(), w, tr);
    }

    @Override
    public Node addDefaultConstructor(TypeSystem ts, NodeFactory nf, ConstructorInstance defaultConstructorInstance)
            throws SemanticException {
        return superLang().addDefaultConstructor(node(), ts, nf, defaultConstructorInstance);
    }

    @Override
    public Node rewriteFieldInits(FieldInitRewriter rw) throws SemanticException {
        GallifreyNodeFactory nf = rw.nodeFactory();
        if (node().flags().isInterface()) {
            return super.rewriteFieldInits(rw);
        }
        ClassBody b = (ClassBody) node().body().copy();
        List<ClassMember> members = new ArrayList<>(b.members());
        List<ClassMember> newMembers = new ArrayList<>();
        List<Stmt> hoistedDecls = new ArrayList<>();
        for (ClassMember member : members) {
            if (member instanceof FieldDecl) {
                FieldDecl f = (FieldDecl) member;
                RefQualification q = ((GallifreyFieldDeclExt) GallifreyExt.ext(f)).qualification();
                Position p = f.position();
                // only hoist field decls with inits
                if (f.init() != null) {
                    // new assignment LHS
                    Field field = nf.Field(p, nf.This(p), nf.Id(p, f.name()));
                    GallifreyExprExt thisExt = GallifreyExprExt.ext(field);
                    thisExt.gallifreyType(new GallifreyType(q));
                    GallifreyFieldInstance fi = (GallifreyFieldInstance) rw.typeSystem().fieldInstance(p, node().type(),
                            Flags.NONE, f.init().type(), f.name());
                    fi.gallifreyType(new GallifreyType(q));
                    field = (Field) field.fieldInstance(fi).type(f.declType());
                    // add assignment
                    FieldAssign fa = nf.FieldAssign(p, field, Assign.ASSIGN, f.init());
                    GallifreyExprExt faExt = GallifreyExprExt.ext(fa);
                    faExt.gallifreyType(new GallifreyType(q));
                    hoistedDecls.add(nf.Eval(p, fa));
                    // remove inits
                    member = f.init(null);
                    GallifreyFieldDeclExt ext = (GallifreyFieldDeclExt) GallifreyExt.ext(member);
                    ext.qualification = q;
                }
            }
            newMembers.add(member);
        }
        if (hoistedDecls.size() > 0) {
            Initializer i = nf.Initializer(node().position(), Flags.NONE, nf.Block(node().position(), hoistedDecls));
            InitializerInstance ii = rw.typeSystem().initializerInstance(node().position(), node().type(), Flags.NONE);
            i = i.initializerInstance(ii);
            newMembers.add(i);
            ClassBody newB = b.members(newMembers);
            return node().body(newB);
        }
        return node();
    }

    @Override
    public Node gallifreyRewrite(GallifreyRewriter rw) throws SemanticException {
        // add CRDT fields and serialVersionUID to class decls
        ClassDecl cd = node();
        GallifreyTypeSystem ts = rw.typeSystem();
        GallifreyNodeFactory nf = rw.nodeFactory();
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
                List<Expr> elements = new ArrayList<>();
                for (Formal f : md.formals()) {
                    TypeNode t = f.type();
                    if (t instanceof ParamTypeNode) {
                        elements.add(nf.Field(p, nf.TypeNodeFromQualifiedName(p, "Object"), nf.Id(p, "class")));
                    } else if (t instanceof ArrayTypeNode) {
                        elements.add(nf.Field(p, (TypeNode) t.copy(), nf.Id(p, "class")));
                    } else if (t instanceof CanonicalTypeNode) {
                        Type ctype = ((CanonicalTypeNode) t).type();
                        // use wrapper types for primitives
                        if (ctype.isPrimitive()) {
                            elements.add(nf.Field(p,
                                    nf.CanonicalTypeNode(p, ts.wrapperClassOfPrimitive((PrimitiveType) ctype)),
                                    nf.Id(p, "class")));
                        } else if (ctype instanceof TypeVariable) {
                            elements.add(nf.Field(p, nf.TypeNodeFromQualifiedName(p, "Object"), nf.Id(p, "class")));
                        } else {
                            elements.add(nf.Field(p, (TypeNode) t.copy(), nf.Id(p, "class")));
                        }

                    } else if (t instanceof AmbTypeNode && !t.name().contains("<")) {
                        // non-parameterized type nodes generated by us in a previous pass
                        elements.add(nf.Field(p, (TypeNode) t.copy(), nf.Id(p, "class")));
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
}
