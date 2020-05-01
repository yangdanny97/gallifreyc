package gallifreyc.extension;

import polyglot.ast.Ext;
import polyglot.ast.ExtFactory;

// override default extension methods
public final class GallifreyExtFactory_c extends GallifreyAbstractExtFactory_c {

    public GallifreyExtFactory_c() {
        super();
    }

    public GallifreyExtFactory_c(ExtFactory nextExtFactory) {
        super(nextExtFactory);
    }

    @Override
    protected Ext extNodeImpl() {
        return new GallifreyExt();
    }
    
    @Override
    protected Ext extTermImpl() {
        return new GallifreyExt();
    }
    
    @Override
    protected Ext extStmtImpl() {
        return new GallifreyExt();
    }
    
    
    @Override
    protected Ext extSourceFileImpl() {
        return new GallifreySourceFileExt();
    }
    
    // Decls
    
    @Override
    protected Ext extClassDeclImpl() {
        return new GallifreyClassDeclExt();
    }

    @Override
    protected Ext extMethodDeclImpl() {
        return new GallifreyMethodDeclExt();
    }
    
    @Override
    protected Ext extConstructorDeclImpl() {
        return new GallifreyConstructorDeclExt();
    }
    
    @Override
    protected Ext extFieldDeclImpl() {
    	return new GallifreyFieldDeclExt();
    }
    
    @Override
    protected Ext extLocalDeclImpl() {
        return new GallifreyLocalDeclExt();
    }
    
    // statements
    
    @Override
    protected Ext extConstructorCallImpl() {
        return new GallifreyConstructorCallExt();
    }
    
    //Types
    
    @Override
    protected Ext extFormalImpl() {
        return new GallifreyFormalExt();
    }
    
    // OVERRIDE EXPRS
    @Override
    protected Ext extExprImpl() {
        return new GallifreyExprExt();
    }
    
    @Override
    protected Ext extAmbExprImpl() {
        return new GallifreyAmbExprExt();
    }
    @Override
    protected Ext extArrayAccessImpl() {
        return new GallifreyArrayAccessExt();
    }
    @Override
    protected Ext extArrayInitImpl() {
        return new GallifreyArrayInitExt();
    }
    @Override
    protected Ext extAssignImpl() {
        return new GallifreyAssignExt();
    }
    @Override
    protected Ext extAmbAssignImpl() {
        return new GallifreyAmbAssignExt();
    }
    @Override
    protected Ext extArrayAccessAssignImpl() {
        return new GallifreyArrayAccessAssignExt();
    }
    @Override
    protected Ext extFieldAssignImpl() {
        return new GallifreyFieldAssignExt();
    }
    @Override
    protected Ext extLocalAssignImpl() {
        return new GallifreyLocalAssignExt();
    }
    @Override
    protected Ext extBinaryImpl() {
        return new GallifreyBinaryExt();
    }
    @Override
    protected Ext extCallImpl() {
        return new GallifreyCallExt();
    }
    @Override
    protected Ext extCastImpl() {
        return new GallifreyCastExt();
    }
    @Override
    protected Ext extConditionalImpl() {
        return new GallifreyConditionalExt();
    }
    @Override
    protected Ext extFieldImpl() {
        return new GallifreyFieldExt();
    }
    @Override
    protected Ext extEnumConstantImpl() {
        return new GallifreyEnumConstantExt();
    }
    @Override
    protected Ext extInstanceofImpl() {
        return new GallifreyInstanceofExt();
    }
    @Override
    protected Ext extLitImpl() {
        return new GallifreyLitExt();
    }
    @Override
    protected Ext extBooleanLitImpl() {
        return new GallifreyBooleanLitExt();
    }
    @Override
    protected Ext extClassLitImpl() {
        return new GallifreyClassLitExt();
    }
    @Override
    protected Ext extFloatLitImpl() {
        return new GallifreyFloatLitExt();
    }
    @Override
    protected Ext extNullLitImpl() {
        return new GallifreyNullLitExt();
    }
    @Override
    protected Ext extNumLitImpl() {
        return new GallifreyNumLitExt();
    }
    @Override
    protected Ext extCharLitImpl() {
        return new GallifreyCharLitExt();
    }
    @Override
    protected Ext extIntLitImpl() {
        return new GallifreyIntLitExt();
    }
    @Override
    protected Ext extStringLitImpl() {
        return new GallifreyStringLitExt();
    }
    @Override
    protected Ext extLocalImpl() {
        return new GallifreyLocalExt();
    }
    @Override
    protected Ext extNewImpl() {
        return new GallifreyNewExt();
    }
    @Override
    protected Ext extNewArrayImpl() {
        return new GallifreyNewArrayExt();
    }
    @Override
    protected Ext extSpecialImpl() {
        return new GallifreySpecialExt();
    }
    @Override
    protected Ext extUnaryImpl() {
        return new GallifreyUnaryExt();
    }
}
