package team5__assignment1.visitors;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.core.Signature;
import org.eclipse.jdt.core.dom.*;
import org.eclipse.jdt.internal.core.util.ASTNodeFinder;
public class CatchClauseVisitor extends ASTVisitor {
	
	
	public int catchQuantity=0;
	public int catchSLOC=0;
	public int trySLOC=0;
	public int TryInvokedMethodCount=0;;
	
	
	public int tryCount =0;
	
	public int catchCount = 0;
	public int counter = 0;
	
	int returnCount =0;
	int methodCount =0;
	int defaultCount =0;
	int tryStart;
	int tryEnd;
	int catchStart;
	int catchEnd;
	int continueActionCount=0;
	int logActionCount = 0;
	int abortActionCount = 0;
	int throwCurrentCount = 0;
	int throwWrapCount =0;
	int flowActionCount;
	int cathchRecoverabilityCount = 0;
	
	private Map<Integer,Integer> catchPositions = new HashMap<>();
	private Map<Integer,Integer> tryPositions = new HashMap<>();
	Map<Integer,TryStatement> trys = new HashMap<>();
	private HashSet<CatchClause> nullCatches = new HashSet<>();
	private HashSet<CatchClause> logNullCatches = new HashSet<>();
	private HashSet<CatchClause> doNothingCatches = new HashSet<>();
	private  HashSet<CatchClause> dummyHandlers = new HashSet<>();
	private  HashSet<CatchClause> multiLines = new HashSet<>();
	private  HashSet<CatchClause> catchGenerics = new HashSet<>();
	private  HashSet<TryStatement> nestedTrys = new HashSet<>();
	private  HashSet<CatchClause> replyGetCause = new HashSet<>();
	private  HashSet<CatchClause> incompleteImplementation = new HashSet<>();
	private  HashSet<CatchClause> desctructiveWrapping = new HashSet<>();
	private  HashSet<CatchClause> interruptedException = new HashSet<>();

	private  HashSet<ASTNode> tryScopeNodes = new HashSet<>();
	int nodeCatchComm =0;
	int nodeTryComm=0;
	int tryScopeCount=0;
	
	
	public List<String> nullMethods = new ArrayList();
	   ArrayList <String> DefaultMethods= new ArrayList<>();

	
	public List<MethodDeclaration> allMethods = new ArrayList<>();
	public List<MethodDeclaration> thrownMethods = new ArrayList<>();
	public   Map<String, List<String>> invokedMethod = new HashMap<String, List<String>>();
	public   Map<String, TryStatement> tryMethods = new HashMap<String,TryStatement>();
	public   Map<String, List<String>> catchMethods = new HashMap<String,List<String>>();
	
       String tempName = null;
       TryStatement tryName = null;
       LineComment lineComm;
       List<String> tempList = new ArrayList();
       CompilationUnit cu;

	
    public CatchClauseVisitor() {
    	   nullMethods.add("printStackTrace");
    	   nullMethods.add("print()");
    	   nullMethods.add("println()");
    	   nullMethods.add("System.out.print()");
    	   nullMethods.add("System.out.println()");
    	   nullMethods.add("Log");
    	   nullMethods.add("display");
    	   nullMethods.add("log");
    	   nullMethods.add("Display");
    	   
    	   
 		  
  		 DefaultMethods.add(" fillInStackTrace");
  		 
  		 DefaultMethods.add("addSuppressed");
  		 DefaultMethods.add("getCause()"); 
  		 DefaultMethods.add("getLocalizedMessage");
  		 DefaultMethods.add("getMessage()");
  		  DefaultMethods.add("getStackTrace()");
  		  DefaultMethods.add("printStackTrace()");
  		  DefaultMethods.add("getSuppressed()");
  		  DefaultMethods.add("initCause()");
  		   DefaultMethods.add("printStackTrace()");
  		  DefaultMethods.add("printStackTrace");
  		  DefaultMethods.add("	setStackTrace");
  		  DefaultMethods.add("toString()"); 
    	   
	}
	

