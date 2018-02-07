package fi.dwo.server.rest;


import javax.annotation.security.PermitAll;
import javax.ws.rs.Path;

/**
 * StudentModel manager for the teacher. Basic operations.
 * 
 *
 * @see fi.dwo.dwojapplet.gui.panels.JPanel.MyProfile
 *
 * @author G.A.J. van der Plas
 */
@PermitAll
@Path("/secure/teacher/schoolclass")
public class SecuredTeacherStudentModelManager  {
//
//    private static final Logger LOG = Logger.getLogger(SecuredTeacherStudentModelManager.class.getName());
//
//    /**
//     * Returns the list of student models in the school.
//     *
//     * @param sc
//     * @return
//     */
//    @GET
//    @Produces({"application/json"})
//    @Path("/getList")
//    public List<DomStudentModelContext> getStudentModels(@Context SecurityContext sc) {
//
//    }
//    
//    
//    /**
//     * Returns the list of student models in the school.
//     *
//     * @param sc
//     * @return
//     */
//    @GET
//    @Produces({"application/json"})
//    @Path("/get")
//    public DomStudentModelContext get(@Context SecurityContext sc, DomStudentModelId modelId) {
//
//    }
//
//    /**
//     * Returns the school data to be displayed.
//     *
//     * @param sc
//     * @param mode The created model.
//     * @return
//     */
//    @PUT
//    @Produces({"application/json"})
//    @Path("/add")
//    public DomStudentModelContext add(@Context SecurityContext sc, DomStudentModelContext model) {
//
//    }
}
