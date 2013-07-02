package org.bigloupe.web.monitor.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bigloupe.web.monitor.service.DAGNode;
import org.bigloupe.web.monitor.service.StatsWriteService;
import org.bigloupe.web.monitor.service.WorkflowEvent;
import org.bigloupe.web.util.json.MappingJackson2HttpMessageConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

/**
 * Http implementation of StatsWriteService for JVM Pig. Communicate via HTTP
 * with Bigloupe to update statistics
 * 
 * @author bigloupe
 */
public class RemoteStatsWriteService implements StatsWriteService {
	private static final Logger LOG = LoggerFactory
			.getLogger(RemoteStatsWriteService.class);

	// URL where Bigloupe is available (e.g. localhost:9090)
	String bigloupeServer;

	RestTemplate restTemplate;
	HttpHeaders headers;

	public RemoteStatsWriteService(String bigloupeServer) {
		this.bigloupeServer = bigloupeServer + "/map-reduce-statistics";
		restTemplate = new RestTemplate();
		List<HttpMessageConverter<?>> messageConverters = restTemplate
				.getMessageConverters();
		List<HttpMessageConverter<?>> converters = new ArrayList<HttpMessageConverter<?>>(
				messageConverters);

		MappingJackson2HttpMessageConverter jsonConverter = new MappingJackson2HttpMessageConverter();
		headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		converters.add(jsonConverter);
		restTemplate.setMessageConverters(converters);

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
		try {

			HttpEntity<Map<String, DAGNode>> request = new HttpEntity<Map<String, DAGNode>>(
					dagNodeNameMap, headers);
			Boolean responseStatus = restTemplate.postForObject(bigloupeServer
					+ "/sendDagNodeNameMap/{workflowId}.html", request,
					Boolean.class, workflowId);
			if (!responseStatus) {
				LOG.info("Can't send dag nodename map for workflow : "
						+ workflowId);

			} else
				LOG.info("Push event to BigLoupe server ('" + bigloupeServer
						+ "') for workflow '" + workflowId + "'");

		} catch (HttpClientErrorException httpException) {
			LOG.info("Server BigLoupe '" + bigloupeServer + "' is unavailable");
		} catch (Exception e) {
			e.printStackTrace();
		}
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
		try {
			HttpEntity<WorkflowEvent> request = new HttpEntity<WorkflowEvent>(
					event, headers);
			Boolean responseStatus = restTemplate.postForObject(bigloupeServer
					+ "/pushEvent/{workflowId}.html", request, Boolean.class,
					workflowId);
			if (!responseStatus) {
				LOG.info("Can't send event for workflow : " + workflowId);

			} else
				LOG.info("Push event to BigLoupe server ('" + bigloupeServer
						+ "') for workflow '" + workflowId + "'");
		} catch (HttpClientErrorException httpException) {
			LOG.info("Server BigLoupe '" + bigloupeServer + "' is unavailable");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
