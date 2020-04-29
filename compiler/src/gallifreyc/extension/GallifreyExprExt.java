package gallifreyc.extension;

import gallifreyc.ast.UnknownRef;
import gallifreyc.types.GallifreyType;
import polyglot.ast.*;
import polyglot.util.InternalCompilerError;
import polyglot.util.Position;
import polyglot.util.SerialVersionUID;

// extra operations for expressions
public class GallifreyExprExt extends GallifreyExt implements ExprOps {
    private static final long serialVersionUID = SerialVersionUID.generate();
    
    public GallifreyType gallifreyType;
    {
    	gallifreyType = new GallifreyType(new UnknownRef(Position.COMPILER_GENERATED));
    }
    
	public static GallifreyExprExt ext(Node n) {
		return (GallifreyExprExt) GallifreyExt.ext(n);
	}
    
    @Override
    public Expr node() {
        return (Expr) super.node();
    }

    @Override
    public boolean constantValueSet(Lang lang) {
        return superLang().constantValueSet(node(), lang);
    }

    @Override
    public boolean isConstant(Lang lang) {
        return superLang().isConstant(node(), lang);
    }

    @Override
    public Object constantValue(Lang lang) {
        return superLang().constantValue(node(), lang);
    }
    
    public GallifreyType gallifreyType() {
    	return gallifreyType;
    }
    
    public GallifreyExprExt gallifreyType(GallifreyType t) {
    	gallifreyType = t;
    	return this;
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
