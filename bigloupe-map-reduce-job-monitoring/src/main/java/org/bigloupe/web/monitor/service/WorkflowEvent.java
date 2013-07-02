/*
Copyright 2012 Twitter, Inc.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */
package org.bigloupe.web.monitor.service;

import java.io.Serializable;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Class that represents a WorkflowEvent of a given EVENT_TYPE. Each one of
 * these created will have a unique eventId that increments up for each object.
 * eventIds will always be >= 0. The data associated with the event currently
 * can be anything. TODO: can we type eventData better?
 * 
 * @author billg
 */
public class WorkflowEvent implements Serializable {

	private static final long serialVersionUID = 143375598957830797L;
	private static AtomicInteger NEXT_ID = new AtomicInteger();

	public static enum EVENT_TYPE {
		JOB_STARTED, JOB_FINISHED, JOB_FAILED, JOB_PROGRESS, WORKFLOW_PROGRESS
	};

	private long timestamp;
	private int eventId;
	private String runtime;
	private EVENT_TYPE eventType;
	private Object eventData;

	/**
	 * Empty constructor for JSON (un)marshalling
	 */
	public WorkflowEvent() {
	}
	
	/**
	 * Constructor to store event in memory
	 * @param eventType
	 * @param eventData
	 * @param runtime
	 */
	public WorkflowEvent(EVENT_TYPE eventType, Object eventData, String runtime) {
		this.eventId = NEXT_ID.incrementAndGet();
		this.timestamp = System.currentTimeMillis();
		this.eventType = eventType;
		this.eventData = eventData;
		this.runtime = runtime;
	}

	/**
	 * Constructor to store event in database
	 * @param eventId
	 * @param eventType
	 * @param eventData
	 * @param runtime
	 * @param timestamp
	 */
	public WorkflowEvent(int eventId, EVENT_TYPE eventType, Object eventData, String runtime, long timestamp) {
		this(eventType, eventData, runtime);
		this.eventId = eventId;
		this.timestamp = timestamp;
	}
	
	public long getTimestamp() {
		return timestamp;
	}

	public int getEventId() {
		return eventId;
	}

	public EVENT_TYPE getEventType() {
		return eventType;
	}

	public Object getEventData() {
		return eventData;
	}

	public String getRuntime() {
		return runtime;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public void setEventId(int eventId) {
		this.eventId = eventId;
	}

	public void setRuntime(String runtime) {
		this.runtime = runtime;
	}

	public void setEventType(EVENT_TYPE eventType) {
		this.eventType = eventType;
	}

	public void setEventData(Object eventData) {
		this.eventData = eventData;
	}
	
	@Override
	public String toString() {
		return "WorkflowEvent [timestamp=" + new Date(timestamp) + ", eventId=" + eventId
				+ ", runtime=" + runtime + ", eventType=" + eventType
				+ ", eventData=" + eventData + "]";
	}
}
