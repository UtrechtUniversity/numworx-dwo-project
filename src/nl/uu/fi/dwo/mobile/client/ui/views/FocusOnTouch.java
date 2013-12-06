package nl.uu.fi.dwo.mobile.client.ui.views;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.user.client.ui.FocusPanel;

final class FocusOnTouch implements MouseUpHandler
{
	private FocusPanel focusPanel;
	
	
	public void requestFocus()
	{
		Scheduler.get().scheduleDeferred(new ScheduledCommand() // voor firefox delayed focus.
		{
			public void execute()
			{
				focusPanel.setFocus(true);
			}
		});
	}
	public FocusOnTouch(FocusPanel focusPanel) {
		super();
		this.focusPanel = focusPanel;
	}
	public void onMouseUp(MouseUpEvent event)
	{
		requestFocus();
	}
}