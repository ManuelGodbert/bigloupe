package org.bigloupe.web.scheduler;

import java.util.List;

public interface ScheduleLoader {
	public void saveSchedule(List<ScheduledJob> schedule);
	
	public List<ScheduledJob> loadSchedule();
}