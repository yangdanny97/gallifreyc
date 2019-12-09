package gallifreyc.types;

import gallifreyc.ast.RefQualification;
import polyglot.types.ArrayType;
import polyglot.types.ClassType;
import polyglot.types.NullType;
import polyglot.types.PrimitiveType;
import polyglot.types.ReferenceType;
import polyglot.types.Resolver;
import polyglot.types.Type;
import polyglot.types.TypeObject;
import polyglot.types.TypeSystem;
import polyglot.types.Type_c;
import polyglot.util.CodeWriter;
import polyglot.util.InternalCompilerError;
import polyglot.util.Position;
import polyglot.util.SerialVersionUID;


public class RefQualifiedType_c extends Type_c implements RefQualifiedType {
    private static final long serialVersionUID = SerialVersionUID.generate();
    
    protected Type base;
    protected RefQualification refQualification;
    
    public Type base() {
        return base;
    }

    public RefQualification refQualification() {
        return refQualification;
    }
    
    @Override
    public boolean equalsImpl(TypeObject t) {
        return this.base.equalsImpl(t);
    }
    
    public RefQualifiedType_c(TypeSystem ts, Position pos, Type base, RefQualification q) {
        super(ts, pos);
        this.base = base;
        this.refQualification = q;
    }
    
    @Override
    public String translate(Resolver c) {
        return this.base.translate(c);
    }
    
    @Override
    public boolean isType() {
        return this.base.isType();
    }

    @Override
    public boolean isPackage() {
        return this.base.isPackage();
    }

    @Override
    public Type toType() {
        return this.base.toType();
    }

    @Override
    public polyglot.types.Package toPackage() {
        return this.base.toPackage();
    }

    /* To be filled in by subtypes. */
    @Override
    public boolean isCanonical() {
        return this.base.isCanonical();
    }

    @Override
    public boolean isPrimitive() {
        return this.base.isPrimitive();
    }

    @Override
    public boolean isNumeric() {
        return this.base.isNumeric();
    }

    @Override
    public boolean isIntOrLess() {
        return this.base.isIntOrLess();
    }

    @Override
    public boolean isLongOrLess() {
        return this.base.isLongOrLess();
    }

    @Override
    public boolean isVoid() {
        return this.base.isVoid();
    }

    @Override
    public boolean isBoolean() {
        return this.base.isBoolean();
    }

    @Override
    public boolean isChar() {
        return this.base.isChar();
    }

    @Override
    public boolean isByte() {
        return this.base.isByte();
    }

    @Override
    public boolean isShort() {
        return this.base.isShort();
    }

    @Override
    public boolean isInt() {
        return this.base.isInt();
    }

    @Override
    public boolean isLong() {
        return this.base.isLong();
    }

    @Override
    public boolean isFloat() {
        return this.base.isFloat();
    }

    @Override
    public boolean isDouble() {
        return this.base.isDouble();
    }

    @Override
    public boolean isReference() {
        return this.base.isReference();
    }

    @Override
    public boolean isNull() {
        return this.base.isNull();
    }

    @Override
    public boolean isClass() {
        return this.base.isClass();
    }

    @Override
    public boolean isArray() {
        return this.base.isArray();
    }

    @Override
    public boolean isThrowable() {
        return this.base.isThrowable();
    }

    @Override
    public boolean isUncheckedException() {
        return this.base.isUncheckedException();
    }

    @Override
    public ClassType toClass() {
        return this.base.toClass();
    }

    @Override
    public NullType toNull() {
        return this.base.toNull();
    }

    @Override
    public ReferenceType toReference() {
        return this.base.toReference();
    }

    @Override
    public PrimitiveType toPrimitive() {
        return this.base.toPrimitive();
    }

    @Override
    public ArrayType toArray() {
        return this.base.toArray();
    }

    @Override
    public ArrayType arrayOf(int dims) {
        return this.base.arrayOf(dims);
    }

    @Override
    public ArrayType arrayOf() {
        return this.base.arrayOf();
    }

    @Override
    public boolean typeEqualsImpl(Type t) {
        return this.base.typeEqualsImpl(t);
    }

    @Override
    public boolean isSubtypeImpl(Type ancestor) {
        return this.base.isSubtypeImpl(ancestor);
    }

    @Override
    public boolean descendsFromImpl(Type ancestor) {
        return this.base.descendsFromImpl(ancestor);
    }

    @Override
    public boolean isCastValidImpl(Type toType) {
        return this.base.isCastValidImpl(toType);
    }

    @Override
    public boolean isImplicitCastValidImpl(Type toType) {
        System.out.printf("Checking if %s isImplicitCastValidImpl to %s\n", this.base.toString(), toType.toString());
        return this.base.isImplicitCastValidImpl(toType);
    }

    @Override
    public boolean numericConversionValidImpl(Object value) {
        return this.base.numericConversionValidImpl(value);
    }

    @Override
    public boolean isComparable(Type t) {
        return this.base.isComparable(t);
    }

    @Override
    public String toString() {
        return this.refQualification.toString() + " " + this.base.toString();
    }
    
    @Override
    public void print(CodeWriter w) {
//        print(refQualification, w, tr);
//        w.write(" ");
//        print(base, w, tr);
        w.write("<RefQualification> ");
        base.print(w);
    }

}