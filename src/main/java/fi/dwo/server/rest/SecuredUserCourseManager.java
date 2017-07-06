package fi.dwo.server.rest;


import java.io.StringWriter;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;

import nl.uu.fi.dwo.rest.dom.entities.DomCourse;
import nl.uu.fi.dwo.rest.dom.entities.DomCourseStudent;
import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfile;
import nl.uu.fi.dwo.rest.dom.entities.DomHasRole;
import nl.uu.fi.dwo.rest.entities.RestCourse;
import nl.uu.fi.dwo.rest.entities.RestDwoProfile;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.exceptions.Dwo2ExceptionCode;
import nl.uu.fi.dwo.rest.exceptions.Dwo2RestException;
import fi.beans.dwomaccess.JSONEncoder;
import fi.beans.private_base64code.StringCodeObject;
import fi.dwo.commons.persistence.MySQLPersistenceId;
import fi.dwo.commons.persistence.entities.PersistentCourse;
import fi.dwo.commons.persistence.entities.PersistentDwoProfile;
import fi.dwo.commons.persistence.entities.PersistentHasRole;
import fi.dwo.commons.persistence.entities.PersistentHasRolePK;
import fi.dwo.commons.persistence.entities.PersistentSchool;
import fi.dwo.commons.persistence.entities.PersistentUser;
import fi.dwo.server.PersistentDataManagers.core.CourseManager;
import fi.dwo.server.PersistentDataManagers.core.DwoProfileManager;
import fi.dwo.server.PersistentDataManagers.core.HasRoleManager;
import fi.dwo.server.PersistentDataManagers.core.UserManager;
import fi.dwo.server.PersistentDataManagers.util.HasRoleUtilManager;

/**
 * Handles the public registration of new users.
 *
 * @author W.P.G. 
 */
@Path("/secure/user/course")
public class SecuredUserCourseManager {

    private static final Logger LOG = Logger.getLogger(SecuredUserCourseManager.class.getName());

    /**
     * Returns the Course description of a course. This method uses MySQL-based
     * indices and should be phased out.
     * 
     * @param courseId
     * @return
     * @throws Dwo2Exception 
     */
    @PUT
    @Produces({"application/json"})
    @Path("/getCourseDescription")
    public String getCourseDescription(@Context SecurityContext sc, RestCourse id) {
    	try {
// TODO NPE tests 		    		
    		DomDwoProfile domDwoProfile = id.getDomDwoProfile();
    		DomHasRole    hasRole = id.getRestContext().getDomHasRole();
    		PersistentUser user = getUserFromContext(sc);		

// TODO verify profile/limited
// TODO hasRole correct	
    		
    		Long courseId = MySQLPersistenceId.getNativeId(id.getDomCourse());
            PersistentCourse course = CourseManager.findEntity(courseId);
            if(course == null) 
            	return "{}"; // Not fatal
            if(! course.getDwoProfileID().equals(MySQLPersistenceId.getNativeId(domDwoProfile)))
            	return "{}";
            Hashtable map = (Hashtable) StringCodeObject.decodeStringToObject(course.getDescription(), null); // FIXM load wiskopdr.jar
            StringWriter writer = new StringWriter();
			JSONEncoder.encode(map, writer, null); // FIXME, load wiskopdr.jar
	        return writer.toString();
		} catch (Exception e) {
			LOG.fine("getCourseDescription "  + id + " " + e.toString());
			return "{}";
		}
    }
    
    private static String LIMITED =  "l"; // Fixme ergens in PersistentDwoProfile?
 
    @PUT
    @Path("/getSchool")
    @Produces({"application/json"})
    public List<DomCourseStudent> getCoursesSchool(@Context SecurityContext sc, RestDwoProfile rest) throws Dwo2Exception {
// TODO NPE tests 		    		
		DomDwoProfile domDwoProfile = rest.getDomDwoProfile();
		DomHasRole    hasRole = rest.getRestContext().getDomHasRole();
		PersistentUser user = getUserFromContext(sc);		
		Long id = MySQLPersistenceId.getNativeId(domDwoProfile);
		PersistentDwoProfile profile = DwoProfileManager.findEntity(id);
        PersistentHasRolePK hasRoleKey = MySQLPersistenceId.getNativeId(hasRole);        
		PersistentHasRole hr = HasRoleManager.findEntity(hasRoleKey);
// FIXME check role is not a guest/student
		
		PersistentSchool school = HasRoleUtilManager.getSchoolforHasRole(hr);
		List<PersistentCourse> courses = CourseManager.findChildrenOf(profile, school);		
		Stream<PersistentCourse> stream = courses.stream();
		Stream<DomCourseStudent> map = stream.map((c) -> c.buildDomCourseStudent());
		map = map.sorted(DomCourseStudentComparator.INSTANCE);
		return map.collect(Collectors.toList());
    }
    
    
    
