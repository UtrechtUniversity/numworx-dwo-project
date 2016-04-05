package fi.dwo.server.rest;

import fi.beans.base64code.StringCodeObject;
import fi.beans.dwomaccess.JSONEncoder;
import fi.dwo.commons.exceptions.Dwo2ExceptionCode;
import fi.dwo.commons.exceptions.Dwo2RestException;
import fi.dwo.commons.persistence.entities.PersistentCourse;
import fi.dwo.server.PersistentDataManagers.core.CourseManager;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Hashtable;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

/**
 * Handles the public registration of new users.
 *
 * @author G.A.J. van der Plas
 */
@Path("/public/course")
public class PublicCourseManager {

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
    public String getCourseDescription(@QueryParam("courseId") int courseId) {
        try {
            PersistentCourse course = CourseManager.findEntity(Long.valueOf(courseId));
            Hashtable map = (Hashtable) StringCodeObject.decodeStringToObject(course.getDescription());
            StringWriter writer = new StringWriter();
			JSONEncoder.encode(map, writer);
	        return writer.toString();
		} catch (Exception e) {
			LOG.fine("getCourseDescription "  + courseId + " " + e.toString());
			return "{}";
		}
    }
}
