package org.bigloupe.launcher;

import static org.fusesource.jansi.Ansi.ansi;
import static org.fusesource.jansi.Ansi.Color.GREEN;
import static org.fusesource.jansi.Ansi.Color.RED;
import static org.fusesource.jansi.Ansi.Color.WHITE;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.ProtectionDomain;
import java.util.concurrent.CountDownLatch;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.apache.commons.io.FileUtils;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.webapp.WebAppContext;
import org.fusesource.jansi.AnsiConsole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * This class launches the web application in an embedded Jetty container. This
 * is the entry point to your application. The Java command that is used for
 * launching should fire this main method.
 * 
 * @author bigloupe
 */
public class Main {

	private static Logger logger = LoggerFactory.getLogger(Main.class);

	private static final String OPTION_DEPLOY = "deploy";
	private static final String OPTION_WEBSERVER_PORT = "port";
	
	private String baseDir;
	private CommandLine cmd;

    private static volatile Thread keepAliveThread;
    private static volatile CountDownLatch keepAliveLatch;

	public Main(String[] args) throws ParseException {
		commandLineParser(args);
	}

	/**
	 * Thread to clean properly
	 * 
	 * @author bigloupe
	 * 
	 */
	private static class BigLoupeCleaner implements Runnable {

		Server server;

		private BigLoupeCleaner(Server server) {
			this.server = server;
		}

		public void run() {
			try {
				System.out
						.println(ansi().fg(RED).bold()
								.a("Stopping BigLoupe Jetty container").boldOff()
								.fg(WHITE));
				server.stop();
			} catch (Exception e) {
				System.out.println(ansi().fg(RED).a(
						"Can't stop properly Jetty Web Server"));
			} finally {
				AnsiConsole.systemUninstall();
			}
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		Main main = new Main(args);
		try {
			main.prepare();
			
			main.startJetty();
		} catch (Exception e) {
			System.out.println("Can't start Big Loupe");
			e.printStackTrace();
		}
	}

	/**
	 * Prepare BigLoupe
	 */
	private void prepare() throws Exception {
		AnsiConsole.systemInstall();
		String version = ((Main.class.getPackage().getImplementationVersion() != null) ? Main.class
				.getPackage().getImplementationVersion()
				: "'undefined'");
		System.out
				.println(ansi()
						.fg(RED)
						.render("@|bold Starting BigLoupe version |@")
						.fg(GREEN)
						.a(version).fg(WHITE));
		
		keepAliveLatch = new CountDownLatch(1);
        // keep this thread alive (non daemon thread) until we shutdown
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                keepAliveLatch.countDown();
            }
        });

        keepAliveThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    keepAliveLatch.await();
                } catch (InterruptedException e) {
                    // bail out
                }
            }
        }, "bigloupe[keepAlive/" + version + "]");
        keepAliveThread.setDaemon(false);
        keepAliveThread.start();
        
        
		ProtectionDomain protectionDomain = Main.class.getProtectionDomain();

		baseDir = new File(protectionDomain.getCodeSource().getLocation()
				.getPath()).getParent();
		
		// Add conf directory to the classpath
		// Chain the current thread classloader
		URLClassLoader urlClassLoader = new URLClassLoader(
				new URL[] { new URL("file:"  + new File(baseDir, "conf").getAbsolutePath() + "/" ) },
				Thread.currentThread().getContextClassLoader());
		// Replace the thread classloader - assumes
		// you have permissions to do so
		Thread.currentThread().setContextClassLoader(urlClassLoader);

		
	}

	/**
	 * Command line parser
	 * @param args
	 * @throws ParseException
	 */
	private void commandLineParser(String[] args) throws ParseException {
		Options options = new Options();
		options.addOption("help", false, "print this message" );
		options.addOption(OPTION_DEPLOY, false, "force to (re)-deploy bigloupe webarchive in work directory");
		options.addOption(OPTION_WEBSERVER_PORT, true, "specify webserver port - (9090 by default)");
		CommandLineParser parser = new PosixParser();
		cmd = parser.parse(options, args);

		if (cmd.hasOption("help")) 
		{
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("java -jar bigloupe.jar", options );
			System.exit(0);
		}
	}

	/**
	 * Start Jetty web server
	 * 
	 * @throws IOException
	 */
	private void startJetty() throws Exception {

		// The port that we should run on can be set into an environment
		// variable
		// Look for that variable and default to 9090 if it isn't there.
		String webPort = cmd.getOptionValue(OPTION_WEBSERVER_PORT);
		if (webPort == null || webPort.isEmpty()) {
			webPort = "9090";
		}

		Server server = new Server(Integer.valueOf(webPort));

		WebAppContext root = new WebAppContext();
		root.setContextPath("/");

		// Test if war already deployed in work directory
		File workDir = new File(baseDir, "work");

		// Get the war-file or war-folder
		String war;
		if (workDir.exists() && !cmd.hasOption(OPTION_DEPLOY)) {
			war = new File(baseDir, "work"
					+ System.getProperty("file.separator") + "webapp")
					.getAbsolutePath();
		} else {
			FileUtils.deleteDirectory(workDir);
			war = baseDir + System.getProperty("file.separator") + "web"
					+ System.getProperty("file.separator")
					+ "bigloupe-war-1.0.war";

		}
		root.setTempDirectory(workDir);
		
		root.setWar(war);

		System.out.println(ansi()
				.render("@|bold Use webarchive bigloupe in : |@").a(war));

		// Parent loader priority is a class loader setting that Jetty accepts.
		// By default Jetty will behave like most web containers in that it will
		// allow your application to replace non-server libraries that are part
		// of the
		// container. Setting parent loader priority to true changes this
		// behavior.
		// Read more here:
		// http://wiki.eclipse.org/Jetty/Reference/Jetty_Classloading
		root.setParentLoaderPriority(true);

		ContextHandlerCollection contexts = new ContextHandlerCollection();
		contexts.setHandlers(new Handler[] { root });
		server.setHandler(contexts);

		Runtime.getRuntime().addShutdownHook(new Thread(new BigLoupeCleaner(server)));
		server.start();

		System.out.println(ansi()
				.render("@|bold BigLoupe available in your browser - |@").a("http://localhost:").a(webPort));

	}

}
