package fi.dwo.server.rest;

import nl.uu.fi.dwo.rest.exceptions.Dwo2ExceptionCode;
import nl.uu.fi.dwo.rest.exceptions.Dwo2RestException;
import fi.dwo.commons.persistence.entities.PersistentDwoSystemParameters;
import fi.dwo.server.PersistentDataManagers.core.DwoSystemParametersManager;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;

/**
 * Public server status. Showing health of the service. Under development.
 *
 * @author G.A.J. van der Plas
 */
@Path("/public/status")
public class PublicServerStatus {

    @Context
    private ServletContext context;

    private static final Logger LOG = Logger.getLogger(PublicServerStatus.class.getName());

    public List<PersistentDwoSystemParameters> getDwoSystemParamStatus() {
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
    @Path("/getDwoSystemParamList")
    public List<PersistentDwoSystemParameters> getStatusJson() {
        return getDwoSystemParamStatus();
    }

    @GET
    @Produces({"text/plain"})
    @Path("/getDwoSystemParamList/html")
    public String getDwoSystemStatusText() {
        List<PersistentDwoSystemParameters> result = getDwoSystemParamStatus();
        StringBuilder string = new StringBuilder();
        for (PersistentDwoSystemParameters p : result) {
            string.append(p.getName());
            string.append(" ");
            string.append(p.getValue());
            string.append("\n");
        }
        return string.toString();
    }

    @GET
    @Produces({"text/plain"})
    @Path("/getServletRevision/html")
    public String getServletRevision() {
        String buildNumber = context.getInitParameter("buildnumber");
        String softwareVersion = context.getInitParameter("projectVersion");
        String timeStamp = context.getInitParameter("timestamp");

        String out = "Software version, buildnumber: " + softwareVersion + ", " + buildNumber + ", timestamp " + timeStamp + "\n";
        LOG.log(Level.INFO, "Software version {0}, buildnumber {1}, timestamp {2}", new Object[]{softwareVersion, buildNumber, timeStamp});

        return out;
    }

    @GET
    @Produces({"application/json"})
    @Path("/get")
    public String getStatusJSON() {
        StringBuilder result = new StringBuilder();
        result.append("[")
                .append("{\"name\":\"projectVersion\", \"value\":\"").append(context.getInitParameter("projectVersion")).append("\"},")
                .append("{\"name\":\"buildNumber\", \"value\":\"").append(context.getInitParameter("buildnumber")).append("\"},")
                .append("{\"name\":\"timestamp\", \"value\":\"").append(context.getInitParameter("timestamp"));
        for (PersistentDwoSystemParameters p : getDwoSystemParamStatus()) {
            result.append("\"},{\"name\":\"").append(p.getName()).append("\", \"value\":\"").append(p.getValue());
        }
        result.append("\"}]");
        return result.toString();
    }

    @GET
    @Produces({"text/plain"})
    @Path("/get/html")
    public String getStatus() {

        return getServletRevision()
                + "\n" + getDwoSystemStatusText();
    }

}
