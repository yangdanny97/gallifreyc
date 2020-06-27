package gallifreyc.extension;

import polyglot.types.SemanticException;
import polyglot.util.InternalCompilerError;
import polyglot.util.SerialVersionUID;
import polyglot.visit.TypeChecker;
import gallifreyc.types.GallifreyType;
import gallifreyc.types.GallifreyTypeSystem;
import gallifreyc.types.RegionContext;
import gallifreyc.visit.GallifreyTypeChecker;
import polyglot.ast.Conditional;
import polyglot.ast.Expr;
import polyglot.ast.Node;

public class GallifreyConditionalExt extends GallifreyExprExt {
    private static final long serialVersionUID = SerialVersionUID.generate();

    @Override
    public Conditional node() {
        return (Conditional) super.node();
    }

    @Override
    public Node typeCheck(TypeChecker tc) throws SemanticException {
        Conditional c = (Conditional) superLang().typeCheck(this.node(), tc);
        GallifreyType trueType = GallifreyExprExt.ext(c.consequent()).gallifreyType;
        GallifreyType falseType = GallifreyExprExt.ext(c.alternative()).gallifreyType;
        if (!trueType.qualification().equals(falseType.qualification())) {
            throw new SemanticException("branches of ternary must have same qualification", c.position());
        }
        this.gallifreyType = new GallifreyType(trueType);
        return c;
    }

    @Override
    public Node typeCheckOverride(Node parent, TypeChecker tc) throws SemanticException {
        GallifreyTypeChecker gtc = (GallifreyTypeChecker) tc.enter(parent, node());
        GallifreyTypeSystem ts = gtc.typeSystem();
        
        // visit children
        Expr cond = visitChild(node().cond(), gtc);
        //Store current region map for later use
        ts.push_regionContext();
        Expr consequent = visitChild(node().consequent(), gtc);
        RegionContext then_outcontext = ts.pop_regionContext();
        ts.push_regionContext();
        Expr alternative = visitChild(node().alternative(), gtc);
        RegionContext else_outcontext = ts.region_context();
        //we just take the else outmap wlog.
        Node n = node().cond(cond).consequent(consequent).alternative(alternative);

        try {
            Node result = (Node) gtc.leave(parent, node(), n, gtc);
            return result;
        }
        catch (InternalCompilerError e) {
            if (e.position() == null) e.setPosition(n.position());
            throw e;
        }
    }  
}
