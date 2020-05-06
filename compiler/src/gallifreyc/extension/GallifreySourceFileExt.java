package gallifreyc.extension;

import java.util.*;

import gallifreyc.ast.RestrictionDecl;
import polyglot.ast.*;
import polyglot.types.*;
import polyglot.util.*;
import polyglot.visit.TypeBuilder;
import polyglot.visit.TypeChecker;

// extra operations for source files
public class GallifreySourceFileExt extends GallifreyExt {
    private static final long serialVersionUID = SerialVersionUID.generate();

    @Override
    public SourceFile node() {
        return (SourceFile) super.node();
    }
    
    @Override
    public Node buildTypes(TypeBuilder tb) throws SemanticException {
        SourceFile n = node();
        List<TopLevelDecl> cd = new ArrayList<>();
        List<RestrictionDecl> rd = new ArrayList<>();
        for (TopLevelDecl d : n.decls()) {
            if (d instanceof ClassDecl)
                cd.add(d);
            if (d instanceof RestrictionDecl)
                rd.add((RestrictionDecl) d);
        }
        n = n.decls(cd);
        n = (SourceFile) superLang().buildTypes(n, tb);
        
        List<TopLevelDecl> newDecls = new ArrayList<>();
        List<RestrictionDecl> newRDecls = new ArrayList<>();
        
        for (RestrictionDecl r : rd) {
            RestrictionDecl newRDecl = (RestrictionDecl) lang().buildTypes(r, tb);
            newRDecls.add(newRDecl);
        }
        newDecls.addAll(n.decls());
        newDecls.addAll(newRDecls);
        return n.decls(newDecls);
    }

    @Override
    public Node typeCheck(TypeChecker tc) throws SemanticException {
        // remove restriction decls before passing to superlang typecheck
        SourceFile n = node();
        List<TopLevelDecl> cd = new ArrayList<>();
        List<RestrictionDecl> rd = new ArrayList<>();
        for (TopLevelDecl d : n.decls()) {
            if (d instanceof ClassDecl)
                cd.add(d);
            if (d instanceof RestrictionDecl)
                rd.add((RestrictionDecl) d);
        }
        n = n.decls(cd);
        n = (SourceFile) superLang().typeCheck(n, tc);
        
        List<TopLevelDecl> newDecls = new ArrayList<>();
        List<RestrictionDecl> newRDecls = new ArrayList<>();
        
        for (RestrictionDecl r : rd) {
            RestrictionDecl newRDecl = (RestrictionDecl) lang().typeCheck(r, tc);
            newRDecls.add(newRDecl);
        }
        newDecls.addAll(n.decls());
        newDecls.addAll(newRDecls);
        return n.decls(newDecls);
    }
}
