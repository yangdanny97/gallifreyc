package gallifreyc.visit;

import java.util.HashMap;
import java.util.Map;

import polyglot.ast.NodeFactory;
import polyglot.frontend.Job;
import polyglot.types.TypeSystem;
import polyglot.visit.ContextVisitor;

// build a map of each Restriction and the Class they are meant for
public class RestrictionMapBuilder extends ContextVisitor {

	public Map<String, String> restrictionMap;
	
	public RestrictionMapBuilder(Job job, TypeSystem ts, NodeFactory nf) {
		super(job, ts, nf);
		restrictionMap = new HashMap<>();
	}
	
	//TODO

}
