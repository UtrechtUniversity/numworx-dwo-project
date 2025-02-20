package nl.uu.fi.dwo.mobile.client.ui.activities;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import org.osgi.util.promise.Failure;
import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Success;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.place.shared.PlaceHistoryHandler.Historian;
import com.google.gwt.place.shared.PlaceHistoryMapper;
import com.google.gwt.place.shared.PlaceChangeEvent;
import com.google.gwt.storage.client.Storage;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import dagger.Lazy;
import dagger.Reusable;
import fi.dwo.gwt.lib.rest.GwtRestVars;
import fi.dwo.gwt.lib.rest.CallManagers.OAuthManager;
import fi.dwo.gwt.lib.rest.CallManagers.SecuredUserAccountManager;
import nl.uu.fi.dwo.account.client.DwoGlobalVars;
import nl.uu.fi.dwo.mobile.CoursesOfClasToSelectItems;
import nl.uu.fi.dwo.mobile.client.DWOplayerParameters;
import nl.uu.fi.dwo.mobile.client.ui.RPCHandler;
import nl.uu.fi.dwo.mobile.client.ui.SCO_TO_MODULEITEM;
import nl.uu.fi.dwo.mobile.client.ui.SelectModuleItem;
import nl.uu.fi.dwo.mobile.client.ui.SelectModuleItemHolder;
import nl.uu.fi.dwo.mobile.client.ui.places.LoginPlace;
import nl.uu.fi.dwo.mobile.client.ui.places.ViewCoursePlace;
import nl.uu.fi.dwo.mobile.client.ui.views.HeaderView;
import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.dom.entities.DomCoursesOfSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomHasRole;
import nl.uu.fi.dwo.rest.dom.entities.DomLoginContext;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolRoleAndClassV2;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolsRolesAndClassesV2;
import nl.uu.fi.dwo.rest.dom.entities.DomToken;
import nl.uu.fi.dwo.rest.dom.entities.DomUserFull;
import nl.uu.fi.dwo.rest.dom.entities.DomUserFullwLoginContext;
import nl.uu.fi.dwo.rest.dom.entities.RoleType;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;

@Reusable
public class LastExamActivity implements Activity, ValueChangeHandler<String>, PlaceChangeEvent.Handler {

	final private static String BASE_KEY = LastExamActivity.class.getName()+ "$";
	enum State {
		TOKEN, HASROLE, SCHOOLCLASS, CLASSCOURSE, PASSWORD, SCO, PLACE;
		String key() { return BASE_KEY + name(); }
	}

	final Storage storage;
	final DwoGlobalVars vars;
	final PlaceController controller;
	final PlaceHistoryMapper mapper;
	final DWOplayerParameters PARAMETERS;
	final Place defaultPlace;
	final OAuthManager oauth = new OAuthManager();
	final SecuredUserAccountManager accountManager = new SecuredUserAccountManager();
	final RPCHandler rpc;
	private boolean kiosk;
	@Inject HeaderView headerView;
	@Inject Lazy<CoursesOfClasToSelectItems> coursesToItems;
	
	
	@Inject void setHistorian(Historian h) {	
		if (kiosk)
			h.addValueChangeHandler(this);
		else
			setItem(State.PLACE, null);
	}

	@Inject void setEventBus(com.google.web.bindery.event.shared.EventBus bus) {
		if (kiosk)
			bus.addHandler(PlaceChangeEvent.TYPE, this);
	}
	
	private Provider<Activity> delegate;
	private boolean started;
	private Place place;
	private EventBus bus;
	private AcceptsOneWidget panel;

	
	@Inject LastExamActivity(DwoGlobalVars vars, 
			PlaceController controller, 
			DWOplayerParameters PARAMETERS,
			@Named("defaultPlace") Place defaultPlace,
			RPCHandler rpc, 
			PlaceHistoryMapper mapper
			) {
		this.storage = Storage.getSessionStorageIfSupported();
		this.vars = vars;
		this.controller = controller;
		this.PARAMETERS = PARAMETERS;
		this.defaultPlace = defaultPlace;
		this.place = defaultPlace;
		this.rpc = rpc;
		this.mapper = mapper;
		//this.kiosk = PARAMETERS.inKiosk() || true;
	}

