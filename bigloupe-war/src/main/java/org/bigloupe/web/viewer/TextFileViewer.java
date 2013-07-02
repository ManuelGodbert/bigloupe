package org.bigloupe.web.viewer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.HashSet;

import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.log4j.Logger;
import org.eclipse.jetty.util.IO;

public class TextFileViewer implements HdfsFileViewer {

    private static Logger logger = Logger.getLogger(TextFileViewer.class);
    private HashSet<String> acceptedSuffix = new HashSet<String>();

    public TextFileViewer() {
        acceptedSuffix.add(".txt");
        acceptedSuffix.add(".csv");
        acceptedSuffix.add(".props");
        acceptedSuffix.add(".xml");
        acceptedSuffix.add(".html");
        acceptedSuffix.add(".json");
        acceptedSuffix.add(".log");
    }

    public boolean canReadFile(FileSystem fs, Path path) {
        return true;
    }

    public String displayFile(FileSystem fs,
                            Path path,
                            OutputStream outputStream,
                            int startLine,
                            int endLine) throws IOException {

        if(logger.isDebugEnabled())
            logger.debug("read in uncompressed text file");
        InputStream inputStream = fs.open(path);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        PrintWriter output = new PrintWriter(outputStream);
        for(int i = 1; i < startLine; i++)
            reader.readLine();
        
        final int bufferLimit = 1000000; //only display the first 1M chars. it is used to prevent showing/downloading gb of data
        int bufferSize = 0;
        for(int i = startLine; i < endLine; i++) {
            String line = reader.readLine();
            if(line == null)
                break;
            
            // bread if reach the buffer limit
            bufferSize += line.length();
            if (bufferSize >= bufferLimit)
                break;
            
            output.write(line);
            output.write("\n");
        }
        output.flush();
        reader.close();
        return "defaultviewer";
    }

	@Override
	public void downloadFile(FileSystem fs, Path path, OutputStream outStream,
			int downloadSize) throws IOException {
		displayFile(fs, path, outStream, 0, 1000);
		
	}
}