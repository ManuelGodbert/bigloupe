package org.bigloupe.web.viewer;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import org.apache.avro.Schema;
import org.apache.avro.Schema.Field;
import org.apache.avro.file.DataFileConstants;
import org.apache.avro.file.DataFileReader;
import org.apache.avro.file.DataFileStream;
import org.apache.avro.file.DataFileWriter;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.Encoder;
import org.apache.avro.io.EncoderFactory;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.log4j.Logger;
import org.bigloupe.web.dto.PairValue;
import org.codehaus.jackson.JsonEncoding;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.util.MinimalPrettyPrinter;

import com.google.common.collect.Lists;

/**
 * This class implements a viewer of avro files
 * 
 * @author lguo
 * 
 */
public class HdfsAvroFileViewer implements HdfsFileViewer {

	private static Logger logger = Logger.getLogger(HdfsAvroFileViewer.class);

	@Override
	public boolean canReadFile(FileSystem fs, Path path) {

		if (logger.isDebugEnabled())
			logger.debug("path:" + path.toUri().getPath());

		try {
			DataFileStream<Object> avroDataStream = getAvroDataStream(fs, path);
			Schema schema = avroDataStream.getSchema();
			avroDataStream.close();
			return schema != null;
		} catch (IOException e) {
			if (logger.isDebugEnabled()) {
				logger.debug(path.toUri().getPath() + " is not an avro file.");
				logger.debug("Error in getting avro schema: "
						+ e.getLocalizedMessage());
			}
			return false;
		}
	}

	private DataFileStream<Object> getAvroDataStream(FileSystem fs, Path path)
			throws IOException {
		if (logger.isDebugEnabled())
			logger.debug("path:" + path.toUri().getPath());

		GenericDatumReader<Object> avroReader = new GenericDatumReader<Object>();
		InputStream hdfsInputStream = fs.open(path);
		return new DataFileStream<Object>(hdfsInputStream, avroReader);

	}

	@Override
	public String displayFile(FileSystem fs, Path path,
			OutputStream outputStream, int startRecord, int endRecord)
			throws IOException {

		if (logger.isDebugEnabled())
			logger.debug("display avro file:" + path.toUri().getPath());

		DataFileStream<Object> avroDatastream = null;

		try {
			avroDatastream = getAvroDataStream(fs, path);
			Schema schema = avroDatastream.getSchema();
			DatumWriter<Object> avroWriter = new GenericDatumWriter<Object>(
					schema);

			JsonGenerator g = new JsonFactory().createJsonGenerator(
					outputStream, JsonEncoding.UTF8);
			// g.useDefaultPrettyPrinter();
			MinimalPrettyPrinter prettyPrinter = new MinimalPrettyPrinter();
			prettyPrinter.setRootValueSeparator(",");
			g.setPrettyPrinter(prettyPrinter);
			Encoder encoder = EncoderFactory.get().jsonEncoder(schema, g);

			int lineno = 1; // line number starts from 1
			if (startRecord >= endRecord)
				endRecord = startRecord + 100;
			
			outputStream.write("[".getBytes("UTF-8"));
			while (avroDatastream.hasNext() && lineno <= endRecord) {
				Object datum = avroDatastream.next();
				if (lineno == startRecord) {
					avroWriter.write(datum, encoder);
					encoder.flush();
				}
				lineno++;
			}
			outputStream.write("]".getBytes("UTF-8"));
			g.close();
		} catch (IOException e) {
			outputStream.write(("Error in display avro file: " + e
					.getLocalizedMessage()).getBytes("UTF-8"));
			throw e;
		} finally {
			avroDatastream.close();
		}
		// If startRecord > 1 then Ajax navigation with JSON : return partial
		// page
		if (startRecord != 1) {
			return "forward:/WEB-INF/jsp/hdfs-browser/hdfs-avro-viewer.jsp";
		}
		return "avroviewer";
	}

	/**
	 * Display schema from Avro file
	 * 
	 * @param fs
	 * @param path
	 * @param outputStream
	 * @throws IOException
	 */
	public void displaySchema(FileSystem fs, Path path,
			OutputStream outputStream) throws IOException {

		if (logger.isDebugEnabled())
			logger.debug("display avro file:" + path.toUri().getPath());

		DataFileStream<Object> avroDatastream = null;

		try {
			avroDatastream = getAvroDataStream(fs, path);
			Schema schema = avroDatastream.getSchema();

			outputStream.write(schema.toString(true).getBytes("UTF-8"));
		} catch (IOException e) {
			outputStream.write(("Error in display avro schema : " + e
					.getLocalizedMessage()).getBytes("UTF-8"));
			throw e;
		} finally {
			avroDatastream.close();
		}
	}

