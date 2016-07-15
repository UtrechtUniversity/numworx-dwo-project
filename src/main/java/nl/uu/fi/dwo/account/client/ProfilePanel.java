package nl.uu.fi.dwo.account.client;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import fi.dwo.gwt.lib.rest.CallManagers.MD5;
import fi.dwo.rest.dom.entities.DomUser;
import fi.dwo.rest.dom.entities.DomUserFull;
import fi.dwo.rest.exceptions.Dwo2ExceptionCode;
import java.util.logging.Level;
import java.util.logging.Logger;
import fi.dwo.rest.locale.DwoLocalesForGWT;

/**
 * Allows user update.
 *
 * @author G.A.J. van der Plas
 */
public class ProfilePanel extends VerticalPanel implements ClickHandler {

    Logger LOG = Logger.getLogger("Account");

    ProfileController control;
    PopupPanel popup;
    Button cnlBtn;
    Button updateBtn;
    DomUserFull user;
    TextBox login = new TextBox();
    TextBox givenName = new TextBox();
    TextBox insertion = new TextBox();
    TextBox familyName = new TextBox();
    TextBox email = new TextBox();
    PasswordTextBox password = new PasswordTextBox();
    PasswordTextBox newPassword = new PasswordTextBox();
    PasswordTextBox newPasswordAgain = new PasswordTextBox();

    public PopupPanel getPopup() {
        return popup;
    }

    public void setPopup(PopupPanel popup) {
        this.popup = popup;
    }

    ProfilePanel(DomUserFull user) {
        control = new ProfileController(this, user);
        init(user);
    }

    public void init(DomUserFull aUser) {
        user = aUser;
        this.setSize("400", "500");

        Grid g = new Grid(10, 2);
        g.getColumnCount();
        g.getRowCount();
        // Put some values in the grid cells.
        g.setText(0, 0, DwoLocalesForGWT.instance.GUI_Username());
        g.setText(0, 1, user.getUserName());
        login.setText(user.getUserName());

        g.setText(1, 0, DwoLocalesForGWT.instance.GUI_GivenName());
        givenName.setText(user.getGivenName());
        g.setWidget(1, 1, givenName);

        g.setText(2, 0, DwoLocalesForGWT.instance.GUI_Insertion());
        insertion.setText(user.getInsertion());
        g.setWidget(2, 1, insertion);

        g.setText(3, 0, DwoLocalesForGWT.instance.GUI_FamilyName());
        familyName.setText(user.getFamilyName());
        g.setWidget(3, 1, familyName);

        g.setText(4, 0, DwoLocalesForGWT.instance.GUI_Email());
        email.setText(user.getEmail());
        g.setWidget(4, 1, email);

        g.setText(6, 0, DwoLocalesForGWT.instance.GUI_Password());
        password.setText("");
        g.setWidget(6, 1, password);

        g.setText(7, 0, DwoLocalesForGWT.instance.GUI_NewPassword());
        newPassword.setText("");
        g.setWidget(7, 1, newPassword);

        g.setText(8, 0, DwoLocalesForGWT.instance.GUI_NewPasswordAgain());
        newPasswordAgain.setText("");
        g.setWidget(8, 1, newPasswordAgain);

        // Just for good measure, let's put a button in the center.
        cnlBtn = new Button(DwoLocalesForGWT.instance.GUI_Button_Cancel());
        cnlBtn.addClickHandler(this);
        g.setWidget(9, 0, cnlBtn);
        updateBtn = new Button(DwoLocalesForGWT.instance.GUI_Button_Update());
        updateBtn.addClickHandler(this);
        g.setWidget(9, 1, updateBtn);
        // You can use the CellFormatter to affect the layout of the grid's cells.
        //g.getCellFormatter().setWidth(0, 2, "256px");
        this.clear();
        this.add(g);
    }

    @Override
    public void onClick(ClickEvent event) {
        if (event.getSource() == cnlBtn) {
            LOG.log(Level.INFO, "Cancelling user profile update.");
            popup.hide();
        } else if (event.getSource() == updateBtn) {
            DomUserFull user = new DomUserFull();
            user.setUserName(control.getCurrentUser().getUserName());
            user.setSingleSchool(control.getCurrentUser().getSingleSchool());
//            user.setPassword(control.getCurrentUser().getPassword());
            user.setEmail(email.getText());
            user.setFamilyName(familyName.getText());
            user.setGivenName(givenName.getText());
            user.setInsertion(insertion.getText());
            if (MD5.md5(password.getText()).equals(control.getCurrentUser().getPassword())) 
            {
                if (!newPassword.getText().equals("") && newPassword.getText().equals(newPasswordAgain.getText())) 
                {
                    user.setPassword(MD5.md5(newPassword.getText()));
                }else{
                    user.setPassword(control.getCurrentUser().getPassword());
                }
                LOG.log(Level.INFO, "Sending data to server.");
                control.setUpdateUser(user);
                control.callUpdate();
                LOG.log(Level.INFO, "Data send to server.");
            } else {
                DwoViewer.showMessage(Dwo2ExceptionCode.GUI_AnIncorrectPasswordWasGiven);
            }
        }
    }

}
