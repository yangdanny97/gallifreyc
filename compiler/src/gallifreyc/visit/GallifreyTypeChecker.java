package gallifreyc.visit;

import gallifreyc.ast.UnknownRef;
import gallifreyc.extension.GallifreyExprExt;
import polyglot.ast.Expr;
import polyglot.ast.Field_c;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.frontend.Job;
import polyglot.frontend.goals.Goal;
import polyglot.main.Report;
import polyglot.types.SemanticException;
import polyglot.types.TypeSystem;
import polyglot.visit.ConstantChecker;
import polyglot.visit.NodeVisitor;
import polyglot.visit.TypeChecker;

// overwrite any typechecker operations
public class GallifreyTypeChecker extends TypeChecker {
	public String currentRestrictionClass;
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
        	GallifreyExprExt ext = GallifreyExprExt.ext(m);
        	if (ext.gallifreyType() == null) {
        		throw new SemanticException("no gallifrey type found", m.position());
        	}
        	if (ext.gallifreyType().qualification() instanceof UnknownRef) {
            	if (m instanceof Field_c) {
            		System.out.println(m);
            	}
        		System.out.println(GallifreyExprExt.ext(old).gallifreyType().qualification());
        		System.out.println(GallifreyExprExt.ext(n).gallifreyType().qualification());
        		System.out.println(GallifreyExprExt.ext(m).gallifreyType().qualification());
        		throw new SemanticException("invalid qualification", m.position());
        	}
        }
        
        return m;
    }

}