	/**
	 * Get list of fields from Avro file
	 * 
	 * @param fs
	 * @param path
	 * @param outputStream
	 * @throws IOException
	 */
	public List<PairValue> displayFields(FileSystem fs, Path path) throws IOException {

		if (logger.isDebugEnabled())
			logger.debug("list fields in avro file:" + path.toUri().getPath());

		DataFileStream<Object> avroDatastream = null;

		try {
			avroDatastream = getAvroDataStream(fs, path);
			Schema schema = avroDatastream.getSchema();
			List<PairValue> fields = Lists.newArrayList();
			for (Field field : schema.getFields()) {
				String fieldType = "string";
				for (Schema schemaTypes : field.schema().getTypes()) {
					fieldType = schemaTypes.getType().getName();
					break;
	            }

				fields.add(new PairValue(fieldType, field.name()));
			}
			return fields;

		} finally {
			avroDatastream.close();
		}
	}
	
	@Override
	public void downloadFile(FileSystem fs, Path path,
			OutputStream outputStream, int downloadSize) throws IOException {
		if (logger.isDebugEnabled())
			logger.debug("download avro file:" + path.toUri().getPath());

		DataFileStream<Object> avroDatastream = null;
		DataFileWriter<GenericRecord> dataFileWriter = null;

		try {
			avroDatastream = getAvroDataStream(fs, path);
			Schema schema = avroDatastream.getSchema();
			GenericDatumWriter<GenericRecord> genericDatumWriter = new GenericDatumWriter<GenericRecord>(
					schema);
			dataFileWriter = new DataFileWriter<GenericRecord>(
					genericDatumWriter);
			dataFileWriter.create(schema, outputStream);

			int noRecord = 1;
			FileStatus fileStatus = fs.getFileStatus(path);
			// TODO : divide by record length
			// genericDatumWriter.getData().
			long endRecord = (fileStatus.getLen() * downloadSize) / 100;
			while (avroDatastream.hasNext() && noRecord <= endRecord) {
				GenericRecord datum = (GenericRecord) avroDatastream.next();
				dataFileWriter.append(datum);
				noRecord++;
			}
		} catch (IOException e) {
			outputStream.write(("Error in display avro file: " + e
					.getLocalizedMessage()).getBytes("UTF-8"));
			throw e;
		} finally {
			avroDatastream.close();
			dataFileWriter.close();
		}

	}

	/**
	 * Compare 2 schemas in JSON
	 * 
	 * @param fileSystem
	 * @param path1
	 * @param path2
	 * @param outputStream
	 * @throws IOException
	 */
	public void displaySchemaDiff(FileSystem fileSystem, Path path1,
			Path path2, OutputStream outputStream) throws IOException {
		
		DataFileStream<Object> avroDatastream1 = null;
		DataFileStream<Object> avroDatastream2 = null;

		try {
			avroDatastream1 = getAvroDataStream(fileSystem, path1);
			avroDatastream2 = getAvroDataStream(fileSystem, path2);
			Schema schema1 = avroDatastream1.getSchema();
			Schema schema2 = avroDatastream2.getSchema();
			JsonFileDiff jsonFileDiff = new JsonFileDiff();
			jsonFileDiff.diff(schema1.toString(), schema2.toString(), outputStream);

			
		} catch (IOException e) {
			outputStream.write(("Error during avro schema comparaison : " + e
					.getLocalizedMessage()).getBytes("UTF-8"));
			throw e;
		} finally {
			if (avroDatastream1 != null)
				avroDatastream1.close();
		}
		
	}
	
	/**
	 * Display CODEC
	 */
	public String displayCodec(FileSystem fileSystem, Path path) throws IOException {
		DataFileStream<Object> avroDatastream = getAvroDataStream(fileSystem, path);
		String codec = avroDatastream.getMetaString(DataFileConstants.CODEC);
		avroDatastream.close();
		return codec;
 	}

}
