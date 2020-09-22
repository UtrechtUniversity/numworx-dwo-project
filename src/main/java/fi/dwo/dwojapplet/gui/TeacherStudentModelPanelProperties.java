package fi.dwo.dwojapplet.gui;
import java.util.List;
import java.util.logging.Logger;
import nl.uu.fi.dwo.lms.jclient.lib.rest.managers.SecureTeacherStudentModelManager;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContext;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContextId;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelStructure;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;


/**
 *
 * @author Gert van der Plas
 */
public class TeacherStudentModelPanelProperties {

    private static final Logger LOG = Logger.getLogger(TeacherStudentModelPanelProperties.class.getName());

    TeacherStudentModelPanelProperties(){
        
    }

    void init() throws Dwo2Exception {
    }
    
    DomStudentModelContext addModel(DomStudentModelContext modelContext) throws Dwo2Exception{
        return SecureTeacherStudentModelManager.addModel(modelContext);
    }

    List<DomStudentModelContext> getModelList() throws Dwo2Exception {
        return SecureTeacherStudentModelManager.getReducedList();
    }

    DomStudentModelContext updateModel(DomStudentModelContext modelContext) throws Dwo2Exception {
        return current = SecureTeacherStudentModelManager.updateModel(modelContext);
    }
    
    public DomStudentModelStructure updateModel(DomStudentModelStructure model) throws Dwo2Exception {
      current.setModelStructure(model);
      return updateModel(current).getModelStructure();
    }
    
    public DomStudentModelContext getModel(DomStudentModelContextId modelContext) throws Dwo2Exception {
      return current = SecureTeacherStudentModelManager.get(modelContext);
    }
    
    void removeModel(DomStudentModelContext modelContext) throws Dwo2Exception {
      SecureTeacherStudentModelManager.removeModel(modelContext);
      if(modelContext == getCurrent()) setCurrent(null);
    }
        
    private DomStudentModelContext current;

    public DomStudentModelContext getCurrent() {
      return current;
    }

    void setCurrent(DomStudentModelContext current) {
      this.current = current;
    }
    
}
