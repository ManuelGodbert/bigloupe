package org.bigloupe.web.scheduler.job;

import org.bigloupe.web.util.Props;

/**
 * A Job that just sleeps for the given amount of time
 * 
 * @author jkreps
 * 
 */
public class SleepyJob extends AbstractJob {

    private final long _sleepTimeMs;
    private Props _props =  new Props();

    public SleepyJob(String name, Props p) {
        super(name);
        this._sleepTimeMs = p.getLong("sleep.time.ms", Long.MAX_VALUE);
    }

    public void run() {
        info("Sleeping for " + _sleepTimeMs + " ms.");
        try {
            Thread.sleep(_sleepTimeMs);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public Props getJobGeneratedProperties() {
        return _props;
    }

}
