package org.bigloupe.server.chart;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.jboss.netty.channel.ChannelHandler;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Compatible with StatsD
 * @see <a href="StatsD">https://github.com/etsy/statsd</a>
 * 
 * @author 
 *
 */
public class StatsDServiceHandler extends AbstractChannelUpstreamHandler
		implements ChannelHandler {
	
	private static Logger logger = LoggerFactory.getLogger(StatsDServiceHandler.class);
	
	public static final String COUNTER_METRIC_TYPE = "c";
	public static final String GAUGE_METRIC_TYPE = "g";
	public static final String HISTOGRAM_METRIC_TYPE = "h";
	public static final String METER_METRIC_TYPE = "m";
	public static final String TIMER_METRIC_TYPE = "ms";
	
	Pattern metricMatcher = Pattern.compile("([^:]+)(:((-?\\d+|delete)?(\\|((\\w+)(\\|@(\\d+\\.\\d+))?)?)?)?)?");
	
	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent msg)
			throws Exception {
			String message = (String)msg.getMessage();
			logger.trace("Received message: " + message);
			
			String[] messageSplitted =  StringUtils.split(message, "\n");
			for (String msgPart : messageSplitted) {
				Matcher matcher = metricMatcher.matcher(msgPart);
				//String label = matcher.find();
			}
			

			
			
	}

}

