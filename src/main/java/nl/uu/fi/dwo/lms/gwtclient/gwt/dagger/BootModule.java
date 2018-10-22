package nl.uu.fi.dwo.lms.gwtclient.gwt.dagger;

import javax.inject.Singleton;

import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.SimpleEventBus;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import dagger.Reusable;
import fi.dwo.gwt.lib.rest.CallManagers.SecuredTeacherSchoolClassManager;
import fi.dwo.gwt.lib.rest.CallManagers.SecuredUserAccountManager;
import nl.uu.fi.dwo.lms.gwtclient.gwt.PresenterFactory;
import nl.uu.fi.dwo.lms.gwtclient.gwt.PresenterFactoryGwt;
import nl.uu.fi.dwo.lms.gwtclient.gwt.ViewFactory;
import nl.uu.fi.dwo.lms.gwtclient.gwt.ViewFactoryJs;
import nl.uu.fi.dwo.lms.gwtclient.gwt.jsdisplays.results.JsLogResultsView;
import nl.uu.fi.dwo.lms.gwtclient.gwt.results.LogResultsPresenter;

@Module
abstract class BootModule {
	@Singleton @Provides static EventBus eventBus() {
		return new SimpleEventBus();
	}
	@Binds abstract ViewFactory viewFactory(ViewFactoryJs view);
	@Binds abstract PresenterFactory presenterFactory(PresenterFactoryGwt factory);
	
	@Reusable @Provides static SecuredUserAccountManager accountManager() {
	  return new SecuredUserAccountManager();
	}
	
	@Reusable @Provides static SecuredTeacherSchoolClassManager schoolClassManager() {
	  return new SecuredTeacherSchoolClassManager();
	}
	
// Binds Views to Presenters
	@Binds abstract LogResultsPresenter.Display logResultsView(JsLogResultsView view);
	
}
