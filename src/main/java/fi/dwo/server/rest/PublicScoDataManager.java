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
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.util.Hashtable;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.stream.JsonParser;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

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
  public String getJSONLaunchDataBytes(@DefaultValue("0") @QueryParam("scoId") Long scoId) {

    PersistentScoData scoData = ScoDataManager.findEntity(scoId);
    if (scoData == null) {
      return "{}"; // Not found, not fatal
    }
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
      byte[] buffer = new byte[1024];
      try {
        ByteArrayInputStream inStream = new ByteArrayInputStream(launchData);
        ByteArrayOutputStream outStream = new ByteArrayOutputStream(launchData.length);
        GZIPInputStream gzIn = new GZIPInputStream(inStream);

        int len;
        while ((len = gzIn.read(buffer)) > 0) {
          outStream.write(buffer, 0, len);
        }

        gzIn.close();
        outStream.close();

        return outStream.toString("UTF-8");
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
      return writer.toString();
    } catch (Exception ex) {
      LOG.log(Level.SEVERE, "Error while decoding launchdata with scoid " + scoId + ".", ex);
    }
    throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError,
        "Error with launchdata with scoid " + scoId + ".");
  }

  @GET
  @Produces("text/css")
  @Path("get/{scoId}/style.css")
  public String getCss(@PathParam("scoId") Long scoId) throws IOException, ParseException {
    String something = "";
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
    }
    return "/*" + scoId + "*/\n" + something;
  }
  
}
