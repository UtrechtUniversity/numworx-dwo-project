package fi.dwo.server.rest;

import fi.dwo.server.PersistentDataManagers.access.AnonDomainAuthorizer;
import fi.dwo.server.PersistentDataManagers.access.TeacherDomainAuthorizer;
import java.util.List;
import java.util.logging.Logger;
import javax.annotation.security.PermitAll;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContext;
import nl.uu.fi.dwo.rest.entities.RestContext;
import nl.uu.fi.dwo.rest.entities.RestStudentModelContext;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.exceptions.Dwo2RestException;

/**
 * StudentModel manager for the teacher. Basic operations.
 *
 *
 * @see fi.dwo.dwojapplet.gui.panels.JPanel.MyProfile
 *
 * @author G.A.J. van der Plas
 */
@PermitAll
@Path("/secure/teacher/studentmodel")
public class SecuredTeacherStudentModelManager {

    private static final Logger LOG = Logger.getLogger(SecuredTeacherStudentModelManager.class.getName());

    /**
     * Returns the list of student models in the school.
     *
     * @param sc
     * @param context
     * @return
     */
    @PUT
    @Produces({"application/json"})
    @Path("/getList")
    public List<DomStudentModelContext> getStudentModels(@Context SecurityContext sc, RestContext context) {
        try {
            TeacherDomainAuthorizer.TeacherState_HR_R_S_SG_U build = AnonDomainAuthorizer.build().setUser(sc.getUserPrincipal().getName())
                    .setHasRole(context.getRestContext().getDomHasRole())
                    .setSchoolAdminTeacher()
                    .setTeacher();
            return build.getStudentModels();
            //return null;
        } catch (Dwo2Exception e) {
            throw new Dwo2RestException(e);
        }
    }

    /**
     * Returns the school data to be displayed.
     *
     * @param sc
     * @param mode The created model.
     * @return
     */
    @PUT
    @Produces({"application/json"})
    @Path("/add")
    public DomStudentModelContext addStudentModel(@Context SecurityContext sc, RestStudentModelContext model) {
        try {
            TeacherDomainAuthorizer.TeacherState_HR_R_S_SG_U build = AnonDomainAuthorizer.build().setUser(sc.getUserPrincipal().getName())
                    .setHasRole(model.getRestContext().getDomHasRole())
                    //.setDefaultHasRole()
                    .setSchoolAdminTeacher()
                    .setTeacher();
            return build.addStudentModel(model.getDomStudentModelContext());
            
        } catch (Dwo2Exception e) {
            throw new Dwo2RestException(e);
        }
    }
}
