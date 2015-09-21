
package fi.dwo.server.rest;

import fi.dwo.commons.exceptions.Dwo2ExceptionCode;
import fi.dwo.commons.exceptions.Dwo2RestException;
import fi.dwo.commons.persistence.entities.PersistentDwoSystemParameters;
import fi.dwo.server.PersistentEntityManagers.DwoSystemParametersManager;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

/**
 * Public server status. Showing health of the service. Under development.
 *
 * @author G.A.J. van der Plas
 */
@Path("/public/systemparam")
public class PublicServerStatus {

    private static final Logger LOG = Logger.getLogger(PublicServerStatus.class.getName());

    public List<PersistentDwoSystemParameters> getStatus() {
        List<PersistentDwoSystemParameters> result;
        try {
            result = DwoSystemParametersManager.findEntities();
            LOG.log(Level.FINER, "Fetched DwoSystemParameters {0}", new Object[]{result.size()});
        }
        catch (Exception e) {
            LOG.log(Level.SEVERE, "Can't query the DwoSystemParameters", e);
            throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, "An exception occured while fetching the DwoSystemParameters.");
        }

        StringBuilder string = new StringBuilder();
        for (PersistentDwoSystemParameters p : result) {
            string.append(p.getName());
            string.append(" ");
            string.append(p.getValue());
            string.append("\n");
        }
        LOG.log(Level.FINER, "Made output:", new Object[]{string.toString()});

        return result;
    }

    @GET
    @Produces({"application/json"})
    @Path("/getlist")
    public List<PersistentDwoSystemParameters> getStatusJson() {
        return getStatus();
    }

    @GET
    @Produces({"text/plain"})
    @Path("/get/html")
    public String getStatusText() {
        List<PersistentDwoSystemParameters> result = getStatus();
        StringBuilder string = new StringBuilder();
        for (PersistentDwoSystemParameters p : result) {
            string.append(p.getName());
            string.append(" ");
            string.append(p.getValue());
            string.append("\n");
        }
        return string.toString();
    }

}
