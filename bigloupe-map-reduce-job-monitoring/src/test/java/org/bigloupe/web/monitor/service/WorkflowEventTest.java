package org.bigloupe.web.monitor.service;

import java.io.IOException;
import java.util.List;

import org.bigloupe.web.monitor.util.JSONUtil;

import com.fasterxml.jackson.core.type.TypeReference;

public class WorkflowEventTest {

	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws IOException {
		String json = JSONUtil
				.readFile("src/test/resources/data/map-reduce-statistics/large-events.json");
		List<WorkflowEvent> events = (List<WorkflowEvent>) JSONUtil.readJson(
				json, new TypeReference<List<WorkflowEvent>>() {
				});
		for (WorkflowEvent event : events) {
			// useful if we need to read a file, add a field, output and
			// re-generate
		}

		JSONUtil.writeJson(
				"src/test/resources/data/small-events.json2", events);
	}
}
