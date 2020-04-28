package team5__assignment1.Patterns;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.HashSet;
import java.util.List;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IOrdinaryClassFile;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CatchClause;
import org.eclipse.jdt.core.dom.Comment;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.TryStatement;

import team5__assignment1.handlers.SampleHandler;
import team5__assignment1.visitors.CatchClauseVisitor;
import team5__assignment1.visitors.CommentVisitor;
import team5__assignment1.visitors.Graph;

public class ExceptionFinderTool extends ASTVisitor {
	
	int returnNullCount = 0;
	int overCatchCount = 0;
	int abortCount = 0;
	String root;
	
	int catchRecoverabilityCount =0;
	int abortActionCount = 0;
	int emptyActionCount =0;
	int defaultMethodCount =0;
	int continueActionCount = 0;
	int catchAndDoNothCount = 0;
	int logActionCount =0;
	int methodCount = 0;
	int returnCount = 0;
	int throwCurrentCount = 0;
	int throwWrapCount =0;
	int nestedCount =0;
	int throwNewCount =0;
	
	int specificCount =0;
	
	int subsumptionCount =0;
	
	HashMap<String,Integer> flowActionNumber= new HashMap<>(); 
	HashMap<String,Double> flowActionPercentages= new HashMap<>(); 
	HashMap<String,Integer> flowStrategyNumber= new HashMap<>(); 
	HashMap<String,Double> flowStrategyPercentages= new HashMap<>();
	Map<Integer,Integer> tryP= new HashMap<>();
	Map<Integer,Integer> catchP= new HashMap<>();
	
	int catchGenericCount = 0;
	int dummyHandlerCount = 0;
	int logAndReturnNullCount = 0;
	int multiLineLogCount = 0;
	int nestedTryCount = 0;
	int replyOnGetCauseCount = 0;
	int incompleteImplCount = 0;
	int destructiveWrappingCount = 0;
	int interExcCount = 0;
	int tryInvokeCount = 0;
	ICompilationUnit cu;
	int level;
	

	Map<MethodDeclaration, String> suspectMethods = new HashMap<MethodDeclaration, String>();
	Map<MethodDeclaration, String> suspectMethodO = new HashMap<MethodDeclaration, String>();
	Map<MethodDeclaration, String> suspectMethodA = new HashMap<MethodDeclaration, String>();
	Map<MethodDeclaration, String> suspectMethodC = new HashMap<MethodDeclaration, String>();
	Map<MethodDeclaration, String> suspectMethodCG = new HashMap<MethodDeclaration, String>();
	Map<MethodDeclaration, String> suspectMethodD = new HashMap<MethodDeclaration, String>();
	Map<MethodDeclaration, String> suspectMethodR = new HashMap<MethodDeclaration, String>();
	Map<MethodDeclaration, String> suspectMethodM = new HashMap<MethodDeclaration, String>();
	Map<MethodDeclaration, String> suspectMethodN = new HashMap<MethodDeclaration, String>();
	Map<MethodDeclaration, String> suspectMethodRG = new HashMap<MethodDeclaration, String>();
	Map<MethodDeclaration, String> suspectMethodI = new HashMap<MethodDeclaration, String>();
	Map<MethodDeclaration, String> suspectMethodDW = new HashMap<MethodDeclaration, String>();
	Map<MethodDeclaration, String> suspectMethodIE = new HashMap<MethodDeclaration, String>();
	Map<MethodDeclaration, String> suspectMethodCon = new HashMap<MethodDeclaration, String>();

	
	Graph<String> g = new Graph<>();
	Map<String, List<String>> allInvoked; 
	Map<String, TryStatement> tryMethods ; 
	Map<String, List<String>> catchMethods;
	List<MethodDeclaration> allMethods ;
	Map<Integer,TryStatement>t ;
	List<Integer> tryCallDepth = new ArrayList();
	  	
	
	private HashSet<String> overCatches = new HashSet<>();
	private HashSet<String> abortCatches = new HashSet<>();
	int catchQuantity = 0;
	int tryQuantity = 0;
	int catchSloc=0;
	int trySloc=0;
	int catchLoc=0;
	int tryLoc=0;
	int tryLineCom = 0;
	int catchLineCom = 0;
	int tryscopeCount = 0;
	//int tryScope = 0;

	  
	public void findExceptions(final IProject project) throws CoreException, JavaModelException, IOException {
		
		if(project!=null && project.isAccessible()&&project.isOpen() && project.isNatureEnabled(JavaCore.NATURE_ID)) {
			final IPackageFragment[] allPackages = JavaCore.create(project).getPackageFragments();
		
			if(allPackages !=null &&allPackages.length>0) {
				for (final IPackageFragment myPackage : allPackages) {
					if(myPackage!=null) {
						findCatchClauseException(myPackage);
					}
					
				}
				
				
			}

		
		}
	}

