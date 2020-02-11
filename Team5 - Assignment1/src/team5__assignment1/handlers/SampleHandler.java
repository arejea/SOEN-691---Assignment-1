package team5__assignment1.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;

import team5__assignment1.patterns.ExceptionFinderTool;

public class SampleHandler extends AbstractHandler {
	
		private static final String consoleName = "Team 5 Exception Anti Pattern Analysis Assignment Started......";
		private static MessageConsole myConsole;
		private static MessageConsoleStream output;
	
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		// TODO Auto-generated method stub
		SampleHandler.myConsole = findConsole(consoleName);
		SampleHandler.output = myConsole.newMessageStream();
		
		ExceptionFinderTool detectException = new ExceptionFinderTool();
		detectException.execute(event);
		return null;
	}


	private MessageConsole findConsole(String name) {
		// TODO Auto-generated method stub
		ConsolePlugin plugin = ConsolePlugin.getDefault();
		IConsoleManager conMgr = plugin.getConsoleManager();
		IConsole[] existingCon = conMgr.getConsoles();
		
		for(int i =0; i<existingCon.length; i++) {
			if(name.equals(existingCon[i].getName())) {
				return (MessageConsole) existingCon[i];			
			}
		}
		
		MessageConsole myNewConsole = new MessageConsole(name, null);
		conMgr.addConsoles(new IConsole[] {myNewConsole});
		
		return myNewConsole;
	}

	static public void printMessage(String message) {
		output.println(message);
	}
}
