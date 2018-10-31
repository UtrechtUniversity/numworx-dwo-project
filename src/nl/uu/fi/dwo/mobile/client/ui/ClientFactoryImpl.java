package nl.uu.fi.dwo.mobile.client.ui;

import javax.inject.Provider;

//import nl.uu.fi.dwo.mobile.client.sco.SCORM_DWOmAccess;
//import nl.uu.fi.dwo.mobile.client.sco.SCORM_guest;
import nl.uu.fi.dwo.mobile.client.ui.views.GotoController;
import nl.uu.fi.dwo.mobile.client.ui.views.HeaderView;
import nl.uu.fi.dwo.mobile.client.ui.views.HeaderViewNone;
import nl.uu.fi.dwo.mobile.client.ui.views.HeaderViewNumworx;
import nl.uu.fi.dwo.mobile.client.ui.views.LoginView;
import nl.uu.fi.dwo.mobile.client.ui.views.LoginViewImpl;
import nl.uu.fi.dwo.mobile.client.ui.views.NavigationView;
import nl.uu.fi.dwo.mobile.client.ui.views.NavigationViewNumworx;
import nl.uu.fi.dwo.mobile.client.ui.views.NoCourseView;
import nl.uu.fi.dwo.mobile.client.ui.views.SelectModuleView;
import nl.uu.fi.dwo.mobile.client.ui.views.SelectModuleViewImpl;
import nl.uu.fi.dwo.mobile.client.ui.views.TreeModuleView;
import nl.uu.fi.dwo.mobile.client.ui.views.TreeModuleViewNumworx;
import nl.uu.fi.dwo.mobile.client.ui.views.ViewModuleView;
import nl.uu.fi.dwo.mobile.client.ui.views.ViewModuleViewImpl;
import nl.uu.fi.dwo.mobile.client.ui.views.ViewModuleViewNumworx;
import nl.uu.fi.dwo.rest.dom.entities.DomClassCourse;
import nl.uu.fi.dwo.rest.persistence.PersistenceClassType;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;

import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Promises;
import org.osgi.util.promise.Success;

import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
//import com.google.gwt.user.client.ui.IsWidget;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.SimpleEventBus;

/**
 * @see GWT
 * 
 * @author Danny Hendrix
 * 
 */
public abstract class ClientFactoryImpl implements ClientFactory, GotoController
{
	private final EventBus eventBus = new SimpleEventBus();

	final Provider<ViewModuleView> NORMAL = new Provider<ViewModuleView>() {

		@Override
		public ViewModuleView get() {
			ViewModuleViewImpl impl = new ViewModuleViewImpl(true, setupAPI());
			impl.initialize();
			impl.zetMaat();
			return impl;
		}};
	
	final Provider<ViewModuleView> NUMWORX_VIEW = new Provider<ViewModuleView>() {

		@Override
		public ViewModuleView get() {
			ViewModuleViewNumworx impl = new ViewModuleViewNumworx(setupAPI());
			return impl.initialize();
		}
		
	};
			
	// singleton pattern.
	final Provider<HeaderView> headerView;
		
	{
	  if (Actions.isAvailable() )
	  {
	    HeaderViewNone headerViewNone = new HeaderViewNone();
	    headerView = () -> headerViewNone;
	  } else {
        HeaderViewNumworx impl = new HeaderViewNumworx(getEventBus());
        impl.setPresenter(ClientFactoryImpl.this);
	    headerView = () -> impl;
	  }
	};
	
	final Provider<NavigationViewNumworx> navigationView = new Provider<NavigationViewNumworx>() {
		NavigationViewNumworx impl = new NavigationViewNumworx();
		{
			impl.setPresenter(ClientFactoryImpl.this);
			
		}
		@Override
		public NavigationViewNumworx get() {
			return impl;
		}
	};
	
	public NavigationView getNavigationView() {
		return navigationView.get();
	}
	
	
	private final PlaceController placeController = new PlaceController(eventBus);
	private Provider<ViewModuleView> entryView = NORMAL;
	private SelectModuleView selectModuleView;
	protected LoginView loginView;
	protected TreeModuleView treeModuleView;
	private RPCHandler handler;
	
	public ClientFactoryImpl()
	{
	}

	@Override
	public EventBus getEventBus()
	{
		return eventBus;
	}

	@Override
	public PlaceController getPlaceController()
	{
		return placeController;
	}

	@Override
	public ViewModuleView getEntryView()
	{
		ViewModuleView view = entryView.get();
		return view;
	}

	@Override
	public SelectModuleView getHomeView()
	{
		if (selectModuleView == null)
			selectModuleView = /*new SelectModuleTest(); // */new SelectModuleViewImpl();
		return selectModuleView;
	}

	@Override 
	public HeaderView getHeaderView() {
		return headerView.get();
	}
	
	
	@Override
	public LoginView getLoginView()
	{
		if (loginView == null)
			loginView = new LoginViewImpl();
		return loginView;
	}

	@Override
	public TreeModuleView getTreeModuleView()
	{
		if (treeModuleView == null){
			{
				
				entryView = NUMWORX_VIEW;				
				return this.treeModuleView = new TreeModuleViewNumworx(getHeaderView(), navigationView.get());
			}
			
		}
		return this.treeModuleView;
	}

	@Override
	public RPCHandler getRPCHandler() {
		return this.handler;
	}

	public void setRPCHandler(RPCHandler handler) {
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
		
	@Override
	public Promise<Void> logout() {
		return Promises.resolved(null);
	}
		
	protected static PersistenceId idOf(Object object, PersistenceClassType type) {
		if(object == null || "".equals(object))
				return null;
		PersistenceId id = new PersistenceId();
		id.setIdString("MYSQL;" + type + ";" + object);
		return id;
	}


	@Override
	public Promise<Void> barrier() {
		return Promises.resolved(null);
	}

	@Override
	public void addBarrier(Promise<?> p) {
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

  @Override
  public Provider<NoCourseView> getNoCourseView() {
    return new Provider<NoCourseView>() {
      
      @Override
      public NoCourseView get() {
          HeaderView header;
          NavigationView navigation;
          header = getHeaderView();
          navigation = getNavigationView();
          return new NoCourseView(header, navigation);
      }
  };
 }
	
	
}
