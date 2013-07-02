package org.bigloupe.web.monitor.pig;

import java.io.IOException;

import org.apache.pig.tools.pigstats.PigStatsUtil;
import org.bigloupe.web.monitor.server.ScriptStatusServer;
import org.bigloupe.web.monitor.service.impl.InMemoryStatsService;
import org.bigloupe.web.monitor.service.impl.RemoteStatsWriteService;


/**
 * Sublclass of AmbrosePigProgressNotificationListener that send information to bigloupe webserver.
 * <P>
 * To use this class with pig, start pig as follows:
 * <pre>
 * $ bin/pig \
 *  -Dpig.notification.listener=org.bigloupe.web.monitor.pig.BigLoupePigProgressNotificationListener \
 *  -Dbigloupe.http.server=http://yourbigloupe
 *  -f path/to/script.pig
 * </pre>
 * </P>
 * @author billg
 */
public class BigLoupePigProgressNotificationListener
             extends AmbrosePigProgressNotificationListener {

  private RemoteStatsWriteService service;

  private static final String POST_SCRIPT_SLEEP_SECS_PARAM = "ambrose.post.script.sleep.seconds";
  private static final String BIGLOUPE_SERVER = "bigloupe.http.server";

  public BigLoupePigProgressNotificationListener() {
    super(new RemoteStatsWriteService(System.getProperty(BIGLOUPE_SERVER, "http://localhost:9090")));
    this.service = (RemoteStatsWriteService)getStatsWriteService();

  }

  @Override
  public void launchCompletedNotification(String scriptId, int numJobsSucceeded) {
    super.launchCompletedNotification(scriptId, numJobsSucceeded);

    // sleeping keeps the app server running for a period after the job is done. if no sleep time
    // is set, still sleep for 10 seconds just to let the client finish it's polling, since it
    // doesn't stop until it get all the job complete events.
    String sleepTime = System.getProperty(POST_SCRIPT_SLEEP_SECS_PARAM, "10");

    try {
      int sleepTimeSeconds = Integer.parseInt(sleepTime);
      // if sleep time is long, display stats so users watching std out can tell things are done.
      // if sleep time is short though, don't bother, since they'll get displayed by Pig after the
      // sleep.
      if (sleepTimeSeconds > 10) {
        PigStatsUtil.displayStatistics();
      }

      log.info("Job complete but sleeping for " + sleepTimeSeconds
        + " seconds to send information to BigLoupe WebServer");
      Thread.sleep(sleepTimeSeconds * 1000);

    } catch (NumberFormatException e) {
      log.warn(POST_SCRIPT_SLEEP_SECS_PARAM + " param is not a valid number, not sleeping: " + sleepTime);
    } catch (InterruptedException e) {
      log.warn("Sleep interrupted", e);
    }
  }

}
