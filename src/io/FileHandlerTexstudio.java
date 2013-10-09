package io;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class FileHandlerTexstudio extends AbstractFileHandler {
	
	public static final String COMMENT_IDENTIFIER = "#";
	
	@Override
	protected void handleReadProcess(String line, List<String> commands) {
		line = line.trim();
		if (!line.startsWith(COMMENT_IDENTIFIER)) {
			line = this.replaceContentBetweenChars(line, '[', ']', "@");
			line = this.replaceContentBetweenChars(line, '{', '}', "@");
			line = this.replaceContentBetweenChars(line, '(', ')', "@");
			line = line.replaceFirst("\\\\", "");
			commands.add(line);
		}
	}
	
	private String replaceContentBetweenChars(String string, char c1, char c2, String replacement) {
		String examine = new String(string);
		int openBracket = examine.indexOf(c1);
		int closeBracket = examine.indexOf(c2);
		while(openBracket != -1 && closeBracket != -1) {
			if (openBracket < closeBracket) {
				String replace = examine.substring(openBracket + 1, closeBracket);
				if (replace.isEmpty()) {
					string = new StringBuilder(string).insert(closeBracket, replacement).toString();
				} else {
					string = string.replace(replace, replacement);
				}
			}
			examine = examine.substring(closeBracket + 1);
			openBracket = examine.indexOf(c1);
			closeBracket = examine.indexOf(c2);
		}
		return string;
	}
	
	@Override
	protected void handleWriteProcess(List<String> commands, BufferedWriter out) throws IOException {
		for (String s : commands) {
			s = s.replaceAll("@", "arg");
			s = "\\" + s;
			out.write(s);
			out.newLine();
		}
	}

	@Override
	protected StringBuffer createFileContentManually(File file,
			List<String> commands) throws IOException {
		/* not needed */
		return null;
	}

}
