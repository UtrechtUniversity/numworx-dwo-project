package fi.dwo.server.rest;

import java.util.logging.Logger;
import javax.ws.rs.Path;

/**
 * REST functions that allows one to test the proper catching of HTML-errors.
 *
 * @see fi.dwo.dwojapplet.gui.panels.JPanel.MyProfile
 *
 * @author G.A.J. van der Plas
 */
@Path("/secure/test")
public class SecuredRestTestManager {

    private static final Logger LOG = Logger.getLogger(SecuredRestTestManager.class.getName());
//
//    @GET
//    @Produces({"application/json"})
//    @Path("/testNoError/json")
//    public String testNoError(@Context SecurityContext sc) {
//        return "{NoError!}";
//    }
//
//    @GET
//    @Produces({"application/json"})
//    @Path("/test400Error/json")
//    public Response test400Error(@Context SecurityContext sc) {
//        String userName = sc.getUserPrincipal().getName();
//        //TODO REST update lastLogin and such.
//        Dwo2RestException e = new Dwo2RestException(Dwo2ExceptionCode.User_AuthenticationError, "DwoRestException 400 thrown on request of user: " + userName + ".");
//        throw e;
//    }
//
//    @GET
//    @Produces({"application/json"})
//    @Path("/test500Error/json")
//    public Response test500Error(@Context SecurityContext sc) {
//        String userName = sc.getUserPrincipal().getName();
//        //TODO REST update lastLogin and such.
//        Dwo2RestException e = new Dwo2RestException(Dwo2ExceptionCode.User_AuthenticationError, "DwoRestException 500 thrown on request of user: " + userName + ".");
//        Response r = Response.status(500).entity(e.getMessage()).build();
//        return r;
//    }   
   
}
