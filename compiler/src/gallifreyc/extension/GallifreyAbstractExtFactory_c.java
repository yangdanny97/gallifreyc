package gallifreyc.extension;

import polyglot.ast.Ext;
import polyglot.ast.ExtFactory;
import polyglot.ext.jl7.ast.JL7AbstractExtFactory_c;

// new methods to create extension objects for new AST nodes

public abstract class GallifreyAbstractExtFactory_c extends JL7AbstractExtFactory_c implements GallifreyExtFactory {

    public GallifreyAbstractExtFactory_c() {
        super();
    }

    public GallifreyAbstractExtFactory_c(ExtFactory nextExtFactory) {
        super(nextExtFactory);
    }

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

    public final Ext extMoveRef() {
        Ext e = extMoveRefImpl();

        ExtFactory nextEF = nextExtFactory();
        Ext e2;
        if (nextEF instanceof GallifreyExtFactory) {
            e2 = ((GallifreyExtFactory) nextEF).extMoveRef();
        } else {
            e2 = nextEF.extNode();
        }

        e = composeExts(e, e2);
        return postExtMoveRef(e);
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

    public final Ext extRestrictionId() {
        Ext e = extRestrictionIdImpl();

        ExtFactory nextEF = nextExtFactory();
        Ext e2;
        if (nextEF instanceof GallifreyExtFactory) {
            e2 = ((GallifreyExtFactory) nextEF).extSharedRef();
        } else {
            e2 = nextEF.extNode();
        }

        e = composeExts(e, e2);
        return postExtRestrictionId(e);
    }

    public final Ext extRefQualifiedTypeNode() {
        Ext e = extRefQualifiedTypeNodeImpl();

        ExtFactory nextEF = nextExtFactory();
        Ext e2;
        if (nextEF instanceof GallifreyExtFactory) {
            e2 = ((GallifreyExtFactory) nextEF).extRefQualifiedTypeNode();
        } else {
            e2 = nextEF.extTypeNode();
        }

        e = composeExts(e, e2);
        return postExtRefQualifiedTypeNode(e);
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

    public final Ext extRestrictionUnionDecl() {
        Ext e = extRestrictionUnionDeclImpl();

        ExtFactory nextEF = nextExtFactory();
        Ext e2;
        if (nextEF instanceof GallifreyExtFactory) {
            e2 = ((GallifreyExtFactory) nextEF).extRestrictionUnionDecl();
        } else {
            e2 = nextEF.extNode();
        }

        e = composeExts(e, e2);
        return postExtRestrictionUnionDecl(e);
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

    public final Ext extTransition() {
        Ext e = extTransitionImpl();

        ExtFactory nextEF = nextExtFactory();
        Ext e2;
        if (nextEF instanceof GallifreyExtFactory) {
            e2 = ((GallifreyExtFactory) nextEF).extTransition();
        } else {
            e2 = nextEF.extStmt();
        }

        e = composeExts(e, e2);
        return postExtTransition(e);
    }

    public final Ext extMatchBranch() {
        Ext e = extMatchBranchImpl();

        ExtFactory nextEF = nextExtFactory();
        Ext e2;
        if (nextEF instanceof GallifreyExtFactory) {
            e2 = ((GallifreyExtFactory) nextEF).extMatchBranch();
        } else {
            e2 = nextEF.extStmt();
        }

        e = composeExts(e, e2);
        return postExtMatchBranch(e);
    }

    public final Ext extMatchRestriction() {
        Ext e = extMatchRestrictionImpl();

        ExtFactory nextEF = nextExtFactory();
        Ext e2;
        if (nextEF instanceof GallifreyExtFactory) {
            e2 = ((GallifreyExtFactory) nextEF).extMatchRestriction();
        } else {
            e2 = nextEF.extStmt();
        }

        e = composeExts(e, e2);
        return postExtMatchRestriction(e);
    }

    public final Ext extWhenStmt() {
        Ext e = extWhenStmtImpl();

        ExtFactory nextEF = nextExtFactory();
        Ext e2;
        if (nextEF instanceof GallifreyExtFactory) {
            e2 = ((GallifreyExtFactory) nextEF).extWhenStmt();
        } else {
            e2 = nextEF.extStmt();
        }

        e = composeExts(e, e2);
        return postExtWhenStmt(e);
    }

    public final Ext extMergeDecl() {
        Ext e = extMergeDeclImpl();

        ExtFactory nextEF = nextExtFactory();
        Ext e2;
        if (nextEF instanceof GallifreyExtFactory) {
            e2 = ((GallifreyExtFactory) nextEF).extMergeDecl();
        } else {
            e2 = nextEF.extTerm();
        }

        e = composeExts(e, e2);
        return postExtMergeDecl(e);
    }

    // IMPL

    protected Ext extPreConditionImpl() {
        return extNode();
    }

    protected Ext extPostConditionImpl() {
        return extNode();
    }

    protected Ext extUniqueRefImpl() {
        return extNode();
    }

    protected Ext extLocalRefImpl() {
        return extNode();
    }

    protected Ext extMoveRefImpl() {
        return extNode();
    }

    protected Ext extSharedRefImpl() {
        return extNode();
    }

    protected Ext extRestrictionIdImpl() {
        return extNode();
    }

    protected Ext extRefQualifiedTypeNodeImpl() {
        return extTypeNode();
    }

    protected Ext extRestrictionDeclImpl() {
        return extNode();
    }

    protected Ext extRestrictionUnionDeclImpl() {
        return extNode();
    }

    protected Ext extRestrictionBodyImpl() {
        return extNode();
    }

    protected Ext extAllowsStmtImpl() {
        return extNode();
    }

    protected Ext extTransitionImpl() {
        return extStmt();
    }

    protected Ext extMatchBranchImpl() {
        return extStmt();
    }

    protected Ext extMatchRestrictionImpl() {
        return extStmt();
    }

    protected Ext extWhenStmtImpl() {
        return extStmt();
    }

    protected Ext extMergeDeclImpl() {
        return extTerm();
    }

    // POST

    protected Ext postExtPreCondition(Ext e) {
        return postExtNode(e);
    }

    protected Ext postExtPostCondition(Ext e) {
        return postExtNode(e);
    }

    protected Ext postExtUniqueRef(Ext e) {
        return postExtNode(e);
    }

    protected Ext postExtLocalRef(Ext e) {
        return postExtNode(e);
    }

    protected Ext postExtSharedRef(Ext e) {
        return postExtNode(e);
    }

    protected Ext postExtMoveRef(Ext e) {
        return postExtNode(e);
    }

    protected Ext postExtRestrictionId(Ext e) {
        return postExtNode(e);
    }

    protected Ext postExtRefQualifiedTypeNode(Ext e) {
        return postExtTypeNode(e);
    }

    protected Ext postExtRestrictionDecl(Ext e) {
        return postExtNode(e);
    }

    protected Ext postExtRestrictionUnionDecl(Ext e) {
        return postExtNode(e);
    }

    protected Ext postExtRestrictionBody(Ext e) {
        return postExtNode(e);
    }

    protected Ext postExtAllowsStmt(Ext e) {
        return postExtNode(e);
    }

    protected Ext postExtTransition(Ext e) {
        return postExtStmt(e);
    }

    protected Ext postExtMatchBranch(Ext e) {
        return postExtNode(e);
    }

    protected Ext postExtMatchRestriction(Ext e) {
        return postExtStmt(e);
    }

    protected Ext postExtWhenStmt(Ext e) {
        return postExtStmt(e);
    }

    protected Ext postExtMergeDecl(Ext e) {
        return postExtTerm(e);
    }
}
