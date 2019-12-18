package gallifreyc.visit;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import gallifreyc.ast.GallifreyLang;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.SourceFile;
import polyglot.ast.TopLevelDecl;
import polyglot.frontend.Job;
import polyglot.types.ReferenceType;
import polyglot.types.TypeSystem;
import polyglot.util.Copy;
import polyglot.visit.ContextVisitor;
import polyglot.visit.NodeVisitor;

public class SharedTypeWrapper extends ContextVisitor {
    // A set of the types that we've already seen, i.e. those for which
    // we have generated a wrapper class
    protected Set<ReferenceType> types_visited;
    protected SourceFile source_file;
    public List<TopLevelDecl> decls;
    
    public SharedTypeWrapper(Job job, TypeSystem ts, NodeFactory nf) {
        super(job, ts, nf);
        types_visited = new HashSet<>();
        decls = new LinkedList<>();
    }
    
    public SharedTypeWrapper sourceFile(SourceFile source_file) {
//        SharedTypeWrapper v = Copy.Util.copy(this);
        this.source_file = source_file;
        return this;
    }
    
    public SourceFile sourceFile() {
        return this.source_file;
    }
    
    public Set<ReferenceType> typesVisited() {
        return this.types_visited;
    }
    
    @Override
    public GallifreyLang lang() {
        return (GallifreyLang) super.lang();
    }

    @Override
    public SharedTypeWrapper enterCall(Node n) {
        // Prepare the visitor for before visiting n by letting each AST node
        // decide what to do with the visitor.
        return lang().wrapSharedTypeEnter(n, this);
    }

    @Override
    public Node leaveCall(Node old, Node n, NodeVisitor v) {
        // After visiting old's children, the results of which are now children
        // of n, visit n itself to perform the operation that n's class defines
        // for this compiler pass.
        return lang().wrapSharedType(n, this);
    }
}
