package gallifreyc.visit;

import polyglot.ast.NodeFactory;
import polyglot.frontend.Job;
import polyglot.types.TypeSystem;
import polyglot.visit.TypeChecker;
import java.util.*;

public class GallifreyTypeChecker extends TypeChecker {
	public Map<String, String> restrictionMap;

	public GallifreyTypeChecker(Job job, TypeSystem ts, NodeFactory nf) {
		super(job, ts, nf);
		restrictionMap = new HashMap<>();
	}

}
