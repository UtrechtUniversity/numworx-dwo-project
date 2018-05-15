package nl.uu.fi.dwo.lms.gwtclient.gwt.modules;

import com.google.web.bindery.event.shared.EventBus;
import java.util.logging.Logger;
import nl.uu.fi.dwo.lms.gwtclient.gwt.DwoGlobalVars;

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
    private String url="https://www.dwo.nl/leerling";

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

    public interface Display {
        public void clear();
        public void openUrl(String url);
    }

    public ModulesPresenter(EventBus anEventBus, DwoGlobalVars aDwoGlobalVars) {
        eventBus = anEventBus;
        dwoGlobalVars = aDwoGlobalVars;
    }

//    @JsMethod not required unless testing stuff.
    public void init() {
        view.clear();
        view.openUrl(url);
    }

}
