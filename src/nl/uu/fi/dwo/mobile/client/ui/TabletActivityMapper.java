package nl.uu.fi.dwo.mobile.client.ui;

import javax.inject.Provider;

import nl.uu.fi.dwo.mobile.DWOplayer;
import nl.uu.fi.dwo.mobile.client.ui.activities.CourseActivity;
import nl.uu.fi.dwo.mobile.client.ui.activities.CourseActivity2;
import nl.uu.fi.dwo.mobile.client.ui.activities.ExamModuleActivity;
import nl.uu.fi.dwo.mobile.client.ui.activities.FlatModuleActivity;
import nl.uu.fi.dwo.mobile.client.ui.activities.GuestActivity;
import nl.uu.fi.dwo.mobile.client.ui.activities.LoginActivity;
import nl.uu.fi.dwo.mobile.client.ui.activities.ReloginActivity;
import nl.uu.fi.dwo.mobile.client.ui.activities.ScoActivity;
import nl.uu.fi.dwo.mobile.client.ui.activities.SearchActivity;
import nl.uu.fi.dwo.mobile.client.ui.activities.SelectModuleActivity;
import nl.uu.fi.dwo.mobile.client.ui.activities.TreeModuleActivity;
import nl.uu.fi.dwo.mobile.client.ui.activities.ViewModuleActivity;
import nl.uu.fi.dwo.mobile.client.ui.places.FlatModulePlace;
import nl.uu.fi.dwo.mobile.client.ui.places.LoginPlace;
import nl.uu.fi.dwo.mobile.client.ui.places.ReloginPlace;
import nl.uu.fi.dwo.mobile.client.ui.places.SearchPlace;
import nl.uu.fi.dwo.mobile.client.ui.places.SelectModulePlace;
import nl.uu.fi.dwo.mobile.client.ui.places.TreeModulePlace;
import nl.uu.fi.dwo.mobile.client.ui.places.ViewModulePlace;
import nl.uu.fi.dwo.mobile.client.ui.places.s;
import nl.uu.fi.dwo.rest.dom.entities.DomScoContext;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;

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
			} else {
				if(item.isExam()) {
					Activity c = new CourseActivity2(clientFactory, item);
					ExamModuleActivity e = new ExamModuleActivity(clientFactory, item, () -> c, false);
					return e;
				}
			}
			return new CourseActivity2(clientFactory, item);
		}

		if (place instanceof s) 
		{
			s where = (s) place;
			PersistenceId id = where.getID();
			SelectModuleItem item = SelectModuleItemHolder.getScoByID(id);
			if(item == null)
			{
				DomScoContext sco = new DomScoContext();
				sco.setId(id);
				item = new SelectModuleItem(sco);
				SelectModuleItemHolder.insert(item);
			}
			return new ScoActivity(clientFactory, item, where);
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
		{	ViewModulePlace where = (ViewModulePlace) place;
			PersistenceId id = where.getID();
			SelectModuleItem item = SelectModuleItemHolder.getScoByID(id);
			if(item == null)
				return new LoginActivity(clientFactory);
			final ViewModuleActivity viewModuleActivity = new ViewModuleActivity(clientFactory, item, where);
			Provider<Activity> provider = new Provider<Activity>() {
				
				@Override
				public Activity get() {
					return viewModuleActivity;
				}
			};
			return 
					item.isExam()
					? new ExamModuleActivity(clientFactory, item, provider, true)				
					: viewModuleActivity;
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
			return 
				item.isExam()
					? new ExamModuleActivity(clientFactory, item)
					: new TreeModuleActivity(clientFactory, item); // Anders geen activity reset action;
		}
		if (place instanceof SearchPlace) {
			SearchPlace tmp = (SearchPlace) place;
			return new SearchActivity(clientFactory, tmp.getId());
		}
		
		return null;
	}

}
