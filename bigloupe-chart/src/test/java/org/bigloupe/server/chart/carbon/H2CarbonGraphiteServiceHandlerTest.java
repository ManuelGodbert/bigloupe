package org.bigloupe.server.chart.carbon;

import org.bigloupe.server.chart.AbstractCarbonServiceHandler;
import org.bigloupe.server.chart.CarbonGraphiteServiceHandler;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/applicationContext.xml",
		"classpath:/applicationContext-jpa.xml" })
@ActiveProfiles(profiles={"chart-jpa", "standalone"})
public class H2CarbonGraphiteServiceHandlerTest extends
		AbstractCarbonServiceHandler {

	
}
