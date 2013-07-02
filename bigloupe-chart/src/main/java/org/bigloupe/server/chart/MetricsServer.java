package org.bigloupe.server.chart;

import static org.fusesource.jansi.Ansi.ansi;
import static org.fusesource.jansi.Ansi.Color.GREEN;
import static org.fusesource.jansi.Ansi.Color.RED;
import static org.fusesource.jansi.Ansi.Color.WHITE;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.bigloupe.web.chart.service.ChartService;
import org.eclipse.jetty.jmx.MBeanContainer;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.resource.FileResource;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.webapp.WebAppContext;
import org.fusesource.jansi.AnsiConsole;
import org.jboss.netty.bootstrap.ConnectionlessBootstrap;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.FixedReceiveBufferSizePredictorFactory;
import org.jboss.netty.channel.socket.nio.NioDatagramChannelFactory;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.jboss.netty.handler.codec.frame.DelimiterBasedFrameDecoder;
import org.jboss.netty.handler.codec.frame.Delimiters;
import org.jboss.netty.handler.codec.string.StringDecoder;
import org.jboss.netty.handler.codec.string.StringEncoder;
import org.jboss.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Main class
 * 
 * @author bigloupe
 * 
 */
public class MetricsServer {

	public final int DEFAULT_PORT_CARBON = 2003;
	public final int DEFAULT_PORT_STATSD = 2004;
	public final int DEFAULT_PORT_JMX_MBEAN_SERVER = 2005;
	public final int DEFAULT_PORT_WEBSERVER = 2006;

	/**
	 * The minimum server currentMinPort number. Set at 1100 to avoid returning
	 * privileged currentMinPort numbers.
	 */
	public static final int MIN_PORT_NUMBER = 1100;

	/**
	 * The maximum server currentMinPort number.
	 */
	public static final int MAX_PORT_NUMBER = 49151;

	public static final String OPTION_BACKEND = "backend";
	private static final String OPTION_STATSD_PORT = "statsd_port";
	private static final String OPTION_CARBON_PORT = "carbon_port";
	private static final String OPTION_WEBSERVER_PORT = "http_port";
	private static final String OPTION_CARBON_ONLY = "carbon_only";
	private static final String OPTION_STATSD_ONLY = "statsd_only";
	private static final String OPTION_NO_WEBSERVER = "no_http";
	private static final String OPTION_WEBSERVER_WEBROOT = "http_webroot";
	private static final String OPTION_HAWTIO = "hawtio";

	private Integer portCarbon;
	private Integer portStatsD;
	private Integer portWebServer;

	// Application context initialized either DispatcherServlet (no Webserver)
	// either manually in function applyOptionWithNoWebServer
	private ClassPathXmlApplicationContext applicationContext;

	@Autowired
	public ChartService chartService;

	private CommandLine cmd;
	private Options options;

	private List<Channel> channels = new ArrayList<Channel>();

	private static volatile Thread keepAliveThread;
	private static volatile CountDownLatch keepAliveLatch;

	private static Logger logger;

	// For current directory
	static {
		String bigloupeLogDir = System.getProperty("bigloupe.log.dir");
		if (bigloupeLogDir != null) {
			File bigloupeLogDirFile = new File(bigloupeLogDir);
			if (!bigloupeLogDirFile.exists() || !bigloupeLogDirFile.canWrite()) {
				System.setProperty("bigloupe.log.dir", ".");
			} else
				System.out
						.println("Use -Dbigloupe.log.dir=${logdir} to force a writable log directory");
		} else
			System.setProperty("bigloupe.log.dir", ".");
		logger = LoggerFactory.getLogger(MetricsServer.class);
	}

