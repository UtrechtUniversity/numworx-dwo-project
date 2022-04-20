package nl.uu.fi.dwo.lms.gwtclient.gwt.dagger;

import javax.inject.Named;

import com.google.web.bindery.event.shared.EventBus;

import dagger.Binds;
import dagger.BindsOptionalOf;
import dagger.Module;
import dagger.Provides;
import nl.uu.fi.dwo.lms.gwtclient.gwt.results.SMDescriptionService;
import nl.uu.fi.dwo.lms.gwtclient.gwt.studentresults.DescriptionService;

@Module
abstract class StudentModelModule {
	@Binds abstract DescriptionService description(SMDescriptionService sm);
	@BindsOptionalOf abstract EventBus eventBus();
	@Provides @Named("test") static boolean test() { return false; }
}
