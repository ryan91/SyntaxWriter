package io;

import java.io.File;
import java.io.IOException;
import java.util.List;

public interface IFileHandler {
	void writeFile(final File file, final List<String> commands) throws IOException;
	List<String> readFile(File file) throws IOException;
}