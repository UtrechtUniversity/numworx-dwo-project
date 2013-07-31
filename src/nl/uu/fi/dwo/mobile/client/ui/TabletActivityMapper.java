package nl.uu.fi.dwo.mobile.client.ui;

import nl.uu.fi.dwo.mobile.DWOplayer;
import nl.uu.fi.dwo.mobile.client.ui.activities.LoginActivity;
import nl.uu.fi.dwo.mobile.client.ui.activities.ProfileActivity;
import nl.uu.fi.dwo.mobile.client.ui.activities.SelectModuleActivity;
import nl.uu.fi.dwo.mobile.client.ui.activities.TreeModuleActivity;
import nl.uu.fi.dwo.mobile.client.ui.activities.ViewModuleActivity;
import nl.uu.fi.dwo.mobile.client.ui.places.LoginPlace;
import nl.uu.fi.dwo.mobile.client.ui.places.ProfilePlace;
import nl.uu.fi.dwo.mobile.client.ui.places.SelectModulePlace;
import nl.uu.fi.dwo.mobile.client.ui.places.TreeModulePlace;
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
		this.clientFactory = cf;
	}

	@Override
	public Activity getActivity(Place place)
	{
		if (place instanceof SelectModulePlace)
		{
			SelectModulePlace tmp = (SelectModulePlace) place;
			int id = Integer.parseInt(tmp.getToken());
			SelectModuleItem item = SelectModuleItemHolder.getItemByID(id);			
			return new SelectModuleActivity(clientFactory, item);
		}
		if (place instanceof ViewModulePlace)
			return new ViewModuleActivity(clientFactory);
		if (place instanceof LoginPlace)
			return new LoginActivity(clientFactory);
		if (place instanceof ProfilePlace)
		{
			if(DWOplayer.profiledata == null)
				return new LoginActivity(clientFactory);
			return new ProfileActivity(clientFactory);
		}
		if (place instanceof TreeModulePlace)
		{
			TreeModulePlace tmp = (TreeModulePlace) place;
			int id = Integer.parseInt(tmp.getToken());
			SelectModuleItem item = SelectModuleItemHolder.getItemByID(id);
			if(item == null)
				return new LoginActivity(clientFactory);
			return new TreeModuleActivity(clientFactory, item); // Anders geen activity reset action;
		}
		return null;
	}

}
