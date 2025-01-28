package nl.uu.fi.dwo.lms.gwtclient.gwt.dagger;

import javax.inject.Named;
import javax.inject.Singleton;

import com.google.gwt.user.client.Window;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.ResettableEventBus;
import com.google.web.bindery.event.shared.SimpleEventBus;

import dagger.Binds;
import dagger.BindsOptionalOf;
import dagger.Module;
import dagger.Provides;
import dagger.Reusable;
import fi.dwo.gwt.lib.rest.CallManagers.OAuthManager;
import fi.dwo.gwt.lib.rest.CallManagers.SecuredStudentStudentModelManager;
import fi.dwo.gwt.lib.rest.CallManagers.SecuredTeacherSchoolClassManager;
import fi.dwo.gwt.lib.rest.CallManagers.SecuredUserAccountManager;
import fi.dwo.gwt.lib.rest.ui.IdleDetect;
//import nl.uu.fi.dwo.ideas.client.IdeasClient;
//import nl.uu.fi.dwo.ideas.client.IdeasIF;
import nl.uu.fi.dwo.lms.gwtclient.gwt.DwoGlobalVars;
import nl.uu.fi.dwo.lms.gwtclient.gwt.ViewFactory;
import nl.uu.fi.dwo.lms.gwtclient.gwt.ViewFactoryJs;
import nl.uu.fi.dwo.lms.gwtclient.gwt.chatbox.ChatboxPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.jsdisplays.chatbox.JsChatboxView;
import nl.uu.fi.dwo.lms.gwtclient.gwt.jsdisplays.modules.JsModulesView;
import nl.uu.fi.dwo.lms.gwtclient.gwt.jsdisplays.organisation.JsOrganisationView;
import nl.uu.fi.dwo.lms.gwtclient.gwt.jsdisplays.results.JsLogResultsView;
import nl.uu.fi.dwo.lms.gwtclient.gwt.jsdisplays.results.JsStudentResultsView;
import nl.uu.fi.dwo.lms.gwtclient.gwt.modules.ModulesPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.organisation.OrganisationPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.results.LogResultsPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.studentresults.StudentResultsPresenter;
import nl.uu.fi.dwo.rest.dom.entities.DomContext;

@Module(subcomponents = { TeacherComponent.class, SchoolAdminComponent.class, GuestComponent.class, StudentComponent.class })
abstract class BootModule {

    @Singleton @Provides static SimpleEventBus simpleEventBus() { // for Singleton eventHandlers
      return new SimpleEventBus();
    }
  
    @Singleton @Provides static IdleDetect idle(SimpleEventBus bus) {
    	return new IdleDetect(bus);
    }
    
	@Singleton @Provides static ResettableEventBus resettableEventBus(SimpleEventBus bus) {
		return new ResettableEventBus(bus);
	}

	@BindsOptionalOf abstract EventBus optionalEventBus();
	@BindsOptionalOf abstract nl.uu.fi.dwo.lms.gwtclient.gwt.persons.PersonsService optionalPersonsService();
	@Provides @Named("test") static boolean test(DwoGlobalVars vars) { return vars.isTest(); }

	@Singleton
	@Provides @Named("responsive") static boolean responsive() {
		return "true".equals(Window.Location.getParameter("responsive")) || getResponsive(); 
	}
	private static native boolean getResponsive() /*-{
		return $wnd.modernstyle;
	}-*/;
	
	
	@Binds abstract EventBus eventBus(ResettableEventBus bus); // for RoleScope eventHandlers
	@Binds abstract ViewFactory viewFactory(ViewFactoryJs view);
	
	@Reusable @Provides static SecuredUserAccountManager accountManager() {
	  return new SecuredUserAccountManager();
	}
	@Reusable @Provides static OAuthManager oauthManager() {
		return new OAuthManager();
	}
	
	@Reusable @Provides static SecuredTeacherSchoolClassManager schoolClassManager() {
	  return new SecuredTeacherSchoolClassManager();
	}
	@Reusable @Provides static SecuredStudentStudentModelManager studentStudenModelManager() {
		return new SecuredStudentStudentModelManager();
	}

//	@Provides @Singleton static IdeasIF ideas() {
//		return new IdeasClient("/ideas/IdeasServlet");
//	}
	
	@Provides @Reusable static DomContext context(DwoGlobalVars vars) {
	    DomContext context = new DomContext();
	    context.setDomHasRole(vars.getActiveSchoolRoleAndClass().getHasRole());
	    context.setRealm(vars.getCurrentLoginContext().getRealm());
	    return context;
	}

//	@IntoMap
//	@RoleKey(RoleType.ANONYMOUS)
//	@Provides
//	static PresenterBuilder guest( GuestComponent.Builder builder ) {
//	  return builder.build();
//	}
	
	
// Binds Views to Presenters
	@Binds abstract LogResultsPresenter.Display logResultsView(JsLogResultsView view);
	@Binds abstract ModulesPresenter.Display modulesView(JsModulesView view);
	@Binds abstract OrganisationPresenter.Display organisationView(JsOrganisationView view);
	@Binds abstract StudentResultsPresenter.Display studentResultsView(JsStudentResultsView view);
	@Binds abstract ChatboxPresenter.Display chatboxView(JsChatboxView view);
}
