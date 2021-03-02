package fi.dwo.server.rest;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.ws.rs.HeaderParam;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;

import fi.dwo.commons.persistence.MySQLPersistenceId;
import fi.dwo.commons.persistence.entities.PersistentCourse;
import fi.dwo.commons.persistence.entities.PersistentHasRole;
import fi.dwo.commons.persistence.entities.PersistentImage;
import fi.dwo.commons.persistence.entities.PersistentSchoolClass;
import fi.dwo.commons.persistence.entities.PersistentScoContext;
import fi.dwo.server.PersistentDataManagers.access.AnonDomainAuthorizer;
import fi.dwo.server.PersistentDataManagers.access.UserDomainAuthorizer.UserState_HR_R_S_SG_U;
import fi.dwo.server.PersistentDataManagers.core.CourseManager;
import fi.dwo.server.PersistentDataManagers.core.ImageManager;
import fi.dwo.server.PersistentDataManagers.core.SchoolClassManager;
import fi.dwo.server.PersistentDataManagers.core.ScoContextManager;
import nl.uu.fi.dwo.rest.dom.entities.DomScoContext;
import nl.uu.fi.dwo.rest.dom.entities.RoleType;
import nl.uu.fi.dwo.rest.entities.RestCourse;
import nl.uu.fi.dwo.rest.entities.RestScoContext;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.exceptions.Dwo2ExceptionCode;
import nl.uu.fi.dwo.rest.exceptions.Dwo2RestException;

@Path("/secure/student/exam/scoContext")
public class SecuredStudentExamScoContextManager {
  private static final String PUBLIC_SCO_GET_IMAGE = "../../../../public/scoContext/getImage";

  private PersistentSchoolClass classOf(RestCourse rest) throws Dwo2Exception {
    Long id = MySQLPersistenceId.getNativeId(rest.getSchoolClassID());
    return SchoolClassManager.findEntity(id);
  }
  private PersistentCourse courseOf(RestCourse rest) throws Dwo2Exception {
    Long id = MySQLPersistenceId.getNativeId(rest.getDomCourse());
    return CourseManager.findEntity(id);
  }
  private DomScoContext builder(PersistentScoContext s, PersistentCourse parent, UriInfo info, String hasRoleId) {
    DomScoContext build = s.buildDomScoContext();
    if(s.getTrashID() != 0) build.setSequencenr(s.getTrashID());
    hasRoleId = "&hasRoleId=" + hasRoleId;
    String pfx = info.getRequestUri().resolve(PUBLIC_SCO_GET_IMAGE).toString();

    PersistentImage img = ImageManager.findEntity(s.getScoID());
    
    if(img != null) {
        build.setImage(pfx + "?scoId=" + s.getScoID() + hasRoleId);
    }
    return build;
}
  
  @Path("/get")
  @PUT
  @Produces({"application/json"})    
  public DomScoContext getSco(@Context SecurityContext sc, RestScoContext restScoContext,
                       @HeaderParam("X-ClassCourseID") String ccid, @HeaderParam("X-TOTP") String totp, @Context UriInfo info) {
    throw new Dwo2RestException(Dwo2ExceptionCode.Rest_LoginNeeded, "login needed");
  }
  
  @Path("/getScos")
  @PUT
  @Produces({"application/json"})    
  public List<DomScoContext> getScos(@Context SecurityContext sc, RestCourse rest,
                              @HeaderParam("X-ClassCourseID") String ccid, @HeaderParam("X-TOTP") String totp, @Context UriInfo info) throws Dwo2Exception {
    PersistentSchoolClass schoolClass = classOf(rest);
    PersistentCourse course = courseOf(rest);
    verifyTOTP(sc, ccid, totp, course, schoolClass);
    UserState_HR_R_S_SG_U state = AnonDomainAuthorizer.build().submitUser(sc.getUserPrincipal().getName()).setHasRole(rest.getRestContext().getDomHasRole());
    state.buildStudent();

    PersistentHasRole phr = state.getHasRole();
    Long pid = MySQLPersistenceId.getNativeId(rest.getDomDwoProfile());
    if (course.getSchoolID() != null) {
      if (!state.getSchool().getSchoolID().equals(course.getSchoolID()))
        return Collections.emptyList();
    }
    if (!course.getDwoProfileID().equals(pid)) {
        return Collections.emptyList();
    }
        
    List<PersistentScoContext> list = ScoContextManager.findEntities(course);
    String hasRoleId = phr.buildPersistenceId().getIdString();
    return list.stream().map((s)->builder(s,course,info,hasRoleId)).sorted(new DomScoContextComparator()).collect(Collectors.toList());     
  }
  
  private void verifyTOTP(SecurityContext sc, String ccid, String totp, PersistentCourse courseOf, PersistentSchoolClass classOf) throws Dwo2Exception {
    SecuredUserAccountManager.verifyTOTP(sc, ccid, totp, courseOf, classOf);   
  }

}
