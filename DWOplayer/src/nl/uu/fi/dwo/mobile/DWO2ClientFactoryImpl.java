package nl.uu.fi.dwo.mobile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.osgi.util.function.Function;
import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Promises;
import org.osgi.util.promise.Success;

import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.place.shared.PlaceHistoryMapper;
import com.google.web.bindery.event.shared.EventBus;

import dagger.Lazy;
import fi.dwo.gwt.lib.rest.util.PersistenceIdDecoderInterface;
import nl.uu.fi.dwo.account.client.DwoGlobalVars;
import nl.uu.fi.dwo.mobile.DWO2player.InsertSelectItems;
import nl.uu.fi.dwo.mobile.client.DWOplayerParameters;
import nl.uu.fi.dwo.mobile.client.SecureMode;
import nl.uu.fi.dwo.mobile.client.sco.SCORM_DWO5;
import nl.uu.fi.dwo.mobile.client.sco.SCORM_guest;
import nl.uu.fi.dwo.mobile.client.ui.ClientFactoryImpl;
import nl.uu.fi.dwo.mobile.client.ui.ConfirmEventHandler;
import nl.uu.fi.dwo.mobile.client.ui.NeedLogin;
import nl.uu.fi.dwo.mobile.client.ui.RPCHandler;
import nl.uu.fi.dwo.mobile.client.ui.SelectModuleItem;
import nl.uu.fi.dwo.mobile.client.ui.SelectModuleItemHolder;
import nl.uu.fi.dwo.mobile.client.ui.TrafficAgent;
import nl.uu.fi.dwo.mobile.client.ui.places.ClassesPlace;
import nl.uu.fi.dwo.mobile.client.ui.places.TreeModulePlace;
import nl.uu.fi.dwo.mobile.client.ui.views.HeaderView;
import nl.uu.fi.dwo.mobile.client.ui.views.HeaderViewNone;
import nl.uu.fi.dwo.mobile.client.ui.views.TreeModuleView;
import nl.uu.fi.dwo.mobile.client.ui.views.TreeModuleViewNumworx;
import nl.uu.fi.dwo.mobile.client.ui.views.ViewModuleViewBuilder;
import nl.uu.fi.dwo.rest.dom.entities.DomCourseStudent;
import nl.uu.fi.dwo.rest.dom.entities.DomCoursesOfSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomSchool;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.RoleType;
import nl.uu.fi.dwo.rest.persistence.PersistenceClassType;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;

@SuppressWarnings("deprecation")
@Singleton
public final class DWO2ClientFactoryImpl extends ClientFactoryImpl {
  
        @Inject TrafficAgent agent;
		@Inject Lazy<ConfirmEventHandler> confirmHandler;
		@Inject Lazy<CoursesOfClasToSelectItems> coursesToItems;
		@Inject NeedLogin oops;
	    final Provider<? extends TreeModuleView> treeModuleViewProvider;
	    TreeModuleView treeModuleView;
	    final private DwoGlobalVars instance;
	    final private DWOplayerParameters PARAMETERS;

		@Inject DWO2ClientFactoryImpl(EventBus bus, PlaceController controller,
            Provider<PlaceHistoryMapper> mapper,
            Provider<HeaderViewNone> none,
            @Named("header") Provider<HeaderView> numworx,
		    Provider<ViewModuleViewBuilder> entry, RPCHandler rpcHandler,
		    Provider<TreeModuleViewNumworx> view,
		    DwoGlobalVars vars,
		    DWOplayerParameters params
		    ) {
              super(bus, controller, entry);
              treeModuleViewProvider = view;
              instance = vars;
              PARAMETERS = params;
              setRPCHandler(rpcHandler);
              setup(none,numworx);
        }

        public TreeModuleView getTreeModuleView()
        {
          if (treeModuleView == null)
            return treeModuleView = treeModuleViewProvider.get();
          return treeModuleView;
        }

//		public Promise<Void> barrier() {
//			return agent.barrier();
//		}

