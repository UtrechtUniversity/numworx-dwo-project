package nl.uu.fi.dwo.mobile.client.ui.activities;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import javax.inject.Inject;
import javax.inject.Provider;

import nl.uu.fi.dwo.account.client.DwoGlobalVars;
import nl.uu.fi.dwo.mobile.client.ui.ClientFactory;
import nl.uu.fi.dwo.mobile.client.ui.SCO_TO_MODULEITEM;
import nl.uu.fi.dwo.mobile.client.ui.SelectModuleItem;
import nl.uu.fi.dwo.mobile.client.ui.SelectModuleItemHolder;
import nl.uu.fi.dwo.mobile.client.ui.places.LoginPlace;
import nl.uu.fi.dwo.mobile.client.ui.places.ViewCoursePlace;
import nl.uu.fi.dwo.mobile.client.ui.places.ViewModulePlace;
import nl.uu.fi.dwo.mobile.client.ui.views.GotoController;
import nl.uu.fi.dwo.mobile.client.ui.views.HeaderView;
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

public class CourseActivity2 extends AbstractActivity implements Activity, GotoController {

	private ClientFactory clientFactory;
	private SelectModuleItem item;
	@Inject PlaceController placeController;
	@Inject Provider<NoCourseView> noCourseView;
	private Place where;
	private HeaderView headerView;

	public CourseActivity2(ClientFactory clientFactory, SelectModuleItem item, Place where) {
		this.clientFactory = clientFactory;
		this.item = item;
		this.where = where;
		placeController = clientFactory.getPlaceController();
		noCourseView = clientFactory.getNoCourseView();
		headerView = clientFactory.getHeaderView();
	}

	@Override
	public void start(AcceptsOneWidget panel, EventBus eventBus)
	{
		final TreeModuleView view = clientFactory.getTreeModuleView();
		clientFactory.getNavigationView().hide();
		view.setBeheer(false);
		view.setPresenter(this);
		DomUserFull currentUser = DwoGlobalVars.instance().getCurrentUser();
		RoleType roleType = clientFactory.getRoleType();
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
			if ( clientFactory.getSchoolClass() != null) {
				Promise<DomCoursesOfSchoolClass> filtered = clientFactory.getRPCHandler().getCourseClass(item.getID(), clientFactory.getSchoolClass()).
				filter(p-> !p.getClassCourses().isEmpty()).
				then(p -> { 
					DomClassCourse cc = p.getValue().getClassCourses().get(0).getValue();
					DomCourseStudent c = p.getValue().getCourses().get(0).getValue();
					item.setDomClassCourseStudent(c,cc);
//					view.setDescription(item);
					view.render(SelectModuleItemHolder.getItems());
					view.selectModule(item);
					clientFactory.getNavigationView().hide();
					if(item.getCourseType() == CourseType.assesment) {
						final UnSafeModuleView w = new UnSafeModuleView(clientFactory.getHeaderView(), clientFactory.getPlaceController());
						w.selectItem(item);
						clientFactory.getNavigationView().hide();
						clientFactory.barrier().onResolve(		
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
					promise = clientFactory.getRPCHandler().getScos(item.getID())
							.map(new SCO_TO_MODULEITEM(item)).then(success , failure);
					item.setChildrenAsync(promise);
				}			
			clientFactory.getRPCHandler().getCourse(item.getID())

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
					clientFactory.getNavigationView().hide();
					return null;
				}
			}, failure);
			}
		} else {
			view.render(SelectModuleItemHolder.getItems());
			view.selectModule(item);
			clientFactory.getNavigationView().hide();			
		}
//		view.setDescription(item);
		panel.setWidget(view);
	}

	private boolean allowAccess(PersistenceId schoolId) {
		DomSchool school = clientFactory.getSchool();
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
		placeController.goTo(place);
	}
}
