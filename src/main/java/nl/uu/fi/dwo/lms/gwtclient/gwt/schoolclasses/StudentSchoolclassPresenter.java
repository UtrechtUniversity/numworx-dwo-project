package nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.inject.Inject;

import org.osgi.util.promise.Failure;

import com.google.gwt.uibinder.elementparsers.IsEmptyParser;
import com.google.web.bindery.event.shared.EventBus;

import nl.uu.fi.dwo.lms.gwtclient.gwt.DwoGlobalVars;
import nl.uu.fi.dwo.lms.gwtclient.gwt.LoggingFailure;
import nl.uu.fi.dwo.lms.gwtclient.gwt.persons.TaggedDomSchoolClass;
import nl.uu.fi.dwo.lms.gwtclient.gwt.ui.BasicDisplay;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;

/**
 * Handler for for Login actions.
 *
 * @author Gert van der Plas
 */
public class StudentSchoolclassPresenter {

    private static final Logger LOG = Logger.getLogger(StudentSchoolclassPresenter.class.getName());

    private final DwoGlobalVars dwoGlobalVars;
    private final EventBus eventBus;
    private final SchoolClassServiceStudent manager;
    private final Failure FAILURE;
    private Display view;

    private DomSchoolClass active;
    private List<DomSchoolClass> list;
    

    public interface Display extends BasicDisplay {

      void setEmptyTableMessage();

      void setLoadingTableMessage();
void setSchoolClasses(Map<String, TaggedDomSchoolClass> schoolClasses);

    }

    @Inject StudentSchoolclassPresenter(DwoGlobalVars vars, EventBus bus, SchoolClassServiceStudent manager) {
      this.dwoGlobalVars = vars;
      this.eventBus = bus;
      this.manager = manager;
      this.FAILURE = new LoggingFailure(LOG, bus);
    }

    public void init() {
      view.setLoadingTableMessage();
      active = dwoGlobalVars.getCurrentSchoolClass();
      manager.getStudentsSchoolClasses().then(p -> { 
        list = p.getValue();
        if (list.isEmpty()) 
          view.setEmptyTableMessage();
        else {
          Map<String, TaggedDomSchoolClass> schoolClasses = new HashMap<>();
          list.forEach(item -> {
            String key = item.getId().getIdString();
            TaggedDomSchoolClass value = new TaggedDomSchoolClass(item);
            value.setTag(key.equals(active.getId().getIdString()));
            schoolClasses.put(key, value);
          });
          view.setSchoolClasses(schoolClasses);        
        }
        return null; }, FAILURE).then(null, f -> view.setEmptyTableMessage());
     }


    /**
     * @param view the view to set
     */
    @Inject void setView(Display view) {
        this.view = view;
        view.setHelp(dwoGlobalVars.buildHelpUrl("#studentSchoolclass"));
   }

}
