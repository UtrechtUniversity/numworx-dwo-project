package fi.dwo.server.rest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPOutputStream;

import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfileFull;
import nl.uu.fi.dwo.rest.entities.RestDwoProfile;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import fi.beans.dwomaccess.JSONEncoder;
import fi.beans.private_base64code.StringCodeObject;
import fi.dwo.commons.persistence.MySQLPersistenceId;
import fi.dwo.commons.persistence.entities.PersistentDwoProfile;
import fi.dwo.server.PersistentDataManagers.core.CourseDataManager;
import fi.dwo.server.PersistentDataManagers.core.DwoProfileManager;

@Path("/public/profile")
public class PublicProfileManager {
	private static final Logger LOG = Logger.getLogger(PublicProfileManager.class.getName());

	@GET
	@Path("/{id}")
	@Produces({"application/json"})
	public DomDwoProfileFull get( @PathParam("id") String id ) {
		PersistentDwoProfile profile;
		profile = DwoProfileManager.findEntity(id);
		if(profile == null) 
		try {
			profile = DwoProfileManager.findEntity(Long.valueOf(id));
		} catch(NumberFormatException e) {
			LOG.log(Level.WARNING, "parse " + id, e);
		}
		if (profile != null) 
			return profile.buildDomDwoProfileFull();
		else {
			LOG.severe("Profile not found for " + id);
		}
		return null;
	}
	
	@PUT
	@Path("/description")
	@Produces({MediaType.APPLICATION_JSON})
	public Response getDescription(RestDwoProfile rest) throws Dwo2Exception, IOException {
     try {
		Long id = MySQLPersistenceId.getNativeId(rest.getDomDwoProfile());
		PersistentDwoProfile profile = DwoProfileManager.findEntity(id);
        if(profile == null) return Response.ok().entity("{}").build(); // Not fatal
        Hashtable map = (Hashtable) StringCodeObject.decodeStringToObject(profile.getDwoProfileText(), null); // FIXM load wiskopdr.jar
        StringWriter writer = new StringWriter();
		JSONEncoder.encode(map, writer, null); // FIXME, load wiskopdr.jar
        String string = writer.toString();

//        if (data != null) {
//        	ByteArrayOutputStream bos = new ByteArrayOutputStream(string.length());
//        	GZIPOutputStream gos = new GZIPOutputStream(bos);
//        	OutputStreamWriter w = new OutputStreamWriter(gos, StandardCharsets.UTF_8);
//        	w.write(string);
//        	w.close();
//        	data.setDescriptionbytes(bos.toByteArray());
//        	data = CourseDataManager.edit(data);
//        }
		return Response.ok().entity(string).build();
     } catch(Exception oops) {
    	 LOG.log(Level.WARNING, "get profile description", oops);
    	 return Response.noContent().build();
     }
		
	}
	
}