	/**
	 * Parse arguments
	 * 
	 * @param args
	 */
	private void commandLineParser(String[] args) throws ParseException {
		options = new Options();
		options.addOption("help", false, "print this message");
		options.addOption(OPTION_CARBON_PORT, true,
				"specify port for graphite/carbon events - ("
						+ DEFAULT_PORT_CARBON + " by default)");
		options.addOption(OPTION_STATSD_PORT, true,
				"specify port for statsD events - (" + DEFAULT_PORT_STATSD
						+ " by default)");
		options.addOption(OPTION_WEBSERVER_PORT, true,
				"specify port for http server - (" + DEFAULT_PORT_WEBSERVER
						+ " by default)");
		options.addOption(OPTION_CARBON_ONLY, false,
				"start TCP socket only for graphite/carbon events");
		options.addOption(OPTION_STATSD_ONLY, false,
				"start UDP socket only for statsd events");
		options.addOption(OPTION_NO_WEBSERVER, false, "don't start webserver");
		options.addOption(OPTION_HAWTIO, false, "start hawtio in webserver");

		options.addOption(OPTION_BACKEND, true,
				"backend implementation available : " + getBackends());
		options.addOption(OPTION_WEBSERVER_WEBROOT, true,
				"HTTP base resource (must be a path)");

		CommandLineParser parser = new PosixParser();
		cmd = parser.parse(options, args);

		if (cmd.hasOption("help")) {
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("java -jar bigloupe-chart.jar [options]",
					options);
			System.exit(0);
		}

	}

	private String getBackends() {
		String backends = "";
		int i = 0;
		for (Backend backend : Backend.values()) {
			backends += backend.toString();
			i++;
			if (i < Backend.values().length) backends += ", ";
		}
		return backends;
	}

	// Common option : standalone with webserver and without webserver
	private void applyCommonOption() {
		if (cmd.hasOption(OPTION_CARBON_PORT))
			setPortCarbon(Integer.parseInt(cmd
					.getOptionValue(OPTION_CARBON_PORT)));

		if (cmd.hasOption(OPTION_STATSD_PORT))
			setPortStatsD(Integer.parseInt(cmd
					.getOptionValue(OPTION_STATSD_PORT)));

		if (cmd.hasOption(OPTION_WEBSERVER_PORT))
			setPortWebServer(Integer.parseInt(cmd
					.getOptionValue(OPTION_WEBSERVER_PORT)));

		if (!cmd.hasOption(OPTION_STATSD_ONLY)
				&& !isPortAvailable(getPortCarbon())) {
			logger.info("Port "
					+ getPortCarbon()
					+ " for carbon TCP listener is not available. Use --carbon_port ${port} to specify an another port");
			System.exit(-1);
		}
		if (!cmd.hasOption(OPTION_CARBON_ONLY)
				&& !isPortAvailable(getPortStatsD())) {
			logger.info("Port "
					+ getPortStatsD()
					+ " for statsD UDP listener is not available. Use --statsd_port ${port} to specify an another port");
			System.exit(-1);
		}
		if (cmd.hasOption(OPTION_NO_WEBSERVER)
				&& !isPortAvailable(getPortStatsD())) {
			logger.info("Port "
					+ getPortStatsD()
					+ " for webserver is not available. Use --http_port ${port} to specify an another port");
			System.exit(-1);
		}

	}

	// Option : standalone without webserver
	private void applyOptionWithoutWebServer() {
		// ApplicationContext initiliazed by DispatcherServlet otherwise
		applicationContext = new ClassPathXmlApplicationContext();
		if (cmd.hasOption(OPTION_BACKEND)) {
			if (cmd.getOptionValue(OPTION_BACKEND).equalsIgnoreCase(Backend.MONGODB.toString()))
				applicationContext.getEnvironment().setActiveProfiles(
						"standalone", "chart-mongodb");
			else if (cmd.getOptionValue(OPTION_BACKEND).equalsIgnoreCase(Backend.RRD.toString()))
				applicationContext.getEnvironment().setActiveProfiles(
						"standalone", "chart-rrd");
			else {
				logger.info("Unknown backend "
						+ cmd.getOptionValue(OPTION_BACKEND)
						+ ". You can choose between " + getBackends() + ".");
				System.exit(-1);
			}

		} else
			applicationContext.getEnvironment().setActiveProfiles("standalone",
					"chart-jpa");
		// Init Spring controller
		applicationContext.setConfigLocations(new String[] {
				"classpath:applicationContext.xml",
				"classpath:applicationContext-jmx.xml",
				"classpath:applicationContext-jmx-standalone.xml",
				"classpath:applicationContext-jpa.xml",
				"classpath:applicationContext-mongodb.xml",
				"classpath:applicationContext-rrd.xml" });
		((ConfigurableApplicationContext) applicationContext)
				.registerShutdownHook();

		applicationContext.refresh();
	}

