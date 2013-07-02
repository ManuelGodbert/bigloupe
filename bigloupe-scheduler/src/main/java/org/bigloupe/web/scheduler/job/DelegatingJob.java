package org.bigloupe.web.scheduler.job;

import org.bigloupe.web.util.Props;

public abstract class DelegatingJob implements Job {

    private final Job _innerJob;

    public DelegatingJob(Job innerJob) {
        _innerJob = innerJob;
    }

    public Job getInnerJob() {
        return _innerJob;
    }

    public void cancel() throws Exception {
        _innerJob.cancel();
    }

    public String getId() {
        return _innerJob.getId();
    }

    public double getProgress() throws Exception {
        return _innerJob.getProgress();
    }

    @Override
    public Props getJobGeneratedProperties()
    {
        return _innerJob.getJobGeneratedProperties();
    }

    public abstract void run() throws Exception;

    @Override
    public String toString()
    {
        return getClass().getSimpleName() + "{" +
               "_innerJob=" + _innerJob +
               '}';
    }
}
