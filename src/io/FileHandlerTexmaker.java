package io;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

public class FileHandlerTexmaker extends AbstractFileHandler {
	
	private static final String IDENTIFIER = "Editor\\UserCompletion";

	@Override
	protected void handleReadProcess(String line, List<String> commands) {
		if (line.contains(IDENTIFIER)) {
			String allCommands = line.substring(line.indexOf('=') + 1, line.length());
			allCommands = allCommands.replaceAll("\\\\x2022", "@")
					.replaceAll("\\\\", "").replaceAll("\"", "");
			String[] split = allCommands.split(", ");
			for (String s : split) {
				commands.add(s);
			}
		}
	}

	@Override
	protected void handleWriteProcess(List<String> commands, BufferedWriter out) {
		/* do nothing */
	}

	@Override
	protected StringBuffer createFileContentManually(final File file, final List<String> commands) throws IOException {
		FileReader fileReader = new FileReader(file);
		BufferedReader bufferedReader = new BufferedReader(fileReader);
		String line;
		StringBuffer contentBuffer = new StringBuffer();
		while((line = bufferedReader.readLine()) != null) {
			if (line.contains(IDENTIFIER)) {
				String newLine = line.substring(0, line.indexOf('=') + 1);
				for (String s : commands) {
					s = s.replaceAll("@", "\\\\x2022");
					newLine += "\"\\\\" + s + "\", ";
				}
				newLine = newLine.substring(0, newLine.length() - 2);
				contentBuffer.append(newLine + "\n");
			} else {
				contentBuffer.append(line + "\n");
			}
		}
		bufferedReader.close();
		fileReader.close();
		return contentBuffer;
	}

}
