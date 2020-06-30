package gallifreyc.ast;

import polyglot.ast.*;

public interface AllowsStmt extends ClassMember {
    Id id();

    Id contingent_id();
    
    boolean testOnly();
}
