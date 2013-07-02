package org.bigloupe.web.scheduler.serializer;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import org.bigloupe.web.scheduler.flow.ExecutableFlow;
import org.bigloupe.web.scheduler.flow.FlowExecutionHolder;

import com.google.common.base.Function;

/**
 */
public class FlowExecutionSerializer implements Function<FlowExecutionHolder, Map<String, Object>>
{
    private final Function<ExecutableFlow, Map<String, Object>> executableFlowSerializer;


    public FlowExecutionSerializer(
            Function<ExecutableFlow, Map<String, Object>> executableFlowSerializer
    )
    {
        this.executableFlowSerializer = executableFlowSerializer;
    }

    @Override
    public Map<String, Object> apply(FlowExecutionHolder flowExecutionHolder) {
        Map<String, Object> retVal = new LinkedHashMap<String, Object>();

        final Map<String, String> propsToPut;
        if (flowExecutionHolder.getParentProps() != null) {
            propsToPut = flowExecutionHolder.getParentProps().getMapByPrefix("");
        } else {
            propsToPut = Collections.emptyMap();
        }

        retVal.put("type", "FlowExecutionHolder");
        retVal.put("flow", executableFlowSerializer.apply(flowExecutionHolder.getFlow()));
        retVal.put("parentProps", propsToPut);

        return retVal;
    }
}
