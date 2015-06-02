/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.dwo.server.rest;

import fi.dwo.commons.exceptions.Dwo2ExceptionCode;
import fi.dwo.commons.exceptions.Dwo2RestException;
import java.util.logging.Logger;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import sun.util.logging.PlatformLogger;

/**
 * Operations for the GUI Component that manages the User profile section.
 *
 * @see fi.dwo.dwojapplet.gui.panels.JPanel.MyProfile
 *
 * @author G.A.J. van der Plas
 */
@Path("/secure/test")
public class RestTestManager {

    private static final Logger LOG = Logger.getLogger(RestTestManager.class.getName());

    @GET
    @Produces({"application/json"})
    @Path("/test400Error/json") 
    public Response test400Error(@Context SecurityContext sc) {
        String userName = sc.getUserPrincipal().getName();
        //TODO REST update lastLogin and such.
        Dwo2RestException e =  new Dwo2RestException(Dwo2ExceptionCode.User_AuthenticationError, "DwoRestException 400 thrown on request of user: "+userName+".");
        Response r = Response.status(400).entity(e.getMessage()).build();
        return r;
        //throw e;
    }

    @GET
    @Produces({"application/json"})
    @Path("/test500Error/json") 
    public Response test500Error(@Context SecurityContext sc) {
        String userName = sc.getUserPrincipal().getName();
        //TODO REST update lastLogin and such.
        Dwo2RestException e =  new Dwo2RestException(Dwo2ExceptionCode.User_AuthenticationError, "DwoRestException 500 thrown on request of user: "+userName+".");
        Response r = Response.status(500).entity(e.getMessage()).build();
        return r;
    }
}
