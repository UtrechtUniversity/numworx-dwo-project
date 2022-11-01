package fi.dwo.server.rest;


import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Hashtable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import javax.imageio.ImageIO;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Response.Status;

import nl.uu.fi.dwo.rest.dom.entities.DomCourse;
import nl.uu.fi.dwo.rest.dom.entities.DomCourseStudent;
import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfile;
import nl.uu.fi.dwo.rest.entities.RestCourse;
import nl.uu.fi.dwo.rest.entities.RestDwoProfile;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.exceptions.Dwo2ExceptionCode;
import nl.uu.fi.dwo.rest.exceptions.Dwo2RestException;
import fi.beans.dwomaccess.JSONEncoder;
import fi.beans.private_base64code.StringCodeObject;
import fi.dwo.commons.persistence.MySQLPersistenceId;
import fi.dwo.commons.persistence.entities.PersistentCourse;
import fi.dwo.commons.persistence.entities.PersistentCourseData;
import fi.dwo.commons.persistence.entities.PersistentDwoProfile;
import fi.dwo.commons.persistence.entities.PersistentSchool;
import fi.dwo.server.PersistentDataManagers.core.CourseDataManager;
import fi.dwo.server.PersistentDataManagers.core.CourseManager;
import fi.dwo.server.PersistentDataManagers.core.DwoProfileManager;
import fi.dwo.server.rest.util.CourseBuilder;

/**
 * Handles the public registration of new users.
 *
 * @author G.A.J. van der Plas
 */
@Path("/public/course")
public class PublicCourseManager {
	private static final boolean SECURITY = true;
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
    public Response getCourseDescription(@DefaultValue("0") @QueryParam("courseId") Long courseId) {
        try {
            PersistentCourse course = CourseManager.findEntity(courseId);
            if(course == null) return Response.ok().entity("{}").build(); // Not fatal
            if (SECURITY) {
              if (course.getSchoolID() != null) {
                  return Response.ok().entity("{}").build();
              } else {
                PersistentDwoProfile profile = DwoProfileManager.findEntity(course.getDwoProfileID());
                if (profile.isLimited()) {
                  return Response.ok().entity("{}").build();               
                }
              }
            }
            PersistentCourseData data = CourseDataManager.findEntity(course.getCourseID());
            if (data != null) {
            	byte[] bytes = data.getDescriptionbytes();
            	if (bytes != null) {
	            	ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
	            	GZIPInputStream gis = new GZIPInputStream(bis);
	            	return Response.ok().encoding("UTF-8").entity(gis).build();
            	} else {
            		course.setDescription(data.getDescription());
            	}
            }
            
            Hashtable map = (Hashtable) StringCodeObject.decodeStringToObject(course.getDescription(), null); // FIXM load wiskopdr.jar
            StringWriter writer = new StringWriter();
			JSONEncoder.encode(map, writer, null); // FIXME, load wiskopdr.jar
	        String string = writer.toString();

	        if (data != null) {
	        	ByteArrayOutputStream bos = new ByteArrayOutputStream(string.length());
	        	GZIPOutputStream gos = new GZIPOutputStream(bos);
	        	OutputStreamWriter w = new OutputStreamWriter(gos, StandardCharsets.UTF_8);
	        	w.write(string);
	        	w.close();
	        	data.setDescriptionbytes(bos.toByteArray());
	        	data = CourseDataManager.edit(data);
	        }
			return Response.ok().entity(string).build();
		} catch (Exception e) {
			LOG.log(Level.WARNING, "getCourseDescription "  + courseId , e);
			return Response.noContent().build();
		}
    }
        
