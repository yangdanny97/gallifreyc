package gallifreyc.ast;

import polyglot.ast.*;
import java.util.List;

// body of a restriction declaration
public interface RestrictionBody extends Term, ClassBody {
    List<ClassMember> restrictionMembers();
}
