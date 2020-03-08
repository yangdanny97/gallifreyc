package gallifreyc.translate;

import polyglot.ast.TypeNode;
import polyglot.frontend.ExtensionInfo;
import polyglot.frontend.Job;
import polyglot.translate.ExtensionRewriter;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.util.Position;
import gallifreyc.types.*;

public class AssignmentRewriter extends ExtensionRewriter {

	public AssignmentRewriter(Job job, ExtensionInfo from_ext, ExtensionInfo to_ext) {
		super(job, from_ext, to_ext);
		// TODO Auto-generated constructor stub
	}
	
	// remove unique/shared annotations
	@Override 
	public TypeNode typeToJava(Type t, Position pos) {
		if (t instanceof RefQualifiedType) {
			return nf.CanonicalTypeNode(pos, ((RefQualifiedType) t).base());
		}
		return super.typeToJava(t, pos);
	}
}
