package nl.uu.fi.dwo.lms.gwtclient.gwt.personen;

import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.user.client.Window.Location;
import com.google.web.bindery.event.shared.EventBus;

import fi.dwo.gwt.lib.rest.util.Base64;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.osgi.util.promise.Promise;

import nl.uu.fi.dwo.lms.gwtclient.gwt.DwoGlobalVars;
import nl.uu.fi.dwo.lms.gwtclient.gwt.account.AccountService;
import nl.uu.fi.dwo.lms.gwtclient.gwt.ui.AlertDialogWithOKEvent;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;

/**
 * Login Presenter.
 *
 * @author G.A.J. van der Plas
 */
public class PersonenPresenter {

    private static final Logger LOG = Logger.getLogger(PersonenPresenter.class.getName());
    private DwoGlobalVars dwoGlobalVars;
    private EventBus eventBus;
    private Display view;
    private PersonenService personen;

    /**
     * @return the view
     */
    public Display getView() {
        return view;
    }

    /**
     * @param view the view to set
     */
    public void setView(Display view) {
        this.view = view;
    }

    public interface Display {
        public void clear();
        public void openUrl(String url);
    }

    public PersonenPresenter(EventBus anEventBus, DwoGlobalVars aDwoGlobalVars) {
        eventBus = anEventBus;
        dwoGlobalVars = aDwoGlobalVars;
        personen = new PersonenService(dwoGlobalVars); // alleen voor getBearerToken.
    }

//    @JsMethod not required unless testing stuff.
    public void init() {
        view.clear();
    }

//    /*
//     * bearer token has arrived:
//     */
//    public Promise<Void> gotToken(Promise<String> resolved) {
//     String token = "2\f" + resolved.getValue(); //format 2
//     StringBuilder u = new StringBuilder(url);
//     u.append( "?a=" ) .append (Base64.btoa(token)); // User Auth Token
//     u.append( "&header=none");
//     String profile = Location.getParameter("profile");
//     if(profile == null || profile.isEmpty()) profile = "77";
//     u.append("&profile=").append(profile);
//     String locale = LocaleInfo.getCurrentLocale().getLocaleName();
//     if ("default".equals(locale) ) locale =  "nl";
//     u.append("&locale=").append(locale);
//     LOG.info("open URL " + u);
//     view.openUrl(u.toString());
//     return null;
//    }
//    
//    /*
//     * send failure event.
//     */
//    public void fail(Promise<?> resolved) {
//      Throwable fail = resolved.getFailure();
//      if (fail instanceof Dwo2Exception) {
//          LOG.log(Level.SEVERE, fail.getMessage());
//          eventBus.fireEvent(new AlertDialogWithOKEvent((Dwo2Exception) fail)); // FIXME which event type?
//      } else {
//          LOG.log(Level.SEVERE, fail.getMessage());
//          eventBus.fireEvent(new AlertDialogWithOKEvent(fail.getMessage()));
//      }
//  }

}
