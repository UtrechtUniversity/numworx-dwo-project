package nl.uu.fi.dwo.mobile.client.ui;

import javax.inject.Provider;

import nl.uu.fi.dwo.mobile.DWOplayer;
import nl.uu.fi.dwo.mobile.client.sco.SCORM_DWOmAccess;
import nl.uu.fi.dwo.mobile.client.sco.SCORM_guest;
import nl.uu.fi.dwo.mobile.client.ui.views.LoginView;
import nl.uu.fi.dwo.mobile.client.ui.views.LoginViewImpl;
import nl.uu.fi.dwo.mobile.client.ui.views.SelectModuleView;
import nl.uu.fi.dwo.mobile.client.ui.views.SelectModuleViewImpl;
import nl.uu.fi.dwo.mobile.client.ui.views.TreeModuleView;
import nl.uu.fi.dwo.mobile.client.ui.views.TreeModuleViewImplDesktop;
import nl.uu.fi.dwo.mobile.client.ui.views.TreeModuleViewImplTablet;
import nl.uu.fi.dwo.mobile.client.ui.views.TreeModuleViewNumworx;
import nl.uu.fi.dwo.mobile.client.ui.views.ViewModuleView;
import nl.uu.fi.dwo.mobile.client.ui.views.ViewModuleViewImpl;
import nl.uu.fi.dwo.mobile.client.ui.views.ViewModuleViewNumworx;
import nl.uu.fi.dwo.rest.dom.entities.DomClassCourse;
import nl.uu.fi.dwo.rest.dom.entities.DomSchool;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.RoleType;
import nl.uu.fi.dwo.rest.persistence.PersistenceClassType;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;

import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Promises;
import org.osgi.util.promise.Success;

import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.SimpleEventBus;
import com.googlecode.mgwt.ui.client.MGWT;
import com.googlecode.mgwt.ui.client.OsDetection;

/**
 * @see GWT
 * 
 * @author Danny Hendrix
 * 
 */
public class ClientFactoryImpl implements ClientFactory
{
	
	final static Provider<ViewModuleView> NORMAL = new Provider<ViewModuleView>() {

		@Override
		public ViewModuleView get() {
			ViewModuleViewImpl impl = new ViewModuleViewImpl(true);
			impl.initialize();
			impl.zetMaat();
			return impl;
		}};
	
	final static Provider<ViewModuleView> NUMWORX_VIEW = new Provider<ViewModuleView>() {

		@Override
		public ViewModuleView get() {
			ViewModuleViewNumworx impl = new ViewModuleViewNumworx();
			return impl.initialize();
		}
		
	};
	
	class ViewModuleHolder implements Provider<ViewModuleView> {
		final ViewModuleView hold;

		@Override
		public ViewModuleView get() {
			return hold;
		}

		public ViewModuleHolder(ViewModuleView hold) {
			this.hold = hold;
		}
		
	}
		
	
	private final EventBus eventBus = new SimpleEventBus();
	private final PlaceController placeController = new PlaceController(eventBus);
	private Provider<ViewModuleView> entryView = NORMAL;
	private SelectModuleView selectModuleView;
	protected LoginView loginView;
	protected TreeModuleView treeModuleView;
	private RPCHandler handler;
	private IsWidget logoutWidget;
	
	protected final static boolean NUMWORX = true;

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
		view.setApi(setupAPI());
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
			if(NUMWORX)
			{
				
				entryView = NUMWORX_VIEW;				
				return this.treeModuleView = new TreeModuleViewNumworx();
			}
			
			
			
			
			OsDetection detection = MGWT.getOsDetection();
			if(detection.isDesktop()
					//&& false
					//|| true
					) {
				this.treeModuleView = new TreeModuleViewImplDesktop();
			} else {
				this.treeModuleView = new TreeModuleViewImplTablet();
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

	@Override
	public void setEntryView(ViewModuleView view) {
		entryView = new ViewModuleHolder(view);
	}
	
	public SCORM_guest setupAPI() {
		SCORM_guest api;
		if(!withUser()) {
			api = new SCORM_guest();
		} else {
			Integer userID = (Integer) getUserID();
			api = new SCORM_DWOmAccess(userID.intValue());
		}
		return api;
	}
	
	@Override
	public IsWidget getMenuWidget() {
		return null;
	}

	public IsWidget getLogoutWidget() {
		return logoutWidget;
	}

	public void setLogoutWidget(IsWidget logoutWidget) {
		this.logoutWidget = logoutWidget;
	}
	
	@Override
	public Promise<Void> logout() {
		DWOplayer.profiledata = null;
		return Promises.resolved(null);
	}

	public boolean withUser() {
		return DWOplayer.profiledata != null;
	}
	
	// NOT NULL, "" als null
	@SuppressWarnings("deprecation")
	public Object getSchoolID() {
		return DWOplayer.profiledata.get("schoolID");
	}

	public DomSchool getSchool() {
		if (getSchoolID() == null || getSchoolID() .equals("")) return null;
		DomSchool school = new DomSchool();
		school.setId(idOf(getSchoolID(), PersistenceClassType.PersistentSchool));
		school.setSchoolName((String)getSchoolName());
		return school;
	}
	
	public Object getClassID() {
		return DWOplayer.profiledata.get("classID");
	}
	
	public DomSchoolClass getSchoolClass() {
		if(getClassID() == null) return null;
		DomSchoolClass cls = new DomSchoolClass();
		cls.setId(idOf(getClass(), PersistenceClassType.PersistentSchoolClass));
		return cls;
	}
	
	protected static PersistenceId idOf(Object object, PersistenceClassType type) {
		if(object == null || "".equals(object))
				return null;
		PersistenceId id = new PersistenceId();
		id.setIdString("MYSQL;" + type + ";" + object);
		return id;
	}


	@Override
	public boolean isIconizer() {
		return Boolean.TRUE.equals(DWOplayer.profiledata.get("iconizer"));
	}

	@Override
	public RoleType getRoleType() {
		try {
			return RoleType.valueOf((String)DWOplayer.profiledata.get("groupname"));
		} catch (Exception e) {
			return RoleType.NONE;
		}
	}

	@Override
	public Object getUserID() {
		return DWOplayer.profiledata.get("userID");
	}

	public Object getSchoolName() {
		return DWOplayer.profiledata.get("schoolName");
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
	
}
