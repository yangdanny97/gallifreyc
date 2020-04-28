package gallifreyc.extension;

import java.util.ArrayList;
import java.util.List;

import gallifreyc.ast.RefQualification;
import gallifreyc.ast.RefQualifiedTypeNode;
import gallifreyc.types.GallifreyTypeSystem;
import polyglot.ast.ConstructorDecl;
import polyglot.ast.Formal;
import polyglot.ast.MethodDecl;
import polyglot.ast.Node;
import polyglot.ast.ProcedureDecl;
import polyglot.ast.TypeNode;
import polyglot.ext.jl5.ast.JL5ConstructorDeclExt;
import polyglot.ext.jl5.ast.JL5Ext;
import polyglot.ext.jl5.ast.JL5FormalExt;
import polyglot.ext.jl5.types.JL5Flags;
import polyglot.ext.jl5.types.TypeVariable;
import polyglot.types.ConstructorInstance;
import polyglot.types.Flags;
import polyglot.types.MethodInstance;
import polyglot.types.ParsedClassType;
import polyglot.types.SemanticException;
import polyglot.types.UnknownType;
import polyglot.util.SerialVersionUID;
import polyglot.visit.TypeBuilder;

public class GallifreyConstructorDeclExt extends JL5ConstructorDeclExt {
	private static final long serialVersionUID = SerialVersionUID.generate();
	
    @Override
    public Node buildTypes(TypeBuilder tb) throws SemanticException {
        ProcedureDecl pd = (ProcedureDecl) node();

        GallifreyTypeSystem ts = (GallifreyTypeSystem) tb.typeSystem();

        ParsedClassType ct = tb.currentClass();

        if (ct == null) {
            return pd;
        }

        boolean isVarArgs = false;
        List<UnknownType> formalTypes = new ArrayList<>(pd.formals().size());
        for (int i = 0; i < pd.formals().size(); i++) {
            formalTypes.add(ts.unknownType(pd.position()));
            Formal f = pd.formals().get(i);
            JL5FormalExt fext = (JL5FormalExt) JL5Ext.ext(f);
            if (fext.isVarArg()) isVarArgs = true;
        }

        List<UnknownType> throwTypes = new ArrayList<>(pd.throwTypes().size());
        for (int i = 0; i < pd.throwTypes().size(); i++) {
            throwTypes.add(ts.unknownType(pd.position()));
        }

        List<TypeVariable> typeParams = new ArrayList<>(this.typeParams.size());
        for (int i = 0; i < this.typeParams.size(); i++) {
            typeParams.add(ts.unknownTypeVariable(pd.position()));
        }

        Flags flags = pd.flags();
        if (isVarArgs) {
            flags = JL5Flags.setVarArgs(flags);
        }
        
        // qualifications
        ConstructorDecl cd = (ConstructorDecl) this.node();
        
        List<RefQualification> inputQ = new ArrayList<>();
        for (Formal f : cd.formals()) {
            if (!(f instanceof RefQualifiedTypeNode)) {
            	throw new SemanticException("param types must be ref qualified: " + cd.name(), cd.position());
            }
            RefQualification fQ = ((RefQualifiedTypeNode) f).qualification();
            inputQ.add(fQ);
        }
        
        ConstructorInstance ci =
                ts.constructorInstance(cd.position(),
                                       ct,
                                       flags,
                                       formalTypes,
                                       throwTypes,
                                       typeParams,
                                       inputQ);
        ct.addConstructor(ci);

        return cd.constructorInstance(ci).flags(flags);
    }
}