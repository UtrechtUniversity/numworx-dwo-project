package fi.dwo.server.PersistentDataManagers.core;

import java.io.StringReader;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.ws.rs.core.UriInfo;

import fi.dwo.commons.persistence.entities.PersistentSchool;
import fi.dwo.commons.persistence.entities.PersistentSchoolData;
import fi.dwo.commons.persistence.entities.PersistentUser;
import fi.dwo.server.PersistentDataManagers.util.SchoolDataUtilManager;
import nl.uu.fi.dwo.rest.dom.entities.DomLRS;
import nl.uu.fi.dwo.rest.dom.entities.util.AboType;
import nl.uu.fi.dwo.rest.dom.xapi.Account;
import nl.uu.fi.dwo.rest.dom.xapi.Agent;

public class XapiManager {

  private XapiManager() {
  }

  public static DomLRS getLRS(PersistentUser user, PersistentSchool school, UriInfo info) {
    Agent agent = new Agent();
    agent.name = user.getUsername();
    agent.account = new Account();
    agent.account.homePage = info.getBaseUri().toASCIIString();
    agent.account.name = "pid:" + user.buildPersistenceId().getIdString();    
    DomLRS lrs = new DomLRS();
    lrs.setAgent(agent);
    String endpoint = System.getProperty("XAPI_ENDPOINT", "/data/xAPI/");
    lrs.setEndpoint(endpoint);
    if (school.getAboType() != AboType.premium) return null;
    // school afhankelijk?
    String auth = System.getProperty("XAPI_AUTH", "ODg5MTZhOWRiNTI1YTM0NDRkZmE0MzliZjMxMDc5NTAzZDcyZDUyODpjYzY3YzA2Zjc3MDFhMDgzY2I2MzBhZGYyMDhjMjQ3YmYyMzhhODQz");
    lrs.setAuth("Basic " + auth);

    PersistentSchoolData data = SchoolDataUtilManager.find(school);
    if (data != null) {
    	JsonReader reader = Json.createReader(new StringReader(data.getSchoolData()));
    	JsonObject object = reader.readObject();
    	reader.close();
    	auth = object.getString("XAPI-Authorization", lrs.getAuth());
    	lrs.setAuth(auth);
    	endpoint = object.getString("XAPI-Endpoint", endpoint);
    	lrs.setEndpoint(endpoint);
    	agent.account.homePage = object.getString("XAPI-HomePage", agent.account.homePage);
    }
    
    
    return lrs;
  }

}
