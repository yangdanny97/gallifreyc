package gallifreyc.extension;

import gallifreyc.ast.MoveRef;
import gallifreyc.ast.RefQualification;
import gallifreyc.ast.SharedRef;
import gallifreyc.ast.UniqueRef;
import gallifreyc.types.RefQualifiedType;
import gallifreyc.types.RefQualifiedType_c;
import polyglot.ast.Expr;
import polyglot.ast.ExprOps;
import polyglot.ast.Lang;
import polyglot.ast.Node;
import polyglot.ast.TypeNode;
import polyglot.types.Type;
import polyglot.util.Position;
import polyglot.util.SerialVersionUID;

// extra operations for expressions
public class GallifreyExprExt extends GallifreyExt implements ExprOps {
    private static final long serialVersionUID = SerialVersionUID.generate();
    
    public RefQualification qualification;
    public String capability;
    public String path;
    
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
    
    public boolean isMove() {
    	Type t = node().type();
        if (t != null && t instanceof RefQualifiedType) {
            RefQualifiedType rt = (RefQualifiedType) t;
            return rt.refQualification() instanceof MoveRef;
        }
        return false;
    }
    
    public boolean isUnique() {
    	Type t = node().type();
        if (t != null && t instanceof RefQualifiedType) {
            RefQualifiedType rt = (RefQualifiedType) t;
            return rt.refQualification() instanceof UniqueRef;
        }
        return false;
    }
    
    public boolean isShared() {
    	Type t = node().type();
        if (t != null && t instanceof RefQualifiedType) {
            RefQualifiedType rt = (RefQualifiedType) t;
            return rt.refQualification() instanceof SharedRef;
        }
        return false;
    }
}
