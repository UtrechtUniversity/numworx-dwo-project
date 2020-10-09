package fi.dwo.server.rest;

import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.exceptions.Dwo2ExceptionCode;
import nl.uu.fi.dwo.rest.exceptions.Dwo2RestException;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;
import fi.dwo.commons.persistence.MySQLPersistenceId;
import fi.dwo.commons.persistence.entities.PersistentApplet;
import fi.dwo.commons.persistence.entities.PersistentCourse;
import fi.dwo.commons.persistence.entities.PersistentImage;
import fi.dwo.commons.persistence.entities.PersistentScoContext;
import fi.dwo.commons.persistence.entities.PersistentScoData;
import fi.dwo.commons.persistence.entities.PersistentStudentModelData;
import fi.dwo.commons.persistence.entities.PersistentStudentScoContext;
import fi.dwo.server.PersistentDataManagers.access.AnonDomainAuthorizer;
import fi.dwo.server.PersistentDataManagers.access.AnonDomainAuthorizer.AnonState;
import fi.dwo.server.PersistentDataManagers.access.SchoolAdminTeacherDomainAuthorizer.SchoolAdminTeacherState_HR_R_S_SG_U;
import fi.dwo.server.PersistentDataManagers.access.UserDomainAuthorizer.UserState_HR_R_S_SG_U;
import fi.dwo.server.PersistentDataManagers.access.UserDomainAuthorizer.UserState_U;
import fi.dwo.server.PersistentDataManagers.actions.MySQLScoContextActions;
import fi.dwo.server.PersistentDataManagers.core.AppletManager;
import fi.dwo.server.PersistentDataManagers.core.CourseManager;
import fi.dwo.server.PersistentDataManagers.core.ImageManager;
import fi.dwo.server.PersistentDataManagers.core.ScoContextManager;
import fi.dwo.server.PersistentDataManagers.core.ScoDataManager;
import fi.dwo.server.PersistentDataManagers.core.StudentModelDataManager;
import fi.dwo.server.PersistentDataManagers.core.StudentScoContextManager;
import fi.dwo.server.PersistentDataManagers.core.StudentScoDataManager;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.security.PermitAll;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceException;
import javax.persistence.RollbackException;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;

import nl.uu.fi.dwo.rest.dom.entities.DomAppletId;
import nl.uu.fi.dwo.rest.dom.entities.DomCourse;
import nl.uu.fi.dwo.rest.dom.entities.DomScoContext;
import nl.uu.fi.dwo.rest.dom.entities.DomScoContextFull;
import nl.uu.fi.dwo.rest.dom.entities.DomScoContextId;
import nl.uu.fi.dwo.rest.dom.entities.DomScoData;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContextId;
import nl.uu.fi.dwo.rest.dom.entities.util.ScoType;
import nl.uu.fi.dwo.rest.entities.RestScoContext;
import nl.uu.fi.dwo.rest.entities.RestScoContextFull;

/**
 * Operations for the GUI Component that manages the school classes.
 *
 * @see fi.dwo.dwojapplet.gui.panels.JPanel.MyProfile
 *
 * @author G.A.J. van der Plas
 */
@PermitAll
@Path("/secure/teacher/scoContext")
public class SecuredTeacherScoContextManager extends AbstractSchoolClassManager {

    private static final Logger LOG = Logger.getLogger(SecuredTeacherScoContextManager.class.getName());

    @PUT
    @Path("remove")
    @Produces({"application/json"})
    public Boolean remove(@Context SecurityContext sc, RestScoContext rest) throws Dwo2Exception {
        SchoolAdminTeacherState_HR_R_S_SG_U state = AnonDomainAuthorizer.build()
        .submitUser(sc.getUserPrincipal().getName()).setHasRole(rest.getRestContext().getDomHasRole())
        .buildSchoolAdminTeacher();
        // TODO state.addProfile().addScoContext().removeSco();
        
        DomScoContext scoContext = rest.getDomScoContext();
        Long scoID = MySQLPersistenceId.getNativeId(scoContext);
        PersistentScoContext pc = ScoContextManager.findEntity(scoID);
        if(pc == null) return Boolean.FALSE;
        PersistentCourse c = CourseManager.findEntity(pc.getCourseID());
        MySQLScoContextActions.remove(pc, c);
        return Boolean.TRUE;
    }

