package org.bigloupe.web.filemanager.command;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.log4j.Logger;
import org.bigloupe.web.filemanager.ConnectorException;
import org.bigloupe.web.filemanager.FileActionEnum;


public class OpenCommand extends AbstractCommandOverride {
	private static Logger logger = Logger.getLogger(OpenCommand.class);

	@Override
	public void execute() throws ConnectorException {
		File fileCurrent = getExistingDir(getParam("current"), FileActionEnum.READ);
		if (fileCurrent != null) {
			File fileTarget = getExistingFile(getParam("target"), fileCurrent, FileActionEnum.READ);
			serveFile(fileTarget);
		} else {
			contentCommand();
		}
	}

	protected void serveFile(File fileTarget) throws ConnectorException {
System.err.println("serveFile : " + fileTarget);
		String mime = getMime(fileTarget);
		String disp = getMimeDisposition(mime);

		getResponse().setContentType(mime);
		String fileUrl = getFileUrl(fileTarget);
		String fileUrlRelative = getFileUrl(fileTarget);
		getResponse().setHeader("Content-Disposition", disp + "; filename=" + fileUrl);
		getResponse().setHeader("Content-Location", fileUrlRelative);
		getResponse().setHeader("Content-Transfer-Encoding", "binary");
		getResponse().setHeader("Connection", "close");

		InputStream is = null;
		try {
			// serve file
			is = new FileInputStream(fileTarget);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			byte[] b = new byte[500];
			int nb;
			while ((nb = is.read(b)) > 0) {
				getResponseWriter().write(nb);
				baos.write(b, 0, nb);
			}
			b = baos.toByteArray();
			getResponse().setContentLength(b.length);

			closeWriter(getResponseWriter());
			setResponseOutputDone(true);
		} catch (Exception e) {
			logger.error("", e);
			throw new ConnectorException("Unknown error");
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {

				}
			}
		}
	}
}
