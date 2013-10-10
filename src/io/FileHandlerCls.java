package io;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class FileHandlerCls extends AbstractFileHandler {
	
	private boolean newcommandFlag;
	
	public FileHandlerCls() {
		super();
		newcommandFlag = false;
	}
	
	@Override
	protected void handleReadProcess(String line, List<String> commands) {
		if (this.newcommandFlag) {
			if (!(line.contains("{") && line.contains("}"))) {
				return;
			}
			int left = line.indexOf('{');
			int right = line.indexOf('}');
			String commandName = line.substring(left + 1, right);
			if (line.contains("[") && line.contains("]")) {
				left = line.indexOf('[');
				right = line.indexOf(']');
				int numberOfParam = 0;
				String paramNumber = line.substring(left +1 , right);
				if (paramNumber.matches("[0-9]*")) {
					try {
						numberOfParam = Integer.parseInt(paramNumber);
					} catch (final NumberFormatException nfe) {
						nfe.printStackTrace();
						return;
					}
				}
				for (int i = 0; i < numberOfParam; i++) {
					commandName = commandName + "{@}";
				}
			}
			commands.add(commandName);
			newcommandFlag = false;
		} else {
			if (!line.trim().startsWith("%") && line.contains("newcommand")) {
				newcommandFlag = true;
			}
		}
	}

	@Override
	protected void handleWriteProcess(List<String> commands, BufferedWriter out)
			throws IOException {
		return;
	}

	@Override
	protected StringBuffer createFileContentManually(File file,
			List<String> commands) throws IOException {
		return null;
	}

}
