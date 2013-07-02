package org.bigloupe.web;

import static org.fusesource.jansi.Ansi.*;
import static org.fusesource.jansi.Ansi.Attribute.*;
import static org.fusesource.jansi.Ansi.Color.*;
import static org.fusesource.jansi.AnsiRenderer.*;

import java.io.File;
import java.lang.management.ManagementFactory;

import org.eclipse.jetty.jmx.MBeanContainer;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.webapp.WebAppContext;
import org.fusesource.jansi.AnsiConsole;

/**
 * 
 * This class launches the web application in an embedded Jetty container. This
 * is the entry point to your application. The Java command that is used for
 * launching should fire this main method.
 * 
 * @author bigloupe
 */
public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		AnsiConsole.systemInstall();
		System.out.println(ansi().eraseScreen().fg(RED).render("Big Loupe for testing only"));
		System.out.println("Please use bigloupe.jar (project bigloupe-launcher) to launch full application");

		String baseDir = "src/main/webapp";
		
		// The port that we should run on can be set into an environment
		// variable
		// Look for that variable and default to 8080 if it isn't there.
		String webPort = System.getenv("PORT");
		if (webPort == null || webPort.isEmpty()) {
			webPort = "9090";
		}


		Server server = new Server(Integer.valueOf(webPort));
		addJMXSupport(server);
		
		/**
		 * Main Root war
		 */
		WebAppContext root = new WebAppContext();
		root.setContextPath("/");
		root.setDescriptor(baseDir + "/WEB-INF/web.xml");
		root.setResourceBase(baseDir);

		//FileUtils.deleteDirectory(new File("./work"));
		root.setTempDirectory(new File("./work"));

		// Parent loader priority is a class loader setting that Jetty accepts.
		// By default Jetty will behave like most web containers in that it will
		// allow your application to replace non-server libraries that are part
		// of the
		// container. Setting parent loader priority to true changes this
		// behavior.
		// Read more here:
		// http://wiki.eclipse.org/Jetty/Reference/Jetty_Classloading
		root.setParentLoaderPriority(true);
		root.setDefaultsDescriptor("./etc/webdefault.xml");

		
	
		// Add all contexts together
		ContextHandlerCollection contexts = new ContextHandlerCollection();
		
		contexts.setHandlers(new Handler[] { root });
		server.setHandler(contexts);
		server.setAttribute("keepgenerated", true);
		server.start();
		server.join();
	}
	
	public static void addJMXSupport (Server server) {
		MBeanContainer mbContainer=new MBeanContainer(ManagementFactory.getPlatformMBeanServer());
		server.getContainer().addEventListener(mbContainer);
		server.addBean(mbContainer);
		 
		// Register loggers as MBeans
		mbContainer.addBean(Log.getLog());
	}

}
