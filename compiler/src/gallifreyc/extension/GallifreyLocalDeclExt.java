package gallifreyc.extension;

import java.util.ArrayList;
import java.util.Arrays;

import gallifreyc.ast.GallifreyNodeFactory;
import gallifreyc.ast.LocalRef;
import gallifreyc.ast.RefQualification;
import gallifreyc.ast.RefQualifiedTypeNode;
import gallifreyc.ast.RestrictionId;
import gallifreyc.ast.SharedRef;
import gallifreyc.ast.UnknownRef;
import gallifreyc.translate.GallifreyRewriter;
import gallifreyc.types.GallifreyLocalInstance;
import gallifreyc.types.GallifreyType;
import gallifreyc.types.GallifreyTypeSystem;
import polyglot.ast.CanonicalTypeNode;
import polyglot.ast.Expr;
import polyglot.ast.LocalDecl;
import polyglot.ast.Node;
import polyglot.ast.TypeNode;
import polyglot.types.SemanticException;
import polyglot.util.Position;
import polyglot.util.SerialVersionUID;
import polyglot.visit.NodeVisitor;
import polyglot.visit.TypeBuilder;
import polyglot.visit.TypeChecker;

public class GallifreyLocalDeclExt extends GallifreyExt implements GallifreyOps {
    private static final long serialVersionUID = SerialVersionUID.generate();

    public RefQualification qualification = new UnknownRef(Position.COMPILER_GENERATED);

    @Override
    public LocalDecl node() {
        return (LocalDecl) super.node();
    }

    @Override
    public NodeVisitor buildTypesEnter(TypeBuilder tb) throws SemanticException {
        TypeNode t = node().type();
        if (t instanceof RefQualifiedTypeNode
                || (t instanceof CanonicalTypeNode && ((CanonicalTypeNode) t).type().isPrimitive())) {
            return superLang().buildTypesEnter(node(), tb);
        }
        throw new SemanticException("cannot declare unqualified local", node().position());
    }

    @Override
    public Node buildTypes(TypeBuilder tb) throws SemanticException {
        LocalDecl node = (LocalDecl) superLang().buildTypes(node(), tb);
        TypeNode t = node.type();
        RefQualification q;
        if (t instanceof RefQualifiedTypeNode) {
            q = ((RefQualifiedTypeNode) t).qualification();
        } else {
            // for primitives
            q = new LocalRef(Position.COMPILER_GENERATED);
        }
        qualification = q;
        GallifreyLocalInstance li = (GallifreyLocalInstance) node.localInstance();
        li.gallifreyType(new GallifreyType(q));
        return node;
    }

    @Override
    public Node typeCheck(TypeChecker tc) throws SemanticException {
        LocalDecl node = (LocalDecl) superLang().typeCheck(node(), tc);
        GallifreyTypeSystem ts = (GallifreyTypeSystem) tc.typeSystem();
        if (node.init() != null) {
            GallifreyType lt = new GallifreyType(qualification);
            GallifreyType rt = GallifreyExprExt.ext(node.init()).gallifreyType();

            if (!ts.checkQualifications(rt, lt)) {
                throw new SemanticException("cannot assign " + rt.qualification + " to " + lt.qualification,
                        node().position());
            }
        }
        return node;
    }

    @Override
    public Node gallifreyRewrite(GallifreyRewriter rw) throws SemanticException {
        // rewrite RHS of decls
        GallifreyNodeFactory nf = rw.nodeFactory();
        GallifreyTypeSystem ts = rw.typeSystem();

        LocalDecl l = node();
        Expr rhs = l.init();
        RefQualification q = this.qualification();
        // shared[R] C x = e ----> R x = new R(e);
        if (q.isShared()) {
            SharedRef s = (SharedRef) q;
            RestrictionId rid = s.restriction();
            l = l.type(rw.getFormalTypeNode(rid));
            if (rhs != null) {
                l = l.init(nf.Cast(l.position(), l.type(), rw.rewriteRHS(rid, rhs)));
            }
            return l;
        } else if (q.isUnique()) {
            l = l.type(nf.TypeNode(l.position(), "Unique<" + l.type().type().toString() + ">"));
            if (rhs != null) {
                l = l.init(nf.New(rhs.position(), nf.TypeNode(l.position(), "Unique<>"),
                        new ArrayList<Expr>(Arrays.asList(rhs))));
            }
            return l;
        } else if (rhs != null && rhs.type() != null && l.type().type() != null
                && ts.isCastValid(rhs.type(), l.type().type())) {
            l = l.init(nf.Cast(l.position(), l.type(), rhs));
        }
        return l;
    }

    public RefQualification qualification() {
        return qualification;
    }
}