	private void findCatchClauseException(final IPackageFragment myPackageFragment) throws JavaModelException, IOException {

			for (final ICompilationUnit unit : myPackageFragment.getCompilationUnits()) {
								cu=	unit;
								//System.out.println("parse  "+ myPackageFragment.getCompilationUnits()[0]);
								hashSetReset();
					final CompilationUnit parsedCompilationUnit = unitParser(unit);
					System.out.println("in class "+ parsedCompilationUnit.getJavaElement().getPath());
			
					String str = root+parsedCompilationUnit.getJavaElement().getPath().toString();
					
					SampleHandler.printMessageLine(str);
		
					CatchClauseVisitor catchExceptionVisitor = new CatchClauseVisitor();
		
					parsedCompilationUnit.accept(catchExceptionVisitor);
						tryP = new HashMap(catchExceptionVisitor.getTryPositions());
						catchP = new HashMap(catchExceptionVisitor.getCatchPositions());
						
						System.out.println("str--"+str);
						String converted = readFileToString(str);
						parse(converted);
						counter(catchExceptionVisitor);
							
							g = new Graph<>();
							
						 this.tryMethods = new HashMap (catchExceptionVisitor.tryMethods); 
						 this.allInvoked = new HashMap (catchExceptionVisitor.invokedMethod); 
						 this.allMethods = new ArrayList (catchExceptionVisitor.allMethods); 
						this.catchMethods = new HashMap (catchExceptionVisitor.catchMethods);
						//this.t =catchExceptionVisitor.getTrys();
						  
						if(tryMethods.size()>=1) {
						
							 getMethodsWithTargetCatchClauses(catchExceptionVisitor);
							
							  OverCatchAnalysis();
							 
							  if(overCatches.size()>0) {
								  for(final String overCatch: overCatches) {
									  if(getMethodDeclaration(overCatch)!=null) {
								  suspectMethodO.put(getMethodDeclaration(overCatch) , "OverCatch Anti-");
								  } 
								  } 
							  }
							  
							  if(abortCatches.size() > 0) {
								  for(final String abortCatch: abortCatches) {
									  if(getMethodDeclaration(abortCatch)!=null) {
								  suspectMethodA.put(getMethodDeclaration(abortCatch) , "AbortAndOverCatch Anti-");
								  } 
								  }
							  }
							  

							  
						}
						 
						printExceptions();
						if(tryMethods.size()>=1) {
						FlowHandlingCalculator();
						}
						//printReport();
						printFlowReport();
						
				
			}
		}
	
	private void printFlowReport() {
		// TODO Auto-generated method stub
		SampleHandler.printMessage(" ,"+getCatchQuantity());
		  for(Entry<String, Double> entry : flowActionPercentages.entrySet()) {
			    String key = entry.getKey();
			    Double value = entry.getValue();
			    
			SampleHandler.printMessage(" ,"+value);
		}
		  SampleHandler.printMessageLine("");
	}

	private void parse(String str) {
		
		Map<Integer,Integer> lineComs = new HashMap();
		Map<Integer,Integer> blockComs = new HashMap<>();
		ASTParser parser = ASTParser.newParser(AST.JLS8);
		parser.setSource(str.toCharArray());
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
 
		final CompilationUnit cu = (CompilationUnit) parser.createAST(null);
 
		for (Comment comment : (List<Comment>) cu.getCommentList()) {
			CommentVisitor lC =new CommentVisitor(cu, str);
			comment.accept(lC);
			 for(Entry<Integer, Integer> entry : lC.getLineComs().entrySet()) {
				    int key = entry.getKey();
				    int value = entry.getValue();
				    
				    lineComs.put(key, value);
			
			 }
			 for(Entry<Integer, Integer> entry : lC.getBlockComs().entrySet()) {
				    int key = entry.getKey();
				    int value = entry.getValue();
				    
				    blockComs.put(key, value);
			
			 }
		
		
		}
	
		commentCountCheck(lineComs, blockComs, tryP, catchP,str);
		
	}

	private String readFileToString(String str) throws IOException {
		StringBuilder fileData = new StringBuilder(1000);
		BufferedReader reader = new BufferedReader(new FileReader(str));
 
		char[] buf = new char[10];
		int numRead = 0;
		while ((numRead = reader.read(buf)) != -1) {
			String readData = String.valueOf(buf, 0, numRead);
			fileData.append(readData);
			buf = new char[1024];
		}
 
		reader.close();
		return fileData.toString();
	}


	private void FlowHandlingCalculator() {
		
		
		eachActionValuePercentageCalculation();
		
		
	}

