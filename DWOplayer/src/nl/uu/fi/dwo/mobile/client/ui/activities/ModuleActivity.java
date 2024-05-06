package nl.uu.fi.dwo.mobile.client.ui.activities;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Promises;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.activity.shared.Activity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import dagger.MembersInjector;
import nl.uu.fi.dwo.account.client.DwoGlobalVars;
import nl.uu.fi.dwo.mobile.client.ui.RPCHandler;
import nl.uu.fi.dwo.mobile.client.ui.SCO_TO_MODULEITEM;
import nl.uu.fi.dwo.mobile.client.ui.SelectModuleItem;
import nl.uu.fi.dwo.mobile.client.ui.SelectModuleItemHolder;
import nl.uu.fi.dwo.mobile.client.ui.places.HasBack;
import nl.uu.fi.dwo.rest.dom.entities.DomClassCourse;
import nl.uu.fi.dwo.rest.dom.entities.DomCourseStudent;
import nl.uu.fi.dwo.rest.dom.entities.DomCoursesOfSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomMapEntry;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomScoContext;
import nl.uu.fi.dwo.rest.dom.entities.util.CourseType;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;

public class ModuleActivity extends AbstractActivity {
	
	final HasBack place;
	final Promise<SelectModuleItem> promise;
	Promise<Activity> delegate;
	@Inject MembersInjector<TreeModuleActivity> injector;

	@Inject ModuleActivity(	PlaceController controller, RPCHandler rpc, DwoGlobalVars vars) {
		place = (HasBack) controller.getWhere();
		PersistenceId id = place.getID();
		SelectModuleItem item = SelectModuleItemHolder.getItemByID(id);
		if (item == null) {
			DomSchoolClass schoolclass = vars.getCurrentSchoolClass();
			if (schoolclass != null) {
				Promise<DomCoursesOfSchoolClass> p = rpc.getCourseClass(id, schoolclass);
				promise = p
						.filter(courses -> ! courses.getCourses().isEmpty())
						.map( (DomCoursesOfSchoolClass courses) -> {
					SelectModuleItem i;
					DomCourseStudent course = courses.getCourses().get(0).getValue();
					DomClassCourse   cc     = courses.getClassCourses().get(0).getValue();
					i = new SelectModuleItem(course, cc);
					i.setParent(place.getBack());
					SelectModuleItemHolder.insert(i);
// iets met de courses.getScoContexts();
					List<DomScoContext> list = courses.getScoContexts().stream().map(DomMapEntry::getValue).collect(Collectors.toList());			
					i.setChildren(new SCO_TO_MODULEITEM(i).apply(list));
					i.setPlace(place.getPlace());
					return i;
				}).recover(this::getRoot);
			} else {
				Promise<DomCourseStudent> p = rpc.getCourse(place.getID());
				promise = p.flatMap( (DomCourseStudent course) -> {
				Promise<DomClassCourse> classCourse = Promises.resolved(null);
				boolean isInvisible = false;

				if (course.getSchoolId() == null) {
					SelectModuleItem parent = SelectModuleItemHolder.getItemByID(course.getParentID());
					if (parent != null) {
						isInvisible = ((DomCourseStudent) parent.original()).isNotVisible();
						if (isInvisible) {
							DomClassCourse cc = new DomClassCourse();
							cc.setCourseType(CourseType.invisible);
							classCourse = Promises.resolved(cc);
						}
					} else {
						classCourse = rpc.getCourse(course.getParentID()).map (
							(DomCourseStudent pa) -> { 
								DomClassCourse cc = new DomClassCourse();
								CourseType type = CourseType.normal;
								if (pa.isNotVisible()) type = CourseType.invisible;
								cc.setCourseType(type);
								DomClassCourse invpa = new DomClassCourse(); invpa.setCourseType(type);
								SelectModuleItemHolder.insert(new SelectModuleItem((DomCourseStudent)pa, (DomClassCourse)invpa));
								return cc;
							}
						);
					}
				}
				
				
				Promise<SelectModuleItem> q = classCourse.map(cc -> {
					SelectModuleItem i = new SelectModuleItem(course, cc);
					i.setParent(place.getBack());
					i.setPlace(place.getPlace());
					SelectModuleItemHolder.insert(i);
					return i;
				});
				return q;
			}).recover(this::getRoot);
			}
		} else 
		{
			item.setPlace(place.getPlace()); // voor forward van XC, mag ook voor m:?
			promise = Promises.resolved(item);
		}
	}

	
	private SelectModuleItem getRoot(Promise<?> failed) {
		return SelectModuleItem.ROOT;
	}
	
	@Override
	public void start(AcceptsOneWidget panel, EventBus eventBus) {
		delegate = promise.map( item -> {
			Activity tree = new TreeModuleActivity(injector, item);
				tree.start(panel, eventBus);
			return tree;});
		//delegate.then(null, FAILURE); // TODO switch to nocourseview, jammer dan
	}

	@Override
	public void onCancel() {
		delegate.then(p -> { p.getValue().onCancel(); return null;});
	}

	@Override
	public void onStop() {
		delegate.then(p -> { p.getValue().onStop(); return null; });
	}

}
