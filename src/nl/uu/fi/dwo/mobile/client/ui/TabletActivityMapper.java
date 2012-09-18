package nl.uu.fi.dwo.mobile.client.ui;

import nl.uu.fi.dwo.mobile.client.ui.activities.LoginActivity;
import nl.uu.fi.dwo.mobile.client.ui.activities.ProfileActivity;
import nl.uu.fi.dwo.mobile.client.ui.activities.SelectModuleActivity;
import nl.uu.fi.dwo.mobile.client.ui.activities.ViewModuleActivity;
import nl.uu.fi.dwo.mobile.client.ui.places.LoginPlace;
import nl.uu.fi.dwo.mobile.client.ui.places.ProfilePlace;
import nl.uu.fi.dwo.mobile.client.ui.places.SelectModulePlace;
import nl.uu.fi.dwo.mobile.client.ui.places.ViewModulePlace;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.Place;

/**
 * @see GWT
 * @author Danny Hendrix
 * 
 */
public class TabletActivityMapper implements ActivityMapper
{
	private ClientFactory clientFactory;

	public TabletActivityMapper(ClientFactory cf)
	{
		super();
		this.clientFactory = cf;
	}

	@Override
	public Activity getActivity(Place place)
	{
		if (place instanceof SelectModulePlace)
			return new SelectModuleActivity(clientFactory);
		if (place instanceof ViewModulePlace)
			return new ViewModuleActivity(clientFactory);
		if (place instanceof LoginPlace)
			return new LoginActivity(clientFactory);
		if (place instanceof ProfilePlace)
			return new ProfileActivity(clientFactory);
		return null;
	}

}