	private void eachActionValuePercentageCalculation() {
		
		
		double catchRecover= percentageCalc(catchRecoverabilityCount); 
		 flowActionNumber.put("SpecificCount", catchRecoverabilityCount);
		 flowActionPercentages.put("SpecificStrategyPercent", catchRecover);
		
		
		double specificPercent= percentageCalc(specificCount); 
		 flowActionNumber.put("SpecificCount", specificCount);
		 flowActionPercentages.put("SpecificStrategyPercent", specificPercent);
		 
		 double subsumptionPercent= percentageCalc(catchGenericCount); 
		 flowActionNumber.put("SubsumptionCount", catchGenericCount);
		 flowActionPercentages.put("SubsumptionPercent", subsumptionPercent);
		 
		 
			 double AbortPercent= percentageCalc(abortActionCount); 
			 flowActionNumber.put("abortActionCount", abortActionCount);
			 flowActionPercentages.put("abortActionPercent", AbortPercent);
			
			 
			 double ContinuePercent=percentageCalc(continueActionCount); 
			 flowActionNumber.put("continueActionCount", continueActionCount);

			 flowActionPercentages.put("continueActionPercent", ContinuePercent);
				
			 
				double EmptyPercent=percentageCalc(emptyActionCount);
			    flowActionPercentages.put("emptyActionCount", EmptyPercent);
			    
			    double NestedTryPercent=percentageCalc(nestedCount); 
				 flowActionPercentages.put("NestedTryPercent", NestedTryPercent);
				 
				
			     double ThrowNewPercent= percentageCalc(throwNewCount);
			     flowActionPercentages.put("ThrowNewPercent", ThrowNewPercent);
			     
			    
			     double toDoCount= percentageCalc(incompleteImplCount);
			     flowActionPercentages.put("toDoCount", toDoCount);
				
			
			double DefaultPercent=percentageCalc(defaultMethodCount);
			 flowActionPercentages.put("DefaultPercent", DefaultPercent);
			 
		    
		    
		    double LogPercent= percentageCalc(logActionCount);
		    flowActionPercentages.put(" logActionCount",  LogPercent);
		   
		     double  MethodPercent= percentageCalc(methodCount);
		     flowActionPercentages.put("MethodCountPercent", MethodPercent);
		    
			 double ReturnPercent=percentageCalc(returnCount); 
			 flowActionPercentages.put("ReturnPercent", ReturnPercent);
			 
			 double   ThrowCurrentPercent=percentageCalc(throwCurrentCount);
			 flowActionPercentages.put("ThrowCurrentPercent", ThrowCurrentPercent);
			
			 double ThrowWrapPercent=percentageCalc(throwWrapCount);
			 flowActionPercentages.put("ThrowWrapPercent", ThrowWrapPercent);
			
			  		
	}

	private double percentageCalc(int value) {
		
		if(catchQuantity==0 || value ==0) {
			return 0;
		}
		
		double answer= (value*100)/catchQuantity;

		return answer;
		
	}

	private void hashSetReset() {
		
		suspectMethods = new HashMap<MethodDeclaration, String>();
		suspectMethodO = new HashMap<MethodDeclaration, String>();
		suspectMethodA = new HashMap<MethodDeclaration, String>();
		suspectMethodC = new HashMap<MethodDeclaration, String>();
		suspectMethodCG = new HashMap<MethodDeclaration, String>();
		suspectMethodD = new HashMap<MethodDeclaration, String>();
		suspectMethodR = new HashMap<MethodDeclaration, String>();
		suspectMethodM = new HashMap<MethodDeclaration, String>();
		suspectMethodN = new HashMap<MethodDeclaration, String>();
		suspectMethodRG = new HashMap<MethodDeclaration, String>();
		suspectMethodI = new HashMap<MethodDeclaration, String>();
		suspectMethodDW = new HashMap<MethodDeclaration, String>();
		suspectMethodIE = new HashMap<MethodDeclaration, String>();
		tryCallDepth = new ArrayList();
	  	
		
		overCatches = new HashSet<>();
		abortCatches = new HashSet<>();
		
		catchQuantity = 0;
		tryQuantity = 0;
		catchSloc=0;
		trySloc=0;
		catchLoc=0;
		tryLoc=0;
		tryLineCom = 0;
		catchLineCom = 0;
		tryscopeCount = 0;
		//int tryScope = 0;
		
		
		returnNullCount = 0;
		overCatchCount = 0;
		int abortCount = 0;
		
		int catchRecoverabilityCount =0;
		int abortActionCount = 0;
		int emptyActionCount =0;
		//int defaultCount =0;
		continueActionCount = 0;
		catchAndDoNothCount = 0;
		logActionCount =0;
		methodCount = 0;
		returnCount = 0;
		throwCurrentCount = 0;
		throwWrapCount =0;
		nestedCount =0;
		throwNewCount =0;
		
		specificCount =0;
		
		subsumptionCount =0;
		
		flowActionNumber= new HashMap<>(); 
		flowActionPercentages= new HashMap<>(); 
		
		
		catchGenericCount = 0;
		dummyHandlerCount = 0;
		logAndReturnNullCount = 0;
		multiLineLogCount = 0;
		int nestedTryCount = 0;
		int replyOnGetCauseCount = 0;
		int incompleteImplCount = 0;
		int destructiveWrappingCount = 0;
		int interExcCount = 0;
		int tryInvokeCount = 0;
		
	}

