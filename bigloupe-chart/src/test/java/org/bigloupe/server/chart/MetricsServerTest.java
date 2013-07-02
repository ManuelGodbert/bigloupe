package org.bigloupe.server.chart;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.bigloupe.server.chart.carbon.CarbonGraphiteClientHandler;
import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.jboss.netty.handler.codec.string.StringEncoder;
import org.jboss.netty.util.CharsetUtil;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Test TCP & UDP interfaces
 * 
 * @author bigloupe
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/applicationContext.xml",
		"classpath:/applicationContext-jpa.xml" })
@ActiveProfiles(profiles={"chart-jpa", "standalone"})
public class MetricsServerTest extends AbstractCarbonServiceHandler {

	static Channel channel;

	@BeforeClass
	public static void beforeClass() throws Exception {
		init();
	}

	@AfterClass
	public static void afterClass() throws Exception {
		channel.close();
	}
	
	public static void init() throws Exception {
		NioClientSocketChannelFactory nioServerSocketChannelFactory = new NioClientSocketChannelFactory(
				Executors.newCachedThreadPool(),
				Executors.newCachedThreadPool());
		
		final CarbonGraphiteClientHandler clientHandler = new CarbonGraphiteClientHandler();
		ClientBootstrap bootstrap = new ClientBootstrap(
				nioServerSocketChannelFactory);
		bootstrap.setPipelineFactory(new ChannelPipelineFactory() {

			@Override
			public ChannelPipeline getPipeline() throws Exception {
				return Channels.pipeline(new StringEncoder(
						CharsetUtil.UTF_8), clientHandler);
			}
		});

		bootstrap.setOption("writeBufferHighWaterMark", 10 * 64 * 1024);
		final InetSocketAddress address = new InetSocketAddress("localhost",
				2003);
		ChannelFuture future = bootstrap.connect(address);
		channel = future.awaitUninterruptibly().getChannel();

	}

	@Test
	public void testListenerCarbonGraphite() throws Exception {
		for (final String msg : msgs) {
			channel.write(msg);
		}

	}

}
