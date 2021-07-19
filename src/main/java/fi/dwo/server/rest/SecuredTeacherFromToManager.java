package fi.dwo.server.rest;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.security.PermitAll;
import javax.persistence.PersistenceException;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;

import fi.dwo.commons.persistence.MySQLPersistenceId;
import fi.dwo.commons.persistence.entities.PersistentCourse;
import fi.dwo.commons.persistence.entities.PersistentDwoProfile;
import fi.dwo.commons.persistence.entities.PersistentFromTo;
import fi.dwo.commons.persistence.entities.PersistentFromToPK;
import fi.dwo.commons.persistence.entities.PersistentHasRole;
import fi.dwo.commons.persistence.entities.PersistentHasRolePK;
import fi.dwo.commons.persistence.entities.PersistentSchool;
import fi.dwo.server.PersistentDataManagers.access.AnonDomainAuthorizer;
import fi.dwo.server.PersistentDataManagers.access.SchoolAdminTeacherDomainAuthorizer.SchoolAdminTeacherState_HR_R_S_SG_U;
import fi.dwo.server.PersistentDataManagers.access.TeacherDomainAuthorizer.TeacherState_HR_P_R_S_SG_U;
import fi.dwo.server.PersistentDataManagers.core.CourseManager;
import fi.dwo.server.PersistentDataManagers.core.DwoProfileManager;
import fi.dwo.server.PersistentDataManagers.core.FromToManager;
import fi.dwo.server.PersistentDataManagers.core.HasRoleManager;
import fi.dwo.server.PersistentDataManagers.core.SchoolManager;
import nl.uu.fi.dwo.rest.dom.entities.DomCourse;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolFrom;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolFromTo;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolId;
import nl.uu.fi.dwo.rest.entities.RestContext;
import nl.uu.fi.dwo.rest.entities.RestSchoolAndProfile;
import nl.uu.fi.dwo.rest.entities.RestSchoolFromTo;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;

@PermitAll
@Path("/secure/teacher/fromto")
public class SecuredTeacherFromToManager {

    @PUT
    @Path("get")
    @Produces({"application/json"})
    public DomSchoolFromTo get(@Context SecurityContext sc, RestContext rest) throws Dwo2Exception {
      SchoolAdminTeacherState_HR_R_S_SG_U state = AnonDomainAuthorizer.build().submitUser(sc)
          .setHasRole(rest.getRestContext().getDomHasRole()).buildSchoolAdminTeacher();
      DomSchoolFromTo result = new DomSchoolFromTo();
      PersistentHasRolePK pk = MySQLPersistenceId.getNativeId(rest.getRestContext().getDomHasRole());
      PersistentHasRole role = HasRoleManager.findEntity(pk);      
      PersistentSchool school = role.getSchoolGroup().getSchool();

      Collection<PersistentFromTo> now;
      now = FromToManager.findEntities(school);
      result.setAll(Boolean.FALSE);
      result.setSchools(new ArrayList<>());
      for(PersistentFromTo item : now) {
        Long to = item.getPersistentFromToPK().getSchoolTo();
        if(to == -1L) result.setAll(Boolean.TRUE);
        else {
          PersistentSchool s = SchoolManager.findEntity(to);
          if (s != null)
            result.getSchools().add(s.buildDomSchoolFrom());
        }
      }
      
      return result;
    }

    @PUT
    @Path("set")
    @Produces({"application/json"})
    public Boolean  set(@Context SecurityContext sc, RestSchoolFromTo rest) throws Dwo2Exception {
      SchoolAdminTeacherState_HR_R_S_SG_U state = AnonDomainAuthorizer.build().submitUser(sc)
          .setHasRole(rest.getRestContext().getDomHasRole()).buildSchoolAdminTeacher();

      PersistentHasRolePK pk = MySQLPersistenceId.getNativeId(rest.getRestContext().getDomHasRole());
      PersistentHasRole role = HasRoleManager.findEntity(pk);      
      PersistentSchool school = role.getSchoolGroup().getSchool();

      Set<PersistentFromToPK> now;
      now = FromToManager.findEntities(school).stream().map(i->i.getPersistentFromToPK()).collect(Collectors.toSet());
      PersistentFromToPK opk;
      for(DomSchoolId item : rest.getSchoolFromTo().getSchools()) {
        Long schoolTo = MySQLPersistenceId.getNativeId(item);
        opk = new PersistentFromToPK(school.getSchoolID(), schoolTo);
        if(!now.remove(opk))
          FromToManager.create(new PersistentFromTo(opk));
      }
      
      opk = new PersistentFromToPK(school.getSchoolID(),-1L);
      if(Boolean.TRUE.equals(rest.getSchoolFromTo().getAll())) {
        if(!now.remove(opk))
          FromToManager.create(new PersistentFromTo(opk));
      }
      now.stream().forEach(FromToManager::destroy);
      Boolean result = Boolean.TRUE;
      return result;
    }

    @PUT
    @Path("getExports")
    @Produces({"application/json"})
    public List<DomSchoolFrom> getExports(@Context SecurityContext sc, RestContext rest) throws Dwo2Exception {
      SchoolAdminTeacherState_HR_R_S_SG_U state = AnonDomainAuthorizer.build().submitUser(sc)
          .setHasRole(rest.getRestContext().getDomHasRole()).buildSchoolAdminTeacher();
    
      List<PersistentSchool> list = SchoolManager.findEntities();
      return list.stream()
          .filter(school -> Boolean.TRUE.equals(school.getExport()))
          .map(PersistentSchool::buildDomSchoolFrom)
          .collect(Collectors.toList());
    }
 
    @PUT
    @Path("getCourses")
    @Produces({"application/json"})
    public List<DomCourse> getCourses(@Context SecurityContext sc, RestSchoolAndProfile rest) throws PersistenceException, Dwo2Exception {
      SchoolAdminTeacherState_HR_R_S_SG_U state = AnonDomainAuthorizer.build().submitUser(sc)
          .setHasRole(rest.getRestContext().getDomHasRole()).buildSchoolAdminTeacher();
      //TeacherState_HR_P_R_S_SG_U teacher = state.setTeacher().addProfile(rest.getDomSchoolAndProfile().getDomDwoProfile());
      
      PersistentSchool school = SchoolManager.findEntity(MySQLPersistenceId.getNativeId(rest.getDomSchoolAndProfile().getDomSchool()));
      PersistentDwoProfile profile = DwoProfileManager.findEntity(MySQLPersistenceId.getNativeId(rest.getDomSchoolAndProfile().getDomDwoProfile()));
// FIXME SECURITY....
      
      List<PersistentCourse> list = CourseManager.findExportsOf(school, profile);
      return list.stream().map(PersistentCourse::buildDomCourse).collect(Collectors.toList());
    }
}
