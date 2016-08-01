package nl.uu.fi.dwo.account.client;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.HasDirection;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

import fi.dwo.rest.dom.entities.DomNewSchoolClass4Student;
import fi.dwo.rest.dom.entities.DomSchoolClass;
import fi.dwo.rest.locale.DwoLocalesForGWT;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 *
 * @author Gert van der Plas
 */
public class SchoolClassAskRegistrationKeyPanel extends VerticalPanel implements ClickHandler {

    private static final Logger LOG = Logger.getLogger(SchoolClassAskRegistrationKeyPanel.class.getName());

    private SchoolClassStudentController control;
    private Label schoolClassLabel = new Label(DwoLocalesForGWT.instance.GUI_SchoolclassName() + ":");
    private Label regKeyLabel = new Label(DwoLocalesForGWT.instance.GUI_SchoolClassRegistrationKey() + ":");
    private TextBox schoolClassNameText = new TextBox();
    private PasswordTextBox regKeyText = new PasswordTextBox();
    private PopupPanel popup;
    private Button addBtn;
    private Button closeBtn;
    private DomSchoolClass schoolClass;

    //Dwo2ExceptionsForGWT.instance.Dwo2ExceptionCode_GUI_AnIncorrectPasswordWasGiven());
    public SchoolClassAskRegistrationKeyPanel(SchoolClassStudentController aControl, DomSchoolClass sc) {
        control = aControl;
        schoolClass = sc;
        VerticalPanel vPanel = new VerticalPanel();
        Grid g = new Grid(2, 2);
        g.setWidget(0, 0, schoolClassLabel);
        schoolClassNameText.setText("");
        schoolClassNameText.setReadOnly(true);
        schoolClassNameText.setWidth("150px");
        g.setWidget(0, 1, schoolClassNameText);
        g.setWidget(1, 0, regKeyLabel);
        regKeyText.setText("");
        regKeyText.setWidth("150px");
        g.setWidget(1, 1, regKeyText);
        vPanel.add(g);
//        this.add(g);
        HorizontalPanel hPanel = new HorizontalPanel();
        hPanel.setHorizontalAlignment(HorizontalAlignmentConstant.endOf(HasDirection.Direction.DEFAULT));
//            hPanel.getElement().getStyle().setPadding(20, Unit.PX);
        closeBtn = new Button("Close");
        closeBtn.addClickHandler(this);
        addBtn = new Button("Add");
        addBtn.addClickHandler(this);
        addBtn.addStyleName("paddedHorizontalPanel");
        hPanel.add(addBtn);
        closeBtn.addStyleName("paddedHorizontalPanel");
        hPanel.add(closeBtn);
        vPanel.add(hPanel);
        this.add(vPanel);
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

    @Override
    public void onClick(ClickEvent event) {
        if (event.getSource() == addBtn) {
            LOG.log(Level.INFO, "Should add new window for adding a schoolclass.");
            DomNewSchoolClass4Student nsc = new DomNewSchoolClass4Student(schoolClass);
            nsc.setRegistrationKey(regKeyText.getText());
            control.registerStudentForSchoolClass(nsc, new AsyncCallback<Boolean>() {
                @Override
                public void onFailure(Throwable t) {
                    //fail and reset all the data.
                    Window.alert(t.getMessage());
                }

                @Override
                public void onSuccess(Boolean result) {
                    //update a view list
                    control.updateStudentsSchoolClassesInView();
                    popup.hide();
                }
            });
        } else if (event.getSource() == closeBtn) {
            LOG.log(Level.INFO, "Done, hiding window.");
            popup.hide();
        }
    }
}
