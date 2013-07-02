package org.bigloupe.web.scheduler.job.builtin;

import java.io.File;
import java.util.List;
import java.util.StringTokenizer;

import org.bigloupe.web.scheduler.JobDescriptor;


public class JavaJob extends JavaProcessJob {

	public static final String RUN_METHOD_PARAM = "method.run";
	public static final String CANCEL_METHOD_PARAM = "method.cancel";
	public static final String PROGRESS_METHOD_PARAM = "method.progress";

	public static final String JOB_CLASS = "job.class";
	public static final String DEFAULT_CANCEL_METHOD = "cancel";
	public static final String DEFAULT_RUN_METHOD = "run";
	public static final String DEFAULT_PROGRESS_METHOD = "getProgress";

	private String _runMethod;
	private String _cancelMethod;
	private String _progressMethod;

	private Object _javaObject = null;
	private JobDescriptor _descriptor;

	public JavaJob(JobDescriptor descriptor) {
		super(descriptor);
	}

	@Override
    protected List<String> getClassPaths() {
        List<String> classPath = super.getClassPaths();
        
        classPath.add(getSourcePathFromClass(JavaJobRunnerMain.class));
        String loggerPath = getSourcePathFromClass(org.apache.log4j.Logger.class);
        if (!classPath.contains(loggerPath)) {
            classPath.add(loggerPath);
        }
        
     
        return classPath;
	}

	@SuppressWarnings("unchecked")
	private static String getSourcePathFromClass(Class containedClass) {
	    File file = new File(containedClass.getProtectionDomain().getCodeSource().getLocation().getPath());
	    
        if (!file.isDirectory() && file.getName().endsWith(".class")) {
            String name = containedClass.getName();
            StringTokenizer tokenizer = new StringTokenizer(name, ".");
            while(tokenizer.hasMoreTokens()) {
                tokenizer.nextElement();
                
                file = file.getParentFile();
            }
            
            return file.getPath();  
        }
        else {
            return containedClass.getProtectionDomain().getCodeSource().getLocation().getPath();
        }
	}
	
    @Override
    protected String getJavaClass() {
        return JavaJobRunnerMain.class.getName();
    }
    
	@Override
	public String toString() {
		return "JavaJob{" + "_runMethod='" + _runMethod + '\''
				+ ", _cancelMethod='" + _cancelMethod + '\''
				+ ", _progressMethod='" + _progressMethod + '\''
				+ ", _javaObject=" + _javaObject + ", _descriptor="
				+ _descriptor + '}';
	}
}
