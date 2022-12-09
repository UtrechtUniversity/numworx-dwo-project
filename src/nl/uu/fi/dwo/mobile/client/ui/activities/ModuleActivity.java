package nl.uu.fi.dwo.mobile.client.ui.activities;

import javax.inject.Inject;

import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Promises;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.activity.shared.Activity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import dagger.MembersInjector;
import nl.uu.fi.dwo.mobile.client.ui.RPCHandler;
import nl.uu.fi.dwo.mobile.client.ui.SelectModuleItem;
import nl.uu.fi.dwo.mobile.client.ui.SelectModuleItemHolder;
import nl.uu.fi.dwo.mobile.client.ui.places.m;
import nl.uu.fi.dwo.rest.dom.entities.DomCourseStudent;

public class ModuleActivity extends AbstractActivity {
	
	final m place;
	final Promise<SelectModuleItem> promise;
	Promise<Activity> delegate;
	@Inject MembersInjector<TreeModuleActivity> injector;

	@Inject ModuleActivity(	PlaceController controller, RPCHandler rpc) {
		place = (m) controller.getWhere();
		String id = place.getToken();
		SelectModuleItem item = SelectModuleItemHolder.getItemByID(id);
		if (item == null) {
			Promise<DomCourseStudent> p = rpc.getCourse(place.getID());
			promise = p.map( (DomCourseStudent course) -> {
				SelectModuleItem i = new SelectModuleItem(course, place.getBack().getClassCourse());
				i.setParent(place.getBack());
				SelectModuleItemHolder.insert(i);
				return i;
			});
			
		} else 
		{
			promise = Promises.resolved(item);
		}
	}

	@Override
	public void start(AcceptsOneWidget panel, EventBus eventBus) {
		delegate = promise.map( item -> {
			Activity tree = new TreeModuleActivity(injector, item);
				tree.start(panel, eventBus);
			return tree;});		
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
