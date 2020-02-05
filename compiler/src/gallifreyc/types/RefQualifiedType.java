package gallifreyc.types;

import gallifreyc.ast.nodes.RefQualification;
import polyglot.types.Type;

public interface RefQualifiedType extends Type {
    Type base();
    RefQualification refQualification();
}