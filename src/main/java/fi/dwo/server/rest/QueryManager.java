/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.dwo.server.rest;

import fi.dwo.commons.rest.entities.SchoolsAndClasses;
import fi.dwo.server.persistence.DwoEmfFactory;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;

/**
 *
 * @author Gert van der Plas <gertvdplas@gmail.com>
 */
@Path("/secure/user/query")
public class QueryManager {

    private static final Logger log = Logger.getLogger(QueryManager.class.getName());

    /**
     * Runs a dangerous free query, but only for some users.
     *
     * @param sc
     * @param query
     * @return
     */
    @POST
    @Produces({MediaType.TEXT_PLAIN})
    @Path("/post")
    @Consumes("application/x-www-form-urlencoded")
    public String RunQuery(@Context SecurityContext sc, @FormParam("query") String query) {
        String name = sc.getUserPrincipal().getName();
        if (name.compareTo("gert_project") == 0) {
            //Allow only this user code to run a jpa query
            EntityManager em = DwoEmfFactory.createEntityManager();
            String r = "";
            try {

                log.log(Level.INFO, "For user with username {0} a free query is run: {1}", new Object[]{name, query});

                javax.persistence.Query q;
                q = em.createQuery(query);
                List<Object[]> resultList = q.getResultList();
                log.log(Level.INFO, "Fetched {1} results for user {0}.", new Object[]{name, resultList.size()});
                for (Object[] oList : resultList) {
                    for (int j = 0; j < oList.length; j++) {
                        r = r + " " + oList[j];
                    }

                    log.log(Level.INFO, "Query Result for user {0}: {1}.", new Object[]{name, r});
                }

            } catch (Exception e) {
                log.log(Level.WARNING, "Unexpected exception: {0}", new Object[]{e.getMessage()});
                return e.getMessage();
            } finally {
                em.close();
            }
            return r;
        } else {
            log.log(Level.WARNING, "ILLEGAL USER-OPERATION: {0} is trying to run an illegal free query operation.", new Object[]{sc.getUserPrincipal().getName()});
            throw new NotAuthorizedException("You Don't Have Permission to run this operation " + name + ".");
        }
    }

    @GET
    @Produces({MediaType.TEXT_HTML})
    @Path("/get/html")
    public String updateCurrentUser(@Context SecurityContext sc) {
        String name = sc.getUserPrincipal().getName();
        if (name.compareTo("gert_project") == 0) {
            String r = "<form action=\"http://localhost:8080/DWO/DWOServer/rest/secure/user/query/post\" method=\"post\" >\n"
                    + "Query:<br>\n"
                    + "<input type=\"text\" size=\"80\" name=\"query\" value=\"select p from PersistentUser p where p like \'gert_project\'\">\n"
                    + "<br><br>\n"
                    + "<input type=\"submit\" value=\"Submit\">\n"
                    + "</form>";
            return r;
        } else {
            return "";
        }
    }
}