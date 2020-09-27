package gallifreyc.extension;

import polyglot.ast.*;
import polyglot.ext.jl7.ast.J7Lang_c;
import polyglot.util.InternalCompilerError;
import polyglot.util.IsolatedID;

// language dispatcher, singleton class
public class GallifreyLang_c extends J7Lang_c implements GallifreyLang {

    public static final GallifreyLang_c instance = new GallifreyLang_c();

    public static GallifreyLang lang(NodeOps n) {
        while (n != null) {
            Lang lang = n.lang();
            if (lang instanceof GallifreyLang)
                return (GallifreyLang) lang;
            if (n instanceof Ext)
                n = ((Ext) n).pred();
            else
                return null;
        }
        throw new InternalCompilerError("Impossible to reach");
    }

    protected GallifreyLang_c() {
        super();
    }

    protected static GallifreyOps gallifreycExt(Node n) {
        return GallifreyExt.ext(n);
    }

    protected GallifreyOps GallifreyOps(Node n) {
        return gallifreycExt(n);
    }

    protected GallifreyExprOps GallifreyExprOps(Node n) {
        return (GallifreyExprOps) gallifreycExt(n);
    }

    @Override
    protected NodeOps NodeOps(Node n) {
        return gallifreycExt(n);
    }

    @Override
    protected ExprOps ExprOps(Expr n) {
        return (ExprOps) gallifreycExt(n);
    }

    @Override
    protected CallOps CallOps(Call n) {
        return (CallOps) gallifreycExt(n);
    }

    @Override
    protected ClassDeclOps ClassDeclOps(ClassDecl n) {
        return (ClassDeclOps) gallifreycExt(n);
    }

    @Override
    protected NewOps NewOps(New n) {
        return (NewOps) gallifreycExt(n);
    }

    @Override
    protected ProcedureCallOps ProcedureCallOps(ProcedureCall n) {
        return (ProcedureCallOps) gallifreycExt(n);
    }

    @Override
    protected ProcedureDeclOps ProcedureDeclOps(ProcedureDecl n) {
        return (ProcedureDeclOps) gallifreycExt(n);
    }

    @Override
    public int fresh() {
        return IsolatedID.newIntID();
    }

    @Override
    public String freshVar() {
        return "_temp" + fresh();
    }
}
