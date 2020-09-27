package gallifreyc.extension;

import polyglot.types.SemanticException;
import polyglot.util.Position;
import polyglot.util.SerialVersionUID;
import polyglot.visit.NodeVisitor;
import polyglot.visit.TypeBuilder;
import polyglot.visit.TypeChecker;
import gallifreyc.ast.GallifreyNodeFactory;
import gallifreyc.ast.LocalRef;
import gallifreyc.ast.RefQualification;
import gallifreyc.ast.RefQualifiedTypeNode;
import gallifreyc.ast.RestrictionId;
import gallifreyc.ast.SharedRef;
import gallifreyc.ast.UnknownRef;
import gallifreyc.translate.GallifreyRewriter;
import gallifreyc.types.GallifreyFieldInstance;
import gallifreyc.types.GallifreyType;
import gallifreyc.types.GallifreyTypeSystem;
import polyglot.ast.CanonicalTypeNode;
import polyglot.ast.FieldDecl;
import polyglot.ast.Node;
import polyglot.ast.TypeNode;

public class GallifreyFieldDeclExt extends GallifreyExt implements GallifreyOps {
    private static final long serialVersionUID = SerialVersionUID.generate();

    public RefQualification qualification = new UnknownRef(Position.COMPILER_GENERATED);

    @Override
    public FieldDecl node() {
        return (FieldDecl) super.node();
    }

    @Override
    public Node gallifreyRewrite(GallifreyRewriter rw) throws SemanticException {
        FieldDecl f = node();
        GallifreyNodeFactory nf = rw.nodeFactory();
        RefQualification q = this.qualification;
        if (q.isUnique()) {
            return f.type(nf.TypeNode(f.position(), "Unique<" + f.type().type().toString() + ">"));
        }
        if (q.isShared()) {
            SharedRef s = (SharedRef) q;
            RestrictionId rid = s.restriction();
            return f.type(rw.getFormalTypeNode(rid));
        }
        return f;
    }

    @Override
    public NodeVisitor buildTypesEnter(TypeBuilder tb) throws SemanticException {
        return superLang().buildTypesEnter(node(), tb);
    }

    @Override
    public Node buildTypes(TypeBuilder tb) throws SemanticException {
        FieldDecl node = (FieldDecl) superLang().buildTypes(node(), tb);
        TypeNode t = node().type();
        RefQualification q;
        if (t instanceof RefQualifiedTypeNode) {
            q = ((RefQualifiedTypeNode) t).qualification();
        } else {
            // default to local for unqualified (incl primitives)
            q = new LocalRef(Position.COMPILER_GENERATED);
        }
        this.qualification = q;
        GallifreyFieldInstance fi = (GallifreyFieldInstance) node.fieldInstance();
        fi.gallifreyType(new GallifreyType(q));
        return node;
    }

    @Override
    public Node typeCheck(TypeChecker tc) throws SemanticException {
        FieldDecl node = (FieldDecl) superLang().typeCheck(node(), tc);
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

    public RefQualification qualification() {
        return qualification;
    }
}
