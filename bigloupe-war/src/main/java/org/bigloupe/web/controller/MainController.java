package org.bigloupe.web.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.dbcp.BasicDataSource;
import org.bigloupe.web.BigLoupeConfiguration;
import org.bigloupe.web.dto.ResponseStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

/**
 * Main controller
 * 
 * @author bigloupe
 * 
 */
@Controller
public class MainController extends AbstractSchedulerController {
	private static Logger logger = LoggerFactory
			.getLogger(MainController.class);

	@Autowired
	BasicDataSource bigloupeDataSource;
	
	@Autowired
	private RequestMappingHandlerMapping handlerMapping;

	@ModelAttribute("configurationHadoopKarmaCluster")
	public Map<String, String> populateHadoopKarmaCluster() {
		return configuration.getHadoopClusters();
	}
	
	/**
	 * Handle index page (home page)
	 */
	@RequestMapping("/index")
	public String showIndexPage(
			ModelMap model,
			@ModelAttribute("configurationHadoopKarmaCluster") Map<String, String> hadoopKarmaCluster,
			SessionStatus sessionStatus) {
		model.addAttribute("configuration", configuration);
		// This will resolve to /WEB-INF/jsp/index.jsp
		return "index";
	}

	/**
	 * BigLoupe configuration
	 */
	@RequestMapping("/configuration")
	protected String search(ModelMap model) {
		model.addAttribute("configuration", configuration);
		model.addAttribute("dataSource", bigloupeDataSource);
		return "configuration";
	}

	@RequestMapping(value = "/changeUseAmbroseInBigLoupe", method = RequestMethod.POST)
	@ResponseBody
	protected ResponseStatus changeUseAmbroseInBigLoupe(boolean useAmbroseInBigLoupe, ModelMap model) {
		ResponseStatus responseStatus = new ResponseStatus();
		configuration.setUseAmbroseInBigLoupe(useAmbroseInBigLoupe);
		model.addAttribute("configuration", configuration);
		String response = "<p>Information successfully saved - <span class=\"label label-important\">Ambrose ";
		response += (useAmbroseInBigLoupe ? "ON" : "OFF") + "</span></p>";
		responseStatus.setMessage(response);
		responseStatus.setSuccess(true);
		return responseStatus;
	}

	@RequestMapping("/contact")
	public String showContactPage() {
		return "contact";
	}

	@RequestMapping("/databaseInformation")
	public String showDatabaseInformaton(ModelMap model) {
		model.addAttribute("dataSource", bigloupeDataSource);
		return "database-info";
	}

	@RequestMapping("/endpoint")
	public String show(Model model) {
		model.addAttribute("handlerMethods",
				this.handlerMapping.getHandlerMethods());
		return "endpoint";
	}

}
