package fi.dwo.server.PersistentEntityManagers;

import fi.dwo.commons.persistence.entities.PersistentScoData;
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
 * Manages sco data in the persistent storage. As this data will be detached
 * from the main transactional database it is considered separate data to be retrieved.
 *
 * @author G.A.J. van der Plas
 */
public class ScoDataManager {

    private static final Logger LOG = Logger.getLogger(ScoDataManager.class.getName());

    private static EntityManager getEntityManager() {
        EntityManager em = DwoEmfFactory.createEntityManager();
        return em;
    }

    /**
     * Create.
     *
     * @param ssd studentScoData
     */
    public static void create(PersistentScoData ssd) throws PersistenceException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            em.persist(ssd);
            em.getTransaction().commit();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Can't create the PersistentScoData.", e);
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
     * @param sd studentScoData
     * @throws Exception
     */
    public static void edit(PersistentScoData sd) throws PersistenceException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            sd = em.merge(sd);
            em.getTransaction().commit();
        } catch (Exception e) {
            String msg = e.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = sd.getScoID();
                if (findEntity(id) == null) {
                    LOG.log(Level.FINE, "The PersistentScoData with " + id + " no longer exists.", e);
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
    public static void destroy(Integer id) throws PersistenceException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            PersistentScoData ssd = null;
            try {
                ssd = em.getReference(PersistentScoData.class, id);
                ssd.getScoID();
            } catch (EntityNotFoundException e) {
                LOG.log(Level.FINE, "The PersistentScoData with " + id + " no longer exists.", e);
                throw new PersistenceException(e);
            }
            em.remove(ssd);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public static List<PersistentScoData> findEntities() {
        return findEntities(true, -1, -1);
    }

    public static List<PersistentScoData> findEntities(int maxResults, int firstResult) {
        return findEntities(false, maxResults, firstResult);
    }

    private static List<PersistentScoData> findEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(PersistentScoData.class));
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

    public static PersistentScoData findEntity(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(PersistentScoData.class, id);
        } finally {
            em.close();
        }
    }

    public static int getEntityCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<PersistentScoData> rt = cq.from(PersistentScoData.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }

}
