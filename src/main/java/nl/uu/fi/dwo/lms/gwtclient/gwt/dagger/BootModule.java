package nl.uu.fi.dwo.lms.gwtclient.gwt.dagger;

import javax.inject.Singleton;

import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.SimpleEventBus;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import nl.uu.fi.dwo.lms.gwtclient.gwt.PresenterFactory;
import nl.uu.fi.dwo.lms.gwtclient.gwt.PresenterFactoryGwt;
import nl.uu.fi.dwo.lms.gwtclient.gwt.ViewFactory;
import nl.uu.fi.dwo.lms.gwtclient.gwt.ViewFactoryJs;

@Module
abstract class BootModule {
	@Singleton @Provides static EventBus eventBus() {
		return new SimpleEventBus();
	}
	
	@Binds abstract ViewFactory viewFactory(ViewFactoryJs view);
	@Binds abstract PresenterFactory presenterFactory(PresenterFactoryGwt factory);

}