	private void commentCountCheck(Map<Integer, Integer> lineComs, Map<Integer, Integer> blockComs,
			Map<Integer, Integer> tryP, Map<Integer, Integer> catchP, String str) {
		
		List<Integer>incompleteList = new ArrayList<>();
		incompleteImplCount =0;
		int tryCom = 0;
		int catchCom =0;
		
		if(lineComs.size()>0 ) {
			
				 for(Entry<Integer, Integer> entry : lineComs.entrySet()) {
					    int key = entry.getKey();
					    int value = entry.getValue();
					    String com = str.substring(key, value);

					    if(tryP.size()>0) {
					    	  for(Entry<Integer, Integer> entryT : tryP.entrySet()) {
								    int keyT = entryT.getKey();
								    int valueT = entryT.getValue();
								    
							
								    if(keyT<key&&valueT>value ) {
								    	tryCom++;
							
								    }
							 }
					    }
					    
						if(catchP.size()>0) {


					    	  for(Entry<Integer, Integer> entryC : catchP.entrySet()) {
								    int keyC = entryC.getKey();
								    int valueC = entryC.getValue();
								    
							
								    if(keyC<key&&valueC>value ) {
								    	

								    	   if(com.contains("//fix") ||com.contains("to ")|| com.contains("//FIX")   
								    			   || com.contains("//todo") ) { 
								    	//	   System.out.println("comment for catch "+ str.substring(keyC, valueC));
								    		   incompleteList.add(keyC);
								    		
								    	}
								    	
								    	catchCom++;
								    }
							 }				    
							
						}				    
				
				 }
			
		}
		
		if(blockComs.size()>0) {
			

			
			 for(Entry<Integer, Integer> entry : blockComs.entrySet()) {
				    int key = entry.getKey();
				    int value = entry.getValue();
				    String com = str.substring(key, value);
				    if(tryP.size()>0) {
				    	  for(Entry<Integer, Integer> entryT : tryP.entrySet()) {
							    int keyT = entryT.getKey();
							    int valueT = entryT.getValue();
							    
						
							    if(keyT<key&&valueT>value ) {
							    //	System.out.println("comment for try "+ source.substring(keyT, valueT));
							    	tryCom++;
							    	
					
							    }
						 }
				    }
				    
					if(catchP.size()>0) {


				    	  for(Entry<Integer, Integer> entryC : catchP.entrySet()) {
							    int keyC = entryC.getKey();
							    int valueC = entryC.getValue();
							    
						
							    if(keyC<key&&valueC>value ) {
							    	   if(com.contains("//fix") ||com.contains("to ")|| com.contains("//FIX")   
							    			   || com.contains("//todo") ) { 
							    		 //  System.out.println("comment for catch "+ str.substring(keyC, valueC));
							    		   incompleteList.add(keyC);

							    	}
							    	catchCom++;
							    }
						 }
			    
						
					}
				 			    
			
			 }
			
		}
		incompleteImplCount = incompleteList.size();
		
		tryLineCom=tryCom;
		catchLineCom = 	catchCom;	
		
	}

	private void counter(CatchClauseVisitor catchExceptionVisitor) {
		 tryscopeCount= catchExceptionVisitor.getTryScopeCount();

		tryQuantity = catchExceptionVisitor.getTryCount();
		trySloc = catchExceptionVisitor.getTrySLOC();
		tryLoc = trySloc+tryLineCom;
		catchQuantity = catchExceptionVisitor.getCatchCount();
		catchSloc = catchExceptionVisitor.getCatchSLOC();
		catchLoc = catchSloc+catchLineCom;
		tryInvokeCount = catchExceptionVisitor.getTryInvokedMethodCount();		
		multiLineLogCount = catchExceptionVisitor.getMultiLines().size();
		dummyHandlerCount = catchExceptionVisitor.getDummyHandlers().size();
		defaultMethodCount = catchExceptionVisitor.getDefaultCount();
		throwNewCount = destructiveWrappingCount;
		nestedCount =nestedTryCount; 
		emptyActionCount = catchAndDoNothCount;
		subsumptionCount = catchGenericCount;
		continueActionCount = catchExceptionVisitor.getContinueActionCount();	
		abortActionCount = catchExceptionVisitor.getAbortActionCount();
		logActionCount = catchExceptionVisitor.getLogActionCount();
		methodCount = catchExceptionVisitor.getMethodCount();
		returnCount = catchExceptionVisitor.getReturnCount();
		throwCurrentCount = catchExceptionVisitor.getThrowCurrentCount();
		throwWrapCount = catchExceptionVisitor.getThrowWrapCount();
		catchRecoverabilityCount = catchExceptionVisitor.getCathchRecoverabilityCount();
	}

	private MethodDeclaration getMethodDeclaration(final String name) { 
		  final List<MethodDeclaration> containsAll = allMethods; 
	  MethodDeclaration correspond = null;
	  
	  if (containsAll.size() > 0) {
	  
	  for (final MethodDeclaration e : containsAll) {
		  final String MethodName =e.getName().toString(); 
		  if (MethodName != null && MethodName.contains(name) || name.equalsIgnoreCase(MethodName)) {
	  
	  correspond = e;
	  
	  break;
	  
	  }
	  
	  } } return correspond;
	  
	  }
	
