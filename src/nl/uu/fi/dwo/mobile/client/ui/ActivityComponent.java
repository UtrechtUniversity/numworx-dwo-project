package nl.uu.fi.dwo.mobile.client.ui;

import java.util.Optional;

import javax.inject.Named;

import com.google.web.bindery.event.shared.EventBus;

import dagger.Subcomponent;
import nl.uu.fi.dwo.account.client.DwoGlobalVars;
import nl.uu.fi.dwo.mobile.client.DWOplayerParameters;

@Subcomponent
public abstract class ActivityComponent {

	public abstract EventBus getEventBus();
	public abstract DWOplayerParameters parameters();
	public abstract TrafficAgent agent();
	public abstract Optional<DwoGlobalVars> vars();
	
	@Subcomponent.Builder
	public
	interface Builder {
		ActivityComponent build();
	}

	@Named("premium")
	public abstract boolean isPremium();
		
}
