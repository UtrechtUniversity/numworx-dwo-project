package nl.uu.fi.dwo.lms.gwtclient.gwt.welcome;

import com.google.web.bindery.event.shared.EventBus;
import java.util.logging.Logger;

import javax.inject.Inject;

import jsinterop.annotations.JsMethod;
import nl.uu.fi.dwo.lms.gwtclient.gwt.DwoGlobalVars;
import nl.uu.fi.dwo.lms.gwtclient.gwt.dagger.RoleScope;
import nl.uu.fi.dwo.lms.gwtclient.gwt.locale.GwtClientMessages;
import nl.uu.fi.dwo.lms.gwtclient.gwt.ui.BasicDisplay;
import nl.uu.fi.dwo.rest.locale.DwoLocalesForGWT;

/**
 * Login Presenter.
 *
 * @author G.A.J. van der Plas
 */
@RoleScope
public class WelcomePresenter {

    private static final Logger LOG = Logger.getLogger(WelcomePresenter.class.getName());
    private Display view;
    private DwoLocalesForGWT resourceBindings = DwoLocalesForGWT.instance;
    private final GwtClientMessages rb;

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
        this.init();
    }

    /**
     * @return the resourceBindings
     */
    public DwoLocalesForGWT getResourceBindings() {
        return resourceBindings;
    }

    /**
     * @param resourceBindings the resourceBindings to set
     */
    public void setResourceBindings(DwoLocalesForGWT resourceBindings) {
        this.resourceBindings = resourceBindings;
    }

    public interface Display extends BasicDisplay {
        public void setDefaultText();
        public void setWelcomeText(String html);
    }

    @Inject WelcomePresenter(EventBus anEventBus, DwoGlobalVars aDwoGlobalVars, GwtClientMessages rb) {
        this.rb = rb;
    }

//    @JsMethod not required unless testing stuff.
    public void init() {
        view.clear();
        view.init();
        view.setWelcomeText(rb.welcomeText());
    }

}
