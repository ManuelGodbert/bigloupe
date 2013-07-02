package org.bigloupe.web.viewer;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.SequenceFile;

public abstract class HdfsSequenceFileViewer implements HdfsFileViewer {

	protected abstract boolean canReadFile(SequenceFile.Reader reader);

	protected abstract void displaySequenceFile(SequenceFile.Reader reader,
			PrintWriter output, int startLine, int endLine) throws IOException;

	public boolean canReadFile(FileSystem fs, Path file) {
		boolean result = false;
		try {
			SequenceFile.Reader reader = new SequenceFile.Reader(fs, file,
					new Configuration());
			result = canReadFile(reader);
			reader.close();
		} catch (IOException e) {
			return false;
		}

		return result;
	}

	public String displayFile(FileSystem fs, Path file,
			OutputStream outputStream, int startLine, int endLine)
			throws IOException {
		SequenceFile.Reader reader = null;
		PrintWriter writer = new PrintWriter(outputStream);
		try {
			reader = new SequenceFile.Reader(fs, file, new Configuration());
			displaySequenceFile(reader, writer, startLine, endLine);
		} catch (IOException e) {
			writer.write("Error opening sequence file " + e);
		} finally {
			if (reader != null) {
				reader.close();
			}
		}
		return "defaultviewer";
	}
}