package fi.dwo.server.rest;

import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

import fi.dwo.commons.persistence.entities.PersistentUser;
import fi.dwo.server.PersistentDataManagers.access.AnonDomainAuthorizer;
import fi.dwo.server.PersistentDataManagers.access.AnonDomainAuthorizer.AnonState;
import nl.uu.fi.dwo.rest.dom.entities.DomLRS;
import nl.uu.fi.dwo.rest.dom.xapi.Account;
import nl.uu.fi.dwo.rest.dom.xapi.Agent;
import nl.uu.fi.dwo.rest.entities.RestContext;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;

@Path("/public/studentmodel")
public class PublicStudentModelManager {
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

}
