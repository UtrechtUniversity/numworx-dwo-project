/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.dwo.server.persistence;

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
 * @author Gert van der Plas <gertvdplas@gmail.com>
 */
@Path("/serverstatus")
public class ServerStatus {

    private static final Logger log = Logger.getLogger(ServerStatus.class.getName());

    private final static EntityManagerFactory emf = Persistence.createEntityManagerFactory("DWO_MySQLDB");

    @GET
    @Produces({"application/json, application/xml"})
    public List<DwoSystemParameters> getStatus() {
        EntityManager em;
        em = emf.createEntityManager();

        List<DwoSystemParameters> result;
        try {
            javax.persistence.Query q = em.createNamedQuery("DwoSystemParameters.findAll");
            result = (List<DwoSystemParameters>) q.getResultList();
            log.log(Level.INFO, "Fetched DwoSystemParameters {0}", new Object[]{result.size()});

        } finally {
            em.close();
        }
        StringBuilder string = new StringBuilder();
        for(DwoSystemParameters p: result){
            string.append(p.getName());
            string.append(" ");
            string.append(p.getValue());
            string.append("\n");
        }
            log.log(Level.INFO, "Made output:", new Object[]{string.toString()});

        return result;
    }
}
