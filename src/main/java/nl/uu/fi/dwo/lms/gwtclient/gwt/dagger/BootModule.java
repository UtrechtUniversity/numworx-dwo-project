package nl.uu.fi.dwo.lms.gwtclient.gwt.dagger;

import javax.inject.Singleton;

import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.ResettableEventBus;
import com.google.web.bindery.event.shared.SimpleEventBus;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import dagger.Reusable;
import dagger.multibindings.IntoMap;
import fi.dwo.gwt.lib.rest.CallManagers.SecuredTeacherSchoolClassManager;
import fi.dwo.gwt.lib.rest.CallManagers.SecuredUserAccountManager;
import nl.uu.fi.dwo.lms.gwtclient.gwt.PresenterFactory;
import nl.uu.fi.dwo.lms.gwtclient.gwt.PresenterFactoryGwt;
import nl.uu.fi.dwo.lms.gwtclient.gwt.ViewFactory;
import nl.uu.fi.dwo.lms.gwtclient.gwt.ViewFactoryJs;
import nl.uu.fi.dwo.lms.gwtclient.gwt.jsdisplays.results.JsLogResultsView;
import nl.uu.fi.dwo.lms.gwtclient.gwt.results.LogResultsPresenter;
import nl.uu.fi.dwo.rest.dom.entities.RoleType;

@Module(subcomponents = { TeacherComponent.class, SchoolAdminComponent.class, GuestComponent.class })
abstract class BootModule {
	@Singleton @Provides static ResettableEventBus resettableEventBus() {
		return new ResettableEventBus(new SimpleEventBus());
	}

	@Binds abstract EventBus eventBus(ResettableEventBus bus);
	@Binds abstract ViewFactory viewFactory(ViewFactoryJs view);
	
	@Reusable @Provides static SecuredUserAccountManager accountManager() {
	  return new SecuredUserAccountManager();
	}
	
	@Reusable @Provides static SecuredTeacherSchoolClassManager schoolClassManager() {
	  return new SecuredTeacherSchoolClassManager();
	}
	
	@IntoMap
	@RoleKey(RoleType.ANONYMOUS)
	@Provides
	static PresenterBuilder guest( GuestComponent.Builder builder ) {
	  return builder.build();
	}
	
	
// Binds Views to Presenters
	@Binds abstract LogResultsPresenter.Display logResultsView(JsLogResultsView view);
	
}
