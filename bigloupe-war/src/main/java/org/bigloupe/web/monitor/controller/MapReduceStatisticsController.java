package org.bigloupe.web.monitor.controller;

import java.io.StringReader;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.bigloupe.web.controller.AbstractConfigurationController;
import org.bigloupe.web.monitor.service.DAGNode;
import org.bigloupe.web.monitor.service.DAGTransformer;
import org.bigloupe.web.monitor.service.WorkflowEvent;
import org.bigloupe.web.monitor.service.impl.DatabaseStatsService;
import org.bigloupe.web.monitor.service.impl.SugiyamaLayoutTransformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.support.SessionStatus;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * This class get information for a job launched by Apache PIG. Pig script must
 * be launched with VM parameter :
 * -Dpig.notification.listener=org.bigloupe.web.monitor.pig.
 * BigLoupePigProgressNotificationListener
 * 
 * @author bigloupe
 * 
 */
@Controller
@RequestMapping("/map-reduce-statistics")
public class MapReduceStatisticsController extends
		AbstractConfigurationController {

	@Autowired
	private DatabaseStatsService statsReadService;

	/**
	 * Handle index page (home page)
	 */
	@RequestMapping("/index")
	public String showIndexStatisticsPage(
			@RequestParam(value = "workflowId", required = false) String workflowId,
			ModelMap model,
			@ModelAttribute("configurationHadoopKarmaCluster") Map<String, String> hadoopKarmaCluster,
			SessionStatus sessionStatus) {
		model.addAttribute("configuration", configuration);
		if (workflowId == null) {
			List<String> workflows = statsReadService.getWorkflows();
			model.addAttribute("workflows", workflows);
		}

		return "statistics";
	}
	
	/**
	 * Delete information concerning a specific workflow
	 */
	@RequestMapping("/delete")
	public String deleteWorkflowId(@RequestParam(value = "workflowId", required = false) String workflowId,
			ModelMap model) {
		if (workflowId != null) {
			statsReadService.deleteWorkflow(workflowId);
			List<String> workflows = statsReadService.getWorkflows();
			model.addAttribute("workflows", workflows);
		}
		return "statistics";
	}

	@RequestMapping(value = "/graphDag", method = RequestMethod.GET)
	@ResponseBody
	public DAGNode[] graphDag(
			@RequestParam(value = "workflowId", required = false) String workflowId,
			ModelMap model, HttpServletRequest request) {

		Collection<DAGNode> nodes = statsReadService.getDagNodeNameMap(
				workflowId).values();
		if (nodes.isEmpty())
			return new DAGNode[0];
		// add the x, y coordinates
		DAGTransformer dagTransformer = new SugiyamaLayoutTransformer(true);
		dagTransformer.transform(nodes);

		return nodes.toArray(new DAGNode[nodes.size()]);
	}

	@RequestMapping(value = "/getEvents", method = RequestMethod.GET)
	@ResponseBody
	public WorkflowEvent[] getEvents(
			@RequestParam("workflowId") String workflowId,
			@RequestParam(value = "sinceId", required = false) Integer sinceId,
			ModelMap model, HttpServletRequest request) {

		if (sinceId == null)
			sinceId = -1;

		Collection<WorkflowEvent> events = statsReadService.getEventsSinceId(
				workflowId, sinceId);

		return events.toArray(new WorkflowEvent[events.size()]);
	}

	/**
	 * Save in database event receive from RemoteStatsWriteService (called for
	 * e.g. in BigLoupePigProgressNotificationListener)
	 * 
	 * @param workflowId
	 * @param event
	 * @return
	 */
	@RequestMapping(value = "/pushEvent/{workflowId}", method = RequestMethod.POST, headers = { "content-type=application/json" })
	@ResponseBody
	public Boolean pushEvent(
			@PathVariable(value = "workflowId") String workflowId,
			@RequestBody WorkflowEvent event) {
		try {
			statsReadService.pushEvent(workflowId, event);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Save in database dag node name information receive from RemoteStatsWriteService (called for
	 * e.g. in BigLoupePigProgressNotificationListener)
	 * 
	 * @param workflowId
	 * @param dagNodeNameMap
	 * @return
	 */
	@RequestMapping(value = "/sendDagNodeNameMap/{workflowId}", method = RequestMethod.POST, headers = { "content-type=application/json" })
	@ResponseBody
	public Boolean sendDagNodeNameMap(HttpServletRequest request,
			@PathVariable(value = "workflowId") String workflowId) {

		try {
			ObjectMapper mapper = new ObjectMapper();
			TypeReference<HashMap<String, DAGNode>> typeRef = new TypeReference<HashMap<String, DAGNode>>() {};
			HashMap map = mapper.readValue(request.getInputStream(), HashMap.class);
			Map<String, DAGNode> dagNodeNameMap = mapper.convertValue(map,
					typeRef);

			statsReadService.sendDagNodeNameMap(workflowId, dagNodeNameMap);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

}
