package gallifreyc.ast;

import gallifreyc.visit.RefQualificationAdder;
import polyglot.ast.*;
import polyglot.util.InternalCompilerError;
import polyglot.util.SerialVersionUID;

public class GallifreyExt extends Ext_c implements GallifreyOps {
    private static final long serialVersionUID = SerialVersionUID.generate();

    public static GallifreyExt ext(Node n) {
        Ext e = n.ext();
        while (e != null && !(e instanceof GallifreyExt)) {
            e = e.ext();
        }
        if (e == null) {
            throw new InternalCompilerError("No Gallifrey extension object for node "
                    + n + " (" + n.getClass() + ")", n.position());
        }
        return (GallifreyExt) e;
    }

    @Override
    public final GallifreyLang lang() {
        return GallifreyLang_c.instance;
    }

    @Override
    public RefQualificationAdder addRefQualificationEnter(RefQualificationAdder v) {
        return v;
    }

    @Override
    public Node addRefQualification(RefQualificationAdder v) {
//        System.out.printf("Calling addRefQualificationEnter on this='%s' whose class is %s\n", node(), node().getClass().getName());
        return node();
    }

    // TODO:  Override operation methods for overridden AST operations.
}
