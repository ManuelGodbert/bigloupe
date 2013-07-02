package org.bigloupe.web.scheduler.serializer;

import java.util.Map;

import org.bigloupe.web.scheduler.flow.FlowExecutionHolder;
import org.bigloupe.web.util.Props;

import com.google.common.base.Function;

/**
 * Deserializes a "flow execution" object.  I.e., one of the JSONObjects
 * in the json files in the execution history directory.
 *
 */
public class FlowExecutionDeserializer implements Function<Map<String, Object>, FlowExecutionHolder>
{
    private final ExecutableFlowDeserializer flowDeserializer;

    public FlowExecutionDeserializer(
            ExecutableFlowDeserializer flowDeserializer
    )
    {
        this.flowDeserializer = flowDeserializer;
    }

    @Override
    public FlowExecutionHolder apply(Map<String, Object> descriptor) {
        final FlowExecutionHolder retVal;

        // Maintain backwards compatibility, check if it's really a holder
        final Object type = descriptor.get("type");
        if (type != null && "FlowExecutionHolder".equals(type.toString())) {
            retVal = new FlowExecutionHolder(
                    flowDeserializer.apply(Verifier.getVerifiedObject(descriptor, "flow", Map.class)),
                    new Props(new Props(), Verifier.getVerifiedObject(descriptor, "parentProps", Map.class))
            );
        }
        else {
            retVal = new FlowExecutionHolder(
                    flowDeserializer.apply(descriptor),
                    new Props()
            );
        }

        return retVal;
    }
}
