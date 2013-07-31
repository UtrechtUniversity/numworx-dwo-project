package nl.uu.fi.dwo.mobile.client.ui.views;


import java.util.Map;

import nl.uu.fi.dwo.mobile.DWOplayer;
import nl.uu.fi.dwo.mobile.client.ui.places.LoginPlace;
import nl.uu.fi.dwo.mobile.client.ui.places.SelectModulePlace;

import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.googlecode.mgwt.dom.client.event.tap.HasTapHandlers;
import com.googlecode.mgwt.dom.client.event.tap.TapEvent;
import com.googlecode.mgwt.dom.client.event.tap.TapHandler;
import com.googlecode.mgwt.dom.client.event.touch.TouchEndEvent;
import com.googlecode.mgwt.dom.client.event.touch.TouchEndHandler;
import com.googlecode.mgwt.dom.client.event.touch.TouchStartEvent;
import com.googlecode.mgwt.dom.client.event.touch.TouchStartHandler;
import com.googlecode.mgwt.ui.client.widget.Button;
import com.googlecode.mgwt.ui.client.widget.HeaderButton;
import com.googlecode.mgwt.ui.client.widget.HeaderPanel;
import com.googlecode.mgwt.ui.client.widget.LayoutPanel;
import com.googlecode.mgwt.ui.client.widget.WidgetList;

public class ProfileViewImpl implements ProfileView
{
	
	private LayoutPanel main;
	private Label school;
	private Label username;
	private Label name;
	private Label userid;
	private Label schoolKlas;
	private HeaderButton logoutBtn;
	private Button submitBtn;

	public HasTapHandlers getLogoutBtn() {
		return logoutBtn;
	}
	
	public HasTapHandlers getSubmitBtn() {
		return submitBtn;
	}
	
	public ProfileViewImpl()
	{
		main = new LayoutPanel();

		HeaderPanel header = new HeaderPanel();
		header.setCenter("Login");
		main.add(header);

		logoutBtn = new HeaderButton();
		logoutBtn.setBackButton(true);
		logoutBtn.setText("Logout");


		header.setLeftWidget(logoutBtn);

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
		schoolKlas = new Label("Klas");
		schoolKlas.getElement().addClassName("listitem");
		list.add(schoolKlas);

		submitBtn = new Button();
		submitBtn.setText("Selecteer module");
		submitBtn.setWidth("300px");
		submitBtn.getElement().getStyle().setProperty("margin", "auto");
		list.add(submitBtn);
	}

	@Override
	public Widget asWidget()
	{
		return main;
	}

	@Override
	public void setupModule()
	{
		final Map<String, Object> profiledata = DWOplayer.profiledata;
		String user_name = profiledata.get("firstname").toString();
		if (profiledata.get("middlename").toString().equals("") == false)
			user_name += " " + profiledata.get("middlename").toString();
		user_name += " " + profiledata.get("lastname").toString();

		name.setText("Naam: " + user_name);
		username.setText("Gebruikersnaam: " + profiledata.get("username"));
		userid.setText("GebruikersID: " + profiledata.get("userID"));
		school.setText("School: " + profiledata.get("schoolName"));
		schoolKlas.setText("Klas: " + profiledata.get("class"));
	}

}
