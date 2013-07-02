package org.bigloupe.web.service.hadoop;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.ServletContext;
import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.hdfs.tools.offlineImageViewer.DelimitedImageVisitorPublic;
import org.apache.hadoop.hdfs.tools.offlineImageViewer.OfflineImageViewer;
import org.bigloupe.web.BigLoupeConfiguration;
import org.bigloupe.web.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.context.ServletContextAware;


/**
 * 
 * Create offline image viewer service for an HDFS Use this tool from Hadoop
 * 0.23.3
 * 
 * @author bigloupe
 * 
 */
@Service
public class OfflineImageViewerService implements ServletContextAware {
	private Log log = LogFactory.getLog(OfflineImageViewerService.class);

	private ServletContext servletContext;

	@Autowired
	BigLoupeConfiguration configuration;

	@Autowired
	DataSource bigloupeDataSource;

	@Override
	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
	}

	/**
	 * Create every 3600 s
	 */
	//@Scheduled(fixedDelay = 3600000)
	@Scheduled(fixedDelay = 1000000)
	public void createImage() throws IOException, SQLException {

		log.info("Create image offline viewer HDFS for disk usage");
		
		File dirImage = new File(configuration.getFullDataDirectory()
				+ System.getProperty("file.separator")
				+ "tsv");
		// For each directory, create image
		File[] dirClusterList = dirImage.listFiles();
		for (File file : dirClusterList) {
			if (file.isDirectory()) {
				String fileSystem = file.getName();
				createImageByFileSystem(fileSystem);

			}
		}

	}

	private void createImageByFileSystem(String fileSystem) throws SQLException,
			IOException {
		log.info("Create image offline viewer HDFS for "
				+ fileSystem);
		
		// Create table if doesn't exist
		// boolean to check if image viewer must be loaded
		boolean load = false;
		Connection connection = bigloupeDataSource.getConnection();
		try {
			
			ResultSet results = connection.createStatement()
					.executeQuery("select count(*) from size_by_path_" + StringUtils.getCompatibleSQLTableName(fileSystem));

			while (results.next()) {
				Integer cnt = results.getInt(1);
				if (cnt == 0) {
					load = true;
					log.info("Offline Image viewer to be created for cluster '" + fileSystem + "'");
				}
				else
					log.info("Offline Image viewer already created for cluster '" + fileSystem + "'");
				log.info("Count of rows in size_by_path_"  + StringUtils.getCompatibleSQLTableName(fileSystem) + " table : " + cnt);
			}
		} catch (SQLException sqlException) {
			// Create Table
			Statement s = connection.createStatement();
			s.executeUpdate("drop table if exists size_by_path_" + StringUtils.getCompatibleSQLTableName(fileSystem));
			s.executeUpdate("create table size_by_path_" + StringUtils.getCompatibleSQLTableName(fileSystem) + " (path varchar(4096), "
					+ "size_in_bytes varchar(4096), file_count bigint, path_depth integer, leaf integer)");
			log.info("Create table 'size_by_path_" + StringUtils.getCompatibleSQLTableName(fileSystem) + "' in H2 Database");
		}

		if (load) {
				// Create file system image
				//createFileSystemImage(fileSystem);

				// launch pig
				// TODO

				// Create data in database from pig result
				fillDatabaseFromOfflineImageViewer(fileSystem, connection);

			
		}
		if ((connection != null) && (!connection.isClosed())) {
			connection.close();
		}
	}

	/**
	 * Fill database from offline image viewer
	 * 
	 * @param fs
	 */
	private void fillDatabaseFromOfflineImageViewer(String fileSystem, Connection connection)
			throws IOException, SQLException {
		FileInputStream fileInputStream = new FileInputStream(
				getPigResultFileName(fileSystem));
		DataInputStream in = new DataInputStream(fileInputStream);
		BufferedReader br = new BufferedReader(new InputStreamReader(in));

		String line;
		PreparedStatement s = connection
				.prepareStatement(
						"insert into size_by_path_" + StringUtils.getCompatibleSQLTableName(fileSystem)
								+ " (path, size_in_bytes, file_count, path_depth, leaf) values (?, ?, ?, ?, ?)");
		while (true) {
			line = br.readLine();
			if (line == null) {
				break;
			}
			String[] parts = line.split("\t");
			s.clearParameters();
			// 1. path:chararray,
			// 2. size_in_bytes:long,
			// 3. file_count:int,
			// 4. leaf

			s.setString(1, parts[0]);
			s.setString(2, parts[1]);
			s.setLong(3, Long.parseLong(parts[2]));
			s.setInt(4,
					parts[0].split("/").length == 0 ? 0
							: parts[0].split("/").length - 1);
			if (parts.length > 3)
				s.setInt(5, Integer.parseInt(parts[3]));
			else
				s.setInt(5, 0);
			s.executeUpdate();

		}
		connection.commit();

		Statement statement = connection.createStatement();

		ResultSet results = statement
				.executeQuery("select count(*) from size_by_path_" + StringUtils.getCompatibleSQLTableName(fileSystem));
		while (results.next()) {
			String cnt = results.getString(1);
			log.info("Count of rows in size_by_path_" + StringUtils.getCompatibleSQLTableName(fileSystem) + " table: " + cnt);
		}

		ResultSet resultSet = statement
				.executeQuery("select * from size_by_path_" + StringUtils.getCompatibleSQLTableName(fileSystem) + " limit 100");

		log.info("Sample lines read from DB:");
		while (resultSet.next()) {
			log.info(String.format(
					"path: %s  bytes: %s  file_count: %d  path_depth: %d",
					resultSet.getString("path"),
					resultSet.getString("size_in_bytes"),
					resultSet.getLong("file_count"),
					resultSet.getInt("path_depth")));
		}
	}

	/**
	 * Create filesystem image from offline image viewer file available in
	 * secondary namenode in /hadoop/tmp/dfs/namesecondary/current
	 * 
	 * @param fs
	 * @throws IOException
	 */
	private void createFileSystemImage(String fileSystem) throws IOException {
		log.info("Create fsimage for cluster : '"
				+ fileSystem + "'");
			String outputFileName = getOfflineImageViewerFileName(fileSystem);
			DelimitedImageVisitorPublic processor = new DelimitedImageVisitorPublic(
					outputFileName, false);

			String fsImageFileName = getFATSecondaryNameNode(fileSystem);

			OfflineImageViewer oiv = new OfflineImageViewer(fsImageFileName,
					processor, false);
			oiv.go();
	}

	/**
	 * Get secondary namenode FAT file 
	 * available in /app/KARMAlocal/hadoop/tmp/dfs/namesecondary/current
	 */
	private String getFATSecondaryNameNode(String fileSystem) {
	return configuration
			.getFullDataDirectory()
			+ System.getProperty("file.separator")
			+ "tsv"
			+ System.getProperty("file.separator")
							+ fileSystem 
				+ System.getProperty("file.separator")
			+ "fsimage-"
			+ fileSystem;
	}
	
	/**
	 * Get offline image viewer filename (in webapp\data\tsv)
	 * 
	 * @param fs
	 * @return
	 */
	private String getOfflineImageViewerFileName(String fileSystem) {
		return configuration.getFullDataDirectory()
				+ System.getProperty("file.separator")
				+ "tsv"
				+ System.getProperty("file.separator")
				+ fileSystem 
				+ System.getProperty("file.separator")
				+ "fsimage-oiv-delimited-"
				+ fileSystem + ".tsv";
	}

	/**
	 * Get offline image viewer filename (in webapp\data\tsv)
	 * 
	 * @param fs
	 * @return
	 */
	private String getPigResultFileName(String fileSystem) {
		return configuration.getFullDataDirectory()
				+ System.getProperty("file.separator") + "tsv"
				+ System.getProperty("file.separator")
				+ fileSystem 
				+ System.getProperty("file.separator") + "hdfsdu-" + fileSystem + ".data";
	}
}
