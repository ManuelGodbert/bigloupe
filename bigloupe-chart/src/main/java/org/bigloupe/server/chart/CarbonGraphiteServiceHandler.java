package org.bigloupe.server.chart;

import org.apache.commons.lang.StringUtils;
import org.bigloupe.web.chart.service.ChartService;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandler;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

/**
 * Handler compatible with Carbon Graphite Only Plaintext mode is supported
 * 
 * 
 * @see <a href="http://graphite.wikidot.com/carbon">Carbon - Backend of
 *      Graphite</a>
 * 
 *      Client applications can send lines of text of the following format:
 * 
 *      my.metric.name value unix_timestamp
 * 
 *      For example:
 * 
 *      performance.servers.www01.cpuUsage 42.5 1208815315
 * 
 *      - The metric name is like a filesystem path that uses a dot as a
 *      separator instead of a forward-slash.
 * 
 *      - The value is some scalar integer or floating point value
 * 
 *      - The unix_timestamp is unix epoch time, as an integer.
 * 
 *      Each line like this corresponds to one data point for one metric.
 */
public class CarbonGraphiteServiceHandler extends
		AbstractChannelUpstreamHandler implements ChannelHandler {

	private static Logger logger = LoggerFactory
			.getLogger(CarbonGraphiteServiceHandler.class);

	ChartService chartService;

	public CarbonGraphiteServiceHandler(ChartService chartService) {
		Assert.notNull(chartService, "ChartService must not be null");
		this.chartService = chartService;

	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e)
			throws Exception {
		e.getCause().printStackTrace();
		Channel ch = e.getChannel();
		ch.close();
	}

	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent msg)
			throws Exception {
		String message = (String) msg.getMessage();
		if (logger.isDebugEnabled()) {
			logger.debug("--- start message -----");
			logger.debug(message);
			logger.debug("--- end message -----");
		}
		String[] msgSplitted = StringUtils.split(message);
		if (msgSplitted.length < 3) {
			logger.trace("Message '" + message + "' malformed");
			return;
		}
		String key = msgSplitted[0];
		// the milliseconds since January 1, 1970, 00:00:00 GMT.
		long date = Long.parseLong(msgSplitted[2]) * 1000;
		double value = Double.parseDouble(msgSplitted[1]);

		chartService.addTimeSeriesDataItem(key, date, value);

		nbMessages.inc();

	}

}