    @PUT
    @Path("trash")
    @Produces({"application/json"})
    public Boolean trash(@Context SecurityContext sc, RestScoContext rest) throws Dwo2Exception {
        SchoolAdminTeacherState_HR_R_S_SG_U state = AnonDomainAuthorizer.build()
        .submitUser(sc.getUserPrincipal().getName()).setHasRole(rest.getRestContext().getDomHasRole())
        .buildSchoolAdminTeacher();
        // TODO state.addProfile().addScoContext().removeSco();
        
        DomScoContext scoContext = rest.getDomScoContext();
        Long scoID = MySQLPersistenceId.getNativeId(scoContext);
        PersistentScoContext pc = ScoContextManager.findEntity(scoID);
        if(pc == null) return Boolean.FALSE;
        PersistentCourse c = CourseManager.findEntity(pc.getCourseID());
        MySQLScoContextActions.trash(pc, c);
        return Boolean.TRUE;
    }

    @PUT
    @Path("update")
    @Produces({"application/json"})
    public DomScoContextFull update(@Context SecurityContext sc, RestScoContextFull rest) throws Dwo2Exception {
      AnonState s0 = AnonDomainAuthorizer.build();
      UserState_U s1 = s0.submitUser(sc.getUserPrincipal().getName());
      UserState_HR_R_S_SG_U s2 = s1.setHasRole(rest.getRestContext().getDomHasRole());
      SchoolAdminTeacherState_HR_R_S_SG_U state = s2.buildSchoolAdminTeacher();
      
      DomScoContextFull scoContext = rest.getDomScoContext();
      DomScoData scoData = rest.getDomScoData();
      Long scoID = MySQLPersistenceId.getNativeId(scoContext);
      PersistentScoContext pc = ScoContextManager.findEntity(scoID);
      PersistentScoData sd = ScoDataManager.findEntity(scoID);
      return MySQLScoContextActions.update(pc, sd, scoContext, scoData, true);
    }
      
    @PUT
    @Path("countStudents")
    @Produces({"application/json"})
    public int countStudents(@Context SecurityContext sc, RestScoContext rest) throws Dwo2Exception {
        AnonState s0 = AnonDomainAuthorizer.build();
        UserState_U s1 = s0.submitUser(sc.getUserPrincipal().getName());
        UserState_HR_R_S_SG_U s2 = s1.setHasRole(rest.getRestContext().getDomHasRole());
        SchoolAdminTeacherState_HR_R_S_SG_U state = s2.buildSchoolAdminTeacher();
        DomScoContextId sco = rest.getDomScoContext();
    	return state.countStudents(sco);
    }
    
