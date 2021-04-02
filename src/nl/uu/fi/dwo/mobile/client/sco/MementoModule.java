package nl.uu.fi.dwo.mobile.client.sco;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import nl.uu.fi.dwo.mobile.client.dagger.ActivityScope;
import nl.uu.fi.dwo.mobile.client.ui.ActivityComponent;

@Module
public class MementoModule {

	@ActivityScope @Provides protected Memento memento(ActivityComponent a, @Named("API") Scorm2004IF api) {
		return new Memento(a, api);
	}
	
	@ActivityScope @Named("API") @Provides Scorm2004IF api(@Named("parentAPI") Scorm2004IF api) { return api; }
}
