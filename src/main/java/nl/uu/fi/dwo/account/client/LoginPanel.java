/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.uu.fi.dwo.account.client;

import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Gert van der Plas
 */
public class LoginPanel extends VerticalPanel {

    private static final Logger LOG = Logger.getLogger(LoginPanel.class.getName());

    Label loginLabel = new Label("usercode:");
    Label passLabel = new Label("password:");
    TextBox loginText = new TextBox();
    PasswordTextBox passText = new PasswordTextBox();
    Label localization = new Label("Localisation: " + LocaleInfo.getLocaleNativeDisplayName("en-gb"));

    //Dwo2ExceptionsForGWT.instance.Dwo2ExceptionCode_GUI_AnIncorrectPasswordWasGiven());
    public LoginPanel() {
        Grid g = new Grid(2, 2);
        g.setWidget(0, 0, loginLabel);
        loginText.setText("");
        g.setWidget(0, 1, loginText);
        g.setWidget(1, 0, passLabel);
        passText.setText("");
        g.setWidget(1, 1, passText);
        this.add(g);
    }

    public void setStatus(String usercode, boolean loggedIn) {
        loginLabel.setText("usercode: " + usercode);
        passLabel.setText("logged in: " + Boolean.valueOf(loggedIn).toString());
        localization.setText("Localisation: " + LocaleInfo.getLocaleNativeDisplayName("en-gb"));
    }

    public String getUserCode() {
        LOG.log(Level.INFO,""+loginText.getText());
        return loginText.getText();
    }

    public String getPassWord() {
        return passText.getText();
    }

    public void setUserCode(String username) {
        loginText.setText(username);
    }

    public void setPassWord(String password) {
        passText.setText(password);
    }

}
