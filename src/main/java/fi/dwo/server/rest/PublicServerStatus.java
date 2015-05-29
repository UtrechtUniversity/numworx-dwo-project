/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.dwo.server.rest;

import fi.dwo.commons.persistence.entities.PersistentDwoSystemParameters;
import fi.dwo.server.persistence.DwoEmfFactory;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

/**
 * Public server status. Showing health of the service. Under development.
 * 
 * @author G.A.J. van der Plas
 */
@Path("/public/serverstatus")
public class PublicServerStatus {

    private static final Logger LOG = Logger.getLogger(PublicServerStatus.class.getName());

    private final static EntityManagerFactory emf = DwoEmfFactory.instance();

    public List<PersistentDwoSystemParameters> getStatus() {
        
        EntityManager em;
        em = emf.createEntityManager();

        List<PersistentDwoSystemParameters> result;
        try {
            javax.persistence.Query q = em.createNamedQuery("DwoSystemParameters.findAll");
            result = (List<PersistentDwoSystemParameters>) q.getResultList();
            LOG.log(Level.INFO, "Fetched DwoSystemParameters {0}", new Object[]{result.size()});

        } finally {
            em.close();
        }
        
        StringBuilder string = new StringBuilder();
        for (PersistentDwoSystemParameters p : result) {
            string.append(p.getName());
            string.append(" ");
            string.append(p.getValue());
            string.append("\n");
        }
        LOG.log(Level.INFO, "Made output:", new Object[]{string.toString()});

        return result;
    }

    @GET
    @Produces({"application/json"})
    @Path("/json")
    public List<PersistentDwoSystemParameters> getStatusJson() {
        return getStatus();
    }

    @GET
    @Produces({"application/xml"})
    @Path("/xml")
    public List<PersistentDwoSystemParameters> getStatusXml() {
        return getStatus();
    }

    @GET
    @Produces({"text/plain"})
    @Path("/html")
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
