package gallifreyc.types;

import java.util.*;

// map of variables to region
public class RegionMap {
    private List<RegionMap> children_maps;
    private Map<String, String> m;
    final RegionMap parent;

    public RegionMap(RegionMap parent) {
        this.parent = parent;
        this.children_maps = new ArrayList<>();
        this.m = new HashMap<>();
    }

    public String lookup(String var) {
        if (!m.containsKey(var)) {
            if (parent != null) {
                return parent.lookup(var);
            }
            return null;
        }
        return m.get(var);
    }

    public RegionMap addChild() {
        RegionMap child = new RegionMap(this);
        this.children_maps.add(child);
        return child;
    }
}
