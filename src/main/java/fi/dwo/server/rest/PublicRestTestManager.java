package fi.dwo.server.rest;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import nl.uu.fi.dwo.rest.exceptions.Dwo2ExceptionCode;
import nl.uu.fi.dwo.rest.exceptions.Dwo2RestException;
import java.util.logging.Logger;
import javax.servlet.ServletContext;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;

/**
 * REST functions that allows one to test the proper catching of HTML-errors.
 *
 * @see fi.dwo.dwojapplet.gui.panels.JPanel.MyProfile
 *
 * @author G.A.J. van der Plas
 */
@Path("/public/test")
public class PublicRestTestManager {

    private static final Logger LOG = Logger.getLogger(PublicRestTestManager.class.getName());
    @Context
    private ServletContext context;

    @GET
    @Produces({"application/json"})
    @Path("/testNoError/json")
    public String testNoError(@Context SecurityContext sc) {
        return "{NoError!}";
    }

    @GET
    @Produces({"application/json"})
    @Path("/test400Error/json")
    public Response test400Error(@Context SecurityContext sc) {
        String userName = sc.getUserPrincipal().getName();
        //TODO REST update lastLogin and such.
        Dwo2RestException e = new Dwo2RestException(Dwo2ExceptionCode.User_AuthenticationError, "DwoRestException 400 thrown on request of user: " + userName + ".");
        Response r = Response.status(400).entity(e.getMessage()).build();
        return r;
    }

    @GET
    @Produces({"application/json"})
    @Path("/test401Error/json")
    public Response test401Error(@Context SecurityContext sc) {
        String userName = sc.getUserPrincipal().getName();
        //TODO REST update lastLogin and such.
        Dwo2RestException e = new Dwo2RestException(Dwo2ExceptionCode.User_AuthenticationError, "DwoRestException 401 thrown on request of user: " + userName + ".");
        Response r = Response.status(401).entity(e.getMessage()).build();
        return r;
    }

    @GET
    @Produces({"application/json"})
    @Path("/test500Error/json")
    public Response test500Error(@Context SecurityContext sc) {
        String userName = sc.getUserPrincipal().getName();
        //TODO REST update lastLogin and such.
        Dwo2RestException e = new Dwo2RestException(Dwo2ExceptionCode.User_AuthenticationError, "DwoRestException 500 thrown on request of user: " + userName + ".");
        Response r = Response.status(500).entity(e.getMessage()).build();
        return r;
    }

    
    
    
    @GET
    @Produces({MediaType.TEXT_HTML})
    @Path("/VerifyBrowserExamKey/html")
    public Response verifyBrowserExamKey(@Context UriInfo uriInfo, @HeaderParam("X-SafeExamBrowser-RequestHash") String headerHash, @QueryParam("key") String rawKey) {
        String uri = uriInfo.getAbsolutePath().toString();
        boolean result = verifySEBHeader(headerHash, uri, rawKey);
        
        Response r = Response.status(200).entity(String.valueOf(result)).build();
        return r;
    }

	public static boolean verifySEBHeader(String headerHash,  String uri, String... rawKey) {
		MessageDigest md;
		if(headerHash == null) return false;
		try {
			md = MessageDigest.getInstance("SHA-256");
		} catch (NoSuchAlgorithmException e) {
			LOG.log(Level.SEVERE, "verifySEBHeader", e);
			return headerHash != null; // cannot verify.
		}
		for(int i = 0; i < rawKey.length; i++) {
			String serverHash = uri + rawKey[i];
			//hash serverHash with SHA-256
			byte[] hash;
			hash = md.digest(serverHash.getBytes(StandardCharsets.UTF_8));

			// Convert the byte to hex format
			StringBuffer hexString = new StringBuffer();

			for (int n = 0; n < hash.length; n++) {
				String hex = Integer.toHexString(0xff & hash[n]);
				if (hex.length() == 1) {
					hexString.append('0');
				}
				hexString.append(hex);
			}

			LOG.log(Level.INFO, "serverHash = {0}", new Object[]{hexString.toString()});
			LOG.log(Level.INFO, "headerHash = {0}", new Object[]{headerHash});
			if (hexString.toString().equals(headerHash)) {
				return true;
			}
		}
		return false;
	}

}
