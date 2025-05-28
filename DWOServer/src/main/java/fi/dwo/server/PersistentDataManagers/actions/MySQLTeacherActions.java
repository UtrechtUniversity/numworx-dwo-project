/**
 * Copyrighted Feb 12, 2018
 */
package fi.dwo.server.PersistentDataManagers.actions;

import fi.dwo.commons.persistence.MySQLPersistenceId;
import fi.dwo.commons.persistence.entities.PersistentClassCourse;
import fi.dwo.commons.persistence.entities.PersistentCourse;
import fi.dwo.commons.persistence.entities.PersistentHasRole;
import fi.dwo.commons.persistence.entities.PersistentSchool;
import fi.dwo.commons.persistence.entities.PersistentSchoolClass;
import fi.dwo.commons.persistence.entities.PersistentSchoolGroup;
import fi.dwo.commons.persistence.entities.PersistentStudentModelContext;
import fi.dwo.commons.persistence.entities.PersistentStudentModelItem;
import fi.dwo.commons.persistence.entities.PersistentStudentOfClass;
import fi.dwo.commons.persistence.entities.PersistentStudentOfClassPK;
import fi.dwo.commons.persistence.entities.PersistentUser;
import fi.dwo.server.PersistentDataManagers.access.TeacherDomainAuthorizer;
import fi.dwo.server.PersistentDataManagers.cache.HasRoleCache;
import fi.dwo.server.PersistentDataManagers.access.StudentDomainAuthorizer.Context;
import fi.dwo.server.PersistentDataManagers.core.ClassCourseManager;
import fi.dwo.server.PersistentDataManagers.core.CourseManager;
import fi.dwo.server.PersistentDataManagers.core.HasRoleManager;
import fi.dwo.server.PersistentDataManagers.core.StudentModelContextManager;
import fi.dwo.server.PersistentDataManagers.core.StudentModelItemManager;
import fi.dwo.server.PersistentDataManagers.core.StudentOfClassManager;
import fi.dwo.server.PersistentDataManagers.core.XapiManager;
import fi.dwo.server.PersistentDataManagers.util.CourseInClassManager;
import fi.dwo.server.PersistentDataManagers.util.SchoolClassUtilManager;
import fi.dwo.server.PersistentDataManagers.util.StudentInClassManager;
import fi.dwo.server.PersistentDataManagers.util.StudentModelContextUtilManager;
import fi.dwo.server.PersistentDataManagers.util.TeacherSchoolClassUtilManager;
import fi.dwo.server.rest.util.Origin;
import fi.dwo.server.rest.util.SchoolyearUtilManager;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.HttpURLConnection;
import java.net.URI;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.PersistenceException;
import javax.ws.rs.core.UriInfo;

import nl.numworx.schoolyear.jclient.SchoolyearClient;
import nl.numworx.schoolyear.jclient.dto.Content;
import nl.numworx.schoolyear.jclient.dto.Element;
import nl.numworx.schoolyear.jclient.dto.ElementId;
import nl.numworx.schoolyear.jclient.dto.ExamDTO;
import nl.numworx.schoolyear.jclient.dto.Vault;
import nl.numworx.schoolyear.jclient.dto.WebPageEntireDomain;
import nl.numworx.schoolyear.jclient.dto.WebPageUrl;
import nl.numworx.schoolyear.jclient.dto.WebPageRegex;
import nl.numworx.schoolyear.jclient.dto.Workspace;
import nl.uu.fi.dwo.rest.dom.entities.DomLRS;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelCategory;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContextId;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelObj;
import nl.uu.fi.dwo.rest.dom.entities.util.AboType;
import nl.uu.fi.dwo.rest.dom.entities.util.CourseType;
import nl.uu.fi.dwo.rest.dom.entities.util.PublishState;
import nl.uu.fi.dwo.rest.dom.entities.util.ViewState;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.exceptions.Dwo2ExceptionCode;
import nl.uu.fi.dwo.rest.exceptions.Dwo2RestException;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;
import nl.uu.fi.dwo.rest.util.DwoDateUtilities;

