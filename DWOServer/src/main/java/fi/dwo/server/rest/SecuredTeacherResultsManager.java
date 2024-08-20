package fi.dwo.server.rest;

import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import fi.dwo.server.PersistentDataManagers.util.HasRoleUtilManager;
import nl.uu.fi.dwo.rest.exceptions.Dwo2ExceptionCode;
import nl.uu.fi.dwo.rest.exceptions.Dwo2RestException;
import fi.dwo.commons.persistence.MySQLPersistenceId;
import fi.dwo.commons.persistence.entities.PersistentClassCourse;
import fi.dwo.commons.persistence.entities.PersistentCourse;
import fi.dwo.commons.persistence.entities.PersistentCourseInClass;
import fi.dwo.commons.persistence.entities.PersistentDwoProfile;
import fi.dwo.commons.persistence.entities.PersistentHasRole;
import fi.dwo.commons.persistence.entities.PersistentHasRolePK;
import fi.dwo.commons.persistence.entities.PersistentSchool;
import fi.dwo.commons.persistence.entities.PersistentSchoolClass;
import fi.dwo.commons.persistence.entities.PersistentSchoolGroup;
import fi.dwo.commons.persistence.entities.PersistentScoContext;
import fi.dwo.commons.persistence.entities.PersistentScoPage;
import fi.dwo.commons.persistence.entities.PersistentStudentInClass;
import fi.dwo.commons.persistence.entities.PersistentStudentOfClass;
import fi.dwo.commons.persistence.entities.PersistentStudentOfClassPK;
import fi.dwo.commons.persistence.entities.PersistentStudentScoContext;
import fi.dwo.commons.persistence.entities.PersistentTeacherOfClassPK;
import fi.dwo.commons.persistence.entities.PersistentUser;
import nl.uu.fi.dwo.rest.util.DwoDateUtilities;
import fi.dwo.server.PersistentDataManagers.access.AnonDomainAuthorizer;
import fi.dwo.server.PersistentDataManagers.access.CascadingPersistenceBuilder;
import fi.dwo.server.PersistentDataManagers.access.TeacherDomainAuthorizer.TeacherState_C_CC_HR_P_R_S_SC_SCO_SG_U;
import fi.dwo.server.PersistentDataManagers.access.TeacherDomainAuthorizer.TeacherState_HR_P_R_S_SG_U;
import fi.dwo.server.PersistentDataManagers.access.TeacherDomainAuthorizer.TeacherState_HR_R_S_SG_U;
import fi.dwo.server.PersistentDataManagers.access.UserDomainAuthorizer.UserState_HR_R_S_SG_U;
import fi.dwo.server.PersistentDataManagers.core.ClassCourseManager;
import fi.dwo.server.PersistentDataManagers.core.CourseManager;
import fi.dwo.server.PersistentDataManagers.core.DwoProfileManager;
import fi.dwo.server.PersistentDataManagers.core.HasRoleManager;
import fi.dwo.server.PersistentDataManagers.core.SchoolClassManager;
import fi.dwo.server.PersistentDataManagers.core.SchoolGroupManager;
import fi.dwo.server.PersistentDataManagers.core.ScoContextManager;
import fi.dwo.server.PersistentDataManagers.core.ScoPageManager;
import fi.dwo.server.PersistentDataManagers.core.StudentOfClassManager;
import fi.dwo.server.PersistentDataManagers.core.StudentScoContextManager;
import fi.dwo.server.PersistentDataManagers.core.TeacherOfClassManager;
import fi.dwo.server.PersistentDataManagers.core.UserManager;
import fi.dwo.server.PersistentDataManagers.util.CourseInClassManager;
import fi.dwo.server.PersistentDataManagers.util.SchoolClassUtilManager;
import fi.dwo.server.PersistentDataManagers.util.StudentInClassManager;
import fi.dwo.server.rest.util.Realm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.persistence.PersistenceException;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;
import nl.uu.fi.dwo.rest.dom.entities.DomClassCourse4Teacher;
import nl.uu.fi.dwo.rest.dom.entities.DomClearStudentDataForScoAndClass;
import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.dom.entities.DomCourse;
import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfile;
import nl.uu.fi.dwo.rest.dom.entities.DomHasRole;
import nl.uu.fi.dwo.rest.dom.entities.DomMapEntry;
import nl.uu.fi.dwo.rest.dom.entities.DomResultsPerTeacher;
import nl.uu.fi.dwo.rest.dom.entities.DomResultsPerTeacherv2;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClassId;
import nl.uu.fi.dwo.rest.dom.entities.DomScoContext;
import nl.uu.fi.dwo.rest.dom.entities.DomStudent;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentOfClass;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentScoContext;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentScoPage;
import nl.uu.fi.dwo.rest.dom.entities.DomTeacher;
import nl.uu.fi.dwo.rest.dom.entities.RoleType;
import nl.uu.fi.dwo.rest.dom.entities.util.ViewState;
import nl.uu.fi.dwo.rest.entities.RestClearStudentDataForScoAndClass;
import nl.uu.fi.dwo.rest.entities.RestDwoProfile;
import nl.uu.fi.dwo.rest.entities.RestResultsPerTeacher;
import nl.uu.fi.dwo.rest.entities.RestResultsPerTeacherv2;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;

/**
 * Operations for the GUI Component that manages the school classes.
 *
 * @see fi.dwo.dwojapplet.gui.panels.JPanel.MyProfile
 *
 * @author G.A.J. van der Plas
 */
@SuppressWarnings("deprecation")
@PermitAll
@Path("/secure/teacher/results")
public class SecuredTeacherResultsManager extends AbstractSchoolClassManager {

    private static final Logger LOG = Logger.getLogger(SecuredTeacherResultsManager.class.getName());
    
