package fi.dwo.server.rest;

import nl.uu.fi.dwo.rest.dom.entities.DomHasRole;
import nl.uu.fi.dwo.rest.dom.entities.DomMapEntry;
import nl.uu.fi.dwo.rest.dom.entities.DomSchool4DwoAdmin;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolAdminAndHasRole;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolFull;
import nl.uu.fi.dwo.rest.dom.entities.DomStatistics;
import nl.uu.fi.dwo.rest.dom.entities.DomTeacherAndHasRole;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import fi.dwo.server.PersistentDataManagers.util.HasRoleUtilManager;
import fi.dwo.server.PersistentDataManagers.util.SchoolUtilManager;
import nl.uu.fi.dwo.rest.exceptions.Dwo2ExceptionCode;
import nl.uu.fi.dwo.rest.exceptions.Dwo2RestException;
import fi.dwo.commons.persistence.MySQLPersistenceId;
import nl.uu.fi.dwo.rest.dom.entities.RoleType;
import nl.uu.fi.dwo.rest.dom.entities.util.DelState;
import fi.dwo.commons.persistence.entities.PersistentACL;
import fi.dwo.commons.persistence.entities.PersistentClassCourse;
import fi.dwo.commons.persistence.entities.PersistentCourse;
import fi.dwo.commons.persistence.entities.PersistentFromTo;
import fi.dwo.commons.persistence.entities.PersistentHasRole;
import fi.dwo.commons.persistence.entities.PersistentLoginContext;
import fi.dwo.commons.persistence.entities.PersistentMethod;
import fi.dwo.commons.persistence.entities.PersistentSamlUser;
import fi.dwo.commons.persistence.entities.PersistentSchool;
import fi.dwo.commons.persistence.entities.PersistentSchoolClass;
import fi.dwo.commons.persistence.entities.PersistentSchoolGroup;
import fi.dwo.commons.persistence.entities.PersistentScoContext;
import fi.dwo.commons.persistence.entities.PersistentStudentModelContext;
import fi.dwo.commons.persistence.entities.PersistentStudentModelItem;
import fi.dwo.commons.persistence.entities.PersistentStudentOfClass;
import fi.dwo.commons.persistence.entities.PersistentStudentScoContext;
import fi.dwo.commons.persistence.entities.PersistentTeacherOfClass;
import fi.dwo.commons.persistence.entities.PersistentUser;
import nl.uu.fi.dwo.rest.dom.entities.DomNewSchool;
import nl.uu.fi.dwo.rest.entities.RestContext;
import nl.uu.fi.dwo.rest.entities.RestHasRole;
import nl.uu.fi.dwo.rest.entities.RestNewSchool;
import nl.uu.fi.dwo.rest.entities.RestSchool4DwoAdmin;
import nl.uu.fi.dwo.rest.entities.RestSchoolFull;
import fi.dwo.server.PersistentDataManagers.access.AnonDomainAuthorizer;
import fi.dwo.server.PersistentDataManagers.access.DwoAdminDomainAuthorizer.DwoAdminState_HR_R_S_SG_U;
import fi.dwo.server.PersistentDataManagers.access.UserDomainAuthorizer.UserState_HR_R_S_SG_U;
import fi.dwo.server.PersistentDataManagers.cache.HasRoleCache;
import fi.dwo.server.PersistentDataManagers.cache.SchoolCache;
import fi.dwo.server.PersistentDataManagers.core.ACLManager;
import fi.dwo.server.PersistentDataManagers.core.ClassCourseManager;
import fi.dwo.server.PersistentDataManagers.core.CourseDataManager;
import fi.dwo.server.PersistentDataManagers.core.CourseManager;
import fi.dwo.server.PersistentDataManagers.core.FromToManager;
import fi.dwo.server.PersistentDataManagers.core.HasRoleManager;
import fi.dwo.server.PersistentDataManagers.core.LoginContextManager;
import fi.dwo.server.PersistentDataManagers.core.MethodManager;
import fi.dwo.server.PersistentDataManagers.core.SamlUserManager;
import fi.dwo.server.PersistentDataManagers.core.SchoolClassManager;
import fi.dwo.server.PersistentDataManagers.core.SchoolGroupManager;
import fi.dwo.server.PersistentDataManagers.core.SchoolManager;
import fi.dwo.server.PersistentDataManagers.core.ScoContextManager;
import fi.dwo.server.PersistentDataManagers.core.ScoDataManager;
import fi.dwo.server.PersistentDataManagers.core.StudentModelContextManager;
import fi.dwo.server.PersistentDataManagers.core.StudentModelItemManager;
import fi.dwo.server.PersistentDataManagers.core.StudentOfClassManager;
import fi.dwo.server.PersistentDataManagers.core.StudentScoContextManager;
import fi.dwo.server.PersistentDataManagers.core.StudentScoDataManager;
import fi.dwo.server.PersistentDataManagers.core.TeacherOfClassManager;
import fi.dwo.server.PersistentDataManagers.core.UserManager;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.annotation.security.PermitAll;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceException;
import javax.persistence.RollbackException;
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
@Path("/secure/dwoadmin/school")
public class SecuredDwoAdminSchoolManager {

