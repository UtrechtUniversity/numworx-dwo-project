package nl.uu.fi.dwo.mobile.client.ui.activities;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import javax.inject.Inject;
import javax.inject.Provider;

import nl.uu.fi.dwo.account.client.DwoGlobalVars;
import nl.uu.fi.dwo.mobile.client.ui.RPCHandler;
import nl.uu.fi.dwo.mobile.client.ui.SCO_TO_MODULEITEM;
import nl.uu.fi.dwo.mobile.client.ui.SelectModuleItem;
import nl.uu.fi.dwo.mobile.client.ui.SelectModuleItemHolder;
import nl.uu.fi.dwo.mobile.client.ui.TrafficAgent;
import nl.uu.fi.dwo.mobile.client.ui.places.LoginPlace;
import nl.uu.fi.dwo.mobile.client.ui.places.TreeModulePlace;
import nl.uu.fi.dwo.mobile.client.ui.places.ViewCoursePlace;
import nl.uu.fi.dwo.mobile.client.ui.places.ViewModulePlace;
import nl.uu.fi.dwo.mobile.client.ui.views.GotoController;
import nl.uu.fi.dwo.mobile.client.ui.views.HeaderView;
import nl.uu.fi.dwo.mobile.client.ui.views.NavigationView;
import nl.uu.fi.dwo.mobile.client.ui.views.NoCourseView;
import nl.uu.fi.dwo.mobile.client.ui.views.TreeModuleView;
import nl.uu.fi.dwo.mobile.client.ui.views.UnSafeModuleView;
import nl.uu.fi.dwo.rest.dom.entities.DomClassCourse;
import nl.uu.fi.dwo.rest.dom.entities.DomCourseStudent;
import nl.uu.fi.dwo.rest.dom.entities.DomCoursesOfSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomMapEntry;
import nl.uu.fi.dwo.rest.dom.entities.DomSchool;
import nl.uu.fi.dwo.rest.dom.entities.DomScoContext;
import nl.uu.fi.dwo.rest.dom.entities.DomUserFull;
import nl.uu.fi.dwo.rest.dom.entities.RoleType;
import nl.uu.fi.dwo.rest.dom.entities.util.CourseType;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.exceptions.Dwo2ExceptionCode;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;

import org.osgi.util.promise.Failure;
import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Promises;
import org.osgi.util.promise.Success;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.activity.shared.Activity;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import dagger.MembersInjector;
import dagger.Reusable;

public class CourseActivity2 extends AbstractActivity implements Activity, GotoController {

    @Reusable
    public static class Factory {
       @Inject MembersInjector<CourseActivity2> caInjector;
       @Inject Factory() {} 
       public CourseActivity2 create(SelectModuleItem item, Place where) {
         return new CourseActivity2(item, where, caInjector);
       }
    }
  
    @Inject TrafficAgent agent;
	@Inject RPCHandler rpc;
	@Inject PlaceController placeController;
	@Inject Provider<NoCourseView> noCourseView;
    @Inject HeaderView headerView;
    @Inject Provider<UnSafeModuleView> unsafe;
    @Inject NavigationView navigation;
    @Inject DwoGlobalVars vars;
    @Inject Provider<TreeModuleView> treeModuleView;

    private Place where;
    private SelectModuleItem item;

	CourseActivity2(SelectModuleItem item, Place where, MembersInjector<CourseActivity2> injector) {
	  this.item = item;
	  this.where = where;
	  injector.injectMembers(this);
	}
	
