package gallifreyc.extension;

import polyglot.ast.Ext;
import polyglot.ext.jl7.ast.JL7ExtFactory;

/**
 * Extension factory for gallifreyc extension.
 */
public interface GallifreyExtFactory extends JL7ExtFactory {
    Ext extPreCondition();

    Ext extPostCondition();

    Ext extIsolatedRef();

    Ext extMoveRef();

    Ext extLocalRef();

    Ext extSharedRef();

    Ext extRefQualifiedTypeNode();

    Ext extRestrictionId();

    Ext extMatchBranch();

    Ext extMatchRestriction();

    Ext extTransition();

    // Restrictions
    Ext extRestrictionDecl();

    Ext extRestrictionBody();

    Ext extAllowsStmt();

    Ext extRestrictionUnionDecl();

    Ext extWhenStmt();

    Ext extMergeDecl();
}
