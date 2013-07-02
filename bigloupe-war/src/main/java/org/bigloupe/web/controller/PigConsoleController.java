package org.bigloupe.web.controller;

import java.io.IOException;
import java.io.Writer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.bigloupe.web.BigLoupeConfiguration;
import org.bigloupe.web.dto.ResponseStatus;
import org.bigloupe.web.service.common.LogService;
import org.bigloupe.web.util.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Display a table of logs file refreshed in LogService {@link LogService}
 * 
 * @author bigloupe
 * 
 */
@Controller
public class PigConsoleController extends AbstractSchedulerController {

	@Autowired
	LogService logService;

	/**
	 * Display console Pig
	 * 
	 * @param request
	 * @param model
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	@RequestMapping(value = "/pigConsole", method = RequestMethod.GET)
	protected String pigConsole(HttpServletRequest request, ModelMap model)
			throws ServletException, IOException {
		model.addAttribute("pigPath",
				servletContext.getRealPath(configuration.getPigDirectory()));
		model.addAttribute("schedulerPath", servletContext
				.getRealPath(BigLoupeConfiguration.getSchedulerDirectory()));
		return "pig-console";
	}

	@RequestMapping(value = "/changeLogDirectoryToScan", method = RequestMethod.GET)
	@ResponseBody
	protected ResponseStatus changeLogDirectoryToScan(
			@RequestParam("logDir") boolean pigLogDirOrSchedulerLogDir)
			throws ServletException, IOException {
		ResponseStatus responseStatus = new ResponseStatus(true);
		configuration.setPigLogDirOrSchedulerLogDir(pigLogDirOrSchedulerLogDir);
		return responseStatus;
	}

	/**
	 * View file Log Can only view file in pig dir or scheduler dir
	 * 
	 * @param file
	 * @param req
	 * @param resp
	 * @throws ServletException
	 * @throws IOException
	 */
	@RequestMapping(value = "/viewFileLog", method = RequestMethod.GET)
	protected void viewJobConsoleLogForJobId(
			@RequestParam("fileLog") String fileLog,
			@RequestParam("logDir") boolean pigLogDirOrSchedulerLogDir,
			HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		resp.setContentType("text/html");
		String fileName;
		Writer writer = resp.getWriter();
		writer.append("<html><head><link rel=\"stylesheet\"" + "href=\"")
				.append(servletContext.getContextPath())
				.append("resources/css/common/bootstrap.min.css\""
						+ "type=\"text/css\" media=\"screen\" /></head><body><pre>");
		if (pigLogDirOrSchedulerLogDir)
			fileName = servletContext.getRealPath(configuration
					.getPigDirectory() + "/" + fileLog.replace("\\", "/"));
		else
			fileName = servletContext
					.getRealPath(BigLoupeConfiguration.getSchedulerDirectory() + "/"
							+ fileLog.replace("\\", "/"));

		Utils.viewFile(fileName, true, req, resp);
		writer.append("</pre></body></html>");

	}

	/**
	 * Delete all logs available in pig or scheduler directory
	 */
	@RequestMapping(value = "/deleteLog", method = RequestMethod.GET)
	@ResponseBody
	public ResponseStatus deleteLog(ModelMap model) throws ServletException, IOException {
		ResponseStatus responseStatus = new ResponseStatus(true);
		logService.deleteLog();
		responseStatus.setMessage("All log files has been deleted");
		return responseStatus;

	}

}
