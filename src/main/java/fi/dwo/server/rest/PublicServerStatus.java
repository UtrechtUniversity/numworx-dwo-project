package fi.dwo.server.rest;

import nl.uu.fi.dwo.rest.exceptions.Dwo2ExceptionCode;
import nl.uu.fi.dwo.rest.exceptions.Dwo2RestException;
import fi.dwo.commons.persistence.entities.PersistentDwoSystemParameters;
import fi.dwo.server.PersistentDataManagers.core.DwoSystemParametersManager;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

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

    /**
     * Returns an empty set of of Attributes if not found.
     * 
     * @return
     * @throws FileNotFoundException
     * @throws IOException 
     */
    Attributes getManifestAttributes() throws FileNotFoundException, IOException {
        Attributes atts;
        try{
        InputStream resourceAsStream = context.getResourceAsStream("/META-INF/MANIFEST.MF");
        Manifest mf = new Manifest();
        mf.read(resourceAsStream);
        atts = mf.getMainAttributes();
        return atts;
        }catch(NullPointerException ex){
            atts = new Attributes();
            return atts;
        }
    }
    
//  tests above getManifestAttributes()
//    @GET
//    @Produces({"application/json"})
//    @Path("/getManifest")    
//    public Response getData() throws FileNotFoundException, IOException {
//        Attributes manifestAttributes = getManifestAttributes();
//
//        return Response.status(Response.Status.OK)
//                .entity(manifestAttributes)
//                .build();
//    }

    public List<PersistentDwoSystemParameters> getDwoSystemParamStatus() {
        List<PersistentDwoSystemParameters> result;
        try {
            result = DwoSystemParametersManager.findEntities();
            LOG.log(Level.FINER, "Fetched DwoSystemParameters {0}", new Object[]{result.size()});
        } catch (Exception e) {
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
    public String getServletRevision() throws FileNotFoundException, IOException {
        Attributes manifestAttributes = getManifestAttributes();
        String buildNumber = manifestAttributes.getValue("Implementation-Build");
        String softwareVersion = manifestAttributes.getValue("Implementation-Version");
        String timeStamp = manifestAttributes.getValue("Implementation-Timestamp");

        String out = "Software version: " + softwareVersion + ", buildnumber: " + buildNumber + ", timestamp " + timeStamp + "\n";
        LOG.log(Level.INFO, "Software version {0}, buildnumber {1}, timestamp {2}", new Object[]{softwareVersion, buildNumber, timeStamp});

        return out;
    }

    /**
     * Returns server status values. In case no Manifest is found 'null' is returned.
     * 
     * @return
     * @throws IOException 
     */
    @GET
    @Produces({"application/json"})
    @Path("/get")
    public String getStatusJSON() throws IOException {
        Attributes manifestAttributes = getManifestAttributes();
        String buildNumber = manifestAttributes.getValue("Implementation-Build");
        String softwareVersion = manifestAttributes.getValue("Implementation-Version");
        String timeStamp = manifestAttributes.getValue("Implementation-Timestamp");
        
        StringBuilder result = new StringBuilder();
        result.append("[");
        result.append("{\"name\":\"projectVersion\", \"value\":\"").append(softwareVersion).append("\"},")
                .append("{\"name\":\"buildNumber\", \"value\":\"").append(buildNumber).append("\"},")
                .append("{\"name\":\"timestamp\", \"value\":\"").append(timeStamp);
        for (PersistentDwoSystemParameters p : getDwoSystemParamStatus()) {
            result.append("\"},{\"name\":\"").append(p.getName()).append("\", \"value\":\"").append(p.getValue());
        }
        result.append("\"}]");
        return result.toString();
    }

    @GET
    @Produces({"text/plain"})
    @Path("/get/html")
    public String getStatus() throws IOException {
        return getServletRevision()
                + "\n" + getDwoSystemStatusText();
    }

}
