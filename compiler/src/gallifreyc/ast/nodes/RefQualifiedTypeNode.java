package gallifreyc.ast.nodes;

import polyglot.ast.TypeNode;

public interface RefQualifiedTypeNode extends TypeNode {
    RefQualification refQualification();
    TypeNode base();
}
