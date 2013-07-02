package org.bigloupe.web.dto;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class PigOption {
	
	private String scriptDir = "scripts";

	private String scriptPig;
	
	/**
	 * List of variables
	 */
	private List<PairValue> scriptVariables = new ArrayList<PairValue>();

	public PigOption() {
	}

	public String getScriptPig() {
		return scriptDir + System.getProperty("file.separator") + scriptPig;
	}

	public void setScriptPig(String scriptPig) {
		this.scriptPig = scriptPig;
	}
	
	public String getScriptDir() {
		return scriptDir;
	}

	public List<PairValue> getScriptPigVariables() {
		return scriptVariables;
	}

	public void setScriptPigVariables(List<PairValue> scriptVariables) {
		this.scriptVariables = scriptVariables;
	}


}
