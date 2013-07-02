
package org.bigloupe.web.scheduler.job.builtin;

import org.bigloupe.web.scheduler.JobDescriptor;

import com.google.common.collect.ImmutableSet;



public class PythonJob extends LongArgJob {
    
    private static final String PYTHON_BINARY_KEY = "python";
    private static final String SCRIPT_KEY = "script";


    public PythonJob(JobDescriptor desc) {
        super(new String[]{desc.getProps().getString(PYTHON_BINARY_KEY, "python"), 
                           desc.getProps().getString(SCRIPT_KEY)}, 
              desc, 
              ImmutableSet.of(PYTHON_BINARY_KEY, SCRIPT_KEY, JobDescriptor.JOB_TYPE));
    }



    
}
