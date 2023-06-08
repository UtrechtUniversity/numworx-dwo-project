package nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.servlet.jsp.tagext.Tag;

import org.osgi.util.promise.Failure;
import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Promises;

import com.google.gwt.uibinder.elementparsers.IsEmptyParser;
import com.google.web.bindery.event.shared.Event;
import com.google.web.bindery.event.shared.EventBus;

import fi.dwo.gwt.lib.rest.util.StringFormatter;
import jsinterop.annotations.JsMethod;
import nl.uu.fi.dwo.lms.gwtclient.gwt.DwoGlobalVars;
import nl.uu.fi.dwo.lms.gwtclient.gwt.LoggingFailure;
import nl.uu.fi.dwo.lms.gwtclient.gwt.login.LoginEvent;
import nl.uu.fi.dwo.lms.gwtclient.gwt.persons.TaggedDomSchoolClass;
import nl.uu.fi.dwo.lms.gwtclient.gwt.ui.AlertDialogWithConfirmCancelDeferred;
import nl.uu.fi.dwo.lms.gwtclient.gwt.ui.AlertDialogWithConfirmCancelEvent;
import nl.uu.fi.dwo.lms.gwtclient.gwt.ui.BasicDisplay;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.locale.DwoLocalesForGWT;

/**
 * Handler for for Login actions.
 *
 * @author Gert van der Plas
 */
public class StudentSchoolclassPresenter {

    private static final LoginEvent LOGIN_EVENT = new LoginEvent(LoginEvent.State.SUCCESS_WELCOME);
private static final Logger LOG = Logger.getLogger(StudentSchoolclassPresenter.class.getName());

    private final DwoGlobalVars dwoGlobalVars;
    private final EventBus eventBus;
    private final SchoolClassServiceStudent manager;
    private final Failure FAILURE;
    private Display view;

    private DomSchoolClass active;
    private List<DomSchoolClass> list;

    private Map<String, TaggedDomSchoolClass> schoolClasses = new HashMap<>(), allClasses = new LinkedHashMap<>();
    

    public interface Display extends BasicDisplay {

      void setEmptyTableMessage();

      void setLoadingTableMessage();
      void setSchoolClasses(Map<String, TaggedDomSchoolClass> schoolClasses);
      void showSchoolClasses(Map<String, TaggedDomSchoolClass> all);

    }

    @Inject StudentSchoolclassPresenter(DwoGlobalVars vars, EventBus bus, SchoolClassServiceStudent manager) {
      this.dwoGlobalVars = vars;
      this.eventBus = bus;
      this.manager = manager;
      this.FAILURE = new LoggingFailure(LOG, bus);
    }

    public void init() {
      view.init();
      view.clear();
      view.setLoadingTableMessage();
      list = Collections.emptyList();
      active = dwoGlobalVars.getCurrentSchoolClass();
      manager.getStudentsSchoolClasses().then(p -> { 
        list = p.getValue();
        schoolClasses.clear();        
        if (list.isEmpty()) 
          view.setEmptyTableMessage();
        else {
          
          list.forEach(item -> {
            String key = item.getId().getIdString();
            TaggedDomSchoolClass value = new TaggedDomSchoolClass(item);
            value.setTag(key.equals(active.getId().getIdString()));
            schoolClasses.put(key, value);
          });
          view.setSchoolClasses(schoolClasses);        
        }
        return null; }, FAILURE).then(null, f -> view.setEmptyTableMessage())
      .then( p -> 
      {
        if (dwoGlobalVars.getActiveSchoolRoleAndClass().getSchool().studentsCanRegisterForSchoolClasses())
          return manager.getSchoolClasses(); else 
          return Promises.resolved(Collections.<DomSchoolClass>emptyList());
      })
      .then(p -> {
        List<DomSchoolClass> all = p.getValue();
        Map<String,TaggedDomSchoolClass> map = allClasses;
        map.clear();
        Collections.sort(all, (a,b) -> {
          return String.CASE_INSENSITIVE_ORDER.compare(a.getSchoolClassName(), b.getSchoolClassName());
        });
        all.forEach(item -> 
          { if (! schoolClasses.keySet().contains( item.getId().toString()))
              map.put(item.getId().toString(), new TaggedDomSchoolClass(item));
          }
          );
        view.showSchoolClasses(map);
        return null;
      });
     }


