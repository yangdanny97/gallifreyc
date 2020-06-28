package gallifreyc.extension;

import gallifreyc.ast.MergeDecl;
import gallifreyc.translate.GallifreyRewriter;
import gallifreyc.types.GallifreyTypeSystem;
import gallifreyc.visit.GallifreyTypeBuilder;
import polyglot.ast.Node;
import polyglot.types.SemanticException;
import polyglot.util.SerialVersionUID;
import polyglot.visit.TypeBuilder;
import polyglot.visit.TypeChecker;

public class GallifreyMergeDeclExt extends GallifreyExt {
    private static final long serialVersionUID = SerialVersionUID.generate();
    
    @Override
    public MergeDecl node() {
        return (MergeDecl) super.node();
    }

    @Override
    public Node gallifreyRewrite(GallifreyRewriter rw) throws SemanticException {
        // TODO Auto-generated method stub
        return super.gallifreyRewrite(rw);
    }
}
