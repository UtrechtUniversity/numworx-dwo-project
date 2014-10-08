package nl.uu.fi.dwo.mobile.client.ui;

import nl.uu.fi.dwo.mobile.client.ui.activities.RPCHandler;
import nl.uu.fi.dwo.mobile.client.ui.views.LoginView;
import nl.uu.fi.dwo.mobile.client.ui.views.LoginViewImpl;
import nl.uu.fi.dwo.mobile.client.ui.views.ProfileView;
import nl.uu.fi.dwo.mobile.client.ui.views.ProfileViewImpl;
import nl.uu.fi.dwo.mobile.client.ui.views.SelectModuleView;
import nl.uu.fi.dwo.mobile.client.ui.views.SelectModuleViewImpl;
import nl.uu.fi.dwo.mobile.client.ui.views.TreeModuleView;
import nl.uu.fi.dwo.mobile.client.ui.views.TreeModuleViewImpl;
import nl.uu.fi.dwo.mobile.client.ui.views.TreeModuleViewImplDesktop;
import nl.uu.fi.dwo.mobile.client.ui.views.TreeModuleViewImplTablet;
import nl.uu.fi.dwo.mobile.client.ui.views.ViewModuleView;
import nl.uu.fi.dwo.mobile.client.ui.views.ViewModuleViewImpl;

import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.PlaceController;
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
	private LoginViewImpl loginView;
	private ProfileView profileView;
	private TreeModuleView treeModuleView;
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
		if(entryView == null)
		{
			ViewModuleViewImpl impl = new ViewModuleViewImpl(true);
			entryView = impl.initialize();
			impl.zetMaat();
		}
		
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
	public ProfileView getProfileView()
	{
		if (profileView == null)
			profileView = new ProfileViewImpl();
		return profileView;
	}

	@Override
	public TreeModuleView getTreeModuleView()
	{
		if (treeModuleView == null){
			OsDetection detection = MGWT.getOsDetection();
			if(detection.isDesktop()
					//&& false
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
			this.handler = new RPCHandler();
		return this.handler;
	}

}
