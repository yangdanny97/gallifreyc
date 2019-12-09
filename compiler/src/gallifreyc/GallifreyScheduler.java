package gallifreyc;

import gallifreyc.visit.RefQualificationAdder;
import gallifreyc.visit.SharedTypeWrapper;
import polyglot.ast.NodeFactory;
import polyglot.ext.jl7.JL7Scheduler;
import polyglot.frontend.CyclicDependencyException;
import polyglot.frontend.ExtensionInfo;
import polyglot.frontend.JLExtensionInfo;
import polyglot.frontend.JLScheduler;
import polyglot.frontend.Job;
import polyglot.frontend.goals.Goal;
import polyglot.frontend.goals.TypeChecked;
import polyglot.frontend.goals.VisitorGoal;
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

    public Goal AddRefQualification(Job job) {
        ExtensionInfo extInfo = job.extensionInfo();
        TypeSystem ts = extInfo.typeSystem();
        NodeFactory nf = extInfo.nodeFactory();
        Goal g = new VisitorGoal(job, new RefQualificationAdder(job, ts, nf));
        try {
            g.addPrerequisiteGoal(Disambiguated(job), this);
        } catch (CyclicDependencyException e) {
            throw new InternalCompilerError(e);
        }
        return internGoal(g);
    }
    
    @Override
    public Goal TypeChecked(Job job) {
        TypeSystem ts = extInfo.typeSystem();
        NodeFactory nf = extInfo.nodeFactory();
        Goal g = TypeChecked.create(this, job, ts, nf);
        try {
            g.addPrerequisiteGoal(AddRefQualification(job), this);
            g.addPrerequisiteGoal(WrapSharedType(job), this);
        } catch (CyclicDependencyException e) {
            throw new InternalCompilerError(e);
          }     
        return g;
    }
    
    public Goal WrapSharedType(Job job) {
        ExtensionInfo extInfo = job.extensionInfo();
        TypeSystem ts = extInfo.typeSystem();
        NodeFactory nf = extInfo.nodeFactory();
        Goal g = new VisitorGoal(job, new SharedTypeWrapper(job, ts, nf));
        try {
            g.addPrerequisiteGoal(Disambiguated(job), this);
        } catch (CyclicDependencyException e) {
            throw new InternalCompilerError(e);
        }
        return internGoal(g);
    }
}