    @PUT
    @Path("/selectedTeachersResultsv2")
    @RolesAllowed("TEACHER")
    public DomResultsPerTeacherv2 selectedTeachersResults(@Context SecurityContext sc, RestResultsPerTeacherv2 rest) throws Dwo2Exception {
    	UserState_HR_R_S_SG_U ustate = AnonDomainAuthorizer.build().submitUser(sc).setRealm(rest.getRestContext().getRealm())
    			.setHasRole(rest.getRestContext().getDomHasRole());
    	PersistentHasRole phr = ustate.getHasRole();
    	TeacherState_HR_P_R_S_SG_U state = ustate.buildSchoolAdminTeacher().setTeacher().addProfile(rest.getDomDwoProfile());
    	PersistentDwoProfile profile = state.getDwoProfile();
    	DomResultsPerTeacherv2 dom = rest.getDomResultsPerTeacher();
    	dom.setTeacher(ustate.getUser().buildDomTeacher(ustate.getRealm()));
        List<PersistentSchoolClass> scl;    	
    	if (dom.getSchoolClasses() == null) {
    		scl = SchoolClassUtilManager.getSchoolClassesOfTeacher(phr);
    	} else {
    		scl = new ArrayList<>();
    		for(DomSchoolClass dsc: dom.getSchoolClasses()) {
    			Long id = MySQLPersistenceId.getNativeId(dsc);
    			PersistentTeacherOfClassPK pk = new PersistentTeacherOfClassPK(phr.getUser().getId(), id, phr.getSchoolGroup().getSchoolGroupID());;
				if (TeacherOfClassManager.findEntity(pk) == null) continue; // security check
    			PersistentSchoolClass pcl = SchoolClassManager.findEntity(id);
				scl.add(pcl);
    		}
    	}
		dom.setSchoolClasses(scl.stream().map(PersistentSchoolClass::buildDomSchoolClass).collect(Collectors.toList()));

		Set<PersistentCourse> courses = new HashSet<>();
    	if (dom.getCourses() == null && dom.getClassCourses() == null) {    		
    		dom.setClassCourses(new ArrayList<>());
    		for (PersistentSchoolClass psc: scl) {
    	          List<PersistentCourseInClass> list = CourseInClassManager.findLeaveEntities(psc, profile);
    	          for (PersistentCourseInClass entry : list) {
    	        	  courses.add(entry.getCourse()); // not unique
    	        	  dom.getClassCourses().add(entry.getClassCourse().buildDomClassCourse4Teacher());
    	          }	
    		}
    		dom.setCourses(courses.stream().map(PersistentCourse::buildDomCourse).collect(Collectors.toList()));
    		return dom;
    	} else if (dom.getCourses() != null) {
    		courses = dom.getCourses()
    				.stream()
    				.map(item -> {
						try {
							return CourseManager.findEntity(MySQLPersistenceId.getNativeId(item));
						} catch (PersistenceException e) {
						} catch (Dwo2Exception e) {
						}
						return null;
					})
    				.collect(Collectors.toSet());
    	    		dom.setCourses(courses.stream().map(PersistentCourse::buildDomCourse).collect(Collectors.toList()));
    	    if (dom.getClassCourses() == null) {
        		dom.setClassCourses(new ArrayList<>());
        		for (PersistentSchoolClass psc: scl) {
        	          List<PersistentCourseInClass> list = CourseInClassManager.findLeaveEntities(psc, profile);
        	          for (PersistentCourseInClass entry : list) {
        	        	  if (courses.contains(entry.getCourse()))
        	        	  dom.getClassCourses().add(entry.getClassCourse().buildDomClassCourse4Teacher());
        	          }	
        		}
   	    	
    	    }
    	
    	
    	
    	}
  // students
        Map<Long,PersistentUser> studentMap = new HashMap<>();
        List<PersistentStudentOfClass> studentOfClassList;
		if ( dom.getStudentsOfClasses() == null) {
          studentOfClassList = 
          scl.stream().flatMap(item -> {
            List<PersistentStudentInClass> cicList = StudentInClassManager.findEntities(item);
            cicList.forEach(cic -> studentMap.put(cic.getUser().getId(), cic.getUser()));
            return cicList.stream().map(PersistentStudentInClass::getStudentOfClass);
          }).collect(Collectors.toList());
          dom.setStudentsOfClasses(studentOfClassList.stream().map(PersistentStudentOfClass::buildDomStudentOfClass).collect(Collectors.toList()));
        } else {
          studentOfClassList = new ArrayList<>();
          dom.getStudentsOfClasses().forEach(entry -> {
            DomStudentOfClass id = entry;
            PersistentStudentOfClass psoc = StudentOfClassManager.findEntity(MySQLPersistenceId.getNativeId(id));
            Long userID = psoc.getPersistentStudentOfClassPK().getUserID();
            studentMap.put(userID, UserManager.findEntity(userID));
            studentOfClassList.add(psoc);
            id = psoc.buildDomStudentOfClass();
            entry.setStudentId(id.getStudentId());
            entry.setClassId(id.getClassId()); // classid must by of teacher!!!!
          });
        }
        if ( dom.getStudents() != null) {
          Map<Long,PersistentUser> allStudents = new HashMap<>(studentMap);
          studentMap.clear();;
          dom.getStudents().forEach(entry -> {
            try {
              Long pid = MySQLPersistenceId.getNativeId(entry);
              PersistentUser u = allStudents.get(pid);
              if (u != null) {
                studentMap.put(pid, u);
              } 
            } catch (Dwo2Exception e) {
            }
          });
          // filter students from students of class list
          Iterator<DomStudentOfClass> iterator = dom.getStudentsOfClasses().iterator();
          Iterator<PersistentStudentOfClass> i2 = studentOfClassList.iterator();
          while (iterator.hasNext()) {
			iterator.next();
			PersistentStudentOfClass soc = i2.next();
			Long pid = soc.getPersistentStudentOfClassPK().getUserID();
			if (!studentMap.containsKey(pid)) {
// same order!!!
				iterator.remove();
				i2.remove();			
			}
          }
        }
        dom.setStudents(studentMap.values()
                .stream().map((PersistentUser item) -> item.buildDomStudent(ustate.getRealm()))
                .collect(Collectors.toList()));
 //scos  	
        Collection<PersistentScoContext> scos;
        if (dom.getScoContexts() == null) {
        	scos = courses.stream().flatMap(
    			item -> ScoContextManager.findEntities(item).stream()
    			)
    			.collect(Collectors.toList());
        } else {
        	scos = dom.getScoContexts().stream().map(item -> {
        		try {
					Long id = MySQLPersistenceId.getNativeId(item);
					return ScoContextManager.findEntity(id);
				} catch (Exception e) {
					LOG.log(Level.WARNING, "getScoContext", e);
					return null;
				}
        	})
        		.filter(Objects::nonNull)
        		.collect(Collectors.toList());
        }
    	dom.setScoContexts( 
    			scos.stream()
    			.map(PersistentScoContext::buildDomScoContext)
    			.collect(Collectors.toList()));
// scopages
    	dom.setStudentScoPages(scos.stream().
    			flatMap(sco -> ScoPageManager.find(sco).stream())
    			.map(page -> buildDomScoPage(page, null))
    			.collect(Collectors.toList())
    	);
    	
// students + scos = studentscos  	
    	Collection<PersistentStudentScoContext> sscs = 
    			studentOfClassList.stream()
    			.flatMap(
        				item -> {
        					PersistentHasRolePK pk = new PersistentHasRolePK(item.getPersistentStudentOfClassPK().getUserID(), item.getPersistentStudentOfClassPK().getSchoolGroupID());
        					return scos.stream().flatMap( 
        							sco -> 
        							StudentScoContextManager.findEntities(sco, pk).stream());
        				})
    			.collect(Collectors.toList());
    	
    	dom.setStudentScoContexts(
    			sscs.stream()
    			.map(PersistentStudentScoContext::buildDomStudentScoContext)
    			.collect(Collectors.toList()));
// studentpages    			
    	List<DomStudentScoPage> studentpages = dom.getStudentScoPages();
    	if (!studentpages.isEmpty()) {
    		studentpages.addAll(
		    	sscs
		    	.stream()
				.flatMap(item -> 
					ScoPageManager.find(item)
						.stream()
						.map(i -> buildDomScoPage(i, item))								   )
				.collect(Collectors.toList()));
			dom.setStudentScoPages(studentpages);	
    	}
 // dom.setStudentScoPages(Collections.emptyList()); // even uit zetten  	
    	return dom;
    }
    
    