	@Override
   	public boolean visit(TryStatement node) {
		//System.out.println("try node "+node);
		int tryStart = node.getStartPosition();
		int tryEnd = tryStart+node.getLength();
		List<CatchClause> c1 = node.catchClauses();
		if(c1.size()!=0) {
			tryEnd = c1.get(0).getStartPosition();
		}
		
		tryPositions.put(tryStart, tryEnd);
		trys.put(tryStart+tryEnd, node);
		

		node.getParent();
		  if(tryName ==null&& tempName!=null) {
		  tryMethods.put(tempName, node);
	
		  } 
		  
		  
		  if(node!=null) { 
			  tryCount++;
			  List<CatchClause> c = node.catchClauses();					  
				catchQuantity= catchQuantity + c.size();

			  
			  tryName = node; 
			  if(node.getParent()!=null) {
				  tryScopeNodes.add(node.getParent());
				  tryScopeCount++;
			  }
			  trySLOC =trySLOC+node.getBody().statements().size() ;
			  }
		 

		  final List<String>tempCatchException = new ArrayList();
		  final List<CatchClause> t = node.catchClauses();
		  if (t.size() > 0) {
			  for (Object c :t){
		  
		  final CatchClause x = (CatchClause)c;
		  final SingleVariableDeclaration caughtE = x.getException();
	
		  tempCatchException.add(caughtE.getType().toString().trim());
		 
		  } 
			  }
		  
		  catchMethods.put(tempName,tempCatchException );
		
		  if(isNestedTry(node)){ 
		
		  nestedTrys.add(node);
		  
		  }
		  
		  if(isInvokedMethod(node)) {
			  TryInvokedMethodCount++;
		  }
		 
   		return super.visit(node);
   	}
	
	private boolean isInvokedMethod(TryStatement node) {

		
		 List<Statement> statements= node.getBody().statements();
	//System.out.println("Try block "+ node);
		   for(Statement s:statements) {
			   String checker= s.toString().trim().replace(" ", "");
			   if(checker.contains("();") || checker.contains(".")&& checker.contains(");")) {
				   
				   TryInvokedMethodCount++;
				}
			   
			   else if(checker.contains("();")) {
				   
				//   TryInvokedMethodCount++;
				}
		   }
		
			return false;
	}
	
	private boolean isNestedTry(TryStatement node) {
	
		 List<Statement> statements= node.getBody().statements();
	  		
		   for(Statement s:statements) {
			   String checker= s.toString().replace(" ", "");
			   if(checker.contains("try")) { 
				   return true;
				   
			   }
		   }
		return false;
	}

	@Override
	public boolean visit(MethodDeclaration node) {
	
		
		  String caller = node.getName().toString(); 
		  tempName =caller;
		  allMethods.add(node); 
		  if(tempName == null && counter ==0) {
}
		  
		  if (node== null || node.equals(null) || node.getBody()==null) { 
			  return false;
		  }
		  
		  
		  for(int i=0; i< node.getBody().statements().size(); i++) {
		  if(node.getBody().statements().get(i).toString().replace(" ","").contains("throw ")){ 
			  thrownMethods.add(node);
		  
		  }
		  
		  }
		  
		  
		  
		  if (tempName != null) { 
		List<String>d = new ArrayList(tempList);
		  
		  if(!d.isEmpty()) {
			  invokedMethod.put(tempName, d);
			  }
		  

		  if(tryName!=null && !tryMethods.containsValue(tryName) ) {
		  tryMethods.put(tempName, tryName); 
		  }
		  
		  tempList.clear();
		  } 
		 
		return true;
	}
	@Override
	public boolean visit(MethodInvocation node) {
	
			
		  String methodName = node.getName().toString();
		
		  if(!nullMethods.contains(methodName)) { 
			  tempList.add(methodName); 
			  }
		  
		  if (tempName != null) { 
			  List<String>d = new ArrayList(tempList);
		  
		  if(!d.isEmpty()) { 
			  invokedMethod.put(tempName, d); 
			  } 
		  }
		 
		return true;
	}
	
