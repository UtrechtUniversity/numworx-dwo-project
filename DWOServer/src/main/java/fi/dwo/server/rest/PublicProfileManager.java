package fi.dwo.server.rest;

import java.io.IOException;
import java.io.StringWriter;
import java.sql.Date;
import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import nl.uu.fi.dwo.lms.jclient.lib.rest.cache.PublicProfileCache;
import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfileFull;
import nl.uu.fi.dwo.rest.entities.RestDwoProfile;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import fi.beans.dwomaccess.JSONEncoder;
import fi.beans.private_base64code.StringCodeObject;
import fi.dwo.commons.persistence.MySQLPersistenceId;
import fi.dwo.commons.persistence.entities.PersistentDwoProfile;
import fi.dwo.server.PersistentDataManagers.core.DwoProfileManager;

@Path("/public/profile")
public class PublicProfileManager {
	private static final Logger LOG = Logger.getLogger(PublicProfileManager.class.getName());

	@GET
	@Path("")
	public Response getquery(@QueryParam("id") String id) {
		return get(id);
	}
	
	
	@GET
	@Path("/{id}")
	@Produces({"application/json"})
	public Response get( @PathParam("id") String id ) {
		CacheControl cc = new CacheControl(); cc.setMaxAge(600);
		DomDwoProfileFull result = PublicProfileCache.getFromCache(id);
		if (result != null) return Response.ok(result)
				.expires(new Date(System.currentTimeMillis()+1000*cc.getMaxAge()))
				.cacheControl(cc)
				.build();
		
		PersistentDwoProfile profile;
		profile = DwoProfileManager.findEntity(id);
		if(profile == null) 
		try {
			profile = DwoProfileManager.findEntity(Long.valueOf(id));
		} catch(NumberFormatException e) {
			LOG.log(Level.INFO, "parse " + id + " " + e);
		}
		if (profile != null) 
			return Response.ok(profile.buildDomDwoProfileFull())
					.expires(new Date(System.currentTimeMillis()+1000*cc.getMaxAge()))
					.cacheControl(cc)
					.build();
		else {
			LOG.severe("Profile not found for " + id);
		}
		return Response.status(Status.NOT_FOUND).build();
	}
	
	@GET
	@Path("/description")
	@Produces({MediaType.APPLICATION_JSON})
	public Response getDescription(@QueryParam("id") Long id) throws Dwo2Exception, IOException {
	  try {
		  CacheControl cc = new CacheControl();		  
		  cc.setMaxAge(600);
		  DomDwoProfileFull dom;
		  dom = PublicProfileCache.get(id.toString());
		  String text;
		  if (dom != null) {
			  text = dom.getDwoProfileText();
		  } else {
				PersistentDwoProfile profile = DwoProfileManager.findEntity(id);
				if(profile == null) return Response.ok().entity("{}").build(); // Not fatal
				text = profile.getDwoProfileText();
		  }
	        Hashtable map = (Hashtable) StringCodeObject.decodeStringToObject(text, null); // FIXME load wiskopdr.jar
	        StringWriter writer = new StringWriter();
			JSONEncoder.encode(map, writer, null); // FIXME, load wiskopdr.jar
	        String string = writer.toString();
			return Response.ok()
					.cacheControl(cc)
					.expires(new Date(System.currentTimeMillis()+cc.getMaxAge()))
					.entity(string)
					.build();
	  } catch(Exception oops) {
	    LOG.log(Level.WARNING, "get profile description", oops);
	    return Response.noContent().build();
	  }
	}
	
	@PUT
	@Path("/description")
	@Produces({MediaType.APPLICATION_JSON})
	public Response getDescription(RestDwoProfile rest) throws Dwo2Exception, IOException {
     try {
		Long id = MySQLPersistenceId.getNativeId(rest.getDomDwoProfile());
		return getDescription(id);
    } catch(Exception oops) {
    	 LOG.log(Level.WARNING, "get profile description", oops);
    	 return Response.noContent().build();
     }
		
	}
	
}
