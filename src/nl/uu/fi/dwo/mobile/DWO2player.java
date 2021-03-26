package nl.uu.fi.dwo.mobile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;

import org.osgi.util.function.Function;
import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Promises;
import org.osgi.util.promise.Success;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.Window;
import com.google.web.bindery.event.shared.EventBus;

import fi.dwo.gwt.lib.rest.css.DwoStyle;
import fi.dwo.gwt.lib.rest.ui.MsgDialogPresenter;
import fi.dwo.gwt.lib.rest.ui.MsgDialogView;
import fi.dwo.gwt.lib.rest.util.Dwo2ExceptionGWTTranslator;
import nl.uu.fi.dwo.account.client.AccountBundle;
import nl.uu.fi.dwo.account.client.DwoGlobalVars;
import nl.uu.fi.dwo.mobile.client.DWO2playerDefaults;
import nl.uu.fi.dwo.mobile.client.DWOplayerParameters;
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
import nl.uu.fi.dwo.mobile.client.ui.places.ClassesPlace;
import nl.uu.fi.dwo.mobile.client.ui.places.TreeModulePlace;
import nl.uu.fi.dwo.mobile.client.ui.views.HeaderView;
import nl.uu.fi.dwo.mobile.client.ui.views.NavigationView;
import nl.uu.fi.dwo.rest.dom.entities.DomClassCourse;
import nl.uu.fi.dwo.rest.dom.entities.DomCourseStudent;
import nl.uu.fi.dwo.rest.dom.entities.DomCoursesOfSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfileFull;
import nl.uu.fi.dwo.rest.dom.entities.RoleType;
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
	
	public static final class InsertSelectItems implements Success<List<SelectModuleItem>, Void> {
		private final boolean iconizer;
		private final RoleType roleType;

		public InsertSelectItems(boolean iconizer2, RoleType roleType2) {
			iconizer = iconizer2;
			roleType = roleType2;
		}

		@Override
		public Promise<Void> call(Promise<List<SelectModuleItem>> resolved) throws Exception {
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
			if (iconizer && RoleType.STUDENT == roleType) {
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
		}
	}


	public DWO2player() {
        //Initialize an Exception translator.
        Dwo2ExceptionTranslator.setTranslator(new Dwo2ExceptionGWTTranslator());
	}
			
  @Inject
  void createTabletDisplay(ClientFactory factory, TabletActivityMapper appActivityMapper, 
		  IdleDetect idleDetect, DWOplayerParameters PARAMETERS, 
		  NavigationView navigation, HeaderView header, EventBus bus) {
    super.createTabletDisplay(appActivityMapper, PARAMETERS, navigation, header, bus);
 
 // TESTING
    bus.addHandler(IdleDetect.TYPE, ev -> { GWT.log(ev.toString()); });
    idleDetect.start();
  
    MsgDialogPresenter mdp = new MsgDialogPresenter(bus);
    DwoStyle style = GWT.<AccountBundle>create(AccountBundle.class).style();
    style.ensureInjected();
    new MsgDialogView(mdp, style);
    
    MessageEvent.initialize(bus);
    Actions.isMainNavVisible.execute();
    
    if (!Actions.isAvailable()) {
      Window.addCloseHandler(ev -> 
      factory.logout()
      );
    }
  }

  protected void createClientFactory() {
        DWO2PlayerComponent create = DaggerDWO2PlayerComponent.builder()
            .profile(PROFILE_ID)
            .build();
        create.inject(this);
		start(create.placeHistoryHandler());
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
	
	@Inject RPCHandler rpc;	

	void initProfile() {
			Success<DomDwoProfileFull, DomDwoProfileFull> getProfileCallback = new Success<DomDwoProfileFull, DomDwoProfileFull>() {
	
				@Override
				public Promise<DomDwoProfileFull> call(Promise<DomDwoProfileFull> promise)
						throws Exception {
					SelectModuleItem r = SelectModuleItem.ROOT;
					DomDwoProfileFull p = promise.getValue();
					r.setName(p.getDwoProfileDescription());
					r.setDescription(p.getDwoProfileText());
					return promise;
				}};
	//		dwoProfile = dwoProfile.then(getProfileCallback);
	//		deferredProfile.resolveWith(clientfactory.getRPCHandler().getDwoProfile());
				rpc.getDwoProfile().then(getProfileCallback);
			
		}

}
