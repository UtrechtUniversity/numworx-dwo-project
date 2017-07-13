package nl.uu.fi.dwo.lms.gwtclient.gwt.account;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

import java.util.logging.Logger;

import nl.uu.fi.dwo.rest.locale.DwoLocalesForGWT;

/**
 * GWT Panel that handles switching the role.
 *
 * @author G.A.J. van der Plas
 */
public class AccountView extends Composite implements ClickHandler, AccountPresenter.Display {
    
    private static final Logger LOG = Logger.getLogger(AccountView.class.getName());
    
    interface MyUiBinder extends UiBinder<Widget, AccountView> {
    }
    private static final MyUiBinder uiBinder = GWT.create(MyUiBinder.class);
    
    private AccountPresenter accountPresenter;
    
    @UiField
    TextBox usernameText;
    @UiField
    TextBox firstNameText;
    @UiField
    TextBox insertionText;
    @UiField
    TextBox familyNameText;
    @UiField
    TextBox emailText;
    @UiField
    PasswordTextBox passwordTextBox;
    @UiField
    PasswordTextBox newPasswordTextBox;
    @UiField
    PasswordTextBox newPasswordAgainTextBox;
    @UiField
    Button resetBtn;
    @UiField
    Button updateBtn;
    
    @UiField
    DwoLocalesForGWT rb = DwoLocalesForGWT.instance;
    
    public AccountView(AccountPresenter ap) {
        accountPresenter = ap;
        accountPresenter.setView(this);
        initWidget(uiBinder.createAndBindUi(this));
        usernameText.setReadOnly(false);
        //controller must be before clicks occur
        resetBtn.addClickHandler(this);
        updateBtn.addClickHandler(this);
    }
    
    @Override
    public void init() {
        passwordTextBox.setText("");
//        //create table
//        String nulLabel = "Result";
//        HTML l = new HTML("<div style=\"text-align: left; background-color: #555555; padding: 2px; overflow auto;\">" + nulLabel + "</div>");
//
//        flexTable.setWidget(0, 0, l);
//        backBtn.getElement().getStyle().setVisibility(Style.Visibility.VISIBLE);
    }
    
    @Override
    public void clear() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    public void onClick(ClickEvent event) {
        if (event.getSource() == resetBtn) {
            passwordTextBox.setText("");
            newPasswordTextBox.setText("");
            newPasswordAgainTextBox.setText("");
            accountPresenter.updateUserData();
        } else if (event.getSource() == updateBtn) {
                accountPresenter.updateUser(
                        firstNameText.getText(),
                        insertionText.getText(),
                        familyNameText.getText(),
                        emailText.getText(),
                        passwordTextBox.getText(),
                        newPasswordTextBox.getText(),
                        newPasswordAgainTextBox.getText());
        }
    }
    
    @Override
    public void updateView(String username, String firstName, String insertion, String familyName, String email) {
        usernameText.setText(username);
        firstNameText.setText(firstName);
        insertionText.setText(insertion);
        familyNameText.setText(familyName);
        emailText.setText(email);
    }
    
}
