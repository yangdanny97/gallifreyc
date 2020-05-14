package gallifreyc.visit;

import java.util.List;

import gallifreyc.ast.RestrictionId;
import gallifreyc.ast.UnknownRef;
import gallifreyc.extension.GallifreyExprExt;
import gallifreyc.extension.GallifreyExt;
import gallifreyc.extension.GallifreyFieldDeclExt;
import gallifreyc.extension.GallifreyFormalExt;
import gallifreyc.extension.GallifreyLocalDeclExt;
import polyglot.ast.Expr;
import polyglot.ast.FieldDecl;
import polyglot.ast.Formal;
import polyglot.ast.LocalDecl;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.TypeNode;
import polyglot.frontend.Job;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.visit.NodeVisitor;
import polyglot.visit.TypeChecker;

// overwrite any typechecker operations
public class GallifreyTypeChecker extends TypeChecker {
    public Type currentRestrictionClass;
    public String currentRestriction;

    public GallifreyTypeChecker(Job job, TypeSystem ts, NodeFactory nf) {
        super(job, ts, nf);
        currentRestriction = null;
        currentRestrictionClass = null;
    }

    @Override
    protected Node leaveCall(Node old, Node n, NodeVisitor v) throws SemanticException {
        // sanity checks
        Node m = super.leaveCall(old, n, v);
        if (m instanceof Expr) {
            GallifreyExprExt extM = GallifreyExprExt.ext(m);

            // HACK: attach n's gallifreyType to m so it doesn't disappear
            GallifreyExprExt extN = GallifreyExprExt.ext(n);
            extM.gallifreyType(extN.gallifreyType());

            if (extM.gallifreyType() == null) {
                throw new IllegalArgumentException("no gallifrey type found");
            }
            if (extM.gallifreyType().qualification() instanceof UnknownRef) {
                throw new IllegalArgumentException("invalid qualification " + m);
            }

        }
        if (m instanceof FieldDecl) {
            GallifreyFieldDeclExt extM = (GallifreyFieldDeclExt) GallifreyExt.ext(m);

            GallifreyFieldDeclExt extN = (GallifreyFieldDeclExt) GallifreyExt.ext(n);
            extM.qualification = extN.qualification();
            
            if (extM.qualification == null || extM.qualification() instanceof UnknownRef) {
                throw new IllegalArgumentException("invalid qualification " + m);
            }
        }
        if (m instanceof LocalDecl) {
            GallifreyLocalDeclExt extM = (GallifreyLocalDeclExt) GallifreyExt.ext(m);

            GallifreyLocalDeclExt extN = (GallifreyLocalDeclExt) GallifreyExt.ext(n);
            extM.qualification = extN.qualification();
            
            if (extM.qualification == null || extM.qualification() instanceof UnknownRef) {
                throw new IllegalArgumentException("invalid qualification " + m);
            }
        }
        if (m instanceof Formal) {
            GallifreyFormalExt extM = (GallifreyFormalExt) GallifreyExt.ext(m);

            GallifreyFormalExt extN = (GallifreyFormalExt) GallifreyExt.ext(n);
            extM.qualification = extN.qualification;
            
            if (extM.qualification == null || extM.qualification instanceof UnknownRef) {
                throw new IllegalArgumentException("invalid qualification "+ m);
            }
        }

        return m;
    }
}
