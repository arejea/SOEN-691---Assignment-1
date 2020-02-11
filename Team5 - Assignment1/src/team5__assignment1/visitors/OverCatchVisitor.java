package team5__assignment1.visitors;

import org.eclipse.jdt.core.dom.ASTVisitor;

public class OverCatchVisitor extends ASTVisitor {
	
	private int methodCount = 4;


	public boolean visit(OverCatchVisitor node) {
		
		// Perform over catch algorithm 
		
		getOvercatchCount();
		
		return visit(node);
	}
	
	public int getOvercatchCount() {
		System.out.println("This is working");
		return methodCount;
	}
}
