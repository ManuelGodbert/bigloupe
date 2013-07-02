package org.bigloupe.web.filemanager;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.bigloupe.web.filemanager.config.AbstractConnectorConfig;


/**
 * File manager servlet implementation.
 */
public class FileManagerServlet extends AbstractConnectorServlet {

	private static final long serialVersionUID = -4866523596079769725L;

	private static Logger S_LOG = Logger.getLogger(FileManagerServlet.class);

	public static final String SERVLET_URL = "/docs/";

	@Override
	protected AbstractConnectorConfig prepareConfig(HttpServletRequest request) {
		// here we could use various configs based on request URL/cookies...
		return new FileManagerConfig();
	}

}
