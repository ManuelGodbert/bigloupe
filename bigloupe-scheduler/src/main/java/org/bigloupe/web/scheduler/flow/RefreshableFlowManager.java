package org.bigloupe.web.scheduler.flow;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicReference;

import org.bigloupe.web.scheduler.JobDescriptor;
import org.bigloupe.web.scheduler.JobManager;
import org.bigloupe.web.scheduler.serializer.FlowExecutionDeserializer;
import org.bigloupe.web.scheduler.serializer.FlowExecutionSerializer;


/**
 *
 */
public class RefreshableFlowManager implements FlowManager
{
    private final Object idSync = new Object();
    
    private final JobManager jobManager;
    private final FlowExecutionSerializer serializer;
    private final FlowExecutionDeserializer deserializer;
    private final File storageDirectory;

    private final AtomicReference<ImmutableFlowManager> delegateManager;

    public RefreshableFlowManager(
            JobManager jobManager,
            FlowExecutionSerializer serializer,
            FlowExecutionDeserializer deserializer,
            File storageDirectory,
            long lastId
    )
    {
        this.jobManager = jobManager;
        this.serializer = serializer;
        this.deserializer = deserializer;
        this.storageDirectory = storageDirectory;

        this.delegateManager = new AtomicReference<ImmutableFlowManager>(null);
        reloadInternal(lastId);
    }

    @Override
    public boolean hasFlow(String name)
    {
        return delegateManager.get().hasFlow(name);
    }

    @Override
    public Flow getFlow(String name)
    {
        return delegateManager.get().getFlow(name);
    }

    @Override
    public Collection<Flow> getFlows()
    {
        return delegateManager.get().getFlows();
    }

    @Override
    public Set<String> getRootFlowNames()
    {
        return delegateManager.get().getRootFlowNames();
    }

    @Override
    public Iterator<Flow> iterator()
    {
        return delegateManager.get().iterator();
    }

    @Override
    public ExecutableFlow createNewExecutableFlow(String name)
    {
        return delegateManager.get().createNewExecutableFlow(name);
    }

    @Override
    public long getNextId()
    {
        synchronized (idSync) {
            return delegateManager.get().getNextId();
        }
    }

    @Override
    public long getCurrMaxId()
    {
        return delegateManager.get().getCurrMaxId();
    }

    @Override
    public FlowExecutionHolder saveExecutableFlow(FlowExecutionHolder holder)
    {
        return delegateManager.get().saveExecutableFlow(holder);
    }

    @Override
    public FlowExecutionHolder loadExecutableFlow(long id)
    {
        return delegateManager.get().loadExecutableFlow(id);
    }

    @Override
    public void reload()
    {
        reloadInternal(null);
    }

    private final void reloadInternal(Long lastId)
    {
        Map<String, Flow> flowMap = new HashMap<String, Flow>();
        Map<String, List<String>> folderToRoot = new LinkedHashMap<String, List<String>>();
        Set<String> rootFlows = new TreeSet<String>();
        final Map<String, JobDescriptor> allJobDescriptors = jobManager.loadJobDescriptors();
        for (JobDescriptor rootDescriptor : jobManager.getRootJobDescriptors(allJobDescriptors)) {
            if (rootDescriptor.getId() != null) {
                // This call of magical wonderment ends up pushing all Flow objects in the dependency graph for the root into flowMap
                Flows.buildLegacyFlow(jobManager, flowMap, rootDescriptor, allJobDescriptors);
                rootFlows.add(rootDescriptor.getId());
                
                // For folder path additions
                String jobPath = rootDescriptor.getPath();
                // For windows
                jobPath = jobPath.replace(System.getProperty("file.separator"), "/");
                if (jobPath.contains("/")) {
	                String[] split = jobPath.split("/");
	                if (split[0].isEmpty()) {
	                	jobPath = split[1];
	                }
	                else {
	                	jobPath = split[0];
	                }
                }
                else {
                	jobPath = "default";
                }

                List<String> root = folderToRoot.get(jobPath);
                if (root == null) {
                	root = new ArrayList<String>();
                	folderToRoot.put(jobPath, root);
                }
                root.add(rootDescriptor.getId());
            }
        }

        synchronized (idSync) {
            delegateManager.set(
                    new ImmutableFlowManager(
                            flowMap,
                            rootFlows,
                    		folderToRoot,
                            serializer,
                            deserializer,
                            storageDirectory,
                            lastId == null ? delegateManager.get().getCurrMaxId() : lastId
                    )
            );
        }
    }

	@Override
	public List<String> getFolders() {
		return delegateManager.get().getFolders();
	}

	@Override
	public List<String> getRootNamesByFolder(String folder) {
		return delegateManager.get().getRootNamesByFolder(folder);
	}
}