    public DomScoContextFull updateOLD(@Context SecurityContext sc, RestScoContextFull rest) {
		DomScoContextFull scoContext = rest.getDomScoContext();
		DomScoData    	  scoData = rest.getDomScoData();
    	try {
// Security...
			Long scoID = MySQLPersistenceId.getNativeId(scoContext);
			PersistentScoContext pc = ScoContextManager.findEntity(scoID);
			PersistentScoData sd = ScoDataManager.findEntity(scoID);
// editable fields?
			if(scoContext.getScoName() != null) 
				pc.setSconame(scoContext.getScoName());
			if (scoContext.getShowScore() != null) {
				pc.setShowscore(scoContext.getShowScore());
			}
			if(scoContext.getImageData()!=null) 
			{	
				byte[] data = (scoContext.getImageData());
				PersistentImage image = ImageManager.findEntity(scoID);
				if(image == null)
				{
					image = new PersistentImage(scoID);
					image.setImage(data);
					ImageManager.create(image);
				} else {
					image.setImage(data);
					image = ImageManager.edit(image);
				}
				scoContext.setImageData(null);
			} else if (scoContext.getUrnId() != null) {
				PersistenceId id = scoContext.getUrnId();
				switch (id.getType()) {
				case PersistentScoContext:
					if (id.equals(scoContext.getId()))
						break;
					// TODO Zie "add"
				default: 
					LOG.log(Level.SEVERE, scoContext.getId() + ": unsupported " + id);	
					throw new Dwo2Exception(Dwo2ExceptionCode.User_IllegalAction, "unsupported " + id);

				}
			}
			if(scoContext.getScoType() != null) {
				pc.setScoType(scoContext.getScoType());
			}
			if (scoContext.getStudentModelContext() != null) {
			  Long model = MySQLPersistenceId.getNativeId(new DomStudentModelContextId(scoContext.getStudentModelContext()));
			  pc.setModelID(model);
			}
			if (scoContext.getSequencenr() != null) {
				pc.setSequencenr(scoContext.getSequencenr());
			}
			
			if (scoContext.getCourseId() != null) {
				DomCourse dc = new DomCourse(); dc.setId(scoContext.getCourseId());
				Long courseID = MySQLPersistenceId.getNativeId(dc);
				pc.setCourseID(courseID);
			}
			
			pc=ScoContextManager.edit(pc);
// if edit fails skip this.
			if(scoContext.getDescription() != null) {
				sd.setDescription(scoContext.getDescription());
				sd = ScoDataManager.edit(sd);
			}

			if(scoData != null) {
				if(scoData.getLaunchdata() != null) 
					sd.setLaunchdata(scoData.getLaunchdata());
				if(scoData.getLaunchdatabytes() != null) 
					sd.setLaunchdatabytes(scoData.getLaunchdatabytes());
// Destroy all studentSco's			
				List<PersistentStudentScoContext> list = StudentScoContextManager.findEntities(pc);
				for(PersistentStudentScoContext ssc:list) {
                  try {
                    StudentScoDataManager.destroy(ssc.getStudentSco()); //  non-fatal. studentscodata
                  } catch (EntityNotFoundException e1) {}
                  try {
                    StudentScoContextManager.destroy(ssc.getStudentSco());
                  } catch (EntityNotFoundException e) {}
				}				
// Destroy all studentModels of that sco.
				List<PersistentStudentModelData> list2 = StudentModelDataManager.findEntity(pc);
				for(PersistentStudentModelData item: list2) {
					StudentModelDataManager.destroy(item.getModelDataId());
				}
				sd = ScoDataManager.edit(sd);		
			}

			
			
			pc.fillDomScoContextFull(scoContext);
			sd.fillDomScoContextFull(scoContext);
			
		} catch (RollbackException e)  {
			throw new Dwo2RestException(Dwo2ExceptionCode.Rest_ScoNameExists, e.getMessage());
		} catch (Dwo2Exception e) {
			throw new Dwo2RestException(e);
		} catch (PersistenceException e) {
			LOG.log(Level.SEVERE, "", e);
			throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, e.getMessage());
		} catch (Exception e) {
			LOG.log(Level.SEVERE, "", e);
			throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, e.getMessage());
		}
    	
    	return scoContext;
    }
    
    @PUT
    @Path("add")
    @Produces({"application/json"})
    public DomScoContextFull add(@Context SecurityContext sc, RestScoContextFull rest) {
		try {
			DomScoContextFull scoContext = rest.getDomScoContext();
			PersistentScoContext pc = new PersistentScoContext();
			DomAppletId applet = new DomAppletId(scoContext.getAppletId());
			Long appletID = MySQLPersistenceId.getNativeId(applet);
			PersistentApplet a = AppletManager.findEntity(appletID); assert a != null;
			pc.setAppletID(appletID);
			DomCourse course = new DomCourse();course.setId(scoContext.getCourseId());
			Long courseID = MySQLPersistenceId.getNativeId(course);
			PersistentCourse c = CourseManager.findEntity(courseID); assert c != null;
// assert school of course = school of user, 
// assert profile of rest = profile of course.
			pc.setCourseID(courseID);
			String sconame = scoContext.getScoName(); assert sconame != null && ! sconame.isEmpty();
			pc.setSconame(sconame);
			Long sequencenr = scoContext.getSequencenr(); if( sequencenr == null) {
				sequencenr = Long.valueOf( ScoContextManager.findEntities(c).size() );
			}
			pc.setSequencenr(sequencenr);
			ScoType scoType = scoContext.getScoType(); if(scoType == null) scoType = ScoType.OEFENEN; // NotNull!
			pc.setScoType(scoType);
			Boolean showscore = scoContext.getShowScore(); if(showscore == null) showscore = Boolean.TRUE; // notnull?
			pc.setShowscore(showscore);
			pc.setUrnID(null); // XXX als images in UrnResource staan.
// fill schoolid and profile id
			pc.setSchoolID(c.getSchoolID());
			pc.setDwoProfileID(c.getDwoProfileID());
// fill reference to model
			if (scoContext.getStudentModelContext() != null) {
              Long model = MySQLPersistenceId.getNativeId(new DomStudentModelContextId(scoContext.getStudentModelContext()));
              pc.setModelID(model);
			}
			ScoContextManager.create(pc);
			if ( ImageManager.findEntity(pc.getScoID())!= null)
			  try {
			    ImageManager.destroy(pc.getScoID());
			  } catch (Exception e) {
			    LOG.log(Level.WARNING, "destroy destroyed image" + e);			    
			  }
			PersistentScoData sd = new PersistentScoData(pc.getScoID(), scoContext.getDescription());
			if( sd.getDescription() == null) sd.setDescription("");
			if(rest.getDomScoData() != null) {
				DomScoData data = rest.getDomScoData();
				sd.setLaunchdata(data.getLaunchdata());
				sd.setLaunchdatabytes(data.getLaunchdatabytes());
			}
			ScoDataManager.create(sd);
			pc.fillDomScoContextFull(scoContext);
			sd.fillDomScoContextFull(scoContext);
			if(scoContext.getImageData() != null) {
				PersistentImage image = new PersistentImage(pc.getScoID(), scoContext.getImageData());
				ImageManager.create(image);
				scoContext.setImageData(null);
				scoContext.setUrnId(scoContext.getId());
			} else if ( scoContext.getUrnId()!= null ) {
				PersistenceId id = scoContext.getUrnId();
				switch(id.getType()) {
				case PersistentScoContext: 
					DomScoContextId scid = new DomScoContextId();
					scid.setId(id);
					Long imageid = MySQLPersistenceId.getNativeId(scid);
					PersistentImage img = ImageManager.findEntity(imageid);
					if(img != null) {
					    img = new PersistentImage(pc.getScoID(), img.getImage()); // need a fresh copy
						ImageManager.create(img);
						scoContext.setUrnId(scoContext.getId());
					} else {
						scoContext.setUrnId(null);
					}
					break;
				default: throw new Dwo2Exception(Dwo2ExceptionCode.User_IllegalAction, "unsupported " + id);
				}
			}
			
			return scoContext;
		} catch (RollbackException e)  {
			LOG.log(Level.WARNING, rest.getDomScoContext().getScoName(), e);
			throw new Dwo2RestException(Dwo2ExceptionCode.Rest_ScoNameExists, rest.getDomScoContext().getScoName());
		} catch (Dwo2Exception e) {
			throw new Dwo2RestException(e);
		} catch (PersistenceException e) {
			LOG.log(Level.SEVERE, rest.getDomScoContext().getScoName(), e);
			throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, rest.getDomScoContext().getScoName());
		} catch (Exception e) {
			LOG.log(Level.SEVERE, rest.getDomScoContext().getScoName(), e);
			throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, rest.getDomScoContext().getScoName());
		}
    }
    
