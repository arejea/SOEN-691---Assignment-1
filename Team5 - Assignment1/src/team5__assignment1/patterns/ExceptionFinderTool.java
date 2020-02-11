package team5__assignment1.patterns;

import java.util.HashMap;

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
import org.eclipse.jdt.core.dom.MethodDeclaration;

import team5__assignment1.handlers.SampleHandler;
import team5__assignment1.visitors.CatchClauseVisitor;

public class ExceptionFinderTool extends ASTVisitor {HashMap <MethodDeclaration, String> suspectMethods = new HashMap<>();


public void findExceptions(IProject project) throws JavaModelException{
	
	IPackageFragment[] allPackages = JavaCore.create(project).getPackageFragments();
	
	for(IPackageFragment myPackage: allPackages) {
		findCatchClauseException(myPackage);
	}
	
}


private void findCatchClauseException(IPackageFragment myPackageFragment) throws JavaModelException {
	for(ICompilationUnit unit : myPackageFragment.getCompilationUnits() ) {
		CompilationUnit parsedCompilationUnit = unitParser(unit);
		
		CatchClauseVisitor catchExceptionVisitor = new CatchClauseVisitor();
		parsedCompilationUnit.accept(catchExceptionVisitor);
		
		getMethodsWithTargetCatchClauses(catchExceptionVisitor);
		
	}
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

public void printExceptions() {
	for(MethodDeclaration declaredMethod: suspectMethods.keySet()) {
		String methodType = suspectMethods.get(declaredMethod);
		SampleHandler.printMessage("The following methods have the "+ methodType+ " pattern.");
		SampleHandler.printMessage(declaredMethod.toString());
	}
}}
