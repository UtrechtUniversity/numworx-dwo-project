/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.uu.fi.dwo.account.client;

import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 *
 * @author Gert van der Plas
 */
public class LoginStatusPanel extends VerticalPanel {
    Label login = new Label("usercode:");
    Label status = new Label("logged in: false");
    
    public LoginStatusPanel() {
        this.add(login);
        this.add(status);
    }
    
    public void setStatus(String usercode, boolean loggedIn ){
        login.setText("usercode: "+ usercode);
        status.setText(Boolean.valueOf(loggedIn).toString());
    }
}