    private static final Logger LOG = Logger.getLogger(SecuredDwoAdminSchoolManager.class.getName());

    /**
     * Registers a new school and only a school. This operation is
     * semi-idempotent. School and SchoolGroup objects are only created if they
     * do not exists. Failed creations are logged but are non-fatal for the
     * execution. This allows to recreate a school if the creation process was
     * aborted during execution. I.e. some SchoolGroup objects are missing.
     *
     * @param sc
     * @param restSchool
     * @return
     * @throws Dwo2Exception 
     */
    @PUT
    @Produces({"application/json"})
    @Path("/submit")
    public Boolean submitSchool(@Context SecurityContext sc, RestNewSchool restSchool) throws Dwo2Exception {
        if (restSchool == null) {
            throw new Dwo2RestException(Dwo2ExceptionCode.Rest_FormatError, "Incorrect formatted REST-request.");
        }

        DwoAdminState_HR_R_S_SG_U state = AnonDomainAuthorizer.build().submitUser(sc).setHasRoleIfType(restSchool.getRestContext().getDomHasRole(), RoleType.ADMIN).buildDwoAdmin();
        
        
        
        DomNewSchool newSchool = restSchool.getDomNewSchool();

        {
            // allowed user role
            PersistentSchool s = new PersistentSchool();
            s.setExpire(newSchool.getDomSchoolFull().getExpire());
            s.setExport(newSchool.getDomSchoolFull().getExport());
            s.setImage(newSchool.getDomSchoolFull().getImage());
            s.setSchoolLogin(newSchool.getDomSchoolFull().getSchoolLogin());
            s.setSchoolName(newSchool.getDomSchoolFull().getSchoolName());
            s.setSchoolRights(newSchool.getDomSchoolFull().getSchoolRights());
            s.setAboType(newSchool.getDomSchoolFull().getAboType());
            SchoolCache.remove(s.getSchoolLogin());
            try {
                SchoolManager.create(s);
                s = SchoolManager.findBySchoolLogin(newSchool.getDomSchoolFull().getSchoolLogin());
                LOG.log(Level.INFO, "Username {0}: created school with schoollogin {1} and id {2}.", new Object[]{sc.getUserPrincipal().getName(), s.getSchoolLogin(), s.getSchoolID()});
                //add user roles
            } catch (PersistenceException e) {
                //non-fatal for semi-idempotent operation
                LOG.log(Level.INFO, "A Persistence exception occured while creating school with schoollogin {0}.", s.getSchoolLogin());
                throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, "An exception occured while creating school " + newSchool.getDomSchoolFull().getSchoolLogin() + ".");
            }
            for (Map.Entry<RoleType, String> entry : newSchool.getRoleTypePasswords().entrySet()) {
                PersistentSchoolGroup newSg = new PersistentSchoolGroup();
                newSg.setSchoolID(s.getSchoolID().intValue());
                newSg.setGroupID(entry.getKey().ordinal());
                newSg.setPasswd(entry.getValue());
                try {
                    SchoolGroupManager.create(newSg);
                } catch (PersistenceException e) {
                    //non-fatal for idempotent operation
                    String msg = MessageFormat.format("A Persistence exception occured while creating schoolgroup for school "
                            + "with logincode {0} and RoleType {1} (with groupid {2}).",
                            new Object[]{s.getSchoolLogin(), entry.getKey().name(), newSg.getGroupID()});
                    LOG.log(Level.INFO, msg);
                    throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, msg);
                }
            }
        } 
        return Boolean.TRUE;
    }

    /**
     * Returns a school from its persistent id.
     *
     * @param sc
     * @param school
     * @return Returns null if there was an error.
     */
    @PUT
    @Produces({"application/json"})
    @Path("/get")
    public DomSchoolFull getSchool(@Context SecurityContext sc, RestSchool4DwoAdmin school
    ) {
        if (school == null) {
            throw new Dwo2RestException(Dwo2ExceptionCode.Rest_FormatError, "Incorrect formatted REST-request.");
        }
        PersistentHasRole hr = null;
        
        try {
            UserState_HR_R_S_SG_U state = AnonDomainAuthorizer.build().submitUser(sc).setHasRoleIfType(school.getRestContext().getDomHasRole(), RoleType.ADMIN);
            hr = state.getHasRole();
            state.buildDwoAdmin();
        } catch (Dwo2Exception ex) {
            Logger.getLogger(SecuredDwoAdminSchoolManager.class.getName()).log(Level.SEVERE, "", ex);
            throw new Dwo2RestException(ex);
        }
        if (hr != null) {
            PersistentSchool s = null;
            try {
                s = SchoolManager.findEntity((Long) MySQLPersistenceId.getNativeId(school.getDomSchool4DwoAdmin()));
                LOG.log(Level.FINER, "Fetched school with id {0}. ", new Object[]{s.getSchoolID()});
                DomSchoolFull full = s.buildDomSchoolFull();
                List<PersistentSchoolGroup> list = SchoolGroupManager.findEntities(s);
                full.setPasswords(list.stream().map(item -> new DomMapEntry<>(item.getRoleType(), item.getPasswd())).collect(Collectors.toList()));
				return full;
            } catch (Exception e) {
                LOG.log(Level.WARNING, "School " + school.getDomSchool4DwoAdmin().getId() + "Could not be found.", e);
                throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, "An exception occured while fetching the school.");

            }
        } else {
            LOG.log(Level.WARNING, "Username {0}: ILLEGAL USER-OPERATION: Trying to access dwoadmin functionality by user with usercode {0}.", new Object[]{sc.getUserPrincipal().getName()});
            throw new Dwo2RestException(Dwo2ExceptionCode.User_IllegalAction, "You Don't Have Permission to access this using usercode " + sc.getUserPrincipal().getName() + ".");
        }
    }

    ;

    
    @PUT
    @Produces({"application/json"})
    @Path("/getList")
    public List<DomSchool4DwoAdmin> getSchools(@Context SecurityContext sc, RestContext rest) throws Dwo2Exception {
        PersistentHasRole hr = null;
        UserState_HR_R_S_SG_U state = AnonDomainAuthorizer.build().submitUser(sc).setHasRoleIfType(rest.getRestContext().getDomHasRole(), RoleType.ADMIN);
        hr = state.getHasRole();
        state.buildDwoAdmin();
    	
        return SchoolManager.findEntities().stream()
        		.filter(s -> DelState.not == s.getDelState())
        		.map(PersistentSchool::buildDomSchool4DwoAdmin)
        		.collect(Collectors.toList());
    }
    
    
    
        /**
         * Returns the school data to be displayed.
         *
         * @param sc
         * @return
         */
    @GET
    @Produces({"application/json"})
    @Path("/getList")
    public List<DomSchool4DwoAdmin> getSchools(@Context SecurityContext sc
    ) {
        PersistentHasRole hr = null;
        try {
            hr = HasRoleUtilManager.getCurrentHasRole(sc.getUserPrincipal().getName(), RoleType.ADMIN);
        } catch (Dwo2Exception ex) {
            Logger.getLogger(SecuredDwoAdminSchoolManager.class.getName()).log(Level.SEVERE, "", ex);
            throw new Dwo2RestException(ex);
        }
        if (hr != null) {
            List<PersistentSchool> schools = null;
            List<DomSchool4DwoAdmin> domSchools;
            try {
                schools = SchoolManager.findEntities();
                LOG.log(Level.FINER, "Fetched all {0} schools. ", new Object[]{schools.size()});
                domSchools = new ArrayList<>(schools.size());
                for (PersistentSchool s : schools) {
                    if (s.getDelState() == DelState.not)
                      domSchools.add(s.buildDomSchool4DwoAdmin());
                }
            } catch (Exception e) {
                LOG.log(Level.WARNING, "Unexpected exception", e);
                throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, "An exception occured while fetching the schools.");
            }
            return domSchools;
        } else {
            LOG.log(Level.WARNING, "Username {0}: ILLEGAL USER-OPERATION: Trying to access dwoadmin functionality by user with usercode {0}.", new Object[]{sc.getUserPrincipal().getName()});
            throw new Dwo2RestException(Dwo2ExceptionCode.User_IllegalAction, "You Don't Have Permission to access this using usercode " + sc.getUserPrincipal().getName() + ".");
        }
    }

    /**
     * Updates the User data of the current user and returns a copy of the
     * updated data. Ignores any schoolID values.
     *
     * @param sc
     * @param restSchool
     * @return
     */
  @PUT
  @Produces({"application/json"})
  @Path("/update")
  public Boolean updateSchool(@Context SecurityContext sc, RestSchoolFull restSchool) {
    if (restSchool == null) {
      throw new Dwo2RestException(Dwo2ExceptionCode.Rest_FormatError,
          "Incorrect formatted REST-request.");
    }
    PersistentHasRole hr = null;
    DomSchoolFull school = restSchool.getDomSchoolFull();
    try {
        UserState_HR_R_S_SG_U state = AnonDomainAuthorizer.build().submitUser(sc).setHasRoleIfType(restSchool.getRestContext().getDomHasRole(), RoleType.ADMIN);
        hr = state.getHasRole();
        state.buildDwoAdmin();
    } catch (Dwo2Exception ex) {
      Logger.getLogger(SecuredDwoAdminSchoolManager.class.getName()).log(Level.SEVERE, "", ex);
      throw new Dwo2RestException(ex);
    }
    if (hr != null) {
      try {
        PersistentSchool editSchool =
            SchoolManager.findEntity(MySQLPersistenceId.getNativeId(school));
        SchoolCache.remove(editSchool.getSchoolLogin());
        HasRoleCache.remove(editSchool);
        // User to update is logged in user.
        editSchool.setExpire(school.getExpire());
        // editSchool.setExport(school.getExport());
        editSchool.setImage(school.getImage());
        editSchool.setSchoolLogin(school.getSchoolLogin());
        editSchool.setSchoolName(school.getSchoolName());
        if(school.getAboType() != null) {
          editSchool.setAboType(school.getAboType());
        }
        // editSchool.setSchoolRights(school.getSchoolRights());
        SchoolManager.edit(editSchool);
        List<DomMapEntry<RoleType, String>> passwords = school.getPasswords();
        if (passwords != null) { // Optional
          List<PersistentSchoolGroup> schoolGroups =
              new ArrayList<>(SchoolGroupManager.findEntities(editSchool));
          Iterator<PersistentSchoolGroup> i = schoolGroups.iterator();
          while (i.hasNext()) {
            PersistentSchoolGroup persistentSchoolGroup = i.next();
            Iterator<DomMapEntry<RoleType, String>> j = passwords.iterator();
            while (j.hasNext()) {
              DomMapEntry<nl.uu.fi.dwo.rest.dom.entities.RoleType, java.lang.String> domMapEntry =
                  j.next();
              if (persistentSchoolGroup.getGroupID() == domMapEntry.getKey().ordinal()) {
                persistentSchoolGroup.setPasswd(domMapEntry.getValue());
                i.remove();
                j.remove();
                SchoolGroupManager.edit(persistentSchoolGroup);
              }

            }
          }
          // TODO wat met de leftovers te doen? nu ignore!
        }
        return Boolean.TRUE;
      } catch (Exception e) {
        LOG.log(Level.SEVERE,
            "Username " + sc.getUserPrincipal().getName() + ": Unexpected exception", e);
        throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError,
            "Failed to update school with login " + school.getSchoolLogin() + " .");
      }
    } else {
      LOG.log(Level.WARNING,
          "Username {0}: ILLEGAL USER-OPERATION: Trying to update the school with login {1}.",
          new Object[] {sc.getUserPrincipal().getName(), school.getSchoolLogin()});
      throw new Dwo2RestException(Dwo2ExceptionCode.User_IllegalAction,
          "You Don't Have Permission to update the school data.");
    }
  }

    /**
     * Removes all the school data of the current school and returns true.
     *
     * @param sc
     * @param restSchool
     * @return
     */
    @PUT
    @Produces({"application/json"})
    @Path("/remove")
    public Boolean removeSchool(@Context SecurityContext sc, RestSchool4DwoAdmin restSchool
    ) {
        if (restSchool == null) {
            throw new Dwo2RestException(Dwo2ExceptionCode.Rest_FormatError, "Incorrect formatted REST-request.");
        }
        //unwrap persistentid
        PersistentSchool school;
        try {
            school = SchoolManager.findEntity((Long) MySQLPersistenceId.getNativeId(restSchool.getDomSchool4DwoAdmin()));
        } catch (Dwo2Exception ex) {
            LOG.log(Level.SEVERE, null, ex);
            throw new Dwo2RestException(ex.getDwo2Code(), "Illegal Persistence key.");
        }
        LOG.log(Level.INFO, "Username " + sc.getUserPrincipal().getName() + " requests delete school with login "+school.getSchoolLogin()+" and id "+school.getSchoolID()+".");

        PersistentHasRole hr = null;
        try {
            UserState_HR_R_S_SG_U state = AnonDomainAuthorizer.build().submitUser(sc).setHasRoleIfType(restSchool.getRestContext().getDomHasRole(), RoleType.ADMIN);
            hr = state.getHasRole();
            state.buildDwoAdmin();
       } catch (Dwo2Exception ex) {
            Logger.getLogger(SecuredDwoAdminSchoolManager.class.getName()).log(Level.SEVERE, "", ex);
            throw new Dwo2RestException(ex);
        }
        LOG.log(Level.INFO, "Username " + sc.getUserPrincipal().getName() + " requests delete school with login "+school.getSchoolLogin()+" and id "+school.getSchoolID()+".");
        if (hr != null) {
            try {
        LOG.log(Level.INFO, "Username " + sc.getUserPrincipal().getName() + " started delete school with login "+school.getSchoolLogin()+" and id "+school.getSchoolID()+".");
                if (school.getDelState() == DelState.not) {
                  school.setDelState(DelState.marked);
                  school.setExpire(new java.util.Date());
                  SchoolManager.edit(school);
                  return Boolean.TRUE;
                }
                school.setDelState(DelState.deleted);
                SchoolManager.edit(school);
        // loop studentmodels en studentmodelitems
                List<PersistentStudentModelContext> smList = StudentModelContextManager.findEntities(school); // XXX ook met standaard 
                for(PersistentStudentModelContext sm: smList) {
                	if (school.getSchoolID().equals(sm.getSchoolID())) {
                		List<PersistentStudentModelItem> itemList = StudentModelItemManager.findEntities(sm);
                		itemList.forEach(item -> StudentModelItemManager.destroy(item.getItemID()));
                		StudentModelContextManager.destroy(sm.getModelID());
                	}}
        // loop methods
                List<PersistentMethod> mList = MethodManager.findEntities(school, null); // XXX ook met standaard 
                for (PersistentMethod m: mList) {
                	if (school.getSchoolID().equals(m.getSchoolID()))
                		MethodManager.destroy(m.getMethodID());
                }
        // Loop FromTos in School
                List<PersistentFromTo> ftList = FromToManager.findEntities(school);
                for (PersistentFromTo ft : ftList) {
                    //Remove FromTo
                    FromToManager.destroy(ft.getPersistentFromToPK());
                }

                //Loop SchoolGroups in School
                List<PersistentSchoolGroup> sgList = SchoolGroupManager.findEntities(school);
                for (PersistentSchoolGroup sg : sgList) {
                    //Loop hasRoles in SchoolGroups
                    List<PersistentHasRole> hrList = HasRoleManager.findEntities(sg);
                    for (PersistentHasRole phr : hrList) {

                        //Loop StudentOf in hasRole
                        List<PersistentStudentOfClass> soList = StudentOfClassManager.findEntities(phr.getPersistentHasRolePK());
                        for (PersistentStudentOfClass so : soList) {
                            //Remove StudentOf
                            StudentOfClassManager.destroy(so.getPersistentStudentOfClassPK());
                        }

                        //Loop TeacherOf in hasRole
                        List<PersistentTeacherOfClass> toList = TeacherOfClassManager.findEntities(phr.getPersistentHasRolePK());
                        for (PersistentTeacherOfClass to : toList) {
                            //Remove TeacherOf
                            TeacherOfClassManager.destroy(to.getPersistentTeacherOfClassPK());
                        }

                        //Loop StudentScoContext in hasRole
                        List<PersistentStudentScoContext> sscList = StudentScoContextManager.findEntities(phr.getPersistentHasRolePK());
                        for (PersistentStudentScoContext ssc : sscList) {
                          try {
                            StudentScoDataManager.destroy(ssc.getStudentSco()); //  non-fatal. studentscodata
                          } catch (EntityNotFoundException e1) {}
                          try {
                            StudentScoContextManager.destroy(ssc.getStudentSco());
                          } catch (EntityNotFoundException e) {}
                        }
                        //Remove hasRole
                        HasRoleManager.destroy(phr.getPersistentHasRolePK());
                        PersistentUser u;
                        //u = UserManager.findEntity(phr.getUser().getId()); // NPE if not found
                        u = phr.getUser(); //eager fetch

                        if (u != null && u.isSingleSchoolAccount()) {
                            //Loop samlusers in user
                            List<PersistentSamlUser> suList = SamlUserManager.findEntities(u);
                            for (PersistentSamlUser su : suList) {
                                //remove saml user
                                SamlUserManager.destroy(su.getId());
                            }
                            try {
                            	LoginContextManager.findEntities(u.getId()).forEach(item -> LoginContextManager.destroy(item.getId()));
                            } catch (PersistenceException e) {
                            	
                            }
                           //remove user
                            UserManager.destroy(u.getId());
                        }
                    }
                    //Clear tblUser schoolgroup values
                    PersistentSchoolGroup nulSg = (PersistentSchoolGroup) SchoolGroupManager.findEntity(SchoolUtilManager.findBySchoolLogin("null"), RoleType.STUDENT);
                    List<PersistentUser> userList = UserManager.findEntities(sg);
                    if (userList != null) {
                        for (PersistentUser u : userList) {
                            u.setSchoolGroupId(nulSg.getSchoolGroupID());
                            UserManager.edit(u);
                        }
                    }

                    //Remove SchoolGroup
                    SchoolGroupManager.destroy(sg.getSchoolGroupID());
                }

                //Loop SchoolClasses in School
                List<PersistentSchoolClass> clList = SchoolClassManager.findEntities(school);
                for (PersistentSchoolClass cl : clList) {
                    //Loop ClassCourses in SchoolClass
                    List<PersistentClassCourse> ccList = ClassCourseManager.findEntities(cl);
                    for (PersistentClassCourse cc : ccList) {
                        //Remove ClassCourse
                        ClassCourseManager.destroy(cc.getClassCourseID());
                    }

                    //Remove FromTo
                    SchoolClassManager.destroy(cl.getClassID());
                }
                List<PersistentCourse> cList;
                //Loop Courses in School
                do { 
                cList = CourseManager.findEntities(school, 100);
                for (PersistentCourse c : cList) {
                    //Loop All ScoContext in Course
                    List<PersistentScoContext> pscList = ScoContextManager.findEntities(c);
                    pscList.addAll(ScoContextManager.findTrashedEntities(c));
                    for (PersistentScoContext psc : pscList) {
                        //Remove ScoData, if exists                       
                        try {
                          ScoDataManager.destroy(psc.getScoID());
                        } catch (EntityNotFoundException e) {
                          LOG.log(Level.WARNING, "ignored", e);
                        }
                        //Remove ScoContext
                        ScoContextManager.destroy(psc.getScoID());
                    }
                    List<PersistentACL> acls = ACLManager.findByCourse(c);
                    acls.forEach(acl -> ACLManager.destroy(acl.getAclID()));
                    ///Remove Course
                    CourseManager.destroy(c.getCourseID());
                    CourseDataManager.destroy(c.getCourseID());
                }
                } while ( ! cList.isEmpty() );
                SchoolManager.destroy(school.getSchoolID());
            } catch (Exception e) {
                LOG.log(Level.SEVERE, "Username " + sc.getUserPrincipal().getName() + ": Unexpected exception", e);
                throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, "Failed to remove school with id " + school.getSchoolID() + " .");
            }
        } else {
            LOG.log(Level.WARNING, "Username {0}: ILLEGAL USER-OPERATION: Trying to remove the school with id {1}.", new Object[]{sc.getUserPrincipal().getName(), school.getSchoolID()});
            throw new Dwo2RestException(Dwo2ExceptionCode.User_IllegalAction, "You Don't Have Permission to rempve the school.");
        }
        LOG.log(Level.INFO, "Delete school with login "+school.getSchoolLogin()+" and id "+school.getSchoolID()+" completed for username " + sc.getUserPrincipal().getName()+".");
        return true;
    }
    @PUT
    @Produces({"application/json"})
    @Path("/getSchoolAdminsAndHasRoleInSchool")
    public List<DomSchoolAdminAndHasRole> getSchoolAdminsAndHasRoleInSchool(@Context SecurityContext sc, RestSchool4DwoAdmin restSchool
    ) throws Dwo2Exception {
        UserState_HR_R_S_SG_U state = AnonDomainAuthorizer.build().submitUser(sc).setHasRoleIfType(restSchool.getRestContext().getDomHasRole(), RoleType.ADMIN);
        state.buildDwoAdmin();
        PersistentSchool school = state.getSchool(); // niet je eigen, maar een andere!
        school = SchoolManager.findBySchoolLogin(restSchool.getDomSchool4DwoAdmin().getSchoolLogin());
        List<PersistentHasRole> hrList;
        List<DomSchoolAdminAndHasRole> resultList = null;
        hrList = HasRoleUtilManager.getHasRolesInSchoolAndRole(school, RoleType.SCHOOLADMIN);
        resultList = new ArrayList<>(hrList.size());
        for (PersistentHasRole hr : hrList) {
            PersistentUser user = UserManager.findEntity(hr.getPersistentHasRolePK().getUserID());
            DomSchoolAdminAndHasRole domTAHR = new DomSchoolAdminAndHasRole();
            domTAHR.setSchoolAdmin(user.buildDomSchoolAdmin(null));
            domTAHR.setHasRole(hr.buildDomHasRole());
            resultList.add(domTAHR);
        }
        return resultList;
    }
    
    /**
     * Returns the school data to be displayed.
     *
     * @param sc
     * @param restSchool
     * @return
     */
    @PUT
    @Produces({"application/json"})
    @Path("/getTeachersAndHasRoleInSchool")
    public List<DomTeacherAndHasRole> getTeachersAndHasRoleInSchool(@Context SecurityContext sc, RestSchool4DwoAdmin restSchool
    ) {
        if (restSchool == null) {
            throw new Dwo2RestException(Dwo2ExceptionCode.Rest_FormatError, "Incorrect formatted REST-request.");
        }
        PersistentHasRole phr = null;
        PersistentSchool school = null;
        List<DomTeacherAndHasRole> resultList = null;

        try {
            UserState_HR_R_S_SG_U state = AnonDomainAuthorizer.build().submitUser(sc).setHasRoleIfType(restSchool.getRestContext().getDomHasRole(), RoleType.ADMIN);
            phr = state.getHasRole();
            state.buildDwoAdmin();
        } catch (Dwo2Exception ex) {
            LOG.log(Level.SEVERE, "", ex);
            throw new Dwo2RestException(ex);
        }
        if (phr==null) {
            LOG.log(Level.SEVERE, "User {0} not in admin role.", sc.getUserPrincipal().getName());
            throw new Dwo2RestException(Dwo2ExceptionCode.User_IllegalAction, "Not in a DWO Admin role.");
        }
        school = SchoolManager.findBySchoolLogin(restSchool.getDomSchool4DwoAdmin().getSchoolLogin());
        if (school == null) {
            LOG.log(Level.SEVERE, "School with login {0} was not found.", restSchool.getDomSchool4DwoAdmin().getSchoolLogin());
            throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, "School not found.");
        }

        List<PersistentHasRole> hrList;
        try {
            hrList = HasRoleUtilManager.getHasRolesInSchoolAndRole(school, RoleType.TEACHER);
            resultList = new ArrayList<>(hrList.size());
            for (PersistentHasRole hr : hrList) {
                PersistentUser user = (PersistentUser) UserManager.findEntity(hr.getPersistentHasRolePK().getUserID());
                DomTeacherAndHasRole domTAHR = new DomTeacherAndHasRole();
                domTAHR.setTeacher(user.buildDomTeacher(null));
                domTAHR.setHasRole(hr.buildDomHasRole());
                resultList.add(domTAHR);
            }
        } catch (Dwo2Exception ex) {
            LOG.log(Level.SEVERE, "", ex);
            throw new Dwo2RestException(ex);
        }
        return resultList;
    }

    /**
     * Updates the User data of the current user and returns a copy of the
     * updated data. Ignores any schoolID values.
     *
     * @param sc
     * @param restHasRole
     * @return
     */
    @PUT
    @Produces({"application/json"})
    @Path("/updateHasRoleRights")
    public Boolean updateHasRoleRights(@Context SecurityContext sc, RestHasRole restHasRole
    ) {
        if (restHasRole == null) {
            throw new Dwo2RestException(Dwo2ExceptionCode.Rest_FormatError, "Incorrect formatted REST-request.");
        }
        PersistentHasRole hr = null;
        DomHasRole domHasRole = restHasRole.getDomHasRole();
        try {
            UserState_HR_R_S_SG_U state = AnonDomainAuthorizer.build().submitUser(sc).setHasRoleIfType(restHasRole.getRestContext().getDomHasRole(), RoleType.ADMIN);
            hr = state.getHasRole();
            state.buildDwoAdmin();
        } catch (Dwo2Exception ex) {
            Logger.getLogger(SecuredDwoAdminSchoolManager.class.getName()).log(Level.SEVERE, "", ex);
            throw new Dwo2RestException(ex);
        }
        if (hr != null) {
            try {
                PersistentHasRole pHasRole = HasRoleManager.findEntity(
                        MySQLPersistenceId.getNativeId(domHasRole));
                pHasRole.setRights(domHasRole.getRights());
                HasRoleManager.editRights(pHasRole);
                return true;
            } catch (Exception e) {
                LOG.log(Level.SEVERE, "Username " + sc.getUserPrincipal().getName() + ": Unexpected exception", e);
                throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, "User with hasRole " + hr.getPersistentHasRolePK() + " failed to update rights of hasrole " + domHasRole.getId() + " to rightsString " + domHasRole.getRights() + " .");
            }
        } else {
            LOG.log(Level.WARNING, "Username {0}: ILLEGAL USER-OPERATION: Trying to update the hasRole {0} with user login {1}.", new Object[]{domHasRole.getId(), sc.getUserPrincipal().getName()});
            throw new Dwo2RestException(Dwo2ExceptionCode.User_IllegalAction, "You Don't Have Permission to update the school data.");
        }
    }
    
    @PUT
    @Produces({"application/json"})
    @Path("/add")
    public DomSchoolFull addSchool(@Context SecurityContext sc, RestSchoolFull rest) throws Dwo2Exception {
      DwoAdminState_HR_R_S_SG_U state = AnonDomainAuthorizer.build()
          .submitUser(sc)
          .setHasRoleIfType(rest.getRestContext().getDomHasRole(), RoleType.ADMIN)
          .buildDwoAdmin();
// TODO move to Action:
        try {
			DomSchoolFull school = rest.getDomSchoolFull();
			PersistentSchool ps = new PersistentSchool();
			ps.setSchoolLogin(school.getSchoolLogin());
			ps.setSchoolName(school.getSchoolName());
			ps.setSchoolRights("_"); // Default rights
			school.setSchoolRights("_");
			ps.setExport(Boolean.FALSE); // no export
			school.setExport(Boolean.FALSE);
			ps.setExpire(school.getExpire());
			ps.setAboType(school.getAboType());
			SchoolManager.create(ps);
			List<DomMapEntry<RoleType, String>> passwords = school.getPasswords();
			for(DomMapEntry<RoleType, String> entry: passwords) {
			  PersistentSchoolGroup psg = new PersistentSchoolGroup();
			  psg.setPasswd(entry.getValue());
			  psg.setSchoolID(ps.getSchoolID().intValue());
			  psg.setGroupID(entry.getKey().ordinal());
			  SchoolGroupManager.create(psg);
			}
		     school.setId(ps.buildPersistenceId());
		     return school;
		} catch (RollbackException e) {
			LOG.log(Level.SEVERE, "rollback addSchool");
			throw new Dwo2Exception(Dwo2ExceptionCode.Rest_ObjectAlreadyExists, e.toString());
		}
    }

    @PUT
    @Produces({"application/json"})
    @Path("/statistics")
    public DomStatistics getStatistics(@Context SecurityContext sc, RestSchool4DwoAdmin rest) throws Dwo2Exception {
        DwoAdminState_HR_R_S_SG_U state = AnonDomainAuthorizer.build()
                .submitUser(sc)
                .setHasRoleIfType(rest.getRestContext().getDomHasRole(), RoleType.ADMIN)
                .buildDwoAdmin();
   	
        DomStatistics result = new DomStatistics();
        result.setSchool(rest.getDomSchool4DwoAdmin());
        result.setFetchTimeStamp(System.currentTimeMillis());
        
        List<DomMapEntry<String, String>> stats = new ArrayList<>();
        
        Long id = MySQLPersistenceId.getNativeId(rest.getDomSchool4DwoAdmin());
        PersistentSchool school = SchoolManager.findEntity(id);
        stats.add(new DomMapEntry<>("id", school.buildPersistenceId().getIdString()));
    	DomMapEntry<String, String> entry;
    {    List<PersistentSchoolGroup> groups = SchoolGroupManager.findEntity(school);
        long studentsco = 0;
        for (PersistentSchoolGroup g: groups) {
        	entry = new DomMapEntry<>(g.getRoleType() + " id", g.buildPersistenceId().getIdString());
        	stats.add(entry);        	
        	List<PersistentHasRole> users = HasRoleManager.findEntities(g);
			int size = users.size();
        	entry = new DomMapEntry<>(g.getRoleType() + " size", Integer.toString(size));
        	stats.add(entry);
            long count;
            long stamp = (System.currentTimeMillis() - 365 * 24 * 3600 * 1000L);
        	count = users.stream()
        	    .filter( f -> f.getUser() != null)
        	    .flatMap(u -> {
        	      List<PersistentLoginContext> entities = LoginContextManager.findEntities(u.getUser().getId());
        	      if (entities == null) entities = Collections.emptyList();
        	      return entities.stream();
        	    })
        	        .filter(u -> {
        	          return u.getLastLogin() != null && u.getLastLogin().longValue()>(stamp);
        	        }).count();
            entry = new DomMapEntry<>(g.getRoleType().name() + " active", Long.toString(count));
            stats.add(entry);
        	count = users.stream()
        	    .filter(f -> f.getUser() != null) // NPE checks
        	    .map(PersistentHasRole::getUser)
        	    .filter(u -> u.isSingleSchoolAccount() != null) // NPE checks
        	    .filter(PersistentUser::isSingleSchoolAccount).count();
        	if (count > 0) {
        		entry = new DomMapEntry<>("singleschool users", Long.toString(count));
        		stats.add(entry);
        	}
        	
        	studentsco += StudentScoContextManager.getEntityCount(g);
        }
    	entry = new DomMapEntry<>("studentscos", Long.toString(studentsco));
    	stats.add(entry);       
}   
        {
        	Long size = SchoolClassManager.getEntityCount(school);
        	entry = new DomMapEntry<>("classes", size.toString());
        	stats.add(entry);       
        }
        {
        	Long count, scocount;
        	count = CourseManager.getEntityCount(school);
        	scocount = ScoContextManager.getEntityCount(school);
         	entry = new DomMapEntry<>("courses", count.toString());
        	stats.add(entry);
        	entry = new DomMapEntry<>("scos", scocount.toString());
        	stats.add(entry);
        }
        {
        	Long size = StudentModelContextManager.getEntityCount(school);
        	entry = new DomMapEntry<>("models", size.toString());
        	stats.add(entry);
        }
        
        
        
        result.setStatistics(stats);
        return result;
    }

}
