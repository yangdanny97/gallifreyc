package gallifreyc.visit;

import polyglot.ast.NodeFactory;
import polyglot.frontend.Job;
import polyglot.types.TypeSystem;
import polyglot.visit.TypeChecker;
import java.util.*;

// overwrite any typechecker operations
public class GallifreyTypeChecker extends TypeChecker {
	public String currentRestrictionClass;
	public String currentRestriction;
	
	public GallifreyTypeChecker(Job job, TypeSystem ts, NodeFactory nf) {
		super(job, ts, nf);
		currentRestriction = null;
		currentRestrictionClass = null;
	}

}
