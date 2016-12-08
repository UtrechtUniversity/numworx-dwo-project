package nl.uu.fi.dwo.mobile;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.osgi.util.function.Function;
import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Promises;
import org.osgi.util.promise.Success;

import nl.uu.fi.dwo.account.client.DwoGlobalVars;
import nl.uu.fi.dwo.account.client.RPCHandlerV2;
import nl.uu.fi.dwo.account.client.UserBar;
import nl.uu.fi.dwo.mobile.client.sco.SCORM_DWO2;
import nl.uu.fi.dwo.mobile.client.sco.SCORM_guest;
import nl.uu.fi.dwo.mobile.client.ui.ClientFactory;
import nl.uu.fi.dwo.mobile.client.ui.ClientFactoryImpl;
import nl.uu.fi.dwo.mobile.client.ui.RPCHandler;
import nl.uu.fi.dwo.mobile.client.ui.SelectModuleItem;
import nl.uu.fi.dwo.mobile.client.ui.SelectModuleItemHolder;
import nl.uu.fi.dwo.mobile.client.ui.places.FlatModulePlace;
import nl.uu.fi.dwo.mobile.client.ui.places.ReloginPlace;
import nl.uu.fi.dwo.mobile.client.ui.places.TreeModulePlace;
import nl.uu.fi.dwo.mobile.client.ui.views.Login2ViewImpl;
import nl.uu.fi.dwo.mobile.client.ui.views.LoginView;
import nl.uu.fi.dwo.rest.dom.entities.DomClassCourse;
import nl.uu.fi.dwo.rest.dom.entities.DomCourse;
import nl.uu.fi.dwo.rest.dom.entities.DomCourseFull;
import nl.uu.fi.dwo.rest.dom.entities.DomCourseStudent;
import nl.uu.fi.dwo.rest.dom.entities.DomCoursesOfSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomHasRole;
import nl.uu.fi.dwo.rest.dom.entities.DomLoginContext;
import nl.uu.fi.dwo.rest.dom.entities.DomRole;
import nl.uu.fi.dwo.rest.dom.entities.DomSchool;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClassFull;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolRoleAndClassV2;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolsRolesAndClassesV2;
import nl.uu.fi.dwo.rest.dom.entities.DomUserFull;
import nl.uu.fi.dwo.rest.dom.entities.DomUserFullwLoginContext;
import nl.uu.fi.dwo.rest.dom.entities.RoleType;
import nl.uu.fi.dwo.rest.persistence.PersistenceClassType;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;
import nl.uu.fi.dwo.rest.util.Dwo2ExceptionTranslator;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.IsWidget;
import com.googlecode.mgwt.ui.client.MGWT;

import fi.dwo.gwt.lib.rest.CallManagers.SecuredUserSchoolLoginManagerV2;
import fi.dwo.gwt.lib.rest.util.Dwo2ExceptionGWTTranslator;
import fi.dwo.gwt.lib.rest.util.PersistenceIdDecoderInterface;

public class DWO2player extends DWOplayer implements EntryPoint {

//	final class DubbeleLogin implements LoginPresenter {
//
//		@Override
//		public void otherlogin(AsyncCallback<Boolean> callback) {
//			boolean ok = Window.confirm(Dwo2ExceptionTranslator.getLocalizedCodeExplanation(DwoGlobalVars.getDwoLocale(), Dwo2ExceptionCode.User_ConfirmNewLoginSession));
//			callback.onSuccess(Boolean.valueOf(ok)); // DOORGAAN
//		}
//		
//	}
	
	private final class DWO2RPCHandler extends RPCHandlerV2 implements RPCHandler{
		private DWO2RPCHandler(String server, int profile) {
			super(server, profile);
		}

//		@Override
//		public void loginMD5(final String name, final String password, final AsyncCallback<Map<String,Object>> callback)
//		{
//			final AsyncCallback<DomUserFullwLoginContext> userCallback = new AsyncUserCallback(schoolManager, callback);
//			loginMD5Helper(name, password, userCallback);	
//		}

//		@Override
//		protected void login(final String name, final String password, final AsyncCallback<Map<String,Object>> callback)
//		{
//			final AsyncCallback<DomUserFullwLoginContext> userCallback = new AsyncUserCallback(schoolManager, callback);
//			loginHelper(name, password, userCallback);
//			
//		}

