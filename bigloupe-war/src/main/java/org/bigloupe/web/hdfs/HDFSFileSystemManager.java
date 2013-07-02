package org.bigloupe.web.hdfs;

import java.util.HashMap;
import java.util.Map;

import org.apache.hadoop.fs.FileSystem;
import org.springframework.stereotype.Component;

/**
 * Manage an array list of FileSystem ({@link FileSystem}
 * Singleton
 * 
 * @author bigloupe
 * 
 */
@Component
public class HDFSFileSystemManager {
	
	private static Map<String, FileSystem> fileSystemMap = new HashMap<String, FileSystem>();
	
	public void addFileSystem(FileSystem fileSystem) {
		fileSystemMap.put(fileSystem.getCanonicalServiceName(), fileSystem);
	}
	
	public static FileSystem getFileSystem(String canonicalServiceName) {
		return fileSystemMap.get(canonicalServiceName);
	}
	
}
