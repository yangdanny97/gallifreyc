package gallifreyc.ast;

import gallifreyc.types.GallifreyTypeSystem;
import gallifreyc.visit.RefQualificationAdder;
import polyglot.ast.MethodDecl;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.TypeNode;
import polyglot.ast.TypeNode_c;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.util.CodeWriter;
import polyglot.util.Position;
import polyglot.util.SerialVersionUID;
import polyglot.visit.AmbiguityRemover;
import polyglot.visit.NodeVisitor;
import polyglot.visit.PrettyPrinter;
import polyglot.visit.TypeBuilder;

public class RefQualifiedTypeNode_c extends TypeNode_c implements RefQualifiedTypeNode {
    private static final long serialVersionUID = SerialVersionUID.generate();

    protected TypeNode base;
    protected RefQualification refQualification;

    public RefQualifiedTypeNode_c(Position pos, RefQualification refQualification, TypeNode t) {
        super(pos);
        this.base = t;
        this.refQualification = refQualification;
    }
    
    @Override
    public boolean isDisambiguated() {
        return this.base.isDisambiguated();
    }

    @Override
    public TypeNode base() {
        return base;
    }
    
//    @Override
//    public Type type() {
//        return base.type();
//    }

    @Override
    public Node visitChildren(NodeVisitor v) {
        TypeNode base = visitChild(this.base, v);
//        return reconstruct(this, base);
        this.base = base;
        return this;
    }
    
    @Override
    public Node buildTypes(TypeBuilder tb) throws SemanticException {
        GallifreyTypeSystem ts = (GallifreyTypeSystem) tb.typeSystem();
//        return type(ts.refQualifiedTypeOf(position(), base.type(), this.refQualification));
        return type(this.base.type());
    }

    @Override
    public Node disambiguate(AmbiguityRemover ar) throws SemanticException {
        GallifreyTypeSystem ts = (GallifreyTypeSystem) ar.typeSystem();
        NodeFactory nf = ar.nodeFactory();

        if (!base.isDisambiguated()) {
            return this;
        }

        Type baseType = base.type();

        if (!baseType.isCanonical()) {
            return this;
        }
        
//        if (!this.refQualification.isDisambiguated()) {
//            return this;
//        }

        return nf.CanonicalTypeNode(position(),
                                    ts.refQualifiedTypeOf(position(), baseType, this.refQualification));
//      return nf.CanonicalTypeNode(position(),
//                                  this.base.type());
    }

    @Override
    public String toString() {
        return refQualification.toString() + " " + super.toString();
    }
    
    @Override
    public void prettyPrint(CodeWriter w, PrettyPrinter tr) {
        print(refQualification, w, tr);
        w.write(" ");
        print(base, w, tr);
    }

    @Override
    public RefQualification refQualification() {
        return refQualification;
    }
}
