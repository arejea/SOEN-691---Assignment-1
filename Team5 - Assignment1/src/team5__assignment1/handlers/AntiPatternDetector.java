package team5__assignment1.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.*;

import team5__assignment1.patterns.ExceptionFinderTool;
import team5__assignment1.visitors.MethodDeclarationVisitor;

public class AntiPatternDetector extends AbstractHandler {
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {		
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IWorkspaceRoot root = workspace.getRoot();
		IProject[] allProjects = root.getProjects();
		detectInProjects(allProjects);
				
		SampleHandler.printMessage("Exception Anti Patter Analysis Finished");
		
		return null;
	}

	private void detectInProjects(IProject[] allProjects)  {
		for(IProject project : allProjects) {
			SampleHandler.printMessage("Analyzing Project: "+ project.getName());
			IPackageFragment[] packages;
			try {
				 packages = JavaCore.create(project).getPackageFragments();
				for (IPackageFragment mypackage : packages) {
				findAllMethods(mypackage);
			} 
		}catch (JavaModelException e) {
			e.printStackTrace();
		}
			ExceptionFinderTool exceptionFinder = new ExceptionFinderTool();
			try {
				exceptionFinder.findExceptions(project);
				exceptionFinder.printExceptions();
			} catch (JavaModelException e1) {
				e1.printStackTrace();
			}				
		}
		
	}

	private void findAllMethods(IPackageFragment packageFragment) throws JavaModelException {

		for(ICompilationUnit unit: packageFragment.getCompilationUnits() ) {
			CompilationUnit parsedCompilationUnit = parse(unit);
			
			MethodDeclarationVisitor methodDeclarationVisitor = new MethodDeclarationVisitor(); 
			parsedCompilationUnit.accept(methodDeclarationVisitor);
			printMethodInfo(methodDeclarationVisitor);

		}
	}

	private void printMethodInfo(MethodDeclarationVisitor methodDeclarationVisitor) {
		SampleHandler.printMessage(String.format("The number of method in the project is %s ", methodDeclarationVisitor.getMethodCount()));		
	}

	@SuppressWarnings("deprecation")
	private CompilationUnit parse(ICompilationUnit unit) {
		ASTParser parser = ASTParser.newParser(AST.JLS8);
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		parser.setSource(unit);
		parser.setResolveBindings(true);
		parser.setBindingsRecovery(true);
		parser.setStatementsRecovery(true);
		return (CompilationUnit) parser.createAST(null); // parse
	
	}
}