	// Option : standalone with webserver. Active profiles
	private void applyOptionWithWebServer() {
		String springProfiles = "standalone";
		if (cmd.hasOption(OPTION_BACKEND)) {
			if (cmd.getOptionValue(OPTION_BACKEND).equalsIgnoreCase(Backend.MONGODB.toString()))
				springProfiles += ",chart-mongodb";
			else if (cmd.getOptionValue(OPTION_BACKEND).equalsIgnoreCase(Backend.RRD.toString()))
				springProfiles += ",chart-rrd";
			else {
				logger.info("Unknown backend "
						+ cmd.getOptionValue(OPTION_BACKEND)
						+ ". You can choose between " + getBackends() + ".");
				System.exit(-1);
			}
		} else {
			springProfiles += ",chart-jpa";
		}

		System.setProperty("spring.profiles.active", springProfiles);
	}

	/**
	 * Start TCP socket for Graphite Carbon events
	 */
	public void startListenerCarbonGraphite() {
		NioServerSocketChannelFactory nioServerSocketChannelFactory = new NioServerSocketChannelFactory(
				Executors.newCachedThreadPool(),
				Executors.newCachedThreadPool());
		ServerBootstrap bootstrap = new ServerBootstrap(
				nioServerSocketChannelFactory);

		// Configure the pipeline factory.
		bootstrap.setPipelineFactory(new ChannelPipelineFactory() {

			@Override
			public ChannelPipeline getPipeline() throws Exception {
				return Channels.pipeline(new DelimiterBasedFrameDecoder(80960,
						Delimiters.lineDelimiter()), new StringDecoder(
						CharsetUtil.UTF_8), new CarbonGraphiteServiceHandler(
						chartService));
			}
		});

		// bootstrap.setOption("child.sendBufferSize", 1048576);
		// bootstrap.setOption("child.receiveBufferSize", 1048576);
		// bootstrap.setOption("child.tcpNoDelay", true);
		// bootstrap.setOption("child.writeBufferLowWaterMark", 32 * 1024);
		// bootstrap.setOption("child.writeBufferHighWaterMark", 64 * 1024);

		logger.info("Listening Carbon/Graphite events on port "
				+ getPortCarbon());
		Channel channel = bootstrap
				.bind(new InetSocketAddress(getPortCarbon()));
		channels.add(channel);

	}

	/**
	 * Start UDP socket for StatsD events
	 */
	public void startListenerStatsD() {
		NioDatagramChannelFactory nioDatagramChannelFactory = new NioDatagramChannelFactory(
				Executors.newCachedThreadPool());
		ConnectionlessBootstrap bootstrap = new ConnectionlessBootstrap(
				nioDatagramChannelFactory);

		// Configure the pipeline factory.
		bootstrap.setPipelineFactory(new ChannelPipelineFactory() {

			@Override
			public ChannelPipeline getPipeline() throws Exception {
				return Channels.pipeline(new StringEncoder(CharsetUtil.UTF_8),
						new StringDecoder(CharsetUtil.UTF_8),
						new StatsDServiceHandler());
			}
		});
		bootstrap.setOption("broadcast", "false");

		bootstrap.setOption("receiveBufferSizePredictorFactory",
				new FixedReceiveBufferSizePredictorFactory(1024));

		logger.info("Listening StatsD events on port " + getPortStatsD());
		Channel channel = bootstrap
				.bind(new InetSocketAddress(getPortStatsD()));
		channels.add(channel);

	}

