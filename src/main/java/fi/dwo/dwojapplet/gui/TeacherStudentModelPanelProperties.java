package fi.dwo.dwojapplet.gui;
import java.util.List;
import java.util.logging.Logger;
import nl.uu.fi.dwo.lms.jclient.lib.rest.managers.SecureTeacherStudentModelManager;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContext;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;


/**
 *
 * @author Gert van der Plas
 */
class TeacherStudentModelPanelProperties {

    private static final Logger LOG = Logger.getLogger(TeacherStudentModelPanelProperties.class.getName());
//    private DomSchoolClassFull schoolClass = new DomSchoolClassFull();
    private List<DomSchoolClass> scList;

    TeacherStudentModelPanelProperties(){
        
    }

    void init() throws Dwo2Exception {
    }
    
    DomStudentModelContext addModel(DomStudentModelContext modelContext) throws Dwo2Exception{
        return SecureTeacherStudentModelManager.addModel(modelContext);
    }


    List<DomStudentModelContext> getModelList() throws Dwo2Exception {
        return SecureTeacherStudentModelManager.getList();
    }

    DomStudentModelContext updateModel(DomStudentModelContext modelContext) throws Dwo2Exception {
        return current = SecureTeacherStudentModelManager.updateModel(modelContext);
    }
    
    private DomStudentModelContext current;

    DomStudentModelContext getCurrent() {
      return current;
    }

    void setCurrent(DomStudentModelContext current) {
      this.current = current;
    }
    
}
