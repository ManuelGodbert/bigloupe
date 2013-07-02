package org.bigloupe.server.chart.carbon;

import java.net.SocketAddress;

import org.apache.commons.lang.StringUtils;
import org.bigloupe.server.chart.AbstractCarbonServiceHandler;
import org.bigloupe.server.chart.CarbonGraphiteServiceHandler;
import org.bigloupe.web.chart.model.Series;
import org.bigloupe.web.chart.service.impl.DatabaseChartServiceImpl;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.MessageEvent;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Test backend MongoDB
 * 
 * @author bigloupe
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/applicationContext.xml",
		"classpath:/applicationContext-mongodb.xml" })
@ActiveProfiles(profiles={"chart-mongodb", "standalone"})
public class MongoCarbonGraphiteServiceHandlerTest extends
		AbstractCarbonServiceHandler {

	@Before 
	public void setUp() throws Exception {
		cleanDatabase();
		super.setUp();
	}

}
