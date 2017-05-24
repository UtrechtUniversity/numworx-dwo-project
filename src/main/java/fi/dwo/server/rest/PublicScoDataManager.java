package fi.dwo.server.rest;

import fi.beans.dwomaccess.JSONEncoder;
import fi.beans.private_base64code.StringCodeObject;
import nl.uu.fi.dwo.rest.exceptions.Dwo2ExceptionCode;
import nl.uu.fi.dwo.rest.exceptions.Dwo2RestException;
import fi.dwo.commons.persistence.entities.PersistentScoData;
import fi.dwo.server.PersistentDataManagers.core.ScoDataManager;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

/**
 * Handles the public registration of new users.
 *
 * @author G.A.J. van der Plas
 */
@Path("/public/scoData")
public class PublicScoDataManager {

    private static final Logger LOG = Logger.getLogger(PublicScoDataManager.class.getName());

    /**
     * Returns the JSON launch data bytes of scoData. This method uses MySQL-based
     * indices and should be phased out.
     *
     * @param scoId
     * @return
     */
    @GET
//    @Produces({"text/plain"})
    @Produces({"application/json"})    
    @Path("/getJSONLaunchDataBytes")
    @Deprecated
    public String getJSONLaunchDataBytes(@DefaultValue("0") @QueryParam("scoId") Long scoId) {

            PersistentScoData scoData = ScoDataManager.findEntity(scoId);
            if(scoData == null) {
            	return "{}"; // Not found, not fatal
            }
            byte[] launchData = scoData.getLaunchdatabytes();
            if(launchData != null)
            {  
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
	            }
	            catch (IOException ex) {
	                LOG.log(Level.SEVERE, "Error while unzipping launchdata with scoid " + scoId + ".", ex);
	            }
            }
// The slow conversion, if bytes are missing.     
            try {
                Hashtable map = (Hashtable) StringCodeObject.decodeStringToObject(scoData.getLaunchdata(), null);
                StringWriter writer = new StringWriter();
    			JSONEncoder.encode(map, writer, null); // FIXME zie DWOmAccess voor loader with wiskopdr.jar
    	        return writer.toString();
            } catch(Exception ex) {
            	LOG.log(Level.SEVERE, "Error while decoding launchdata with scoid " + scoId + ".", ex);
            }
            throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, "Error with launchdata with scoid " + scoId + ".");
        }
    }
