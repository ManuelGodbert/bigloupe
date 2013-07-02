package org.bigloupe.web.scheduler.flow;

import java.util.Map;

import org.bigloupe.web.scheduler.JobManager;
import org.bigloupe.web.scheduler.JobWrappingFactory;
import org.bigloupe.web.scheduler.job.Status;
import org.bigloupe.web.scheduler.serializer.Verifier;
import org.bigloupe.web.util.Props;
import org.joda.time.DateTime;

import com.google.common.base.Function;

/**
 *
 */
public class JobManagerFlowDeserializer implements Function<Map<String, Object>, ExecutableFlow>
{
    private final JobManager jobManager;
    private final JobWrappingFactory jobFactory;

    public JobManagerFlowDeserializer(
            JobManager jobManager,
            JobWrappingFactory jobFactory
    )
    {
        this.jobManager = jobManager;
        this.jobFactory = jobFactory;
    }

    @Override
    public ExecutableFlow apply(Map<String, Object> descriptor)
    {
        String jobName = Verifier.getString(descriptor, "name");
        Status jobStatus = Verifier.getEnumType(descriptor, "status", Status.class);
        String id = Verifier.getString(descriptor, "id");
        DateTime startTime = Verifier.getOptionalDateTime(descriptor, "startTime");
        DateTime endTime = Verifier.getOptionalDateTime(descriptor, "endTime");
        Map<String, String> parentPropsMap = Verifier.getOptionalObject(descriptor, "overrideProps", Map.class);
        Map<String, String> returnPropsMap = Verifier.getOptionalObject(descriptor, "returnProps", Map.class);

        final IndividualJobExecutableFlow retVal = new IndividualJobExecutableFlow(
                id,
                jobName,
                jobManager
        );
        if (jobStatus != Status.RUNNING) {
            retVal.setStatus(jobStatus);
        }

        if (startTime != null) {
            retVal.setStartTime(startTime);
        }

        if (endTime != null) {
            retVal.setEndTime(endTime);
        }

        if (parentPropsMap != null) {
            Props parentProps = new Props();
            parentProps.putAll(parentPropsMap);

            retVal.setParentProperties(parentProps);
        }

        if (returnPropsMap != null) {
            Props returnProps = new Props();
            returnProps.putAll(returnPropsMap);

            retVal.setReturnProperties(returnProps);
        }

        return retVal;
    }
}
