package org.bigloupe.web.hdfs;

import org.json.simple.JSONObject;

public class NodeData {
	public String path;
	public String fileSize;
	public Long nChildren = 0L;
	public boolean leaf;

	public JSONObject toJSON() {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("path", this.path);
		jsonObject.put("fileSize", fileSize);
		jsonObject.put("nChildren", nChildren);
		jsonObject.put("leaf", leaf);
		return jsonObject;
	}
}
