package org.bigloupe.web.chart.controller;

import org.bigloupe.web.BigLoupeConfiguration;
import org.bigloupe.web.chart.util.DocumentationRestApi;
import org.bigloupe.web.controller.AbstractConfigurationController;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.WebApplicationContext;

import com.wordnik.swagger.core.Documentation;

@Controller
@RequestMapping("/chart/api-docs")
public class DocumentationController extends AbstractConfigurationController
		implements InitializingBean {

	private String apiVersion = "1.0";

	private String swaggerVersion = "1.1";

	private DocumentationRestApi documentationApiReader;

	@RequestMapping(method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody
	Documentation getResourceListing() {
		return documentationApiReader.getResourceListing();
	}

	public void afterPropertiesSet() throws Exception {
		String documentationBasePath = BigLoupeConfiguration
				.getConfigurationWebBigLoupeServer();
		documentationApiReader = new DocumentationRestApi(apiVersion, swaggerVersion,
				documentationBasePath, "/chart/api-docs.json");
	}

	public String getApiVersion() {
		return apiVersion;
	}

	public void setApiVersion(String apiVersion) {
		this.apiVersion = apiVersion;
	}

	public String getSwaggerVersion() {
		return swaggerVersion;
	}

	public void setSwaggerVersion(String swaggerVersion) {
		this.swaggerVersion = swaggerVersion;
	}

}