	private void toDefault() {
		controller.goTo(defaultPlace);
	}
 	
	private Promise<Object> success(Promise<Object> p) {
		started = true;
		final Activity activity = delegate.get();
		delegate = () -> activity;
		activity.start(panel, bus);
		return p;
	}

	private void failed(Promise<?> p) {
		GWT.log("failed", p.getFailure());
		destroy();
		controller.goTo(new LoginPlace(defaultPlace));
	}
	
	public Activity setActivity(Provider<Activity> delegate, Place place) {
		started = false;
		this.delegate = delegate;
		this.place = place;
		return this;
	}
		
	@Override
	public void start(AcceptsOneWidget panel, EventBus eventBus) {
		started = false;
		if (delegate == null) {
			if (restorable()) {
				restore().onResolve( this::toDefault );
			} else {
				Scheduler.get().scheduleDeferred(() ->toDefault());
			}
		} else {
			if (restorable()) {
				this.panel = panel;
				this.bus = eventBus;
				Success<Object, Object> success = this::success;
				Failure failed = this::failed;
				restore().then(success, failed);
			} else {
				Place login = new LoginPlace(place);
				Scheduler.get().scheduleDeferred(() ->controller.goTo(login));
			}
		}
		
	}
	
	private Promise<?> restore() {
		String token = getItem(State.TOKEN);
		Promise<DomToken> p1;
		p1 = oauth.refresh_token(token);
		Promise<DomLoginContext> p2 = p1.then(
				p -> {
					DomToken dt = p.getValue();							
					GwtRestVars.instance().setBearerToken(dt.getAccess_token());
					GwtRestVars.instance().setRefreshToken(dt.getRefresh_token());
					return accountManager.getLoginContext();
				});
		Promise<DomUserFullwLoginContext> p3 = p2.then( 
				q -> {
					DomLoginContext c = q.getValue();
                    DomContext context = new DomContext();
                    context.setRealm(q.getValue().getRealm());
                    context.setDomHasRole(new DomHasRole());
                    context.getDomHasRole().setId(q.getValue().getHasRoleId());
                    context.getDomHasRole().setSchoolGroupId(q.getValue().getSchoolGroupId());
                    context.getDomHasRole().setUserId(q.getValue().getUserId());
		            return accountManager.getAccountData(context).map( 
		                        data -> { 
				                    DomUserFullwLoginContext all = new DomUserFullwLoginContext();
				                    all.setDomLoginContext(q.getValue());
				                    all.setDomUserFull(data);
				                    return all;
		                        }).then(rpc.setContext());

				});
		Promise<DomSchoolsRolesAndClassesV2> p4 = p3.then(new LoginActivity.Login_Stap1(rpc, vars));
		p4 = p4.then(this::fixActiveRoleAndClass);
		Promise<Void> p5 = p4.then(new LoginActivity.Login_Stap2(vars));
		p5.then(p -> {
			DomUserFull currentUser = vars.getCurrentUser();
			RoleType roleType = vars.getRoleType();
			headerView.setUserAndRole(currentUser, roleType);
			return p;
		} );
		String classcourse = getItem(State.CLASSCOURSE);		
		if (classcourse != null) {
			if (place instanceof ViewCoursePlace) {
				return p5.then(this::getScoItem);
			}
			
		}
		return p5;
	}

	Promise<DomCoursesOfSchoolClass> succes(Promise<DomCoursesOfSchoolClass> p) {
		    DomCoursesOfSchoolClass r = p.getValue();
		    DomSchoolClass currentClass = r.getSchoolClass();
		    vars.setCurrentSchoolClass(currentClass);		    
		    return p;
		  }

