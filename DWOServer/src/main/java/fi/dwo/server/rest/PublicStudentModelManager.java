package fi.dwo.server.rest;

import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Response.Status;

import fi.dwo.commons.persistence.MySQLPersistenceId;
import fi.dwo.commons.persistence.entities.PersistentStudentModelContext;
import fi.dwo.commons.persistence.entities.PersistentUser;
import fi.dwo.server.PersistentDataManagers.access.AnonDomainAuthorizer;
import fi.dwo.server.PersistentDataManagers.access.AnonDomainAuthorizer.AnonState;
import fi.dwo.server.PersistentDataManagers.access.TeacherDomainAuthorizer.TeacherState_HR_R_S_SG_U;
import fi.dwo.server.PersistentDataManagers.core.StudentModelContextManager;
import fi.dwo.server.PersistentDataManagers.util.StudentModelContextUtilManager;
import nl.uu.fi.dwo.rest.dom.entities.DomHasRole;
import nl.uu.fi.dwo.rest.dom.entities.DomLRS;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContext;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContextId;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelStructure;
import nl.uu.fi.dwo.rest.dom.xapi.Account;
import nl.uu.fi.dwo.rest.dom.xapi.Agent;
import nl.uu.fi.dwo.rest.entities.RestContext;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.exceptions.Dwo2ExceptionCode;
import nl.uu.fi.dwo.rest.exceptions.Dwo2RestException;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;

@Path("/public/studentmodel")
public class PublicStudentModelManager {
 
	private static final Logger LOG = Logger.getLogger(PublicStudentModelManager.class.getName());

	@PUT
    @Produces("application/json")
    @Path("/getLRS") 
    public DomLRS getLRS(@Context UriInfo info, RestContext rest) throws Dwo2Exception {
	  AnonState state = AnonDomainAuthorizer.build();
          
      //return state.getLRS(info);
      
      Agent agent = new Agent();
      agent.name = "anonymous";
      agent.account = new Account();
      agent.account.homePage = info.getBaseUri().toASCIIString();
      agent.account.name = "pid:" + PersistentUser.buildPersistenceId(0L);   
      DomLRS lrs = new DomLRS();
      lrs.setAgent(agent);
      String endpoint = System.getProperty("XAPI_ENDPOINT", "/data/xAPI/");
      lrs.setEndpoint(endpoint);
      // school afhankelijk?
      String auth = System.getProperty("XAPI_AUTH", "ODg5MTZhOWRiNTI1YTM0NDRkZmE0MzliZjMxMDc5NTAzZDcyZDUyODpjYzY3YzA2Zjc3MDFhMDgzY2I2MzBhZGYyMDhjMjQ3YmYyMzhhODQz");
      lrs.setAuth("Basic " + auth);
      return lrs;
    }
	@GET
	@Produces("test/css")
	@Path("getCSS")
	public Response getCSS(
			@QueryParam("id") String uuid, @QueryParam("modelId") String modelid,
			@QueryParam("locale") String locale
	) {
		try {
			DomStudentModelContextId did = new DomStudentModelContextId(new PersistenceId(modelid));
			Long id = MySQLPersistenceId.getNativeId(did);
	        PersistentStudentModelContext pModel = StudentModelContextManager.findEntity(id);
	        if ( pModel == null) {
	          throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, "Illegal operation");
	        }
	        StudentModelContextUtilManager.merge(pModel);		
			DomStudentModelContext result = pModel.buildDomStudentModelContext();
			DomStudentModelStructure struct = result.getModelStructure();
			String obj = SecuredStudentStudentModelManager.getStructStyle(struct, uuid, locale);
			CacheControl cc = new CacheControl();
			cc.setMaxAge(3600);
			Date expiry = new  Date(System.currentTimeMillis()+1000*cc.getMaxAge());
			Date last = null;
			if ( null != result.getLastChangeTimeStamp())
				last = new Date(result.getLastChangeTimeStamp().longValue());
			
			return Response.ok(obj)
					.cacheControl(cc)
					.expires(expiry)
					.lastModified(last)
					.build();
		} catch (Dwo2Exception e) {
			LOG.log(Level.WARNING, "return not found", e);
			return Response.status(Status.NOT_FOUND).build();
		}
	}

}
