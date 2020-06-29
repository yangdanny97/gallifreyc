package gallifreyc.extension;

import gallifreyc.ast.MergeDecl;
import polyglot.util.SerialVersionUID;

public class GallifreyMergeDeclExt extends GallifreyExt {
    private static final long serialVersionUID = SerialVersionUID.generate();
    
    @Override
    public MergeDecl node() {
        return (MergeDecl) super.node();
    }
}
