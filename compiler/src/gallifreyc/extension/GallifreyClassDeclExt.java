package gallifreyc.extension;

import polyglot.ast.ClassDeclOps;
import polyglot.ast.ClassMember;
import polyglot.ast.Field;
import polyglot.ast.FieldAssign;
import polyglot.ast.FieldDecl;
import polyglot.ast.Initializer;

import java.util.ArrayList;
import java.util.List;

import gallifreyc.ast.GallifreyNodeFactory;
import gallifreyc.ast.RefQualification;
import gallifreyc.translate.FieldInitRewriter;
import gallifreyc.types.GallifreyFieldInstance;
import gallifreyc.types.GallifreyType;
import polyglot.ast.Assign;
import polyglot.ast.ClassBody;
import polyglot.ast.ClassDecl;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.Stmt;
import polyglot.types.ConstructorInstance;
import polyglot.types.Flags;
import polyglot.types.InitializerInstance;
import polyglot.types.SemanticException;
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
                    field = field.fieldInstance(fi);
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

}