    /**
     * @param view the view to set
     */
    @Inject void setView(Display view) {
        this.view = view;
        view.setHelp(dwoGlobalVars.buildHelpUrl("#studentSchoolclass"));
   }

    @JsMethod public void switchSchoolclass(String id) {
      final TaggedDomSchoolClass tag = schoolClasses.get(id);
      if (tag != null) {
        manager.setCurrentSchoolClass(tag.getSchoolClass()).then(
          p -> { 
            if (p.getValue().booleanValue()) {
              dwoGlobalVars.getActiveSchoolRoleAndClass().setSchoolClass(tag.getSchoolClass());
              eventBus.fireEvent(LOGIN_EVENT);
            }
             return null;
          }, FAILURE);
      }
    }
    
    @JsMethod public void removeASchoolclass(String id) {
      final TaggedDomSchoolClass tag = schoolClasses.get(id);
      if (tag != null && dwoGlobalVars.getActiveSchoolRoleAndClass().getSchool().studentsCanRegisterForSchoolClasses()) {
        Promise<Boolean> pr;
        String msg = StringFormatter.format(DwoLocalesForGWT.instance.NUM_DLG_Class_ConfirmRemoveSchoolClass(), tag.getSchoolClass().getSchoolClassName() );
        AlertDialogWithConfirmCancelDeferred dialogPromise = new AlertDialogWithConfirmCancelDeferred(msg);
        AlertDialogWithConfirmCancelEvent event = new AlertDialogWithConfirmCancelEvent(AlertDialogWithConfirmCancelEvent.EventType.ConfirmDialog, dialogPromise);
        eventBus.fireEvent(event);
        pr = dialogPromise.getPromise();
        pr = pr.then(p-> { 
          if (p.getValue().booleanValue()) {
            p = manager.removeSchoolClass(tag.getSchoolClass());
          }
          return p;
        });
        pr = pr.then(p -> {
          return manager.getActiveSchoolClass().map(value -> {
            DomSchoolClass old = dwoGlobalVars.getActiveSchoolRoleAndClass().getSchoolClass();
            dwoGlobalVars.getActiveSchoolRoleAndClass().setSchoolClass(value);init();
            if (!equal(old, value))
              eventBus.fireEvent(LOGIN_EVENT);
              return Boolean.TRUE;});
        });
        pr.then(null, FAILURE);
      } else {
        init();
      }
    }
    
    private static boolean equal(DomSchoolClass old, DomSchoolClass value) {
      if (old == null) return value == null;
      if (value == null) return false;
      return old.getId().equals(value.getId());
    }
    
@JsMethod public void addSchoolclass(String id, String key) {
      final TaggedDomSchoolClass tag = allClasses.get(id);
      LOG.info("id = " + id + ", key = " + key);
      if (tag != null && dwoGlobalVars.getActiveSchoolRoleAndClass().getSchool().studentsCanRegisterForSchoolClasses()) {
        LOG.info(tag.getSchoolClass().getSchoolClassName());
        manager.addSchoolClass(tag.getSchoolClass(), key)
        .then( p -> {
          if (p.getValue().booleanValue())
            init();
            if (dwoGlobalVars.getActiveSchoolRoleAndClass().getSchoolClass() == null) {
              dwoGlobalVars.getActiveSchoolRoleAndClass().setSchoolClass(tag.getSchoolClass());
              eventBus.fireEvent(LOGIN_EVENT);
            }
          return null;}, FAILURE);
        }
    }
}
