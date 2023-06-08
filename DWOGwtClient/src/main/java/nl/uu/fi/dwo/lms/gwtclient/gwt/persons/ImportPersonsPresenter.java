package nl.uu.fi.dwo.lms.gwtclient.gwt.persons;

import com.google.gson.JsonObject;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONValue;
import com.google.web.bindery.event.shared.EventBus;

import fi.dwo.gwt.lib.rest.CallManagers.MD5;
import fi.dwo.gwt.lib.rest.CallManagers.SecuredTeacherSchoolClassManager;
import fi.dwo.gwt.lib.rest.util.DomSingleSchoolStudentCodec;

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
import nl.uu.fi.dwo.lms.gwtclient.gwt.SwitchViewEvent;
import nl.uu.fi.dwo.lms.gwtclient.gwt.ui.AlertDialogWithOKEvent;
import nl.uu.fi.dwo.lms.gwtclient.gwt.ui.BasicDisplay;
import nl.uu.fi.dwo.lms.gwtclient.gwt.ui.MessageDialogWithOKEvent;
import nl.uu.fi.dwo.rest.dom.entities.DomNewSingleSchoolStudent;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomSingleSchoolStudent;
import nl.uu.fi.dwo.rest.dom.entities.DomSubmitTeacherToSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomUserFull;
import nl.uu.fi.dwo.rest.dom.entities.RoleType;
import nl.uu.fi.dwo.rest.dom.entities.SimpleValidUserFieldsChecker;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.exceptions.Dwo2ExceptionCode;
import nl.uu.fi.dwo.rest.locale.DwoLocalesForGWT;
import nl.uu.fi.dwo.rest.persistence.PersistenceClassType;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;
import nl.uu.fi.dwo.rest.util.Dwo2ExceptionTranslator;

