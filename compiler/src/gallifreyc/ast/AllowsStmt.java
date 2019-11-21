package gallifreyc.ast;

import polyglot.ast.*;

public interface AllowsStmt extends RestrictionMember {
    Id id();
    Id contingent_id();
}
