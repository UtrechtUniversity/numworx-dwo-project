package fi.dwo.server.rest;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfileFull;
import fi.dwo.commons.persistence.entities.PersistentDwoProfile;
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
	
}
