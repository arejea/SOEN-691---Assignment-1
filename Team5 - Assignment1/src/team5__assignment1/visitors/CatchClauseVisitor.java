package team5__assignment1.visitors;

import java.util.HashSet;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.*;

public class CatchClauseVisitor extends ASTVisitor {

	
	private HashSet<CatchClause> nullCatches = new HashSet<>();
	private HashSet<CatchClause> overCatches = new HashSet<>();

	@Override
	public boolean visit(CatchClause node) {
		// TODO Auto-generated method stub
		
		MethodInvocationVisitor methodInvocationVisitor = new MethodInvocationVisitor("LogCatchSwitch");
		node.accept(methodInvocationVisitor);;
		
		
		if(isNullCatch(node)) {
			nullCatches.add(node);
		}
		
		
		return super.visit(node);
	}

	private boolean isNullCatch(CatchClause node) {
		
		for(int i =0; i<node.getBody().statements().size(); i++) {
			if(node.getBody().statements().get(i).toString().replace(" ", "").contains("returnnull")) {
				return true;
			}
			}
		
		return false;
	}

	public HashSet<CatchClause> getNullCatches() {
		return nullCatches;
	}



}
