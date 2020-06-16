package gallifreyc.types;

import polyglot.ast.Expr;
import java.util.*;

class Region_c implements Region {
    final String region_id;
    Region_c(final String region_id){
	this.region_id = region_id;
    }
}

class RegionFunctionType_c implements RegionFunctionType {
}

public class HeapContext_c implements HeapContext<Region_c, RegionFunctionType_c>, Cloneable{
    
    private final NameGenerator gen;
    private final TreeSet<Region_c> regions;
    
    private HeapContext_c(HeapContext_c old){
	this.gen = old.gen;
	this.regions = (TreeSet<Region_c>) old.regions.clone();
    }

    public HeapContext_c() {
    	gen = new NameGenerator_c();
    	regions = new TreeSet<>();
    }

    public Region_c trueNew() {
        Region_c r = new Region_c(gen.generate());
	regions.add(r);
	return r;
    }
    
    public boolean isValidRegion(Region_c r) {
        return regions.contains(r);
    }
    
    public void regionAssign(Expr lhs, Region_c lhsRegion, Region_c rhsRegion) {
	//Nothing to do here until we have focus implemented
    }

    //build this from a GallifreyMethodInstance somehow? 

	@Override
	public RegionFunctionReturns regionApply(RegionFunctionType_c mi, List<Region_c> inputRegions) {
		// TODO Auto-generated method stub
		return null;
	}
	
    @Override
    public HeapContext<Region_c,RegionFunctionType_c> clone(){
	return new HeapContext_c(this);
    }

}
