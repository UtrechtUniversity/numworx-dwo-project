package nl.uu.fi.dwo.mobile;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.osgi.util.function.Function;
import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Promises;
import org.osgi.util.promise.Success;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Cookies;

import fi.dwo.gwt.lib.rest.CallManagers.XapiManager;
import fi.dwo.gwt.lib.rest.css.DwoStyle;
import fi.dwo.gwt.lib.rest.ui.MsgDialogPresenter;
import fi.dwo.gwt.lib.rest.ui.MsgDialogView;
import fi.dwo.gwt.lib.rest.util.Base64;
import fi.dwo.gwt.lib.rest.util.Dwo2ExceptionGWTTranslator;
import fi.dwo.gwt.lib.rest.util.PersistenceIdDecoderInterface;
import nl.uu.fi.dwo.account.client.AccountBundle;
import nl.uu.fi.dwo.account.client.DwoGlobalVars;
import nl.uu.fi.dwo.mobile.client.DWO2playerDefaults;
import nl.uu.fi.dwo.mobile.client.SecureMode;
import nl.uu.fi.dwo.mobile.client.dagger.DWO2PlayerComponent;
import nl.uu.fi.dwo.mobile.client.dagger.DaggerDWO2PlayerComponent;
import nl.uu.fi.dwo.mobile.client.ui.Actions;
import nl.uu.fi.dwo.mobile.client.ui.ClientFactory;
import nl.uu.fi.dwo.mobile.client.ui.IdleDetect;
import nl.uu.fi.dwo.mobile.client.ui.MessageEvent;
import nl.uu.fi.dwo.mobile.client.ui.RPCHandler;
import nl.uu.fi.dwo.mobile.client.ui.SelectModuleItem;
import nl.uu.fi.dwo.mobile.client.ui.SelectModuleItemHolder;
import nl.uu.fi.dwo.mobile.client.ui.TabletActivityMapper;
import nl.uu.fi.dwo.mobile.client.ui.places.TreeModulePlace;
import nl.uu.fi.dwo.rest.dom.entities.DomClassCourse;
import nl.uu.fi.dwo.rest.dom.entities.DomCourse;
import nl.uu.fi.dwo.rest.dom.entities.DomCourseStudent;
import nl.uu.fi.dwo.rest.dom.entities.DomCoursesOfSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomMapEntry;
import nl.uu.fi.dwo.rest.dom.entities.DomUserFullwLoginContext;
import nl.uu.fi.dwo.rest.dom.entities.RoleType;
import nl.uu.fi.dwo.rest.dom.entities.util.CourseType;
import nl.uu.fi.dwo.rest.persistence.PersistenceClassType;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;
import nl.uu.fi.dwo.rest.util.Dwo2ExceptionTranslator;

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
	
	private final static class DWO2RPCHandler extends nl.uu.fi.dwo.account.client.RPCHandlerV3 implements RPCHandler {
		private DWO2RPCHandler(int profile) {
			super(null, profile, false);
		}
// MISSING clear schoollogins etc.
		@Override
		public Promise<Void> logout() {
			return super.logout().then ( p -> {
				DwoGlobalVars.instance().setSchoolLogins(null);
				DwoGlobalVars.instance().setActiveSchoolRoleAndClass(null);
				xapi = null;
				return p;
			});
		}
		
		private Promise<XapiManager> xapi; // caching xapi 1 per login
		
		@Override
		public Promise<XapiManager> getLRS() {
		  if (xapi == null) {
		    xapi = super.getLRS();
		  }
		  return xapi;
		}
    @Override
    public Promise<DomUserFullwLoginContext> getUserFromAuthToken(String authToken) {
      if (PARAMETERS.getDwoEnv().contains("test")||PARAMETERS.getDwoEnv().contains("saml"))
        return super.getUserFromOAuthToken(authToken);
      else
        return super.getUserFromAuthToken(authToken);
    }

    @Override
    public Promise<DomUserFullwLoginContext> samlLogin(String name, String org) {
      String authToken = Cookies.getCookie(DWO_SAML_AUTH_TOKEN);
      authToken = "3\f" + name + '\f' + org + '\f' + authToken;
      return super.getUserFromOAuthToken(Base64.btoa(authToken));
    }
		
	}


	public DWO2player() {
        //Initialize an Exception translator.
        Dwo2ExceptionTranslator.setTranslator(new Dwo2ExceptionGWTTranslator());
	}
			
  @Inject
  void createTabletDisplay(ClientFactory factory, TabletActivityMapper appActivityMapper, IdleDetect idleDetect) {
    super.createTabletDisplay(factory, appActivityMapper);
 
 // TESTING
 //   factory.getEventBus().addHandler(IdleDetect.TYPE, ev -> { GWT.log(ev.toString()); });
    idleDetect.start();
    DWO2playerDefaults.idle = idleDetect;
  
    MsgDialogPresenter mdp = new MsgDialogPresenter(factory.getEventBus());
    DwoStyle style = GWT.<AccountBundle>create(AccountBundle.class).style();
    style.ensureInjected();
    new MsgDialogView(mdp, style);
    
    MessageEvent.initialize(factory.getEventBus());
    Actions.isMainNavVisible.execute();
  }

  protected ClientFactory createClientFactory() {
        DWO2RPCHandler rpc = new DWO2RPCHandler(PROFILE_ID);
        DWO2PlayerComponent create = DaggerDWO2PlayerComponent.builder()
            .rpcHandler(rpc)
            .build();
        create.inject(this);
		return create.clientFactory();
	}

	
	@Override
