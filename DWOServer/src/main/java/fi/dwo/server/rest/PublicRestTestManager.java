package fi.dwo.server.rest;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Date;
import java.util.List;
import java.util.logging.Level;

import nl.numworx.schoolyear.jclient.SchoolyearClient;
import nl.numworx.schoolyear.jclient.dto.SignatureDTO;
import nl.uu.fi.dwo.rest.dom.entities.util.DelState;
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

import fi.dwo.commons.persistence.entities.PersistentHasRole;
import fi.dwo.commons.persistence.entities.PersistentLoginContext;
import fi.dwo.commons.persistence.entities.PersistentUser;
import fi.dwo.server.PersistentDataManagers.cache.HasRoleCache;
import fi.dwo.server.PersistentDataManagers.core.HasRoleManager;
import fi.dwo.server.PersistentDataManagers.core.LoginContextManager;
import fi.dwo.server.PersistentDataManagers.core.UserManager;

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
    
    @GET
    @Produces({MediaType.TEXT_HTML})
    @Path("VerifySchoolyearSignature/html")
    public Response verifySchoolyearSignature(@HeaderParam("x-sy-signature") String signature) throws IOException {
    	SchoolyearClient client = new SchoolyearClient.Builder().build();
    	SignatureDTO dto = new SignatureDTO();
    	dto.x_sy_signature = signature;
		boolean result = client.validateSignature(dto);
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

	
	@GET
	@Path("system/maintenance")
	@Produces({"text/plain"})
	public String maintenance(@QueryParam("skip") Integer start, @QueryParam("limit") Integer limit) throws Exception {
		StringBuilder sb = new StringBuilder();
		
		if (start == null) start = 0;
		if (limit == null) limit = 100;
		
		
		List<PersistentHasRole> hr = HasRoleManager.findEntities(limit, start);
		for(PersistentHasRole item: hr) {
			start = start + 1;
			if (item.optlock > 0) continue;
			Long userid = item.getPersistentHasRolePK().getUserID();
			List<PersistentLoginContext> lc = LoginContextManager.findEntities(userid);
			if (!lc.isEmpty() && null != lc.get(0).getLastLogin()) {
				item.setLastLogin(new Date(lc.get(0).getLastLogin()));
			} else {
				PersistentUser u = UserManager.findEntity(userid);
				if (u == null) 
				{
					item.delState = DelState.marked;
					userid = null;
				}
				else
					item.setLastLogin(u.getLastLogin());
			}
			item = HasRoleManager.edit(item);
			HasRoleCache.remove(item);
			sb.append(start).append(" ").append(userid).append("\n");
		}
		sb.append(start).append(" END");
		return sb.toString();
	}
}
