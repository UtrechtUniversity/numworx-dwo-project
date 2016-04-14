/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.uu.fi.dwo.account.client;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import fi.dwo.rest.dom.entities.DomUser;
import fi.dwo.rest.dom.entities.DomUserFull;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author G.A.J. van der Plas
 */
public class ProfilePanel extends VerticalPanel implements ClickHandler {

    Logger LOG = Logger.getLogger("Account");

    ProfileController control = new ProfileController();
    PopupPanel popup;
    Button cnlBtn;
    Button okBtn;
    DomUser user;
    TextBox login = new TextBox();
    TextBox givenName = new TextBox();
    TextBox insertion = new TextBox();
    TextBox familyName = new TextBox();
    TextBox email = new TextBox();
    PasswordTextBox password = new PasswordTextBox();
    PasswordTextBox newPassword = new PasswordTextBox();

    public PopupPanel getPopup() {
        return popup;
    }

    public void setPopup(PopupPanel popup) {
        this.popup = popup;
    }

    ProfilePanel(DomUserFull user) {
        init(user);
    }

    private void init(DomUserFull user) {
        LOG.log(Level.INFO, "testing");
        this.setSize("400", "500");

        Grid g = new Grid(10, 2);
        g.getColumnCount();
        g.getRowCount();
        // Put some values in the grid cells.
        g.setText(0, 0, "login");
        g.setText(0, 1, user.getUserName());
        login.setText(user.getUserName());

        g.setText(1, 0, "given name");
        givenName.setText(user.getGivenName());
        g.setWidget(1, 1, givenName);

        g.setText(2, 0, "insertion");
        insertion.setText(user.getInsertion());
        g.setWidget(2, 1, insertion);

        g.setText(3, 0, "family name");
        familyName.setText(user.getFamilyName());
        g.setWidget(3, 1, familyName);

        g.setText(4, 0, "email");
        email.setText(user.getEmail());
        g.setWidget(4, 1, email);

        g.setText(6, 0, "password");
        password.setText("");
        g.setWidget(6, 1, password);

        g.setText(7, 0, "new password");
        newPassword.setText("");
        g.setWidget(7, 1, newPassword);

        g.setText(8, 0, "new password again");
        PasswordTextBox newPasswordAgain = new PasswordTextBox();
        newPasswordAgain.setText("");
        g.setWidget(8, 1, newPasswordAgain);

        // Just for good measure, let's put a button in the center.
        okBtn = new Button("OK");
        okBtn.addClickHandler(this);
        g.setWidget(9, 0, okBtn);
        cnlBtn = new Button("CANCEL");
        cnlBtn.addClickHandler(this);
        g.setWidget(9, 1, cnlBtn);
        // You can use the CellFormatter to affect the layout of the grid's cells.
        //g.getCellFormatter().setWidth(0, 2, "256px");
        this.add(g);
//        try {
//            props.init();
//        } catch (Dwo2Exception ex) {
//            Logger.getLogger(ProfilePanel.class.getName()).log(Level.SEVERE, null, ex);
//                Window.alert("Init Failed.");
//        }
    }

    @Override
    public void onClick(ClickEvent event) {
        //logger.log(Level.INFO, "object {0}", new Object[]{event.getSource()});
        Window.alert(event.getSource().toString());
        if (event.getSource() == cnlBtn) {
            LOG.log(Level.INFO, "SCancelling user profile update.");
            Window.alert("CANCEL!");
            popup.hide();
        } else if (event.getSource() == okBtn) {
            DomUserFull user = new DomUserFull();
            user.setEmail(email.getText());
            user.setFamilyName(familyName.getText());
            user.setGivenName(givenName.getText());
            user.setInsertion(insertion.getText());
            user.setPassword(password.getText());
            more
            LOG.log(Level.INFO, "Sending data to server.");
            control.update();
            popup.hide();
        }
    }
}
