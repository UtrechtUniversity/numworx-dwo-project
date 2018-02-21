package fi.dwo.dwojapplet.gui;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContext;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;


/**
 *
 * @author Gert van der Plas
 */
public class TeacherStudentModelPanelProperties {

    private static final Logger LOG = Logger.getLogger(TeacherStudentModelPanelProperties.class.getName());
//    private DomSchoolClassFull schoolClass = new DomSchoolClassFull();
    private List<DomSchoolClass> scList;

    public TeacherStudentModelPanelProperties(){
        
    }

    public void init() throws Dwo2Exception {
    }
    
//    
//    public DomSchoolClassFull getSchoolClass() {
//        return schoolClass;
//    }
//
//    public void setSchoolClass(DomSchoolClassFull sc) {
//        this.schoolClass = sc;
//    }
    
    public Boolean addModel(DomStudentModelContext modelContext) throws Dwo2Exception{
        return null;//return SecureSchoolAdminSchoolClassManager.submitSchoolClass(sc);
    }


    public List<DomStudentModelContext> getModelList() throws Dwo2Exception {
        // can changed to caching. However low frequent operation.
        return new ArrayList<DomStudentModelContext>();
    }

        
}
