package org.bigloupe.web.scheduler.serializer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.bigloupe.web.scheduler.flow.ComposedExecutableFlow;
import org.bigloupe.web.scheduler.flow.ExecutableFlow;
import org.bigloupe.web.scheduler.flow.GroupedExecutableFlow;
import org.bigloupe.web.scheduler.flow.MultipleDependencyExecutableFlow;


import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 *
 */
public class ExecutableFlowDeserializer implements Function<Map<String, Object>, ExecutableFlow>
{
    private volatile Function<Map<String, Object>, ExecutableFlow> jobDeserializer;

    public void setJobDeserializer(Function<Map<String, Object>, ExecutableFlow> jobDeserializer) {
        this.jobDeserializer = jobDeserializer;
    }

    @Override
    public ExecutableFlow apply(Map<String, Object> descriptor)
    {
        Map<String, ExecutableFlow> jobs = Maps.uniqueIndex(
                Iterables.<Map<String, Object>, ExecutableFlow>transform(
                        Verifier.getVerifiedObject(descriptor, "jobs", Map.class).values(),
                        jobDeserializer
                        ),
                new Function<ExecutableFlow, String>()
                {
                    @Override
                    public String apply(ExecutableFlow flow)
                    {
                        return flow.getName();
                    }
                }
        );

        Map<String, List<String>> dependencies = Verifier.getVerifiedObject(descriptor, "dependencies", Map.class);
        List<String> roots = Verifier.getVerifiedObject(descriptor, "root", List.class);
        String id = Verifier.getString(descriptor, "id");
        
        return buildFlow(id, roots, dependencies, jobs);
    }

    private ExecutableFlow buildFlow(
            final String id,
            Iterable<String> roots,
            final Map<String, List<String>> dependencies,
            final Map<String, ExecutableFlow> jobs
    )
    {
        final ArrayList<ExecutableFlow> executableFlows = Lists.newArrayList(
                Iterables.transform(
                        roots,
                        new Function<String, ExecutableFlow>()
                        {
                            @Override
                            public ExecutableFlow apply(String root)
                            {
                                if (dependencies.containsKey(root)) {
                                    final ExecutableFlow dependeeFlow = buildFlow(id, dependencies.get(root), dependencies, jobs);

                                    if (dependeeFlow instanceof GroupedExecutableFlow) {
                                        return new MultipleDependencyExecutableFlow(
                                                id,
                                                buildFlow(id, Arrays.asList(root), Collections.<String, List<String>>emptyMap(), jobs),
                                                (ExecutableFlow[]) dependeeFlow.getChildren().toArray()
                                        );
                                    }
                                    else {
                                        return new ComposedExecutableFlow(
                                                id,
                                                buildFlow(id, Arrays.asList(root), Collections.<String, List<String>>emptyMap(), jobs),
                                                dependeeFlow
                                        );
                                    }

                                }
                                else {
                                    if (! jobs.containsKey(root)) {
                                        throw new IllegalStateException(String.format(
                                                "Expected job[%s] in jobs list[%s]",
                                                root,
                                                jobs
                                        ));
                                    }

                                    return jobs.get(root);
                                }
                            }
                        }
                )
        );

        if (executableFlows.size() == 1) {
            return executableFlows.get(0);
        }
        else {
            return new GroupedExecutableFlow(id, executableFlows.toArray(new ExecutableFlow[executableFlows.size()]));
        }
    }
}
