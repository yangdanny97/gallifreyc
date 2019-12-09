package gallifreyc.ast;

import gallifreyc.visit.RefQualificationAdder;
import gallifreyc.visit.SharedTypeWrapper;
import polyglot.ast.*;
import polyglot.ext.jl7.ast.J7Lang;

public interface GallifreyLang extends J7Lang {
    RefQualificationAdder addRefQualificationEnter(Node n, RefQualificationAdder v);
    Node addRefQualification(Node n, RefQualificationAdder v);
    
    SharedTypeWrapper wrapSharedTypeEnter(Node n, SharedTypeWrapper v);
    Node wrapSharedType(Node n, SharedTypeWrapper v);
}
