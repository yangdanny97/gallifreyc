package gallifreyc.types;

import gallifreyc.ast.RefQualification;
import polyglot.types.Resolver;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.types.Type_c;
import polyglot.util.CodeWriter;
import polyglot.util.Position;
import polyglot.util.SerialVersionUID;


public class RefQualifiedType_c extends Type_c implements RefQualifiedType {
    private static final long serialVersionUID = SerialVersionUID.generate();
    
    protected Type base;
    protected RefQualification refQualification;
    
    RefQualifiedType_c(TypeSystem ts, Position pos, Type base, RefQualification q) {
        super(ts, pos);
        this.base = base;
        this.refQualification = q;
    }
    
    @Override
    public boolean isCanonical() {
        return base.isCanonical();
    }

    @Override
    public String translate(Resolver c) {
        return base.translate(c);
    }
    
    @Override
    public boolean typeEqualsImpl(Type t) {
        return this.base == t;
    }

    @Override
    public String toString() {
        return this.refQualification.toString() + " " + this.base.toString();
    }
    
    @Override
    public void print(CodeWriter w) {
        base.print(w);
    }

}