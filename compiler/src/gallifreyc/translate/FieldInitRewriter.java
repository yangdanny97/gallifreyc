package gallifreyc.translate;

import polyglot.ast.*;
import polyglot.frontend.ExtensionInfo;
import polyglot.frontend.Job;
import polyglot.types.SemanticException;
import gallifreyc.extension.GallifreyExt;

//move field initializers into an initializer block
public class FieldInitRewriter extends GRewriter {

    public FieldInitRewriter(Job job, ExtensionInfo from_ext, ExtensionInfo to_ext) {
        super(job, from_ext, to_ext);
    }

    @Override
    public Node extRewrite(Node n) throws SemanticException {
        return GallifreyExt.ext(n).rewriteFieldInits(this);
    }
}
