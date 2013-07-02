package org.bigloupe.web.scheduler.job.builtin;

import org.bigloupe.web.scheduler.JobDescriptor;
import org.bigloupe.web.scheduler.job.Job;
import org.bigloupe.web.util.Props;


/**
 *
 */
public class NoopJob implements Job
{
    public NoopJob(JobDescriptor descriptor)
    {
        
    }

    @Override
    public String getId()
    {
        return "Scheduler!! -- " + getClass().getName();
    }

    @Override
    public void run() throws Exception
    {
    }

    @Override
    public void cancel() throws Exception
    {
    }

    @Override
    public double getProgress() throws Exception
    {
        return 0;
    }

    @Override
    public Props getJobGeneratedProperties()
    {
        return new Props();
    }
}
