package fi.dwo.dwojapplet.gui;
import java.util.ArrayList;
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
public class TeacherStudentModelPanelProperties {

    private static final Logger LOG = Logger.getLogger(TeacherStudentModelPanelProperties.class.getName());
//    private DomSchoolClassFull schoolClass = new DomSchoolClassFull();
    private List<DomSchoolClass> scList;

    public TeacherStudentModelPanelProperties(){
        
    }

    public void init() throws Dwo2Exception {
    }
    
    public DomStudentModelContext addModel(DomStudentModelContext modelContext) throws Dwo2Exception{
        return SecureTeacherStudentModelManager.addModel(modelContext);
    }


    public List<DomStudentModelContext> getModelList() throws Dwo2Exception {
        return SecureTeacherStudentModelManager.getList();
    }

        
}
