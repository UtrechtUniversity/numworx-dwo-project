package nl.uu.fi.dwo.lms.gwtclient.gwt.persons;

import com.google.web.bindery.event.shared.EventBus;
import fi.dwo.gwt.lib.rest.CallManagers.SecuredTeacherSchoolClassManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import java.util.logging.Logger;
import jsinterop.annotations.JsMethod;

import nl.uu.fi.dwo.lms.gwtclient.gwt.DwoGlobalVars;
import nl.uu.fi.dwo.lms.gwtclient.gwt.ui.AlertDialogWithOKEvent;
import nl.uu.fi.dwo.lms.gwtclient.gwt.ui.BasicDisplay;
import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.dom.entities.DomNewSingleSchoolStudent;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClassId;
import nl.uu.fi.dwo.rest.dom.entities.DomSingleSchoolStudent;
import nl.uu.fi.dwo.rest.dom.entities.DomStudent;
import nl.uu.fi.dwo.rest.dom.entities.ValidUserFieldsChecker;
import nl.uu.fi.dwo.rest.entities.RestStudent;
import nl.uu.fi.dwo.rest.locale.DwoLocalesForGWT;
import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Promises;

/**
 * Login Presenter.
 *
 * @author G.A.J. van der Plas
 */
public class ImportPersonsPresenter {

    private static final Logger LOG = Logger.getLogger(ImportPersonsPresenter.class.getName());
    private DwoGlobalVars dwoGlobalVars;
    private EventBus eventBus;
    private Display view;
    private SecuredTeacherSchoolClassManager manager = new SecuredTeacherSchoolClassManager();
    private List<DomSingleSchoolStudent> persons;
    private String fileName;
    private Map<String, TaggedDomSchoolClass> taggedSchoolClasses;

    /**
     * @return the view
     */
    public Display getView() {
        return view;
    }

    /**
     * @param view the view to set
     */
    public void setView(Display view) {
        this.view = view;
    }

    public interface Display extends BasicDisplay {

        void clear();

        String fetchFileName();

        void setPersonImportList(List<DomSingleSchoolStudent> persons);

        void showSchoolClasses(Map<String,TaggedDomSchoolClass> schoolClasses);

        void setEmptyPeopleTableMessage();

        void setLoadingPeopleTableMessage();

        void setEmptySchoolClassesTableMessage();

        void setLoadingSchoolClassesTableMessage();
    }

    public ImportPersonsPresenter(EventBus anEventBus, DwoGlobalVars aDwoGlobalVars) {
        eventBus = anEventBus;
        dwoGlobalVars = aDwoGlobalVars;
    }

//    @JsMethod not required unless testing stuff.
    public void init() {
        view.clear();
        view.setHelp(dwoGlobalVars.buildHelpUrl("#importPersons"));
        view.setEmptyPeopleTableMessage();
        view.setEmptySchoolClassesTableMessage();
        fileName = view.fetchFileName();
        persons = loadFile(fileName);
        view.setPersonImportList(persons);
        showTeachersSchoolClasses();
    }

    private void showTeachersSchoolClasses() {
        Promise<List<DomSchoolClass>> promise;
        promise = manager.getTeachersSchoolClasses();
        promise.then((resolved) -> {
            List<DomSchoolClass> classList = (List<DomSchoolClass>) resolved.getValue();
            taggedSchoolClasses = new HashMap<String, TaggedDomSchoolClass>(classList.size());
            classList.forEach((v) -> taggedSchoolClasses.put(v.getId().getIdString(), new TaggedDomSchoolClass(v)));
            view.showSchoolClasses(taggedSchoolClasses);
            return null;
        });
    }

    /**
     * Students are imported as single school.
     */
    @JsMethod
    public void importStudents(List<DomSingleSchoolStudent> students) {
        eventBus.fireEvent(new AlertDialogWithOKEvent(DwoLocalesForGWT.instance.GUI_Feature_Not_Supported_Yet()));
    }

    /**
     * Teacher are granted a full account.
     *
     */
    @JsMethod
    public void importTeachers(List<DomSingleSchoolStudent> teachers) {
        eventBus.fireEvent(new AlertDialogWithOKEvent(DwoLocalesForGWT.instance.GUI_Feature_Not_Supported_Yet()));
    }

    private List<DomSingleSchoolStudent> loadFile(String file) {
        //tokenize import file.
        String[][] importData;
        String[] lines = file.split("\n");
        LOG.log(Level.INFO, "Read " + lines.length + " lines.");
        importData = new String[lines.length][];
        List<DomSingleSchoolStudent> personList = new ArrayList<>(importData.length);
        for (int i = 0; i < lines.length; i++) {
            String[] cols = lines[i].split("\t");
            importData[i] = cols;
            LOG.log(Level.INFO, "Read " + cols.length + " columns.");
            if (cols.length != 6) {
                eventBus.fireEvent(new AlertDialogWithOKEvent("Invalid format"));
                return personList;
            }
            for (String field : cols) {
                LOG.log(Level.INFO, "Read >" + field + "< field.");
            }
        }
        //convert to SingleSchoolStudent which is subclassed of DomUserFull and 
        //works for teachers too.

        //username, givenname, insertion, familyname, email, password.
        for (int i = 0; i < importData.length; i++) {
            DomNewSingleSchoolStudent s = new DomNewSingleSchoolStudent();
            s.getDomSingleSchoolStudent().setUserName(importData[0][0]);
            s.getDomSingleSchoolStudent().setGivenName(importData[0][1]);
            s.getDomSingleSchoolStudent().setInsertion(importData[0][2]);
            s.getDomSingleSchoolStudent().setFamilyName(importData[0][3]);
            s.getDomSingleSchoolStudent().setEmail(importData[0][4]);
            s.getDomSingleSchoolStudent().setPassword(importData[0][5]);
            s.getDomSingleSchoolStudent().setSingleSchool(true);
        }
        return personList;
    }

}
