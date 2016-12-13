package nl.uu.fi.dwo.account.client.boot;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.uu.fi.dwo.account.client.boot.Results.ResultPanel;

/**
 * BootPanel, boots the app, initializes with the server, requests a login.
 *
 * @author G.A.J. van der Plas <G.A.J.vanderPlas@uu.nl>
 */
public class BootPanel extends Composite implements EntryPoint {

    private static final Logger LOG = Logger.getLogger(BootPanel.class.getName());

    /**
     * @return the loginWidget
     */
    public Widget getLoginWidget() {
        return loginWidget;
    }

    /**
     * @param loginWidget the loginWidget to set
     */
    public void setLoginWidget(Widget loginWidget) {
        this.loginWidget = loginWidget;
    }

    interface MyUiBinder extends UiBinder<Widget, BootPanel> {
    }
    private static final MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

    @UiField
    DeckPanel mainDeckPanel = new DeckPanel();
    @UiField(provided = true)
    Widget loginWidget = new LoginPanel();
    Widget resultWidget = new ResultPanel();

    public BootPanel() {
        initWidget(uiBinder.createAndBindUi(this));
        LOG.log(Level.INFO, "Widget Count:" + mainDeckPanel.getWidgetCount() + ".");
        ((LoginPanel) loginWidget).setParent(this);
        mainDeckPanel.add(resultWidget);
        mainDeckPanel.showWidget(0);
        LOG.log(Level.INFO, "Widget Count:" + mainDeckPanel.getWidgetCount() + ".");
    }

    @Override
    public void onModuleLoad() {
        RootLayoutPanel.get().add(this);
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