	@Override
	public boolean visit(CatchClause node) {
		catchCount++;
	//	System.out.println("this is the catch node "+ node);
		int catchStart = node.getStartPosition();
		int catchEnd = catchStart+node.getLength();
		catchPositions.put(catchStart, catchEnd);
		node.getAST();
		if(node!=null) {
			  catchSLOC =catchSLOC+node.getBody().statements().size() ;

			if (isNullCatch(node)) {
				nullCatches.add(node);
				if(isLogCatch(node)) {
					logNullCatches.add(node);
				}
			}
			
			if(getMethodCount(node)) {
				methodCount++;
			}

			if(isUncompleteImplementation(node)) {
				incompleteImplementation.add(node);
			}
			
			if(abortActionHandler(node)) {
				abortActionCount++;
				
			}
			
			if(isUnRecoverableHandler(node)) {
				cathchRecoverabilityCount++;
			}
			
			if(isContinueHandler(node)) {
				continueActionCount++;
			}
			
			if(isDummyHandler(node)) {
				dummyHandlers.add(node);
			}
			
			if(isMultiLineLog(node)) {
				multiLines.add(node);
			}
			
			if(isCatchAndDoNotthing(node)) {
				doNothingCatches.add(node);
			}
			
			if(isCatchGeneric(node)) {
				catchGenerics.add(node);
			}
			
			if(isReplyOnGetCause(node)) {
				replyGetCause.add(node);
			}
			
			if(isDestructive(node)) {
				desctructiveWrapping.add(node);
			}
			
			if(InterruptedException(node)){
				interruptedException.add(node);
			}
		}
		
		return true;
	}

	private boolean isUnRecoverableHandler(CatchClause node) {



		 List<Statement> statements= node.getBody().statements();
	  		
		   for(Statement s:statements) {
			   
			   String checker= s.toString().replace(" ", "");
			//   System.out.println("catch statement "+ checker);
			   if(!checker.contains(".abort(")&& !checker.contains(".exit(")) { 
				   return true;
				   
			   }
		   }
		
		return false;
	
	
	
	
	}


	private boolean abortActionHandler(CatchClause node) {



		 List<Statement> statements= node.getBody().statements();
	  		
		   for(Statement s:statements) {
			   
			   String checker= s.toString().replace(" ", "");
			//   System.out.println("catch statement "+ checker);
			   if(checker.contains(".abort")) { 
				   return true;
				   
			   }
		   }
		
		return false;
	
	
	
	
	}


	public int getAbortActionCount() {
		return abortActionCount;
	}

	public boolean getMethodCount(CatchClause node){



		 List<Statement> statements= node.getBody().statements();
	  		
		   for(Statement s:statements) {
			   
			   String checker= s.toString().replace(" ", "");
			 //  System.out.println("catch statement "+ checker);
			   if(checker.contains("();")||checker.contains(".(") && checker.contains(");")){ 
			//	   System.out.println("catch clause with invocation "+ node);
				   return true;
				   
			   }
			
		   }
		   
		  
	   
		
		return false;
	
	
	
	
	}
	private boolean isContinueHandler(CatchClause node) {


		 List<Statement> statements= node.getBody().statements();
	  	
		   for(Statement s:statements) {
		   String checker= s.toString().replace(" ", "");

		   if(checker=="continue;") { 
			   return true;
			   
		   }
	   
		   }
		return false;
	
	
	
	}


	@Override
	public boolean visit(ContinueStatement node) {
		// TODO Auto-generated method stub
		return super.visit(node);
	}