	private void getMethodsWithTargetCatchClauses(final CatchClauseVisitor catchExceptionVisitor) {

		for (final CatchClause nullCatch : catchExceptionVisitor.getNullCatches()) {
			final MethodDeclaration m = findCatchMethod(nullCatch);
			if (m != null) {
				suspectMethods.put(m, "CatchAndReturnNull Anti-");
			}
		}

		for (final CatchClause nullLogCatch : catchExceptionVisitor.getLogNullCatches()) {
			final MethodDeclaration m = findCatchMethod(nullLogCatch);
			if (m != null) {
				suspectMethodR.put(m, "LogAndReturnNull Anti-");
			}
		}

		for (final CatchClause catchAndDoNothing : catchExceptionVisitor.getDoNothingCatches()) {
			final MethodDeclaration m = findCatchMethod(catchAndDoNothing);
			if (m != null) {
				suspectMethodC.put(m, "Catch And Do Nothing Anti-");
			}
		}

		for (final CatchClause dummyCatch : catchExceptionVisitor.getDummyHandlers()) {
			final MethodDeclaration m = findCatchMethod(dummyCatch);
			if (m != null) {
				suspectMethodD.put(m, "DummyHandler Anti-");
			}
		}

		for (final CatchClause multiLineLog : catchExceptionVisitor.getMultiLines()) {
			final MethodDeclaration m = findCatchMethod(multiLineLog);
			if (m != null) {
				suspectMethodD.put(m, "MultiLineLog Anti-");
			}
		}

		for (final CatchClause catchGeneric : catchExceptionVisitor.getCatchGenerics()) {
			final MethodDeclaration m = findCatchMethod(catchGeneric);
			if (m != null) {
				suspectMethodCG.put(m, "CatchGeneric Anti-");
			}
		}

		for (final TryStatement nestedTry : catchExceptionVisitor.getNestedTrys()) {
			final MethodDeclaration m = findCatchMethod(nestedTry);
			if (m != null) {
				suspectMethodN.put(m, "NestedTry Anti-");
			}
		}

		for (final CatchClause replOnGetCause : catchExceptionVisitor.getReplyGetCause()) {
			final MethodDeclaration m = findCatchMethod(replOnGetCause);
			if (m != null) {
				suspectMethodRG.put(m, "ReplyOnGetCause Anti-");
			}
		}
		
		for (final CatchClause incompletImple : catchExceptionVisitor.getIncompleteImplementation()) {
			final MethodDeclaration m = findCatchMethod(incompletImple);
			if (m != null) {
				suspectMethodI.put(m, "IncompletImplementation Anti-");
			}
		}
		
		for (final CatchClause desctructiveWrapping : catchExceptionVisitor.getDesctructiveWrapping()) {
			final MethodDeclaration m = findCatchMethod(desctructiveWrapping);
			if (m != null) {
				suspectMethodDW.put(m, "DestructiveWrapping Anti-");
			}
		}
		
		for (final CatchClause interuptedExc : catchExceptionVisitor.getInterruptedException()) {
			final MethodDeclaration m = findCatchMethod(interuptedExc);
			if (m != null) {
				suspectMethodIE.put(m, "InterruptedException Anti-");
			}
		}

	}

	private MethodDeclaration findCatchMethod(final CatchClause catchClause) {
		return (MethodDeclaration) findParentMethodDeclaration(catchClause);
	}

	private MethodDeclaration findCatchMethod(final TryStatement tryStatement) {
		// TODO Auto-generated method stub
		return (MethodDeclaration) findParentMethodDeclaration(tryStatement);
	}
	

	private ASTNode findParentMethodDeclaration(final ASTNode node) {
		
		if (node != null && node.getParent() != null && node.getParent().getNodeType() == ASTNode.METHOD_DECLARATION) {
			return node.getParent();
		} else if (node != null) {
			return findParentMethodDeclaration(node.getParent());
		}

		else {
			return null;
		}
	}

