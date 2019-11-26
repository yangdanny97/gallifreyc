package gallifreyc.ast;

import polyglot.ast.TypeNode;

public interface RefQualifiedTypeNode extends TypeNode {
    RefQualification refQualification();
    TypeNode base();
    RefQualifiedTypeNode base(TypeNode base);

}
