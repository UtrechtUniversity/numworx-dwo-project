package nl.uu.fi.dwo.mobile.client.ui.views;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import nl.uu.fi.dwo.mobile.DWOplayer;
import nl.uu.fi.dwo.mobile.client.ui.SelectModuleItem;
import nl.uu.fi.dwo.mobile.client.ui.SelectModuleItemHolder;
import nl.uu.fi.dwo.mobile.client.ui.places.LoginPlace;
import nl.uu.fi.dwo.mobile.client.ui.places.SelectModulePlace;
import nl.uu.fi.dwo.mobile.client.ui.places.TreeModulePlace;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.googlecode.mgwt.dom.client.event.tap.TapEvent;
import com.googlecode.mgwt.dom.client.event.tap.TapHandler;
import com.googlecode.mgwt.dom.client.event.touch.TouchStartEvent;
import com.googlecode.mgwt.dom.client.event.touch.TouchStartHandler;
import com.googlecode.mgwt.ui.client.widget.Button;
import com.googlecode.mgwt.ui.client.widget.HeaderButton;
import com.googlecode.mgwt.ui.client.widget.HeaderPanel;
import com.googlecode.mgwt.ui.client.widget.LayoutPanel;
import com.googlecode.mgwt.ui.client.widget.WidgetList;

public class ProfileViewImpl implements ProfileView
{
	
	private static final AsyncCallback<List<Map<String,Object>>> GETCOURSES_CALLBACK = new AsyncCallback<List<Map<String,Object>>>() {

		@Override
		public void onFailure(Throwable caught) {
			Window.alert(caught.toString());
		}

		@Override
		public void onSuccess(List<Map<String,Object>> result) {
			SelectModuleItemHolder.getItems().clear(); // FIXME hier leegmaken of elders?
			for (Iterator<Map<String, Object>> iterator = result.iterator(); iterator.hasNext();) {
				Map<String, Object> map = (Map<String, Object>) iterator.next();
				SelectModuleItem item = new SelectModuleItem(map, SelectModuleItem.Type.MODULE);
				SelectModuleItemHolder.insert(item);
			}
			
			DWOplayer.clientfactory.getPlaceController().goTo(new TreeModulePlace("0"));
		}
		
	};
	
	
	
	private LayoutPanel main;
	private Label school;
	private Label username;
	private Label name;
	private Label userid;

	
	
	
	public ProfileViewImpl()
	{
		main = new LayoutPanel();

		HeaderPanel header = new HeaderPanel();
		header.setCenter("Login");
		main.add(header);

		HeaderButton hb = new HeaderButton();
		hb.setBackButton(true);
		hb.setText("Logout");

		hb.addTapHandler(new TapHandler()
		{

			@Override
			public void onTap(TapEvent event)
			{
				DWOplayer.profiledata = null;
				DWOplayer.clientfactory.getPlaceController().goTo(new LoginPlace("Login"));
			}
		});

		header.setLeftWidget(hb);

		//create details list
		WidgetList list = new WidgetList();
		main.add(list);

		username = new Label("Username");
		username.getElement().addClassName("listitem");
		list.add(username);

		name = new Label("Name");
		name.getElement().addClassName("listitem");
		list.add(name);

		userid = new Label("Userid");
		userid.getElement().addClassName("listitem");
		list.add(userid);

		school = new Label("School");
		school.getElement().addClassName("listitem");
		list.add(school);

		Button submitbutton = new Button();
		submitbutton.setText("Selecteer module");
		submitbutton.setWidth("300px");
		submitbutton.getElement().getStyle().setProperty("margin", "auto");
		submitbutton.addTouchStartHandler(new TouchStartHandler()
		{

			@Override
			public void onTouchStart(TouchStartEvent event)
			{
				DWOplayer.clientfactory.getRPCHandler().getCourses(DWOplayer.profiledata, GETCOURSES_CALLBACK);
			}
		});

		list.add(submitbutton);
	}

	@Override
	public Widget asWidget()
	{
		return main;
	}

	@Override
	public void setupModule()
	{
		String user_name = DWOplayer.profiledata.get("firstname").toString();
		if (DWOplayer.profiledata.get("middlename").toString().equals("") == false)
			user_name += " " + DWOplayer.profiledata.get("middlename").toString();
		user_name += " " + DWOplayer.profiledata.get("lastname").toString();

		name.setText("Naam: " + user_name);
		username.setText("Gebruikersnaam: " + DWOplayer.profiledata.get("username").toString());
		userid.setText("GebruikersID: " + DWOplayer.profiledata.get("userID").toString());
		school.setText("School: " + DWOplayer.profiledata.get("schoolName").toString());
	}

}
