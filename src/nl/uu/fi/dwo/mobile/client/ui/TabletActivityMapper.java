package nl.uu.fi.dwo.mobile.client.ui;

import java.util.Map;

import javax.inject.Inject;
import javax.inject.Provider;

import nl.uu.fi.dwo.mobile.client.ui.activities.CourseActivity2;
import nl.uu.fi.dwo.mobile.client.ui.activities.ExamActivity;
import nl.uu.fi.dwo.mobile.client.ui.activities.ExamModuleActivity;
import nl.uu.fi.dwo.mobile.client.ui.activities.GuestActivity;
import nl.uu.fi.dwo.mobile.client.ui.activities.LoginActivity;
import nl.uu.fi.dwo.mobile.client.ui.activities.LogoutActivity;
import nl.uu.fi.dwo.mobile.client.ui.activities.MaybeLogoutActivity;
import nl.uu.fi.dwo.mobile.client.ui.activities.ReloginActivity;
import nl.uu.fi.dwo.mobile.client.ui.activities.ScoActivity;
import nl.uu.fi.dwo.mobile.client.ui.activities.SearchActivity;
import nl.uu.fi.dwo.mobile.client.ui.activities.TreeModuleActivity;
import nl.uu.fi.dwo.mobile.client.ui.activities.ViewCourseActivity;
import nl.uu.fi.dwo.mobile.client.ui.activities.ViewModuleActivity;
import nl.uu.fi.dwo.mobile.client.ui.places.Exam;
import nl.uu.fi.dwo.mobile.client.ui.places.HasHash;
import nl.uu.fi.dwo.mobile.client.ui.places.Hash;
import nl.uu.fi.dwo.mobile.client.ui.places.LoginPlace;
import nl.uu.fi.dwo.mobile.client.ui.places.LogoutPlace;
import nl.uu.fi.dwo.mobile.client.ui.places.MaybeLogout;
import nl.uu.fi.dwo.mobile.client.ui.places.ReloginPlace;
//import nl.uu.fi.dwo.mobile.client.ui.places.SearchPlace;
import nl.uu.fi.dwo.mobile.client.ui.places.TreeModulePlace;
import nl.uu.fi.dwo.mobile.client.ui.places.ViewCoursePlace;
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
	@Inject TabletActivityMapper(ClientFactory cf)
	{
		this.clientFactory = cf;
	}

//  @Inject Provider<MaybeLogoutActivity> maybeLogout;
//	@Inject Provider<ExamActivity> exam;
//	@Inject Provider<ClassesActivity> classes;
	@Inject Provider<LoginActivity> login;
	
	@Inject Map<Class<?>, Provider<Activity>> activityMap;
	
	
	@Override
	public Activity getActivity(Place place)
	{
// simple case 
		Provider<Activity> provider = activityMap.get(place.getClass());
		if (provider != null) return provider.get();
		
//		if (place instanceof MaybeLogout) {
//	    return maybeLogout.get().place(place);
//	  }
	  		
		if (place instanceof nl.uu.fi.dwo.mobile.client.ui.places.c) 
		{
			String id = ((Hash) place).getToken();
			SelectModuleItem item = SelectModuleItemHolder.getItemByID(id);
			if(item == null)
			{
				item = new SelectModuleItem(id, SelectModuleItem.Type.MODULE);
				item.setPlace(place);
				SelectModuleItemHolder.insert(item);
			} else {
			    item.setPlace(place);
				if(item.isExam()) {
					Activity c = new CourseActivity2(clientFactory, item, place);
					ExamModuleActivity e = new ExamModuleActivity(clientFactory, item, () -> c, false);
					return e;
				}
			}
			return new CourseActivity2(clientFactory, item, place);
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
            item.setPlace(place);
			return new ScoActivity(clientFactory, item, where);
		}
		
		
		if (place instanceof ViewModulePlace)
		{	ViewModulePlace where = (ViewModulePlace) place;
			PersistenceId id = where.getID();
			SelectModuleItem item = SelectModuleItemHolder.getScoByID(id);
			if(item == null)
				return login.get();
			item.setPlace(place);
			final ViewModuleActivity viewModuleActivity = 
			    place instanceof ViewCoursePlace 
			    ? new ViewCourseActivity(clientFactory, item, where)
			    : new ViewModuleActivity(clientFactory, item, where);
			    provider = new Provider<Activity>() {
				
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
//		if (place instanceof LoginPlace)
//			return new LoginActivity(clientFactory, ((LoginPlace) place).getPlace());
		if (place instanceof ReloginPlace)
		{
			if(clientFactory.withUser())
				return new ReloginActivity(clientFactory, ((HasHash) place).getPlace());
			else
				return login.get();
		}
		if (place instanceof TreeModulePlace)
		{
			TreeModulePlace tmp = (TreeModulePlace) place;
			String id = tmp.getToken();
			SelectModuleItem item = SelectModuleItemHolder.getItemByID(id);
			if(item == null)
				return login.get();
			item.setPlace(place);
			return 
				item.isExam()
					? new ExamModuleActivity(clientFactory, item)
					: new TreeModuleActivity(clientFactory, item); // Anders geen activity reset action;
		}
//		if (place instanceof SearchPlace) {
//			SearchPlace tmp = (SearchPlace) place;
//			return new SearchActivity(clientFactory, tmp.getId());
//		}
		
		return null;
	}

}
