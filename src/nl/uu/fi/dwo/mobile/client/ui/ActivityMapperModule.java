package nl.uu.fi.dwo.mobile.client.ui;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.place.shared.PlaceController;

import dagger.Module;
import dagger.Provides;
import dagger.multibindings.ClassKey;
import dagger.multibindings.IntoMap;
import fi.dwo.gwt.lib.rest.util.PersistenceIdDecoderInterface;
import nl.uu.fi.dwo.mobile.client.ui.activities.CourseActivity2;
import nl.uu.fi.dwo.mobile.client.ui.activities.ExamModuleActivity;
import nl.uu.fi.dwo.mobile.client.ui.places.xc;
import nl.uu.fi.dwo.rest.persistence.PersistenceClassType;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;

@Module
public abstract class ActivityMapperModule {


	@Provides @IntoMap @ClassKey(xc.class) 
	static Activity xcActivity(PlaceController controller, CourseActivity2.Factory caFactory, ExamModuleActivity.Factory exFactory) {
		xc place = (xc) controller.getWhere();
		Object id = PersistenceIdDecoderInterface.instance.idOf((PersistenceId) place.getID(), PersistenceClassType.PersistentCourse);
		
		SelectModuleItem item = SelectModuleItemHolder.getItemByID(id);
		if(item == null)
		{
			item = new SelectModuleItem(id, SelectModuleItem.Type.MODULE);
			item.setPlace(place);
			SelectModuleItemHolder.insert(item);
		} else {
		    item.setPlace(place);
			if(item.isExam()) {
				Activity c = caFactory.create(item, place);
				ExamModuleActivity e = exFactory.create(item, () -> c);
				return e;
			}
		}
		return caFactory.create(item, place);
	}

}
