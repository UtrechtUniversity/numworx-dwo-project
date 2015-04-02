/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.dwo.server.persistence;

import fi.dwo.commons.persistence.entities.DwoSystemParameters;
import fi.dwo.commons.persistence.entities.PersistentUser;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

/**
 *
 * @author plas0006
 */
@Path("/user")
public class PersistentUserManager {

    private static final Logger log = Logger.getLogger(ServerStatus.class.getName());

 //   private final static EntityManagerFactory emf = Persistence.createEntityManagerFactory("DWO_MySQLDB");
//
//    public List<PersistentUser> getStatus() {
//        
//        EntityManager em;
//        em = emf.createEntityManager();
//
//        List<PersistentUser> result;
//        try {
//            javax.persistence.Query q = em.createNamedQuery("PersistentUser.findAll");
//            result = (List<DwoSystemParameters>) q.getResultList();
//            log.log(Level.INFO, "Fetched DwoSystemParameters {0}", new Object[]{result.size()});
//
//        } finally {
//            em.close();
//        }
        
//        StringBuilder string = new StringBuilder();
//        for (DwoSystemParameters p : result) {
//            string.append(p.getName());
//            string.append(" ");
//            string.append(p.getValue());
//            string.append("\n");
//        }
//        log.log(Level.INFO, "Made output:", new Object[]{string.toString()});
//
//        return result;
//    }
//
//    @GET
//    @Produces({"application/json"})
//    @Path("/json")
//    public List<PersistentUser> getStatusJson() {
//        return getStatus();
//    }
//
//    @GET
//    @Produces({"application/xml"})
//    @Path("/xml")
//    public List<PersistentUser> getStatusXml() {
//        return getStatus();
//    }
//
//    @GET
//    @Produces({"text/plain"})
//    @Path("/html")
//    public String getStatusText() {
//        List<DwoSystemParameters> result = getStatus();
//        StringBuilder string = new StringBuilder();
//        for (DwoSystemParameters p : result) {
//            string.append(p.getName());
//            string.append(" ");
//            string.append(p.getValue());
//            string.append("\n");
//        }
//        return string.toString();
//    }    
}