		@Override
		public Promise<Void> logout() {
			return agent.barrier().
					then(new Success<Void,Void>(){

						@Override
						public Promise<Void> call(Promise<Void> resolved) throws Exception {
//								menuWidget = null;
							if(instance.withUser()) {
								return handler.logout();
							}
							return resolved;
						}}).
					then(new Success<Void,Void>() {

						@Override
						public Promise<Void> call(Promise<Void> resolved) throws Exception {
							treeModuleView = null;
							return null;
						}});
		}

		public SCORM_guest setupAPI() {
			SCORM_guest api;
			if(!instance.withUser()) {
				api = new SCORM_guest();
			} else {
// secure alleen voor studenten!
				boolean secure = PARAMETERS.inExam() && RoleType.STUDENT == getRoleType();
                api = new SCORM_DWO5(getSchoolClass(),
						instance.getActiveSchoolRoleAndClass().getHasRole(),
						agent,
						secure,
						eventBus,
						confirmHandler 
						,oops, instance);
			}
			return api;
		}

		public boolean withUser() {
			return instance.withUser();
		}

		
		public DomSchool getSchool() {
			try {
				return instance.getActiveSchoolRoleAndClass().getSchool();
			} catch (Exception e) {
				return null;
			}
		}

		
		public DomSchoolClass getSchoolClass() {
			return instance.getCurrentSchoolClass();
		}

		
		public boolean isIconizer() {
			try {
				return getSchoolClass().getIconizer().booleanValue();
			} catch (Exception e) {
				return true;
			}
		}

		
		public RoleType getRoleType() {
			try {
				String roleName = instance.getActiveSchoolRoleAndClass().getRole().getRoleName();
				return RoleType.valueOf(roleName);
			} catch (Exception e) {
				return RoleType.ANONYMOUS;
			}
		}

		
		public Object getUserID() {
			PersistenceId id = instance.getCurrentUser().getId();
			return PersistenceIdDecoderInterface.instance.idOf(id, PersistenceClassType.PersistentUser);
		}

		
		@Override
		public void gotoCourses() {
			DwoGlobalVars vars = instance;
			RPCHandler rpc = handler;
			treeModuleView = null;
			SelectModuleItemHolder.clear(); // hier leegmaken of elders?
			Promise<List<SelectModuleItem>> modules;
			final RoleType roleType = vars.getRoleType();
			if( vars.withUser() && vars.getCurrentSchoolClass() != null) {
				Promise<DomCoursesOfSchoolClass> promise = rpc.getCoursesClass(vars.getCurrentSchoolClass());

				modules = promise.map(coursesToItems.get());
			} else if (vars.withUser() && RoleType.STUDENT != roleType)
			{
				Promise<List<DomCourseStudent>> p1 = rpc.getCourses();
				Promise<List<DomCourseStudent>> p2 = rpc.getCoursesSchool(vars.getSchool());
				modules = Promises.all(p1,p2).map(new Function<List<List<DomCourseStudent>>,List<DomCourseStudent>>() {

					@Override
					public List<DomCourseStudent> apply(List<List<DomCourseStudent>> t) {
						List<DomCourseStudent> result = new ArrayList<DomCourseStudent>();
						for (List<DomCourseStudent> item: t) { 
							result.addAll(item);
						}
						return result;
					}})
						.map(DWO2player.TO_SELECTMODULEITEM);
			} else if (!PARAMETERS.inKiosk() ) { // no free lunch in exam
				modules = rpc.getCourses().map(DWO2player.TO_SELECTMODULEITEM);
			} else {
				modules = Promises.resolved(Collections.emptyList());
			}
				
			boolean iconizer = vars.isIconizer();
			if (roleType == RoleType.STUDENT && PARAMETERS.inKiosk() )
				iconizer = false;

			modules.then(new InsertSelectItems(iconizer, roleType)).onResolve(new Runnable() {

					@Override
					public void run() {
						if( !PARAMETERS.inKiosk() || roleType != RoleType.STUDENT)
							placeController.goTo(new TreeModulePlace("0"));
						else 
						{ // was FlatModulePlace();
							placeController.goTo(new ClassesPlace());
						}
					}});
				return;
			
		}
		
		
	}