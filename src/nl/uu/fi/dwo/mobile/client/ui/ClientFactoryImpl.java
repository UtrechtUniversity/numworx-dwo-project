package nl.uu.fi.dwo.mobile.client.ui;

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
import nl.uu.fi.dwo.rest.dom.entities.DomSchool;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.RoleType;
import nl.uu.fi.dwo.rest.persistence.PersistenceClassType;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;

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
	private final EventBus eventBus = new SimpleEventBus();
	private final PlaceController placeController = new PlaceController(eventBus);
	private ViewModuleView entryView;
	private SelectModuleView selectModuleView;
	protected LoginViewImpl loginView;
	private TreeModuleView treeModuleView;
	private RPCHandler handler;
	private IsWidget logoutWidget;

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
		if(entryView == null)
		{
			ViewModuleViewImpl impl = new ViewModuleViewImpl(true);
			entryView = impl.initialize();
			impl.zetMaat();
		}
		entryView.setApi(DWOplayer.api);
		return entryView;
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
			if(false)
				return this.treeModuleView = new TreeModuleViewNumworx();
			
			
			
			
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
		if(this.handler == null)
			this.handler = new nl.uu.fi.dwo.mobile.client.ui.activities.RPCHandler();
		return this.handler;
	}

	public void setRPCHandler(RPCHandler handler) {
		this.handler = handler;
	}

	@Override
	public void setEntryView(ViewModuleView view) {
		entryView = view;
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
	public void logout() {
		DWOplayer.profiledata = null;
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
	
}
