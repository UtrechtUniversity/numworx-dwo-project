package fi.dwo.server.rest;

import nl.uu.fi.dwo.rest.dom.entities.DomSchoolFull;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.exceptions.Dwo2ExceptionCode;
import fi.dwo.commons.persistence.MySQLPersistenceId;
import fi.dwo.commons.persistence.entities.PersistentSchool;
import nl.uu.fi.dwo.rest.entities.RestSchoolFull;
import fi.dwo.server.PersistentDataManagers.access.AnonDomainAuthorizer;
import fi.dwo.server.PersistentDataManagers.access.TeacherDomainAuthorizer.TeacherState_HR_R_S_SG_U;
import fi.dwo.server.PersistentDataManagers.access.UserDomainAuthorizer.UserState_HR_R_S_SG_U;
import fi.dwo.server.PersistentDataManagers.access.UserDomainAuthorizer.UserState_U;
import fi.dwo.server.PersistentDataManagers.core.SchoolManager;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.security.PermitAll;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;

/**
 * Operations for the GUI Component that manages the User Profile.
 *
 * @see fi.dwo.dwojapplet.gui.panels.JPanel.MyProfile
 *
 * @author G.A.J. van der Plas
 */
@PermitAll
@Path("/secure/teacher/school")
public class SecuredTeacherSchoolManager {

    private static final Logger LOG = Logger.getLogger(SecuredTeacherSchoolManager.class.getName());

    @PUT
    @Produces({"application/json"})
    @Path("/update")
    public Boolean updateSchool(@Context SecurityContext sc, RestSchoolFull rest) throws Dwo2Exception {
        UserState_U user = AnonDomainAuthorizer.build().submitUser(sc);
        UserState_HR_R_S_SG_U role = user.setHasRole(rest.getRestContext().getDomHasRole());
        PersistentSchool ps = role.getSchool();
        TeacherState_HR_R_S_SG_U state = role.buildSchoolAdminTeacher().setTeacher();
      DomSchoolFull school = rest.getDomSchoolFull();
      Long id = MySQLPersistenceId.getNativeId(school);
      if( ! id.equals(ps.getSchoolID()))
        throw new Dwo2Exception(Dwo2ExceptionCode.User_AuthorizationError, "Illegal user action");
      if (school.getExport() != null)
        ps.setExport(school.getExport());
      try {
        SchoolManager.edit(ps);
      } catch (Exception e) {
          LOG.log(Level.SEVERE, "update school for teacher", e);
          throw new Dwo2Exception(Dwo2ExceptionCode.Rest_InternalError, e.getMessage());
      }
      return Boolean.TRUE;
    }

}
