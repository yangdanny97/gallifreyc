package gallifreyc;

import gallifreyc.visit.*;
import gallifreyc.translate.*;
import polyglot.ast.NodeFactory;
import polyglot.ext.jl7.JL7Scheduler;
import polyglot.frontend.*;
import polyglot.frontend.goals.*;
import polyglot.types.TypeSystem;
import polyglot.util.InternalCompilerError;
import polyglot.visit.ConstantChecker;

/**
 * {@code GallifreyScheduler} extends the base scheduler to handle translations
 * of Gallifrey programs to Java.
 */
public class GallifreyScheduler extends JL7Scheduler {
    public GallifreyScheduler(GallifreyExtensionInfo extInfo) {
        super(extInfo);
    }

    /*
     * PASS ORDERING: Validated <<RewriteFieldInitPass>> <<ANormalizePass>>
     * <<FinalRewritePass>> CodeGenerated
     */

    @Override
    public Goal TypeChecked(Job job) {
        TypeSystem ts = extInfo.typeSystem();
        NodeFactory nf = extInfo.nodeFactory();
        Goal g = new VisitorGoal(job, new GallifreyTypeChecker(job, ts, nf));
        try {
            g.addPrerequisiteGoal(Disambiguated(job), this);
            g.addPrerequisiteGoal(MembersFiltered(job), this);
        } catch (CyclicDependencyException e) {
            throw new InternalCompilerError(e);
        }
        return internGoal(g);
    }

    @Override
    public Goal ConstantsChecked(Job job) {
        TypeSystem ts = extInfo.typeSystem();
        NodeFactory nf = extInfo.nodeFactory();
        Goal g = new VisitorGoal(job, new ConstantChecker(job, ts, nf));
        try {
            g.addPrerequisiteGoal(TypeChecked(job), this);
        } catch (CyclicDependencyException e) {
            throw new InternalCompilerError(e);
        }
        return internGoal(g);
    }

    // hoist field initializers to separate block
    public Goal RewriteFieldInitPass(Job job) {
        FieldInitRewriter rw = new FieldInitRewriter(job, extInfo, extInfo);
        Goal g = new VisitorGoal(job, rw);
        try {
            g.addPrerequisiteGoal(ConstantsChecked(job), this);
            g.addPrerequisiteGoal(Validated(job), this);
        } catch (CyclicDependencyException e) {
            throw new InternalCompilerError(e);
        }
        return internGoal(g);
    }

    // A-Normalize Field/ArrayAccess/Function Calls
    public Goal ANormalizePass(Job job) {
        ANormalizer rw = new ANormalizer(job, extInfo, extInfo);
        Goal g = new VisitorGoal(job, rw);
        try {
            g.addPrerequisiteGoal(RewriteFieldInitPass(job), this);
        } catch (CyclicDependencyException e) {
            throw new InternalCompilerError(e);
        }
        return internGoal(g);
    }

    // autoboxing for Shared/Unique, nulling out moves
    public Goal FinalRewritePass(Job job) {
        GallifreyRewriter rw = new GallifreyRewriter(job, extInfo, extInfo);
        Goal g = new VisitorGoal(job, rw);
        try {
            g.addPrerequisiteGoal(ANormalizePass(job), this);
        } catch (CyclicDependencyException e) {
            throw new InternalCompilerError(e);
        }
        return internGoal(g);
    }

    @Override
    public Goal CodeGenerated(Job job) {
        Goal g = super.CodeGenerated(job);
        try {
            g.addPrerequisiteGoal(FinalRewritePass(job), this);
        } catch (CyclicDependencyException e) {
            throw new InternalCompilerError(e);
        }
        return internGoal(g);
    }

}