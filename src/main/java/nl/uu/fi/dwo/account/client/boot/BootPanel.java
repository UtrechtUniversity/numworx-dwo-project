package nl.uu.fi.dwo.account.client.boot;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import nl.uu.fi.dwo.account.client.LoginPanel;

/**
 * BootPanel, boots the app, initializes with the server, requests a login.
 *
 * @author G.A.J. van der Plas <G.A.J.vanderPlas@uu.nl>
 */
public class BootPanel extends Composite implements EntryPoint {

    interface MyUiBinder extends UiBinder<Widget, BootPanel> {
    }
    private static final MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

    @UiField(provided = true)
    Widget loginWidget;

    public BootPanel() {
        loginWidget = new LoginPanel();
        initWidget(loginWidget);
        initWidget(uiBinder.createAndBindUi(this));
    }

    @Override
    public void onModuleLoad() {
        RootLayoutPanel.get().add(this);
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
