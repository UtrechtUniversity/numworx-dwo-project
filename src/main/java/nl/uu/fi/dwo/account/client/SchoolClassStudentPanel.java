/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.uu.fi.dwo.account.client;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import fi.dwo.rest.dom.entities.DomSchoolClass;
import fi.dwo.rest.dom.entities.DomUser;
import fi.dwo.rest.dom.entities.DomUserFull;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author G.A.J. van der Plas
 */
public class SchoolClassStudentPanel extends VerticalPanel implements ClickHandler {

    private Logger LOG = Logger.getLogger("Account");

    private SchoolClassStudentController control;
    private PopupPanel popup;
    private Button loginBtn;
    private Button delBtn;
    private Button newBtn;
    private Button doneButton;
    private DomUser user;
    private TextBox schoolClass = new TextBox();

    public PopupPanel getPopup() {
        return popup;
    }

    public void setPopup(PopupPanel popup) {
        this.popup = popup;
    }

    SchoolClassStudentPanel(DomUserFull user) {
        init(user);
         control= new SchoolClassStudentController(this,user);
    }

    public void init(DomUserFull user) {
        control.setCurrentUser(user);
        this.setSize("400", "500");
        control.getSchoolClasses();
        Grid g = new Grid(control.getSchoolClasses().size()+1, 3);
        for(int i=0;i<control.getSchoolClasses().size();i++){
            g.setText(i, 0, control.getSchoolClasses().get(i).getSchoolClassName());
            loginBtn = new Button("login");
            loginBtn.addClickHandler(this);
            g.setWidget(i, 1, loginBtn);
            delBtn = new Button("del");
            delBtn.addClickHandler(this);
            g.setWidget(i, 2, delBtn);
        }

        // Just for good measure, let's put a button in the center.
        doneButton = new Button("Done");
        doneButton.addClickHandler(this);
        g.setWidget(control.getSchoolClasses().size(), 0, doneButton);
        newBtn = new Button("NEW");
        newBtn.addClickHandler(this);
        g.setWidget(control.getSchoolClasses().size(), 2, newBtn);
        this.clear();
        this.add(g);
    }

    @Override
    public void onClick(ClickEvent event) {
        if (event.getSource() == newBtn) {
            LOG.log(Level.INFO, "Should add new window for adding a schoolclass.");
            popup.hide();
        } else if (event.getSource() == doneButton) {
            LOG.log(Level.INFO, "Done, hiding window.");
            popup.hide();
        }
    }

    void setSchoolClasses(List<DomSchoolClass> schoolClasses) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
