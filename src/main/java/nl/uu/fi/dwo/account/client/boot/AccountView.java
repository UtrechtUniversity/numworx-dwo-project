package nl.uu.fi.dwo.account.client.boot;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import java.util.logging.Logger;

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
    PasswordTextBox passwordTextBox;
    @UiField
    PasswordTextBox newPasswordTextBox;
    @UiField
    PasswordTextBox newPasswordAgainTextBox;
    @UiField
    Button resetBtn;
    @UiField
    Button updateBtn;

    public class ResultData {

        int width;
        int height;
        String[][] data; //height, width
    }

    public AccountView(AccountPresenter ap) {
        accountPresenter = ap;
        accountPresenter.setView(this);
        usernameText.setEnabled(true);
        initWidget(uiBinder.createAndBindUi(this));
        //controller must be before clicks occur
        //reset.addClickHandler(this);
    }

    @Override
    public void init() {
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
        //            scoResultsPresenter.selectRow(schoolIndex);

        if (event.getSource() == resetBtn) {
//            accountPresenter.goBackToResults();
        } else if (event.getSource() == updateBtn) {

        }
    }

    @Override
    public void updateView(String username, String firstName, String insertion, String familyName) {
        usernameText.setText(username);
        firstNameText.setText(firstName);
        insertionText.setText(insertion);
        familyNameText.setText(familyName);
    }

}
