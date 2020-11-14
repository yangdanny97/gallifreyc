package gallifreyc.extension;

import gallifreyc.ast.RestrictionUnionDecl;
import gallifreyc.translate.GallifreyCodegenRewriter;
import gallifreyc.types.GallifreyTypeSystem;
import polyglot.ast.Node;
import polyglot.types.SemanticException;
import polyglot.util.SerialVersionUID;

public class GallifreyRestrictionUnionDeclExt extends GallifreyExt {

    private static final long serialVersionUID = SerialVersionUID.generate();

    public GallifreyRestrictionUnionDeclExt() {
    }

    @Override
    public RestrictionUnionDecl node() {
        return (RestrictionUnionDecl) super.node();
    }

    @Override
    public Node gallifreyRewrite(GallifreyCodegenRewriter rw) throws SemanticException {
        rw.genRVHolderInterface(node());
        GallifreyTypeSystem ts = (GallifreyTypeSystem) rw.typeSystem();
        for (String subRestriction : ts.getRestrictionsForRV(node().name())) {
            rw.genRVSubrestrictionInterface(node().name(), subRestriction);
            rw.genRVSubrestrictionImpl(node().name(), subRestriction);
        }
        rw.genRVClass(node());
        return null;
    }

}
