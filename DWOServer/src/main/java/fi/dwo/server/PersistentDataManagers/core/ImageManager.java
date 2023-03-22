package fi.dwo.server.PersistentDataManagers.core;

import fi.dwo.commons.persistence.entities.PersistentImage;
import fi.dwo.server.persistence.DwoEmfFactory;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

/**
 * Manages courseSequence sequences in the persistent storage. 
 *
 * @author G.A.J. van der Plas
 */
public class ImageManager {

    private static final Logger LOG = Logger.getLogger(ImageManager.class.getName());

    private static EntityManager getEntityManager() {
        EntityManager em = DwoEmfFactory.getEntityManager();
        return em;
    }

    /**
     * Create.
     *
     * @param image
     */
    public static void create(PersistentImage image) throws PersistenceException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            em.persist(image);
            em.getTransaction().commit();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Can't create the PersistentImage.", e);
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
     * @param image
     * @throws Exception
     */
    public static PersistentImage edit(PersistentImage image) throws PersistenceException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            image = em.merge(image);
            em.getTransaction().commit();
            return image;
        } catch (Exception e) {
            String msg = e.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Long id = image.getCourseID();
                if (findEntity(id) == null) {
                    LOG.log(Level.FINE, "The PersistentImage with " + id + " no longer exists.", e);
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
            PersistentImage image = null;
            try {
                image = em.getReference(PersistentImage.class, id);
                image.getCourseID();
            } catch (EntityNotFoundException e) {
                LOG.log(Level.FINE, "The PersistentImage with " + id + " no longer exists.", e);
                throw e;
            }
            em.remove(image);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public static List<PersistentImage> findEntities() {
        return findEntities(true, -1, -1);
    }

    public static List<PersistentImage> findEntities(int maxResults, int firstResult) {
        return findEntities(false, maxResults, firstResult);
    }

    private static List<PersistentImage> findEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery<PersistentImage> cq = em.getCriteriaBuilder().createQuery(PersistentImage.class);
            cq.select(cq.from(PersistentImage.class));
            TypedQuery<PersistentImage> q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }
    
    public static PersistentImage findEntity(Long id) throws PersistenceException{
        EntityManager em = getEntityManager();
        try {
            return em.find(PersistentImage.class, id);
         } catch (PersistenceException e) {
            LOG.log(Level.FINE, "The PersistentImage with " + id + " was not found.", e);
            throw e;
         } finally {
            em.close();
        }
    }

    public static int getEntityCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery<Long> cq = em.getCriteriaBuilder().createQuery(Long.class);
            Root<PersistentImage> rt = cq.from(PersistentImage.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            TypedQuery<Long> q = em.createQuery(cq);
            return q.getSingleResult().intValue();
        } finally {
            em.close();
        }
    }

}