    @PUT
    @Path("/selectedTeachersResults")
    public DomResultsPerTeacher selectedTeachersResults(@Context SecurityContext sc, RestResultsPerTeacher rest) throws Dwo2Exception {
      UserState_HR_R_S_SG_U s1 = AnonDomainAuthorizer.build().submitUser(sc)
      .setRealm(rest.getRestContext().getRealm())
      .setHasRole(rest.getRestContext().getDomHasRole());
      PersistentHasRole phr = s1.getHasRole();
      TeacherState_HR_P_R_S_SG_U state = s1
      .buildSchoolAdminTeacher().setTeacher().addProfile(rest.getDomDwoProfile());
      PersistentDwoProfile profile = state.getDwoProfile();
      DomResultsPerTeacher dom = rest.getDomResultsPerTeacher();
      dom.setTeacher(s1.getUser().buildDomTeacher(s1.getRealm()));
      List<PersistentSchoolClass> scl;
      if ( dom.getSchoolClasses() == null) {
        scl = SchoolClassUtilManager.getSchoolClassesOfTeacher(phr);
        dom.setSchoolClasses(scl.stream().map(item -> new DomMapEntry<>(item.buildPersistenceId(), item.buildDomSchoolClass())).collect(Collectors.toList()));
      } else {
        scl = new ArrayList<>();
        dom.getSchoolClasses().forEach(entry -> {
          DomSchoolClassId id = new DomSchoolClassId(entry.getKey());
          try {
            PersistentSchoolClass psc = SchoolClassManager.findEntity(MySQLPersistenceId.getNativeId(id));
            scl.add(psc);
            entry.setValue(psc.buildDomSchoolClass());
          } catch (Dwo2Exception e) {
            entry.setValue(null);
            LOG.log(Level.WARNING, "retrieve sc " + entry.getKey(), e);
          }
        });
      }
      List<PersistentStudentOfClass> studentOfClassList;
      Map<Long,PersistentUser> studentMap = new HashMap<>();
      if ( dom.getStudentsOfClasses() == null) {
        studentOfClassList = 
        scl.stream().flatMap(item -> {
          List<PersistentStudentInClass> cicList = StudentInClassManager.findEntities(item);
          cicList.forEach(cic -> studentMap.put(cic.getUser().getId(), cic.getUser()));
          return cicList.stream().map(PersistentStudentInClass::getStudentOfClass);
        }).collect(Collectors.toList());
        dom.setStudentsOfClasses(studentOfClassList.stream().map(item -> new DomMapEntry<>(item.buildPersistenceId(), item.buildDomStudentOfClass())).collect(Collectors.toList()));
      } else {
        studentOfClassList = new ArrayList<>();
        dom.getStudentsOfClasses().forEach(entry -> {
          DomStudentOfClass id = new DomStudentOfClass(); id.setId(entry.getKey());
          PersistentStudentOfClass psoc = StudentOfClassManager.findEntity(MySQLPersistenceId.getNativeId(id));
          Long userID = psoc.getPersistentStudentOfClassPK().getUserID();
          studentMap.put(userID, UserManager.findEntity(userID));
          studentOfClassList.add(psoc);
          entry.setValue(psoc.buildDomStudentOfClass());
        });
      }
      if ( dom.getStudents() == null) {
        dom.setStudents(studentMap.values()
            .stream().map(item -> new DomMapEntry<PersistenceId,DomStudent>(item.buildPersistenceId(),item.buildDomStudent(s1.getRealm())))
            .collect(Collectors.toList()));
      } else {
        Map<Long,PersistentUser> allStudents = new HashMap<>(studentMap);
        studentMap.clear();;
        dom.getStudents().forEach(entry -> {
          DomStudent id = new DomStudent(); id.setId(entry.getKey());
          try {
            Long pid = MySQLPersistenceId.getNativeId(id);
            PersistentUser u = allStudents.get(pid);
            if (u != null) {
              studentMap.put(pid, u);
              entry.setValue(u.buildDomStudent(s1.getRealm()));
            } else {
              entry.setValue(null);
            }
          } catch (Dwo2Exception e) {
            entry.setValue(null);
          }
        });
      }
      
      Map<Long, PersistentCourse> courseMap;
      courseMap = new HashMap<>();
      dom.setClassCourses(new ArrayList<>());
      scl.forEach(schoolClass -> {
        try {
          List<PersistentCourseInClass> list = CourseInClassManager.findLeaveEntities(schoolClass, profile);
          list.forEach(item -> {
            courseMap.put(item.getCourse().getCourseID(), item.getCourse());
            dom.getClassCourses().add(new DomMapEntry<PersistenceId, DomClassCourse4Teacher>(item.getClassCourse().buildPersistenceId(), item.getClassCourse().buildDomClassCourse4Teacher()));
          });
        } catch (Dwo2Exception e) {
          LOG.log(Level.WARNING, "find classcourse", e);
        }
      });
      
      if (dom.getCourses() == null) {
        dom.setCourses(courseMap.values().stream().map(item-> new DomMapEntry<>(item.buildPersistenceId(), item.buildDomCourse())).collect(Collectors.toList()));
      } else {
          Map<Long, PersistentCourse> copy = new HashMap<>(courseMap);
          courseMap.clear();
          dom.getCourses().forEach(entry -> {
              DomCourse id = new DomCourse(); id.setId(entry.getKey());
              try {
                Long pid = MySQLPersistenceId.getNativeId(id);
                PersistentCourse c = copy.get(pid);
                if (c != null) {
                  entry.setValue(c.buildDomCourse());
                  courseMap.put(pid, c);
                } else {
                  entry.setValue(null);
                }
              } catch (Dwo2Exception e) {
                entry.setValue(null);
              }
           });
          List<DomMapEntry<PersistenceId, DomClassCourse4Teacher>> reduced = dom.getClassCourses()
              .stream()
              .filter(entry -> {
                DomCourse id = new DomCourse(); id.setId(entry.getValue().getCourseId());
                try {
                  return courseMap.containsKey(MySQLPersistenceId.getNativeId(id));
                } catch (Dwo2Exception e) {
                  return false;
                }
              })
              .collect(Collectors.toList());
          dom.setClassCourses(reduced);
      }
      Collection<PersistentScoContext> scos = new ArrayList<>();
      dom.setScoContexts(
          courseMap.values().stream().flatMap((PersistentCourse c) -> ScoContextManager.findEntities(c).stream()
            .map(sco -> {
              scos.add(sco);
              return new DomMapEntry<>(sco.buildPersistenceId(), sco.buildDomScoContext());
              }
            ))
            .collect(Collectors.toList())
      );
      PersistentSchool school = s1.getSchool();
      PersistentSchoolGroup sg = SchoolGroupManager.findEntity(school, RoleType.STUDENT); 
      dom.setStudentScoContexts(
        studentMap.values().stream()
        .flatMap( (PersistentUser user) ->
          {
              PersistentHasRolePK key = new PersistentHasRolePK(user.getId(), sg.getSchoolGroupID());
              return scos.stream().flatMap(scoContext -> {
                Stream<PersistentStudentScoContext> stream = null;
                List<PersistentStudentScoContext> list = StudentScoContextManager.findEntities(scoContext, key);
                if(list != null) stream = list.stream();
                return stream;
              });
          })
          .map(item -> new DomMapEntry<PersistenceId, DomStudentScoContext>(item.buildPersistenceId(), item.buildDomStudentScoContext()))
          .collect(Collectors.toList())
     );
      dom.setFetchTimeStamp(DwoDateUtilities.getCurrentDwoUnixTimeStamp());
      return dom;
    }
    
    
    /**
     * Returns the all the schoolclass/student results of a teacher within a
     * school. This includes the invisible data with ViewState none.
     *
     * @param sc
     * @param aProfile
     * @return
     */
    @PUT
    @Produces({"application/json"})
    @Path("/getTeachersResults")
    public DomResultsPerTeacher getTeachersResults(@Context SecurityContext sc, RestDwoProfile aProfile) throws Dwo2RestException{
        long curTime = DwoDateUtilities.getCurrentDwoUnixTimeStamp();
        DomDwoProfile domProfile = aProfile.getDomDwoProfile();
        DomContext context = aProfile.getRestContext();
        DomHasRole domHasRole = context.getDomHasRole();
        String realm = Realm.of(context);
        //check given role in RestContext
        if (domHasRole == null) {
            throw new Dwo2RestException(Dwo2ExceptionCode.User_IllegalAction, "User " + sc.getUserPrincipal().getName() + "didn't submit a hasRole in his RestContext.");
        } else if (domProfile == null) {
            throw new Dwo2RestException(Dwo2ExceptionCode.User_IllegalAction, "User " + sc.getUserPrincipal().getName() + "didn't submit a domProfile in his RestContext.");
        }
        PersistentHasRole phr = null;
        PersistentSchool school = null;
        final PersistentDwoProfile profile;

        try {
            PersistentHasRolePK hasRoleKey = MySQLPersistenceId.getNativeId(domHasRole);
            phr = HasRoleManager.findEntity(hasRoleKey);
            PersistentUser user = UserManager.findByUserName(sc.getUserPrincipal().getName());
            if (user == null || !user.getId().equals(phr.getPersistentHasRolePK().getUserID())) {
                LOG.log(Level.SEVERE, "Username {0}: ILLEGAL USER-OPERATION: Using uid {1} in HasRole differs from user principal name {0}.", new Object[]{sc.getUserPrincipal().getName(), user.getUsername()});
                throw new Dwo2Exception(Dwo2ExceptionCode.User_IllegalAction, "You Don't Have Permission to access this using usercode " + sc.getUserPrincipal().getName() + ".");
            }
            profile = DwoProfileManager.findEntity(MySQLPersistenceId.getNativeId(domProfile));
            if (profile == null) {
                LOG.log(Level.SEVERE, "Username {0}: ILLEGAL USER-OPERATION: Using unknown profileId {1}.", new Object[]{sc.getUserPrincipal().getName(), domProfile.getId()});
                throw new Dwo2Exception(Dwo2ExceptionCode.User_IllegalAction, "You Don't Have Permission to access this using usercode " + sc.getUserPrincipal().getName() + ".");
            }
        } catch (Dwo2Exception ex) {
            LOG.log(Level.SEVERE, "", ex);
            throw new Dwo2RestException(ex);
        }

        try {
            school = HasRoleUtilManager.getSchoolforHasRole(phr);
        } catch (Dwo2Exception ex) {
            LOG.log(Level.SEVERE, "", ex);
            throw new Dwo2RestException(ex);
        }

        //fetch Profile
        if (phr != null && school != null) {
            long prevTime = curTime;
            curTime = DwoDateUtilities.getCurrentDwoUnixTimeStamp();
            LOG.log(Level.INFO, "security fetch: " + (curTime - prevTime));

            DomResultsPerTeacher results = new DomResultsPerTeacher();
            results.setFetchTimeStamp(DwoDateUtilities.getCurrentDwoUnixTimeStamp());
            // DomResultsPerTeacher requires a fetchTimeStamp, teacher, schoolclasses, 
            // studentsof, students, 
            // classcourses, courses, scocontext's and studentscocontext's.

            //Fetch teacher
            DomTeacher teacher = UserManager.findEntity(phr.getPersistentHasRolePK().getUserID()).buildDomTeacher(realm);
            results.setTeacher(teacher);

            //Collect schoolClasses of the teacher
            List<PersistentSchoolClass> schoolClasses = SchoolClassUtilManager.getSchoolClassesOfTeacher(phr);
            HashMap<PersistenceId, DomSchoolClass> domSchoolClasses = new HashMap<>(schoolClasses.size());
            HashMap<PersistentStudentOfClassPK, PersistentStudentOfClass> socMap = new HashMap<>();
            HashMap<String, PersistentUser> studentMap = new HashMap<>();
            //create the DomSchoolClasses
            schoolClasses.stream().map((schoolClass) -> {
                DomSchoolClass s = schoolClass.buildDomSchoolClass();
                domSchoolClasses.putIfAbsent(s.getId(), s);
                return schoolClass;
            }).forEach((schoolClass) -> {
                List<PersistentStudentInClass> cicList = StudentInClassManager.findEntities(schoolClass);
                for (PersistentStudentInClass sic : cicList) {
                    socMap.putIfAbsent(sic.getStudentOfClass().getPersistentStudentOfClassPK(), sic.getStudentOfClass());
                    studentMap.putIfAbsent(sic.getUser().getId().toString(), sic.getUser());
                }
                
//                
//                //And while at it, for each schoolClass fill the set of studentsOf and students
////                try {
//                for (PersistentStudentOfClass soc : StudentOfClassManager.findLeaveEntities(schoolClass)) {
//                    //add studentOf to set socMap
//                    socMap.putIfAbsent(soc.getPersistentStudentOfClassPK(), soc);
//                    //add user to set studentMap
//                    PersistentUser user = UserManager.findEntity(soc.getPersistentStudentOfClassPK());
//                    studentMap.putIfAbsent(user.getId(), user);
//                }
//                //TODO optimize: remove the multiple user fetches.
////                    for (PersistentUser user : UserUtilManager.getUsersforStudentsInSchoolClass(schoolClass)) {
////                        studentMap.putIfAbsent(user.getId(), user);
////                    }
////                } catch (Dwo2Exception ex) {
////                    Logger.getLogger(SecuredTeacherResultsManager.class.getName()).log(Level.SEVERE, null, ex);
////                }
            });            
            List<DomMapEntry<PersistenceId, DomSchoolClass>> scList = new ArrayList<>(domSchoolClasses.size());
            domSchoolClasses.entrySet().stream().forEach((entry) -> {
                scList.add(new DomMapEntry(entry));
            });
            results.setSchoolClasses(scList);

            //convert studentMap and set in result
            HashMap<PersistenceId, DomStudent> domStudents = new HashMap<>(studentMap.size());
            studentMap.entrySet().stream().forEach((keyValuePair) -> {
                DomStudent s = keyValuePair.getValue().buildDomStudent(realm);
                domStudents.put(s.getId(), s);
            });
            List<DomMapEntry<PersistenceId, DomStudent>> entryList = new ArrayList<>(domStudents.size());
            domStudents.entrySet().stream().forEach((entry) -> {
                entryList.add(new DomMapEntry<PersistenceId, DomStudent>(entry));
            });
            results.setStudents(entryList);
            //convert StudentOfClass map (socMap) and set in result
            HashMap<PersistenceId, DomStudentOfClass> domSocs = new HashMap<>(socMap.size());
            Set<PersistentHasRolePK> studentHasRoleSet = new HashSet<>();
            socMap.entrySet().stream().forEach((keyValuePair) -> {
                DomStudentOfClass s = keyValuePair.getValue().buildDomStudentOfClass();
                domSocs.putIfAbsent(s.getId(), s);
                PersistentHasRolePK key = new PersistentHasRolePK(keyValuePair.getKey());
                studentHasRoleSet.add(key);

            });
            List<DomMapEntry<PersistenceId, DomStudentOfClass>> socsList = new ArrayList<>(domSocs.size());
            domSocs.entrySet().stream().forEach((entry) -> {
                socsList.add(new DomMapEntry(entry));
            });
            results.setStudentsOfClasses(socsList);

            prevTime = curTime;
            curTime = DwoDateUtilities.getCurrentDwoUnixTimeStamp();
            LOG.log(Level.INFO, "student in class fetch and convert: " + (curTime - prevTime));
            
            //Fetch courses for all classes ClassCourses. No filtering occurs on
            //CourseType, notBefore and notAfter for results. Filtering of Courses
            //occurs on Profile.
            HashMap<PersistenceId, PersistentClassCourse> classCoursesMap = new HashMap<>();
            HashMap<PersistenceId, PersistentCourse> coursesMap = new HashMap<>();
            schoolClasses.stream().forEach((schoolClass) -> {
                List<PersistentCourseInClass> cicList;
                try {
                    cicList = CourseInClassManager.findLeaveEntities(schoolClass, profile);
                } catch (Dwo2Exception ex) {
                    Logger.getLogger(SecuredTeacherResultsManager.class.getName()).log(Level.SEVERE, null, ex);
                    throw new Dwo2RestException(ex);
                }
                for (PersistentCourseInClass cic : cicList) {
                    classCoursesMap.putIfAbsent(cic.getClassCourse().buildPersistenceId(), cic.getClassCourse());
                    coursesMap.putIfAbsent(cic.getCourse().buildPersistenceId(), cic.getCourse());
                }
            });

            //fill DomClassCourse4Teacher List
            HashMap<PersistenceId, DomClassCourse4Teacher> domClassCourses = new HashMap<>(classCoursesMap.size());
            classCoursesMap.entrySet().forEach((keyValuePair) -> {
                DomClassCourse4Teacher c = keyValuePair.getValue().buildDomClassCourse4Teacher();
                domClassCourses.put(c.getId(), c);
            });
            List<DomMapEntry<PersistenceId, DomClassCourse4Teacher>> dccList = new ArrayList<>(domClassCourses.size());
            domClassCourses.entrySet().stream().forEach((entry) -> {
                dccList.add(new DomMapEntry(entry));
            });

            results.setClassCourses(dccList);

            HashMap<PersistenceId, DomCourse> domCourses = new HashMap<>();
            Map<PersistenceId, PersistentCourse> courses = new HashMap<>();
            Queue<PersistentCourse> courseQueue = new LinkedList<>();
            Map<PersistenceId, PersistentCourse> leaves = new HashMap<>();
            courseQueue.addAll(coursesMap.values());//note this is a set!

            //Danger Will Robinson, circular reference will hang thread forever.
            //hence map and queue approach and a Depth First Search queue approach. 
            //Trying to get the queue empty.
            while (!courseQueue.isEmpty()) {
                PersistentCourse course = courseQueue.remove();
                PersistentCourse r = courses.putIfAbsent(course.buildPersistenceId(), course);
                if (r == null && course.isWithChildren()) {
                    //put current course in the courseMap
                    //put kids on the queue
                    List<PersistentCourse> childrenCourses = CourseManager.findChildrenOf(profile, course);
                    //add courses to a map 
                    //if not in map add to queue
                    courseQueue.addAll(childrenCourses);
                } else {//course is aleave
                    leaves.putIfAbsent(course.buildPersistenceId(), course);
                }
            }
            //export courses to result.
            courses.entrySet().stream().forEach((keyValuePair) -> {
                DomCourse c = keyValuePair.getValue().buildDomCourse();
                domCourses.putIfAbsent(c.getId(), c);
            });

            List<DomMapEntry<PersistenceId, DomCourse>> dcList = new ArrayList<>(domCourses.size());
            domCourses.entrySet().stream().forEach((entry) -> {
                dcList.add(new DomMapEntry(entry));
            });
            results.setCourses(dcList);

            prevTime = curTime;
            curTime = DwoDateUtilities.getCurrentDwoUnixTimeStamp();
            LOG.log(Level.INFO, "course in class fetch and convert: " + (curTime - prevTime));
            
            //process leaves and fill hashmap scoContext
            HashMap<PersistenceId, PersistentScoContext> scosMap = new HashMap<>();
            leaves.entrySet().stream().forEach((keyValuePair) -> {
                List<PersistentScoContext> scoContexts = ScoContextManager.findEntities(keyValuePair.getValue());
                scoContexts.forEach((scoContext) -> {
                    scosMap.putIfAbsent(scoContext.buildPersistenceId(), scoContext);
                });
            });
            HashMap<PersistenceId, DomScoContext> domScoContexts = new HashMap<>(scosMap.size());
            scosMap.entrySet().stream().forEach((keyValuePair) -> {
                DomScoContext s = keyValuePair.getValue().buildDomScoContext();
                domScoContexts.put(s.getId(), s);
            });

            List<DomMapEntry<PersistenceId, DomScoContext>> dscList = new ArrayList<>(domScoContexts.size());
            domScoContexts.entrySet().stream().forEach((entry) -> {
                dscList.add(new DomMapEntry(entry));
            });
            results.setScoContexts(dscList);

            prevTime = curTime;
            curTime = DwoDateUtilities.getCurrentDwoUnixTimeStamp();
            LOG.log(Level.INFO, "scocontext fetch and convert: " + (curTime - prevTime));

            long sgId = SchoolGroupManager.findEntity(school, RoleType.STUDENT).getSchoolGroupID();
            //fill hashmap studentSco for each student x sco
            HashMap<PersistenceId, PersistentStudentScoContext> studentScosMap = new HashMap<>();
            scosMap.entrySet().forEach((sco) -> {
                List<PersistentStudentScoContext> studentScos = StudentScoContextManager.findEntities(sco.getValue(),sgId);
                studentScos.forEach((studentSco) -> {
                    //note each studentsco must be a student in a school class. Hence it must exist in the student list.
                    //quick and hasty fetch.
                    
                    if(studentMap.containsKey(studentSco.getPersistentHasRolePK().getUserID().toString())){
                    studentScosMap.putIfAbsent(studentSco.buildPersistenceId(), studentSco);
                    }
                });
            });
//            HashMap<Long, PersistentStudentScoContext> studentScosMap = new HashMap<>();
//            schoolClasses.forEach((cc) -> { 
// //           classCoursesMap.values().forEach((cc) -> {
//                List<PersistentStudentScoContext> ssList = StudentScoInClassManager.findEntities(cc);
//                ssList.forEach((ss) -> {studentScosMap.putIfAbsent(ss.getStudentSco(), ss);}) ;
//            });
            
//            for (PersistentScoContext sco : scosMap.values()) {
//                for (PersistentHasRolePK hasRoleKey : studentHasRoleSet) {
//                    //TODO optimize
//                    List<PersistentStudentScoContext> studentScos = StudentScoContextManager.findLeaveEntities(sco, hasRoleKey);
//                    studentScos.forEach((studentSco) -> {
//                        studentScosMap.putIfAbsent(studentSco.getStudentSco(), studentSco);
//                    });
//                }
//            }

            HashMap<PersistenceId, DomStudentScoContext> domStudentScoContexts = new HashMap<>(studentScosMap.size());
            studentScosMap.entrySet().stream().forEach((keyValuePair) -> {
                DomStudentScoContext s = keyValuePair.getValue().buildDomStudentScoContext();
                domStudentScoContexts.put(s.getId(), s);
            });

            List<DomMapEntry<PersistenceId, DomStudentScoContext>> sscList = new ArrayList<>(domStudentScoContexts.size());
            domStudentScoContexts.entrySet().stream().forEach((entry) -> {
                sscList.add(new DomMapEntry(entry));
            });

            results.setStudentScoContexts(sscList);
            prevTime = curTime;
            curTime = DwoDateUtilities.getCurrentDwoUnixTimeStamp();
            LOG.log(Level.INFO, "studentscocontext fetch and convert: " + (curTime - prevTime));

            return results;
            // recurse here using Java queue
        } else {
            LOG.log(Level.WARNING, "Username {0}: ILLEGAL USER-OPERATION: Trying to access teacher functionality by user with usercode {0}.", new Object[]{sc.getUserPrincipal().getName()});
            throw new Dwo2RestException(Dwo2ExceptionCode.User_IllegalAction, "You Don't Have Permission to access this using usercode " + sc.getUserPrincipal().getName() + ".");
        }
    }

