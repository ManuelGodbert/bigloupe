/*
 * Copyright 2010 LinkedIn, Inc
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package org.bigloupe.web.scheduler;


import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;
import java.util.TimeZone;

import org.apache.log4j.Logger;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.log.Log4JLogChute;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.bigloupe.web.BigLoupeConfiguration;
import org.bigloupe.web.scheduler.flow.CachingFlowManager;
import org.bigloupe.web.scheduler.flow.FlowManager;
import org.bigloupe.web.scheduler.flow.RefreshableFlowManager;
import org.bigloupe.web.scheduler.job.Job;
import org.bigloupe.web.scheduler.job.JobExecutorManager;
import org.bigloupe.web.scheduler.job.builtin.IndexFileProcessJob;
import org.bigloupe.web.scheduler.job.builtin.JavaJob;
import org.bigloupe.web.scheduler.job.builtin.JavaProcessJob;
import org.bigloupe.web.scheduler.job.builtin.MapReduceJob;
import org.bigloupe.web.scheduler.job.builtin.NoopJob;
import org.bigloupe.web.scheduler.job.builtin.PigProcessJob;
import org.bigloupe.web.scheduler.job.builtin.ProcessJob;
import org.bigloupe.web.scheduler.job.builtin.PythonJob;
import org.bigloupe.web.scheduler.job.builtin.RubyJob;
import org.bigloupe.web.scheduler.job.builtin.ScriptJob;
import org.bigloupe.web.scheduler.job.builtin.SqoopProcessJob;
import org.bigloupe.web.scheduler.jobcontrol.impl.jobs.locks.NamedPermitManager;
import org.bigloupe.web.scheduler.jobcontrol.impl.jobs.locks.ReadWriteLockManager;
import org.bigloupe.web.scheduler.serializer.DefaultExecutableFlowDeserializer;
import org.bigloupe.web.scheduler.serializer.DefaultExecutableFlowSerializer;
import org.bigloupe.web.scheduler.serializer.ExecutableFlowDeserializer;
import org.bigloupe.web.scheduler.serializer.ExecutableFlowSerializer;
import org.bigloupe.web.scheduler.serializer.FlowExecutionDeserializer;
import org.bigloupe.web.scheduler.serializer.FlowExecutionSerializer;
import org.bigloupe.web.service.common.MailerService;
import org.bigloupe.web.util.Props;
import org.bigloupe.web.util.Utils;
import org.joda.time.DateTimeZone;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.ImmutableMap;

/**
 * Master application that runs everything
 *
 * This class will be loaded up either by running from the command line or via a
 * servlet context listener.
 *
 * @author jkreps
 *
 */
public class InitializeScheduler
{

    private static final Logger logger = Logger.getLogger(InitializeScheduler.class);
    private static final String INSTANCE_NAME = "instance.name";
    private static final String DEFAULT_TIMEZONE_ID = "default.timezone.id";
    
    private final String _instanceName;
    private final List<File> jobDirs;
    private final File logsDir;
    private final File tempDir;
    private final JobManager _jobManager;
    
    protected MailerService _mailerService;
    
    private final ClassLoader _baseClassLoader;
    private final String _hdfsUrl;
    private final FlowManager _allFlows;
    
    private final JobExecutorManager _jobExecutorManager;
    private final ScheduleManager _schedulerManager;
    
