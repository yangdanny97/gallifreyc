package gallifreyc.visit;

import gallifreyc.ast.GallifreyLang;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.frontend.Job;
import polyglot.types.TypeSystem;
import polyglot.visit.ContextVisitor;
import polyglot.visit.NodeVisitor;

public class RefQualificationAdder extends ContextVisitor {
    // TODO: Implement this class.
    public RefQualificationAdder(Job job, TypeSystem ts, NodeFactory nf) {
        super(job, ts, nf);
    }
    
    @Override
    public GallifreyLang lang() {
        return (GallifreyLang) super.lang();
    }

    @Override
    public RefQualificationAdder enterCall(Node n) {
        // Prepare the visitor for before visiting n by letting each AST node
        // decide what to do with the visitor.
        return lang().addRefQualificationEnter(n, this);
    }

    @Override
    public Node leaveCall(Node old, Node n, NodeVisitor v) {
        // After visiting old's children, the results of which are now children
        // of n, visit n itself to perform the operation that n's class defines
        // for this compiler pass.
        return lang().addRefQualification(n, this);
    }
}
