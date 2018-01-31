package fi.dwo.server.PersistentDataManagers.core;

import fi.dwo.commons.persistence.entities.PersistentAnalyticalModel;
import fi.dwo.commons.persistence.entities.PersistentCourse;
import fi.dwo.commons.persistence.entities.PersistentSchool;
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
 * Manages analytical models in the persistent storage. 
 *
 * @author G.A.J. van der Plas
 */
public class AnalyticalModelManager {

    private static final Logger LOG = Logger.getLogger(AnalyticalModelManager.class.getName());

    private static EntityManager getEntityManager() {
        EntityManager em = DwoEmfFactory.getEntityManager();
        return em;
    }

    /**
     * Create.
     *
     * @param model
     */
    public static void create(PersistentAnalyticalModel model) throws PersistenceException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            em.persist(model);
            em.getTransaction().commit();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Can't create the PersistentAnalyticalModel.", e);
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
     * @param model
     * @return jpa merged course
     */
    public static PersistentAnalyticalModel edit(PersistentAnalyticalModel model) throws PersistenceException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            model = em.merge(model);
            em.getTransaction().commit();
        } catch (Exception e) {
            String msg = e.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Long id = model.getModelID();
                if (findEntity(id) == null) {
                    LOG.log(Level.INFO, "The PersistentAnalyticalModel with " + id + " no longer exists.", e);
                    throw new PersistenceException(e);
                }
            }
            throw new PersistenceException(e);
        } finally {
            if (em != null) {
                em.close();
            }
        }
        return model;
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
            PersistentAnalyticalModel model = null;
            try {
                model = em.getReference(PersistentAnalyticalModel.class, id);
                model.getModelID();
            } catch (EntityNotFoundException e) {
                LOG.log(Level.FINE, "The PersistentAnalyticalModel with " + id + " no longer exists.", e);
                throw new PersistenceException(e);
            }
            em.remove(model);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public static List<PersistentAnalyticalModel> findEntities() {
        return findEntities(true, -1, -1);
    }

    public static List<PersistentAnalyticalModel> findEntities(int maxResults, int firstResult) {
        return findEntities(false, maxResults, firstResult);
    }

    private static List<PersistentAnalyticalModel> findEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(PersistentAnalyticalModel.class));
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
        
    public static List<PersistentCourse> findEntities(PersistentSchool s) {
        EntityManager em = getEntityManager();
        try {
            javax.persistence.Query q = em.createNamedQuery("PersistentAnalyticalModel.findBySchoolID");
            q.setParameter("schoolID", s.getSchoolID());
            List<PersistentCourse> list = q.getResultList();
            LOG.log(Level.FINE, "Course-manager retrieved {0} PersistentAnalyticalModel with schoolid {1}", new Object[]{list.size(), s.getSchoolID()});
            return list;
        }
        finally {
            em.close();
        }
    }


    public static PersistentAnalyticalModel findEntity(Long id) throws PersistenceException {
        EntityManager em = getEntityManager();
        try {
            return em.find(PersistentAnalyticalModel.class, id);
         } catch (PersistenceException e) {
            LOG.log(Level.FINE, "The PersistentAnalyticalModel with " + id + " was not found.", e);
            throw e;
       } finally {
            em.close();
        }
    }

    public static int getEntityCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<PersistentAnalyticalModel> rt = cq.from(PersistentAnalyticalModel.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }


}
