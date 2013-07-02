package org.bigloupe.web.scheduler.flow;

import java.util.Map;
import java.util.Set;

import org.bigloupe.web.scheduler.JobDescriptor;
import org.bigloupe.web.scheduler.JobManager;
import org.bigloupe.web.scheduler.job.Status;


/**
 *
 */
public class Flows
{
    public static Flow buildLegacyFlow(
            final JobManager jobManager,
            final Map<String, Flow> alreadyBuiltFlows,
            final JobDescriptor rootDescriptor,
            final Map<String, JobDescriptor> allJobDescriptors
    )
    {
        //TODO MED: The jobManager isn't really the best Job factory.  It should be revisited, but it works for now.
        if (alreadyBuiltFlows.containsKey(rootDescriptor.getId())) {
            return alreadyBuiltFlows.get(rootDescriptor.getId());
        }

        final Flow retVal;
        if (rootDescriptor.hasDependencies()) {
            Set<JobDescriptor> dependencies = rootDescriptor.getDependencies();
            Flow[] depFlows = new Flow[dependencies.size()];

            int index = 0;
            for (JobDescriptor jobDescriptor : dependencies) {
                depFlows[index] = buildLegacyFlow(jobManager, alreadyBuiltFlows, jobDescriptor, allJobDescriptors);
                ++index;
            }

            retVal = new MultipleDependencyFlow(
                    new IndividualJobFlow(
                            rootDescriptor.getId(),
                            jobManager
                    ),
                    depFlows
            );
        }
        else {
            retVal = new IndividualJobFlow(
                    rootDescriptor.getId(),
                    jobManager
            );
        }

        alreadyBuiltFlows.put(retVal.getName(), retVal);

        return retVal;
    }

    public static ExecutableFlow resetFailedFlows(
            final ExecutableFlow theFlow
    )
    {
        if (theFlow.getStatus() == Status.FAILED) {
            theFlow.reset();
        }

        if (theFlow.hasChildren()) {
            for (ExecutableFlow flow : theFlow.getChildren()) {
                resetFailedFlows(flow);
            }
        }

        return theFlow;
    }

}
