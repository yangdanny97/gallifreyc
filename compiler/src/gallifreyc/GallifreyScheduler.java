package gallifreyc;

import gallifreyc.visit.SharedTypeWrapper;
import gallifreyc.translate.*;
import polyglot.ast.NodeFactory;
import polyglot.ext.jl7.JL7Scheduler;
import polyglot.frontend.*;
import polyglot.frontend.goals.*;
import polyglot.types.TypeSystem;
import polyglot.util.InternalCompilerError;

/**
 * {@code CArraySchedule} extends the base scheduler to handle translations of
 * CArray programs to Java.
 */
public class GallifreyScheduler extends JL7Scheduler {
    public GallifreyScheduler(JLExtensionInfo extInfo) {
        super(extInfo);
    }
    
//    @Override
//    public Goal Disambiguated(Job job) {
//        TypeSystem ts = extInfo.typeSystem();
//        NodeFactory nf = extInfo.nodeFactory();
//        Goal g = Disambiguated.create(this, job, ts, nf);
//        try {
//            g.addPrerequisiteGoal(WrapSharedType(job), this);
//        } catch (CyclicDependencyException e) {
//            throw new InternalCompilerError(e);
//        }
//        return g;
//    }
//    
//    @Override
//    public Goal TypeChecked(Job job) {
//        TypeSystem ts = extInfo.typeSystem();
//        NodeFactory nf = extInfo.nodeFactory();
//        Goal g = TypeChecked.create(this, job, ts, nf);
//        try {
//            g.addPrerequisiteGoal(WrapSharedType(job), this);
//        } catch (CyclicDependencyException e) {
//            throw new InternalCompilerError(e);
//          }     
//        return g;
//    }
    
    public Goal RewriteAssignment(Job job) {
        Goal g =
                new VisitorGoal(job,
                                new AssignmentRewriter(job,
                                                   extInfo,
                                                   extInfo.outputExtensionInfo()));
        try {
            g.addPrerequisiteGoal(Serialized(job), this);
        }
        catch (CyclicDependencyException e) {
            throw new InternalCompilerError(e);
        }
        return internGoal(g);
    }
    
//    public Goal WrapSharedType(Job job) {
//        ExtensionInfo extInfo = job.extensionInfo();
//        TypeSystem ts = extInfo.typeSystem();
//        NodeFactory nf = extInfo.nodeFactory();
//        Goal g = new VisitorGoal(job, new SharedTypeWrapper(job, ts, nf));
////        try {
////            g.addPrerequisiteGoal(Disambiguated(job), this);
////        } catch (CyclicDependencyException e) {
////            throw new InternalCompilerError(e);
////        }
////        g.addCorequisiteGoal(Disambiguated(job), this);
//        return internGoal(g);
//    }
    
    @Override
    public Goal CodeGenerated(Job job) {
        Goal g = super.CodeGenerated(job);
        try {
            g.addPrerequisiteGoal(RewriteAssignment(job), this);
        }
        catch (CyclicDependencyException e) {
            throw new InternalCompilerError(e);
        }
        return internGoal(g);
    }

}