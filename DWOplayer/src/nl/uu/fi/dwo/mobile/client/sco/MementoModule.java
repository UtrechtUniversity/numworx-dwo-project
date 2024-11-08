package nl.uu.fi.dwo.mobile.client.sco;

import javax.annotation.Nullable;
import javax.inject.Named;
import javax.inject.Provider;

import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.ResettableEventBus;

import dagger.Module;
import dagger.Provides;
import nl.uu.fi.dwo.mobile.client.dagger.ActivityScope;
import nl.uu.fi.dwo.mobile.client.ui.ActivityComponent;

@Module
public class MementoModule {

	@ActivityScope @Provides @Nullable protected Memento memento(ActivityComponent a, @Named("API") Provider<Scorm2004IF> api) {
		return new Memento(a, api.get());
	}
	
	@ActivityScope @Named("API") @Provides protected Scorm2004IF api(@Named("parentAPI") Provider<Scorm2004IF> api) { return api.get(); }

	@ActivityScope @Provides static protected ResettableEventBus eventbus(EventBus bus) { return new ResettableEventBus(bus); }
}
