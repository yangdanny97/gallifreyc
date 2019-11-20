package gallifreyc.ast;

import polyglot.ast.AbstractExtFactory_c;
import polyglot.ast.Ext;
import polyglot.ast.ExtFactory;
import polyglot.ext.jl7.ast.JL7AbstractExtFactory_c;

public abstract class GallifreyAbstractExtFactory_c extends JL7AbstractExtFactory_c
        implements GallifreyExtFactory {

    public GallifreyAbstractExtFactory_c() {
        super();
    }

    public GallifreyAbstractExtFactory_c(ExtFactory nextExtFactory) {
        super(nextExtFactory);
    }

    // TODO: Implement factory methods for new extension nodes in future
    // extensions.  This entails calling the factory method for extension's
    // AST superclass.
    public final Ext extPreCondition() {
        Ext e = extPreConditionImpl();

        ExtFactory nextEF = nextExtFactory();
        Ext e2;
        if (nextEF instanceof GallifreyExtFactory) {
            e2 = ((GallifreyExtFactory) nextEF).extPreCondition();
        } else {
            e2 = nextEF.extNode();
        }

        e = composeExts(e, e2);
        return postExtPreCondition(e);
    }

    public final Ext extPostCondition() {
        Ext e = extPostConditionImpl();

        ExtFactory nextEF = nextExtFactory();
        Ext e2;
        if (nextEF instanceof GallifreyExtFactory) {
            e2 = ((GallifreyExtFactory) nextEF).extPostCondition();
        } else {
            e2 = nextEF.extNode();
        }

        e = composeExts(e, e2);
        return postExtPostCondition(e);
    }
    
    public final Ext extConditionedMethodDecl() {
        Ext e = extConditionedMethodDeclImpl();

        ExtFactory nextEF = nextExtFactory();
        Ext e2;
        if (nextEF instanceof GallifreyExtFactory) {
            e2 = ((GallifreyExtFactory) nextEF).extConditionedMethodDecl();
        } else {
            e2 = nextEF.extMethodDecl();
        }

        e = composeExts(e, e2);
        return postExtConditionedMethodDecl(e);
    }


    protected Ext extPreConditionImpl() {
        return extNode();
    }
    protected Ext extPostConditionImpl() {
        return extNode();
    }
    protected Ext extConditionedMethodDeclImpl() {
        return extMethodDecl();
    }


    protected Ext postExtPreCondition(Ext e) {
        return postExtNode(e);
    }
    protected Ext postExtPostCondition(Ext e) {
        return postExtNode(e);
    }
    protected Ext postExtConditionedMethodDecl(Ext e) {
        return postExtMethodDecl(e);
    }
}