	private void getSlOCOnNode(CatchClause node) {
		// TODO Auto-generated method stub
		 List<Statement> statements= node.getBody().statements();
	  		
		   for(Statement s:statements) {
			   String checker= s.toString().replace(" ", "");
			   if(!checker.contains("//")) {
				   catchSLOC++;
				}
		   }
		
	}
	private boolean InterruptedException(CatchClause node) {
		 List<Statement> statements= node.getBody().statements();
	
			String ExceptionType= node.getException().toString(); 
			if(ExceptionType=="InterruptedException") { 
				if(statements.isEmpty()) {
					return true;
				}
				else {
				   for(Statement s:statements) {
					
					   String checker= s.toString().trim().replace(" ", "");;
					   if(checker.contains("throws") || checker.contains("break")) { 
						
						  return false;
					   }
					  
					   
						   
					   }} 
			
			} 
		
		
		// TODO Auto-generated method stub
		return false;
	}
	private boolean isDestructive(CatchClause node) {
		String exception = node.getException().toString();
		
		 List<Statement> statements= node.getBody().statements();
			for(Statement s:statements) {
				   String checker= s.toString().trim().replace(" ", "");
				   if(checker.contains("throw ") && !checker.contains("new")) { 
					   throwCurrentCount++;
					   
				   }
				   if(checker.contains("throw") && checker.contains("exception")) { 
					   throwWrapCount++;
					   
				   }
				   
				   if(checker.contains("throws") && checker.contains("new")) {
					   return true;
					   
				   }
					   
				   } 
		return false;
	}
	private boolean isUncompleteImplementation(CatchClause node) {

		 List<Statement> statements= node.getBody().statements();
	  		
		   for(Statement s:statements) {
			   String checker= s.toString().replace(" ", "");
			   if(checker.contains("//fix") ||checker.contains("to ")|| checker.contains("//FIX")   || checker.contains("//todo") ) { 
				   return true;
				   
			   }
		   }
		
		return false;
	
	}
	private boolean isReplyOnGetCause(CatchClause node) {
		// TODO Auto-generated method stub

		 List<Statement> statements= node.getBody().statements();
	  		
		   for(Statement s:statements) {
			   String checker= s.toString().trim().replace(" ", "");
			   if(checker.contains(".getCause()")) { 
				   return true;
				   
			   }
		   }
		
		return false;
	}
	private boolean isCatchGeneric(CatchClause node) {
		// TODO Auto-generated method stub				
		
		String ExceptionType= node.getException().toString(); 
		
		if(ExceptionType.contains("Exception")|| ExceptionType.contains("Throwable")||ExceptionType.contains("RuntimeException")
				|| ExceptionType.contains("SystemException") || ExceptionType.contains("Error")
				) {
			return true;
		}
		
		return false;
	}
	private boolean isCatchAndDoNotthing(CatchClause node) {
		// TODO Auto-generated method stub
		if(node.getBody().statements().size()==0) {
			return true;
		}
		
		return false;
	}
	private boolean isMultiLineLog(CatchClause node) {
		// TODO Auto-generated method stub
		int multiCount =0;
		
		for (int i = 0; i < node.getBody().statements().size(); i++) {
			String testWord = node.getBody().statements().get(i).toString().replace(" ", "");
			
				multiCount = extracted(multiCount, testWord);
		
			
			
		}
		
		if(multiCount>1) {
			return true;
		}
		
		return false;
	}
	private boolean isLogCatch(CatchClause node) {
		
			for (int i = 0; i < node.getBody().statements().size(); i++) {
				String testWord = node.getBody().statements().get(i).toString().replace(" ", "");
				
				for(String logWord:nullMethods) {
					if(testWord.contains(logWord)) {
						return true;
					}
				}
				
				
			}
			return false;
		
	}
	private boolean isNullCatch(CatchClause node) {
		
		for (int i = 0; i < node.getBody().statements().size(); i++) {
			if(DefaultMethods.contains(node.getBody().statements().get(i).toString().replace(" ", ""))) {
			defaultCount++;	
			}
			
			if (node.getBody().statements().get(i).toString().replace(" ", "").contains("return")) {
				returnCount++;
			}
			
			if (node.getBody().statements().get(i).toString().replace(" ", "").contains("returnnull")) {
				return true;
			}
		}

		return false;
	}
	private boolean isDummyHandler(CatchClause node) {
		int dummyCount=0;
		
		for (int i = 0; i < node.getBody().statements().size(); i++) {
			String testWord = node.getBody().statements().get(i).toString().replace(" ", "");
			
			
				dummyCount = dummyCount+extracted(dummyCount, testWord);
			
			
		}
		
		if(dummyCount==node.getBody().statements().size() && dummyCount>0) {
			return true;
		}
		
		if(dummyCount>0) {
			logActionCount++;
		}
		return false;
	
	}
	private int extracted(int dummyCount, String testWord) {
		for(String logWord:nullMethods) {
		if(testWord.contains(logWord)) {
			dummyCount++;
		}
		}
		return dummyCount;
	}
	public HashSet<CatchClause> getDummyHandlers() {
		return dummyHandlers;
	}
	public HashSet<CatchClause> getMultiLines() {
		return multiLines;
	}
	public HashSet<CatchClause> getNullCatches() {
		return nullCatches;
	}
	public HashSet<CatchClause> getDoNothingCatches() {
		return doNothingCatches;
	}
	public HashSet<CatchClause> getLogNullCatches() {
		return logNullCatches;
	}
	public int getTrySLOC() {
		return trySLOC;
	}
	public int getCatchQuantity() {
		return catchQuantity;
	}
	public int getTryCount() {
		return tryCount;
	}
	public int getCatchSLOC() {
		return catchSLOC;
	}
	public int getCatchCount() {
		return catchCount;
	}
	public HashSet<CatchClause> getCatchGenerics() {
		return catchGenerics;
	}
	public HashSet<TryStatement> getNestedTrys() {
		return nestedTrys;
	}
	public HashSet<CatchClause> getReplyGetCause() {
		return replyGetCause;
	}
	public HashSet<CatchClause> getIncompleteImplementation() {
		return incompleteImplementation;
	}
	public HashSet<CatchClause> getDesctructiveWrapping() {
		return desctructiveWrapping;
	}
	public int getTryInvokedMethodCount() {
		return TryInvokedMethodCount;
	}
	public HashSet<CatchClause> getInterruptedException() {
		return interruptedException;
	}
	public HashSet<ASTNode> getTryScopeNodes() {
		return tryScopeNodes;
	}
	public int getTryScopeCount() {
		return tryScopeCount;
	}
	public int getTryStart() {
		return tryStart;
	}
	public int getTryEnd() {
		return tryEnd;
	}
	public int getCatchStart() {
		return catchStart;
	}
	public int getCatchEnd() {
		return catchEnd;
	}
	public int getContinueActionCount() {
		return continueActionCount;
	}
	public int getLogActionCount() {
		return logActionCount;
	}


