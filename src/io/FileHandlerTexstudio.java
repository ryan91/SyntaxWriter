package io;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class FileHandlerTexstudio extends AbstractFileHandler {

	@Override
	protected void handleReadProcess(String line, List<String> commands) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void handleWriteProcess(List<String> commands, BufferedWriter out) {
		// TODO Auto-generated method stub

	}

	@Override
	protected StringBuffer createFileContentManually(File file,
			List<String> commands) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

}
