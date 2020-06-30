package gallifreyc.types;

import java.util.*;

// map of variables to region
public class RegionMap implements Cloneable {

    private TreeMap<String, Region_c> m;

    public RegionMap() {
        m = new TreeMap<>();
    }

    private RegionMap(TreeMap<String, Region_c> m) {
        this.m = m;
    }

    @Override
    @SuppressWarnings("unchecked")
    public RegionMap clone() {
        return new RegionMap((TreeMap<String, Region_c>) m.clone());
    }

    public Region_c region_at(String s) {
        return m.get(s);
    }

    public void set_region(String s, Region_c r) {
        m.put(s, r);
    }
}