	/**
	 * Stop all sockets
	 */
	@PreDestroy
	public void stopListener() {
		for (Channel channel : channels) {
			logger.info("Stop socket on port " + channel.getLocalAddress());
			channel.unbind();
		}

	}

	/**
	 * Start all sockets
	 */
	private void startListener() {
		if (!cmd.hasOption(OPTION_STATSD_ONLY))
			startListenerCarbonGraphite();
		if (!cmd.hasOption(OPTION_CARBON_ONLY))
			startListenerStatsD();
	}

	/**
	 * Start Jetty web server
	 * 
	 * @param version
	 *            of bigloupe-chart
	 * @return
	 * @throws Exception
	 */
	private Server startWebServer(String version) throws Exception {
		if (!cmd.hasOption(OPTION_NO_WEBSERVER)) {

			applyOptionWithWebServer();

			Server server = new Server(getPortWebServer());

			WebAppContext root = new WebAppContext();
			root.setContextPath("/");

			if (cmd.hasOption(OPTION_WEBSERVER_WEBROOT)) {
				String webRoot = cmd.getOptionValue(OPTION_WEBSERVER_WEBROOT);
				Resource resource = FileResource.newResource(webRoot);
				root.setBaseResource(resource);

			} else {
				String webFiles = "bigloupe-chart-" + version + "-webapp.war";
				File fileWebApp = new File(webFiles);
				if (!fileWebApp.exists()) {
					if (version.equals("'undefined'")) {
						Resource resource = FileResource
								.newResource("src/main/webapp");
						root.setBaseResource(resource);
						root.setDefaultsDescriptor("./etc/webdefault.xml");
						logger.info("Embedded webServer started with base resource "
								+ resource.getFile().getAbsolutePath());
					} else {
						logger.info(webFiles + " file not available");
						logger.info("Embedded webServer will be not started");
						return null;
					}
				} else {
					root.setWar(fileWebApp.getAbsolutePath());
				}
			}

			File tmp = new File("tmp");
			if (!tmp.exists())
				tmp.mkdir();
			root.setTempDirectory(tmp);

			ContextHandlerCollection contexts = new ContextHandlerCollection();
			Handler handlerHawtIO = addWebApplicationHawtIO();
			if (handlerHawtIO != null)
				contexts.setHandlers(new Handler[] { root, handlerHawtIO });
			else
				contexts.setHandlers(new Handler[] { root });
			server.setHandler(contexts);

			server.start();
			addWebServerJMXSupport(server);
			return server;
		} else {
			applyOptionWithoutWebServer();
			return null;
		}
	}

	private Handler addWebApplicationHawtIO() throws Exception {
		if (cmd.hasOption(OPTION_HAWTIO)) {
			WebAppContext webapp = new WebAppContext();
			webapp.setContextPath("/hawtio");
			File hawtioWeb = new File("hawtio-web-1.0.war");
			if (!hawtioWeb.exists()) {
				logger.info("Web archive file 'hawtio-web-1.0.war' not available in "
						+ hawtioWeb.getAbsolutePath());
				return null;
			}
			webapp.setWar("hawtio-web-1.0.war");
			webapp.setParentLoaderPriority(true);
			return webapp;
		}
		return null;

	}

	private void addWebServerJMXSupport(Server server) {
		MBeanContainer mbContainer = new MBeanContainer(
				ManagementFactory.getPlatformMBeanServer());
		server.getContainer().addEventListener(mbContainer);
		server.addBean(mbContainer);

		// Register loggers as MBeans
		mbContainer.addBean(Log.getLog());
	}

	public Integer getPortCarbon() {
		if (portCarbon == null)
			this.portCarbon = DEFAULT_PORT_CARBON;
		return portCarbon;
	}

