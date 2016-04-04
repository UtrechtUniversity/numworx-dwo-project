package fi.dwo.server.rest;

import fi.beans.base64code.StringCodeObject;
import fi.dwo.commons.exceptions.Dwo2ExceptionCode;
import fi.dwo.commons.exceptions.Dwo2RestException;
import fi.dwo.commons.persistence.entities.PersistentCourse;
import fi.dwo.server.PersistentDataManagers.core.CourseManager;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Hashtable;
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
     * Verifies that a user, password combination. Waits a configured amount of
     * time before giving a response.
     *
     * @param loginCheck
     * @return
     */
    @GET
    @Produces({"application/json"})
    @Path("/getCourseDescription")
    @Deprecated
    public Hashtable getCourseDescription(@QueryParam("courseId") int courseId) {

        PersistentCourse course = CourseManager.findEntity(Integer.valueOf(courseId).longValue());
        Hashtable map = (Hashtable) StringCodeObject.decodeStringToObject(course.getDescription());
        return map;
    }
}
