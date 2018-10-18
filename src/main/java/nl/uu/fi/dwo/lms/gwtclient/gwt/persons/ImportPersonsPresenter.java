package nl.uu.fi.dwo.lms.gwtclient.gwt.persons;

import com.google.web.bindery.event.shared.EventBus;
import fi.dwo.gwt.lib.rest.CallManagers.SecuredTeacherSchoolClassManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import java.util.logging.Logger;

import javax.inject.Inject;
import javax.swing.text.View;

import jsinterop.annotations.JsMethod;

import nl.uu.fi.dwo.lms.gwtclient.gwt.DwoGlobalVars;
import nl.uu.fi.dwo.lms.gwtclient.gwt.LoggingFailure;
import nl.uu.fi.dwo.lms.gwtclient.gwt.ui.AlertDialogWithOKEvent;
import nl.uu.fi.dwo.lms.gwtclient.gwt.ui.BasicDisplay;
import nl.uu.fi.dwo.rest.dom.entities.DomNewSingleSchoolStudent;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomSingleSchoolStudent;
import nl.uu.fi.dwo.rest.locale.DwoLocalesForGWT;

import org.osgi.util.promise.Deferred;
import org.osgi.util.promise.Failure;
import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Promises;
import org.vectomatic.file.File;
import org.vectomatic.file.FileReader;
import org.vectomatic.file.events.AbortEvent;
import org.vectomatic.file.events.AbortHandler;

/**
 * Login Presenter.
 *
 * @author G.A.J. van der Plas
 */
public class ImportPersonsPresenter {

    private static final Logger LOG = Logger.getLogger(ImportPersonsPresenter.class.getName());
    private final Failure FAILURE;
    private final DwoGlobalVars dwoGlobalVars;
    private final EventBus eventBus;
    private Display view;
    private final SecuredTeacherSchoolClassManager manager;
    private List<DomSingleSchoolStudent> persons;
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

        void setPersonImportList(List<DomSingleSchoolStudent> persons);

        void showSchoolClasses(Map<String,TaggedDomSchoolClass> schoolClasses);

        void setEmptyPeopleTableMessage();

        void setLoadingPeopleTableMessage();

        void setEmptySchoolClassesTableMessage();

        void setLoadingSchoolClassesTableMessage();
    }

    @Inject ImportPersonsPresenter(EventBus anEventBus, DwoGlobalVars aDwoGlobalVars, SecuredTeacherSchoolClassManager m) {
        eventBus = anEventBus;
        dwoGlobalVars = aDwoGlobalVars;
        manager = m;
        FAILURE = new LoggingFailure(LOG,anEventBus);
    }

//    @JsMethod not required unless testing stuff.
    public void init(File file) {
        view.init();
        view.clear();
        view.setHelp(dwoGlobalVars.buildHelpUrl("#importPersons"));
        view.setEmptyPeopleTableMessage();
        view.setEmptySchoolClassesTableMessage();
        
        final Deferred<String> contents = new Deferred<>();
        try {
          FileReader reader = new FileReader();
          reader.addAbortHandler( event -> contents.fail(new RuntimeException("aborted :" + file.getName())));
          reader.addErrorHandler( event -> contents.fail(new RuntimeException("error: " + file.getName())));
          reader.addLoadEndHandler(event -> contents.resolve(reader.getStringResult()));  
          reader.readAsText(file);
        } catch(Exception e) {
          contents.fail(e);
        }
        Promise<Void> p1 = 
        contents.getPromise()
          .then(this::loadFile)
          .then(
            p -> {
              persons = p.getValue();
              view.setPersonImportList(persons);
              return null;
            }
        );
        Promise<Void> p2 = showTeachersSchoolClasses();
        
        Promises.all(p1, p2).then( 
          this::enable,
          FAILURE);
    }

    private Promise<Void> enable(Promise<List<Void>> p) { return null; }
    
    private Promise<Void> showTeachersSchoolClasses() {
        Promise<List<DomSchoolClass>> promise;
        promise = manager.getTeachersSchoolClasses();
        return promise.then((resolved) -> {
            List<DomSchoolClass> classList = resolved.getValue();
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

    private Promise<List<DomSingleSchoolStudent>> loadFile(Promise<String> file) {
      try {
        return Promises.resolved(loadFile(file.getValue()));
      } catch (Exception e) {
        return Promises.failed(e);
      }
    }
        
    private List<DomSingleSchoolStudent> loadFile(String file) {
        //tokenize import file.
        String[] lines = file.split("\n");
        LOG.log(Level.INFO, "Read " + lines.length + " lines.");
        List<DomSingleSchoolStudent> personList = new ArrayList<>(lines.length);
        for (int i = 0; i < lines.length; i++) {
            String[] cols = lines[i].split("\t");
            LOG.log(Level.INFO, "Read " + cols.length + " columns.");
            
            if(cols.length < 6) {
                String[] col = new String[6];
                System.arraycopy(cols, 0, col, 0, cols.length);
                cols = col;
            }
            
            for (String field : cols) {
                LOG.log(Level.INFO, "Read >" + field + "< field.");
            }
            //convert to SingleSchoolStudent which is subclassed of DomUserFull and 
            //works for teachers too.
            DomSingleSchoolStudent s = new DomSingleSchoolStudent();
            s.setUserName(cols[0]);
            s.setGivenName(cols[1]);
            s.setInsertion(cols[2]);
            s.setFamilyName(cols[3]);
            s.setEmail(cols[4]);
            s.setPassword(cols[5]);
            s.setSingleSchool(true);
            personList.add(s);
        }

        return personList;
    }

}
