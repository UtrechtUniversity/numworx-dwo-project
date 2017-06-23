package nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.Widget;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Panel to configure and add a schoolclass.
 *
 * @author Gert van der Plas
 */
public class FileUploadStudentsView extends Composite implements FileUploadStudentsPresenter.Display, ClickHandler {

    private static Logger LOG = Logger.getLogger(FileUploadStudentsView.class.getName());

    interface MyUiBinder extends UiBinder<Widget, FileUploadStudentsView> {
    }
    private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

    @UiField
    FileUpload fileUpload;
    @UiField
    PushButton importBtn;
    @UiField
    PushButton cancelBtn;
    @UiField
    PushButton loadBtn;

    FileUploadStudentsPresenter fileUploadPresenter;
    final DialogBox dialogBox = new DialogBox();

    public FileUploadStudentsView(FileUploadStudentsPresenter ap) {
        initWidget(uiBinder.createAndBindUi(this));
        fileUploadPresenter = ap;
        ap.setView(this);
        cancelBtn.addClickHandler(this);
        loadBtn.addClickHandler(this);
        importBtn.addClickHandler(this);
    }

    public void onClick(ClickEvent event) {
        if (event.getSource() == loadBtn) {
            LOG.log(Level.INFO, "Add schoolclass now");
            dialogBox.hide();
//            editSchoolClassPresenter.updateAndBack(schoolclassName.getText(), showTree.getValue(), setClassKey.getValue(), classKey.getValue());
        } else if (event.getSource() == cancelBtn) {
            LOG.log(Level.INFO, "Add schoolclass now");
            dialogBox.hide();
//            editSchoolClassPresenter.Back();
        }
    }

    @Override
    public void clear() {
    }

    @Override
    public void init() {
        clear();
    }

    @Override
    public void showDialog() {
//        schoolclassName.setText(name);
//        showTree.setValue(showTreeValue);
//        setClassKey.setValue(hasRegKey);
//        classKey.setText(regKey);
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