	@SuppressWarnings("depraciation")
	private CompilationUnit unitParser(final ICompilationUnit unit) {
		if(unit!=null ) {
			final ASTParser parser = ASTParser.newParser(AST.JLS8);
			parser.setKind(ASTParser.K_COMPILATION_UNIT);
			parser.setSource(unit);
			parser.setResolveBindings(true);
			parser.setBindingsRecovery(true);
			parser.setStatementsRecovery(true);
			CompilationUnit result = (CompilationUnit) parser.createAST(null);
			if(result!=null&&result.getLength()>0) {
				return result;
			}
		}
		
		
		
		return null;
	}
	

	
	public void OverCatchAnalysis() { 

		  if(tryMethods.size()>=1) { 
			  if(allInvoked.size()>=1) {
				  
		
				  for(Entry<String, List<String>> entry : allInvoked.entrySet()) {
					    String key = entry.getKey();
					    List<String> value = entry.getValue();
					    
					    for (int i = 0; i < value.size(); i++) {
							  value.get(i); 
							  g.addEdge(key, value.get(i)); 
							  }
					    
				  }
				
			  }
		  
		  
		  final List<String> tempTryException = new ArrayList(); 
		  final List<String>tempCatchException = new ArrayList();
		  
		  
		  for(Entry<String, TryStatement> entry : tryMethods.entrySet()) {
			    String key = entry.getKey();
			    TryStatement value = entry.getValue();

			    
				  final MethodDeclaration method =getMethodDeclaration(key);
				  if(method!=null  && containsTry(method)) { 
					  final IMethodBinding mb = method.resolveBinding();
				  if(mb!=null && mb.getExceptionTypes()!=null &&  mb.getExceptionTypes().length>0) {
					  final ITypeBinding[] tB = mb.getExceptionTypes();  
					  if (tB.length >= 1) {
						  
						  for (int x = 0; x < tB.length; x++) { 

							  tempTryException.add(tB[x].getName());

						  } 
						  } 
				  }
				  
				  else {
					  List<String> foundExceptions = new ArrayList<>();
					  setLevel(0);
				  if(allInvoked.size()>=1) {
					  
					  for(Entry<String, List<String>> entry2 : allInvoked.entrySet()) {
						    String keyI = entry2.getKey();
						    List<String> valueI = entry2.getValue();
				  
				  if(valueI.size()>=1&& !foundExceptions.contains(key)) {
					  level++;
					  for (int z = 0; z < valueI.size(); z++) {
						 
				  final MethodDeclaration m = getMethodDeclaration(valueI.get(z));
				  
				  if (g.hasVertex(key)&& m!=null && g.isReachable(key, valueI.get(z)) && key.trim()!= valueI.get(z).trim())
				  {
				  final IMethodBinding mb2 = m.resolveBinding(); 
				  
				  if(mb2!=null && mb2.getExceptionTypes()!=null && mb2.getExceptionTypes().length>=1 ) {
					  final ITypeBinding[]tB2 =mb2.getExceptionTypes();
					  if(mb2!=null && tB2.length>=1) { 
						  
						  tryCallDepth.add(level);
						  foundExceptions.add(valueI.get(z));
						  foundExceptions.add(key);
						 setLevel(0);
						  for (int x = 0; x < tB2.length; x++) {

					  tempTryException.add(tB2[x].getName().toString().trim());
					  
					  }
					  
					  }  
				  }
				  
				  else {
					  
					  if (m.getBody()!=null &&m.getBody().statements().size() >= 1) {
						  
						  for (int y = 0; y < m.getBody().statements().size(); y++) { 
							  String tempValue = m.getBody().statements().get(y).toString() .replace(" ", "_");
							  tempValue = tempValue.replace(".", "_"); 
							  final String[] trial = tempValue.split("_");
						  
						  for (int gs = 0; gs < trial.length; gs++) { 
							  if (gs + 2 < trial.length) { 
								 if  (trial[gs].contains("new") && trial[gs + 2].contains("catch")) { String v =
						  trial[gs + 1]; v = v.replace("();", ""); v = v.replace("}", "");
						 
						  tempTryException.add(v.trim()); 
						  tryCallDepth.add(getLevel());
						  foundExceptions.add(valueI.get(z));
						  foundExceptions.add(key);
						  setLevel(0);
						  }
						  
						  else if (trial[gs].contains("java") && trial[gs + 1].contains("lang")) {
						  String v = trial[gs + 2]; v = v.replace("();", ""); v = v.replace("}", "");
						 
						  tempTryException.add(v.trim()); 
						 
						  tryCallDepth.add(getLevel());
						  foundExceptions.add(valueI.get(z));
						  foundExceptions.add(key);
						  setLevel(0);
						  }
						  }
						  
						  }
						  
						  }
						  
						  } 
				  }
				  
				  
				  
				  
				  }
				  } 
					  } 
				  }
				  
				  } }
				  
				if(catchMethods.containsKey(key)) {
					  final List<String> t = catchMethods.get(key);
					  if (t!=null && t.size() > 0) {
						  for (int y = 0; y < t.size(); y++) {
					 
					  tempCatchException.add(t.get(y));
					  
					  } 
						  }
				  
					
					  for(final String c: tempCatchException) {
					  
					  if(!tempTryException.contains(c)) { }
					  }
					  
					  }
					  
				}
				  
				  
				  
				  if (tempTryException.size() >= 1 && tempCatchException.size()>=1) {
				  
		
				  for(String caughtEx:tempCatchException) {
					  
					  if (tempCatchException.contains("Exception") ||tempCatchException.contains("RuntimeException")) {
						  
						 
						  overCatches.add(key); //isOverCatch(key);
						  if(isAbort(key)) {
							  abortCatches.add(key);
						  }
						  
						  } 
				  }
				  
				 
				  
				  if (tempCatchException.size() > tempTryException.size()) {
				  
				  overCatches.add(key); //isOverCatch(key); 
				  if(isAbort(key)) {
					  abortCatches.add(key);
				  }
				  }
				  
				  else if (tempCatchException.size() == tempTryException.size()) { 
					  int error =0;
					  for(final String tryEx: tempTryException) {
				  if(!tempCatchException.contains(tryEx)) { 
					  error++; }
				  }
				  
				  if(error>1) { 
					  overCatches.add(key); 
					  if(isAbort(key)) {
						  abortCatches.add(key);
					  }
					  }
				  
				  if(error==0) {
					  specificCount++;
				  }
				  
				  }
				  } 
				  tempCatchException.clear(); 
				  tempTryException.clear(); 
				  
				  
			}
		  
		  
		  }


		  else { 
			  //System.out.println("no try methods here"); }
		  }
		
	  
	  }


	private boolean containsTry(final MethodDeclaration method) {
		// TODO Auto-generated method stub

		if (method == null || method.getBody() == null || method.getBody().statements() == null) {
			return false;
		}

		else {
			int tryB = 0;
			int catchB = 0;
			for (int x = 0; x < method.getBody().statements().size(); x++) {
				final String stat = method.getBody().statements().get(x).toString().replace(" ", "").trim();
				if (stat.contains("try")) {
					tryB++;
				}

				if (stat.contains("catch(")) {
					catchB++;
				}

				if (tryB > 0 && catchB > 0) {
					return true;
				}

			}
		}
		return false;
	}

	private boolean isAbort(String key) {
	  
	  if(tryMethods.containsKey(key)) { 
		  TryStatement t =tryMethods.get(key); 
		  for(int i= 0; i< t.catchClauses().size(); i++) { 
			  CatchClause x = (CatchClause) t.catchClauses().get(i); 
			  for(int s = 0; s<x.getBody().statements().size();s++) { 
				  String statM = x.getBody().statements().get(s).toString().trim().replace(" ", "");
	  if(statM.contains(".exit") || statM.toString().contains(".abort"))
	  { 		  
		  return true; 
		  } 
	  }
	  
	  } 
		  } return false;
		  
	  }
	 
