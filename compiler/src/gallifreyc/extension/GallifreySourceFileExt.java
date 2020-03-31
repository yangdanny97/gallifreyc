package gallifreyc.extension;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import gallifreyc.ast.RestrictionDecl;
import gallifreyc.types.RefQualifiedType;
import gallifreyc.types.RefQualifiedType_c;
import polyglot.ast.CanonicalTypeNode;
import polyglot.ast.ClassBody;
import polyglot.ast.ClassBody_c;
import polyglot.ast.ClassDecl;
import polyglot.ast.ClassDecl_c;
import polyglot.ast.ClassMember;
import polyglot.ast.Expr;
import polyglot.ast.Id_c;
import polyglot.ast.Import;
import polyglot.ast.Node;
import polyglot.ast.SourceFile;
import polyglot.ast.TopLevelDecl;
import polyglot.ast.TypeNode;
import polyglot.ast.Import.Kind;
import polyglot.ext.jl5.ast.JL5Import;
import polyglot.types.Flags;
import polyglot.types.Named;
import polyglot.types.ReferenceType;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.util.Copy;
import polyglot.util.ListUtil;
import polyglot.util.Position;
import polyglot.util.SerialVersionUID;
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
    	// TODO revisit this ordering... I think restriction mapping needs to be done pre-TC
    	n = n.decls(cd);
    	n = (SourceFile) superLang().typeCheck(n, tc);
    	for (RestrictionDecl r : rd) {
    		lang().typeCheck(r, tc);
    	}
    	return n;
    }
}
