package gallifreyc.ast;

import polyglot.ast.*;
import java.util.List;

public interface RestrictionBody extends Term, ClassBody {
    List<ClassMember> restrictionMembers();
}
