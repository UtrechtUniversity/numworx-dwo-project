package nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.Widget;
import fi.dwo.gwt.lib.rest.CallManagers.SecuredTeacherSchoolClassManager;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.uu.fi.dwo.lms.gwtclient.gwt.DialogEvent;
import nl.uu.fi.dwo.lms.gwtclient.gwt.DwoGlobalVars;
import nl.uu.fi.dwo.lms.gwtclient.gwt.SwitchViewEvent;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClassFull;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.exceptions.Dwo2ExceptionCode;
import org.osgi.util.promise.Failure;
import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Success;

/**
 * Handler for for Login actions.
 *
 * @author Gert van der Plas
 */
public class FileUploadStudentsPresenter implements SchoolClassDialogEventHandler {

    private static final Logger LOG = Logger.getLogger(FileUploadStudentsPresenter.class.getName());
    private DwoGlobalVars dwoGlobalVars;
    private EventBus eventBus;
    private SecuredTeacherSchoolClassManager manager = new SecuredTeacherSchoolClassManager();
    private Display view;
    private DomSchoolClass schoolClass;
    private String[][] importData;
    
    public interface Display {
        Widget asWidget();
        void clear();
        void init();
        void showDialog();
        void enableImport();
    }

    public FileUploadStudentsPresenter(EventBus anEventBus, DwoGlobalVars aDwoGlobalVars) {
        eventBus = anEventBus;
        dwoGlobalVars = aDwoGlobalVars;
        eventBus.addHandler(SchoolClassDialogEvent.TYPE, this);
    }

    @Override
    public void onDialogEvent(SchoolClassDialogEvent dialogEvent) {
        if (dialogEvent.getEventValue() == SchoolClassDialogEvent.Dialogs.LoadStudentFile) {
            importData=null;
            view.showDialog();
//            schoolClass = (DomSchoolClass) dialogEvent.getSchoolClass();
//            Promise<DomSchoolClassFull> promise;
//            promise = manager.getFullSchoolClass(schoolClass);
//            // onSuccess calculate results and show.
//            promise.then(new Success<DomSchoolClassFull, Void>() {
//                @Override
//                public Promise<Void> call(Promise<DomSchoolClassFull> resolved) throws Exception {
//                    //flip back to schoolclasses screen 
//                    DomSchoolClassFull value = resolved.getValue();
////                    view.showDialog(value.getSchoolClassName(), value.getIconizer(), value.getHasRegKey(), value.getRegistrationKey());
//                    return null;
//                }
//            }, new Failure() {
//                @Override
//                public void fail(Promise<?> resolved) throws Exception {
//                    Throwable fail = resolved.getFailure();
//                    if (fail instanceof Dwo2Exception) {
//                        LOG.log(Level.SEVERE, fail.getMessage());
//                        eventBus.fireEvent(new DialogEvent((Dwo2Exception) fail));
//                    } else {
//                        LOG.log(Level.SEVERE, fail.getMessage());
//                        eventBus.fireEvent(new DialogEvent(fail.getMessage()));
//                        //throw directly
//                    }
//                }
//            }
//            );
        }
    }

    public void init() {
        importData=null;
    }

    /**
     * @param view the view to set
     */
    public void setView(Display view) {
        this.view = view;
    }

    public void updateAndBack(String name, Boolean showTree, Boolean hasRegKey, String regKey) {
        Promise<Boolean> promise;
        DomSchoolClassFull fullSchoolClass = new DomSchoolClassFull();
        fullSchoolClass.setId(schoolClass.getId());
        fullSchoolClass.setSchoolClassName(name);
        fullSchoolClass.setIconizer(showTree);
        fullSchoolClass.setHasRegKey(hasRegKey);
        fullSchoolClass.setRegistrationKey(regKey);
        promise = manager.updateSchoolClass(fullSchoolClass);
        // onSuccess calculate results and show.
        promise.then(new Success<Boolean, Void>() {
            @Override
            public Promise<Void> call(Promise<Boolean> resolved) throws Exception {
                //flip back to schoolclasses screen 
                if (resolved.getValue() == true) {
                    eventBus.fireEvent(new SwitchViewEvent(SwitchViewEvent.SelectedView.SCHOOLCLASSES));
                    return null;
                } else {
                    throw new Dwo2Exception(Dwo2ExceptionCode.Rest_InternalError, "Rest request failed for unknown reasons.");
                }
            }
        },
                new Failure() {
            @Override
            public void fail(Promise<?> resolved) throws Exception {
                Throwable fail = resolved.getFailure();
                if (fail instanceof Dwo2Exception) {
                    LOG.log(Level.SEVERE, fail.getMessage());
                    eventBus.fireEvent(new DialogEvent((Dwo2Exception) fail));
                } else {
                    LOG.log(Level.SEVERE, fail.getMessage());
                    eventBus.fireEvent(new DialogEvent(fail.getMessage()));
                    //throw directly
                }
            }
        });
    }

    public void loadFile(String file) {        
        String[] lines = file.split("\n");
        LOG.log(Level.INFO, "Read " + lines.length + " lines.");
        importData = new String[lines.length][];
        for (int i=0;i<lines.length; i++) {
            String[] cols = lines[i].split("\t");
            importData[i]= cols;
            LOG.log(Level.INFO, "Read " + cols.length + " columns.");
            if (cols.length != 6) {
                eventBus.fireEvent(new DialogEvent("Invalid format"));
                return;
            }
            for (String field : cols) {
                LOG.log(Level.INFO, "Read >" + field + "< field.");
            }
        }
        eventBus.fireEvent(new DialogEvent("Valid format, click import to add."));
        view.enableImport();
    }

    public void importData() {
        eventBus.fireEvent(new SchoolClassDialogEvent(SchoolClassDialogEvent.Dialogs.ImportStudentData,importData));
    }
}
