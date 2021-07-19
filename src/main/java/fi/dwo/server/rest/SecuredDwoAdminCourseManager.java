package fi.dwo.server.rest;

import java.util.logging.Logger;

import javax.annotation.security.PermitAll;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;

import fi.dwo.server.PersistentDataManagers.access.AnonDomainAuthorizer;
import fi.dwo.server.PersistentDataManagers.access.DwoAdminDomainAuthorizer.DwoAdminState_HR_P_R_S_SG_U;
import nl.uu.fi.dwo.rest.dom.entities.DomCourseFull;
import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfileId;
import nl.uu.fi.dwo.rest.dom.entities.RoleType;
import nl.uu.fi.dwo.rest.entities.RestCourse;
import nl.uu.fi.dwo.rest.entities.RestCourseFull;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;

@PermitAll
@Path("/secure/dwoadmin/course")
public class SecuredDwoAdminCourseManager {
  private static final Logger LOG = Logger.getLogger(SecuredDwoAdminCourseManager.class.getName());

  @PUT
  @Path("update")
  @Produces({"application/json"})
  public DomCourseFull update(@Context SecurityContext sc, RestCourseFull rest)
      throws Dwo2Exception {
    PersistenceId id = rest.getDomCourse().getDwoProfileId();

    DwoAdminState_HR_P_R_S_SG_U state =
        AnonDomainAuthorizer.build().submitUser(sc)
            .setHasRoleIfType(rest.getRestContext().getDomHasRole(), RoleType.ADMIN).buildDwoAdmin()
            .addDwoProfile(new DomDwoProfileId(id));

    return state.addCourse(rest.getDomCourse()).update(rest.getDomCourse());
  }

  @PUT
  @Path("add")
  @Produces({"application/json"})
  public DomCourseFull add(@Context SecurityContext sc, RestCourseFull rest) throws Dwo2Exception {
    PersistenceId id = rest.getDomCourse().getDwoProfileId();
    DwoAdminState_HR_P_R_S_SG_U state =
        AnonDomainAuthorizer.build().submitUser(sc)
            .setHasRoleIfType(rest.getRestContext().getDomHasRole(), RoleType.ADMIN).buildDwoAdmin()
            .addDwoProfile(new DomDwoProfileId(id));
    DomCourseFull course = state.add(rest.getDomCourse());
    return course;
  }

  @PUT
  @Path("remove")
  @Produces({"application/json"})
  public Boolean remove(@Context SecurityContext sc, RestCourse rest) throws Dwo2Exception {
    DwoAdminState_HR_P_R_S_SG_U state =
        AnonDomainAuthorizer.build().submitUser(sc)
            .setHasRoleIfType(rest.getRestContext().getDomHasRole(), RoleType.ADMIN).buildDwoAdmin()
            .addDwoProfile(rest.getDomDwoProfile());
    return state.addCourse(rest.getDomCourse()).removeCourse();
  }
}
