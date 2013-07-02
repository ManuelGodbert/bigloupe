package org.bigloupe.server.chart.carbon;

import java.util.concurrent.CountDownLatch;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CarbonGraphiteClientHandler extends SimpleChannelUpstreamHandler {
	
	public volatile CountDownLatch latch;
	
	private static Logger logger = LoggerFactory
			.getLogger(CarbonGraphiteClientHandler.class);


	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent msg)
			throws Exception {
		String message = (String) msg.getMessage();
		logger.trace("--- start message -----");
		logger.trace(message);
		logger.trace("--- end message -----");
		latch.countDown();
	}
	
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) {
        e.getCause().printStackTrace();
        e.getChannel().close();
    }
}
