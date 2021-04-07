package nl.uu.fi.dwo.mobile.client.ui;

import java.util.Optional;

import javax.inject.Named;

import com.google.web.bindery.event.shared.EventBus;

import dagger.Subcomponent;
import nl.uu.fi.dwo.account.client.DwoGlobalVars;
import nl.uu.fi.dwo.mobile.client.DWOplayerParameters;
import nl.uu.fi.dwo.mobile.client.dagger.ActivityScope;
import nl.uu.fi.dwo.mobile.client.sco.Memento;
import nl.uu.fi.dwo.mobile.client.sco.MementoModule;
import nl.uu.fi.dwo.mobile.client.sco.SMLogger;
import nl.uu.fi.dwo.mobile.client.sco.Scorm2004IF;
import nl.uu.fi.dwo.mobile.utils.Logging;

@Subcomponent(modules= {MementoModule.class, SMLogger.LoggingModule.class} )
@ActivityScope
public abstract class ActivityComponent {

	public abstract EventBus getEventBus();
	public abstract DWOplayerParameters parameters();
	public abstract TrafficAgent agent();
	public abstract Optional<DwoGlobalVars> vars();
	
	public abstract Memento memento();
	@Named("API") public abstract Scorm2004IF api();
	
	@Subcomponent.Builder
	public
	interface Builder {
		ActivityComponent build();
		Builder mementoModule(MementoModule module);
		Builder loggingModule(SMLogger.LoggingModule module);
	}

	@Named("premium")
	public abstract boolean isPremium();

	public abstract Logging getLogging();
		
}
