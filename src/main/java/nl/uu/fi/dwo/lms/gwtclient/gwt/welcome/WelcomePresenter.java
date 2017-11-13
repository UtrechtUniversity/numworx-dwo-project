package nl.uu.fi.dwo.lms.gwtclient.gwt.welcome;

import nl.uu.fi.dwo.lms.gwtclient.gwt.login.*;
import com.google.gwt.event.shared.EventBus;
import java.util.logging.Level;
import java.util.logging.Logger;
import jsinterop.annotations.JsMethod;
import nl.uu.fi.dwo.lms.gwtclient.gwt.gui.DialogEvent;
import nl.uu.fi.dwo.lms.gwtclient.gwt.DwoGlobalVars;
import nl.uu.fi.dwo.rest.dom.entities.RoleType;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.exceptions.Dwo2ExceptionCode;
import nl.uu.fi.dwo.rest.locale.DwoLocalesForGWT;
import org.osgi.util.promise.Failure;
import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Success;

/**
 * Login Presenter.
 *
 * @author G.A.J. van der Plas
 */
public class WelcomePresenter {

    private static final Logger LOG = Logger.getLogger(WelcomePresenter.class.getName());
    private DwoGlobalVars dwoGlobalVars;
    private EventBus eventBus;
    private Display view;
    private DwoLocalesForGWT resourceBindings = DwoLocalesForGWT.instance;

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

    /**
     * @return the resourceBindings
     */
    @JsMethod
    public DwoLocalesForGWT getResourceBindings() {
        return resourceBindings;
    }

    /**
     * @param resourceBindings the resourceBindings to set
     */
    public void setResourceBindings(DwoLocalesForGWT resourceBindings) {
        this.resourceBindings = resourceBindings;
    }

    public interface Display {

    }

    public WelcomePresenter(EventBus anEventBus, DwoGlobalVars aDwoGlobalVars) {
        eventBus = anEventBus;
        dwoGlobalVars = aDwoGlobalVars;
        init();
    }

    final public void init() {
//        getView().setUsername(defaultUsername);
//        getView().setPassword(defaultPassword);
    }

    /**
     * Welcome text loaded depending on locale as a html-formatted resource
     * file.
     *
     * @return
     */
    public String getWelcomeText() {
        return "<h1>Welkome Page</h1>\n"
                + "            \n"
                + "            <p>Welkom text voor de welkom pagina.</p>";
    }

}
