package org.bigloupe.web.viewer;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.SequenceFile.Reader;
import org.apache.hadoop.io.Text;
import org.apache.log4j.Logger;

public class JsonSequenceFileViewer extends HdfsSequenceFileViewer {

    private static Logger logger = Logger.getLogger(JsonSequenceFileViewer.class);

    public boolean canReadFile(Reader reader) {
        Text keySchema = reader.getMetadata().get(new Text("key.schema"));
        Text valueSchema = reader.getMetadata().get(new Text("value.schema"));

        return keySchema != null && valueSchema != null;
    }

    public void displaySequenceFile(SequenceFile.Reader reader,
                                    PrintWriter output,
                                    int startLine,
                                    int endLine) throws IOException {

        if(logger.isDebugEnabled())
            logger.debug("display json file");

        try {
            BytesWritable keyWritable = new BytesWritable();
            BytesWritable valueWritable = new BytesWritable();
            Text keySchema = reader.getMetadata().get(new Text("key.schema"));
            Text valueSchema = reader.getMetadata().get(new Text("value.schema"));

            //JsonTypeSerializer keySerializer = new JsonTypeSerializer(keySchema.toString());
            //JsonTypeSerializer valueSerializer = new JsonTypeSerializer(valueSchema.toString());

            // skip lines before the start line
            for(int i = 1; i < startLine; i++)
                reader.next(keyWritable, valueWritable);

            // now actually output lines
            for(int i = startLine; i <= endLine; i++) {
                boolean readSomething = reader.next(keyWritable, valueWritable);
                if(!readSomething)
                    break;
                //output.write(safeToString(keySerializer.toObject(keyWritable.getBytes())));
                output.write("\t=>\t");
                //output.write(safeToString(valueSerializer.toObject(valueWritable.getBytes())));
                output.write("\n");
                output.flush();
            }
        } finally {
            reader.close();
        }
    }

    private String safeToString(Object value) {
        if(value == null)
            return "null";
        else
            return value.toString();
    }

	@Override
	public void downloadFile(FileSystem fs, Path path, OutputStream outStream,
			int downloadSize) throws IOException {
		// TODO To be implemented		
	}

}