    public InitializeScheduler(List<File> jobDirs, File logDir, File tempDir, MailerService mailerService, BigLoupeConfiguration configuration, boolean enableDevMode) throws IOException {
        this.jobDirs = Utils.nonNull(jobDirs);
        this.logsDir = Utils.nonNull(logDir);
        this.tempDir = Utils.nonNull(tempDir);
        this._mailerService = mailerService;

        if(!this.logsDir.exists())
            this.logsDir.mkdirs();

        if(!this.tempDir.exists())
            this.tempDir.mkdirs();

        for(File jobDir: jobDirs) {
            if(!jobDir.exists()) {
                logger.warn("Job directory " + jobDir + " does not exist. Creating.");
                jobDir.mkdirs();
            }
        }

        if(jobDirs.size() < 1)
            throw new IllegalArgumentException("No job directory given.");

        Props defaultProps = PropsUtils.loadPropsInDirs(jobDirs, ".properties", ".schema");

        _baseClassLoader = getBaseClassloader();

        String defaultTimezoneID = defaultProps.getString(DEFAULT_TIMEZONE_ID, null);
        if (defaultTimezoneID != null) {
        	DateTimeZone.setDefault(DateTimeZone.forID(defaultTimezoneID));
        	TimeZone.setDefault(TimeZone.getTimeZone(defaultTimezoneID));
        }
        
        NamedPermitManager permitManager = getNamedPermitManager(defaultProps);
        JobWrappingFactory factory = new JobWrappingFactory(
                permitManager,
                new ReadWriteLockManager(),
                logsDir.getAbsolutePath(),
                "java",
                new ImmutableMap.Builder<String, Class<? extends Job>>()
                 .put("java", JavaJob.class)
                 .put("command", ProcessJob.class)
                 .put("javaprocess", JavaProcessJob.class)
                 .put("map-reduce", MapReduceJob.class)
                 .put("pig", PigProcessJob.class)
                 .put("propertyPusher", NoopJob.class)
                 .put("python", PythonJob.class)
                 .put("ruby", RubyJob.class)
                 .put("script", ScriptJob.class)
                 .put("sqoop", SqoopProcessJob.class)
                 .put("indexfile", IndexFileProcessJob.class)
                 .put("noop", NoopJob.class).build());

        _hdfsUrl = defaultProps.getString("hdfs.instance.url", null);
        _jobManager = new JobManager(factory,
                                     logsDir.getAbsolutePath(),
                                     defaultProps,
                                     jobDirs,
                                     _baseClassLoader, configuration);

        String failureEmail = defaultProps.getString("job.failure.email", null);
        String successEmail = defaultProps.getString("job.success.email", null);
        int schedulerThreads = defaultProps.getInt("scheduler.threads", 50);
        _instanceName = defaultProps.getString(INSTANCE_NAME, "");
        
        final File initialJobDir = jobDirs.get(0);
        File schedule = getScheduleFile(defaultProps, initialJobDir);
        File backup = getBackupFile(defaultProps, initialJobDir);
        File executionsStorageDir = new File(
                defaultProps.getString("azkaban.executions.storage.dir", initialJobDir.getAbsolutePath() + "/executions")
        );
        if (! executionsStorageDir.exists()) executionsStorageDir.mkdirs();
        long lastExecutionId = getLastExecutionId(executionsStorageDir);
        logger.info(String.format("Using path[%s] for storing executions.", executionsStorageDir));
        logger.info(String.format("Last known execution id was [%s]", lastExecutionId));

        final ExecutableFlowSerializer flowSerializer = new DefaultExecutableFlowSerializer();
        final ExecutableFlowDeserializer flowDeserializer = new DefaultExecutableFlowDeserializer(_jobManager, factory);

        FlowExecutionSerializer flowExecutionSerializer = new FlowExecutionSerializer(flowSerializer);
        FlowExecutionDeserializer flowExecutionDeserializer = new FlowExecutionDeserializer(flowDeserializer);

        _allFlows = new CachingFlowManager(
                new RefreshableFlowManager(
                        _jobManager,
                        flowExecutionSerializer,
                        flowExecutionDeserializer, 
                        executionsStorageDir,
                        lastExecutionId
                ),
                defaultProps.getInt("azkaban.flow.cache.size", 1000)
        );
        _jobManager.setFlowManager(_allFlows);

        _jobExecutorManager = new JobExecutorManager(
				        		_allFlows, 
				        		_jobManager, 
				        		failureEmail, 
				        		successEmail,
				        		schedulerThreads
				        	);
        
        this._schedulerManager = new ScheduleManager(_jobExecutorManager, new LocalFileScheduleLoader(schedule, backup));

        /* set predefined log url prefix 
        */
        String server_url = defaultProps.getString("server.url", null) ;
        if (server_url != null) {
            if (server_url.endsWith("/"))
            	_jobExecutorManager.setRuntimeProperty(BigLoupeConfiguration.DEFAULT_LOG_URL_PREFIX, server_url + "logs?file=" );
            else 
            	_jobExecutorManager.setRuntimeProperty(BigLoupeConfiguration.DEFAULT_LOG_URL_PREFIX, server_url + "/logs?file=" );
        }

    }

