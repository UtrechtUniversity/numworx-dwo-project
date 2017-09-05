package nl.uu.fi.dwo.lms.gwtclient.gwt.results;

import nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses.*;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
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
public class ResultsFileExportView extends Composite implements ResultsFileExportPresenter.Display, ClickHandler {

    private static Logger LOG = Logger.getLogger(ResultsFileExportView.class.getName());

    interface MyUiBinder extends UiBinder<Widget, ResultsFileExportView> {
    }
    private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

    @UiField
    FileUpload fileUpload;
    @UiField
    PushButton exportBtn;
    @UiField
    PushButton cancelBtn;

    static ResultsFileExportPresenter fileExportPresenter;
    final DialogBox dialogBox = new DialogBox();

    public static void fileLoaded(String fileContents) {
        if(fileExportPresenter!=null){
        fileExportPresenter.loadFile(fileContents);
        }
    }

    public static native void readTextFile(JavaScriptObject files) /*-{
    var reader = new FileReader();

    reader.onload = function(e) {
        @nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses.FileUploadStudentsView::fileLoaded(*)(reader.result);
    }

    return reader.readAsText(files[0]);
}-*/;

    public ResultsFileExportView(ResultsFileExportPresenter ap) {
        initWidget(uiBinder.createAndBindUi(this));
        exportBtn.setEnabled(false);
        fileExportPresenter = ap;
        ap.setView(this);
        cancelBtn.addClickHandler(this);
        exportBtn.addClickHandler(this);
    }

    public void onClick(ClickEvent event) {
        if (event.getSource() == exportBtn) {
            LOG.log(Level.INFO, "Exporting data to csv.");
            exportBtn.setEnabled(false);
            fileExportPresenter.importData();
            dialogBox.hide();
//            editSchoolClassPresenter.updateAndBack(schoolclassName.getText(), showTree.getValue(), setClassKey.getValue(), classKey.getValue());
        } else if (event.getSource() == cancelBtn) {
            LOG.log(Level.INFO, "Cancel export window.");
            dialogBox.hide();
//            fileExportPresenter.Cancel();
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
    
    @Override
    public void enableImport(){
        exportBtn.setEnabled(true);
    }

}
