package gallifreyc.extension;

import polyglot.ast.ConstructorCall;
import polyglot.ast.ProcedureCall;
import polyglot.ast.ProcedureCallOps;
import polyglot.util.CodeWriter;
import polyglot.util.SerialVersionUID;
import polyglot.visit.PrettyPrinter;

/* this is the constructor call STATEMENT (this(...) or super(...)) */
public class GallifreyConstructorCallExt extends GallifreyExt implements ProcedureCallOps {
	private static final long serialVersionUID = SerialVersionUID.generate();

	public GallifreyConstructorCallExt() {}
	
    @Override
    public ConstructorCall node() {
    	return (ConstructorCall) super.node();
    }
	
	@Override
	public void printArgs(CodeWriter w, PrettyPrinter tr) {
		superLang().printArgs(node(), w, tr);
	}
	
	//TODO
}
