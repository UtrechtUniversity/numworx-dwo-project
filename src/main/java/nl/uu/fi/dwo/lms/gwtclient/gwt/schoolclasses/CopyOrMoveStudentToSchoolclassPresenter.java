package nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses;

import java.util.Map;
import nl.uu.fi.dwo.rest.dom.entities.DomStudent;


public class CopyOrMoveStudentToSchoolclassPresenter {

    public interface Display {

        void clear();

        void init();

        void showStudentsClassA(Map<String, DomStudent> students);
        
        void showStudentsClassB(Map<String, DomStudent> students);
        
        void setEmptyTableMessage();

        void setsetLoadingTableMessage();
    }
    
}
