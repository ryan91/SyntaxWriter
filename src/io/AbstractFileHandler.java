package io;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractFileHandler implements IFileHandler {
	
	protected FileReader reader;
	protected BufferedReader in;
	protected FileWriter writer;
	protected BufferedWriter out;
	
	protected abstract void handleReadProcess(final String line, final List<String> commands);
	protected abstract void handleWriteProcess(final List<String> commands, final BufferedWriter out) throws IOException;	
	protected abstract StringBuffer createFileContentManually(final File file, final List<String> commands) throws IOException;
	
	public AbstractFileHandler() {
	}
	
	@Override
	public List<String> readFile(final File file) throws IOException {
		this.reader = new FileReader(file);
		this.in = new BufferedReader(this.reader);
		final List<String> commands = new ArrayList<String>();
		String line;
		while((line = in.readLine()) != null) {
			this.handleReadProcess(line, commands);
		}
		this.in.close();
		this.reader.close();
		return commands;
	}

	@Override
	public void writeFile(final File file, final List<String> commands) throws IOException {
		StringBuffer fileContentBuffer = this.createFileContentManually(file, commands);
		this.writer = new FileWriter(file);
		this.out = new BufferedWriter(this.writer);
		if (fileContentBuffer != null) {
			this.out.write(fileContentBuffer.toString());
		} else {
			this.handleWriteProcess(commands, out);
		}
		this.out.close();
		this.writer.close();
	}

}
