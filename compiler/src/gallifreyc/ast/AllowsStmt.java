package gallifreyc.ast;

import polyglot.ast.*;

// declare allowed method names inside restriction body, shared objects under restriction
// can only use allowed methods
public interface AllowsStmt extends ClassMember {
    Id id();

    Id contingent_id();

    boolean testOnly();
}
