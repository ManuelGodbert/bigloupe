package org.bigloupe.web.filemanager.command;

import java.io.File;

import org.bigloupe.web.filemanager.ConnectorException;
import org.bigloupe.web.filemanager.FileActionEnum;
import org.bigloupe.web.filemanager.FsException;


/**
 * @author Antoine Walter (www.anw.fr)
 * @date 29 aug. 2011
 * @version $Id$
 * @license BSD
 */
public class RenameCommand extends AbstractCommandOverride {

	@Override
	public void execute() throws ConnectorException {
		File dirCurrent = getExistingDir(getParam("current"), FileActionEnum.CREATE_FILE);
		if (dirCurrent != null) {
			File targetFile = getExistingFile(getParam("target"), dirCurrent, FileActionEnum.DELETE);
			File futureFile = getNewFile(getParam("name"), dirCurrent, FileActionEnum.WRITE);
			try {
				getFs().renameFileOrDirectory(targetFile, futureFile);
			} catch (FsException e) {
				throw new ConnectorException("Unable to rename file", e);
			}

			putResponse("select", _hash(targetFile));
			_content(dirCurrent, true);
		}
	}

}
