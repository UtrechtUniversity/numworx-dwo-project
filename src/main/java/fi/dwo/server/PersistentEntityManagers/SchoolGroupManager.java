package fi.dwo.server.PersistentEntityManagers;

import fi.dwo.commons.persistence.entities.PersistentSchoolGroup;
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
 * Manages school groups  in the persistent storage.
 *
 * @author G.A.J. van der Plas
 */
public class SchoolGroupManager {

    private static final Logger LOG = Logger.getLogger(SchoolGroupManager.class.getName());

    private static EntityManager getEntityManager() {
        EntityManager em = DwoEmfFactory.createEntityManager();
        return em;
    }

    /**
     * Create.
     *
     * @param entity
     */
    public static void create(PersistentSchoolGroup entity) throws PersistenceException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            em.persist(entity);
            em.getTransaction().commit();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Can't create the PersistentSchoolGroup.", e);
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
     * @param entity
     * @throws Exception
     */
    public static void edit(PersistentSchoolGroup entity) throws PersistenceException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            entity = em.merge(entity);
            em.getTransaction().commit();
        } catch (Exception e) {
            String msg = e.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = entity.getSchoolGroupID();
                if (findEntity(id) == null) {
                    LOG.log(Level.FINE, "The PersistentSchoolGroup with " + id + " no longer exists.", e);
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
                PersistentSchoolGroup entity = null;
            try {
                entity = em.getReference(PersistentSchoolGroup.class, id);
                entity.getSchoolGroupID();
            } catch (EntityNotFoundException e) {
                LOG.log(Level.FINE, "The PersistentSchoolGroup with " + id + " no longer exists.", e);
                throw new PersistenceException(e);
            }
            em.remove(entity);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public static List<PersistentSchoolGroup> findEntities() {
        return findEntities(true, -1, -1);
    }

    public static List<PersistentSchoolGroup> findEntities(int maxResults, int firstResult) {
        return findEntities(false, maxResults, firstResult);
    }

    private static List<PersistentSchoolGroup> findEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(PersistentSchoolGroup.class));
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

    public static PersistentSchoolGroup findEntity(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(PersistentSchoolGroup.class, id);
        } finally {
            em.close();
        }
    }

    public static int getEntityCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<PersistentSchoolGroup> rt = cq.from(PersistentSchoolGroup.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }

}
