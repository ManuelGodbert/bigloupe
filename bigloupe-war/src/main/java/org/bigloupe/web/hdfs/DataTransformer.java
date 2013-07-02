package org.bigloupe.web.hdfs;

import java.util.List;

import org.json.simple.JSONObject;

public class DataTransformer {
	
	public static JSONObject getJSONTree(String rootPath, int depth,
			List<NodeData> data) {
		Tree t = new Tree(rootPath);
		for (NodeData nodeData : data) {
			t.add(nodeData);
		}
		return t.toJSON();
	}
}