// needs get, update, delete, etc...
    
    
//    /**
//     * Returns the school data to be displayed. note
//     *
//     * @param sc
//     * @param aProfile
//     * @return
//     */
//    @PUT
//    @Produces({"application/json"})
//    @Path("/getSchoolCourses")
//    public DomSchoolCourses getTeachersResults(@Context SecurityContext sc, RestDwoProfile aProfile) {
//
//        DomDwoProfile domProfile = aProfile.getDomDwoProfile();
//        DomContext context = aProfile.getRestContext();
//        DomHasRole domHasRole = context.getDomHasRole();
//
//        //check given role in RestContext
//        if (domHasRole == null) {
//            throw new Dwo2RestException(Dwo2ExceptionCode.User_IllegalAction, "User " + sc.getUserPrincipal().getName() + "didn't submit a hasRole in his RestContext.");
//        } else if (domProfile == null) {
//            throw new Dwo2RestException(Dwo2ExceptionCode.User_IllegalAction, "User " + sc.getUserPrincipal().getName() + "didn't submit a domProfile in his RestContext.");
//        }
//        PersistentHasRole phr = null;
//        PersistentSchool school = null;
//        final PersistentDwoProfile profile;
//
//        try {
//            PersistentHasRolePK hasRoleKey = MySQLPersistenceId.getNativeId(domHasRole);
//            phr = HasRoleManager.findEntity(hasRoleKey);
//            PersistentUser user = UserManager.findByUserName(sc.getUserPrincipal().getName());
//            if (user == null || !user.getId().equals(phr.getPersistentHasRolePK().getUserID())) {
//                LOG.log(Level.SEVERE, "Username {0}: ILLEGAL USER-OPERATION: Using uid {1} in HasRole differs from user principal name {0}.", new Object[]{sc.getUserPrincipal().getName(), user.getUsername()});
//                throw new Dwo2Exception(Dwo2ExceptionCode.User_IllegalAction, "You Don't Have Permission to access this using usercode " + sc.getUserPrincipal().getName() + ".");
//            }
//            profile = DwoProfileManager.findEntity(MySQLPersistenceId.getNativeId(domProfile));
//            if (profile == null) {
//                LOG.log(Level.SEVERE, "Username {0}: ILLEGAL USER-OPERATION: Using unknown profileId {1} in HasRole differs from user principal name {0}.", new Object[]{sc.getUserPrincipal().getName(), domProfile.getId()});
//                throw new Dwo2Exception(Dwo2ExceptionCode.User_IllegalAction, "You Don't Have Permission to access this using usercode " + sc.getUserPrincipal().getName() + ".");
//            }
//        } catch (Dwo2Exception ex) {
//            LOG.log(Level.SEVERE, "", ex);
//            throw new Dwo2RestException(ex);
//        }
//
//        try {
//            school = HasRoleUtilManager.getSchoolforHasRole(phr);
//        } catch (Dwo2Exception ex) {
//            LOG.log(Level.SEVERE, "", ex);
//            throw new Dwo2RestException(ex);
//        }
//
//        //fetch Profile
//        if (phr != null && school != null) {
//            DomResultsPerTeacher results = new DomResultsPerTeacher();
//            results.setFetchTimeStamp(DwoDateUtilities.getCurrentDwoUnixTimeStamp());
//            // DomResultsPerTeacher requires a fetchTimeStamp, teacher, schoolclasses, 
//            // studentsof, students, 
//            // classcourses, courses, scocontext's and studentscocontext's.
//
//            //Fetch teacher
//            DomTeacher teacher = UserManager.findEntity(phr.getPersistentHasRolePK().getUserID()).buildDomTeacher();
//            results.setTeacher(teacher);
//
//            //Collect schoolClasses of the teacher
//            List<PersistentSchoolClass> schoolClasses = SchoolClassUtilManager.getSchoolClassesOfTeacher(phr);
//            HashMap<PersistenceId, DomSchoolClass> domSchoolClasses = new HashMap<>(schoolClasses.size());
//            HashMap<PersistentStudentOfClassPK, PersistentStudentOfClass> socMap = new HashMap<>();
//            HashMap<Long, PersistentUser> studentMap = new HashMap<>();
            //create the DomSchoolClasses
