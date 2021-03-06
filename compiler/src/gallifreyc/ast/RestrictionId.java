package gallifreyc.ast;

import polyglot.ast.Node;
import polyglot.ast.Id;

// represents the name of a restriction, can take the following forms:
// rv::restriction OR wildcard::restriction OR restriction
public interface RestrictionId extends Node {
    public Id rv();

    public Id restriction();

    public boolean wildcardRv();

    public boolean isRvQualified();

    public String getInterfaceName();

    public String getWrapperName();

    public RestrictionId copy();
}
