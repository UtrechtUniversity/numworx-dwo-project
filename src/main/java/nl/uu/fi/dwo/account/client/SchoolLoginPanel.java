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
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import fi.dwo.rest.dom.entities.DomUserFull;
import java.util.logging.Logger;

/**
 *
 * @author G.A.J. van der Plas
 */
public class SchoolLoginPanel extends VerticalPanel implements ClickHandler {

    Logger LOG = Logger.getLogger("Account");

    //SchoolLoginController control;
    PopupPanel popup;
    Button cnlBtn;
    Button newSchoolLoginBtn;
    DomUserFull user;

    public PopupPanel getPopup() {
        return popup;
    }

    public void setPopup(PopupPanel popup) {
        this.popup = popup;
    }

    SchoolLoginPanel(DomUserFull user) {
        init(user);
 //       control = new SchoolLoginController(this, user);

    }

    private void init(DomUserFull user) {
        this.user = user;
        this.setSize("400", "500");
        Grid g = new Grid(5, 3);
        // Put some values in the grid cells.
        g.setText(0, 0, "school");
        g.setText(0, 1, "login");
        g.setText(0, 2, "delete");
        TextBox login = new TextBox();
        login.setText(user.getUserName());
        g.setWidget(1, 0, login);
        TextBox name = new TextBox();
        name.setText(user.getUniqueDisplayName());
        g.setWidget(1, 1, name);
        TextBox delete = new TextBox();
        delete.setText("X");
        g.setWidget(1, 2, delete);

        // Just for good measure, let's put a button in the center.
        newSchoolLoginBtn = new Button("New SchoolLogin");
        newSchoolLoginBtn.addClickHandler(this);
        g.setWidget(2, 0, newSchoolLoginBtn);
        cnlBtn = new Button("CANCEL");
        cnlBtn.addClickHandler(this);
        g.setWidget(2, 1, cnlBtn);
        this.add(g);
//        try {
//            control.init();
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
            Window.alert("CANCEL!");
            popup.hide();
        } else if (event.getSource() == newSchoolLoginBtn) {
            Window.alert("OK!");
//            try {
//                control.Update();
//            } catch (Dwo2Exception ex) {
//                Logger.getLogger(ProfilePanel.class.getName()).log(Level.SEVERE, null, ex);
//                Window.alert("Update Failed.");
//            }
            popup.hide();
        }
    }
}
