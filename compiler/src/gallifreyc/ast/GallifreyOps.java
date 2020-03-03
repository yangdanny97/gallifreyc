package gallifreyc.ast;

import gallifreyc.visit.RefQualificationAdder;
import gallifreyc.visit.SharedTypeWrapper;
import polyglot.ast.Node;
import polyglot.ast.NodeOps;

/**
 * Define a collection of operations that any Gallifrey node must
 * implement.  This interface allows extensions both to override and reuse
 * functionality in GallifreyExt (i think).
 * 
 * TODO: neither of these seems to be necessary, revisit when I add TC passes
 */
public interface GallifreyOps extends NodeOps {
    RefQualificationAdder addRefQualificationEnter(RefQualificationAdder v);
    Node addRefQualification(RefQualificationAdder v);
    
    SharedTypeWrapper wrapSharedTypeEnter(SharedTypeWrapper v);
    Node wrapSharedType(SharedTypeWrapper v);
}