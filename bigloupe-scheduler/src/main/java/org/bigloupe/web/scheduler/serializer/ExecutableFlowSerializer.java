package org.bigloupe.web.scheduler.serializer;

import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import org.bigloupe.web.scheduler.flow.ExecutableFlow;
import org.bigloupe.web.scheduler.flow.WrappingExecutableFlow;

import com.google.common.base.Function;

/**
 *
 */
public class ExecutableFlowSerializer implements Function<ExecutableFlow, Map<String, Object>>
{
    private final AtomicBoolean serializersBeenSet = new AtomicBoolean(false);

    private volatile Map<Class, Function<ExecutableFlow, Map<String, Object>>> serializers;

    public void setSerializers(Map<Class, Function<ExecutableFlow, Map<String, Object>>> serializers)
    {
        if (serializers != null && serializersBeenSet.compareAndSet(false, true)) {
            this.serializers = serializers;
        }
        else {
            throw new IllegalStateException(String.format(
                    "Serializers cannot null nor can they be set more than once, value passed in was [%s]",
                    serializers
            ));
        }
    }

    @Override
    public Map<String,  Object> apply(ExecutableFlow executableFlow)
    {
        if (! serializersBeenSet.get()) {
            throw new IllegalStateException("Serializers have not been set yet.  Call setSerializers before calling apply.");
        }


        Function<ExecutableFlow, Map<String, Object>> subSerializer = serializers.get(executableFlow.getClass());

        if (subSerializer == null) {
            if (executableFlow instanceof WrappingExecutableFlow) {
                return apply(((WrappingExecutableFlow) executableFlow).getDelegateFlow());
            }

            throw new RuntimeException(String.format("No known serializer for class[%s].", executableFlow.getClass()));
        }

        return subSerializer.apply(executableFlow);
    }
}
