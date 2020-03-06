package team5__assignment1.patterns;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CatchClause;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.TryStatement;

import team5__assignment1.handlers.SampleHandler;
import team5__assignment1.visitors.CatchClauseVisitor;
//import team5__assignment1.visitors.Graph;
import team5__assignment1.visitors.Graph;

public class ExceptionFinderTool extends ASTVisitor {HashMap <MethodDeclaration, String> suspectMethods = new HashMap<>();

public static CatchClauseVisitor catchExceptionVisitor; 
public String printMethodName; 
List<String> printTryException = new ArrayList();
List<String> printCatchException = new ArrayList(); 
boolean overCatchChecker= false;

public void findExceptions(IProject project) throws JavaModelException{
	
	IPackageFragment[] allPackages = JavaCore.create(project).getPackageFragments();
	
	for(IPackageFragment myPackage: allPackages) {
		findCatchClauseException(myPackage);
	}
	
	OverCatchAnalysis();
	System.out.println("repitition here?");
}


public static CatchClauseVisitor getCatchExceptionVisitor() {
	return catchExceptionVisitor;
}




private void findCatchClauseException(IPackageFragment myPackageFragment) throws JavaModelException {
	

	for(ICompilationUnit unit : myPackageFragment.getCompilationUnits() ) {
		CompilationUnit parsedCompilationUnit = unitParser(unit);
		
		this.catchExceptionVisitor = new CatchClauseVisitor();
		parsedCompilationUnit.accept(catchExceptionVisitor);
		
		getMethodsWithTargetCatchClauses(catchExceptionVisitor);
			 

	}


}


private HashMap<String, List<String>> getThrownExceptions(CatchClauseVisitor catchExceptionVisitor) {
    // TODO Auto-generated method stub
   
    HashMap<String, List<String>> result = new HashMap();
    List<MethodDeclaration> allMethodList = catchExceptionVisitor.allMethods;
    HashMap<String, List<String>> allInvoked = catchExceptionVisitor.invokedMethod;
    List<MethodDeclaration> thrownMethods = catchExceptionVisitor.thrownMethods;
   
    for(int i=0; i<allMethodList.size(); i++) {
               String methodName = allMethodList.get(i).getName().toString();
              
               if(!allInvoked.containsKey(methodName) && !thrownMethods.contains(methodName)) {
                          MethodDeclaration m = getMethodDeclaration(methodName, catchExceptionVisitor );
                          IMethodBinding methodBinding = m.resolveBinding();
                          ITypeBinding[] exceptions = methodBinding.getExceptionTypes();
                          List<String> exceptionList = new ArrayList();
                         
                          for(int x =0; x<exceptions.length; x++) {
                                     exceptionList.add(exceptions[x].getName());
                          }
                         
              
                          result.put(methodName, exceptionList);
               }
              
              
    }
   
    return null;
}

private MethodDeclaration getMethodDeclaration(String name, CatchClauseVisitor catchExceptionVisitor) {
	List<MethodDeclaration> containsAll=this.catchExceptionVisitor.allMethods; 
	MethodDeclaration correspond = null;

	for (MethodDeclaration e:containsAll) { 
	   String MethodName= e.getName().toString();
	if(MethodName.contains(name)) {

	            correspond= e;

	           break;

	}
 
	 }
	return correspond;


	          
}


private void getMethodsWithTargetCatchClauses(CatchClauseVisitor catchExceptionVisitor) {
	
	for(CatchClause nullCatch: catchExceptionVisitor.getNullCatches()) {
		suspectMethods.put(findCatchMethod(nullCatch), "NullCatch Anti-");
	}
}



private MethodDeclaration findCatchMethod(CatchClause catchClause) {
	// TODO Auto-generated method stub
	return (MethodDeclaration) findParentMethodDeclaration(catchClause);
}


private ASTNode findParentMethodDeclaration(ASTNode node) {
	

	if(node.getParent().getNodeType() == ASTNode.METHOD_DECLARATION) {
		return node.getParent();
	}
	else {
		return findParentMethodDeclaration(node.getParent());
	}
}

@SuppressWarnings("deprecation")
private CompilationUnit unitParser(ICompilationUnit unit) {
	ASTParser parser = ASTParser.newParser(AST.JLS8);
	parser.setKind(ASTParser.K_COMPILATION_UNIT);
	parser.setSource(unit);
	parser.setResolveBindings(true);
	parser.setBindingsRecovery(true);
	parser.setStatementsRecovery(true);
	return (CompilationUnit) parser.createAST(null);
}

public void OverCatchAnalysis() {
	 Graph<String> g = new Graph<>();
	 
	 
	 HashMap<String, List<String>> allInvoked = catchExceptionVisitor.invokedMethod;
	 List<MethodDeclaration> thrownMethods = catchExceptionVisitor.thrownMethods;
	 HashMap<String, TryStatement> tryMethods = catchExceptionVisitor.tryMethods;
	 List<MethodDeclaration> declaredMethods = catchExceptionVisitor.allMethods;
	 //System.out.println("Graph is about to be called");
	 //g.addEdge("A", "B");
	
		 allInvoked.forEach((key, value) -> { 
			 this.printMethodName= key;
				for(int i =0; i<value.size(); i++) {
				//	System.out.println("adding "+key+" "+value.get(i));
					String compare = value.get(i);
					g.addEdge(key, value.get(i));
					
				}

			});
		 
			 List<String> tempTryException = new ArrayList();
			 List<String> tempCatchException = new ArrayList();
			 tryMethods.forEach((key, value) -> {
				 MethodDeclaration method = getMethodDeclaration(key, catchExceptionVisitor);
				// System.out.println("this is the method to check "+method);
					 IMethodBinding mb = method.resolveBinding();
						ITypeBinding[] tB = mb.getExceptionTypes();
						if(tB.length>=1) {
						
							for(int x =0; x<tB.length; x++) {
								tempTryException.add(tB[x].getName());
							//	System.out.println(tempTryException);

					 }
						}
						else {
							allInvoked.forEach((keyI, valueI) -> {
							//	System.out.println(keyI +" with "+ key);
								
									for(int z=0; z<valueI.size(); z++) {
										MethodDeclaration m = getMethodDeclaration(valueI.get(z),this.catchExceptionVisitor);
									
										
											//System.out.println(">>>>> ");
											if(g.isReachable(key, valueI.get(z)) || 
													g.isReachable(valueI.get(z), key)) {
												String find = valueI.get(z);
											//	System.out.println("finddddd "+ find);
												
												for(MethodDeclaration a: declaredMethods) {
													if(a.getName().toString().contains(find)) {
										
													if(a != null && a.thrownExceptionTypes() != null && a.thrownExceptionTypes().size()>=1) {
														for(int b =0; b<m.thrownExceptionTypes().size(); b++) {
															//System.out.println("wow "+ a.thrownExceptionTypes().get(b).toString());
															tempTryException.add(a.thrownExceptionTypes().get(b).toString());
															// System.out.println("wow ");
														}
													}
													
													
													
													else {
														if(a.getBody().statements().size()>=1) {
															
															  for(int y =0; y<a.getBody().statements().size(); y++) {
																  String tempValue = a.getBody().statements().get(y).toString().replace(" ", "_");
																  tempValue = tempValue.replace(".", "_");
																  String [] trial = tempValue.split("_");
																  
																  for(int gs=0; gs<trial.length; gs++) {
																	  if(gs+2<trial.length) {
																		  if(trial[gs].contains("new")&& trial[gs+2].contains("catch") ) {
																			  String v = trial[gs+1];
																			  v = v.replace("();", "");
																			  v = v.replace("}", "");
																			  tempTryException.add(v.trim());
																		  }
																		  
																		  if(trial[gs].contains("java") && trial[gs+1].contains("lang")) {
																			  String v = trial[gs+2];
																			  v = v.replace("();", "");
																			  v = v.replace("}", "");
																			  tempTryException.add(v.trim());
																			  //throw new java.lang.NullPointerException();
																		  }
																	  }
																	  
																		  
																  }
															
														}

													}
												}
											}
					
										 
									}
									
								
											}	
									}});
							
						}
						
						TryStatement val = value;
						
						List<CatchClause> t = val.catchClauses();
						
			
			  if(t.size()>0) { 
				  for(int y =0;y<value.catchClauses().size(); y++ ) { 
					  CatchClause x =(CatchClause) val.catchClauses().get(y);
					  SingleVariableDeclaration caughtE = x.getException();
			//  System.out.println("first caught "+caughtE.getType());
			  
			tempCatchException.add(caughtE.getType().toString());
			 } }
			
			  this.printTryException= tempTryException;
			  this.printCatchException= tempCatchException;
				 if(tempTryException.size()>=0) {
					 if (tempCatchException.contains("Exception")){ 
							overCatchChecker=true;
							 isOverCatch(); 
							 
						 }
						 		
				 if(tempCatchException.size()> tempTryException.size()) { 
					 isOverCatch();
					 }
				 
				 
					 if(tempCatchException.size()== tempTryException.size()) {
						 for (String  tp: tempTryException){  
							 boolean Check=tempCatchException.contains(tp);
							 if (Check ==false) {
								 isOverCatch();
							 }
					 }
					 }
				 }
			  
			  tempTryException.clear();
			  tempCatchException.clear();
			 });   

	 
} 
public void  isOverCatch(){ 
	SampleHandler.printMessage(" Overcatch Exception was detected in Method "+ printMethodName);
	//SampleHandler.printMessage("Exceptions  thrown : " + printTryException);
	SampleHandler.printMessage("Exceptions Handled : "+ printCatchException);
	 
	 
}


public void printExceptions() {
	for(MethodDeclaration declaredMethod: suspectMethods.keySet()) {
		String methodType = suspectMethods.get(declaredMethod);
		SampleHandler.printMessage("The following methods have the "+ methodType+ " pattern.");
		SampleHandler.printMessage(declaredMethod.getName().toString());
	}
}}
