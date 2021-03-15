package nl.uu.fi.dwo.mobile.client.ui;

import javax.inject.Provider;

import nl.uu.fi.dwo.mobile.client.sco.Scorm2004IF;
//import nl.uu.fi.dwo.mobile.client.sco.SCORM_DWOmAccess;
//import nl.uu.fi.dwo.mobile.client.sco.SCORM_guest;
import nl.uu.fi.dwo.mobile.client.ui.views.GotoController;
import nl.uu.fi.dwo.mobile.client.ui.views.HeaderView;
import nl.uu.fi.dwo.mobile.client.ui.views.HeaderViewNone;
import nl.uu.fi.dwo.mobile.client.ui.views.LoginView;
import nl.uu.fi.dwo.mobile.client.ui.views.NavigationView;
import nl.uu.fi.dwo.mobile.client.ui.views.NavigationViewNumworx;
import nl.uu.fi.dwo.mobile.client.ui.views.NoCourseView;
import nl.uu.fi.dwo.mobile.client.ui.views.TreeModuleView;
import nl.uu.fi.dwo.mobile.client.ui.views.TreeModuleViewNumworx;
import nl.uu.fi.dwo.mobile.client.ui.views.ViewModuleView;
import nl.uu.fi.dwo.mobile.client.ui.views.ViewModuleViewBuilder;
import nl.uu.fi.dwo.rest.dom.entities.DomClassCourse;
import nl.uu.fi.dwo.rest.persistence.PersistenceClassType;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;

import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Promises;
import org.osgi.util.promise.Success;

import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.place.shared.PlaceHistoryMapper;
//import com.google.gwt.user.client.ui.IsWidget;
import com.google.web.bindery.event.shared.EventBus;

import dagger.Lazy;

/**
 * @see GWT
 * 
 * @author Danny Hendrix
 * 
 */
public abstract class ClientFactoryImpl implements ClientFactory, GotoController
{
	private final EventBus eventBus;
			
	// singleton pattern.
	Provider<HeaderView> headerView;

	protected void setup(Provider<HeaderViewNone> none, Provider<HeaderView> numworx)
	{
	  if (Actions.isAvailable() )
	  {
	    HeaderViewNone headerViewNone = none.get();
	    headerViewNone.setPresenter(this);
	    headerView = () -> headerViewNone;
	  } else {
        HeaderView impl = numworx.get();
        impl.setPresenter(this);
	    headerView = () -> impl;
	  }
	};
		
//	final Provider<NavigationViewNumworx> navigationView;
	
//	public NavigationView getNavigationView() {
//		NavigationViewNumworx view = navigationView.get();
//		view.setPresenter(this);
//		return view;
//	}
	
	
	private final PlaceController placeController;
	private Provider<ViewModuleView> entryView;
	private RPCHandler handler;
	
	public ClientFactoryImpl(EventBus bus, PlaceController controller, 
	                 final Provider<ViewModuleViewBuilder> entryView	                 
	       )
	{
	  this.eventBus = bus;
	  this.placeController = controller;
	  this.entryView = () -> {
	    ViewModuleViewBuilder view = entryView.get();
	    view.initialize(setupAPI());
	    return view;
	  };
	}

	protected abstract Scorm2004IF setupAPI();

	@Override
	public EventBus getEventBus()
	{
		return eventBus;
	}

	@Override
	public ViewModuleView getEntryView()
	{
		ViewModuleView view = entryView.get();
		return view;
	}

	public HeaderView getHeaderView() {
		return headerView.get();
	}
	
	
//	@Override
//	public LoginView getLoginView()
//	{
//		if (loginView == null)
//			loginView = new LoginViewImpl();
//		return loginView;
//	}


	@Override
	public RPCHandler getRPCHandler() {
		return this.handler;
	}

	protected void setRPCHandler(RPCHandler handler) {
		this.handler = handler;
	}
	
//	public SCORM_guest setupAPI() {
//		SCORM_guest api;
//		if(!withUser()) {
//			api = new SCORM_guest();
//		} else {
//			Integer userID = (Integer) getUserID();
//			api = new SCORM_DWOmAccess(userID.intValue());
//		}
//		return api;
//	}
		
		
	protected static PersistenceId idOf(Object object, PersistenceClassType type) {
		if(object == null || "".equals(object))
				return null;
		PersistenceId id = new PersistenceId();
		id.setIdString("MYSQL;" + type + ";" + object);
		return id;
	}


	private DomClassCourse exam;
	
	
	@Override
	public Promise<Void> startExam(final DomClassCourse classCourse, final String password) {
		Promise<Void> p = barrier()
			.then(new Success<Void, Void>() {

					@Override
					public Promise<Void> call(Promise<Void> resolved)
							throws Exception {
						return getRPCHandler().startExam(classCourse.getId().toString(), password);
					}
			})
			.then(new Success<Void, Void>() {

				@Override
				public Promise<Void> call(Promise<Void> resolved)
						throws Exception {
					exam = classCourse;
					return null;
				}
			});
		addBarrier(p); // 
		return p;
	}
	
	public boolean inExam(DomClassCourse classCourse) {
		return (exam != null) &&
			exam.getId().equals(classCourse.getId());
	}

	@Override
	public void goTo(Place place) {
		placeController.goTo(place);
	}	
	
}