/**
 *
 * @author plas0006
 */
public class MySQLTeacherActions implements TeacherActions {

    private static final Logger LOG = Logger.getLogger(MySQLTeacherActions.class.getName());

    public PersistentStudentModelContext addStudentModel(TeacherDomainAuthorizer.Context context, PersistentStudentModelContext model) throws Dwo2Exception {
        try {
            PersistentStudentModelContext pModel = new PersistentStudentModelContext();
            pModel.setModelStructure(model.getModelStructure());
            pModel.setSchoolID(model.getSchoolID());
            pModel.setPublishState(model.getPublishState());
            pModel.setDwoProfileID(model.getDwoProfileID());
            return StudentModelContextUtilManager.create(pModel);
        } catch (Exception e) {
            String msg = MessageFormat.format("Username {0}: Internal error: {1}", new Object[]{context.getUserCtx().getUser().getUsername(), e.getMessage()});
            LOG.log(Level.WARNING, msg, e);
            throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, msg);
        }
    }

    public List<PersistentStudentModelContext> getStudentModels(TeacherDomainAuthorizer.Context context) throws Dwo2Exception {
        List<PersistentStudentModelContext> pModels = StudentModelContextManager.findEntities(context.getUserCtx().getSchool());
        return pModels;
    }

    public List<PersistentStudentModelContext> getReducedStudentModels(TeacherDomainAuthorizer.Context context) throws Dwo2Exception {
    	List<PersistentStudentModelContext> pModels = StudentModelContextManager.findReducedEntities(context.getUserCtx().getSchool(), context.getTeacherCtx().getProfile());
        return pModels;
    }

    public PersistentStudentModelContext getStudentModel(TeacherDomainAuthorizer.Context context, DomStudentModelContextId model) throws Dwo2Exception {
        Long id = MySQLPersistenceId.getNativeId(model);
        PersistentStudentModelContext pModel = StudentModelContextManager.findEntity(id);
        if ( pModel == null) {
          throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, "Illegal operation");
        }
        //verify if studentModel is in school
        long uSchoolID = context.getUserCtx().school.getSchoolID().longValue();
		long mSchoolID = pModel.getSchoolID().longValue();
		if ( mSchoolID != 0L && mSchoolID != uSchoolID) {
            LOG.log(Level.WARNING, "Username {0}: ILLEGAL USER-OPERATION: Requested studentmode {2} is from a different school that is registered for hasRole in school {1} with usercode {0}.", new Object[]{context.getUserCtx().getUser().getUsername(), context.getUserCtx().getSchool().getSchoolID(), (pModel != null) ? pModel.getSchoolID() : "model==null"});
            throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, "Database error using usercode " + context.getUserCtx().getUser().getUsername() + ".");
        }
        StudentModelContextUtilManager.merge(pModel);
        return pModel;
    }
    
    
    
    @Override
    public List<PersistentSchoolClass> getSchoolClasses(TeacherDomainAuthorizer.Context context) throws Dwo2Exception {
        List<PersistentSchoolClass> schoolClasses = SchoolClassUtilManager.getSchoolClassesOfTeacher(context.getUserCtx().getHasRole());
        return schoolClasses;
    }

    @Override
    public List<PersistentUser> getTeachersStudents(TeacherDomainAuthorizer.Context context) throws Dwo2Exception {
        Map<String, PersistentUser> students = new HashMap<>();
        List<PersistentSchoolClass> schoolClasses = getSchoolClasses(context);
        for (PersistentSchoolClass sc : schoolClasses) {
            StudentInClassManager.findEntities(sc).forEach((k -> students.putIfAbsent(k.getUser().buildPersistenceId().getIdString(), k.getUser())));
        }
        List<PersistentUser> results = new ArrayList<>(students.size());
        students.forEach((k, v) -> results.add(v));
        return results;
    }

    @Override
    public void addStudent(TeacherDomainAuthorizer.Context context, PersistentSchoolClass sc, PersistentHasRole shr) throws Dwo2Exception {
        try {
            PersistentStudentOfClassPK socId = new PersistentStudentOfClassPK(shr.getPersistentHasRolePK().getUserID(), sc.getClassID(), shr.getPersistentHasRolePK().getSchoolGroupID());
            PersistentStudentOfClass soc = new PersistentStudentOfClass();
            soc.setPersistentStudentOfClassPK(socId);
            soc.setRegisterDate(DwoDateUtilities.getCurrentDwoDate());
            StudentOfClassManager.create(soc);

            if (shr.getClassID() == null) {
                shr.setSchoolClass(sc);
                HasRoleCache.remove(shr);
                HasRoleManager.edit(shr); // TODO met try/catch?
            }

        } catch (PersistenceException e) {
            String msg = MessageFormat.format("Can not add student to class for Username {0}, error: {1}", new Object[]{context.getUserCtx().getUser().getUsername(), e.getMessage()});
            LOG.log(Level.WARNING, msg, e);
            throw new Dwo2RestException(Dwo2ExceptionCode.Rest_CanNotAddStudentToClass, msg);
        }
    }

    @Override
    public List<PersistenceId> getSharedTeacherClasses(TeacherDomainAuthorizer.Context context, PersistentUser otherTeacher) throws Dwo2Exception {
        return TeacherSchoolClassUtilManager.getSharedTeacherClasses(context.getUserCtx().getUser(), context.getUserCtx().getSchoolGroup(), otherTeacher);
    }

    @Override
    public List<PersistenceId> getTeachersClassesOfStudent(TeacherDomainAuthorizer.Context context, PersistentSchoolGroup studentSg, PersistentUser student) throws Dwo2Exception {
        return TeacherSchoolClassUtilManager.getTeachersStudentClasses(context.getUserCtx().getUser(), context.getUserCtx().getSchoolGroup(), studentSg, student);
    }
    
    @Override
    public Boolean addCourseToClass(TeacherDomainAuthorizer.Context context, CourseType courseType, Date from, Date to, String accessKey) throws Dwo2Exception {
        //Loop up the course tree and find the tree path
        Deque<PersistentCourse> treePath = new LinkedList<>();
        PersistentCourse curCourse = context.getTeacherCtx().getCourse();
        treePath.add(curCourse);
        while (curCourse.getParentID() != 0) {
            curCourse = CourseManager.findEntity(curCourse.getParentID());
            //if no classCourse addPrincipalUser to stack
//            if (ClassCourseManager.findEntities(context.getTeacherCtx().getSchoolClass(), curCourse).isEmpty()) {
                treePath.push(curCourse);
//            } else {
//                break; // Someone might erase an existing classcourse in the background, yet this failure will be visible after a tree refresh.
//            }
        }// stop when added course with parentid = 0;

        //Walk the treepath list from bottom to top of tree and add classCourses idempotently (ignore if it already exists).   
        while (treePath.size() > 0) {
            curCourse = treePath.pop();
            List<PersistentClassCourse> ccResult = ClassCourseManager.findEntities(context.getTeacherCtx().getSchoolClass(),curCourse);
            //if below is for the future case classcourse are not unique any more.
            if (ccResult.isEmpty()) { //create new 
                PersistentClassCourse cc = new PersistentClassCourse();
                cc.setClassID(context.getTeacherCtx().getSchoolClass().getClassID());
                cc.setCourseID(curCourse.getCourseID());
                cc.setDwoProfileID(curCourse.getDwoProfileID());
                if (treePath.size() > 0) {
                    cc.setType(CourseType.normal.ordinal());  // intermediate node
                } else {
                	cc.setNotAfter(to);
                	cc.setNotBefore(from);
                	cc.setAccessKey(accessKey);
                	cc.setType(courseType.ordinal());
                	if (courseType == CourseType.kiosk) {
                		setKioskMode(cc, context.getUserCtx().school, context.getTeacherCtx().getCourse(), context.getTeacherCtx().getSchoolClass() );
                	}
               }
                cc.setViewState(ViewState.studentsAndTeachers);
                ClassCourseManager.insertOrUpdateViewState(cc);
//                    LOG.log(Level.INFO, "created cc of "+ccResult);
            } else {
                for (PersistentClassCourse cc : ccResult) {
//                    LOG.log(Level.INFO, "setting visibility of "+cc.getClassCourseID());
                    ClassCourseManager.editViewState(cc.getClassCourseID(),ViewState.studentsAndTeachers);
                }
            }
        }
        return true;    
            }
 
    private static void allowMethods(String... methods) {
        try {
            Field methodsField = HttpURLConnection.class.getDeclaredField("methods");

            Field modifiersField = Field.class.getDeclaredField("modifiers");
            modifiersField.setAccessible(true);
            modifiersField.setInt(methodsField, methodsField.getModifiers() & ~Modifier.FINAL);

            methodsField.setAccessible(true);

            String[] oldMethods = (String[]) methodsField.get(null);
            Set<String> methodsSet = new LinkedHashSet<>(Arrays.asList(oldMethods));
            methodsSet.addAll(Arrays.asList(methods));
            String[] newMethods = methodsSet.toArray(new String[0]);

            methodsField.set(null/*static field*/, newMethods);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
    }
    static {
    	allowMethods("PATCH");
    }
    
    
    
    public static String setKioskMode(PersistentClassCourse cc, PersistentSchool school, PersistentCourse course, PersistentSchoolClass sc) throws Dwo2Exception {
		if (school.getAboType() == AboType.premium && school.hasKiosk()) {
			SchoolyearClient client = SchoolyearUtilManager.build(school);
			int count = StudentOfClassManager.findEntities(sc).size();
			ExamDTO exam = new ExamDTO();
			exam.display_name = course.getName();
			exam.expected_workspaces = count;
			exam.end_time = cc.getNotAfter();
			exam.start_time = cc.getNotBefore();
			if (exam.start_time == null) {			
				cc.setNotBefore(exam.start_time = new Date());
			}
			if (exam.end_time == null) {
				cc.setNotAfter(exam.end_time = exam.start_time);
			}
			
			exam.pin = cc.getAccessKey();
			if (cc.getSyExamID() == null) {
				exam.workspace = new Workspace();
				exam.workspace.vault = new Vault();
				exam.workspace.vault.content = new Content();
				Map<String, Element> elements = exam.workspace.vault.content.elements = new HashMap<>();
				String uuid = UUID.randomUUID().toString();
				Element root = new Element();
				root.url_entire_domain = new WebPageEntireDomain();
				root.type = WebPageEntireDomain.TYPE;
				root.origin = "api_key";
				root.url_entire_domain.url = Origin.ORIGINS[0];
				elements.put(uuid, root);
				Element logout = new Element();
				List<ElementId> logouts = new ArrayList<>(3);
				uuid = UUID.randomUUID().toString();
				logouts.add(new ElementId(uuid));
				logout.origin = "api_key";
				logout.type = WebPageUrl.TYPE;
				logout.url = new WebPageUrl();
				logout.url.url = root.url_entire_domain.url + "/toets/logout.html";
		        elements.put(uuid, logout);

		        uuid = UUID.randomUUID().toString();
				logouts.add(new ElementId(uuid));
				logout.origin = "api_key";
				logout.type = WebPageUrl.TYPE;
				logout.url = new WebPageUrl();
				logout.url.url = root.url_entire_domain.url + "/dwo/saml/doLogout.jsp";
		        elements.put(uuid, logout);
		        
		        uuid = UUID.randomUUID().toString();
				logouts.add(new ElementId(uuid));
				URI uri = URI.create(root.url_entire_domain.url);
				WebPageRegex regex = new WebPageRegex();
				logout.origin = "api_key";
				logout.type = WebPageRegex.TYPE;
				logout.url_regex = regex;
				regex.protocol = uri.getScheme();
				regex.hostname = uri.getHost();
				if (uri.getPort() != -1)
					regex.port = Integer.toString(uri.getPort());
				regex.pathname = "**/logout.html";				
		        elements.put(uuid, logout);
		        
		        uuid = UUID.randomUUID().toString();
				logouts.add(new ElementId(uuid));
				regex = new WebPageRegex();
				logout.origin = "api_key";
				logout.type = WebPageRegex.TYPE;
				logout.url_regex = regex;
				regex.protocol = uri.getScheme();
				regex.hostname = uri.getHost();
				if (uri.getPort() != -1)
					regex.port = Integer.toString(uri.getPort());
				regex.pathname = "/dwo/saml/doLogout.jsp";
				regex.search_params = Collections.singletonMap("return", "*");
		        elements.put(uuid, logout);
				exam.workspace.vault.content.exit_points = logouts;
				try {
					exam = client.createExam(exam);
				} catch (IOException e) {
					LOG.log(Level.SEVERE, "setKioskmode create for " + course, e);
				}
				cc.setSyExamID(exam.id);
				//cc.setAccessKey(exam.pin);
			} else {
				exam.id = cc.getSyExamID();
				try {
					exam = client.updateExam(exam);
				} catch (IOException e) {
					LOG.log(Level.SEVERE, "setKioskmode update for " + course, e);
				}
			}
			return null;
//			try {
//				String result = client.openSettingsUI(exam);
//				LOG.info("go to " + result);
//				return result;
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
		} 
// not valid, fall back to assesment
		{
			cc.setType(CourseType.assesment.ordinal());
			return null;
		}
		
	}

	@Override
    public Boolean attachCourseToClass(TeacherDomainAuthorizer.Context context) throws Dwo2Exception {
        //Loop up the course tree and find the tree path
        Deque<PersistentCourse> treePath = new LinkedList<>();
        PersistentCourse curCourse = context.getTeacherCtx().getCourse();
        treePath.add(curCourse);
        while (curCourse.getParentID() != 0) {
            curCourse = CourseManager.findEntity(curCourse.getParentID());
            //if no classCourse addPrincipalUser to stack
//            if (ClassCourseManager.findEntities(context.getTeacherCtx().getSchoolClass(), curCourse).isEmpty()) {
                treePath.push(curCourse);
//            } else {
//                break; // Someone might erase an existing classcourse in the background, yet this failure will be visible after a tree refresh.
//            }
        }// stop when added course with parentid = 0;

        //Walk the treepath list from bottom to top of tree and add classCourses idempotently (ignore if it already exists).   
        while (treePath.size() > 0) {
            curCourse = treePath.pop();
            List<PersistentClassCourse> ccResult = ClassCourseManager.findEntities(context.getTeacherCtx().getSchoolClass(),curCourse);
            //if below is for the future case classcourse are not unique any more.
            if (ccResult.isEmpty()) { //create new 
                PersistentClassCourse cc = new PersistentClassCourse();
                cc.setClassID(context.getTeacherCtx().getSchoolClass().getClassID());
                cc.setCourseID(curCourse.getCourseID());
                cc.setDwoProfileID(curCourse.getDwoProfileID());
                cc.setNotAfter(null);
                cc.setNotBefore(null);
                cc.setType(CourseType.normal.ordinal());
                cc.setViewState(ViewState.studentsAndTeachers);
                ClassCourseManager.insertOrUpdateViewState(cc);
//                    LOG.log(Level.INFO, "created cc of "+ccResult);
            } else {
                for (PersistentClassCourse cc : ccResult) {
//                    LOG.log(Level.INFO, "setting visibility of "+cc.getClassCourseID());
                    ClassCourseManager.editViewState(cc.getClassCourseID(),ViewState.studentsAndTeachers);
                }
            }
        }
        return true;
    }

    @Override
    public Boolean detachCourseFromClass(TeacherDomainAuthorizer.Context context) throws Dwo2Exception {
        CourseInClassManager.detachLeaveAndUpdateTree(context.getTeacherCtx().getSchoolClass(), context.getTeacherCtx().getCourse());
        return true;
    }
    
    @Override
    public DomLRS getLRS(TeacherDomainAuthorizer.Context context, UriInfo info) {
      PersistentUser user = context.getUserCtx().user;
      PersistentSchool school = context.getUserCtx().school;
      return XapiManager.getLRS(user, school, info);
    }

    
}
