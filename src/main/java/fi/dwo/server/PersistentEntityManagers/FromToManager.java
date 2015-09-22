package fi.dwo.server.PersistentEntityManagers;

import fi.dwo.commons.persistence.entities.PersistentFromTo;
import fi.dwo.commons.persistence.entities.PersistentFromToPK;
import fi.dwo.server.persistence.DwoEmfFactory;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

/**
 * Manages a teacher's class membership in the persistent storage. 
 *
 * @author G.A.J. van der Plas
 */
public class FromToManager {

    private static final Logger LOG = Logger.getLogger(FromToManager.class.getName());

    private static EntityManager getEntityManager() {
        EntityManager em = DwoEmfFactory.getEntityManager();
        return em;
    }

    /**
     * Create.
     *
     * @param fromTo
     */
    public static void create(PersistentFromTo fromTo) throws PersistenceException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            em.persist(fromTo);
            em.getTransaction().commit();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Can't create the PersistentFromTo.", e);
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
     * @param fromTo
     * @throws Exception
     */
    public static void edit(PersistentFromTo fromTo) throws PersistenceException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            fromTo = em.merge(fromTo);
            em.getTransaction().commit();
        } catch (Exception e) {
            String msg = e.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                PersistentFromToPK id = fromTo.getPersistentFromToPK();
                if (findEntity(id) == null) {
                    LOG.log(Level.FINE, "The PersistentFromTo with " + id + " no longer exists.", e);
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
    public static void destroy(PersistentFromToPK id) throws PersistenceException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
                PersistentFromTo fromTo = null;
            try {
                fromTo = em.getReference(PersistentFromTo.class, id);
                fromTo.getPersistentFromToPK();
            } catch (EntityNotFoundException e) {
                LOG.log(Level.FINE, "The PersistentFromTo with " + id + " no longer exists.", e);
                throw new PersistenceException(e);
            }
            em.remove(fromTo);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public static List<PersistentFromTo> findEntities() {
        return findEntities(true, -1, -1);
    }

    public static List<PersistentFromTo> findEntities(int maxResults, int firstResult) {
        return findEntities(false, maxResults, firstResult);
    }

    private static List<PersistentFromTo> findEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(PersistentFromTo.class));
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

    public static PersistentFromTo findEntity(PersistentFromToPK id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(PersistentFromTo.class, id);
        } finally {
            em.close();
        }
    }

    public static int getEntityCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<PersistentFromTo> rt = cq.from(PersistentFromTo.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
}
