package nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DialogBox;
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
public class EditSchoolclassView extends Composite implements EditSchoolclassPresenter.Display, ClickHandler {

    private static Logger LOG = Logger.getLogger(EditSchoolclassView.class.getName());

    interface MyUiBinder extends UiBinder<Widget, EditSchoolclassView> {
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

    EditSchoolclassPresenter editSchoolClassPresenter;
    final DialogBox dialogBox = new DialogBox();

    public EditSchoolclassView(EditSchoolclassPresenter ap) {
        initWidget(uiBinder.createAndBindUi(this));
        editSchoolClassPresenter = ap;
        ap.setView(this);
        cancelButton.addClickHandler(this);
        addButton.addClickHandler(this);
    }

    public void onClick(ClickEvent event) {
        if (event.getSource() == addButton) {
            LOG.log(Level.INFO, "Add schoolclass now");
            dialogBox.hide();
            editSchoolClassPresenter.updateAndBack(schoolclassName.getText(), showTree.getValue(), setClassKey.getValue(), classKey.getValue());
        } else if (event.getSource() == cancelButton) {
            LOG.log(Level.INFO, "Add schoolclass now");
            dialogBox.hide();
            editSchoolClassPresenter.Back();
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

    @Override
    public void showDialog(String name, Boolean showTreeValue, Boolean hasRegKey, String regKey) {
        schoolclassName.setText(name);
        showTree.setValue(showTreeValue);
        setClassKey.setValue(hasRegKey);
        classKey.setText(regKey);
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
