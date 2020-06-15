package gallifreyc.types;

import polyglot.ast.Expr;

public class HeapContext {

    public HeapContext() {
        // TODO Auto-generated constructor stub
    }
    
    public boolean isValidRegion(Region r) {
        return false; //TODO
    }
    
    public Region trueNew() {
        return null; //TODO
    }
    
    public void regionAssign(Expr lhs, Region lhsRegion, Region rhsRegion) {
        //TODO
    }
    
    public Region regionApply(GallifreyMethodInstance mi, Region...inputRegions) {
        return null; //TODO
    }

}
