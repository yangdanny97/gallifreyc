package gallifreyc.extension;

import polyglot.ast.*;
import polyglot.ext.jl7.ast.J7Lang_c;
import polyglot.util.InternalCompilerError;
import polyglot.util.UniqueID;


// language dispatcher, singleton class
public class GallifreyLang_c extends J7Lang_c implements GallifreyLang {

    public static final GallifreyLang_c instance = new GallifreyLang_c();

    public static GallifreyLang lang(NodeOps n) {
        while (n != null) {
            Lang lang = n.lang();
            if (lang instanceof GallifreyLang) return (GallifreyLang) lang;
            if (n instanceof Ext)
                n = ((Ext) n).pred();
            else return null;
        }
        throw new InternalCompilerError("Impossible to reach");
    }

    protected GallifreyLang_c() {
    	super();
    }

    protected static GallifreyOps gallifreycExt(Node n) {
        return GallifreyExt.ext(n);
    }

    @Override
    protected NodeOps NodeOps(Node n) {
        return gallifreycExt(n);
    }
    
    protected GallifreyOps GallifreyOps(Node n) {
        return gallifreycExt(n);
    }
    
    @Override
    protected ExprOps ExprOps(Expr n) {
        return (ExprOps) gallifreycExt(n);
    }
    
    @Override
    public GallifreyExprExt exprExt(Expr n) {
    	return (GallifreyExprExt) gallifreycExt(n);
    }
    
    @Override
    public int fresh() {
    	return UniqueID.newIntID();
    }
    
    @Override
    public String freshVar() {
    	return "generatedVar" + fresh();
    }
}
