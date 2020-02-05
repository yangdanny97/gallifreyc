package gallifreyc.ast.nodes;

import polyglot.ast.Id;
import polyglot.ast.Node;

public interface RestrictionDecl extends Node {
    Id id();
    Id for_id();
    RestrictionBody body();
}
