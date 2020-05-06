package gallifreyc.visit;

import gallifreyc.ast.UnknownRef;
import gallifreyc.extension.GallifreyExt;
import gallifreyc.extension.GallifreyFieldDeclExt;
import gallifreyc.extension.GallifreyLocalDeclExt;
import polyglot.ast.FieldDecl;
import polyglot.ast.LocalDecl;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.frontend.Job;
import polyglot.types.TypeSystem;
import polyglot.visit.NodeVisitor;
import polyglot.visit.TypeBuilder;

public class GallifreyTypeBuilder extends TypeBuilder {

    public GallifreyTypeBuilder(Job job, TypeSystem ts, NodeFactory nf) {
        super(job, ts, nf);
    }
    
    @Override
    public Node leave(Node old, Node n, NodeVisitor v) {
        // sanity checks
        Node m = super.leave(old, n, v);
        if (m instanceof FieldDecl) {
            GallifreyFieldDeclExt extM = (GallifreyFieldDeclExt) GallifreyExt.ext(m);

            GallifreyFieldDeclExt extN = (GallifreyFieldDeclExt) GallifreyExt.ext(n);
            extM.qualification = extN.qualification();
            
            if (extM.qualification == null || extM.qualification() instanceof UnknownRef) {
                throw new IllegalArgumentException("invalid qualification");
            }
        }
        if (m instanceof LocalDecl) {
            GallifreyLocalDeclExt extM = (GallifreyLocalDeclExt) GallifreyExt.ext(m);

            GallifreyLocalDeclExt extN = (GallifreyLocalDeclExt) GallifreyExt.ext(n);
            extM.qualification = extN.qualification();
            
            if (extM.qualification == null || extM.qualification() instanceof UnknownRef) {
                throw new IllegalArgumentException("invalid qualification");
            }
        }

        return m;
    }

}
