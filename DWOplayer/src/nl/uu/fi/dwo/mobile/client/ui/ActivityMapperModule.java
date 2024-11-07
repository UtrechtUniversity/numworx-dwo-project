package nl.uu.fi.dwo.mobile.client.ui;

import java.util.Collections;
import java.util.List;

import javax.inject.Provider;

import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Promises;

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
import nl.uu.fi.dwo.mobile.client.ui.activities.UpActivity;
import nl.uu.fi.dwo.mobile.client.ui.activities.ViewModuleActivity;
import nl.uu.fi.dwo.mobile.client.ui.places.ViewModulePlace;
import nl.uu.fi.dwo.mobile.client.ui.places.last;
import nl.uu.fi.dwo.mobile.client.ui.places.m;
import nl.uu.fi.dwo.mobile.client.ui.places.up;
import nl.uu.fi.dwo.mobile.client.ui.places.xc;
import nl.uu.fi.dwo.mobile.client.ui.places.xs;
import nl.uu.fi.dwo.mobile.client.ui.views.NoCourseView;
import nl.uu.fi.dwo.rest.dom.entities.DomClassCourse;
import nl.uu.fi.dwo.rest.dom.entities.DomCourseStudent;
import nl.uu.fi.dwo.rest.dom.entities.DomMapEntry;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomScoContext;
import nl.uu.fi.dwo.rest.dom.entities.RoleType;
import nl.uu.fi.dwo.rest.dom.entities.util.CourseType;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.exceptions.Dwo2ExceptionCode;
import nl.uu.fi.dwo.rest.persistence.PersistenceClassType;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;

@Module
public abstract class ActivityMapperModule {

	
	

//	@Provides @IntoMap @ClassKey(xc.class) 
//	static Activity xcActivity(PlaceController controller, MembersInjector<TreeModuleActivity> trInjector, ExamModuleActivity.Factory exFactory, RPCHandler rpc, DwoGlobalVars vars) {
//		xc place = (xc) controller.getWhere();
//		Object id = PersistenceIdDecoderInterface.instance.idOf((PersistenceId) place.getID(), PersistenceClassType.PersistentCourse);
//		
//		SelectModuleItem item = SelectModuleItemHolder.getItemByID(id);
//		if(item == null)
//		{
////			item = new SelectModuleItem(id, SelectModuleItem.Type.MODULE);
////			item.setPlace(place);
////			SelectModuleItemHolder.insert(item); 
//			
//			DelayedActivity<SelectModuleItem> activity = new DelayedActivity<>((item2) -> {
//				item2.setPlace(place);
//			    if (place.getBack() != null) {
//			    	item2.setParent(place.getBack());
//			    }
//				return new TreeModuleActivity(trInjector, item2);				
//			});
//			Promise<DomCourseStudent> pp;
//			if (vars.getRoleType() == RoleType.STUDENT) 
//				pp = rpc.getCourseClass(id, vars.getCurrentSchoolClass())
//				.map(x -> x.getCourses().get(0).getValue()); // FIXME extract classcourse
//			else 
//				pp = rpc.getCourse(id);
//			Promise<SelectModuleItem> p = 
//			pp.map( (DomCourseStudent dc) -> { 
//				SelectModuleItem i = new SelectModuleItem(dc, (DomClassCourse)null); 
//				SelectModuleItemHolder.insert(i);
//				return i;});
//			p = p.then(activity, activity);
//			return activity;
//			
//			// return activity that downloads this course first and then delegates to treemoduleactivity/exammoduleactivity
//		} else {
//		    item.setPlace(place);
//			if(item.isExam()) {
//				Activity c = new TreeModuleActivity(trInjector, item);
//				ExamModuleActivity e = exFactory.create(item, () -> c);
//				return e;
//			}
//		}
//		return new TreeModuleActivity(trInjector, item);
//	}

	static class ViewXSActivity extends ViewModuleActivity {
		private final xs place;

		ViewXSActivity(MembersInjector<ViewModuleActivity> injector, SelectModuleItem sco, 
				xs place) {
			super(injector, sco, place);
			sco.setPlace(place);
			this.place = place;
		}

		@Override
		protected void setTrail(List<SelectModuleItem> trail) {
			super.setTrail(Collections.singletonList(place.getBack()));
		}
	}

	@Provides @IntoMap @ClassKey(xs.class)
	static Activity xsActivity( PlaceController controller, MembersInjector<ViewModuleActivity> vmInjector, RPCHandler rpc, DwoGlobalVars vars, Provider<NoCourseView> noCourseView) {
		xs place = (xs) controller.getWhere();
		PersistenceId id = place.getID();
		SelectModuleItem item = SelectModuleItemHolder.getScoByID(id);
		if (item != null) {
			final ViewModuleActivity viewModuleActivity = new ViewXSActivity(vmInjector, item, place);
			return viewModuleActivity;
		}
		DelayedActivity<SelectModuleItem> activity = new DelayedActivity<>((item2) -> {
			return new ViewXSActivity(vmInjector, item2, place);				
		}, noCourseView);
		Promise<DomScoContext> sco;
		DomSchoolClass schoolClass = vars.getCurrentSchoolClass();
		if (schoolClass == null)
			sco = rpc.getSco(id);
		else
// FIXME de parent van sco moet al z'n children hebben, de ViewModuleActivity gaat daar al vanuit.
// daar kan die niet van uit gaan. Er kan geen parent zijn!, activiteit geladen zonder module			
			sco = rpc.getScoContextClass(id, schoolClass)
			.flatMap(v -> {
				if (v.getCourses().isEmpty()) 
				{
					return Promises.failed(new Dwo2Exception(Dwo2ExceptionCode.Rest_ResourceNotFound, ""));
				}
				PersistenceId pid = v.getCourses().get(0).getKey();
				if (SelectModuleItemHolder.getItemByID(pid) == null) {
					if (v.getClassCourses().get(0).getValue().getCourseType() != CourseType.normal)
						return Promises.failed(new Dwo2Exception(Dwo2ExceptionCode.User_AuthorizationError, ""));
				}
				return Promises.resolved(v);
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

	@Binds @IntoMap @ClassKey(xc.class)
	abstract Activity xcActivity(ModuleActivity m);
	
	@Binds @IntoMap @ClassKey(up.class)
	abstract Activity upActivity(UpActivity up);
}
