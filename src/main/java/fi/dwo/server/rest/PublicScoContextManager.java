package fi.dwo.server.rest;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import fi.dwo.commons.persistence.MySQLPersistenceId;
import fi.dwo.commons.persistence.entities.PersistentCourse;
import fi.dwo.commons.persistence.entities.PersistentDwoProfile;
import fi.dwo.commons.persistence.entities.PersistentScoContext;
import fi.dwo.server.PersistentDataManagers.core.CourseManager;
import fi.dwo.server.PersistentDataManagers.core.DwoProfileManager;
import fi.dwo.server.PersistentDataManagers.core.ScoContextManager;
import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfile;
import nl.uu.fi.dwo.rest.dom.entities.DomScoContext;
import nl.uu.fi.dwo.rest.entities.RestCourse;
import nl.uu.fi.dwo.rest.entities.RestScoContext;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;

@Path("/public/scoContext")
public class PublicScoContextManager {
	
	private String LIMITED = "l";
	
	/** get scos of a course.
		If profile is Limited, no sco's
	 * @throws Dwo2Exception 
	*/
    @PUT
    @Path("/getScos")
    @Produces({"application/json"})
    public List<DomScoContext> getScos(RestCourse rest) throws Dwo2Exception {
// TODO NPE tests 		    		
		DomDwoProfile domDwoProfile = rest.getDomDwoProfile();
// test for honest people
		if (domDwoProfile.getDwoProfileRights() != null && 
				domDwoProfile.getDwoProfileRights().contains(LIMITED))
			return Collections.emptyList();
// Security, only non limited profiles are public 		
		long pid = MySQLPersistenceId.getNativeId(domDwoProfile);
		PersistentDwoProfile profile = DwoProfileManager.findEntity(pid);
		if ( profile.getDwoProfileRights().contains(LIMITED))
			return Collections.emptyList();
		long cid = MySQLPersistenceId.getNativeId(rest.getDomCourse());
		PersistentCourse parent = CourseManager.findEntity(cid);
		if ( pid != parent.getDwoProfileID()				// match profile and public school
				|| parent.getSchoolID() != null)
		return Collections.emptyList();
		List<PersistentScoContext> list = ScoContextManager.findEntities(parent);
		
		return list.stream().map((s)->s.buildDomScoContext()).sorted(new DomScoContextComparator()).collect(Collectors.toList());    	
    }

    public DomScoContext get(RestScoContext rest) throws Dwo2Exception {
// TODO NPE tests 		    		
		DomDwoProfile domDwoProfile = rest.getDomDwoProfile();
// test for honest people
		if (domDwoProfile.getDwoProfileRights() != null && 
				domDwoProfile.getDwoProfileRights().contains(LIMITED))
			return null;
// Security, only non limited profiles are public 		
		long id = MySQLPersistenceId.getNativeId(domDwoProfile);
		PersistentDwoProfile profile = DwoProfileManager.findEntity(id);
		if ( profile.getDwoProfileRights().contains(LIMITED))
			return null;
		id = MySQLPersistenceId.getNativeId(rest.getDomScoContext());
		PersistentScoContext scoContext = ScoContextManager.findEntity(id);
		id = scoContext.getCourseID();
		PersistentCourse parent = CourseManager.findEntity(id);
		if ( id != parent.getDwoProfileID()				// match profile and public school
				|| parent.getSchoolID() != null)
			return null;
	
		return scoContext.buildDomScoContext();
    	
    }
    
}