	Promise<?> getScoItem(Promise<Void> pr) {
	      PersistenceId id = new PersistenceId(getItem(State.CLASSCOURSE));
	      Promise<DomCoursesOfSchoolClass> promise = rpc.getClassCourse(id);
	      return promise.then(this::succes)
	      .map(coursesToItems.get())
	      .then(l -> {
	        SelectModuleItem item = l.getValue().get(0);
	        SelectModuleItemHolder.insert(item);
	        String pw = getItem(State.PASSWORD);
	        
	        	return rpc.startExam(item.getClassCourse(), pw).then(
	        		p -> {
	// getscos         			
	    				Promise<List<SelectModuleItem>> scos = item.getChildrenAsync();
	    				if(scos == null || (scos.isDone() && scos.getFailure() != null)) {
	    					scos = rpc.getScos(item.getID())
	    							.map(new SCO_TO_MODULEITEM(item));
	    					item.setChildrenAsync(scos);
	    				}
	    				return scos;
	        		}
	        	);
	        });
		
	}
	
	private Promise<DomSchoolsRolesAndClassesV2> fixActiveRoleAndClass(Promise<DomSchoolsRolesAndClassesV2> p) {
		String hasrole = getItem(State.HASROLE);
		if (!p.getValue().getActiveSchoolRoleAndClass().getHasRole().getId().getIdString().equals(hasrole)) {
			List<DomSchoolRoleAndClassV2> roles = p.getValue().getSchoolsRolesAndClassesList();
			for (DomSchoolRoleAndClassV2 role: roles) {
				if (role.getHasRole().getId().getIdString().equals(hasrole))
				{
					p.getValue().setActiveSchoolRoleAndClass(role);
					// TODO fix schoolclass if not default
					break;
				}
				
			}
		} else {
			// TODO fix schoolclass if not default
		}
		return p;
	}
	
	
	public void setClassCourseId(PersistenceId cc) {
		setItem(State.CLASSCOURSE, cc.getIdString());
	}

	public void setPassword(String pw) {
		if (pw == null) removeItem(State.PASSWORD);
		else
			setItem(State.PASSWORD, pw);
	}
	
	
	public void suspend() {
		if (vars.withUser() && vars.getRoleType() == RoleType.STUDENT) {
			String token = GwtRestVars.instance().getRefreshToken();
			setItem(State.TOKEN, token);
			String hasrole = vars.getActiveSchoolRoleAndClass().getHasRole().getId().getIdString();
			setItem(State.HASROLE, hasrole);
			String schoolclass = vars.getActiveSchoolRoleAndClass().getSchoolClass().getId().getIdString();
			setItem(State.SCHOOLCLASS, schoolclass);			
		} else {
			destroy(); // not restorable
		}
	}
	
	public boolean restorable() {
		return PARAMETERS.inExam() && null != getItem(State.TOKEN);
	}
	
	public void destroy() {
		for (State key: State.values())
			removeItem(key);
	}

	private void setItem(State key, String item) {
		if (item == null) removeItem(key); else
		storage.setItem(key.key(), item);
	}
	
	private void removeItem(State key) {
		storage.removeItem(key.key());
	}

	private String getItem(State key) {
		return storage.getItem(key.key());
	}
	
	
	@Override
	public String mayStop() {
		if (started) return delegate.get().mayStop();
		return null;
	}

	@Override
	public void onCancel() {
		place = defaultPlace;
		if (started) {
			final Activity activity = delegate.get();
			delegate = null;
			started = false;
			activity.onCancel();
		}
	}

	@Override
	public void onStop() {
		place = defaultPlace;
		if (started) {
			final Activity activity = delegate.get();
			delegate = null;
			started = false;
			activity.onStop();
		}
	}

	public String getPassword() {
		return getItem(State.PASSWORD);
	}

	@Override
	public void onValueChange(ValueChangeEvent<String> event) {
		setItem(State.PLACE, event.getValue());
		
	}

	public String getPlace() {
		if (kiosk) 
			return getItem(State.PLACE);
		return null;
	}

	@Override
	public void onPlaceChange(PlaceChangeEvent event) {
		Place p = event.getNewPlace();
		String map = defaultPlace.equals(p) ? null : mapper.getToken(p);
		setItem(State.PLACE, map);
		
	}

}
