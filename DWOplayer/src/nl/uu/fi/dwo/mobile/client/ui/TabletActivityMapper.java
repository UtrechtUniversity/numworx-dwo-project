package nl.uu.fi.dwo.mobile.client.ui;

import java.util.Map;

import javax.inject.Inject;
import javax.inject.Provider;

import nl.uu.fi.dwo.account.client.DwoGlobalVars;
import nl.uu.fi.dwo.mobile.client.DWOplayerParameters;
import nl.uu.fi.dwo.mobile.client.ui.activities.ActivityFactory;
import nl.uu.fi.dwo.mobile.client.ui.activities.CourseActivity2;
import nl.uu.fi.dwo.mobile.client.ui.activities.ExamModuleActivity;
import nl.uu.fi.dwo.mobile.client.ui.activities.LastExamActivity;
import nl.uu.fi.dwo.mobile.client.ui.activities.LoginActivity;
import nl.uu.fi.dwo.mobile.client.ui.activities.NoCourseActivity;
import nl.uu.fi.dwo.mobile.client.ui.activities.ReloginActivity;
import nl.uu.fi.dwo.mobile.client.ui.activities.ScoActivity;
import nl.uu.fi.dwo.mobile.client.ui.activities.TreeModuleActivity;
import nl.uu.fi.dwo.mobile.client.ui.activities.ViewCourseActivity;
import nl.uu.fi.dwo.mobile.client.ui.activities.ViewModuleActivity;
import nl.uu.fi.dwo.mobile.client.ui.activities.ViewScoActivity;
import nl.uu.fi.dwo.mobile.client.ui.places.HasHash;
import nl.uu.fi.dwo.mobile.client.ui.places.Hash;
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
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;

import dagger.Lazy;
import dagger.MembersInjector;

/**
 * @see GWT
 * @author Danny Hendrix
 * 
 */
public class TabletActivityMapper implements ActivityMapper
{
	private boolean inexam;
	@Inject TabletActivityMapper(DWOplayerParameters parameters)
	{
		inexam = parameters.inExam();
	}

//  @Inject Provider<MaybeLogoutActivity> maybeLogout;
//	@Inject Provider<ExamActivity> exam;
//	@Inject Provider<ClassesActivity> classes;
	@Inject Provider<LoginActivity> login;
	@Inject Provider<NoCourseActivity> noCourse;
	
	@Inject Map<Class<?>, Provider<Activity>> activityMap;
	@Inject Lazy<CourseActivity2.Factory> caFactory;
	@Inject Lazy<ExamModuleActivity.Factory> exFactory;
	@Inject Lazy<ScoActivity.Factory> scoFactory;
	@Inject Lazy<ViewScoActivity.Factory> viewScoFactory;
	@Inject Lazy<LastExamActivity> lastExam;

	@Inject PlaceController placeController;
	@Inject MembersInjector<ViewModuleActivity> vmInjector;
	@Inject MembersInjector<TreeModuleActivity> trInjector;
	@Inject MembersInjector<ReloginActivity> rlInjector;
	@Inject DwoGlobalVars vars;
	
	@Override
	public Activity getActivity(Place place)
	{
		if (ViewCourseActivity.bookmark != null) {
			place = ViewCourseActivity.bookmark;
			ViewCourseActivity.bookmark = null;
			//Window.alert("BOOKMARK" + place);
			lastExam.get().replacePlace(place);
		}
// simple case 
		Provider<Activity> provider = activityMap.get(place.getClass());
		if (provider != null) return provider.get();
			  		
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
					Activity c = caFactory.get().create(item, place);
					ExamModuleActivity e = exFactory.get().create(item, () -> c);
					return e;
				}
			}
			return caFactory.get().create(item, place);
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
            ActivityFactory f = Actions.isAvailable() ? viewScoFactory.get() : scoFactory.get();
			return f.create(item, where);
		}
		
		
		if (place instanceof ViewModulePlace) // or s
		{	ViewModulePlace where = (ViewModulePlace) place;
			PersistenceId id = where.getID();
			SelectModuleItem item = SelectModuleItemHolder.getScoByID(id);
			if(item == null)
			{
				if (!vars.withUser() && inexam && place instanceof ViewCoursePlace) {
					Provider<Activity> getActivity = () -> {
						SelectModuleItem i2 = SelectModuleItemHolder.getScoByID(id);
						if (i2 == null || !i2.isExam()) return login.get();
						i2.setPlace(where);
						return new ViewCourseActivity(vmInjector, i2, where);
					};
					return lastExam.get().setActivity(getActivity, where);
				}
				return login.get();
			}
			item.setPlace(place);
			final ViewModuleActivity viewModuleActivity = 
			    place instanceof ViewCoursePlace 
			    ? new ViewCourseActivity(vmInjector, item, where)
			    : new ViewModuleActivity(vmInjector, item, where);
			    provider = new Provider<Activity>() {
				
				@Override
				public Activity get() {
					return viewModuleActivity;
				}
			};
			return 
					item.isExam()
					? exFactory.get().create(item, provider)				
					: viewModuleActivity;
		}
//		if (place instanceof LoginPlace)
//			return new LoginActivity(clientFactory, ((LoginPlace) place).getPlace());
		if (place instanceof ReloginPlace)
		{
			if(vars.withUser())
				return new ReloginActivity(rlInjector, ((HasHash) place).getPlace());
			else
				return login.get();
		}
		if (place instanceof TreeModulePlace)
		{
			TreeModulePlace tmp = (TreeModulePlace) place;
			String id = tmp.getToken();
			SelectModuleItem item = SelectModuleItemHolder.getItemByID(id);
			if(item == null)
			{
				if (vars.withUser())
					return noCourse.get();
				return login.get();
			}
			item.setPlace(place);
			return 
				item.isExam()
					? exFactory.get().create(item, () -> new TreeModuleActivity(trInjector, item) )
					: new TreeModuleActivity(trInjector, item); // Anders geen activity reset action;
		}
//		if (place instanceof SearchPlace) {
//			SearchPlace tmp = (SearchPlace) place;
//			return new SearchActivity(clientFactory, tmp.getId());
//		}
		
		return null;
	}

}
