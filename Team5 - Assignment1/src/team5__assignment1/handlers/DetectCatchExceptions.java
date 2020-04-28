package team5__assignment1.handlers;

import java.io.IOException;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;

import team5__assignment1.Patterns.ExceptionFinderTool;
import team5__assignment1.visitors.MethodDeclarationVisitor;
import team5__assignment1.visitors.OverCatchVisitor;

public class DetectCatchExceptions extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IWorkspaceRoot root = workspace.getRoot();
		IProject[] projects = root.getProjects();
		if(projects!=null && projects.length>=1) {
			//printHeader();
			printFlowHeader();
			
			try {
				detectInProjects(projects);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		}
		SampleHandler.printMessage("Exception Anti Patter Analysis Finished");
		return null;
	}
	
	private void printFlowHeader() {

		SampleHandler.printMessage("catchRecover,");
				SampleHandler.printMessage("specificPercent,");
						SampleHandler.printMessage("subsumptionPercent,");
								SampleHandler.printMessage("AbortPercent,");
										SampleHandler.printMessage("ContinuePercent,");
		 
												SampleHandler.printMessage("ReturnPercent,");
														SampleHandler.printMessage("emptyActionCount,");
		    SampleHandler.printMessage("NestedTryPercent,");
		    		SampleHandler.printMessage("ThrowNewPercent,");
		    				SampleHandler.printMessage("toDoCount,");
		    						SampleHandler.printMessage("DefaultPercent,");
		    								SampleHandler.printMessage("logActionCount,");
		    										SampleHandler.printMessage("MethodCountPercent,");
		    												SampleHandler.printMessage("ThrowCurrentPercent,");
		    														SampleHandler.printMessage("ThrowWrapPercent,");
			
		
		
	}

	// provide java source code to parse
	private void detectInProjects(IProject[] projects) throws IOException{
		
		if(projects!=null && projects.length>0) {
			try {
			for (IProject project : projects) {
				System.out.println("Printtttt  " + project.getLocation());

				ExceptionFinderTool exceptionFinder = null;
				if(project!=null && project.exists()&&project.isAccessible()&&project.isOpen()&&project.isNatureEnabled(JavaCore.NATURE_ID)) {
					

					System.out.println("DETECTING IN: " + project.getName());
				//	SampleHandler.printMessage("DETECTING IN: " + project.getName());
					
					exceptionFinder = new ExceptionFinderTool();
					exceptionFinder.setRoot(project.getLocation().toString());	;
					exceptionFinder.findExceptions(project);
							
				
				} 
				



			}
			
			
			}
				catch (JavaModelException e1) {
						e1.printStackTrace();
					}
					catch (CoreException e1) {
						e1.printStackTrace();
					}
				
				
				
			
		
		}
		
	}

	
	private static void printMethodInfo(MethodDeclarationVisitor methodDeclarationVisitor) {
		SampleHandler.printMessage(String.format("The number of method in the project is %s ", methodDeclarationVisitor.getMethodCount()));		
		
	}

	// Print Result  for Catch Clause 
	
      private static void printMethodInfo(OverCatchVisitor  checksOverCatch) {
		
//		SampleHandler.printMessage(String.format("The number of method in the project is %s ", methodDeclarationVisitor.getMethodCount()));
		System.out.println(String.format("The number of method in the project is %s ", checksOverCatch.getOvercatchCount()));
		} 
	

      public void printHeader() {
  		SampleHandler.printMessage("Java File: ");
  		SampleHandler.printMessage(",Catch quantity #: ");
  		SampleHandler.printMessage(",Try quantity #: ");
  		SampleHandler.printMessage(",Catch SLOC #: ");
  		SampleHandler.printMessage(",Catch LOC #: ");
  		SampleHandler.printMessage(",Try SLOC #: ");
  		SampleHandler.printMessage(",Try LOC #: ");
  		SampleHandler.printMessage(",Try Invoked Method Count #: ");
  		SampleHandler.printMessage(",Try Scope Count #: ");
  		SampleHandler.printMessage(",OverCatchAntiPatternCount #: ");
  		SampleHandler.printMessage(",AbortAndOvercatchCount #: ");
  		SampleHandler.printMessage(",CatchAndReturnNullCount #: ");
  		SampleHandler.printMessage(",CatchAndDoNothingCount #: ");
  		SampleHandler.printMessage(",CatchGenericCount #: ");
  		SampleHandler.printMessage(",DummyHandlerCount #: ");
  		SampleHandler.printMessage(",LogAndReturnNullCount #: ");
  		SampleHandler.printMessage(",MultiLineLogCount #: ");
  		SampleHandler.printMessage(",NestedTryCount #: ");
  		SampleHandler.printMessage(",ReplOneGetCauseCount #: ");
  		SampleHandler.printMessage(",IncompleteImplementationCount #: ");
  		SampleHandler.printMessage(",DestructiveWrappingCount #: ");
  		SampleHandler.printMessage(",InterruptedExceptionCount #: ");
  		//SampleHandler.printMessage(",Flow Handling Actions #: ");
  		SampleHandler.printMessageLine("");
  	
  	}
      }
