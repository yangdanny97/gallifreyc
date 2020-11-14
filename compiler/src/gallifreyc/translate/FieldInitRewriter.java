package gallifreyc.translate;

import polyglot.ast.*;
import polyglot.frontend.ExtensionInfo;
import polyglot.frontend.Job;
import polyglot.types.SemanticException;
import gallifreyc.extension.GallifreyExt;

/**
 * 
 * pass for moving field initializers to separate block:
 * 
 * original: private int field1 = 1;
 * 
 * new: private int field1; { this.field1 = 1; }
 */
public class FieldInitRewriter extends GallifreyRewriter {

    public FieldInitRewriter(Job job, ExtensionInfo from_ext, ExtensionInfo to_ext) {
        super(job, from_ext, to_ext);
    }

    @Override
    public Node extRewrite(Node n) throws SemanticException {
        return GallifreyExt.ext(n).rewriteFieldInits(this);
    }
}