import org.osgi.util.promise.Deferred;
import org.osgi.util.promise.Failure;
import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Promises;
import org.osgi.util.promise.Success;
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
    private final PersonsService manager;
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

    @Inject ImportPersonsPresenter(EventBus anEventBus, DwoGlobalVars aDwoGlobalVars, PersonsService m) {
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
        String[] lines;

        lines = file.split("\\r?\\n");         
        // check de situatie waarin csvText alleen \r als regelscheiding bevat
        if ((lines.length == 1) && file.contains("\r") && !file.contains("\r\n"))
        {
            // csvText has only \r as separator
            lines = file.split("\\r");
        }

        String separator = "\t";
        if (! file.contains(separator)) separator = ";"; // no tab, then ;
        if (! file.contains(separator)) separator = ",";
        LOG.log(Level.INFO, "Read " + lines.length + " lines.");
        List<DomSingleSchoolStudent> personList = new ArrayList<>(lines.length);
        for (int i = 0; i < lines.length; i++) {
            String[] cols;
            boolean quote = lines[i].contains("\"");
            if (quote) 
              cols = quoted(lines[i], separator.charAt(0));
            else 
              cols = lines[i].split(separator);
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
            s.setGivenName(cols[0]);
            s.setInsertion(cols[1]);
            s.setFamilyName(cols[2]);
            s.setUserName(cols[3]);
            s.setPassword(cols[4]);
            s.setEmail(cols[5]);
            s.setSingleSchool(true);
            personList.add(s);
        }

        return personList;
    }

    
    private String[] quoted(String string, char separator) {
      ArrayList<String> list = new ArrayList<String>(6);
      int state = 0;
      StringBuilder builder = new StringBuilder();
      for (char ch: string.toCharArray()) {
        switch(state) {
          case 2:
            if (ch == '"') {
              builder.append(ch); state = 1;
              break;
            }
            state = 0;
          case 0:
            if (ch == separator) {
              list.add(builder.toString()); builder.setLength(0);
            } else if (ch == '"') {
              state = 1;
            } else {
              builder.append(ch);
            }
            break;
          case 1: 
              if (ch == '"') {
                state = 2;
              } else {
                builder.append(ch);
              }
              break;
        }
        
        
      }
      if ( builder.length() > 0) {
        list.add(builder.toString());
      }
      return list.toArray(new String[list.size()]);
    }

    @JsMethod
    void submitImportStudents(JavaScriptObject json, String schoolClassID) {
      TaggedDomSchoolClass tagged = taggedSchoolClasses.get(schoolClassID);
      if(tagged == null) {
        eventBus.fireEvent(new AlertDialogWithOKEvent(Dwo2ExceptionTranslator.getLocalizedCodeExplanation(DwoGlobalVars.getDwoLocale(), Dwo2ExceptionCode.Rest_Active_SchoolClass_Not_Set)));
          return;
      }
      List<DomSingleSchoolStudent> personList = new ArrayList<>();
      int size = getAndCheck(json, personList);
      if(size <= 0) return;
      
      DomSchoolClass schoolClass = tagged.getSchoolClass();
      final List<DomSingleSchoolStudent> newPersons = new ArrayList<>(size);
      
      List<Promise<Boolean>> promises = new ArrayList<>(size);
      for(DomSingleSchoolStudent student: personList) {
        Promise<Boolean> promise;
        DomNewSingleSchoolStudent newStudent = new DomNewSingleSchoolStudent();
        newStudent.setDomSingleSchoolStudent(student.duplicate());
        newStudent.getDomSingleSchoolStudent().setPassword(MD5.md5(student.getPassword()));
        newStudent.setDomSchoolClass(schoolClass);
        promise = manager.submitSingleSchoolStudent(newStudent);
        promise = promise.then(p -> {
          return p;
        }, p-> { 
          Throwable t = p.getFailure();
          if (t instanceof Dwo2Exception) {
            Dwo2ExceptionCode code = ((Dwo2Exception) t).getDwo2Code();
            if (code == Dwo2ExceptionCode.Rest_Registration_UserName_exists) {
              student.setId(new PersistenceId("LOCAL;" + PersistenceClassType.PersistentUser + ";0000000000"));
            }
          }
          newPersons.add(student);
        });
        promises.add(promise);
      }
      Promises.all(promises).then(p-> {
        eventBus.fireEvent(new MessageDialogWithOKEvent(DwoLocalesForGWT.instance.NUN_DLG_Persons_PersonsImported()));
        eventBus.fireEvent(new SwitchViewEvent(SwitchViewEvent.SelectedView.PERSONS));
        return null;
      }, FAILURE).then( null, p-> {
        persons = newPersons;
        view.setPersonImportList(persons);
      });
      
      
    }

    private int getAndCheck(JavaScriptObject json, List<DomSingleSchoolStudent> personList) {
      JSONArray array = new JSONArray(json);
      int size = array.size();
      for(int i = 0; i < size; i++ )   
      {
        JSONValue value = array.get(i);
        DomSingleSchoolStudent student = DomSingleSchoolStudentCodec.CODEC.decode(value);
        student.setSingleSchool(true);
        String givenName = student.getGivenName();
        String username = student.getUserName();
        String eMail = student.getEmail();
        String familyName = student.getFamilyName();
        String password = student.getPassword();
        String insertion = student.getInsertion();
        // Verify formfields
        if (SimpleValidUserFieldsChecker.isNonEmptyNorNull(password, familyName , givenName, eMail , username)) {
          LOG.log(Level.INFO, "valid required fields.");
          if (SimpleValidUserFieldsChecker.isNonEmptyNorNull(insertion)) {
              insertion = insertion.trim();
          } else {
              insertion = null;
          }
          student.setInsertion(insertion);
        } else {
          eventBus.fireEvent(new AlertDialogWithOKEvent(Dwo2ExceptionTranslator.getLocalizedCodeExplanation(DwoGlobalVars.getDwoLocale(), Dwo2ExceptionCode.Rest_Registration_Required_Fields)));
          return -1;
        }
  
       if (!SimpleValidUserFieldsChecker.isValidUserName(username)) {
         eventBus.fireEvent(new AlertDialogWithOKEvent(Dwo2ExceptionTranslator.getLocalizedCodeExplanation(DwoGlobalVars.getDwoLocale(), Dwo2ExceptionCode.Rest_Registration_UserName_Invalid)));
         return -1;
       }
       if (!verifyEmail(eMail)) {
            eventBus.fireEvent(new AlertDialogWithOKEvent(Dwo2ExceptionTranslator.getLocalizedCodeExplanation(DwoGlobalVars.getDwoLocale(), Dwo2ExceptionCode.Rest_Registration_Email_Address_Invalid)));
            return -1;
        } else {
          eMail = eMail.trim();
          student.setEmail(eMail);
        }
        if (!SimpleValidUserFieldsChecker.isValidPassword(password)) {
          //invalid password format
          eventBus.fireEvent(new AlertDialogWithOKEvent(Dwo2ExceptionTranslator.getLocalizedCodeExplanation(DwoGlobalVars.getDwoLocale(), Dwo2ExceptionCode.GUI_AnIncorrectPasswordWasGiven)));
          return -1 ;
      }      
       
        personList.add(student);
      }
      return size;
    }

    @JsMethod
    boolean verifyEmail(String eMail) {
      return SimpleValidUserFieldsChecker.isValidEmail(eMail);
    }
    
    
    @JsMethod
    void submitImportTeachers(JavaScriptObject json, String schoolClassID) {
      TaggedDomSchoolClass tagged = taggedSchoolClasses.get(schoolClassID);
      
      List<DomSingleSchoolStudent> personList = new ArrayList<>();
      int size = getAndCheck(json, personList);
      if(size <= 0) return;
      
      DomSchoolClass schoolClass = tagged != null ? tagged.getSchoolClass(): null;
      final List<DomSingleSchoolStudent> newPersons = new ArrayList<>(size);
      
      List<Promise<Boolean>> promises = new ArrayList<>(size);
      for(DomSingleSchoolStudent student: personList) {
        Promise<Boolean> promise;
        DomUserFull teacher = student.duplicate();
        teacher.setPassword(MD5.md5(student.getPassword()));
        promise = manager.submitTeacher(teacher);
        promise = promise.then(p -> {
          if ( schoolClass != null) {
            DomSubmitTeacherToSchoolClass submit = new DomSubmitTeacherToSchoolClass();
            submit.setSchoolClass(schoolClass);
            return 
                manager.getTeachersInSchool().map(list -> list.stream()
                  .filter( u -> student.getUserName().equals(u.getUserName()))
                  .findFirst().get())
               .then( p2-> {
                  submit.setTeacher(p2.getValue());
                  return manager.submitTeacherToSchoolClass(submit);
                });
          }
          return p;
        }, p-> { 
          Throwable t = p.getFailure();
          if (t instanceof Dwo2Exception) {
            Dwo2ExceptionCode code = ((Dwo2Exception) t).getDwo2Code();
            if (code == Dwo2ExceptionCode.Rest_Registration_UserName_exists) {
              student.setId(new PersistenceId("LOCAL;" + PersistenceClassType.PersistentUser + ";0000000000"));
            }
          }
          newPersons.add(student);
        });
        promises.add(promise);
      }
      Promises.all(promises).then(p-> {
        eventBus.fireEvent(new SwitchViewEvent(SwitchViewEvent.SelectedView.PERSONS));
        return null;
      }, FAILURE).then( null, p-> {
        persons = newPersons;
        view.setPersonImportList(persons);
      });
    }

    
    
}
