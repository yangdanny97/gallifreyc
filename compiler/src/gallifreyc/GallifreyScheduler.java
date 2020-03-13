package gallifreyc;

import gallifreyc.visit.*;
import gallifreyc.translate.*;
import polyglot.ast.NodeFactory;
import polyglot.ext.jl7.JL7Scheduler;
import polyglot.frontend.*;
import polyglot.frontend.goals.*;
import polyglot.types.TypeSystem;
import polyglot.util.InternalCompilerError;
import polyglot.visit.TypeChecker;

/**
 * {@code GallifreyScheduler} extends the base scheduler to handle translations of
 * Gallifrey programs to Java.
 */
public class GallifreyScheduler extends JL7Scheduler {
    public GallifreyScheduler(GallifreyExtensionInfo extInfo) {
        super(extInfo);
    }
   
    
    public Goal Rewrite(Job job) {
    	GallifreyRewriter rw = new GallifreyRewriter(job, extInfo, extInfo);
        Goal g = new VisitorGoal(job, rw);
        try {
            g.addPrerequisiteGoal(Serialized(job), this);
        }
        catch (CyclicDependencyException e) {
            throw new InternalCompilerError(e);
        }
        return internGoal(g);
    }
    
    
    @Override
    public Goal TypeChecked(Job job) {
        TypeSystem ts = extInfo.typeSystem();
        NodeFactory nf = extInfo.nodeFactory();
        Goal g = new VisitorGoal(job, new GallifreyTypeChecker(job, ts, nf));
        try {
            g.addPrerequisiteGoal(Disambiguated(job), this);
            g.addPrerequisiteGoal(MembersFiltered(job), this);
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
            g.addPrerequisiteGoal(Rewrite(job), this);
        }
        catch (CyclicDependencyException e) {
            throw new InternalCompilerError(e);
        }
        return internGoal(g);
    }

}