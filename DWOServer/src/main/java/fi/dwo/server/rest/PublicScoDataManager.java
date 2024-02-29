package fi.dwo.server.rest;

import fi.beans.css.StateToCss;
import fi.beans.dwomaccess.JSONEncoder;
import fi.beans.private_base64code.StringCodeObject;
import nl.uu.fi.dwo.rest.exceptions.Dwo2ExceptionCode;
import nl.uu.fi.dwo.rest.exceptions.Dwo2RestException;
import fi.dwo.commons.persistence.entities.PersistentDwoProfile;
import fi.dwo.commons.persistence.entities.PersistentScoContext;
import fi.dwo.commons.persistence.entities.PersistentScoData;
import fi.dwo.server.PersistentDataManagers.core.DwoProfileManager;
import fi.dwo.server.PersistentDataManagers.core.ScoContextManager;
import fi.dwo.server.PersistentDataManagers.core.ScoDataManager;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.util.Date;
import java.util.Hashtable;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Response;

import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * Handles the public registration of new users.
 *
 * @author G.A.J. van der Plas
 */
@Path("/public/scoData")
public class PublicScoDataManager {

  private static final Logger LOG = Logger.getLogger(PublicScoDataManager.class.getName());
  private static final boolean SECURITY = true;
  private void throwLoginNeeded() {
    throw new Dwo2RestException(Dwo2ExceptionCode.Rest_LoginNeeded, "Login needed");
}

  /**
   * Returns the JSON launch data bytes of scoData. This method uses MySQL-based indices and should
   * be phased out.
   *
   * @param scoId
   * @return
   */
  @GET
  @Produces({"application/json"})
  @Path("/getJSONLaunchDataBytes")
  @Deprecated
  public Response getJSONLaunchDataBytes(@DefaultValue("0") @QueryParam("scoId") Long scoId) {

    PersistentScoData scoData = ScoDataManager.findEntity(scoId);
    if (scoData == null) {
      return Response.ok("{}","application/json").build(); // Not found, not fatal
    }
    Date last = new Date(scoData.getLastChangeTimeStamp());
    CacheControl cc = new CacheControl();
    cc.setMaxAge(3600);
    if (SECURITY) {
      PersistentScoContext scoContext = ScoContextManager.findEntity(scoId);
      if (scoContext.getSchoolID() != null)
        throwLoginNeeded();
      PersistentDwoProfile profile = DwoProfileManager.findEntity(scoContext.getDwoProfileID());
      if ( profile.isLimited())
        throwLoginNeeded();      
    }
    byte[] launchData = scoData.getLaunchdatabytes();
    if (launchData != null) {
      try {
        ByteArrayInputStream inStream = new ByteArrayInputStream(launchData);
        GZIPInputStream gzIn = new GZIPInputStream(inStream);
        return Response.ok(gzIn, "application/json")
        		.lastModified(last)
        		.cacheControl(cc)
        		.expires(new Date(System.currentTimeMillis()+1000*60*10))
        		.build();
      } catch (IOException ex) {
        LOG.log(Level.SEVERE, "Error while unzipping launchdata with scoid " + scoId + ".", ex);
      }
    }
    // The slow conversion, if bytes are missing.
    try {
      Hashtable map =
          (Hashtable) StringCodeObject.decodeStringToObject(scoData.getLaunchdata(), null);
      StringWriter writer = new StringWriter();
      JSONEncoder.encode(map, writer, null); // FIXME zie DWOmAccess voor loader with wiskopdr.jar
      return Response.ok(writer.toString(), "application/json")
    		  .lastModified(last)
    		  .cacheControl(cc)
      		  .expires(new Date(System.currentTimeMillis()+1000*60*10))
    		  .build();
    } catch (Exception ex) {
      LOG.log(Level.SEVERE, "Error while decoding launchdata with scoid " + scoId + ".", ex);
    }
    throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError,
        "Error with launchdata with scoid " + scoId + ".");
  }

  @GET
  @Produces("text/css")
  @Path("get/{scoId}/style.css")
  public Response getCss(@PathParam("scoId") Long scoId) throws IOException, ParseException {
    String something = "";
    Date last = null;
    PersistentScoData scoData = ScoDataManager.findEntity(scoId);
    if (scoData != null) {
      Map map;
      byte[] launchData = scoData.getLaunchdatabytes();
      if (launchData != null) {
        ByteArrayInputStream inStream = new ByteArrayInputStream(launchData);
        GZIPInputStream gzIn = new GZIPInputStream(inStream);
        Reader reader = new InputStreamReader(gzIn, "UTF-8");
        JSONParser parser = new JSONParser();
        map = (Map) parser.parse(reader);
      } else {
        map = (Map) StringCodeObject.decodeStringToObject(scoData.getLaunchdata(), null);
      }
      something = StateToCss.createCssFromInstellingen(map, null);
      last = new Date(scoData.getLastChangeTimeStamp());
    }
    CacheControl cc = new CacheControl();
    cc.setMaxAge(3600);
	return Response.ok()
    		.lastModified(last)
    		.expires(new Date(System.currentTimeMillis()+1000*cc.getMaxAge()))
    		.cacheControl(cc)
    		.type("text/css")
    		.entity("/*" + scoId + "*/\n" + something)
    		.build();
  }
  
}
