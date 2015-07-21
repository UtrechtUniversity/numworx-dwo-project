/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.dwo.server.rest;

import fi.dwo.commons.exceptions.Dwo2ExceptionCode;
import fi.dwo.commons.exceptions.Dwo2RestException;
import fi.dwo.server.persistence.DwoEmfFactory;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.ws.rs.GET;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

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
        throw e;
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
    

    /**
     * Drop/clean and create a named DWO2TestSchool.
     * 
     * @param sc
     * @return 
     */
    @GET
    @Produces({"application/json"})
    @Path("/InitializeDWO2TestSchool/json")
    public String InitializeDWO2TestSchool(@Context SecurityContext sc) {
            final String schoolName = "DWO2TestSchool";
        
        
           String name = sc.getUserPrincipal().getName();           
        if (name.compareTo("gert_project") == 0) {
            //Allow only this user code to run a jpa query
            EntityManager em = DwoEmfFactory.getEntityManager();
            String r = "";
            try {

                LOG.log(Level.INFO, "For user with username {0} the testschool is created {1}", new Object[]{name, schoolName});
                //Create school here!

            } catch (Exception e) {
                LOG.log(Level.WARNING, "Unexpected exception: {0}", e);
                throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InterfaceError, "Error running intialization");
            } finally {
                em.close();
            }
            return "All is well.";
        } else {
            LOG.log(Level.SEVERE, "ILLEGAL USER-OPERATION: {0} is trying to run an illegal rest operation.", new Object[]{sc.getUserPrincipal().getName()});
            throw new NotAuthorizedException("You Don't Have Permission to run this operation " + name + ".");
        }
    }
    
}
