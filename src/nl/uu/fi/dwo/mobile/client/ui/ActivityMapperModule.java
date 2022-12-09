package nl.uu.fi.dwo.mobile.client.ui;

import java.util.List;

import org.osgi.util.promise.Promise;
import com.google.gwt.activity.shared.Activity;
import com.google.gwt.place.shared.PlaceController;

import dagger.Binds;
import dagger.MembersInjector;
import dagger.Module;
import dagger.Provides;
import dagger.multibindings.ClassKey;
import dagger.multibindings.IntoMap;
import fi.dwo.gwt.lib.rest.util.PersistenceIdDecoderInterface;
import nl.uu.fi.dwo.account.client.DwoGlobalVars;
import nl.uu.fi.dwo.mobile.client.ui.activities.DelayedActivity;
import nl.uu.fi.dwo.mobile.client.ui.activities.ExamModuleActivity;
import nl.uu.fi.dwo.mobile.client.ui.activities.LastActivity;
import nl.uu.fi.dwo.mobile.client.ui.activities.ModuleActivity;
import nl.uu.fi.dwo.mobile.client.ui.activities.TreeModuleActivity;
import nl.uu.fi.dwo.mobile.client.ui.activities.ViewModuleActivity;
import nl.uu.fi.dwo.mobile.client.ui.places.last;
import nl.uu.fi.dwo.mobile.client.ui.places.m;
import nl.uu.fi.dwo.mobile.client.ui.places.xc;
import nl.uu.fi.dwo.mobile.client.ui.places.xs;
import nl.uu.fi.dwo.rest.dom.entities.DomClassCourse;
import nl.uu.fi.dwo.rest.dom.entities.DomCourseStudent;
import nl.uu.fi.dwo.rest.dom.entities.DomMapEntry;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomScoContext;
import nl.uu.fi.dwo.rest.dom.entities.RoleType;
import nl.uu.fi.dwo.rest.persistence.PersistenceClassType;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;

@Module
public abstract class ActivityMapperModule {

	
	

	@Provides @IntoMap @ClassKey(xc.class) 
	static Activity xcActivity(PlaceController controller, MembersInjector<TreeModuleActivity> trInjector, ExamModuleActivity.Factory exFactory, RPCHandler rpc, DwoGlobalVars vars) {
		xc place = (xc) controller.getWhere();
		Object id = PersistenceIdDecoderInterface.instance.idOf((PersistenceId) place.getID(), PersistenceClassType.PersistentCourse);
		
		SelectModuleItem item = SelectModuleItemHolder.getItemByID(id);
		if(item == null)
		{
//			item = new SelectModuleItem(id, SelectModuleItem.Type.MODULE);
//			item.setPlace(place);
//			SelectModuleItemHolder.insert(item); 
			
			DelayedActivity<SelectModuleItem> activity = new DelayedActivity<>((item2) -> {
				item2.setPlace(place);			
				return new TreeModuleActivity(trInjector, item2);				
			});
			Promise<DomCourseStudent> pp;
			if (vars.getRoleType() == RoleType.STUDENT) 
				pp = rpc.getCourseClass(id, vars.getCurrentSchoolClass())
				.map(x -> x.getCourses().get(0).getValue()); // FIXME extract classcourse
			else 
				pp = rpc.getCourse(id);
			Promise<SelectModuleItem> p = 
			pp.map( (DomCourseStudent dc) -> { 
				SelectModuleItem i = new SelectModuleItem(dc, (DomClassCourse)null); 
				SelectModuleItemHolder.insert(i);
				return i;});
			p = p.then(activity, activity);
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

	@Provides @IntoMap @ClassKey(xs.class)
	static Activity xsActivity( PlaceController controller, MembersInjector<ViewModuleActivity> vmInjector, RPCHandler rpc, DwoGlobalVars vars) {
		xs place = (xs) controller.getWhere();
		PersistenceId id = place.getID();
		SelectModuleItem item = SelectModuleItemHolder.getScoByID(id);
		if (item != null) {
			item.setPlace(place);
			final ViewModuleActivity viewModuleActivity = new ViewModuleActivity(vmInjector, item, place);
			return viewModuleActivity;
		}
		DelayedActivity<SelectModuleItem> activity = new DelayedActivity<>((item2) -> {
			item2.setPlace(place);			
			return new ViewModuleActivity(vmInjector, item2, place);				
		});
		Promise<DomScoContext> sco;
		DomSchoolClass schoolClass = vars.getCurrentSchoolClass();
		if (schoolClass == null)
			sco = rpc.getSco(id);
		else
// FIXME de parent van sco moet al z'n children hebben, de ViewModuleActivity gaat daar al vanuit.
			
			sco = rpc.getScoContextClass(id, schoolClass)
			.filter(v -> {
				if (v.getCourses().isEmpty()) return false;
				PersistenceId pid = v.getCourses().get(0).getKey();
				Object cid = PersistenceIdDecoderInterface.instance.idOf(pid, PersistenceClassType.PersistentCourse);				
				return SelectModuleItemHolder.getItemByID(cid) != null;
			})
			
			.map(v -> {
				List<DomMapEntry<PersistenceId, DomScoContext>> list = v.getScoContexts();
				return list.get(0).getValue();
			});
		Promise<SelectModuleItem> p = 
		sco.map( (DomScoContext dc) -> { 
			SelectModuleItem i = new SelectModuleItem(dc); 
			SelectModuleItemHolder.insert(i);
			return i;});
		p = p.then(activity, activity);
		
		
		return activity;
	}

	@Binds @IntoMap @ClassKey(last.class)
	abstract Activity lastActivity(LastActivity last);
	
	@Binds @IntoMap @ClassKey(m.class)
	abstract Activity mActivity(ModuleActivity m);
}
