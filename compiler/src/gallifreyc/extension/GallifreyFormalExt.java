package gallifreyc.extension;

import polyglot.types.SemanticException;
import polyglot.util.Position;
import polyglot.util.SerialVersionUID;
import polyglot.visit.TypeBuilder;
import gallifreyc.ast.GallifreyNodeFactory;
import gallifreyc.ast.LocalRef;
import gallifreyc.ast.RefQualification;
import gallifreyc.ast.RefQualifiedTypeNode;
import gallifreyc.ast.RestrictionId;
import gallifreyc.ast.SharedRef;
import gallifreyc.ast.UnknownRef;
import gallifreyc.translate.GallifreyRewriter;
import gallifreyc.types.GallifreyLocalInstance;
import polyglot.ast.ArrayTypeNode;
import polyglot.ast.CanonicalTypeNode;
import polyglot.ast.Formal;
import polyglot.ast.Node;
import polyglot.ast.TypeNode;

public class GallifreyFormalExt extends GallifreyExt {
    private static final long serialVersionUID = SerialVersionUID.generate();

    public RefQualification qualification;

    {
        qualification = new UnknownRef(Position.COMPILER_GENERATED);
    }

    @Override
    public Formal node() {
        return (Formal) super.node();
    }

    @Override
    public Node buildTypes(TypeBuilder tb) throws SemanticException {
        Formal n = (Formal) superLang().buildTypes(this.node, tb);
        TypeNode t = n.type();
        if (!(t instanceof RefQualifiedTypeNode
                || (t instanceof CanonicalTypeNode && ((CanonicalTypeNode) t).type().isPrimitive())
                || (t instanceof ArrayTypeNode && ((ArrayTypeNode) t).base() instanceof RefQualifiedTypeNode))) {
            throw new SemanticException("declaration must have qualification", n.position());
        }
        GallifreyLocalInstance li;
        if (t instanceof RefQualifiedTypeNode) {
            RefQualifiedTypeNode rt = (RefQualifiedTypeNode) t;
            li = (GallifreyLocalInstance) n.localInstance();
            li.gallifreyType().qualification = rt.qualification();
        } else if (t instanceof ArrayTypeNode && ((ArrayTypeNode) t).base() instanceof RefQualifiedTypeNode) {
            RefQualifiedTypeNode rt = (RefQualifiedTypeNode) ((ArrayTypeNode) t).base();
            li = (GallifreyLocalInstance) n.localInstance();
            li.gallifreyType().qualification = rt.qualification();
        } else {
            li = (GallifreyLocalInstance) n.localInstance();
            li.gallifreyType().qualification = new LocalRef(Position.COMPILER_GENERATED);
        }
        qualification = li.gallifreyType().qualification;
        return n;
    }

    @Override
    public Node gallifreyRewrite(GallifreyRewriter rw) throws SemanticException {
        Formal f = node();
        GallifreyNodeFactory nf = rw.nodeFactory();
        RefQualification q = qualification;
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

}
