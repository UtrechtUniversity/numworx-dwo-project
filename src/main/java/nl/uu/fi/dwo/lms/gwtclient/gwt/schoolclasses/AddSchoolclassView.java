package nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Panel to configure and add a schoolclass.
 * 
 * @author Gert van der Plas
 */
public class AddSchoolclassView extends Composite implements AddSchoolclassPresenter.Display, ClickHandler {

    private static Logger LOG = Logger.getLogger(AddSchoolclassView.class.getName());

    interface MyUiBinder extends UiBinder<Widget, AddSchoolclassView> {
    }
    private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

    @UiField
    TextBox schoolclassName;
    @UiField
    CheckBox setClassKey;
    @UiField
    CheckBox showTree;
    @UiField
    TextBox classKey;
    @UiField
    PushButton cancelButton;
    @UiField
    PushButton addButton;

    AddSchoolclassPresenter addSchoolClassPresenter;

    public AddSchoolclassView(AddSchoolclassPresenter ap) {
        initWidget(uiBinder.createAndBindUi(this));
        addSchoolClassPresenter = ap;
        ap.setView(this);
        cancelButton.addClickHandler(this);
        addButton.addClickHandler(this);
    }

    public void onClick(ClickEvent event) {
        if (event.getSource() == addButton) {
            LOG.log(Level.INFO, "Add schoolclass now");
            addSchoolClassPresenter.AddAndBack(schoolclassName.getText(), showTree.getValue(), setClassKey.getValue(), classKey.getValue());
        } else if (event.getSource() == cancelButton) {
            LOG.log(Level.INFO, "Add schoolclass now");
            addSchoolClassPresenter.Back();
        }
    }

    @Override
    public void clear() {
        schoolclassName.setText("");
        showTree.setValue(false);
        setClassKey.setValue(false);
        classKey.setText("");
    }

    @Override
    public void init() {
        clear();
    }

}
