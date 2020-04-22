package gallifreyc.extension;

import gallifreyc.ast.*;
import gallifreyc.types.GallifreyType;
import polyglot.ast.*;
import polyglot.types.Type;
import polyglot.util.Position;
import polyglot.util.SerialVersionUID;

// extra operations for expressions
public class GallifreyExprExt extends GallifreyExt implements ExprOps {
    private static final long serialVersionUID = SerialVersionUID.generate();
    
    public GallifreyType gallifreyType;
    
    @Override
    public Expr node() {
        return (Expr) super.node();
    }

    @Override
    public boolean constantValueSet(Lang lang) {
        return lang().constantValueSet(node(), lang);
    }

    @Override
    public boolean isConstant(Lang lang) {
        return lang().isConstant(node(), lang);
    }

    @Override
    public Object constantValue(Lang lang) {
        return lang().constantValue(node(), lang);
    }
    
    public boolean isMove() {
    	return gallifreyType.isMove();
    }
    
    public boolean isUnique() {
    	return gallifreyType.isUnique();
    }
    
    public boolean isShared() {
    	return gallifreyType.isShared();
    }
    
    public boolean isLocal() {
    	return gallifreyType.isShared();
    }
}
