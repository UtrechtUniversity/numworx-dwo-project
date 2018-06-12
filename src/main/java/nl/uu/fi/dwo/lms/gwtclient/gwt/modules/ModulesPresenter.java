package nl.uu.fi.dwo.lms.gwtclient.gwt.modules;

import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.user.client.Window.Location;
import com.google.web.bindery.event.shared.EventBus;

import fi.dwo.gwt.lib.rest.util.Base64;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Promises;

import nl.uu.fi.dwo.lms.gwtclient.gwt.DwoGlobalVars;
import nl.uu.fi.dwo.lms.gwtclient.gwt.SwitchViewEvent;
import nl.uu.fi.dwo.lms.gwtclient.gwt.account.AccountService;
import nl.uu.fi.dwo.lms.gwtclient.gwt.ui.AlertDialogWithOKEvent;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;

/**
 * Login Presenter.
 *
 * @author G.A.J. van der Plas
 */
public class ModulesPresenter {

    private static final Logger LOG = Logger.getLogger(ModulesPresenter.class.getName());
    private DwoGlobalVars dwoGlobalVars;
    private EventBus eventBus;
    private Display view;
    private String url="/dwo/tablet/DWOplayer.html";
    private AccountService account;
    private Promise<String> init;

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

    public ModulesPresenter(EventBus anEventBus, DwoGlobalVars aDwoGlobalVars) {
        eventBus = anEventBus;
        dwoGlobalVars = aDwoGlobalVars;
        account = new AccountService(dwoGlobalVars); // alleen voor getBearerToken.
    }

//    @JsMethod not required unless testing stuff.
    public void show() {
      if (init == null || (init.isDone() && init.getFailure() != null)) {
        init();
      } 
        init.then(p-> {
          LOG.info("switch to modules view " + p.getValue());
          eventBus.fireEvent(new SwitchViewEvent(SwitchViewEvent.SelectedView.MODULESVIEW));
          return null;
        });
    }

    public void init() {
      view.clear();
      init = account.getBearerToken().then(this::gotToken,this::fail);
    }

    /*
     * bearer token has arrived:
     */
    public Promise<String> gotToken(Promise<String> resolved) {
     String token = "2\f" + resolved.getValue(); //format 2
     StringBuilder u = new StringBuilder(url);
     u.append( "?a=" ) .append (Base64.btoa(token)); // User Auth Token
     u.append( "&header=none");
     String profile = Location.getParameter("profile");
     if(profile == null || profile.isEmpty()) profile = "77";
     u.append("&profile=").append(profile);
     String locale = LocaleInfo.getCurrentLocale().getLocaleName();
     if ("default".equals(locale) ) locale =  "nl";
     u.append("&locale=").append(locale);
     String string = u.toString();
     LOG.info("open URL " + string);
     view.openUrl(string);
     return Promises.resolved(string);
    }
    
    /*
     * send failure event.
     */
    public void fail(Promise<?> resolved) {
      Throwable fail = resolved.getFailure();
      if (fail instanceof Dwo2Exception) {
          LOG.log(Level.SEVERE, fail.getMessage());
          eventBus.fireEvent(new AlertDialogWithOKEvent((Dwo2Exception) fail)); // FIXME which event type?
      } else {
          LOG.log(Level.SEVERE, fail.getMessage());
          eventBus.fireEvent(new AlertDialogWithOKEvent(fail.getMessage()));
      }
  }

}
