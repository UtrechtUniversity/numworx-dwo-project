package nl.uu.fi.dwo.mobile.client.ui;

import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Success;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.place.shared.PlaceController;

import dagger.MembersInjector;
import dagger.Module;
import dagger.Provides;
import dagger.multibindings.ClassKey;
import dagger.multibindings.IntoMap;
import fi.dwo.gwt.lib.rest.util.PersistenceIdDecoderInterface;
import nl.uu.fi.dwo.mobile.client.ui.activities.DelayedActivity;
import nl.uu.fi.dwo.mobile.client.ui.activities.ExamModuleActivity;
import nl.uu.fi.dwo.mobile.client.ui.activities.TreeModuleActivity;
import nl.uu.fi.dwo.mobile.client.ui.places.xc;
import nl.uu.fi.dwo.rest.dom.entities.DomClassCourse;
import nl.uu.fi.dwo.rest.dom.entities.DomCourseStudent;
import nl.uu.fi.dwo.rest.persistence.PersistenceClassType;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;

@Module
public abstract class ActivityMapperModule {


	@Provides @IntoMap @ClassKey(xc.class) 
	static Activity xcActivity(PlaceController controller, MembersInjector<TreeModuleActivity> trInjector, ExamModuleActivity.Factory exFactory, RPCHandler rpc) {
		xc place = (xc) controller.getWhere();
		Object id = PersistenceIdDecoderInterface.instance.idOf((PersistenceId) place.getID(), PersistenceClassType.PersistentCourse);
		
		SelectModuleItem item = SelectModuleItemHolder.getItemByID(id);
		if(item == null)
		{
//			item = new SelectModuleItem(id, SelectModuleItem.Type.MODULE);
//			item.setPlace(place);
//			SelectModuleItemHolder.insert(item); 
			
			DelayedActivity<SelectModuleItem> activity = new DelayedActivity<>(() -> {
				SelectModuleItem item2 = SelectModuleItemHolder.getItemByID(id);
				item2.setPlace(place);			
				return new TreeModuleActivity(trInjector, item2);				
			});
			Promise<SelectModuleItem> p = 
			rpc.getCourse(id).map( (DomCourseStudent dc) -> { 
				SelectModuleItem i = new SelectModuleItem(dc, (DomClassCourse)null); 
				SelectModuleItemHolder.insert(i);
				return i;});
			p = p.then(activity);
			return activity;
			
			// return activity that downloads this course first and then delegates to treemoduleactivity/exammoduleactivity
		} else {
		    item.setPlace(place);
			if(item.isExam()) {
				Activity c = new TreeModuleActivity(trInjector, item);
				ExamModuleActivity e = exFactory.create(item, () -> c);
				return e;
			}
		}
		return new TreeModuleActivity(trInjector, item);
	}

}
