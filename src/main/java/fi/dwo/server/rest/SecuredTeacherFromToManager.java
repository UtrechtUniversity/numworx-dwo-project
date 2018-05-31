package fi.dwo.server.rest;

import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.security.PermitAll;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;

import fi.dwo.commons.persistence.entities.PersistentSchool;
import fi.dwo.server.PersistentDataManagers.access.AnonDomainAuthorizer;
import fi.dwo.server.PersistentDataManagers.access.SchoolAdminTeacherDomainAuthorizer.SchoolAdminTeacherState_HR_R_S_SG_U;
import fi.dwo.server.PersistentDataManagers.core.FromToManager;
import fi.dwo.server.PersistentDataManagers.core.SchoolManager;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolFrom;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolFromTo;
import nl.uu.fi.dwo.rest.entities.RestContext;
import nl.uu.fi.dwo.rest.entities.RestSchoolFromTo;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;

@PermitAll
@Path("/secure/teacher/fromto")
public class SecuredTeacherFromToManager {

    @PUT
    @Path("get")
    @Produces({"application/json"})
    public DomSchoolFromTo get(@Context SecurityContext sc, RestContext rest) throws Dwo2Exception {
      SchoolAdminTeacherState_HR_R_S_SG_U state = AnonDomainAuthorizer.build().submitUser(sc.getUserPrincipal().getName())
          .setHasRole(rest.getRestContext().getDomHasRole()).buildSchoolAdminTeacher();
      DomSchoolFromTo result = new DomSchoolFromTo();
      return result;
    }

    @PUT
    @Path("set")
    @Produces({"application/json"})
    public Boolean  set(@Context SecurityContext sc, RestSchoolFromTo rest) throws Dwo2Exception {
      SchoolAdminTeacherState_HR_R_S_SG_U state = AnonDomainAuthorizer.build().submitUser(sc.getUserPrincipal().getName())
          .setHasRole(rest.getRestContext().getDomHasRole()).buildSchoolAdminTeacher();
      
      PersistentSchool school = null;
      FromToManager.findEntities(school);
      
      Boolean result = Boolean.FALSE;
      return result;
    }

    @PUT
    @Path("getExports")
    @Produces({"application/json"})
    public List<DomSchoolFrom> getExports(@Context SecurityContext sc, RestContext rest) throws Dwo2Exception {
      SchoolAdminTeacherState_HR_R_S_SG_U state = AnonDomainAuthorizer.build().submitUser(sc.getUserPrincipal().getName())
          .setHasRole(rest.getRestContext().getDomHasRole()).buildSchoolAdminTeacher();
    
      List<PersistentSchool> list = SchoolManager.findEntities();
      return list.stream()
          .filter(school -> Boolean.TRUE.equals(school.getExport()))
          .map(PersistentSchool::buildDomSchoolFrom)
          .collect(Collectors.toList());
    }
 
    
}
