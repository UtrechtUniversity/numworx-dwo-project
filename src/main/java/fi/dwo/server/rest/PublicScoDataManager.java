package fi.dwo.server.rest;

import fi.dwo.commons.exceptions.Dwo2ExceptionCode;
import fi.dwo.commons.exceptions.Dwo2RestException;
import fi.dwo.commons.persistence.entities.PersistentScoData;
import fi.dwo.server.PersistentDataManagers.core.ScoDataManager;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;
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
    public String getJSONLaunchDataBytes(@QueryParam("scoId") int scoId) {

            PersistentScoData scoData = ScoDataManager.findEntity(Integer.valueOf(scoId).longValue());
            byte[] launchData = scoData.getLaunchdatabytes();

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
            throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, "Error while unzipping launchdata with scoid " + scoId + ".");
        }
    }