	public void printExceptions() {
		returnNullCount = 0; abortCount= 0; logAndReturnNullCount= 0;
		overCatchCount=0; catchAndDoNothCount=0; catchGenericCount = 0;
		nestedTryCount= 0; replyOnGetCauseCount =0;
		destructiveWrappingCount = 0; interExcCount = 0;
		
		for (final MethodDeclaration declaredMethod : suspectMethods.keySet()) {
			final String methodType = suspectMethods.get(declaredMethod);
			//SampleHandler.printMessage("The following methods have the " + methodType + " pattern.");
		//	SampleHandler.printMessage(declaredMethod.getName().toString());
			returnNullCount++;
		//SampleHandler.printMessage(declaredMethod.toString());

		}
		for (final MethodDeclaration declaredMethod : suspectMethodO.keySet()) {
			final String methodType = suspectMethodO.get(declaredMethod);
			if (methodType != null && methodType.length() > 0) {
				overCatchCount++;
			//	SampleHandler.printMessage("The following methods have the " + methodType + " pattern.");
			//	SampleHandler.printMessage(declaredMethod.getName().toString());
			//SampleHandler.printMessage(declaredMethod.toString());
			}

		}
		for (final MethodDeclaration declaredMethod : suspectMethodA.keySet()) {
			final String methodType = suspectMethodA.get(declaredMethod);
			if (methodType != null && methodType.length() > 0) {
			//	SampleHandler.printMessage("The following methods have the " + methodType + " pattern.");
			//	SampleHandler.printMessage(declaredMethod.getName().toString());
				abortCount++;
			//SampleHandler.printMessage(declaredMethod.toString());
			}

		}

		for (final MethodDeclaration declaredMethod : suspectMethodC.keySet()) {
			final String methodType = suspectMethodC.get(declaredMethod);
			if (methodType != null && methodType.length() > 0) {
			//	SampleHandler.printMessage("The following methods have the " + methodType + " pattern.");
			//	SampleHandler.printMessage(declaredMethod.getName().toString());
			//SampleHandler.printMessage(declaredMethod.toString());
				 catchAndDoNothCount++ ;
			}

		}

		for (final MethodDeclaration declaredMethod : suspectMethodM.keySet()) {
			final String methodType = suspectMethodM.get(declaredMethod);
			if (methodType != null && methodType.length() > 0) {
			//	SampleHandler.printMessage("The following methods have the " + methodType + " pattern.");
			//	SampleHandler.printMessage(declaredMethod.getName().toString());
			//SampleHandler.printMessage(declaredMethod.toString());
				
				 
			}

		}

		for (final MethodDeclaration declaredMethod : suspectMethodD.keySet()) {
			final String methodType = suspectMethodD.get(declaredMethod);
			if (methodType != null && methodType.length() > 0) {
			//	SampleHandler.printMessage("The following methods have the " + methodType + " pattern.");
			//	SampleHandler.printMessage(declaredMethod.getName().toString());
			//SampleHandler.printMessage(declaredMethod.toString());
				  
				 
			}

		}

		for (final MethodDeclaration declaredMethod : suspectMethodR.keySet()) {
			final String methodType = suspectMethodR.get(declaredMethod);
			if (methodType != null && methodType.length() > 0) {
			//	SampleHandler.printMessage("The following methods have the " + methodType + " pattern.");
			//	SampleHandler.printMessage(declaredMethod.getName().toString());
			//SampleHandler.printMessage(declaredMethod.toString());
				  logAndReturnNullCount++;
				 
			}

		}

		for (final MethodDeclaration declaredMethod : suspectMethodCG.keySet()) {
			final String methodType = suspectMethodCG.get(declaredMethod);
			if (methodType != null && methodType.length() > 0) {
			//	SampleHandler.printMessage("The following methods have the " + methodType + " pattern.");
			//	SampleHandler.printMessage(declaredMethod.getName().toString());
			//SampleHandler.printMessage(declaredMethod.toString());
				  catchGenericCount++;
			}

		}

		for (final MethodDeclaration declaredMethod : suspectMethodN.keySet()) {
			final String methodType = suspectMethodN.get(declaredMethod);
			if (methodType != null && methodType.length() > 0) {
			//	SampleHandler.printMessage("The following methods have the " + methodType + " pattern.");
			//	SampleHandler.printMessage(declaredMethod.getName().toString());
			//SampleHandler.printMessage(declaredMethod.toString());
				  nestedTryCount++;
				
			}

		}
		for (final MethodDeclaration declaredMethod : suspectMethodRG.keySet()) {
			final String methodType = suspectMethodRG.get(declaredMethod);
			if (methodType != null && methodType.length() > 0) {
			//	SampleHandler.printMessage("The following methods have the " + methodType + " pattern.");
			//	SampleHandler.printMessage(declaredMethod.getName().toString());
			//SampleHandler.printMessage(declaredMethod.toString());
				  replyOnGetCauseCount++; 
				
			}

		}
		
		for (final MethodDeclaration declaredMethod : suspectMethodI.keySet()) {
			final String methodType = suspectMethodI.get(declaredMethod);
			if (methodType != null && methodType.length() > 0) {
			//	SampleHandler.printMessage("The following methods have the " + methodType + " pattern.");
			//	SampleHandler.printMessage(declaredMethod.getName().toString());
			//SampleHandler.printMessage(declaredMethod.toString());
				  
			}

		}
		
		for (final MethodDeclaration declaredMethod : suspectMethodDW.keySet()) {
			final String methodType = suspectMethodDW.get(declaredMethod);
			if (methodType != null && methodType.length() > 0) {
			//	SampleHandler.printMessage("The following methods have the " + methodType + " pattern.");
			//	SampleHandler.printMessage(declaredMethod.getName().toString());
			//SampleHandler.printMessage(declaredMethod.toString());
				destructiveWrappingCount++; 
				
			}

		}
		
		
		for (final MethodDeclaration declaredMethod : suspectMethodIE.keySet()) {
			final String methodType = suspectMethodIE.get(declaredMethod);
			if (methodType != null && methodType.length() > 0) {
			//	SampleHandler.printMessage("The following methods have the " + methodType + " pattern.");
			//	SampleHandler.printMessage(declaredMethod.getName().toString());
				interExcCount++;
			//SampleHandler.printMessage(declaredMethod.toString());
			}

		}

	}