public void setupDWOPlayer() {
	
	super.setupDWOPlayer();
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
		if( withUser() && clientfactory.getSchoolClass() != null) {
			Promise<DomCoursesOfSchoolClass> promise = clientfactory.getRPCHandler().getCoursesClass(clientfactory.getSchoolClass());

			modules = promise.map(new Function<DomCoursesOfSchoolClass, List<SelectModuleItem>>() {

				private Collection<DomClassCourse> sort(List<DomMapEntry<PersistenceId, DomClassCourse>> list, DomCoursesOfSchoolClass t) {
					boolean again;
					List<DomClassCourse> classcourses = null;
					if(list != null) {
						classcourses = new ArrayList<DomClassCourse>(list.size());
						for(DomMapEntry<?,DomClassCourse> e: list) classcourses.add(e.getValue());
					}
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
					DomCourse r = find(t, id);
					if(r != null)
					{
						id = r.getParentID();
//  IF ROOT, return null
						if(id == null || PersistenceIdDecoderInterface.instance.idOf(id, PersistenceClassType.PersistentCourse) .equals(0) || ! containsKey(t, id))
								return null;
						return id;
					}
					return null;
				}

				protected boolean containsKey(DomCoursesOfSchoolClass t, PersistenceId id) {
					List<DomMapEntry<PersistenceId, DomCourseStudent>> courses = t.getCourses();
					for(DomMapEntry<PersistenceId, DomCourseStudent> entry : courses)
						if(id .equals( entry.getKey())) return true;
					return false;
				}

				protected DomCourse find(DomCoursesOfSchoolClass t, PersistenceId id) {
					List<DomMapEntry<PersistenceId, DomCourseStudent>> courses = t.getCourses();
					for(DomMapEntry<PersistenceId, DomCourseStudent>entry: courses)
						if(id.equals(entry.getKey())) return entry.getValue();
					return null;
				}

				private PersistenceId getID(DomClassCourse course) {
					if(course != null) return course.getCourseId();
					return null;
				}



				@Override
				public List<SelectModuleItem> apply(DomCoursesOfSchoolClass t) {
					long now = System.currentTimeMillis() + timezone;
					Long serverNow = t.getFetchTimeStamp();
// timezone = diff now/servernow
					if(serverNow != null) {
						timezone += serverNow.longValue() - now;
						now = serverNow.longValue();
					}
					boolean inExam = PARAMETERS.getSecureMode() != SecureMode.NORMAL;
					Map<PersistenceId, DomCourseStudent> courses = map(t.getCourses());
					Collection<DomClassCourse> classcourses = sort(t.getClassCourses(),t);
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
						else
							if(inExam && item.getCourseType() == CourseType.normal)
								continue;
						result.add(item);
					}
					return result;
				}

				private Map<PersistenceId, DomCourseStudent> map(
						List<DomMapEntry<PersistenceId, DomCourseStudent>> courses) {
					Map<PersistenceId, DomCourseStudent> map = new HashMap<>();
					for(DomMapEntry<PersistenceId, DomCourseStudent> entry: courses)
						map.put(entry.getKey(), entry.getValue());
					return map;
				}
			});
		} else if (withUser() && RoleType.STUDENT != clientfactory.getRoleType())
		{
			Promise<List<DomCourseStudent>> p1 = clientfactory.getRPCHandler().getCourses();
			Promise<List<DomCourseStudent>> p2 = clientfactory.getRPCHandler().getCoursesSchool(clientfactory.getSchool());
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
							if(iconizer) {							
							Collections.sort(children, new Comparator<SelectModuleItem>() {
	
								@Override
								public int compare(SelectModuleItem o1,
										SelectModuleItem o2) {
									int s1 = o1.getSequencenr();
									int s2 = o2.getSequencenr();
									int c = Integer.compare(s1, s2);
									if(c == 0)
										c = o1.getName().compareTo(o2.getName());
									return c;
								} });
							}
						}
					}
					SelectModuleItemHolder.insert(item);
				}
				// prune					
				if (iconizer && RoleType.STUDENT == clientfactory.getRoleType()) {
					for(SelectModuleItem folder: resolved.getValue()) {
						if (folder.getType() == SelectModuleItem.Type.FOLDER) {
							SelectModuleItem parent = folder;
							do {
								SelectModuleItem grant = parent.getParent();
								if (grant == null) break;
								if (parent.getChildren().isEmpty()) {
									grant.getChildren().remove(parent);
									parent = grant;
								} else {
									parent = null;
									break;
								}
							} while(parent != null);
						}
						
					}
					// prune root children.
					Iterator<SelectModuleItem> list = SelectModuleItemHolder.getItems().iterator();
					while (list.hasNext()) {
						SelectModuleItem item = list.next();
						if (item.getType() == SelectModuleItem.Type.FOLDER && item.getChildren().isEmpty()) {
							list.remove();
						}
					}
					
				}
				return null;
			}}).onResolve(new Runnable() {

				@Override
				public void run() {
					if(clientfactory.isIconizer())
						clientfactory.getPlaceController().goTo(new TreeModulePlace("0"));
					else 
					{ // was FlatModulePlace();
						clientfactory.getPlaceController().goTo(new TreeModulePlace("0"));
					}
				}});
			return;
		
	}

}
