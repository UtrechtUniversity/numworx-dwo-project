package fi.dwo.server.PersistentDataManagers.core;

import fi.dwo.commons.persistence.entities.PersistentAppletConfig;
import fi.dwo.server.persistence.DwoEmfFactory;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

/**
 * Manages applet configs in the persistent storage. 
 *
 * @author G.A.J. van der Plas
 */
public class AppletConfigManager {

    private static final Logger LOG = Logger.getLogger(AppletConfigManager.class.getName());

    private static EntityManager getEntityManager() {
        EntityManager em = DwoEmfFactory.getEntityManager();
        return em;
    }

    /**
     * Create.
     *
     * @param persistentAppletConfig
     */
    public static void create(PersistentAppletConfig persistentAppletConfig) throws PersistenceException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            em.persist(persistentAppletConfig);
            em.getTransaction().commit();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Can't create the PersistentAppletConfig.", e);
            throw new PersistenceException(e);
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    /**
     * Update
     *
     * @param persistentAppletConfig
     * @throws Exception
     */
    public static void edit(PersistentAppletConfig persistentAppletConfig) throws PersistenceException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            persistentAppletConfig = em.merge(persistentAppletConfig);
            em.getTransaction().commit();
        } catch (Exception e) {
            String msg = e.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Long id = persistentAppletConfig.getAppletConfigID();
                if (findEntity(id) == null) {
                    LOG.log(Level.FINE, "The PersistentAppletConfig with " + id + " no longer exists.", e);
                    throw new PersistenceException(e);
                }
            }
            throw new PersistenceException(e);
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    /**
     * Removes a user from the persistent store.
     *
     * @param id
     */
    public static void destroy(Long id) throws PersistenceException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            PersistentAppletConfig persistentAppletConfig = null;
            try {
                persistentAppletConfig = em.getReference(PersistentAppletConfig.class, id);
                persistentAppletConfig.getAppletConfigID();
            } catch (EntityNotFoundException e) {
                LOG.log(Level.FINE, "The PersistentAppletConfig with " + id + " no longer exists.", e);
                throw new PersistenceException(e);
            }
            em.remove(persistentAppletConfig);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public static List<PersistentAppletConfig> findEntities() {
        return findEntities(true, -1, -1);
    }

    public static List<PersistentAppletConfig> findEntities(int maxResults, int firstResult) {
        return findEntities(false, maxResults, firstResult);
    }

    private static List<PersistentAppletConfig> findEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(PersistentAppletConfig.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public static PersistentAppletConfig findEntity(Long id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(PersistentAppletConfig.class, id);
        } finally {
            em.close();
        }
    }

    public static int getEntityCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<PersistentAppletConfig> rt = cq.from(PersistentAppletConfig.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }


}
