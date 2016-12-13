package nl.uu.fi.dwo.account.client.boot;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.uu.fi.dwo.account.client.boot.Results.ResultPanel;

/**
 * Panel that handles the login-authentication. 
 * 
 * @author G.A.J. van der Plas <G.A.J.vanderPlas@uu.nl>
 */
public class LoginPanel extends Composite implements ClickHandler {

    private static final Logger LOG = Logger.getLogger(LoginPanel.class.getName());

    interface MyUiBinder extends UiBinder<Widget, LoginPanel> {
    }
    private static final MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

    @UiField TextBox usernameText;
    @UiField PasswordTextBox passwordTextBox;
    @UiField CheckBox checkBox;
    @UiField Button loginBtn;
    
    private BootPanel parent;
    
    public void setParent(BootPanel aParent){
        parent=aParent;
    }
    
    
    /**
     * @return the parent
     */
    public BootPanel getParent() {
        return parent;
    }

    public LoginPanel() {        
        initWidget(uiBinder.createAndBindUi(this));    
        loginBtn.addClickHandler(this);
    }

    public void onClick(ClickEvent event) {
        if (event.getSource() == loginBtn) {
            usernameText.setValue("Clicked it");
            LOG.log(Level.INFO, "Simulating login switching to resultpanel.");              
            LOG.log(Level.INFO, "Widget Count:"+parent.mainDeckPanel.getWidgetCount()+".");
            parent.mainDeckPanel.showWidget(1);
            
        }
    }    
}
