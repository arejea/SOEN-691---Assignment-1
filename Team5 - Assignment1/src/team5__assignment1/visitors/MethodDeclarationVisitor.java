package team5__assignment1.visitors;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.MethodDeclaration;

public class MethodDeclarationVisitor extends ASTVisitor {
	

	
	private int methodCount = 0;

    
    public MethodDeclarationVisitor() {
    }
    

	@Override
	public boolean visit(MethodDeclaration node) {
		// TODO Auto-generated method stub
		
		methodCount += 1;

		return super.visit(node);
	}

	public int getMethodCount() {
		return methodCount;
	}

	


}
