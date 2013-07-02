package org.bigloupe.web.scheduler.job;

/**
 * Thrown to indicate the job has failed
 * 
 * @author jkreps
 * 
 */
public class JobFailedException extends RuntimeException {

    private static final long serialVersionUID = 1;

    public JobFailedException() {
        super();
    }

    public JobFailedException(String message, Throwable cause) {
        super(message, cause);
    }

    public JobFailedException(String message) {
        super(message);
    }

    public JobFailedException(Throwable cause) {
        super(cause);
    }

}
