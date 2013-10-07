package io;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TexmakerIo {
	
	private static final String IDENTIFIER = "Editor\\UserCompletion";
	
	private TexmakerIo() {
		/* do nothing */
	}
	
	public static List<String> getExistingCommands(File file) throws IOException {
		FileReader fileReader = new FileReader(file);
		BufferedReader bufferedReader = new BufferedReader(fileReader);
		List<String> existingCommands = new ArrayList<String>();
		String line;
		while((line = bufferedReader.readLine()) != null) {
			if (line.contains(IDENTIFIER)) {
				String allCommands = line.substring(line.indexOf('=') + 1, line.length());
				allCommands = allCommands.replaceAll("\\\\x2022", "@")
						.replaceAll("\\\\", "").replaceAll("\"", "");
				String[] split = allCommands.split(", ");
				for (String s : split) {
					existingCommands.add(s);
				}
				bufferedReader.close();
				fileReader.close();
				return existingCommands;
			}
		}
		System.err.println("Identifier not found - file might be corrupted");
		bufferedReader.close();
		fileReader.close();
		return null;
	}
	
	public static void writeToFile(final File file, final List<String> commands) throws IOException {
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
		
		FileWriter fileWriter = new FileWriter(file);
		BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
		bufferedWriter.write(contentBuffer.toString());
		bufferedWriter.close();
		fileWriter.close();
	}
}
