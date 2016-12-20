package nl.uu.fi.dwo.mobile.client.ui;

import nl.uu.fi.dwo.mobile.DWOplayer;
import nl.uu.fi.dwo.mobile.client.ui.activities.CourseActivity;
import nl.uu.fi.dwo.mobile.client.ui.activities.FlatModuleActivity;
import nl.uu.fi.dwo.mobile.client.ui.activities.GuestActivity;
import nl.uu.fi.dwo.mobile.client.ui.activities.LoginActivity;
import nl.uu.fi.dwo.mobile.client.ui.activities.ReloginActivity;
import nl.uu.fi.dwo.mobile.client.ui.activities.ScoActivity;
import nl.uu.fi.dwo.mobile.client.ui.activities.SelectModuleActivity;
import nl.uu.fi.dwo.mobile.client.ui.activities.TreeModuleActivity;
import nl.uu.fi.dwo.mobile.client.ui.activities.ViewModuleActivity;
import nl.uu.fi.dwo.mobile.client.ui.places.FlatModulePlace;
import nl.uu.fi.dwo.mobile.client.ui.places.LoginPlace;
import nl.uu.fi.dwo.mobile.client.ui.places.ReloginPlace;
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
		if (place instanceof nl.uu.fi.dwo.mobile.client.ui.places.guest)
		{
			return new GuestActivity(clientFactory);
		}
		
		if (place instanceof nl.uu.fi.dwo.mobile.client.ui.places.c) 
		{
			String id = ((nl.uu.fi.dwo.mobile.client.ui.places.c) place).getToken();
			SelectModuleItem item = SelectModuleItemHolder.getItemByID(id);
			if(item == null)
			{
				item = new SelectModuleItem(id, SelectModuleItem.Type.MODULE);
				SelectModuleItemHolder.insert(item);
			}
			return new CourseActivity(clientFactory, item);
		}

		if (place instanceof nl.uu.fi.dwo.mobile.client.ui.places.s) 
		{
			String id = ((nl.uu.fi.dwo.mobile.client.ui.places.s) place).getToken();
			SelectModuleItem item = SelectModuleItemHolder.getItemByID(id);
			if(item == null)
			{
				item = new SelectModuleItem(id, SelectModuleItem.Type.SCO);
				SelectModuleItemHolder.insert(item);
			}
			return new ScoActivity(clientFactory, item);
		}
		
		
		if (place instanceof SelectModulePlace)
		{
			SelectModulePlace tmp = (SelectModulePlace) place;
			String id = (tmp.getToken());
			SelectModuleItem item = SelectModuleItemHolder.getItemByID(id);			
			return new SelectModuleActivity(clientFactory, item);
		}
		if (place instanceof FlatModulePlace)
		{
			FlatModulePlace tmp = (FlatModulePlace) place;
			String id = tmp.getToken();
			SelectModuleItem item = SelectModuleItemHolder.getItemByID(id);			
			return new FlatModuleActivity(clientFactory, item);
		}
		if (place instanceof ViewModulePlace)
		{	String id = ((ViewModulePlace) place).getToken();
			SelectModuleItem item = SelectModuleItemHolder.getScoByID(id);
			if(item == null)
				return new LoginActivity(clientFactory);
			return new ViewModuleActivity(clientFactory, item);
		}
		if (place instanceof LoginPlace)
			return new LoginActivity(clientFactory, ((LoginPlace) place).getPlace());
		if (place instanceof ReloginPlace)
		{
			if(DWOplayer.withUser())
				return new ReloginActivity(clientFactory, ((ReloginPlace) place).getPlace());
			else
				return new LoginActivity(clientFactory, ((ReloginPlace) place).getPlace());
		}
		if (place instanceof TreeModulePlace)
		{
			TreeModulePlace tmp = (TreeModulePlace) place;
			String id = tmp.getToken();
			SelectModuleItem item = SelectModuleItemHolder.getItemByID(id);
			if(item == null)
				return new LoginActivity(clientFactory);
			return new TreeModuleActivity(clientFactory, item); // Anders geen activity reset action;
		}
		return null;
	}

}
