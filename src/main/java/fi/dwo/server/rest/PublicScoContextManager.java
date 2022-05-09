package fi.dwo.server.rest;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Response.Status;

import fi.dwo.commons.persistence.MySQLPersistenceId;
import fi.dwo.commons.persistence.entities.PersistentCourse;
import fi.dwo.commons.persistence.entities.PersistentDwoProfile;
import fi.dwo.commons.persistence.entities.PersistentImage;
import fi.dwo.commons.persistence.entities.PersistentScoContext;
import fi.dwo.server.PersistentDataManagers.core.CourseManager;
import fi.dwo.server.PersistentDataManagers.core.DwoProfileManager;
import fi.dwo.server.PersistentDataManagers.core.ImageManager;
import fi.dwo.server.PersistentDataManagers.core.ScoContextManager;
import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfile;
import nl.uu.fi.dwo.rest.dom.entities.DomScoContext;
import nl.uu.fi.dwo.rest.entities.RestCourse;
import nl.uu.fi.dwo.rest.entities.RestScoContext;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.exceptions.Dwo2ExceptionCode;
import nl.uu.fi.dwo.rest.exceptions.Dwo2RestException;

@Path("/public/scoContext")
public class PublicScoContextManager {

	private static final Logger LOG = Logger.getLogger(PublicScoContextManager.class.getName());
	
	/** get scos of a course.
		If profile is Limited, no sco's
	 * @throws Dwo2Exception 
	*/
    @PUT
    @Path("/getScos")
    @Produces({"application/json"})
    public List<DomScoContext> getScos(RestCourse rest, @Context UriInfo info) throws Dwo2Exception {
// TODO NPE tests 		    		
		DomDwoProfile domDwoProfile = rest.getDomDwoProfile();
		Long dompid = MySQLPersistenceId.getNativeId(domDwoProfile);
		Long cid = MySQLPersistenceId.getNativeId(rest.getDomCourse());
		PersistentCourse parent = CourseManager.findEntity(cid);
		if (parent == null) throw new Dwo2Exception(Dwo2ExceptionCode.Rest_ResourceNotFound, cid + " not found");
// Security, only non limited profiles are public 		
		Long pid = parent.getDwoProfileID();
		PersistentDwoProfile profile = DwoProfileManager.findEntity(pid);
		if ( profile.isLimited())
			throwLoginNeeded();
// only public courses
		if ( parent.getSchoolID() != null)
			throwLoginNeeded();
// match course and profile
		if ( ! pid.equals(dompid))
		{	LOG.log(Level.SEVERE, "getScos profile mismatch: " + pid + "<>" + dompid);
			return Collections.emptyList();
		}
		
		List<PersistentScoContext> list = ScoContextManager.findEntities(parent);	
		return list.stream()
				.filter(PersistentScoContext::isOefenen)
				.map((s)->builder(s,parent, info)).sorted(new DomScoContextComparator()).collect(Collectors.toList());    	
    }

    private DomScoContext builder(PersistentScoContext s, PersistentCourse parent, UriInfo info) {
    	DomScoContext build = s.buildDomScoContext();
    	String pfx = info.getRequestUri().resolve("getImage").toString();

    	PersistentImage img = ImageManager.findEntity(s.getScoID());
    	
		if (img != null) {
			build.setImage(pfx + "?scoId=" + s.getScoID());
		} 
		return build;
    }
    
    
    @PUT
    @Path("/get")
    @Produces({"application/json"})
    public DomScoContext get(RestScoContext rest, @Context UriInfo info) throws Dwo2Exception {
// TODO NPE tests 		    		
		DomDwoProfile domDwoProfile = rest.getDomDwoProfile();
		Long pid = MySQLPersistenceId.getNativeId(domDwoProfile);
		{
// Security, only non limited profiles are public 		
		PersistentDwoProfile profile = DwoProfileManager.findEntity(pid);
		if (profile == null) throw new Dwo2Exception(Dwo2ExceptionCode.User_IllegalAction, "mismatch");
		if ( profile.isLimited())
			throwLoginNeeded();
		}
		Long id = MySQLPersistenceId.getNativeId(rest.getDomScoContext());
		PersistentScoContext scoContext = ScoContextManager.findEntity(id);
		if (scoContext == null) throw new Dwo2Exception(Dwo2ExceptionCode.Rest_ResourceNotFound, "not found");
		id = scoContext.getCourseID();
		PersistentCourse parent = CourseManager.findEntity(id);
		if ( parent.getSchoolID() != null)
			throwLoginNeeded();
		if ( !parent.getDwoProfileID().equals(pid))	// match profile and public school
		{
			LOG.log(Level.SEVERE, "get profile mismatch: " + pid + "<>" + parent.getDwoProfileID());
			throw new Dwo2Exception(Dwo2ExceptionCode.User_IllegalAction, "mismatch");
		}
	
		return builder(scoContext, parent, info);
    	
    }

    @GET
    @Path("/getImage")
    @Produces({"image/png"})
    public Response getImage(@QueryParam("scoId") Long scoId, @QueryParam("hasRoleId") String hasRoleId) {
    	try {
    		PersistentScoContext sco = ScoContextManager.findEntity(scoId);
    		PersistentImage pimage = ImageManager.findEntity(scoId);
    		PersistentCourse course =  CourseManager.findEntity(sco.getCourseID());
    		if(hasRoleId == null) {
        		PersistentDwoProfile profile = DwoProfileManager.findEntity(course.getDwoProfileID());
        		if(
        				course.getSchoolID() != null ||
        				profile.isLimited())
        		{
        			LOG.log(Level.WARNING, "Illegal access to " + scoId);
        			return Response.status(Status.NOT_FOUND).build();
        		}
    		
    		}
    		byte[] imageData = 
    				pimage != null ? pimage.getImage() :
    				course.getImageData();
    		BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageData));
    		ByteArrayOutputStream out = new ByteArrayOutputStream();
    		ImageIO.write(image, "png", out);
    		imageData = out.toByteArray();
    		return Response.ok(imageData, "image/png").build();    		
    	} catch(Exception e) {
    		LOG.log(Level.SEVERE, "getImage error", e);
    	}
    	return Response.status(Status.NOT_FOUND).build();
    }    

    private void throwLoginNeeded() {
		throw new Dwo2RestException(Dwo2ExceptionCode.Rest_LoginNeeded, "Login needed");
	}

}
