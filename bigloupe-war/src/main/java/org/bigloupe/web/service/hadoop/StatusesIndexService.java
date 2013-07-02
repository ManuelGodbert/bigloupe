package org.bigloupe.web.service.hadoop;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.PathFilter;
import org.apache.hadoop.security.AccessControlException;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.NIOFSDirectory;
import org.apache.lucene.util.Version;
import org.bigloupe.web.BigLoupeConfiguration;
import org.bigloupe.web.hdfs.HDFSFileSystemManager;
import org.bigloupe.web.listener.SessionListener;
import org.bigloupe.web.util.HdfsUtils;
import org.bigloupe.web.util.lucene.AutoCompletionAnalyzer;
import org.joda.time.DateTime;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.context.ServletContextAware;


/**
 * Index HDFS path Each 10 s
 * 
 * @author bigloupe
 * 
 */
@Service
public class StatusesIndexService implements ServletContextAware,
		InitializingBean {
	private ServletContext servletContext;

	private Log log = LogFactory.getLog(StatusesIndexService.class);

	/**
	 * All HDFS filesystem opened by bigloupe users
	 */
	Map<String, FileSystemScanned> fileSystemScannedSet = new ConcurrentHashMap<String, FileSystemScanned>();

	PathFilter noLogPathFilter = new HdfsUtils.RegexExcludePathFilter(
			"^.*_logs$");

	@Autowired
	BigLoupeConfiguration configuration;

	@Override
	public void afterPropertiesSet() throws Exception {
	}

	/**
	 * FileSystem and last scanned datetime
	 * 
	 */
	public class FileSystemScanned {

		FileSystem fileSystem;
		DateTime lastScannedTime;
		Directory index;
		IndexWriter indexWriter;
		

		public FileSystemScanned(DateTime now, FileSystem fs) {

			try {
				index = new NIOFSDirectory(new File(
						BigLoupeConfiguration.getIndexDirectory(), fs
								.getCanonicalServiceName().replace('.', '-')
								.replace(':', '_')));
				AutoCompletionAnalyzer analyzer = new AutoCompletionAnalyzer();

				IndexWriterConfig config = new IndexWriterConfig(
						Version.LUCENE_35, analyzer);
				config.setOpenMode(OpenMode.CREATE);
				indexWriter = new IndexWriter(index, config);
			} catch (Exception e) {
				e.printStackTrace();
			}

			this.fileSystem = fs;
			this.lastScannedTime = now;
		}

		public FileSystem getFileSystem() {
			return fileSystem;
		}

		public void setFileSystem(FileSystem fileSystem) {
			this.fileSystem = fileSystem;
		}

		public DateTime getLastScannedTime() {
			return lastScannedTime;
		}

		public void setLastScannedTime(DateTime lastScannedTime) {
			this.lastScannedTime = lastScannedTime;
		}

		private StatusesIndexService getOuterType() {
			return StatusesIndexService.this;
		}

		public Directory getIndex() {
			return index;
		}

		public IndexWriter getIndexWriter() {
			return indexWriter;
		}

		@Override
		public int hashCode() {
			return fileSystem.getCanonicalServiceName().hashCode();
		}

		/**
		 * For comparaison : compare only fileSystem
		 */
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			FileSystemScanned other = (FileSystemScanned) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (fileSystem == null) {
				if (other.fileSystem != null)
					return false;
			} else if (!fileSystem.getCanonicalServiceName().equals(
					other.fileSystem.getCanonicalServiceName()))
				return false;
			return true;
		}

	}

	public Map<String, FileSystemScanned> getFileSystemScannedSet() {
		return fileSystemScannedSet;
	}

	@Override
	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
	}

	public BigLoupeConfiguration getConfiguration() {
		return configuration;
	}

	public void setConfiguration(BigLoupeConfiguration configuration) {
		this.configuration = configuration;
	}

	/**
	 * Scan all filesystem opened by users every 10 s
	 */
	@Scheduled(fixedDelay = 10000)
	public void scanFileSystem() {

		if (servletContext != null) {
			SessionListener sessionListener = SessionListener
					.getInstance(servletContext);
			Map<HttpSession, String> sessionsMap = sessionListener
					.getSessions();
			Set<HttpSession> sessions = sessionsMap.keySet();

			// Search distinct filesystem opened by users in Http session
			for (HttpSession httpSession : sessions) {
				String fsCanonicalServiceName = (String) httpSession
						.getAttribute(BigLoupeConfiguration.FILESYSTEM);
				
				if ((fsCanonicalServiceName != null)
						&& (!fileSystemScannedSet.containsKey(fsCanonicalServiceName))) {
					FileSystem fs = HDFSFileSystemManager.getFileSystem(fsCanonicalServiceName);
					fileSystemScannedSet.put(fs.getCanonicalServiceName(),
							new FileSystemScanned(DateTime.now(), fs));
				}
			}

			// For each fileSystem, find all path
			for (FileSystemScanned fileSystemScanned : fileSystemScannedSet
					.values()) {
				try {
					// Rescan if old fileSystem in 20 mn only
					if (fileSystemScanned.getLastScannedTime().isBefore(
							DateTime.now().minusMinutes(20))) {

						FileSystem fs = fileSystemScanned.getFileSystem();
						
						// Delete all previous document
						fileSystemScanned.getIndexWriter().deleteAll();
						fileSystemScanned.getIndexWriter().commit();
						
						Path pattern = new Path("/*");
						// Remember to exclude _log directories. Using a regex
						// here
						// saves us from having to check of
						// empty directories that only contain _log directories,
						// simplifying checks below.
						FileStatus[] statuses = fs.globStatus(pattern,
								noLogPathFilter);
						if (statuses == null || statuses.length == 0) {
							// Warn if we can't find directories for that day.
							log.warn("No directories found : " + pattern);
						} else {

							for (FileStatus status : statuses) {
								try {
									// filter name
									if (!HdfsUtils.HIDDEN_FILE_FILTER
											.accept(status.getPath())) {
										continue;
									}
									if (status.isDir()) {
										addDocument(
												fileSystemScanned
														.getIndexWriter(),
												fs.getCanonicalServiceName(),
												status.getPath());
										walkInSubdirectories(
												fileSystemScanned
														.getIndexWriter(),
												status, fs, 0);
									}
								} catch (AccessControlException ace) {
								}

							}
						}

						// Update scanner time
						fileSystemScanned.setLastScannedTime(DateTime.now());
						// server.commit();
						fileSystemScanned.getIndexWriter().commit();
					}
				} catch (IOException ioe) {
					ioe.printStackTrace();
					log.info("Can't update filepath history for HDFS "
							+ fileSystemScanned.fileSystem.getUri().toString());
					log.info("Last update : "
							+ fileSystemScanned.getLastScannedTime());
				}
			} // end for
		}
	}

	/**
	 * Add document in Lucene index
	 * 
	 * @param indexWriter
	 * @param value
	 * @throws IOException
	 */
	private static void addDocument(IndexWriter indexWriter,
			String fsCanonicalServiceName, Path path) throws IOException {

		Document doc = new Document();
		doc.add(new Field("fileSystem", fsCanonicalServiceName,
				Field.Store.YES, Field.Index.NOT_ANALYZED));

		doc.add(new Field("path", path.toUri().toString(), Field.Store.YES,
				Field.Index.NOT_ANALYZED));
		doc.add(new Field("highlightedpath", path.toUri().toString(),
				Field.Store.YES, Field.Index.ANALYZED,
				Field.TermVector.WITH_POSITIONS_OFFSETS));
		indexWriter.addDocument(doc);

	}

	/**
	 * Recursive function to search in sub directories
	 * 
	 * @param fileStatus
	 * @throws IOException
	 */
	private void walkInSubdirectories(IndexWriter indexWriter,
			FileStatus fileStatus, FileSystem fs, int deep) throws IOException {

		if (!HdfsUtils.HIDDEN_FILE_FILTER.accept(fileStatus.getPath())) {
			return;
		}

		// Recursively walk subdirectories
		if (fileStatus.isDir()) {
			FileStatus[] statuses = fs.listStatus(fileStatus.getPath());
			if (statuses != null) {
				for (FileStatus childStatus : statuses) {
					if (childStatus.isDir()) {
						addDocument(indexWriter, fs.getCanonicalServiceName(),
								childStatus.getPath());
						try {
							// To avoid a bug of recursivity
							if (deep < 20)
								deep++;
								walkInSubdirectories(indexWriter, childStatus, fs, deep);
						} catch (AccessControlException ace) {
						}
					}
				}
			}
		}
	}

}
