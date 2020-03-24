package gallifreyc.types;

import java.util.List;
import java.util.Map;

import gallifreyc.ast.RefQualification;
import polyglot.ext.jl7.types.JL7TypeSystem;
import polyglot.types.*;
import polyglot.util.Position;

public interface GallifreyTypeSystem extends JL7TypeSystem {
    RefQualifiedType refQualifiedTypeOf(Position pos, Type base, RefQualification q);
    
    public void addRestrictionMapping(String restriction, String cls);
    public String getClassNameForRestriction(String restriction);
    
    public void addUnionRestriction(String union, List<String> restrictions);
    public List<String> getVariantRestrictions(String restriction);
    public boolean isUnionRestriction(String restriction);
}
