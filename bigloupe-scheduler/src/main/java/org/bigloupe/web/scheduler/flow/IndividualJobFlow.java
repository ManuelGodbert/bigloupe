package org.bigloupe.web.scheduler.flow;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.bigloupe.web.scheduler.JobManager;



/**
 * A flow wrapper the name of a Job object
 */
public class IndividualJobFlow implements Flow
{
    private final JobManager jobManager;
    private final String name;

    public IndividualJobFlow(String name, JobManager jobManager)
    {
        this.name = name;
        this.jobManager = jobManager;
    }

    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public boolean hasChildren()
    {
        return false;
    }

    @Override
    public List<Flow> getChildren()
    {
        return Collections.emptyList();
    }

    @Override
    public ExecutableFlow createExecutableFlow(String id, Map<String, ExecutableFlow> overrides)
    {
        final ExecutableFlow retVal = overrides.containsKey(getName()) ?
                                      overrides.get(getName()) :
                                      new IndividualJobExecutableFlow(id, name, jobManager);

        if (overrides.containsKey(retVal.getName())) {
            throw new RuntimeException(String.format("overrides already has an entry with my key[%s], wtf?", retVal.getName()));
        }
        overrides.put(retVal.getName(), retVal);

        return retVal;
    }

    @Override
    public String toString()
    {
        return "IndividualJobExecutableFlow{" +
               "job=" + name +
               '}';
    }
}