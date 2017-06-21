package nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import java.util.logging.Logger;

/**
 * Panel to configure and add a schoolclass.
 *
 * @author Gert van der Plas
 */
public class EditStudentView extends Composite implements EditStudentPresenter.Display, ClickHandler {

    private static Logger LOG = Logger.getLogger(EditStudentView.class.getName());

    interface MyUiBinder extends UiBinder<Widget, EditStudentView> {
    }
    private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

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
    PasswordTextBox newPasswordTextBox;
    @UiField
    PasswordTextBox newPasswordAgainTextBox;
    @UiField
    Button resetBtn;
    @UiField
    Button updateBtn;

    EditStudentPresenter editStudentPresenter;
    final DialogBox dialogBox = new DialogBox();

    public EditStudentView(EditStudentPresenter ep) {
        editStudentPresenter = ep;
        ep.setView(this);
        initWidget(uiBinder.createAndBindUi(this));
        usernameText.setReadOnly(true);
        //controller must be before clicks occur
        resetBtn.addClickHandler(this);
        updateBtn.addClickHandler(this);
    }

    @Override
    public void init() {
        clear();
    }

    @Override
    public void clear() {
        usernameText.setText("");
        firstNameText.setText("");
        insertionText.setText("");
        familyNameText.setText("");
        emailText.setText("");    }

    public void onClick(ClickEvent event) {
        if (event.getSource() == resetBtn) {
            newPasswordTextBox.setText("");
            newPasswordAgainTextBox.setText("");
            editStudentPresenter.updateUserData();
        } else if (event.getSource() == updateBtn) {
            dialogBox.hide();
            editStudentPresenter.updateUser(
                    firstNameText.getText(),
                    insertionText.getText(),
                    familyNameText.getText(),
                    emailText.getText(),
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

    @Override
    public void showDialog(String username, String firstName, String insertion, String familyName, String email) {
        usernameText.setText(username);
        firstNameText.setText(firstName);
        insertionText.setText(insertion);
        familyNameText.setText(familyName);
        emailText.setText(email);
        
        if (dialogBox.getWidget() == null) {
            dialogBox.add(this.asWidget());
            dialogBox.setModal(true);
            dialogBox.setAutoHideEnabled(true);
            dialogBox.setGlassEnabled(true);
            dialogBox.setAnimationEnabled(true);
            dialogBox.center();
        }
        dialogBox.show();
    }

}
