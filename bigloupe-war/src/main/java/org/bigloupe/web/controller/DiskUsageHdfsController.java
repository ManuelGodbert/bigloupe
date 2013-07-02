package org.bigloupe.web.controller;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.bigloupe.web.hdfs.DataTransformer;
import org.bigloupe.web.hdfs.NodeData;
import org.bigloupe.web.util.StringUtils;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

@Controller
public class DiskUsageHdfsController extends AbstractConfigurationController {

	private static Logger logger = Logger
			.getLogger(DiskUsageHdfsController.class);

	@Autowired
	DataSource bigloupeDataSource;

	@RequestMapping(value = "/diskUsageHdfs/{cluster}", method = RequestMethod.GET)
	public String diskUsage(
			@PathVariable("cluster") String hadoopConfigurationCluster,
			ModelMap model, HttpServletRequest request) {
		model.addAttribute("configuration", configuration);
		return "hdfsdiskusage";
	}

	/**
	 * Return JSON but build manually and not with Jackson object converter
	 * 
	 * @param cluster
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping(value = "/diskUsageHdfs/sizeByPath/{cluster}", method = RequestMethod.GET)
	public void getLinesSizeByPath(
			@PathVariable("cluster") String cluster, HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.setContentType("application/json");
		cluster = StringUtils.getCompatibleSQLTableName(cluster);
		List<String> lines = Lists.newLinkedList();
		ObjectMapper mapper = new ObjectMapper();

		try {
			List<Map<String, String>> results = Lists.newArrayList();
			ResultSet resultSet = getSizeByPath(cluster, request);

			while (resultSet.next()) {
				Map<String, String> entry = Maps.newHashMap();
				entry.put("path", resultSet.getString("path"));
				entry.put("bytes", resultSet.getString("size_in_bytes"));
				entry.put("count", resultSet.getString("file_count"));
				entry.put("leaf", resultSet.getString("leaf"));
				results.add(entry);
			}

			StringWriter stringWriter = new StringWriter();
			mapper.writeValue(stringWriter, results);
			lines.add(stringWriter.toString());
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (JsonGenerationException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		Writer writer = response.getWriter();
		for (String line : lines) {
			writer.write(line);
		}
	}

	/**
	 * Return JSON but build manually and not with Jackson object converter
	 * 
	 * @param cluster
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping(value = "/diskUsageHdfs/treeSizeByPath/{cluster}", method = RequestMethod.GET)
	public void getLinesTreeSizeByPath(@PathVariable("cluster") String cluster,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		response.setContentType("application/json");
		cluster = StringUtils.getCompatibleSQLTableName(cluster);
		String paramPath = request.getParameter("path");
		if (paramPath == null) {
			paramPath = "/";
		}
		List<String> lines = Lists.newLinkedList();
		List<org.bigloupe.web.hdfs.NodeData> elems = Lists
				.newArrayList();
		Integer paramDepth = request.getParameter("depth") == null ? 2
				: Integer.parseInt(request.getParameter("depth"));

		try {
			ResultSet resultSet = getSizeByPath(cluster, request);
			NodeData data;
			while (resultSet.next()) {
				data = new NodeData();
				data.fileSize = resultSet.getString("size_in_bytes");
				data.nChildren = resultSet.getLong("file_count");
				data.path = resultSet.getString("path");
				data.leaf = resultSet.getBoolean("leaf");
				elems.add(data);
			}
			JSONObject jsonObject = DataTransformer.getJSONTree(paramPath,
					paramDepth, elems);

			String ans = null;
			if (jsonObject != null) {
				ans = jsonObject.toJSONString();
			}

			if (ans == null) {
				lines.add("{ \"children\": [] }");
			} else {
				lines.add(ans);
			}
			Connection connection = resultSet.getStatement().getConnection();
			if ((connection != null) && (!connection.isClosed())) {
				connection.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		Writer writer = response.getWriter();
		for (String line : lines) {
			writer.write(line);
		}

	}

	public ResultSet getSizeByPath(String hadoopConfigurationCluster,
			HttpServletRequest request) throws SQLException {
		String paramPath = request.getParameter("path");
		if (paramPath == null) {
			paramPath = "/";
		}
		int paramPathDepth = paramPath.split("/").length == 0 ? 0 : paramPath
				.split("/").length - 1;
		logger.info("Depth of " + paramPath + " is " + paramPathDepth);

		Integer paramDepth = request.getParameter("depth") == null ? 2
				: Integer.parseInt(request.getParameter("depth"));
		paramDepth += paramPathDepth;

		Integer paramLimit = request.getParameter("limit") == null ? 100
				: Integer.parseInt(request.getParameter("limit"));

		Connection connection = bigloupeDataSource.getConnection();
		Statement statement = connection.createStatement();
		String query;
		if (paramPath.equals("/")) {
			query = "select * from size_by_path_" + hadoopConfigurationCluster
					+ " where (path like '" + paramPath
					+ "%') and path_depth <= " + paramDepth
					+ " order by path limit " + paramLimit;
		} else {
			query = "select * from size_by_path_" + hadoopConfigurationCluster
					+ " where (path like '" + paramPath + "/%' or path = '"
					+ paramPath + "') and path_depth <= " + paramDepth
					+ " order by path limit " + paramLimit;
		}
		logger.info("Running query: " + query);

		return statement.executeQuery(query);
	}
}
