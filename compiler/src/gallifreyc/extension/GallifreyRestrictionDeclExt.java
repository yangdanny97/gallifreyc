package gallifreyc.extension;

import gallifreyc.ast.RestrictionDecl;
import gallifreyc.translate.GallifreyRewriter;
import polyglot.ast.Node;
import polyglot.types.SemanticException;
import polyglot.util.SerialVersionUID;

public class GallifreyRestrictionDeclExt extends GallifreyExt {

    private static final long serialVersionUID = SerialVersionUID.generate();

    public GallifreyRestrictionDeclExt() {
    }

    @Override
    public RestrictionDecl node() {
        return (RestrictionDecl) super.node();
    }

    @Override
    public Node gallifreyRewrite(GallifreyRewriter rw) throws SemanticException {
        rw.genRestrictionInterface(node());
        rw.genRestrictionImplClass(node());
        return null;
    }

}
