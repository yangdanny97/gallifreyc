package gallifreyc.types;

import polyglot.ast.Expr;
import java.util.Collection;
import java.util.List;

interface Region {

}

interface RegionFunctionType {

}

class RegionFunctionReturns {
    final Region return_result;
    final Collection<Region> preserved_regions;

    public RegionFunctionReturns(final Region return_result, final Collection<Region> preserved_regions) {
        this.return_result = return_result;
        this.preserved_regions = preserved_regions;
    }
}

public interface HeapContext<Reg extends Region, Fun extends RegionFunctionType> {

    public boolean isValidRegion(Reg r);

    public Reg trueNew();

    public void regionAssign(Expr lhs, Reg lhsRegion, Reg rhsRegion);

    public RegionFunctionReturns regionApply(Fun mi, List<Reg> inputRegions);

}