//            schoolClasses.stream().map((schoolClass) -> {
//                DomSchoolClass s = schoolClass.buildDomSchoolClass();
//                domSchoolClasses.putIfAbsent(s.getId(), s);
//                return schoolClass;
//            }).forEach((schoolClass) -> {
//                //And while at it, for each schoolClass fill the set of studentsOf and students
////                try {
//                for (PersistentStudentOfClass soc : StudentOfClassManager.findEntities(schoolClass)) {
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
//            });
//            List<DomMapEntry<PersistenceId, DomSchoolClass>> scList = new ArrayList<>(domSchoolClasses.size());
//            domSchoolClasses.entrySet().stream().forEach((entry) -> {
//                scList.add(new DomMapEntry(entry));
//            });
//            results.setSchoolClasses(scList);
//
//            //convert studentMap and set in result
//            HashMap<PersistenceId, DomStudent> domStudents = new HashMap<>(studentMap.size());
//            studentMap.entrySet().stream().forEach((keyValuePair) -> {
//                DomStudent s = keyValuePair.getValue().buildDomStudent();
//                domStudents.put(s.getId(), s);
//            });
//            List<DomMapEntry<PersistenceId, DomStudent>> entryList = new ArrayList<>(domStudents.size());
//            domStudents.entrySet().stream().forEach((entry) -> {
//                entryList.add(new DomMapEntry<PersistenceId, DomStudent>(entry));
//            });
//            results.setStudents(entryList);
//            //convert StudentOfClass map (socMap) and set in result
//            HashMap<PersistenceId, DomStudentOfClass> domSocs = new HashMap<>(socMap.size());
//            Set<PersistentHasRolePK> studentHasRoleSet = new HashSet<>();
//            socMap.entrySet().stream().forEach((keyValuePair) -> {
//                DomStudentOfClass s = keyValuePair.getValue().buildDomStudentOfClass();
//                domSocs.putIfAbsent(s.getId(), s);
//                PersistentHasRolePK key = new PersistentHasRolePK(keyValuePair.getKey());
//                                        studentHasRoleSet.add(key);
//
//            });
//            List<DomMapEntry<PersistenceId, DomStudentOfClass>> socsList = new ArrayList<>(domSocs.size());
//            domSocs.entrySet().stream().forEach((entry) -> {
//                socsList.add(new DomMapEntry(entry));
//            });
//            results.setStudentsOfClasses(socsList);

            //Fetch courses for all classes ClassCourses. No filtering occurs on
            //CourseType, notBefore and notAfter for results. Filtering of Courses
            //occurs on Profile.
