package fi.dwo.server.rest;


import java.io.StringWriter;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import nl.uu.fi.dwo.rest.dom.entities.DomCourse;
import nl.uu.fi.dwo.rest.dom.entities.DomCourseStudent;
import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfile;
import nl.uu.fi.dwo.rest.entities.RestCourse;
import nl.uu.fi.dwo.rest.entities.RestDwoProfile;
import nl.uu.fi.dwo.rest.exceptions.Dwo2ExceptionCode;
import nl.uu.fi.dwo.rest.exceptions.Dwo2RestException;
import fi.beans.dwomaccess.JSONEncoder;
import fi.beans.private_base64code.StringCodeObject;
import fi.dwo.commons.persistence.MySQLPersistenceId;
import fi.dwo.commons.persistence.entities.PersistentCourse;
import fi.dwo.commons.persistence.entities.PersistentDwoProfile;
import fi.dwo.commons.persistence.entities.PersistentSchool;
import fi.dwo.server.PersistentDataManagers.core.CourseManager;
import fi.dwo.server.PersistentDataManagers.core.DwoProfileManager;

/**
 * Handles the public registration of new users.
 *
 * @author G.A.J. van der Plas
 */
@Path("/public/course")
public class PublicCourseManager {
	private static final boolean SECURITY = false;
    private static final Logger LOG = Logger.getLogger(PublicCourseManager.class.getName());

    /**
     * Returns the Course description of a course. This method uses MySQL-based
     * indices and should be phased out.
     * 
     * @param courseId
     * @return
     */
    @GET
    @Produces({"application/json"})
    @Path("/getCourseDescription")
    @Deprecated
    public String getCourseDescription(@DefaultValue("0") @QueryParam("courseId") Long courseId) {
        try {
            PersistentCourse course = CourseManager.findEntity(courseId);
            if(course == null) return "{}"; // Not fatal
            Hashtable map = (Hashtable) StringCodeObject.decodeStringToObject(course.getDescription(), null); // FIXM load wiskopdr.jar
            StringWriter writer = new StringWriter();
			JSONEncoder.encode(map, writer, null); // FIXME, load wiskopdr.jar
	        return writer.toString();
		} catch (Exception e) {
			LOG.fine("getCourseDescription "  + courseId + " " + e.toString());
			return "{}";
		}
    }
    
    private static String LIMITED =  "l"; // Fixme ergens in PersistentDwoProfile?
    
    @PUT
    @Path("/getRoot")
    @Produces({"application/json"})
    public List<DomCourseStudent> getCourses(RestDwoProfile rest) 
    {
    	try {
   // TODO NPE tests 		    		
    		DomDwoProfile domDwoProfile = rest.getDomDwoProfile();
   // test for honest people
    		if (domDwoProfile.getDwoProfileRights() != null && 
    				domDwoProfile.getDwoProfileRights().contains(LIMITED))
    			return Collections.emptyList();
   // Security, only non limited profiles are public 		
    		long id = MySQLPersistenceId.getNativeId(domDwoProfile);
    		PersistentDwoProfile profile = DwoProfileManager.findEntity(id);
if(SECURITY)
    		if ( profile.getDwoProfileRights().contains(LIMITED))
    			return Collections.emptyList();
    		
    		PersistentSchool school = new PersistentSchool(null);
    		List<PersistentCourse> courses = CourseManager.findChildrenOf(profile, school);
    		
    		Stream<PersistentCourse> stream = courses.stream();
			Stream<DomCourseStudent> map = stream.map((c) -> c.buildDomCourseStudent());
			map = map.sorted(DomCourseStudentComparator.INSTANCE);
    		return map.collect(Collectors.toList());
    	} catch (Dwo2RestException e) {
    		throw e;
    	} catch (Exception e) {
    		LOG.log(Level.WARNING, "getCourses", e);
    		throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, "An exception occured while fetching the modules.");	
    	}
    }
    
    @PUT
    @Path("/getChildren")
    @Produces({"application/json"})
    public List<DomCourseStudent> getCourses(RestCourse rest) {
    	try {
    		DomCourse course = rest.getDomCourse();
    		long id = MySQLPersistenceId.getNativeId(course);
    		PersistentCourse parent = CourseManager.findEntity(id);
// Verify parent is public and profile is not limited and hasChildren
    		PersistentDwoProfile profile = DwoProfileManager.findEntity(parent.getDwoProfileID());
if(SECURITY)
    		if ( parent.getSchoolID() != null || 
    			 ! parent.isWithChildren()	||
    			 profile.getDwoProfileRights().contains(LIMITED)
// Verify context: profile matches...
    			|| !rest.getDomDwoProfile().getId().equals(profile.buildPersistenceId())
    		)
    			return Collections.emptyList();
    			
    		List<PersistentCourse> courses = CourseManager.findChildrenOf(parent);    		
    		return courses.stream().map( (c)-> c.buildDomCourseStudent()).sorted(DomCourseStudentComparator.INSTANCE).collect(Collectors.toList());
    	} catch (Dwo2RestException e) {
    		throw e;
    	} catch (Exception e) {
    		LOG.log(Level.WARNING, "getCourses", e);
    		throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, "An exception occured while fetching the modules.");	
    	}    	
    }
    
    @PUT
    @Path("/get")
    @Produces({"application/json"})
    public DomCourseStudent getCourse(RestCourse rest) {
    	try {
    		DomCourse course = rest.getDomCourse();
    		long id = MySQLPersistenceId.getNativeId(course);
    		PersistentCourse parent = CourseManager.findEntity(id);
// Verify parent is public and profile is not limited
    		PersistentDwoProfile profile = DwoProfileManager.findEntity(parent.getDwoProfileID());
if(SECURITY) 
    		if ( parent.getSchoolID() != null || 
    			 profile.getDwoProfileRights().contains(LIMITED))
    			return null;
// TODO Verify context: profile matches...
    		if (!SECURITY || rest.getDomDwoProfile().getId().equals(profile.buildPersistenceId()))    		
    			return parent.buildDomCourseStudent();
    	} catch (Dwo2RestException e) {
    		throw e;
    	} catch (Exception e) {
    		LOG.log(Level.WARNING, "getCourses", e);
    		throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, "An exception occured while fetching the module.");	
    	}
    	return null;
    }
    
@GET
    @Path("/getImage")
    @Produces({"application/json"})
    public DomCourseStudent getImage() {
//    	try {
//    		DomCourse course = rest.getDomCourse();
//    		long id = MySQLPersistenceId.getNativeId(course);
//    		PersistentCourse parent = CourseManager.findEntity(id);
//// Verify parent is public and profile is not limited
//    		PersistentDwoProfile profile = DwoProfileManager.findEntity(parent.getDwoProfileID());
//if(SECURITY) 
//    		if ( parent.getSchoolID() != null || 
//    			 profile.getDwoProfileRights().contains(LIMITED))
//    			return null;
//// TODO Verify context: profile matches...
//    		if (!SECURITY || rest.getDomDwoProfile().getId().equals(profile.buildPersistenceId()))    		
//    			return parent.buildDomCourseStudent();
//    	} catch (Dwo2RestException e) {
//    		throw e;
//    	} catch (Exception e) {
//    		LOG.log(Level.WARNING, "getCourses", e);
//    		throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, "An exception occured while fetching the module.");	
//    	}
    	return null;
    }    
}
