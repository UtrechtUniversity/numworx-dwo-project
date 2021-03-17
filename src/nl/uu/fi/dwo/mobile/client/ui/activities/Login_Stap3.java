package nl.uu.fi.dwo.mobile.client.ui.activities;

import javax.inject.Inject;

import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Success;

import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.user.client.Window;

import fi.dwo.gwt.lib.rest.CallManagers.SecuredUserAccountManager;
import fi.dwo.gwt.lib.rest.util.Base64;
import nl.uu.fi.dwo.account.client.DwoGlobalVars;
import nl.uu.fi.dwo.mobile.DWOplayer;
import nl.uu.fi.dwo.mobile.client.ui.Actions;
import nl.uu.fi.dwo.mobile.client.ui.ClientFactory;
import nl.uu.fi.dwo.mobile.client.ui.views.HeaderView;
import nl.uu.fi.dwo.rest.dom.entities.DomUserFull;
import nl.uu.fi.dwo.rest.dom.entities.RoleType;

final class Login_Stap3 implements Success<Void, Void> {

	ClientFactory clientFactory;
	Place next;

	@Inject SecuredUserAccountManager account = new SecuredUserAccountManager();
	private PlaceController placeController;
	private HeaderView headerView;
	private DwoGlobalVars instance;
  
	  boolean legal(String base) {
	    RegExp r = RegExp.compile("^/[a-z]+(/[a-z]+)*/$");
	    return r.test(base);
	  }

	
//  private Promise<Void> gotoGwtClient(String page) {
//    final String url = "/gwtclient/index.html";
//    return account.getBearerToken().then(
//      resolved-> {
//        String token = "2\f" + resolved.getValue(); //format 2
//        StringBuilder u = new StringBuilder(url);
//        u.append( "?a=" ) .append (Base64.btoa(token)); // User Auth Token
//        u.append( "&test=on");
//        String profile = String.valueOf(DWOplayer.PROFILE_ID);
//        u.append("&profile=").append(profile);
//        String locale = LocaleInfo.getCurrentLocale().getLocaleName();
//        if ("default".equals(locale) ) locale =  "nl";
//        u.append("&locale=").append(locale);
//        u.append("&view=").append(page);
//        String base = Window.Location.getParameter("base");
//        if(base != null && !base.isEmpty() && legal(base)) 
//          u.append("&base=").append(base);
//        String string = u.toString();
//        Window.Location.replace(string);
//        return null;
//      }     
//     );    
//}	
	Login_Stap3(ClientFactory clientFactory, Place next, PlaceController placeController, HeaderView headerView, DwoGlobalVars vars) {
		super();
		this.clientFactory = clientFactory;
		this.next = next;
		this.placeController = placeController;
		this.headerView = headerView;
		this.instance = vars;
	}

	@Override
	public Promise<Void> call(Promise<Void> resolved) throws Exception {

		DomUserFull currentUser = instance.getCurrentUser();
		RoleType roleType = instance.getRoleType();
		headerView.setUserAndRole(currentUser, roleType);
		if(next == null)
		{ // Niet meer relevant: switch naar gwtclient als test en teacher
//		  boolean test = "test".equals(DWOplayer.PARAMETERS.getDwoEnv()); // FIXME GERT test = true dan altijd een switch bij docent
//		  boolean teacher = RoleType.TEACHER == roleType;
//		  if ( teacher && test && ! Actions.isAvailable()) // switch naar gwtclient bij test, als teacher en niet embedded in gwtclient
//		  {
//		    return gotoGwtClient("WELCOME");
//		  }
		  DWOplayer.gotoCourses();
		}
		else
			placeController.goTo(next);
		return null;

	}
}