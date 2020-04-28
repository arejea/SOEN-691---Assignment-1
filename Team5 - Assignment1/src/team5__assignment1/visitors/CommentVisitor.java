package team5__assignment1.visitors;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.BlockComment;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.LineComment;

public class CommentVisitor extends ASTVisitor {

	CompilationUnit cu;
	String source;
	private Map<Integer,Integer> lineComs = new HashMap<>();
	

	private Map<Integer,Integer> blockComs = new HashMap<>();
 
	public CommentVisitor(CompilationUnit cu, String source) {
		super();
		this.cu = cu;
		this.source = source;
	}
 
	public boolean visit(LineComment node) {
		int start = node.getStartPosition();
		int end = start + node.getLength();
		String comment = source.substring(start, end);
		lineComs.put(start, end);
	//	System.out.println(comment);
		return true;
	}
 
	public boolean visit(BlockComment node) {
		int start = node.getStartPosition();
		int end = start + node.getLength();
		String comment = source.substring(start, end);
		blockComs.put(start, end);
	//	System.out.println(comment);
		return true;
	}
	
	public Map<Integer, Integer> getLineComs() {
		return lineComs;
	}

	public Map<Integer, Integer> getBlockComs() {
		return blockComs;
	}

}
