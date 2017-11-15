package nl.uu.fi.dwo.lms.gwtclient.gwt.welcome;

import com.google.gwt.event.shared.EventBus;
import java.util.logging.Logger;
import jsinterop.annotations.JsMethod;
import nl.uu.fi.dwo.lms.gwtclient.gwt.DwoGlobalVars;
import nl.uu.fi.dwo.rest.locale.DwoLocalesForGWT;

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

    public interface Display {
        public void clear();
        public void setWelcomeText(String html);
    }

    public WelcomePresenter(EventBus anEventBus, DwoGlobalVars aDwoGlobalVars) {
        eventBus = anEventBus;
        dwoGlobalVars = aDwoGlobalVars;
    }

    @JsMethod
    public void init() {
        view.clear();
        view.setWelcomeText("<h1>Welkom</h1>\n"
                + "            \n"
                + "            <p>Rechtsboven vind u een menu knop achter uw naam. In het menu vind u de volgende opties:</p>"
                + "<ul><li>In <b>account-settings</b> kunt u uw naam, email-adres en wachtwoord"
                + " wijzigen.</li><li> In <b>docentrollen</b> kunt u van school veranderen.</li><li> In <b>mijn "
                + "klassen</b> kunt klassen, studenten, docenten en modules beheren.</liliul>"
                + "<li>In de <b>resultatenbrowser</b> vind u het overzicht van de resultaten "
                + "van de klassen voor toegekende modules.</li></ul>");
    }

}
