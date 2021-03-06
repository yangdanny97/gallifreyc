package gallifreyc.translate;

import gallifreyc.ast.GallifreyNodeFactory;
import gallifreyc.extension.GallifreyExprExt;
import gallifreyc.extension.GallifreyExt;
import gallifreyc.extension.GallifreyFieldDeclExt;
import gallifreyc.extension.GallifreyFormalExt;
import gallifreyc.extension.GallifreyLang;
import gallifreyc.extension.GallifreyLocalDeclExt;
import gallifreyc.types.GallifreyTypeSystem;
import polyglot.ast.Expr;
import polyglot.ast.FieldDecl;
import polyglot.ast.Formal;
import polyglot.ast.LocalDecl;
import polyglot.ast.Node;
import polyglot.frontend.ExtensionInfo;
import polyglot.frontend.Job;
import polyglot.translate.ExtensionRewriter;
import polyglot.types.SemanticException;
import polyglot.visit.NodeVisitor;

// parent class for Gallifrey AST rewriters, which contains some sanity checks to make sure
// the additional typing info is not lost
public abstract class GallifreyRewriter extends ExtensionRewriter {

    public GallifreyRewriter(Job job, ExtensionInfo from_ext, ExtensionInfo to_ext) {
        super(job, from_ext, to_ext);
    }

    @Override
    public GallifreyLang lang() {
        return (GallifreyLang) super.lang();
    }

    @Override
    public GallifreyNodeFactory nodeFactory() {
        return (GallifreyNodeFactory) super.nodeFactory();
    }

    @Override
    public GallifreyTypeSystem typeSystem() {
        return (GallifreyTypeSystem) super.typeSystem();
    }

    public abstract Node extRewrite(Node n) throws SemanticException;

    @Override
    public Node leaveCall(Node old, Node n, NodeVisitor v) throws SemanticException {
        Node m = extRewrite(n);

        // sanity checks

        if (m instanceof Expr) {
            GallifreyExprExt extM = GallifreyExprExt.ext(m);

            // HACK: attach n's gallifreyType to m so it doesn't disappear
            GallifreyExprExt extN = GallifreyExprExt.ext(n);
            extM.gallifreyType(extN.gallifreyType());

            if (extM.gallifreyType() == null) {
                throw new IllegalArgumentException("no gallifrey type found");
            }
            if (extM.gallifreyType().qualification().isUnknown()) {
                throw new IllegalArgumentException("invalid qualification: " + m);
            }

        }
        if (m instanceof FieldDecl) {
            GallifreyFieldDeclExt extM = (GallifreyFieldDeclExt) GallifreyExt.ext(m);

            GallifreyFieldDeclExt extN = (GallifreyFieldDeclExt) GallifreyExt.ext(n);
            extM.qualification = extN.qualification();

            if (extM.qualification == null || extM.qualification().isUnknown()) {
                throw new IllegalArgumentException("invalid qualification: " + m);
            }
        }
        if (m instanceof LocalDecl) {
            GallifreyLocalDeclExt extM = (GallifreyLocalDeclExt) GallifreyExt.ext(m);

            GallifreyLocalDeclExt extN = (GallifreyLocalDeclExt) GallifreyExt.ext(n);
            extM.qualification = extN.qualification();

            if (extM.qualification == null || extM.qualification().isUnknown()) {
                throw new IllegalArgumentException("invalid qualification " + m);
            }
        }
        if (m instanceof Formal) {
            GallifreyFormalExt extM = (GallifreyFormalExt) GallifreyExt.ext(m);

            GallifreyFormalExt extN = (GallifreyFormalExt) GallifreyExt.ext(n);
            extM.qualification = extN.qualification;

            if (extM.qualification == null || extM.qualification.isUnknown()) {
                throw new IllegalArgumentException("invalid qualification " + m);
            }
        }

        return m;
    }

}