    public String getLogDirectory() {
        return logsDir.getAbsolutePath();
    }

    public String getTempDirectory() {
        return tempDir.getAbsolutePath();
    }

    public List<File> getJobDirectories() {
        return jobDirs;
    }

    public JobExecutorManager getJobExecutorManager() {
        return _jobExecutorManager;
    }
    
    public ScheduleManager getScheduleManager() {
        return _schedulerManager;
    }
    
    public JobManager getJobManager() {
        return _jobManager;
    }

    public String getHdfsUrl() {
        return this._hdfsUrl;
    }

    public boolean hasHdfsUrl() {
        return this._hdfsUrl != null;
    }

    public ClassLoader getClassLoader() {
        return _baseClassLoader;
    }

    public String getAppInstanceName() {
        return _instanceName;
    }
    
    public FlowManager getAllFlows()
    {
        return _allFlows;
    }

    private ClassLoader getBaseClassloader() throws MalformedURLException
    {
        final ClassLoader retVal;

        String hadoopHome = System.getenv("HADOOP_HOME");
        if(hadoopHome == null) {
            logger.info("HADOOP_HOME not set, using default hadoop config.");
            retVal = getClass().getClassLoader();
        } else {
            logger.info("Using hadoop config found in " + hadoopHome);
            retVal = new URLClassLoader(new URL[] { new File(hadoopHome, "conf").toURI().toURL() },
                                        getClass().getClassLoader());
        }

        return retVal;
    }

    private NamedPermitManager getNamedPermitManager(Props props) throws MalformedURLException
    {
        int workPermits = props.getInt("total.job.permits", Integer.MAX_VALUE);
        NamedPermitManager permitManager = new NamedPermitManager();
        permitManager.createNamedPermit("default", workPermits);

        return permitManager;
    }

    private File getBackupFile(Props defaultProps, File initialJobDir)
    {
        File retVal = new File(initialJobDir.getAbsoluteFile(), "jobs.schedule.backup");

        String backupFile = defaultProps.getString("schedule.backup.file", null);
        if(backupFile != null)
            retVal = new File(backupFile);
        else
            logger.info("Schedule backup file param not set. Defaulting to " + retVal.getAbsolutePath());

        return retVal;
    }

    private File getScheduleFile(Props defaultProps, File initialJobDir)
    {
        File retVal = new File(initialJobDir.getAbsoluteFile(), "jobs.schedule");

        String scheduleFile = defaultProps.getString("schedule.file", null);
        if(scheduleFile != null)
            retVal = new File(scheduleFile);
        else
            logger.info("Schedule file param not set. Defaulting to " + retVal.getAbsolutePath());

        return retVal;
    }

    private long getLastExecutionId(File executionsStorageDir)
    {
        long lastId = 0;

        for (File file : executionsStorageDir.listFiles()) {
            final String filename = file.getName();
            if (filename.endsWith(".json")) {
                try {
                    lastId = Math.max(
                            lastId,
                            Long.parseLong(filename.substring(0, filename.length() - 5))
                    );
                }
                catch (NumberFormatException e) {
                }
            }
        }

        return lastId;
    }

    
    public String getRuntimeProperty(String name) {
        return _jobExecutorManager.getRuntimeProperty(name);
    }

    public void setRuntimeProperty(String key, String value) {
    	_jobExecutorManager.setRuntimeProperty(key, value);
    }

}
