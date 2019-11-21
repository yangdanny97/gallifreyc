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
    
    public final Ext extLocalRef() {
        Ext e = extLocalRefImpl();

        ExtFactory nextEF = nextExtFactory();
        Ext e2;
        if (nextEF instanceof GallifreyExtFactory) {
            e2 = ((GallifreyExtFactory) nextEF).extLocalRef();
        } else {
            e2 = nextEF.extNode();
        }

        e = composeExts(e, e2);
        return postExtLocalRef(e);
    }

    public final Ext extUniqueRef() {
        Ext e = extUniqueRefImpl();

        ExtFactory nextEF = nextExtFactory();
        Ext e2;
        if (nextEF instanceof GallifreyExtFactory) {
            e2 = ((GallifreyExtFactory) nextEF).extUniqueRef();
        } else {
            e2 = nextEF.extNode();
        }

        e = composeExts(e, e2);
        return postExtUniqueRef(e);
    }

    public final Ext extSharedRef() {
        Ext e = extSharedRefImpl();

        ExtFactory nextEF = nextExtFactory();
        Ext e2;
        if (nextEF instanceof GallifreyExtFactory) {
            e2 = ((GallifreyExtFactory) nextEF).extSharedRef();
        } else {
            e2 = nextEF.extNode();
        }

        e = composeExts(e, e2);
        return postExtSharedRef(e);
    }
    
    public final Ext extRefQualification() {
        Ext e = extRefQualificationImpl();

        ExtFactory nextEF = nextExtFactory();
        Ext e2;
        if (nextEF instanceof GallifreyExtFactory) {
            e2 = ((GallifreyExtFactory) nextEF).extRefQualification();
        } else {
            e2 = nextEF.extNode();
        }

        e = composeExts(e, e2);
        return postExtRefQualification(e);
    }
    
    public final Ext extRestrictionDecl() {
        Ext e = extRestrictionDeclImpl();

        ExtFactory nextEF = nextExtFactory();
        Ext e2;
        if (nextEF instanceof GallifreyExtFactory) {
            e2 = ((GallifreyExtFactory) nextEF).extRestrictionDecl();
        } else {
            e2 = nextEF.extNode();
        }

        e = composeExts(e, e2);
        return postExtRestrictionDecl(e);
    }

    public final Ext extRestrictionBody() {
        Ext e = extRestrictionBodyImpl();

        ExtFactory nextEF = nextExtFactory();
        Ext e2;
        if (nextEF instanceof GallifreyExtFactory) {
            e2 = ((GallifreyExtFactory) nextEF).extRestrictionBody();
        } else {
            e2 = nextEF.extNode();
        }

        e = composeExts(e, e2);
        return postExtRestrictionBody(e);
    }

    public final Ext extRestrictionMember() {
        Ext e = extRestrictionMemberImpl();

        ExtFactory nextEF = nextExtFactory();
        Ext e2;
        if (nextEF instanceof GallifreyExtFactory) {
            e2 = ((GallifreyExtFactory) nextEF).extRestrictionMember();
        } else {
            e2 = nextEF.extNode();
        }

        e = composeExts(e, e2);
        return postExtRestrictionMember(e);
    }

    public final Ext extAllowsStmt() {
        Ext e = extAllowsStmtImpl();

        ExtFactory nextEF = nextExtFactory();
        Ext e2;
        if (nextEF instanceof GallifreyExtFactory) {
            e2 = ((GallifreyExtFactory) nextEF).extAllowsStmt();
        } else {
            e2 = nextEF.extNode();
        }

        e = composeExts(e, e2);
        return postExtAllowsStmt(e);
    }


    protected Ext extPreConditionImpl() {
        return extNode();
    }
    protected Ext extPostConditionImpl() {
        return extNode();
    }
    protected Ext extLocalRefImpl() {
        return extRefQualification();
    }
    protected Ext extUniqueRefImpl() {
        return extRefQualification();
    }
    protected Ext extSharedRefImpl() {
        return extRefQualification();
    }
    protected Ext extRefQualificationImpl() {
        return extNode();
    }
    protected Ext extRestrictionDeclImpl() {
        return extNode();
    }
    protected Ext extRestrictionBodyImpl() {
        return extNode();
    }
    protected Ext extRestrictionMemberImpl() {
        return extNode();
    }
    protected Ext extAllowsStmtImpl() {
        return extRestrictionMember();
    }


    protected Ext postExtPreCondition(Ext e) {
        return postExtNode(e);
    }
    protected Ext postExtPostCondition(Ext e) {
        return postExtNode(e);
    }
    protected Ext postExtLocalRef(Ext e) {
        return postExtRefQualification(e);
    }
    protected Ext postExtUniqueRef(Ext e) {
        return postExtRefQualification(e);
    }
    protected Ext postExtSharedRef(Ext e) {
        return postExtRefQualification(e);
    }
    protected Ext postExtRefQualification(Ext e) {
        return postExtNode(e);
    }
    protected Ext postExtRestrictionDecl(Ext e) {
        return postExtNode(e);
    }
    protected Ext postExtRestrictionBody(Ext e) {
        return postExtNode(e);
    }
    protected Ext postExtRestrictionMember(Ext e) {
        return postExtNode(e);
    }
    protected Ext postExtAllowsStmt(Ext e) {
        return postExtRestrictionMember(e);
    }
}
