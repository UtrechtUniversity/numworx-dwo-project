package nl.uu.fi.dwo.lms.gwtclient.gwt.persons;

import com.google.web.bindery.event.shared.EventBus;
import java.util.Map;
import java.util.logging.Logger;
import nl.uu.fi.dwo.lms.gwtclient.gwt.DwoGlobalVars;
import nl.uu.fi.dwo.lms.gwtclient.gwt.ui.BasicDisplay;

/**
 *
 * @author Gert van der Plas
 */
public class AddPersonPresenter {
    
    protected static final Logger LOG = Logger.getLogger(AddStudentPresenter.class.getName());
    protected DwoGlobalVars dwoGlobalVars;
    protected EventBus eventBus;
    protected Display view;


    

    public interface Display extends BasicDisplay {

        void init(String role); //Supports "TEACHER", "SCHOOLADMIN"

        void showSchoolClasses(Map<String, TaggedDomSchoolClass> schoolClasses);

        void setEmptyTableMessage();

        void setLoadingTableMessage();
    }
    
    public AddPersonPresenter() {
    }

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
    
}
