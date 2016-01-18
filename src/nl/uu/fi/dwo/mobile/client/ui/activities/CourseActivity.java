package nl.uu.fi.dwo.mobile.client.ui.activities;

import java.util.Map;

import nl.uu.fi.dwo.mobile.DWOplayer;
import nl.uu.fi.dwo.mobile.client.sco.SCORM_DWOmAccess;
import nl.uu.fi.dwo.mobile.client.sco.SCORM_MC2mAccess;
import nl.uu.fi.dwo.mobile.client.sco.SCORM_guest;
import nl.uu.fi.dwo.mobile.client.ui.ClientFactory;
import nl.uu.fi.dwo.mobile.client.ui.SelectModuleItem;
import nl.uu.fi.dwo.mobile.client.ui.places.LoginPlace;
import nl.uu.fi.dwo.mobile.client.ui.places.SelectModulePlace;
import nl.uu.fi.dwo.mobile.client.ui.places.ViewModulePlace;
import nl.uu.fi.dwo.mobile.client.ui.views.SelectModuleView;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.googlecode.mgwt.dom.client.event.tap.TapEvent;
import com.googlecode.mgwt.dom.client.event.tap.TapHandler;
import com.googlecode.mgwt.mvp.client.MGWTAbstractActivity;
import com.googlecode.mgwt.ui.client.widget.celllist.CellSelectedEvent;
import com.googlecode.mgwt.ui.client.widget.celllist.CellSelectedHandler;

public class CourseActivity extends MGWTAbstractActivity implements Activity {

	private ClientFactory clientFactory;
	private SelectModuleItem item;

	public CourseActivity(ClientFactory clientFactory, SelectModuleItem item) {
		this.clientFactory = clientFactory;
		this.item = item;
	}
	@Override
	public void start(AcceptsOneWidget panel, EventBus eventBus)
	{
		final Map<String, Object> profiledata = DWOplayer.profiledata;
		if(profiledata == null) {
			DWOplayer.api = new SCORM_guest();
		} else {
// FIXME use strategy pattern!
			Object userIDo = profiledata.get("userID");
			if(userIDo instanceof String) {			
				String userID = (String) userIDo;
				String username = (String) profiledata.get("username");
				String fullname = profiledata.get("middlename") + " " + profiledata.get("lastname") + ", " + profiledata.get("firstname");
				fullname = fullname.trim();
				DWOplayer.api = new SCORM_MC2mAccess(userID, username, fullname);
			} else {
				Integer userID = (Integer) userIDo;
				DWOplayer.api = new SCORM_DWOmAccess(userID.intValue());
			}
		}
		
		final SelectModuleView view = clientFactory.getHomeView();
		view.setLogout(true); // terug of logout
		final Place next = 
				new LoginPlace(
						clientFactory.getPlaceController().getWhere());
		
		addHandlerRegistration(view.getList().addCellSelectedHandler(new CellSelectedHandler()
		{
			@Override
			public void onCellSelected(CellSelectedEvent event)
			{
				final SelectModuleItem id = view.getItems().get(event.getIndex());
				clientFactory.getPlaceController().goTo(new ViewModulePlace(id.getID()));
			}
		}));
		addHandlerRegistration(view.getBackBtn().addTapHandler(new TapHandler() {		
			@Override
			public void onTap(TapEvent event) {
				clientFactory.getPlaceController().goTo(next);			
			}
		}));
		
		view.render(item);
		if(item.getName() == null) {
			item.setName("#c:" + item.getID());
			AsyncCallback<Map<String,Object>> getCoursesCallback = 
					new AsyncCallback<Map<String,Object>>() {

						@Override
						public void onFailure(Throwable caught) {
						}

						@Override
						public void onSuccess(Map<String, Object> result) {
							String name = (String) result.get("name");
							String description = (String) result.get("description");
							item.setDescription(description);
							item.setName(name);
							view.setDescription(item);
						}
					};
			clientFactory.getRPCHandler().getCourse(item.getID(), getCoursesCallback);
		}
		view.setDescription(item);
		panel.setWidget(view);
	}
}
