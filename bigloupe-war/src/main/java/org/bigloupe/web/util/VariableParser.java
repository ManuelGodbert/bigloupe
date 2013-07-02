package org.bigloupe.web.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.bigloupe.web.dto.PairValue;


/**
 * Parse all variables from a file
 * 
 * @author bigloupe
 *
 */
public class VariableParser {

	public static List<PairValue> getListVariable(File sourceFile)
			throws FileNotFoundException, IOException {
		List<PairValue> variables = new ArrayList<PairValue>();
		LineIterator it = FileUtils.lineIterator(sourceFile, "UTF-8");
		try {
			while (it.hasNext()) {
				String line = it.nextLine();
				if (line.indexOf("'$") != -1) {
					PairValue pairValue = new PairValue(line.substring(line.indexOf("'$") + 1,
							line.indexOf('\'', line.indexOf("'$") + 1)));
					variables.add(pairValue);
				}
			}
		} finally {
			LineIterator.closeQuietly(it);
		}

		return variables;
	}
}