    /**
     *
     * @param sc
     * @param rest
     * @return
     */
    @PUT
    @Produces({"application/json"})
    @Path("/clearStudentResults")
    public Boolean clearStudentResults(@Context SecurityContext sc, RestClearStudentDataForScoAndClass rest) throws Dwo2RestException {
        //clear results, FIXME classcourse wordt niet gedeleted, issue in DWOJCLIENT (Wim)
        try {
            @SuppressWarnings("deprecation")
            CascadingPersistenceBuilder.State_C_CC_HR_P_R_S_SC_SCO_SG_U build = CascadingPersistenceBuilder.user(sc.getUserPrincipal().getName())
                    .addHasRoleIfType(rest.getRestContext().getDomHasRole(), RoleType.TEACHER)
                    .addSchoolClass(rest.getClearStudentDataForScoAndClass().getDomSchoolClass())
                    .addProfile(rest.getClearStudentDataForScoAndClass().getDomProfile())
                    .addScoContext(rest.getClearStudentDataForScoAndClass().getDomScoContext());
            boolean result = build.removeStudentScoWithClassCourse();
            //TODO clear all excess classcourses.
            
            PersistentClassCourse cc = build.getClassCourse();
            if(cc != null && cc.getViewState() == ViewState.invisible) {
              // check for studentscocontexts in sco_of_course X student_of_class
              PersistentCourse pc = build.getCourse();
              List<PersistentScoContext> scos = ScoContextManager.findEntities(pc);
              scos.addAll(ScoContextManager.findTrashedEntities(pc));
              List<PersistentStudentOfClass> students = StudentOfClassManager.findEntities(build.getSchoolClass());
              if( ! students.isEmpty() && scos.size() > 0 ) { // kan >1 zijn, als de bovenstaande clear goed z'n best doet.
                long sgId = students.get(0).getPersistentStudentOfClassPK().getSchoolGroupID().longValue();
                Set<Long> users = students.stream().map(s -> s.getPersistentStudentOfClassPK().getUserID()).collect(Collectors.toSet());
                for ( PersistentScoContext scoContext: scos) {
// Bulk: all students results of a school
                  List<PersistentStudentScoContext> ss = StudentScoContextManager.findEntities(scoContext, sgId);
                  boolean match = ss.stream().anyMatch(pss -> 
                      {
                        Long uid = pss.getPersistentHasRolePK().getUserID();
                        return users.contains(uid);
                      }
                      
                      );
                  
                  if (match) return false;
                }
                try {
                  ClassCourseManager.destroy(cc.getClassCourseID());
                } catch (PersistenceException e) {
                  LOG.log(Level.WARNING, "destroy classcourse " + cc.getClassCourseID(), e);
                  return false;
                }
                return true;
              }
            }
            
        } catch (Dwo2Exception e) {
            throw new Dwo2RestException(e);
        }
        return false;
    }
    
    
    @PUT
    @Produces({"application/json"})
    @Path("/createStudentResults")
    public DomResultsPerTeacher createStudentResults(@Context SecurityContext sc, RestClearStudentDataForScoAndClass rest) throws Dwo2Exception {
      TeacherState_HR_R_S_SG_U state = AnonDomainAuthorizer.build().submitUser(sc)
          .setHasRole(rest.getRestContext().getDomHasRole()).buildSchoolAdminTeacher().setTeacher();

      DomClearStudentDataForScoAndClass dom = rest.getClearStudentDataForScoAndClass();
      TeacherState_C_CC_HR_P_R_S_SC_SCO_SG_U call = state.addProfile(dom.getDomProfile()).addSchoolClass(dom.getDomSchoolClass()).addScoContext(dom.getDomScoContext());
      PersistentSchool school = call.getSchool();
      PersistentSchoolGroup studentRole = SchoolGroupManager.findBySchoolAndRole(school, RoleType.STUDENT);
      PersistentSchoolClass schoolClass = call.getSchoolClass();
  
      List<DomStudent> students = dom.getDomStudentList();
     List<DomMapEntry<PersistenceId, DomStudentScoContext>> studentScoContexts = new ArrayList<>();
     PersistentScoContext scoContext = call.getScoContext();
     for(DomStudent student: students) {
       Long sid = MySQLPersistenceId.getNativeId(student);
// verify student member of class class
       PersistentStudentOfClassPK spk = new PersistentStudentOfClassPK(sid, schoolClass.getClassID(), studentRole.getSchoolGroupID());
       if ( StudentOfClassManager.findEntity(spk) == null) continue;
             
       PersistentHasRolePK pk = new PersistentHasRolePK(spk);
       List<PersistentStudentScoContext> sscList = StudentScoContextManager.findEntities(scoContext, pk);
       DomStudentScoContext dssc;
       if(sscList.isEmpty()) {
         PersistentStudentScoContext ssc = new PersistentStudentScoContext();
         ssc.setScoID(scoContext.getScoID());
         ssc.setPersistentHasRolePK(pk);
         long now = System.currentTimeMillis();
         ssc.setCreateDate(new java.sql.Date(now));
         ssc.setCreateTime(new java.sql.Time(now));
         ssc.setCompletionStatus("not attempted");
         ssc.setLocation("");
         ssc.setScore(0);
         ssc.setSessionTime("");
         StudentScoContextManager.create(ssc);
         dssc = ssc.buildDomStudentScoContext();
       } else {
         dssc = sscList.get(0).buildDomStudentScoContext();     
       }
       studentScoContexts.add(new DomMapEntry<PersistenceId, DomStudentScoContext>(dssc.getId(), dssc));
     }
     
      DomResultsPerTeacher result = new DomResultsPerTeacher();
      final PersistentClassCourse cc = call.getClassCourse();
      if (cc != null) {
        DomClassCourse4Teacher classCourse = cc.buildDomClassCourse4Teacher();
        result.setClassCourses(Collections.singletonList(new DomMapEntry<PersistenceId, DomClassCourse4Teacher>(classCourse.getId(), classCourse)));
      } else {
        result.setClassCourses(Collections.emptyList());
      }
      DomCourse course = call.getCourse().buildDomCourse();
      result.setCourses(Collections.singletonList(new DomMapEntry<PersistenceId, DomCourse>(course.getId(), course)));
      
      DomScoContext sco = call.getScoContext().buildDomScoContext();
      result.setScoContexts(Collections.singletonList(new DomMapEntry<PersistenceId, DomScoContext>(sco.getId(), sco)));
      result.setStudentScoContexts(studentScoContexts);
      result.setFetchTimeStamp(System.currentTimeMillis());
      return result;
    }

