package org.bigloupe.web.scheduler.serializer;

import java.util.Map;

import org.bigloupe.web.scheduler.flow.ComposedExecutableFlow;
import org.bigloupe.web.scheduler.flow.ExecutableFlow;
import org.bigloupe.web.scheduler.flow.GroupedExecutableFlow;
import org.bigloupe.web.scheduler.flow.IndividualJobExecutableFlow;
import org.bigloupe.web.scheduler.flow.MultipleDependencyExecutableFlow;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;

/**
 *
 */
public class DefaultExecutableFlowSerializer extends ExecutableFlowSerializer
{
    public DefaultExecutableFlowSerializer()
    {
        Map<Class, Function<ExecutableFlow, Map<String, Object>>> subSerializers =
                ImmutableMap.<Class, Function<ExecutableFlow, Map<String, Object>>>of(
                        IndividualJobExecutableFlow.class, new IndividualJobEFSerializer(),
                        GroupedExecutableFlow.class, new GroupedEFSerializer(this),
                        ComposedExecutableFlow.class, new ComposedEFSerializer(this),
                        MultipleDependencyExecutableFlow.class, new MultipleDependencyEFSerializer(this)
                );

        setSerializers(subSerializers);
    }
}
