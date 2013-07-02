package org.bigloupe.web.scheduler.jobcontrol.impl.jobs;

import org.apache.log4j.Logger;
import org.bigloupe.web.scheduler.job.DelegatingJob;
import org.bigloupe.web.scheduler.job.Job;
import org.bigloupe.web.scheduler.jobcontrol.impl.jobs.locks.JobLock;


/**
 * A wrapper that blocks the given job until the required number of permits
 * become available
 *
 * @author jkreps
 *
 */
public class ResourceThrottledJob extends DelegatingJob {

    private final JobLock _jobLock;
    private final Logger _logger;

    private final Object lock = new Object();
    private volatile boolean canceled = false;

    public ResourceThrottledJob(Job job, JobLock lock) {
        super(job);
        _jobLock = lock;
        this._logger = Logger.getLogger(job.getId());
    }

    /**
     * Wrapper that acquires needed permits, runs job, and then releases permits
     */
    @Override
    public void run() throws Exception
    {
        long start = System.currentTimeMillis();
        _logger.info("Attempting to acquire " + _jobLock + " at time " + start);
        try {
            _jobLock.acquireLock();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return;
        }
        long totalWait = System.currentTimeMillis() - start;
        _logger.info(_jobLock + " Time: " + totalWait + " ms.");
        try {
            boolean shouldRunJob;
            synchronized(lock) {
                shouldRunJob = ! canceled;
            }
            if(shouldRunJob) {
                getInnerJob().run();
            }
            else {
                _logger.info("Job was canceled while waiting for lock.  Not running.");
            }
        } finally {
            _jobLock.releaseLock();
        }
    }

    @Override
    public void cancel() throws Exception
    {
        synchronized (lock) {
            canceled = true;

            super.cancel();
        }
    }
}
