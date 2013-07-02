package org.bigloupe.web.scheduler.flow;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.bigloupe.web.scheduler.job.Status;
import org.bigloupe.web.util.Props;


/**
 * A FlowManager that caches ExecutableFlows.
 *
 * That is, if "createNewExecutableFlow()" generated a Flow with id 2, subsequent calls to loadExecutableFlow(2) would
 * return the same instance.
 */
public class CachingFlowManager implements FlowManager
{
    private static final Logger log = Logger.getLogger(CachingFlowManager.class);

    private final FlowManager baseManager;
    private final Map<String, FlowExecutionHolder> flowCache;

    public CachingFlowManager(FlowManager baseManager, final int cacheSize)
    {
        this.baseManager = baseManager;

        this.flowCache = Collections.synchronizedMap(
                new LinkedHashMap<String, FlowExecutionHolder>((int) (cacheSize * 1.5), 0.75f, true){
                    @Override
                    protected boolean removeEldestEntry(Map.Entry<String, FlowExecutionHolder> eldest)
                    {
                        final boolean tooManyElements = super.size() > cacheSize;

                        if (tooManyElements) {
                            final Status status = eldest.getValue().getFlow().getStatus();

                            if (status != Status.RUNNING) {
                                return true;
                            }
                            else {
                                log.warn(String.format(
                                        "Cache is at size[%s] and should have evicted an entry, but the oldest entry wasn't completed[%s].  Perhaps the cache size is too small",
                                        super.size(),
                                        status
                                ));
                            }
                        }

                        return false;
                    }
                }
        );
    }

    public boolean hasFlow(String name)
    {
        return baseManager.hasFlow(name);
    }

    public Flow getFlow(String name)
    {
        return baseManager.getFlow(name);
    }

    public Collection<Flow> getFlows()
    {
        return baseManager.getFlows();
    }

    public Set<String> getRootFlowNames()
    {
        return baseManager.getRootFlowNames();
    }

    @Override
    public Iterator<Flow> iterator()
    {
        return baseManager.iterator();
    }

    public ExecutableFlow createNewExecutableFlow(String name)
    {
        final ExecutableFlow retVal = baseManager.createNewExecutableFlow(name);

        return new WrappingExecutableFlow(retVal){
            @Override
            public void execute(Props parentProperties, FlowCallback callback) {

                if (! flowCache.containsKey(getId())) {
                    addToCache(
                            new FlowExecutionHolder(
                                    retVal,
                                    parentProperties
                            )
                    );
                }

                super.execute(parentProperties, callback);
            }
        };
    }

    public long getNextId()
    {
        return baseManager.getNextId();
    }

    public long getCurrMaxId()
    {
        return baseManager.getCurrMaxId();
    }

    public FlowExecutionHolder saveExecutableFlow(FlowExecutionHolder holder)
    {
        return baseManager.saveExecutableFlow(holder);
    }

    public FlowExecutionHolder loadExecutableFlow(long id)
    {
        final FlowExecutionHolder executableFlow = flowCache.get(String.valueOf(id));
        if (executableFlow != null) {
            return executableFlow;
        }

        final FlowExecutionHolder retVal = baseManager.loadExecutableFlow(id);

        addToCache(retVal);

        return retVal;
    }

    public void reload()
    {
        baseManager.reload();
    }

    private void addToCache(FlowExecutionHolder retVal)
    {
        if (retVal == null || retVal.getFlow() == null) {
            return;
        }

        if (flowCache.put(retVal.getFlow().getId(), retVal) != null) {
            throw new IllegalStateException(
                    String.format(
                            "Attempted to add object to the cache but the id[%s] already existed.  Flow was [%s]",
                            retVal.getFlow().getId(),
                            retVal
                    )
            );
        }
    }

	@Override
	public List<String> getFolders() {
		return baseManager.getFolders();
	}

	@Override
	public List<String> getRootNamesByFolder(String folder) {
		return baseManager.getRootNamesByFolder(folder);
	}
}
