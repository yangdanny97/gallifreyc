package gallifreyc.ast.nodes;

import polyglot.ast.*;
import java.util.List;

public interface RestrictionBody extends Node {
    List<Node> members();
}