		@Override
		public void getUserResults(Object courseID, Object userID,
				AsyncCallback<List<Map<String,Object>>> getUserResultsCallback) {
			Object schoolGroupID = getSchoolGroupID();
			getUserResultsHelper(courseID, userID, schoolGroupID, getUserResultsCallback);
		}

//		@Override
//		public void samlLogin(String name, String org,
//				AsyncCallback<Map<String, Object>> callback) {
//			final AsyncUserCallback userCallback = new AsyncUserCallback(schoolManager, callback);
//			samlLoginHelper(name, org, userCallback);
//		}

		/* (non-Javadoc)
		 * @see nl.uu.fi.dwo.mobile.client.ui.activities.RPCHandler#getAuthTokenUser(java.lang.String, com.google.gwt.user.client.rpc.AsyncCallback)
		 */
//		@Override
//		public void getUserFromAuthToken(String authToken,
//				AsyncCallback<Map<String, Object>> callback) {
//			final AsyncUserCallback userCallback = new AsyncUserCallback(schoolManager, callback);
//			getUserFromAuthTokenHelper(authToken, userCallback);
//		}
	}

//	final class AsyncUserCallback implements AsyncCallback<DomUserFullwLoginContext> {
//		private final SecuredUserSchoolLoginManagerV2 schoolManager;
//		Map<String,Object> profile = new HashMap<String,Object>();
//		AsyncCallback<? super Map<String,Object>> callback;
//
//		AsyncUserCallback(SecuredUserSchoolLoginManagerV2 schoolManager, AsyncCallback<? super Map<String,Object>> callback) {
//			this.schoolManager = schoolManager;
//			this.callback = callback;
//		}
//
//		@Override
//		public void onSuccess(DomUserFullwLoginContext result) {
//			DomLoginContext context = result.getDomLoginContext();
//			timezone =  context.getLastLoginTimeStamp().longValue() - System.currentTimeMillis();
//			DomUserFull user = result.getDomUserFull();
//			DwoGlobalVars.getInstance().setCurrentUser(user);
//			DwoGlobalVars.getInstance().setCurrentLoginContext(context);
//			toProfile(user, profile);
//			schoolManager.getSchoolLoginsV2(new AsyncCallback<DomSchoolsRolesAndClassesV2>() {
//
//					@Override
//					public void onFailure(Throwable caught) {
//						callback.onFailure(caught);			
//					}
//
//					@Override
//					public void onSuccess(DomSchoolsRolesAndClassesV2 result) {
//						DwoGlobalVars.instance().setSchoolLogins(result);
//						DomSchoolRoleAndClassV2 active = result.getActiveSchoolRoleAndClass();
//						DomSchoolClass schoolClass = active != null ? active.getSchoolClass() : null;
//						DwoGlobalVars.instance().setCurrentSchoolClass(schoolClass);
//
//						toProfile(result, profile);
//						callback.onSuccess(profile);
//					}
//				});
//			
//			
//		}
//
//		@Override
//		public void onFailure(Throwable caught) {
//			if(caught.getMessage().contains("Cancelled"))
//				return; // Probeer het nog eens...
//			callback.onFailure(caught);
//		}
//	}

	private Object getSchoolGroupID() {
		Object sgID;
		try {
			sgID = PersistenceIdDecoderInterface.instance.idOf(
					DwoGlobalVars.getInstance().getSchoolLogins().getActiveSchoolRoleAndClass().getHasRole().getSchoolGroupId(),
					PersistenceClassType.PersistentSchoolGroup);
		} catch (Exception ignore) {/*NPE*/ sgID = null;}
		return sgID;
	}

	public DWO2player() {
        //Initialize an Exception translator.
        Dwo2ExceptionTranslator.setTranslator(new Dwo2ExceptionGWTTranslator());
        
        getUserBar().setResetLogin(new Command() {
        	Place place = new ReloginPlace(); // FIXME met een hash?
        	
			@Override
			public void execute() {
				clientfactory.getPlaceController().goTo(place);
			}
        	
        });
        
        
	}
	
	
	@Override
	public void setupDWOPlayer() {
		super.setupDWOPlayer();
		if( MGWT.getOsDetection().isAndroid() )
			getUserBar().getElement().getStyle().setColor("white");
	}


