/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.uu.fi.dwo.account.client;

import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import fi.dwo.rest.locale.Dwo2ExceptionsForGWT;

/**
 *
 * @author Gert van der Plas
 */
public class LoginStatusPanel extends VerticalPanel {
    Label login = new Label("usercode:");
    Label schoolClass = new Label("schoolclass name:");
    Label status = new Label("logged in: false");
    Label localization = new Label("Localisation: "+LocaleInfo.getLocaleNativeDisplayName("en-gb"));
    
    Label test = new Label("test: "+Dwo2ExceptionsForGWT.instance.Dwo2ExceptionCode_GUI_AnIncorrectPasswordWasGiven());
    
    public LoginStatusPanel() {
        this.add(login);
        this.add(schoolClass);
        this.add(status);
        this.add(localization);
        this.add(test);
    }

    public void setSchoolClass(String schoolClassName){
        schoolClass.setText("schoolclass name:"+schoolClassName);
    }
    public void setStatus(String usercode, boolean loggedIn ){
        login.setText("usercode: "+ usercode);
        status.setText("logged in: "+Boolean.valueOf(loggedIn).toString());
        localization.setText("Localisation: "+LocaleInfo.getLocaleNativeDisplayName("en-gb"));
    }
}
