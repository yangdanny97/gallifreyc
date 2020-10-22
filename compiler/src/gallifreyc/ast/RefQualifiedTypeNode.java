package gallifreyc.ast;

import polyglot.ast.TypeNode;

// type node for qualified types
public interface RefQualifiedTypeNode extends TypeNode {
    RefQualification qualification();

    TypeNode base();
}
