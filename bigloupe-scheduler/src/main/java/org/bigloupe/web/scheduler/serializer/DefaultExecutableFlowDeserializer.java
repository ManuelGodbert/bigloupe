package org.bigloupe.web.scheduler.serializer;

import java.util.Map;

import org.bigloupe.web.scheduler.JobManager;
import org.bigloupe.web.scheduler.JobWrappingFactory;
import org.bigloupe.web.scheduler.flow.ExecutableFlow;
import org.bigloupe.web.scheduler.flow.JobManagerFlowDeserializer;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;

/**
 */
public class DefaultExecutableFlowDeserializer extends ExecutableFlowDeserializer
{
    public DefaultExecutableFlowDeserializer(
            final JobManager jobManager,
            final JobWrappingFactory jobFactory
    )
    {
        setJobDeserializer(
                new JobFlowDeserializer(
                        ImmutableMap.<String, Function<Map<String, Object>, ExecutableFlow>>of(
                                "jobManagerLoaded", new JobManagerFlowDeserializer(jobManager, jobFactory)
                        )
                )
        );
    }
}
