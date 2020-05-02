package gallifreyc.ast;

import polyglot.ast.TypeNode;

public interface RefQualifiedTypeNode extends TypeNode {
    RefQualification qualification();

    TypeNode base();
}
