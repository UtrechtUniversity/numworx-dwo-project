package fi.dwo.server.rest;

import nl.uu.fi.dwo.rest.dom.entities.DomHasRole;
import nl.uu.fi.dwo.rest.dom.entities.DomMapEntry;
import nl.uu.fi.dwo.rest.dom.entities.DomSchool4DwoAdmin;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolFull;
import nl.uu.fi.dwo.rest.dom.entities.DomTeacherAndHasRole;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import fi.dwo.server.PersistentDataManagers.util.HasRoleUtilManager;
import nl.uu.fi.dwo.rest.exceptions.Dwo2ExceptionCode;
import nl.uu.fi.dwo.rest.exceptions.Dwo2RestException;
import fi.dwo.commons.persistence.MySQLPersistenceId;
import nl.uu.fi.dwo.rest.dom.entities.RoleType;
import fi.dwo.commons.persistence.entities.PersistentClassCourse;
import fi.dwo.commons.persistence.entities.PersistentCourse;
import fi.dwo.commons.persistence.entities.PersistentFromTo;
import fi.dwo.commons.persistence.entities.PersistentHasRole;
import fi.dwo.commons.persistence.entities.PersistentSamlUser;
import fi.dwo.commons.persistence.entities.PersistentSchool;
import fi.dwo.commons.persistence.entities.PersistentSchoolClass;
import fi.dwo.commons.persistence.entities.PersistentSchoolGroup;
import fi.dwo.commons.persistence.entities.PersistentScoContext;
import fi.dwo.commons.persistence.entities.PersistentStudentOfClass;
import fi.dwo.commons.persistence.entities.PersistentStudentScoContext;
import fi.dwo.commons.persistence.entities.PersistentTeacherOfClass;
import fi.dwo.commons.persistence.entities.PersistentUser;
import nl.uu.fi.dwo.rest.dom.entities.DomNewSchool;
import nl.uu.fi.dwo.rest.entities.RestHasRole;
import nl.uu.fi.dwo.rest.entities.RestNewSchool;
import nl.uu.fi.dwo.rest.entities.RestSchool4DwoAdmin;
import nl.uu.fi.dwo.rest.entities.RestSchoolFull;
import fi.dwo.server.PersistentDataManagers.access.AnonDomainAuthorizer;
import fi.dwo.server.PersistentDataManagers.access.DwoAdminDomainAuthorizer.DwoAdminState_HR_R_S_SG_U;
import fi.dwo.server.PersistentDataManagers.access.SchoolAdminTeacherDomainAuthorizer.SchoolAdminTeacherState_HR_R_S_SG_U;
import fi.dwo.server.PersistentDataManagers.access.TeacherDomainAuthorizer.TeacherState_HR_R_S_SG_U;
import fi.dwo.server.PersistentDataManagers.access.UserDomainAuthorizer.UserState_HR_R_S_SG_U;
import fi.dwo.server.PersistentDataManagers.access.UserDomainAuthorizer.UserState_U;
import fi.dwo.server.PersistentDataManagers.core.ClassCourseManager;
import fi.dwo.server.PersistentDataManagers.core.CourseManager;
import fi.dwo.server.PersistentDataManagers.core.FromToManager;
import fi.dwo.server.PersistentDataManagers.core.HasRoleManager;
import fi.dwo.server.PersistentDataManagers.core.SamlUserManager;
import fi.dwo.server.PersistentDataManagers.core.SchoolClassManager;
import fi.dwo.server.PersistentDataManagers.core.SchoolGroupManager;
import fi.dwo.server.PersistentDataManagers.core.SchoolManager;
import fi.dwo.server.PersistentDataManagers.core.ScoContextManager;
import fi.dwo.server.PersistentDataManagers.core.ScoDataManager;
import fi.dwo.server.PersistentDataManagers.core.StudentOfClassManager;
import fi.dwo.server.PersistentDataManagers.core.StudentScoContextManager;
import fi.dwo.server.PersistentDataManagers.core.StudentScoDataManager;
import fi.dwo.server.PersistentDataManagers.core.TeacherOfClassManager;
import fi.dwo.server.PersistentDataManagers.core.UserManager;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.security.PermitAll;
import javax.persistence.PersistenceException;
import javax.ws.rs.GET;
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
        UserState_U user = AnonDomainAuthorizer.build().submitUser(sc.getUserPrincipal().getName());
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
