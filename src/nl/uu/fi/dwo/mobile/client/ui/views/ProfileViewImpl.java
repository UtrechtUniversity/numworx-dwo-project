package nl.uu.fi.dwo.mobile.client.ui.views;


import java.util.Map;

import nl.uu.fi.dwo.mobile.DWOplayer;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.googlecode.mgwt.dom.client.event.tap.HasTapHandlers;
import com.googlecode.mgwt.ui.client.widget.Button;
import com.googlecode.mgwt.ui.client.widget.HeaderButton;

public class ProfileViewImpl extends Composite implements ProfileView
{
	

	@UiField Label school;
	@UiField Label username;
	@UiField Label name;
	@UiField Label userid;
	@UiField Label schoolKlas;
	@UiField HeaderButton logoutBtn;
	@UiField Button submitBtn;

	public HasTapHandlers getLogoutBtn() {
		return logoutBtn;
	}
	
	public HasTapHandlers getSubmitBtn() {
		return submitBtn;
	}
	
	private static ProfileViewImplUiBinder uiBinder = GWT
			.create(ProfileViewImplUiBinder.class);

	interface ProfileViewImplUiBinder extends
			UiBinder<Widget, ProfileViewImpl> {
	}
	
	public ProfileViewImpl()
	{
	
		initWidget(uiBinder.createAndBindUi(this));
	}


	@Override
	public void setupModule()
	{
		// FIXME, Maybe can get of these widgets and use the HTML
		// Elements instead .... should be faster then widgets
		// according to : http://dl.google.com/io/2009/pres/W_1230_MeasureinMilliseconds-PerformanceTipsforGoogleWebToolkit.pdf
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
