package team5__assignment1.visitors;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.*;

public class MethodInvocationVisitor extends ASTVisitor {


	private static String [] nullMethods = {"null", "returnnull"};
	
	private int logPrintDefaultStatements = 0;
	private String LogCatchSwitch;
	
	public MethodInvocationVisitor(String LogCatchSwitch) {
		this.LogCatchSwitch = LogCatchSwitch;
	}
	
	
	@Override
	public boolean visit(MethodInvocation node) {
		
		if(this.LogCatchSwitch == "LogCatchSwitch"){ 
			String nodeName = node.getName().toString();
			 if (IsNullStatement(nodeName)) {
				logPrintDefaultStatements += 1;
			}
		}	
			
		return super.visit(node);
	}
	
    
	private static boolean IsNullStatement(String statement) {
		
		if(statement ==null) {
			return false;
		}
		
		for(String nullMethod: nullMethods) {
			System.out.println("null method clause sees "+ nullMethod);
			if(statement.replace(" ", "").contains(nullMethod)) {
				System.out.println("progress found");
				return true;
			}
			
		}
		
		return false;
	}
	
	public int getLogPrintDefaultStatements() {
		
		return logPrintDefaultStatements;
	}




}
