package gallifreyc.extension;

import polyglot.ast.*;
import polyglot.ext.jl7.ast.J7Lang_c;
import polyglot.util.InternalCompilerError;
import polyglot.translate.ExtensionRewriter;


// language dispatcher, singleton class
public class GallifreyLang_c extends J7Lang_c implements GallifreyLang {

    public static final GallifreyLang_c instance = new GallifreyLang_c();
    private int counter;

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
    	counter = 0;
    }

    protected static GallifreyExt gallifreycExt(Node n) {
        return GallifreyExt.ext(n);
    }

    @Override
    protected NodeOps NodeOps(Node n) {
        return gallifreycExt(n);
    }
    
    protected GallifreyOps GallifreyOps(Node n) {
        return gallifreycExt(n);
    }
    
    // TODO: define custom/new ops for new node AST classes as necessary and add methods here
    
    @Override
    public int fresh() {
    	counter++;
    	return counter;
    }
    
    @Override
    public String freshVar() {
    	return "generatedVar" + fresh();
    }
}
