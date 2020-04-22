package gallifreyc.extension;

import java.util.*;

import gallifreyc.ast.RestrictionDecl;
import polyglot.ast.*;
import polyglot.ast.Import.Kind;
import polyglot.ext.jl5.ast.JL5Import;
import polyglot.types.*;
import polyglot.util.*;
import polyglot.visit.TypeChecker;

// extra operations for source files
public class GallifreySourceFileExt extends GallifreyExt {
    private static final long serialVersionUID = SerialVersionUID.generate();
    
    @Override
    public SourceFile node() {
        return (SourceFile) super.node();
    }
    
    @Override
    public Node typeCheck(TypeChecker tc) throws SemanticException {
    	// remove restriction decls before passing to superlang typecheck
    	SourceFile n = node();
    	List<TopLevelDecl> cd = new ArrayList<>();
    	List<RestrictionDecl> rd = new ArrayList<>();
    	for (TopLevelDecl d : n.decls()) {
    		if (d instanceof ClassDecl) cd.add(d);
    		if (d instanceof RestrictionDecl) rd.add((RestrictionDecl) d);
    	}
    	n = n.decls(cd);
    	n = (SourceFile) superLang().typeCheck(n, tc);
    	for (RestrictionDecl r : rd) {
    		lang().typeCheck(r, tc);
    	}
    	return n;
    }
}
