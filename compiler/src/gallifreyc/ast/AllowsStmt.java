package gallifreyc.ast;

import polyglot.ast.*;

public interface AllowsStmt extends Node {
    Id id();

    Id contingent_id();
    
    boolean testOnly();
}
