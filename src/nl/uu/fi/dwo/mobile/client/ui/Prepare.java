package nl.uu.fi.dwo.mobile.client.ui;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;

public class Prepare
{
	void defer(ScheduledCommand cmd)
	{
		Scheduler.get().scheduleDeferred(cmd);
	}

	@Deprecated
	void immediate(ScheduledCommand cmd)
	{
		cmd.execute();
	}
}