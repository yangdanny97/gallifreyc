package gallifreyc.ast;

import polyglot.ast.Ext;
import polyglot.ast.ExtFactory;
import polyglot.ext.jl7.ast.JL7ExtFactory;

/**
 * Extension factory for gallifreyc extension.
 */
public interface GallifreyExtFactory extends JL7ExtFactory {
    // TODO: Declare any factory methods for new extension nodes.
    Ext extPreCondition();
    Ext extPostCondition();
    Ext extLocalRef();
    Ext extUniqueRef();
    Ext extSharedRef();
    Ext extRefQualification();
}