    static boolean visible(PersistentCourse c) {
      return ! c.isNotVisible() || ! c.isWithChildren() || c.getSchoolID() != null;
    }
    
    
    @PUT
    @Path("/getRoot")
    @Produces({"application/json"})
    public List<DomCourseStudent> getCourses(RestDwoProfile rest, @Context UriInfo info) 
    {
    	try {
   // TODO NPE tests 		    		
    		DomDwoProfile domDwoProfile = rest.getDomDwoProfile();
   // test for honest people
   // Security, only non limited profiles are public 		
    		Long id = MySQLPersistenceId.getNativeId(domDwoProfile);
    		PersistentDwoProfile profile = DwoProfileManager.findEntity(id);
if(SECURITY)
    		if ( profile.isLimited())
    		{
    			throwLoginNeeded();
    		}
    		
    		PersistentSchool school = new PersistentSchool(null);
    		List<PersistentCourse> courses = CourseManager.findChildrenOf(profile, school);
    		
    		Stream<PersistentCourse> stream = courses.stream();
    		stream = stream.filter(PublicCourseManager::visible);
    		String uri = info.getRequestUri().resolve("getImage").toString();
			Stream<DomCourseStudent> map = stream.map(new CourseBuilder(uri));
			map = map.sorted(DomCourseStudentComparator.INSTANCE);
    		return map.collect(Collectors.toList());
    	} catch (Dwo2RestException e) {
    		throw e;
    	} catch (Exception e) {
    		LOG.log(Level.WARNING, "getCourses", e);
    		throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, "An exception occured while fetching the modules.");	
    	}
    }

	private void throwLoginNeeded() {
		throw new Dwo2RestException(Dwo2ExceptionCode.Rest_LoginNeeded, "Login needed");
	}
    
    @PUT
    @Path("/getChildren")
    @Produces({"application/json"})
    public List<DomCourseStudent> getCourses(RestCourse rest, @Context UriInfo info) {
    	try {
    		DomCourse course = rest.getDomCourse();
    		long id = MySQLPersistenceId.getNativeId(course);
    		PersistentCourse parent = CourseManager.findEntity(id);
            if (parent == null) throw new Dwo2RestException(Dwo2ExceptionCode.Rest_ResourceNotFound, "not found");

    		// Verify parent is public and profile is not limited and hasChildren
    		PersistentDwoProfile profile = DwoProfileManager.findEntity(parent.getDwoProfileID());
if(SECURITY)
    		if ( parent.getSchoolID() != null || 
    			 profile.isLimited() ||
    			 !visible(parent)
// Verify context: profile matches...
//    			|| !rest.getDomDwoProfile().getId().equals(profile.buildPersistenceId())
    		)
    			throwLoginNeeded();
    			
    		List<PersistentCourse> courses = CourseManager.findChildrenOf(parent); 
    		final String PFX = info.getRequestUri().resolve("getImage").toString();
    		return courses.stream()
    		    .filter(PublicCourseManager::visible)
    		    .map(
    			new CourseBuilder(PFX)).sorted(DomCourseStudentComparator.INSTANCE).collect(Collectors.toList());
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
    		Long id = MySQLPersistenceId.getNativeId(course);
    		PersistentCourse parent = CourseManager.findEntity(id);
// Verify parent is public and profile is not limited
    		PersistentDwoProfile profile = DwoProfileManager.findEntity(parent.getDwoProfileID());
if(SECURITY) 
    		if ( parent.getSchoolID() != null || 
    		     !visible(parent) ||
    			 profile.isLimited()) 
    			throwLoginNeeded();
// TODO Verify context: profile matches...
    		if (!SECURITY || rest.getDomDwoProfile().getId().equals(profile.buildPersistenceId()))    		
    			return parent.buildDomCourseStudent();
    	} catch (Dwo2RestException e) {
    		throw e;
    	} catch (Exception e) {
    		LOG.log(Level.WARNING, "getCourses", e);
    	}
    	throw new Dwo2RestException(Dwo2ExceptionCode.Rest_ResourceNotFound, "not found");
    }
    
    @GET
    @Path("/getImage")
    @Produces({"image/png"})
    public Response getImage(@QueryParam("courseId") Long courseId, @QueryParam("hasRoleId") String hasRoleId) {
    	try {
    		PersistentCourse course = CourseManager.findEntity(courseId);
    		if(SECURITY && hasRoleId == null) {
        		PersistentDwoProfile profile = DwoProfileManager.findEntity(course.getDwoProfileID());
        		if(
        				course.getSchoolID() != null ||
        				profile.isLimited())
        		{
        			LOG.log(Level.WARNING, "Illegal access to " + courseId);
        			return Response.status(Status.NOT_FOUND).build();
        		}
    		
    		}
    		byte[] imageData = course.getImageData(); // NULL PointerException
    		BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageData));
    		ByteArrayOutputStream out = new ByteArrayOutputStream();
    		ImageIO.write(image, "png", out);
    		imageData = out.toByteArray();
    		return Response.ok(imageData, "image/png").build();    		
    	} catch(Exception e) {
    		LOG.log(Level.WARNING, "getImage error", e);
    	}
    	return Response.status(Status.NOT_FOUND).build();
    }  
    
    
    @PUT
    @Path("/getAll")
    @Produces("application/json")
    public List<DomCourse> getAll(RestDwoProfile rest) throws Dwo2Exception {
      Long profileID = MySQLPersistenceId.getNativeId(rest.getDomDwoProfile());
      PersistentDwoProfile profile = DwoProfileManager.findEntity(profileID);
      if ( profile.isLimited())
      {
          throwLoginNeeded();
      }
      List<PersistentCourse> list = CourseManager.findVisibleEntities(profileID);      
      return list.stream().map(PersistentCourse::buildDomCourse).collect(Collectors.toList());
    }
}