    @PUT
    @Path("/getRoot")
    @Produces({"application/json"})
    public List<DomCourseStudent> getCourses(@Context SecurityContext sc, RestDwoProfile rest) 
    {
    	try {
   // TODO NPE tests 		    		
    		DomDwoProfile domDwoProfile = rest.getDomDwoProfile();
    		DomHasRole    hasRole = rest.getRestContext().getDomHasRole();
    		PersistentUser user = getUserFromContext(sc);
			PersistentHasRolePK hasRoleKey = MySQLPersistenceId.getNativeId(hasRole);
            PersistentHasRole phr = HasRoleManager.findEntity(hasRoleKey);
// userid must match hasrole
         		if (user.getId().longValue() != phr.getPersistentHasRolePK().getUserID().longValue())
         			return Collections.emptyList();

// Security, only non limited profiles are public 		
    		long id = MySQLPersistenceId.getNativeId(domDwoProfile);
    		PersistentDwoProfile profile = DwoProfileManager.findEntity(id);
    		if ( profile.getDwoProfileRights().contains(LIMITED))
    		{
    			PersistentHasRole hr = phr;
    			PersistentSchool limited = HasRoleUtilManager.getSchoolforHasRole(hr);
// SECURITY
    			// if ! limitedschools .contains (limited) return EMPTY_LIST;  			
    		}
    		
    		PersistentSchool school = new PersistentSchool(null);
    		List<PersistentCourse> courses = CourseManager.findChildrenOf(profile, school);
    		
    		Stream<PersistentCourse> stream = courses.stream();
			Stream<DomCourseStudent> map = stream.map((c) -> c.buildDomCourseStudent());
			map = map.sorted(DomCourseStudentComparator.INSTANCE);
    		return map.collect(Collectors.toList());
    	} catch (Dwo2RestException e) {
    		throw e;
    	} catch (Exception e) {
    		LOG.log(Level.SEVERE, "getCourses", e);
    		throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, "An exception occured while fetching the modules.");	
    	}
    }
    
    @PUT
    @Path("/getChildren")
    @Produces({"application/json"})
    public List<DomCourseStudent> getCourses(@Context SecurityContext sc, RestCourse rest) {
    	try {
    		DomCourse course = rest.getDomCourse();
    		DomHasRole hasRole = rest.getRestContext().getDomHasRole();
    		DomDwoProfile domDwoProfile = rest.getDomDwoProfile();
    		Long id = MySQLPersistenceId.getNativeId(course);
// Context
            PersistentUser user = getUserFromContext(sc);		
            PersistentHasRolePK hasRoleKey = MySQLPersistenceId.getNativeId(hasRole);        
    		PersistentHasRole hr = HasRoleManager.findEntity(hasRoleKey);
// userid must match hasrole
         		if (! user.getId().equals( hr.getPersistentHasRolePK().getUserID()))
         			return Collections.emptyList();
// FIXME check role is not a guest/student
    		
    		PersistentSchool school = HasRoleUtilManager.getSchoolforHasRole(hr);

    		
    		PersistentCourse parent = CourseManager.findEntity(id);
// Verify parent is public and profile is not limited and hasChildren, 
// or that school of hasRole == school of course (no pun)
    		PersistentDwoProfile profile = DwoProfileManager.findEntity(parent.getDwoProfileID());
    		if ( parent.getSchoolID() != null || 
    			 ! parent.isWithChildren()	||
    			 profile.getDwoProfileRights().contains(LIMITED)
// Verify context: profile matches...
    			|| !profile.getDwoProfileID().equals(MySQLPersistenceId.getNativeId(domDwoProfile))
    		)
    		{
    			if(!school.getSchoolID().equals(parent.getSchoolID()))
    				return Collections.emptyList();
    		}
    			
    		List<PersistentCourse> courses = CourseManager.findChildrenOf(parent);    		
    		return courses.stream()
    				.map( (c)-> c.buildDomCourseStudent())
    				.sorted(DomCourseStudentComparator.INSTANCE)
    				.collect(Collectors.toList());
    	} catch (Dwo2RestException e) {
    		throw e;
    	} catch (Exception e) {
    		LOG.log(Level.WARNING, "getCourses", e);
    		throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, "An exception occured while fetching the modules.");	
    	}    	
    }

	private static PersistentUser getUserFromContext(SecurityContext sc) {
		PersistentUser user = null;
		try {
		    user = UserManager.findByUserName(sc.getUserPrincipal().getName());
		    LOG.log(Level.FINE, "Username {0}: Fetched User with username {1}", new Object[]{sc.getUserPrincipal().getName(), user.getUsername()});
		}
		catch (Exception e) {
		    LOG.log(Level.SEVERE, "Username " + sc.getUserPrincipal().getName() + ": Unexpected exception", e);
		    throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, "Failed to query user id " + sc.getUserPrincipal().getName() + " .");
		}
		return user;
	}
    
    @PUT
    @Path("/get")
    @Produces({"application/json"})
    public DomCourseStudent getCourse(@Context SecurityContext sc, RestCourse rest) {
    	try {
// TODO NPE tests 		    		
    		DomDwoProfile domDwoProfile = rest.getDomDwoProfile();
    		DomHasRole    hasRole = rest.getRestContext().getDomHasRole();
    		PersistentUser user = getUserFromContext(sc);		
// TODO hasRole is correct    		
    		DomCourse course = rest.getDomCourse();
    		Long id = MySQLPersistenceId.getNativeId(course);
    		PersistentCourse parent = CourseManager.findEntity(id);
// TODO Verify parent is public and profile is not limited OR user.school matches course.school
    		PersistentDwoProfile profile = DwoProfileManager.findEntity(parent.getDwoProfileID());
//    		if ( parent.getSchoolID() != null || 
//    			 profile.getDwoProfileRights().contains(LIMITED))
//    			return null;

// Verify context: profile matches...  		
    		if (profile.getDwoProfileID().equals(MySQLPersistenceId.getNativeId(domDwoProfile)))
    			return parent.buildDomCourseStudent();
    	} catch (Dwo2RestException e) {
    		throw e;
    	} catch (Exception e) {
    		LOG.log(Level.WARNING, "getCourse", e);
    		throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, "An exception occured while fetching the module.");	
    	}
    	return null;
    }
    
}
