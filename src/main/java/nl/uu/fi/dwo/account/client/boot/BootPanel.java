package nl.uu.fi.dwo.account.client.boot;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.uu.fi.dwo.account.client.DwoGlobalVars;
import nl.uu.fi.dwo.account.client.boot.Results.ResultPanel;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;

/**
 * BootPanel, boots the app, initializes with the server, requests a login.
 *
 * @author G.A.J. van der Plas <G.A.J.vanderPlas@uu.nl>
 */
public class BootPanel extends Composite implements EntryPoint {

    private static final Logger LOG = Logger.getLogger(BootPanel.class.getName());
    private DwoGlobalVars dwoGlobalVars;

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
    @UiField(provided = true)
    Widget resultWidget = new ResultPanel();

    public BootPanel() {

    }

    @Override
    public void onModuleLoad() {
        try {
            dwoGlobalVars = new DwoGlobalVars();
        } catch (Dwo2Exception ex) {            
            LOG.log(Level.SEVERE, null, ex);
            PopupPanel popup = new PopupPanel();
            popup.add(new Label("Programmers-error"));
        }
        initWidget(uiBinder.createAndBindUi(this));
        ((LoginPanel) loginWidget).setParent(this);
//        mainDeckPanel.add(loginWidget);
        mainDeckPanel.showWidget(0);
        ((ResultPanel) resultWidget).setParent(this);
//        mainDeckPanel.add(resultWidget);
        LOG.log(Level.INFO, "Showing loginPanel.");
        
        RootLayoutPanel.get().add(this);
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public int getMainDeckCount(){
        return mainDeckPanel.getWidgetCount();
    }
}
