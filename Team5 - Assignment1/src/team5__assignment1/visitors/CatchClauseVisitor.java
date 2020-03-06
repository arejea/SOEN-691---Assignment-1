package team5__assignment1.visitors;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.eclipse.jdt.core.Signature;
import org.eclipse.jdt.core.dom.*;
import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.core.util.ASTNodeFinder;
public class CatchClauseVisitor extends ASTVisitor {

	private HashSet<CatchClause> nullCatches = new HashSet<>();
	private HashSet<CatchClause> overCatches = new HashSet<>();

	private HashMap<MethodDeclaration, ITypeBinding[]> checkedExceptions = new HashMap<>();
	private HashMap<MethodDeclaration, List<String>> unCheckedExceptions = new HashMap<>();
	List<String> nullMethods = new ArrayList();
	
		//{"printStackTrace", "println"};
	public List<MethodDeclaration> allMethods = new ArrayList<>();
	public List<MethodDeclaration> thrownMethods = new ArrayList<>();
	public   HashMap<String, List<String>> invokedMethod = new HashMap<String, List<String>>();
	public   HashMap<String, TryStatement> tryMethods = new HashMap<String,TryStatement>();
       String tempName = null;
       TryStatement tryName = null;
       List<String> tempList = new ArrayList();
       
	
       public CatchClauseVisitor() {
    	   nullMethods.add("printStackTrace");
    	   nullMethods.add("print()");
    	   nullMethods.add("System.out.print()");
    	   nullMethods.add("System.out.println()");
    	   
	}
	@Override
   	public boolean visit(TryStatement node) {
   		// TODO Auto-generated method stub
    	  if(node!=null) {
    		  tryName = node;  
    	  }
    	  
   		return super.visit(node);
   	}
	@Override
	public boolean visit(MethodDeclaration node) {
		allMethods.add(node);
		
		if	(node== null || node.equals(null) || node.getBody()==null) {
		return false;
	}
		
		for(int i=0; i< node.getBody().statements().size(); i++) {

            if(node.getBody().statements().get(i).toString().contains("thrown ")){

                       thrownMethods.add(node);

            }

 }
	
		  if (tempName != null) {
              List<String>d = new ArrayList(tempList);
              
              if(!d.isEmpty()) {
              invokedMethod.put(tempName, d);
              }
              if(tryName!=null) {
            	  tryMethods.put(tempName, tryName); 
              }
              
              tempList.clear();
   }
   String caller = node.getName().toString();
   tempName = caller;
	
		return true;
	}

	@Override
	public boolean visit(MethodInvocation node) {
        String methodName = node.getName().toString();
        if(!nullMethods.contains(methodName)) {
        	
            tempList.add(methodName);
        }
   
		return true;
	}

	

    public void printData() {
    	tryMethods.forEach((title, values) -> {
                   System.out.println("==========================================");
                   System.out.println(title+" "+values);
        });
}

	@Override
	public boolean visit(CatchClause node) {
		// TODO Auto-generated method stub
		//node.getException();

		MethodInvocationVisitor methodInvocationVisitor = new MethodInvocationVisitor("LogCatchSwitch");
		node.accept(methodInvocationVisitor);
	
		if (isNullCatch(node)) {
			nullCatches.add(node);
		}

		if (isOverCatch(node)) {
			overCatches.add(node);
		}
		


		return true;
	}

	private boolean isOverCatch(CatchClause node) {
	
		
		return true;
	}

	private boolean isNullCatch(CatchClause node) {

		for (int i = 0; i < node.getBody().statements().size(); i++) {
			if (node.getBody().statements().get(i).toString().replace(" ", "").contains("returnnull")) {
				return true;
			}
		}

		return false;
	}

	public HashSet<CatchClause> getNullCatches() {
		return nullCatches;
	}

}
