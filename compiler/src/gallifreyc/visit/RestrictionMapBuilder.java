package gallifreyc.visit;

import java.util.HashMap;
import java.util.Map;

import polyglot.ast.NodeFactory;
import polyglot.frontend.Job;
import polyglot.types.TypeSystem;
import polyglot.visit.ContextVisitor;

public class RestrictionMapBuilder extends ContextVisitor {

	public Map<String, String> restrictionMap;
	
	public RestrictionMapBuilder(Job job, TypeSystem ts, NodeFactory nf) {
		super(job, ts, nf);
		restrictionMap = new HashMap<>();
	}
	
	

}
