package org.bigloupe.web.controller;

import java.io.File;

import javax.servlet.ServletContext;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.ServletContextAware;

/**
 * Admin FileManager
 * 
 * @author bigloupe
 * 
 */
@Controller
public class FileManagerController extends AbstractConfigurationController {

	@RequestMapping("/filemanager")
	public String getAdminFileManager(ModelMap model) {
		return "filemanager";
	}
	
	@RequestMapping("/filemanager/view/{file}")
	public String viewFile(@PathVariable String file, ModelMap model) {
		String filePath = servletContext.getRealPath("pig/" + file);	
		File fileToView = new File(filePath);
		if (fileToView.exists()) {
			model.addAttribute("fileToView", fileToView);
		}
		return "fileviewer";
	}

}
