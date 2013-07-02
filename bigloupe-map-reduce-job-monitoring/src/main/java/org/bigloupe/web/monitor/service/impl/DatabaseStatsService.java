package org.bigloupe.web.monitor.service.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bigloupe.web.monitor.service.DAGNode;
import org.bigloupe.web.monitor.service.StatsReadService;
import org.bigloupe.web.monitor.service.StatsWriteService;
import org.bigloupe.web.monitor.service.WorkflowEvent;
import org.bigloupe.web.monitor.service.WorkflowEvent.EVENT_TYPE;
import org.bigloupe.web.monitor.util.JSONUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

/**
 * Database implementation of both StatsReadService and StatsWriteService. Can
 * be used inside BigLoupe to read and write statistics
 * 
 * @author bigloupe
 */
@Service
public class DatabaseStatsService implements StatsReadService,
		StatsWriteService, InitializingBean {
	private static final Logger LOG = LoggerFactory
			.getLogger(DatabaseStatsService.class);

	private static final String TABLE_NAME_PIG_STATS_EVENTS = "PIG_STATS_EVENTS";
	private static final String TABLE_NAME_PIG_STATS_JOBNODE = "PIG_STATS_JOBNODE";

	@Autowired
	private JdbcTemplate bigloupeJdbcTemplate;

	private boolean cleanTable = false;

	public DatabaseStatsService() {
	}

	/**
	 * Send a map of all DAGNodes in the workflow. The structure of the DAG is
	 * assumed to be immutable. For that reason, visualization clients are
	 * expected to request the DAG once for initial rendering and then poll for
	 * events after that. For that reason this method only makes sense to be
	 * called once. Subsequent calls to modify the DAG will likely go unnoticed.
	 * 
	 * @param workflowId
	 *            the id of the workflow being updated
	 * @param dagNodeNameMap
	 *            a Map of DAGNodes where the key is the DAGNode name
	 */
	@Override
	public synchronized void sendDagNodeNameMap(String workflowId,
			Map<String, DAGNode> dagNodeNameMap) {

		// Save Map of dagNode for workflowId
		bigloupeJdbcTemplate.update("insert into " + TABLE_NAME_PIG_STATS_JOBNODE
				+ " (workflowId, dagNodeNameMap) values (?, ?)", new Object[] {
				workflowId, dagNodeNameMap });
	}

	/**
	 * List all workflow stored in database
	 */
	public List<String> getWorkflows() {
		List<String> listWorkflow = bigloupeJdbcTemplate.query(
				"select distinct(workflowId) from "
						+ TABLE_NAME_PIG_STATS_JOBNODE,
				new RowMapper<String>() {
					@Override
					public String mapRow(ResultSet rs, int rowNum)
							throws SQLException {
						return rs.getString("workflowId");

					}
				});
		return listWorkflow;
	}

	/**
	 * Delete workflow in event table
	 * @param workflowId
	 * @return
	 */
	public boolean deleteWorkflow(String workflowId) {
		int numRow = bigloupeJdbcTemplate.update("delete from "
				+ TABLE_NAME_PIG_STATS_JOBNODE + " WHERE workflowId = ?",
				new Object[] { workflowId });
		
		numRow += bigloupeJdbcTemplate.update("delete from "
				+ TABLE_NAME_PIG_STATS_EVENTS + " WHERE workflowId = ?",
				new Object[] { workflowId });
		
		
		
		return (numRow == 2) ? true : false;
	}

	/**
	 * Get a map of all DAGNodes in the workflow.
	 * 
	 * @param workflowId
	 *            the id of the workflow being fetched
	 * @return a Map of DAGNodes where the key is the DAGNode name
	 */
	@Override
	public synchronized Map<String, DAGNode> getDagNodeNameMap(String workflowId) {
		List<Map<String, DAGNode>> dagNodeName = bigloupeJdbcTemplate.query(
				"select workflowId, dagNodeNameMap from "
						+ TABLE_NAME_PIG_STATS_JOBNODE + " where workflowId='"
						+ workflowId + "'",
				new RowMapper<Map<String, DAGNode>>() {
					@Override
					public Map<String, DAGNode> mapRow(ResultSet rs, int rowNum)
							throws SQLException {
						byte[] dagNodeNameMapBytes = rs
								.getBytes("dagNodeNameMap");
						Map<String, DAGNode> dagNodeNameMapObject = null;
						try {
							ObjectInputStream dagNodeNameMapObjectIn = new ObjectInputStream(
									new ByteArrayInputStream(
											dagNodeNameMapBytes));
							dagNodeNameMapObject = (Map<String, DAGNode>) dagNodeNameMapObjectIn
									.readObject();
						} catch (Exception e) {
							LOG.debug("Can't deserialize :" + e.getMessage());
						}
						return dagNodeNameMapObject;
					}
				});
		if (!dagNodeName.isEmpty())
			return dagNodeName.get(0);
		else
			return new HashMap<String, DAGNode>();
	}

	@Override
	public synchronized Collection<WorkflowEvent> getEventsSinceId(
			String workflowId, int sinceId) {
		Collection<WorkflowEvent> events = bigloupeJdbcTemplate
				.query("select workflowId, eventId, timestamp, runtime, eventType, eventDataObjectName, eventData from "
						+ TABLE_NAME_PIG_STATS_EVENTS
						+ " where workflowId='"
						+ workflowId + "'", new RowMapper<WorkflowEvent>() {

					@Override
					public WorkflowEvent mapRow(ResultSet rs, int rowNum)
							throws SQLException {
						byte[] eventDataBytes = rs.getBytes("eventData");
						Object evenDataObject = null;
						try {
							ObjectInputStream eventDataObjectIn = new ObjectInputStream(
									new ByteArrayInputStream(eventDataBytes));
							evenDataObject = eventDataObjectIn.readObject();
						} catch (Exception e) {
							LOG.debug("Can't deserialize :" + e.getMessage());
						}

						String eventType = rs.getString("eventType");

						WorkflowEvent event = new WorkflowEvent(rs
								.getInt("eventId"), EVENT_TYPE
								.valueOf(eventType), evenDataObject, rs
								.getString("runtime"), rs.getLong("timestamp"));
						return event;
					}

				});
		return events;
	}

	/**
	 * Push an events for a given workflow.
	 * 
	 * @param workflowId
	 *            the id of the workflow being updated
	 * @param event
	 *            the event bound to the workflow
	 */
	@Override
	public synchronized void pushEvent(String workflowId, WorkflowEvent event) {
		bigloupeJdbcTemplate
				.update("insert into "
						+ TABLE_NAME_PIG_STATS_EVENTS
						+ " (workflowId, eventId, timestamp, runtime, eventType, eventDataObjectName, eventData) values (?, ?, ?, ?, ?, ?, ?)",
						new Object[] { workflowId, event.getEventId(),
								event.getTimestamp(), event.getRuntime(),
								event.getEventType().name(),
								event.getEventData().getClass().getName(),
								event.getEventData() });
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		// Create table if necessary
		try {
			if (isCleanTable()) {
				LOG.info("Initialization mode : drop table "
						+ TABLE_NAME_PIG_STATS_EVENTS);
				dropTables();
			}
			int countEvents = bigloupeJdbcTemplate
					.queryForInt("select count(*) from "
							+ TABLE_NAME_PIG_STATS_EVENTS);

		} catch (DataAccessException dae) {
			dropTables();
			LOG.info("Table " + TABLE_NAME_PIG_STATS_EVENTS + " or table "
					+ TABLE_NAME_PIG_STATS_JOBNODE + " doesn't exist");
			LOG.info("Create table " + TABLE_NAME_PIG_STATS_EVENTS);
			bigloupeJdbcTemplate
					.execute("create table "
							+ TABLE_NAME_PIG_STATS_EVENTS
							+ " (workflowId varchar(300), eventId integer not null, timestamp long, runtime varchar(4096), eventType varchar(300), eventDataObjectName varchar(100), eventData blob)");
			LOG.info("Create table " + TABLE_NAME_PIG_STATS_JOBNODE);
			bigloupeJdbcTemplate.execute("create table "
					+ TABLE_NAME_PIG_STATS_JOBNODE
					+ " (workflowId varchar(300), dagNodeNameMap blob)");

		}

	}

	private void dropTables() {
		try {
			bigloupeJdbcTemplate.execute("drop table " + TABLE_NAME_PIG_STATS_EVENTS);
		} catch (DataAccessException e) {
			LOG.info("Can't drop table " + TABLE_NAME_PIG_STATS_EVENTS);
		}
		try {
			bigloupeJdbcTemplate
					.execute("drop table " + TABLE_NAME_PIG_STATS_JOBNODE);
		} catch (DataAccessException e) {
			LOG.info("Can't drop table " + TABLE_NAME_PIG_STATS_JOBNODE);
		}

	}

	public JdbcTemplate getJdbcH2Template() {
		return bigloupeJdbcTemplate;
	}

	public void setJdbcH2Template(JdbcTemplate jdbcH2Template) {
		this.bigloupeJdbcTemplate = jdbcH2Template;
	}

	public boolean isCleanTable() {
		return cleanTable;
	}

	public void setCleanTable(boolean cleanTable) {
		this.cleanTable = cleanTable;
	}

}