	@Override
	public void start(AcceptsOneWidget panel, EventBus eventBus)
	{
		final TreeModuleView view = treeModuleView.get();
		navigation.hide();
		view.setBeheer(false);
		view.setPresenter(this);
		DomUserFull currentUser = vars.getCurrentUser();
		RoleType roleType = vars.getRoleType();
		headerView.setUserAndRole(currentUser, roleType);
		headerView.setPresenter(this);
		headerView.setHomePlace(where);
		headerView.setUpPlace(where);
		
		final Place next = 
				new LoginPlace(where);
				
		
		if(item.getName() == null) {
			item.setName("#c:" + item.getID());
			final Failure failure = new Failure() {
				
				@Override
				public void fail(Promise<?> resolved) throws Exception {
					Throwable t = resolved.getFailure();
					if(t instanceof Dwo2Exception) {
						Dwo2Exception e = (Dwo2Exception) t;
						if( e.getDwo2Code() == Dwo2ExceptionCode.Rest_LoginNeeded)
						{
							item.setFromSchool(true);
							placeController.goTo(next);
							return;
						}
					}

					if (t instanceof NoSuchElementException)
					{
						NoCourseView view = noCourseView.get();
						panel.setWidget(view);
						view.setHomePlace(placeController.getWhere());
						view.render();
						return;
					}

					GWT.log("failure", t);
				}
			};

			Promise<List<SelectModuleItem>> promise = item.getChildrenAsync();
// Start downloading sco's

// start downloading description/name/attributes
			if ( vars.getCurrentSchoolClass() != null) {
				Promise<DomCoursesOfSchoolClass> filtered = rpc.getCourseClass(item.getID(), vars.getCurrentSchoolClass()).
				filter(p-> !p.getClassCourses().isEmpty()).
				then(p -> { 
					DomClassCourse cc = p.getValue().getClassCourses().get(0).getValue();
					DomCourseStudent c = p.getValue().getCourses().get(0).getValue();
					item.setDomClassCourseStudent(c,cc);
//					view.setDescription(item);
					view.render(SelectModuleItemHolder.getItems());
					view.selectModule(item);
					headerView.setUpPlace(where);

					navigation.hide();
					if(item.getCourseType() == CourseType.assesment) {
						final UnSafeModuleView w = unsafe.get();
						w.selectItem(item);
						navigation.hide();
						agent.barrier().onResolve(		
								new Runnable() {
									public void run() {
										panel.setWidget(w);
						}
						});
						return Promises.failed(new IllegalArgumentException());
					}
					return p;
				}, failure);
				if(promise == null || (promise.isDone() && promise.getFailure() != null)) {
					promise = filtered.map(v -> {
						List<DomMapEntry<PersistenceId, DomScoContext>> list = v.getScoContexts();
						ArrayList<DomScoContext> scos = new ArrayList<>();
						for(DomMapEntry<PersistenceId, DomScoContext> entry: list) scos.add(entry.getValue());
						return scos;
					}).map(new SCO_TO_MODULEITEM(item));
					item.setChildrenAsync(promise);
				}				
			} else {
				if(promise == null || (promise.isDone() && promise.getFailure() != null)) {
					Success<List<SelectModuleItem>, List<SelectModuleItem>> success = 
							new Success<List<SelectModuleItem>, List<SelectModuleItem>>() {

								@Override
								public Promise<List<SelectModuleItem>> call(
										Promise<List<SelectModuleItem>> resolved)
										throws Exception {
									return resolved;
								}
					};
					promise = rpc.getScos(item.getID())
							.map(new SCO_TO_MODULEITEM(item)).then(success , failure);
					item.setChildrenAsync(promise);
				}			
			rpc.getCourse(item.getID())

			.filter(p -> allowAccess(p.getSchoolId()))
			
			
			.then(new Success<DomCourseStudent, Void>() {

				@Override
				public Promise<Void> call(Promise<DomCourseStudent> resolved) throws Exception {
					DomCourseStudent value = resolved.getValue();
					String name = value.getName();
					String description = value.getDescription();
					item.setDescription(description);
					item.setName(name);
					item.showChildren(!value.isNotVisible());
					PersistenceId schoolId = value.getSchoolId();
					item.setFromSchool(schoolId != null);
//					view.setDescription(item);
					view.render(SelectModuleItemHolder.getItems());
					view.selectModule(item);
					navigation.hide();
					return null;
				}
			}, failure);
			}
		} else {
			view.render(SelectModuleItemHolder.getItems());
			view.selectModule(item);
			navigation.hide();			
		}
//		view.setDescription(item);
		panel.setWidget(view);
	}

	private boolean allowAccess(PersistenceId schoolId) {
		DomSchool school = vars.getSchool();
		return schoolId == null || (school != null && schoolId.equals(school.getId()));
	}

	@Override
	public void goTo(Place place) {
		GWT.log(place.getClass().getName() + "  " + place.toString());
		if(place instanceof ViewModulePlace)
		  place = new ViewCoursePlace((ViewModulePlace)place);
	    if (place instanceof LoginPlace) {
	        place = new LoginPlace(where); // logout/login
	      }
	    if (place instanceof TreeModulePlace) {
	      place = where;
	    }
		placeController.goTo(place);
	}
}
