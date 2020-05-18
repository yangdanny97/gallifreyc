package gallifreyc.visit;

import gallifreyc.ast.GallifreyNodeFactory;
import gallifreyc.ast.UnknownRef;
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
import polyglot.ast.NodeFactory;
import polyglot.frontend.Job;
import polyglot.frontend.goals.Goal;
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

    @Override
    protected Node leaveCall(Node old, Node n, NodeVisitor v) throws SemanticException {
        // sanity checks
        Node m = super.leaveCall(old, n, v);
        boolean allGood = true;

        try {
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
                    throw new IllegalArgumentException("invalid qualification " + m);
                }
            }
        } catch (IllegalArgumentException e) {
            allGood = false;
        }

        if (!allGood) {
            Goal g = job.extensionInfo().scheduler().currentGoal();
            g.setUnreachableThisRun();
        }

        return m;
    }
}
