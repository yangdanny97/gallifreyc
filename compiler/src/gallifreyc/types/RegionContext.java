package gallifreyc.types;

public class RegionContext{

    public RegionContext prev = null;
    
    public RegionMap vars_to_regions = new RegionMap();
    
    public HeapContext<Region_c,RegionFunctionType_c> heapctx = new HeapContext_c();

    
    public RegionContext(RegionContext previous){
	this.vars_to_regions = previous.vars_to_regions.clone();
	this.heapctx = ((HeapContext_c)previous.heapctx).clone();
    }
    
    public RegionContext(){}
    
    //TODO
    public boolean isEmpty() { return true; }
}
