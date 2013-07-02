package org.bigloupe.web.filemanager;

import org.apache.log4j.Logger;
import org.bigloupe.web.BigLoupeConfiguration;
import org.bigloupe.web.filemanager.config.AbstractConnectorConfig;
import org.bigloupe.web.filemanager.fs.DiskFsImpl;
import org.bigloupe.web.filemanager.fs.IFsImpl;



/**
 * File manager configuration.
 */
public class FileManagerConfig extends AbstractConnectorConfig {
	private static final Logger S_LOG = Logger.getLogger(FileManagerConfig.class);

	/**
	 * Filesystem.
	 */
	private DiskFsImpl fsImpl;
	
	public FileManagerConfig() {
		fsImpl = new DiskFsImpl();
	}
	
	@Override
	public IFsImpl getFs() {
		return fsImpl;
	}
	
	@Override
	public String getRoot() {
		return ".";
		//return SpringPropertyUtils.getProperty(Constants.STATIC_ROOT_DIRECTORY);
	}

	@Override
	public String getRootUrl() {
		return BigLoupeConfiguration.getFileManagerUrl();
	}

	@Override
	public String rootAliasOrBaseName() {
		return "Shared docs";
	}
}
