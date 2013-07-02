package org.bigloupe.web.util.test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.junit.Assert;

public class TestSerialization {

	public void testIsSerializable(Object obj) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(out);
		oos.writeObject(obj);
		oos.close();
		Assert.assertTrue(out.toByteArray().length > 0);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) throws IOException {
		TestSerialization test = new TestSerialization();
		FileSystem fs = FileSystem.get(new Configuration());
		test.testIsSerializable(new ByteArrayOutputStream());

	}

}