    @PUT
    @Produces({"application/json"})
    @Path("/createStudentResultsv2")
    @RolesAllowed("TEACHER")
    public DomResultsPerTeacherv2 createStudentResultsv2(@Context SecurityContext sc, RestClearStudentDataForScoAndClass rest) throws Dwo2Exception {
      TeacherState_HR_R_S_SG_U state = AnonDomainAuthorizer.build().submitUser(sc)
          .setHasRole(rest.getRestContext().getDomHasRole()).buildSchoolAdminTeacher().setTeacher();

      DomClearStudentDataForScoAndClass dom = rest.getClearStudentDataForScoAndClass();
      TeacherState_C_CC_HR_P_R_S_SC_SCO_SG_U call = state.addProfile(dom.getDomProfile()).addSchoolClass(dom.getDomSchoolClass()).addScoContext(dom.getDomScoContext());
      PersistentSchool school = call.getSchool();
      PersistentSchoolGroup studentRole = SchoolGroupManager.findBySchoolAndRole(school, RoleType.STUDENT);
      PersistentSchoolClass schoolClass = call.getSchoolClass();
  
     List<DomStudent> students = dom.getDomStudentList();
     List<DomStudentScoContext> studentScoContexts = new ArrayList<>();
     PersistentScoContext scoContext = call.getScoContext();
     // scopages
	  	List<DomStudentScoPage> collect = ScoPageManager.find(scoContext).stream()
	  				.map(page -> buildDomScoPage(page, null))
	  				.collect(Collectors.toList());
     for(DomStudent student: students) {
       Long sid = MySQLPersistenceId.getNativeId(student);
// verify student member of class class
       PersistentStudentOfClassPK spk = new PersistentStudentOfClassPK(sid, schoolClass.getClassID(), studentRole.getSchoolGroupID());
       if ( StudentOfClassManager.findEntity(spk) == null) continue;
             
       PersistentHasRolePK pk = new PersistentHasRolePK(spk);
       List<PersistentStudentScoContext> sscList = StudentScoContextManager.findEntities(scoContext, pk);
       DomStudentScoContext dssc;
       if(sscList.isEmpty()) {
         PersistentStudentScoContext ssc = new PersistentStudentScoContext();
         ssc.setScoID(scoContext.getScoID());
         ssc.setPersistentHasRolePK(pk);
         long now = System.currentTimeMillis();
         ssc.setCreateDate(new java.sql.Date(now));
         ssc.setCreateTime(new java.sql.Time(now));
         ssc.setCompletionStatus("not attempted");
         ssc.setLocation("");
         ssc.setScore(0);
         ssc.setSessionTime("");
         StudentScoContextManager.create(ssc);
         dssc = ssc.buildDomStudentScoContext();
       } else {
         PersistentStudentScoContext ssc = sscList.get(0);
         dssc = ssc.buildDomStudentScoContext();     
         if (!collect.isEmpty()) {
     		List<DomStudentScoPage> studentpages = ScoPageManager.find(ssc).stream()
     				.map (item -> buildDomScoPage(item, ssc))
     				.collect(Collectors.toList());
     		collect.addAll(studentpages);
         }
       
       
       }
       studentScoContexts.add(dssc);
     }
     
      DomResultsPerTeacherv2 result = new DomResultsPerTeacherv2();
      final PersistentClassCourse cc = call.getClassCourse();
      if (cc != null) {
        DomClassCourse4Teacher classCourse = cc.buildDomClassCourse4Teacher();
        result.setClassCourses(Collections.singletonList(classCourse));
      } else {
        result.setClassCourses(Collections.emptyList());
      }
      DomCourse course = call.getCourse().buildDomCourse();
      result.setCourses(Collections.singletonList(course));
      
      DomScoContext sco = call.getScoContext().buildDomScoContext();
      result.setScoContexts(Collections.singletonList(sco));
      result.setStudentScoContexts(studentScoContexts);
   // scopages
		result.setStudentScoPages(collect);
   // scostudentpages
      result.setFetchTimeStamp(System.currentTimeMillis());
      return result;
    }


	private DomStudentScoPage buildDomScoPage(PersistentScoPage page, PersistentStudentScoContext ssc) {
		DomStudentScoPage s = new DomStudentScoPage();
		s.setCorrectie(page.getCorrectie());
		s.setDocentCorrectie(page.getCheckDocent());
		s.setMaxScore(page.getMaxScore());
		s.setScoID(PersistentScoContext.buildPersistenceId(page.getId().getScoID()));
		s.setScore(page.getScore());
		s.setSequencenr(page.getId().getSequencenr());
		PersistentHasRolePK pk = page.getId().getHasRolePK();
		if (pk != null)
		{
			s.setUserID(PersistentUser.buildPersistenceId(pk.getUserID()));
			s.setSchoolGroupID(PersistentSchoolGroup.buildPersistenceId(pk.getSchoolGroupID()));
			s.setId(ssc.buildPersistenceId());
		} else {
			s.setId(s.getScoID());
		}
		return s;
	}
}
