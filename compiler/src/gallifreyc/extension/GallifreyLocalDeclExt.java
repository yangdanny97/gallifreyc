package gallifreyc.extension;

import gallifreyc.ast.GallifreyNodeFactory;
import gallifreyc.ast.LocalRef;
import gallifreyc.ast.RefQualification;
import gallifreyc.ast.RefQualifiedTypeNode;
import gallifreyc.ast.RestrictionId;
import gallifreyc.ast.SharedRef;
import gallifreyc.ast.UnknownRef;
import gallifreyc.translate.GallifreyCodegenRewriter;
import gallifreyc.types.GallifreyLocalInstance;
import gallifreyc.types.GallifreyType;
import gallifreyc.types.GallifreyTypeSystem;
import polyglot.ast.Cast;
import polyglot.ast.Expr;
import polyglot.ast.LocalDecl;
import polyglot.ast.Node;
import polyglot.ast.TypeNode;
import polyglot.types.SemanticException;
import polyglot.types.Type;
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
        return superLang().buildTypesEnter(node(), tb);
    }

    @Override
    public Node buildTypes(TypeBuilder tb) throws SemanticException {
        LocalDecl node = (LocalDecl) superLang().buildTypes(node(), tb);
        TypeNode t = node.type();
        RefQualification q;
        if (t instanceof RefQualifiedTypeNode) {
            q = ((RefQualifiedTypeNode) t).qualification();
        } else {
            // unqualified defaults to local (incl primitives)
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
    public Node gallifreyRewrite(GallifreyCodegenRewriter rw) throws SemanticException {
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
                l = l.init(this.maybeCast(nf, l.position(), l.type(), rw.rewriteRHS(rid, rhs)));
            }
            return l;
        } else if (rhs != null && rhs.type() != null && l.type().type() != null
                && ts.isCastValid(rhs.type(), l.type().type())) {
            l = l.init(this.maybeCast(nf, l.position(), l.type(), rhs));
        }
        return l;
    }
    
    // avoid double casting on RHS if there is already a cast with the exact type we want to cast to
    private Expr maybeCast(GallifreyNodeFactory nf, Position pos, TypeNode t, Expr rhs) {
        if (rhs instanceof Cast) {
            Cast c = (Cast) rhs;
            if (t.type() != null && c.type().typeEquals(t.type())) {
                return rhs;
            }
        }
        return nf.Cast(pos, t, rhs);
    }

    public RefQualification qualification() {
        return qualification;
    }
}
