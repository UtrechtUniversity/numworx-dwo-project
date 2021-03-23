package nl.uu.fi.dwo.mobile.client.ui;

import com.google.web.bindery.event.shared.EventBus;

import dagger.Subcomponent;
import nl.uu.fi.dwo.mobile.client.DWOplayerParameters;

@Subcomponent
public abstract class ActivityComponent {

	public abstract EventBus getEventBus();
	public abstract DWOplayerParameters parameters();
	public abstract TrafficAgent agent();

	
	@Subcomponent.Builder
	public
	interface Builder {
		ActivityComponent build();
	}
}
