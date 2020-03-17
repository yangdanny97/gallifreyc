package gallifreyc.extension;

import polyglot.ast.Ext;
import polyglot.ast.ExtFactory;
import polyglot.ext.jl7.ast.JL7ExtFactory;

/**
 * Extension factory for gallifreyc extension.
 */
public interface GallifreyExtFactory extends JL7ExtFactory {
    Ext extPreCondition();
    Ext extPostCondition();
    Ext extLocalRef();
    Ext extUniqueRef();
    Ext extMoveRef();
    Ext extSharedRef();
    Ext extRefQualification();
    Ext extRefQualifiedTypeNode();
    Ext extRestrictionId();
    Ext extMatchBranch();
    Ext extMatchRestriction();
    Ext extTransition();
    Ext extMove();
    // Restrictions
    Ext extRestrictionDecl();
    Ext extRestrictionBody();
    Ext extRestrictionMember();
    Ext extAllowsStmt();
    Ext extRestrictionUnionDecl();
}
