package nl.uu.fi.dwo.lms.gwtclient.gwt.persons;

import com.google.web.bindery.event.shared.EventBus;
import java.util.Map;
import java.util.logging.Logger;
import nl.uu.fi.dwo.lms.gwtclient.gwt.DwoGlobalVars;
import nl.uu.fi.dwo.lms.gwtclient.gwt.ui.BasicDisplay;
import nl.uu.fi.dwo.rest.dom.entities.RoleType;

/**
 *
 * @author Gert van der Plas
 */
public abstract class AddPersonPresenter {
    
    protected static final Logger LOG = Logger.getLogger(AddPersonPresenter.class.getName());
    protected DwoGlobalVars dwoGlobalVars;
    protected EventBus eventBus;
    protected Display view;


    

    public interface Display extends BasicDisplay {

        void init(RoleType role); //Supports "TEACHER", "SCHOOLADMIN"

        void showSchoolClasses(Map<String, TaggedDomSchoolClass> schoolClasses);

        void setEmptyTableMessage();

        void setLoadingTableMessage();
    }
    
    AddPersonPresenter() {
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

    public abstract void init();
    
}
