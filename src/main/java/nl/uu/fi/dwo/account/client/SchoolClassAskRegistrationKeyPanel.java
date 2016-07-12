package nl.uu.fi.dwo.account.client;

import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import java.util.logging.Logger;

/**
 *
 *
 * @author Gert van der Plas
 */
public class SchoolClassAskRegistrationKeyPanel extends VerticalPanel {

    private static final Logger LOG = Logger.getLogger(SchoolClassAskRegistrationKeyPanel.class.getName());

    private SchoolClassStudentController control;
    private Label schoolClassLabel = new Label("schoolClassName:");
    private Label regKeyLabel = new Label("registrationKey:");
    private TextBox schoolClassNameText = new TextBox();
    private PasswordTextBox regKeyText = new PasswordTextBox();
    private Label localization = new Label("Localisation: " + LocaleInfo.getLocaleNativeDisplayName("en-gb"));
    private PopupPanel popup;

    //Dwo2ExceptionsForGWT.instance.Dwo2ExceptionCode_GUI_AnIncorrectPasswordWasGiven());
    public SchoolClassAskRegistrationKeyPanel() {
        Grid g = new Grid(2, 2);
        g.setWidget(0, 0, schoolClassLabel);
        schoolClassNameText.setText("");
        schoolClassNameText.setReadOnly(true);
        g.setWidget(0, 1, schoolClassNameText);
        g.setWidget(1, 0, regKeyLabel);
        regKeyText.setText("");
        g.setWidget(1, 1, regKeyText);
        this.add(g);
    }

    /**
     * @return the regKeyText
     */
    public String getRegKey() {
        return regKeyText.getText();
    }

    /**
     * @param key the regKeyText to set
     */
    public void setRegKey(String key) {
        this.regKeyText.setText(key);
    }

    /**
     * @return the regKeyText
     */
    public String getSchoolClassName() {
        return schoolClassNameText.getText();
    }

    /**
     * @param key the regKeyText to set
     */
    public void setSchoolClassName(String name) {
        this.schoolClassNameText.setText(name);
    }

    void setPopup(PopupPanel popup) {
        this.popup = popup;
    }

    /**
     * @return the control
     */
    public SchoolClassStudentController getControl() {
        return control;
    }

    /**
     * @param control the control to set
     */
    public void setControl(SchoolClassStudentController control) {
        this.control = control;
    }

}