	public void setPortCarbon(Integer port) {
		if (port == null)
			portCarbon = DEFAULT_PORT_CARBON;
		else
			portCarbon = port;
	}

	public Integer getPortStatsD() {
		if (portStatsD == null)
			this.portStatsD = DEFAULT_PORT_STATSD;
		return portStatsD;
	}

	public void setPortStatsD(Integer port) {
		if (port == null)
			this.portStatsD = DEFAULT_PORT_STATSD;
		else
			this.portStatsD = port;
	}

	public Integer getPortWebServer() {
		if (portWebServer == null)
			this.portWebServer = DEFAULT_PORT_WEBSERVER;
		return portWebServer;
	}

	private void setPortWebServer(Integer port) {
		if (port == null)
			this.portWebServer = DEFAULT_PORT_WEBSERVER;
		else
			this.portWebServer = port;
	}

	/**
	 * Initialization with Spring
	 * 
	 * @throws Exception
	 */
	@PostConstruct
	public void initialize() throws Exception {
		try {
			// Parse empty command line if MetricsServer has been launched
			// inside an another application (e.g. bigloupe web) without main
			if (cmd == null)
				commandLineParser(new String[] {});
		} catch (ParseException e) {
			e.printStackTrace();
		}
		startListener();
	}

	/**
	 * Thread to clean properly
	 * 
	 * @author bigloupe
	 * 
	 */
	private class MetricsServerCleaner implements Runnable {

		Server server;

		public MetricsServerCleaner(Server server) {
			this.server = server;
		}

		public void run() {
			try {
				System.out.println(ansi().fg(RED).bold()
						.a("Stopping BigLoupe Metrics server").boldOff()
						.fg(WHITE));
				// metricsServer.stopListener() called by PreDestro annotation
				if (server != null)
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
	 * Called only in standalone mode
	 * 
	 * @param args
	 */
	private void startMetricsServer(String[] args) {
		AnsiConsole.systemInstall();
		String version = ((MetricsServer.class.getPackage()
				.getImplementationVersion() != null) ? MetricsServer.class
				.getPackage().getImplementationVersion() : "'undefined'");
		System.out.println(ansi().fg(RED)
				.render("@|bold Starting BigLoupe Metrics server version |@")
				.fg(GREEN).a(version).fg(WHITE));
		logger.info("Starting BigLoupe Metrics server version " + version);

		try {
			commandLineParser(args);
			applyCommonOption();

			Server server = startWebServer(version);

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
			}, "BigLoupe metrics-server[keepAlive/" + version + "]");
			keepAliveThread.setDaemon(false);
			keepAliveThread.start();

			Runtime.getRuntime().addShutdownHook(
					new Thread(new MetricsServerCleaner(server)));
		} catch (ParseException e) {
			System.out.println(e.getMessage());
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("java -jar bigloupe-chart.jar [options]",
					options);
			System.exit(-1);
		} catch (Exception e) {
			throw new IllegalArgumentException(e);
		}
	}

	/**
	 * Checks to see if a specific port is available. From Apache Camel
	 * 
	 * @param port
	 *            the port to check for availability
	 */
	public static boolean isPortAvailable(int port) {
		if (port < MIN_PORT_NUMBER || port > MAX_PORT_NUMBER) {
			throw new IllegalArgumentException("Invalid start port: " + port);
		}

		ServerSocket ss = null;
		DatagramSocket ds = null;
		try {
			ss = new ServerSocket(port);
			ss.setReuseAddress(true);
			ds = new DatagramSocket(port);
			ds.setReuseAddress(true);
			return true;
		} catch (IOException e) {
		} finally {
			if (ds != null) {
				ds.close();
			}

			if (ss != null) {
				try {
					ss.close();
				} catch (IOException e) {
					/* should not be thrown */
				}
			}
		}

		return false;
	}

	public static void main(String[] args) {
		MetricsServer metricsServer = new MetricsServer();
		metricsServer.startMetricsServer(args);

	}

}