	public Map<Integer, Integer> getCatchPositions() {
		return catchPositions;
	}
	public Map<Integer, Integer> getTryPositions() {
		return tryPositions;
	}
	


	public int getMethodCount() {
		return methodCount;
	}


	public int getReturnCount() {
		return returnCount;
	}


	public int getThrowCurrentCount() {
		return throwCurrentCount;
	}


	public int getThrowWrapCount() {
		return throwWrapCount;
	}


	public Map<Integer, TryStatement> getTrys() {
		return trys;
	}


	public int getDefaultCount() {
		return defaultCount;
	}


	public void setTrys(Map<Integer, TryStatement> trys) {
		this.trys = trys;
	}


	public int getCathchRecoverabilityCount() {
		return cathchRecoverabilityCount;
	}


	public void setThrowWrapCount(int throwWrapCount) {
		this.throwWrapCount = throwWrapCount;
	}


	public void setTryScopeCount(int tryScopeCount) {
		this.tryScopeCount = tryScopeCount;
	}
	@Override
	public void endVisit(CatchClause node) {
		// TODO Auto-generated method stub
		super.endVisit(node);
	}
	@Override
	public void endVisit(MethodDeclaration node) {
		// TODO Auto-generated method stub
		super.endVisit(node);
	}
	@Override
	public void endVisit(MethodInvocation node) {
		// TODO Auto-generated method stub
		super.endVisit(node);
	}
	@Override
	public void endVisit(TryStatement node) {
		// TODO Auto-generated method stub
		super.endVisit(node);
	}}
