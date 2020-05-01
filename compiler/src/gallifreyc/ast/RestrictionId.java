package gallifreyc.ast;

import polyglot.ast.Node;
import polyglot.ast.Id;

//rv::restriction OR wildcard::restriction OR restriction
public interface RestrictionId extends Node {
    public Id rv();

    public Id restriction();

    public boolean wildcardRv();

    public boolean isRvQualified();
}
