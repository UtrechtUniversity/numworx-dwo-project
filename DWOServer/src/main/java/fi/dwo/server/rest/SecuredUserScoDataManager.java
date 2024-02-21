package fi.dwo.server.rest;

import java.security.Principal;
import java.util.Date;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import fi.dwo.commons.persistence.entities.PersistentClassCourse;
import fi.dwo.commons.persistence.entities.PersistentDwoProfile;
import fi.dwo.commons.persistence.entities.PersistentSchoolClass;
import fi.dwo.commons.persistence.entities.PersistentScoContext;
import fi.dwo.commons.persistence.entities.PersistentScoData;
import fi.dwo.server.PersistentDataManagers.core.ScoDataManager;
import fi.dwo.server.rest.jaxrsfilters.DwoUserPrincipal;
import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfile;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClassId;
import nl.uu.fi.dwo.rest.dom.entities.DomScoContext;
import nl.uu.fi.dwo.rest.entities.RestContext;
import nl.uu.fi.dwo.rest.entities.RestScoContext;
import nl.uu.fi.dwo.rest.entities.RestScormValues;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;

@Path("/secure/user/scoData")
public class SecuredUserScoDataManager extends SecuredCommonScoDataManager {
    private static final Logger LOG = Logger.getLogger(SecuredUserScoDataManager.class.getName());

    @PUT
    @Produces({"application/json"})    
    @Path("/getJSONLaunchDataBytes")
    public String getJSONLaunchDataBytes(@Context SecurityContext sc, RestScoContext rest) throws Dwo2Exception {
      return super.getJSONLaunchDataBytes(sc, rest);
    }
    
    @GET
    @Produces({"application/json"})    
    @Path("/getJSONLaunchDataBytes")
    public Response GETjsonLaunchDataBytes(@Context SecurityContext sc,
    		@HeaderParam(HttpHeaders.IF_MODIFIED_SINCE) Date since,
    		@QueryParam("scoId") Long scoId,
    		@QueryParam("profileId") Long profileId,
    		@QueryParam("classId") Long classId
    		) throws Dwo2Exception {
        CacheControl cc = new CacheControl();
        cc.setMaxAge(3600);
    	RestScoContext rest = new RestScoContext();
    	rest.setRestContext(new DomContext());
    	DomScoContext sco = new DomScoContext(); 
    	sco.setId(PersistentScoContext.buildPersistenceId(scoId));
        Date last = new Date(); // niet goed!
        PersistentScoData data = ScoDataManager.findEntity(scoId);
        if (data != null) {
        	last = new Date(data.getLastChangeTimeStamp()); // wel goed
        	if (since != null && !since.before(last)) {
        		return Response.status(HttpServletResponse.SC_NOT_MODIFIED).build();
        	}
        }    	
    	DomDwoProfile profile = new DomDwoProfile();
    	profile.setId(PersistentDwoProfile.buildPersistenceId(profileId));
    	DomSchoolClassId schoolclass = null;
    	if (classId != null) schoolclass = new DomSchoolClassId(PersistentSchoolClass.buildPersistenceId(classId));
    	Principal p = sc.getUserPrincipal();
    	if (p instanceof DwoUserPrincipal) {
    		DwoUserPrincipal u = (DwoUserPrincipal) p;   		
    		rest.getRestContext().setDomHasRole(u.getHr().buildDomHasRole());   		
    	}
    	rest.setDomScoContext(sco);
    	rest.setDomDwoProfile(profile);
    	rest.setSchoolClassID(schoolclass);
    	String result = getJSONLaunchDataBytes(sc, rest);
        return Response.ok(result, "application/json")
      		  .lastModified(last)
      		  .cacheControl(cc)
        	  .expires(new Date(System.currentTimeMillis()+1000*cc.getMaxAge()))
      		  .build();
	
    }

    @PUT
    @Produces({"application/json"})
    @Path("/getValues")
    public Response getValues(@Context SecurityContext sc, RestScormValues rest) throws Dwo2Exception {
      return super.getValues(sc, rest);
    }

    @PUT
    @Produces({"application/json"})
    @Path("/setValues")
    public Response setValues(@Context SecurityContext sc, RestScormValues rest, @HeaderParam("if-match") EntityTag match) throws Dwo2Exception {
      return super.setValues(sc, rest, match);
  }
	
	@PUT
	@Produces("application/json")
	@Path("/patchValues")
	public Response patchValues(@Context SecurityContext sc, RestScormValues rest, @HeaderParam("if-match") String match) throws Dwo2Exception {
	  return super.patchValues(sc, rest, match);
	}

	final static Integer NORMAL = Integer.valueOf(0);

	@Override
	boolean checkType(PersistentClassCourse pcc) {
		return NORMAL.equals(pcc.getType());
	}

}