//            HashMap<Long, PersistentClassCourse> classCoursesMap = new HashMap<>();
//            HashMap<Long, PersistentCourse> coursesMap = new HashMap<>();
//            schoolClasses.stream().forEach((schoolClass) -> {
//                List<PersistentClassCourse> ccList = ClassCourseManager.findEntities(schoolClass);
//                ccList.forEach((classCourse) -> {
//                    //fetch course and check profile
//                    PersistentCourse course = CourseManager.findEntity(classCourse.getCourseID());
//                    //note currently one class course per higher tree node
//                    if (course != null && !course.isWithChildren()
//                            && course.getDwoProfileID().equals(profile.getDwoProfileID())) {
//                        //push to classCourses 
//                        classCoursesMap.putIfAbsent(classCourse.getClassCourseID(), classCourse);
//                        //push to courses map for recursive collection
//                        coursesMap.putIfAbsent(course.getCourseID(), course);
//                    }
//                });
//            });
//
//            //fill DomClassCourse List
//            HashMap<PersistenceId, DomClassCourse> domClassCourses = new HashMap<>(classCoursesMap.size());
//            classCoursesMap.entrySet().forEach((keyValuePair) -> {
//                DomClassCourse c = keyValuePair.getValue().buildDomClassCourse();
//                domClassCourses.put(c.getId(), c);
//            });
//            List<DomMapEntry<PersistenceId, DomClassCourse>> dccList = new ArrayList<>(domClassCourses.size());
//            domClassCourses.entrySet().stream().forEach((entry) -> {
//                dccList.add(new DomMapEntry(entry));
//            });
//
//            results.setClassCourses(dccList);
//
//            HashMap<PersistenceId, DomCourse> domCourses = new HashMap<>();
//            Map<Long, PersistentCourse> courses = new HashMap<>();
//            Queue<PersistentCourse> courseQueue = new LinkedList<>();
//            Map<Long, PersistentCourse> leaves = new HashMap<>();
//            courseQueue.addAll(coursesMap.values());//note this is a set!
//
//            //Danger Will Robinson, circular reference will hang thread forever.
//            //hence map and queue approach and a Depth First Search queue approach. 
//            //Trying to get the queue empty.
//            while (!courseQueue.isEmpty()) {
//                PersistentCourse course = courseQueue.remove();
//                PersistentCourse r = courses.putIfAbsent(course.getCourseID(), course);
//                if (r == null && course.isWithChildren()) {
//                    //put current course in the courseMap
//                    //put kids on the queue
//                    List<PersistentCourse> childrenCourses = CourseManager.findChildrenOf(profile, course);
//                    //add courses to a map 
//                    //if not in map add to queue
//                    courseQueue.addAll(childrenCourses);
//                } else {//course is aleave
//                    leaves.putIfAbsent(course.getCourseID(), course);
//                }
//            }
//            //export courses to result.
//            courses.entrySet().stream().forEach((keyValuePair) -> {
//                DomCourse c = keyValuePair.getValue().buildDomCourse();
//                domCourses.putIfAbsent(c.getId(), c);
//            });
//
//            List<DomMapEntry<PersistenceId, DomCourse>> dcList = new ArrayList<>(domCourses.size());
//            domCourses.entrySet().stream().forEach((entry) -> {
//                dcList.add(new DomMapEntry(entry));
//            });
//            results.setCourses(dcList);
//
//            //process leaves and fill hashmap scoContext
//            HashMap<Long, PersistentScoContext> scosMap = new HashMap<>();
//            leaves.entrySet().stream().forEach((keyValuePair) -> {
//                List<PersistentScoContext> scoContexts = ScoContextManager.findEntities(keyValuePair.getValue());
//                scoContexts.forEach((scoContext) -> {
//                    scosMap.putIfAbsent(scoContext.getScoID(), scoContext);
//                });
//            });
//            HashMap<PersistenceId, DomScoContext> domScoContexts = new HashMap<>(scosMap.size());
//            scosMap.entrySet().stream().forEach((keyValuePair) -> {
//                DomScoContext s = keyValuePair.getValue().buildDomScoContext();
//                domScoContexts.put(s.getId(), s);
//            });
//
//            List<DomMapEntry<PersistenceId, DomScoContext>> dscList = new ArrayList<>(domScoContexts.size());
//            domScoContexts.entrySet().stream().forEach((entry) -> {
//                dscList.add(new DomMapEntry(entry));
//            });
//            results.setScoContexts(dscList);
//
//            //fill hashmap studentSco for each student x sco
////            HashMap<Long, PersistentStudentScoContext> studentScosMap = new HashMap<>();
////            scosMap.entrySet().forEach((sco) -> {
////                List<PersistentStudentScoContext> studentScos = StudentScoContextManager.findEntities(sco.getValue());
////                studentScos.forEach((studentSco) -> {
////                    studentScosMap.putIfAbsent(studentSco.getStudentSco(), studentSco);
////                });
////            });
//            HashMap<Long, PersistentStudentScoContext> studentScosMap = new HashMap<>();
//            for (PersistentScoContext sco : scosMap.values()) {
//                for (PersistentHasRolePK hasRoleKey : studentHasRoleSet) {
//                    List<PersistentStudentScoContext> studentScos = StudentScoContextManager.findEntities(sco, hasRoleKey);
//                    studentScos.forEach((studentSco) -> {
//                        studentScosMap.putIfAbsent(studentSco.getStudentSco(), studentSco);
//                    });
//                }
//            }
//
//            HashMap<PersistenceId, DomStudentScoContext> domStudentScoContexts = new HashMap<>(studentScosMap.size());
//            studentScosMap.entrySet().stream().forEach((keyValuePair) -> {
//                DomStudentScoContext s = keyValuePair.getValue().buildDomStudentScoContext();
//                domStudentScoContexts.put(s.getId(), s);
//            });
//
//            List<DomMapEntry<PersistenceId, DomStudentScoContext>> sscList = new ArrayList<>(domStudentScoContexts.size());
//            domStudentScoContexts.entrySet().stream().forEach((entry) -> {
//                sscList.add(new DomMapEntry(entry));
//            });
//
//            results.setStudentScoContexts(sscList);
//
//            return results;
//            // recurse here using Java queue
//        } else {
//            LOG.log(Level.WARNING, "Username {0}: ILLEGAL USER-OPERATION: Trying to access teacher functionality by user with usercode {0}.", new Object[]{sc.getUserPrincipal().getName()});
//            throw new Dwo2RestException(Dwo2ExceptionCode.User_IllegalAction, "You Don't Have Permission to access this using usercode " + sc.getUserPrincipal().getName() + ".");
//        }
    
    
}
