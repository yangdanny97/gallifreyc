package gallifreyc.ast;

import gallifreyc.visit.RefQualificationAdder;
import polyglot.ast.*;
import polyglot.ext.jl7.ast.J7Lang;

public interface GallifreyLang extends J7Lang {
    RefQualificationAdder addRefQualificationEnter(Node n, RefQualificationAdder v);

    Node addRefQualification(Node n, RefQualificationAdder v);
}
