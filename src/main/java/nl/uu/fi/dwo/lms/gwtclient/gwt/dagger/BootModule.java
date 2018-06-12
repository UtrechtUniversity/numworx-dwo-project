package nl.uu.fi.dwo.lms.gwtclient.gwt.dagger;

import javax.inject.Singleton;

import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.SimpleEventBus;

import dagger.Module;
import dagger.Provides;

@Module
abstract class BootModule {
	@Singleton @Provides static EventBus eventBus() {
		return new SimpleEventBus();
	}
}