	public int getTryQuantity() {
		return tryQuantity;
	}

	public int getReturnNullCount() {
		return returnNullCount;
	}

	public int getOverCatchCount() {
		return overCatchCount;
	}

	public int getCatchAndDoNothCount() {
		return catchAndDoNothCount;
	}

	public int getCatchGenericCount() {
		return catchGenericCount;
	}

	public int getDummyHandlerCount() {
		return dummyHandlerCount;
	}

	public int getLogAndReturnNullCount() {
		return logAndReturnNullCount;
	}

	public int getMultiLineLogCount() {
		return multiLineLogCount;
	}

	public int getNestedTryCount() {
		return nestedTryCount;
	}

	public int getReplyOnGetCauseCount() {
		return replyOnGetCauseCount;
	}

	public int getIncompleteImplCount() {
		return incompleteImplCount;
	}

	public int getDestructiveWrappingCount() {
		return destructiveWrappingCount;
	}

	public int getInterExcCount() {
		return interExcCount;
	}

	public int getAbortCount() {
		return abortCount;
	}

	public int getCatchSloc() {
		return catchSloc;
	}

	public int getTrySloc() {
		return trySloc;
	}

	public int getCatchLoc() {
		return catchLoc;
	}

	public int getTryLoc() {
		return tryLoc;
	}
	
	public int getCatchQuantity() { 
		  return catchQuantity; 
		  }
	 
	public int getCatchSLOC() {
		  return catchSloc; 
		  }

	public int getTryInvokeCount() {
		return tryInvokeCount;
	}

	public int getTryscopeCount() {
		return tryscopeCount;
	}

	public int getLevel() {
		return level;
	}

	public HashMap<String, Double> getFlowActionPercentages() {
		return flowActionPercentages;
	}

	public HashMap<String, Integer> getFlowActionNumber() {
		return flowActionNumber;
	}

	public String getRoot() {
		return root;
	}

	public void setRoot(String root) {
	
		String result[]= root.split("/");
		int l =result.length;
		String answer ="";
		for(int i =0; i<l; i++) {
			if(i!=l-1) {
				answer =answer+"/" +result[i];
			}
			
		}
		this.root = answer;
	}

	public void setFlowActionNumber(HashMap<String, Integer> flowActionNumber) {
		this.flowActionNumber = flowActionNumber;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public void printReport() {
		SampleHandler.printMessage(" ,"+getCatchQuantity());
		SampleHandler.printMessage(" ,"+getTryQuantity());
		SampleHandler.printMessage(" ,"+ getCatchSLOC());
		SampleHandler.printMessage(" ,"+ getCatchLoc());
		SampleHandler.printMessage(" ,"+ getTrySloc());
		SampleHandler.printMessage(" ,"+ getTryLoc());
		SampleHandler.printMessage(" ,"+ getTryInvokeCount());
		SampleHandler.printMessage(" ,"+ getTryscopeCount());
		SampleHandler.printMessage(" ,"+ getOverCatchCount());
		SampleHandler.printMessage(" ,"+ getAbortCount());
		SampleHandler.printMessage(" ,"+ getReturnNullCount());
		SampleHandler.printMessage(" ,"+ getCatchAndDoNothCount());
		SampleHandler.printMessage(" ,"+ getCatchGenericCount());
		SampleHandler.printMessage(" ,"+ getDummyHandlerCount());
		SampleHandler.printMessage(" ,"+ getLogAndReturnNullCount());
		SampleHandler.printMessage(" ,"+ getMultiLineLogCount());
		SampleHandler.printMessage(" ,"+ getNestedTryCount());
		SampleHandler.printMessage(" ,"+ getReplyOnGetCauseCount());
		SampleHandler.printMessage(" ,"+ getIncompleteImplCount());
		SampleHandler.printMessage(" ,"+ getDestructiveWrappingCount());
		SampleHandler.printMessage(" ,"+ getInterExcCount());
		//SampleHandler.printMessage(" ,"+getFlowActionPercentages());
		SampleHandler.printMessageLine("");
	}


	
	
	
}
