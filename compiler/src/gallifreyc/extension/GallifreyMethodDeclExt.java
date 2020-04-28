package gallifreyc.extension;

import java.util.ArrayList;
import java.util.List;

import gallifreyc.ast.PostCondition;
import gallifreyc.ast.PreCondition;
import gallifreyc.ast.RefQualification;
import gallifreyc.ast.RefQualifiedTypeNode;
import gallifreyc.types.GallifreyType;
import gallifreyc.types.GallifreyTypeSystem;
import polyglot.ast.Formal;
import polyglot.ast.MethodDecl;
import polyglot.ast.Node;
import polyglot.ast.ProcedureDecl;
import polyglot.ast.TypeNode;
import polyglot.ext.jl5.ast.JL5Ext;
import polyglot.ext.jl5.ast.JL5FormalExt;
import polyglot.ext.jl5.ast.JL5MethodDeclExt;
import polyglot.ext.jl5.types.JL5Flags;
import polyglot.ext.jl5.types.JL5MethodInstance;
import polyglot.ext.jl5.types.JL5TypeSystem;
import polyglot.ext.jl5.types.TypeVariable;
import polyglot.types.Flags;
import polyglot.types.MethodInstance;
import polyglot.types.ParsedClassType;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.UnknownType;
import polyglot.util.SerialVersionUID;
import polyglot.visit.TypeBuilder;
import polyglot.visit.TypeChecker;

// extends method declarations to hold an optional pre/post condition, and a flag for whether it's a test method
public class GallifreyMethodDeclExt extends JL5MethodDeclExt {
    private static final long serialVersionUID = SerialVersionUID.generate();
    
    public PreCondition pre;
    public PostCondition post;
    // Is this MethodDecl a test method (inside a restriction)
    public boolean isTest;

    PreCondition pre() {
        return pre;
    }
    
    PostCondition post() {
        return post;
    }
    
    boolean isTest() {
        return isTest;
    }
    
    @Override
    public MethodDecl node() {
        return (MethodDecl) super.node();
    }
    
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

        MethodDecl md = (MethodDecl) this.node();
        
        TypeNode returnType = md.returnType();
        if (!(returnType instanceof RefQualifiedTypeNode)) {
        	throw new SemanticException("return type must be ref qualified: " + md.name(), md.position());
        }
        RefQualification galReturn = ((RefQualifiedTypeNode) returnType).qualification();
        
        List<RefQualification> inputQ = new ArrayList<>();
        for (Formal f : md.formals()) {
            if (!(f instanceof RefQualifiedTypeNode)) {
            	throw new SemanticException("param types must be ref qualified: " + md.name(), md.position());
            }
            RefQualification fQ = ((RefQualifiedTypeNode) f).qualification();
            inputQ.add(fQ);
        }
        
        if (ct.flags().isInterface()) {
            flags = flags.Public().Abstract();
        }

        MethodInstance mi =
                ts.methodInstance(md.position(),
                                  ct,
                                  flags,
                                  ts.unknownType(md.position()),
                                  md.name(),
                                  formalTypes,
                                  throwTypes,
                                  typeParams,
                                  inputQ,
                                  galReturn);
        ct.addMethod(mi);
        return md.methodInstance(mi);
    }
}
