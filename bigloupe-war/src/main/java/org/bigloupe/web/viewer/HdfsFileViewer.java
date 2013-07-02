package org.bigloupe.web.viewer;

import java.io.IOException;
import java.io.OutputStream;

import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

public interface HdfsFileViewer {

    public boolean canReadFile(FileSystem fs, Path path);

    /**
     * Prepare view 
     * 
     * @param fs
     * @param path
     * @param outStream
     * @param startLine
     * @param endLine
     * @return view to display
     * @throws IOException
     */
    public String displayFile(FileSystem fs,
                            Path path,
                            OutputStream outStream,
                            int startLine,
                            int endLine) throws IOException;

    public void downloadFile(FileSystem fs,
            Path path,
            OutputStream outStream,
            int downloadSize) throws IOException;
}