	private UserBar userBar = new UserBar();

	protected UserBar getUserBar() {
		return userBar;
	}

	
	protected ClientFactory createClientFactory() {
		ClientFactoryImpl factory = new ClientFactoryImpl() { 
			
			private IsWidget  menuWidget;
			@Override
			public IsWidget getMenuWidget() {
				return menuWidget;
			}

			@Override
			public LoginView getLoginView()
			{
				if (loginView == null)
					loginView = new Login2ViewImpl();
				return loginView;
			}

			@Override
			public void logout() {
				menuWidget = null;
				if(withUser())
				{
					getRPCHandler().logout();
				}
				super.logout();
			}

			public SCORM_guest setupAPI() {
				SCORM_guest api;
				if(!withUser()) {
					api = new SCORM_guest();
					menuWidget = null;
				} else {
					Object userID = 
							PersistenceIdDecoderInterface.instance.idOf(
							DwoGlobalVars.getInstance().getCurrentUser().getId(), 
							PersistenceClassType.PersistentUser);
					Object sgID;
					sgID = getSchoolGroupID();
					
					api = new SCORM_DWO2(userID, sgID);
					menuWidget = getUserBar();
					
					userBar.setRole(getRoleType());

				}
				return api;
			}

			@Override
			public boolean withUser() {
				return DwoGlobalVars.getInstance().getCurrentUser() != null;
			}

			@Override
			public Object getSchoolID() {
				try {
					PersistenceId id = DwoGlobalVars.getInstance().getSchoolLogins().getActiveSchoolRoleAndClass().getSchool().getId();
					return PersistenceIdDecoderInterface.instance.idOf(id, PersistenceClassType.PersistentSchool);
				} catch (Exception e) {
					return "";
				}
			}

			@Override
			public Object getClassID() {
				try {
					PersistenceId id = DwoGlobalVars.getInstance().getCurrentSchoolClass().getId();
					return PersistenceIdDecoderInterface.instance.idOf(id, PersistenceClassType.PersistentSchoolClass);
				} catch (Exception e) {
					return "";
				}
			}

			@Override
			public boolean isIconizer() {
				try {
					return DwoGlobalVars.getInstance().getCurrentSchoolClass().getIconizer().booleanValue();
				} catch (Exception e) {
					return true;
				}
			}

			@Override
			public RoleType getRoleType() {
				try {
					String roleName = DwoGlobalVars.getInstance().getSchoolLogins().getActiveSchoolRoleAndClass().getRole().getRoleName();
					return RoleType.valueOf(roleName);
				} catch (Exception e) {
					return RoleType.ANONYMOUS;
				}
			}

			@Override
			public Object getUserID() {
				PersistenceId id = DwoGlobalVars.getInstance().getCurrentUser().getId();
				return PersistenceIdDecoderInterface.instance.idOf(id, PersistenceClassType.PersistentUser);
			}

			@Override
			public Object getSchoolName() {
				try {
					return DwoGlobalVars.getInstance().getSchoolLogins().getActiveSchoolRoleAndClass().getSchool().getSchoolName();
				} catch (Exception e) {
					return "school";
				}
			}


		};
		String host = PARAMETERS.getHost();
		String http = Window.Location.getProtocol();
		factory.setRPCHandler(new DWO2RPCHandler(http + "//" + host + "/dwo/xmlrpc", PROFILE_ID));
		return factory;
	}

	
	final static Function<List<DomCourseStudent>,List<SelectModuleItem>> TO_SELECTMODULEITEM = new Function<List<DomCourseStudent>,List<SelectModuleItem>>(){

		@Override
		public List<SelectModuleItem> apply(List<DomCourseStudent> t) {
			List<SelectModuleItem> result = new ArrayList<SelectModuleItem>(t.size());
			for (DomCourseStudent item: t) {
				result.add(new SelectModuleItem(item, (DomClassCourse)null));
			}
			return result;
		}};
	
	
	@Override
	protected void gotoCourses_impl() {
		SelectModuleItemHolder.clear(); // hier leegmaken of elders?
		Promise<List<SelectModuleItem>> modules;
		if( withUser() && DwoGlobalVars.getInstance().getCurrentSchoolClass() != null) {
			Promise<DomCoursesOfSchoolClass> promise = clientfactory.getRPCHandler().getCoursesClass(DwoGlobalVars.getInstance().getCurrentSchoolClass());

			modules = promise.map(new Function<DomCoursesOfSchoolClass, List<SelectModuleItem>>() {

				private Collection<DomClassCourse> sort(Collection<DomClassCourse> classcourses, DomCoursesOfSchoolClass t) {
					boolean again;
					if(classcourses == null || classcourses.isEmpty()|| Boolean.FALSE.equals(t.getSchoolClass().getIconizer()))
						return classcourses;
					List<DomClassCourse> courses = new ArrayList<>(classcourses);
					do {
						again = false;
						more:
						for(int i = 0; i < courses.size(); i++ ) {
							DomClassCourse course = courses.get(i);
							if( getParentID(course, t) == null) {
								int j;
								for(j = i-1; j >= 0; j--) {
									if(getParentID(courses.get(j), t) == null) {
										if(j == i-1)
											break;
										courses.add(j+1, courses.remove(i));
										continue more;
									}
								}
								if(j == -1) {
									courses.add(0, courses.remove(i));
									continue more;
								}
							} else {
								PersistenceId pid = getParentID(course,t); int j;
								for(j = i-1; j>=0; j--) {
									if(pid.equals( getParentID(courses.get(j),t)) || pid.equals(getID(courses.get(j)))) {
										if(j == i-1) break;
										courses.add(j+1, courses.remove(i));
										continue more;
									}
								}
								if(j == -1) {
									again = true;
								}
							}
						}
					} while(again);
					return courses;
				}

				private PersistenceId getParentID(DomClassCourse course, DomCoursesOfSchoolClass t) {
					PersistenceId id = getID(course);
					DomCourse r = t.getCourses().get(id);
					if(r != null)
					{
						id = r.getParentID();
//  IF ROOT, return null
						if(id == null || PersistenceIdDecoderInterface.instance.idOf(id, PersistenceClassType.PersistentCourse) .equals(0) || ! t.getCourses().containsKey(id))
								return null;
						return id;
					}
					return null;
				}

				private PersistenceId getID(DomClassCourse course) {
					if(course != null) return course.getCourseId();
					return null;
				}



				@Override
				public List<SelectModuleItem> apply(DomCoursesOfSchoolClass t) {
					long now = System.currentTimeMillis() + timezone;
					Map<PersistenceId, DomCourseStudent> courses = t.getCourses();
					Collection<DomClassCourse> classcourses = sort(t.getClassCourses().values(),t);
					List<SelectModuleItem> result = new ArrayList<SelectModuleItem>(classcourses.size());
					for (Iterator<DomClassCourse> iterator = classcourses.iterator(); iterator.hasNext();) {
						DomClassCourse domClassCourse = iterator.next();
						Date o = domClassCourse.getNotBefore();
			            if (o != null) {
			                if (now < o.getTime()) {
			                    continue;
			                }
			            }
			            o = domClassCourse.getNotAfter();
			            if (o != null) {
			                if (now > o.getTime()) {
			                    continue;
			                }
			            }			            
						DomCourseStudent course = courses.get(domClassCourse.getCourseId());
						SelectModuleItem item = new SelectModuleItem(course, domClassCourse);
						if(item.getType() == SelectModuleItem.Type.FOLDER)
							item.setChildren(new ArrayList<SelectModuleItem>());							
						result.add(item);
					}
					return result;
				}
			});
		} else if (withUser() && RoleType.STUDENT != clientfactory.getRoleType())
		{
			Promise<List<DomCourseStudent>> p1 = clientfactory.getRPCHandler().getCourses();
			Promise<List<DomCourseStudent>> p2 = clientfactory.getRPCHandler().getCoursesSchool(DwoGlobalVars.getInstance().getSchoolLogins().getActiveSchoolRoleAndClass().getSchool());
			modules = Promises.all(p1,p2).map(new Function<List<List<DomCourseStudent>>,List<DomCourseStudent>>() {

				@Override
				public List<DomCourseStudent> apply(List<List<DomCourseStudent>> t) {
					List<DomCourseStudent> result = new ArrayList<DomCourseStudent>();
					for (List<DomCourseStudent> item: t) { 
						result.addAll(item);
					}
					return result;
				}})
					.map(TO_SELECTMODULEITEM);
		} else {
			modules = clientfactory.getRPCHandler().getCourses().map(TO_SELECTMODULEITEM);
		}
			
		modules.then(new Success<List<SelectModuleItem>, Void>() {

			@Override
			public Promise<Void> call(Promise<List<SelectModuleItem>> resolved) throws Exception {
				boolean iconizer = clientfactory.isIconizer();
				for (SelectModuleItem item : resolved.getValue()) {
					if(!iconizer && item.getType() == SelectModuleItem.Type.FOLDER) // insert no folders.
						continue;
					if(!iconizer) item.setParent(null);
					
					if(item.getParent() != null) {
						SelectModuleItem parent = item.getParent();
						if (parent != null) { 
							List<SelectModuleItem> children = parent.getChildren();
							if (children == null)
								parent.setChildren(children = new ArrayList<SelectModuleItem>());
							children.add(item);
						}
					}
					SelectModuleItemHolder.insert(item);		
				}
				return null;
			}}).onResolve(new Runnable() {

				@Override
				public void run() {
					if(clientfactory.isIconizer())
						clientfactory.getPlaceController().goTo(new TreeModulePlace("0"));
					else 
						clientfactory.getPlaceController().goTo(new FlatModulePlace());
				}});
			return;
		
		//super.gotoCourses_impl();
	}

// From RestRpcHandler
//	void toProfile(DomSchoolsRolesAndClassesV2 result, Map<String, Object> profile) {
//		PersistenceIdDecoderInterface instance = PersistenceIdDecoderInterface.instance;
//		DomSchoolRoleAndClassV2 active = result.getActiveSchoolRoleAndClass();
//		DomSchoolClass schoolClass;
//		DomSchool      school;
//		DomHasRole     hasRole;
//		DomRole        role;
//		schoolClass = active.getSchoolClass();
//		school = active.getSchool();
//		hasRole = active.getHasRole();
//		role  = active.getRole();
//		PersistenceId classId = schoolClass != null ? schoolClass.getId() : null;
//		PersistenceId schoolId = school != null ? school.getId() : null;
//		PersistenceId sgId = hasRole != null ? hasRole.getSchoolGroupId() : null;
//
//		profile.put("iconizer", schoolClass != null ? schoolClass.getIconizer() : Boolean.FALSE);
//		profile.put("classID", classId == null ? "" :
//				instance.idOf(classId, PersistenceClassType.PersistentSchoolClass));
//		profile.put("schoolID", schoolId == null ? "" :
//				instance.idOf(schoolId, PersistenceClassType.PersistentSchool));
//		profile.put("schoolName", school.getSchoolName());
//		profile.put("groupname",  role.getRoleName());
//		profile.put("class", schoolClass == null ? "" :schoolClass.getSchoolClassName());
//		profile.put("groupID", instance.idOf(role.getId(), PersistenceClassType.PersistentRole));
//		profile.put("schoolGroupID", instance.idOf(sgId, PersistenceClassType.PersistentSchoolGroup));
//		RoleType roleType = RoleType.NONE;
//		try { roleType = RoleType.valueOf(role.getRoleName()); } catch(Exception ignore) {}
//		getUserBar().setRole(roleType);
//	}
//
//	void toProfile(DomUserFull result, Map<String, Object> profile) {
//		profile.put("firstname", result.getGivenName());
//		profile.put("middlename", result.getInsertion());
//		profile.put("lastname", result.getFamilyName());
//		profile.put("userID", PersistenceIdDecoderInterface.instance.idOf(result.getId(), PersistenceClassType.PersistentUser));
//		profile.put("username", result.getUserName());
//		profile.put("password",result.getPassword());
//		PersistenceId userId = result.getId();
//		PersistenceIdDecoderInterface instance = PersistenceIdDecoderInterface.instance;
//		profile.put("userID", instance.idOf(userId, PersistenceClassType.PersistentUser));
//
//		getUserBar().setSingleSchool(result.getSingleSchool());
//		
//	}

	
}
