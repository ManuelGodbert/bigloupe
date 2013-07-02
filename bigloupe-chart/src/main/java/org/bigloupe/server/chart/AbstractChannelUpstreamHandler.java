package org.bigloupe.server.chart;

import org.jboss.netty.channel.SimpleChannelUpstreamHandler;

import com.yammer.metrics.Metrics;
import com.yammer.metrics.core.Counter;

public abstract class AbstractChannelUpstreamHandler extends SimpleChannelUpstreamHandler {

	final Counter nbMessages = Metrics.newCounter(NumberOfMessages.class, "number of messages");
	
	public class NumberOfMessages {
		int nbMessage;
	}
	
}
