package fi.dwo.server.PersistentDataManagers.core;

import fi.dwo.commons.persistence.entities.PersistentStudentModelData;
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
import nl.uu.fi.dwo.rest.util.DwoDateUtilities;

/**
 * Manages analytical models in the persistent storage. 
 *
 * @author G.A.J. van der Plas
 */
public class StudentModelDataManager {

    private static final Logger LOG = Logger.getLogger(StudentModelDataManager.class.getName());

    private static EntityManager getEntityManager() {
        EntityManager em = DwoEmfFactory.getEntityManager();
        return em;
    }

    /**
     * Create.
     *
     * @param data
     */
    public static PersistentStudentModelData create(PersistentStudentModelData data) throws PersistenceException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            data.setLastChangeTimeStamp(DwoDateUtilities.getCurrentDwoUnixTimeStamp());
            em.persist(data);
            em.getTransaction().commit();
            return data;
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Can't create the PersistentStudentModelData.", e);
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
     * @param data
     * @return jpa merged course
     */
    public static PersistentStudentModelData edit(PersistentStudentModelData data) throws PersistenceException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            data.setLastChangeTimeStamp(DwoDateUtilities.getCurrentDwoUnixTimeStamp());            
            data = em.merge(data);
            em.getTransaction().commit();
        } catch (Exception e) {
            String msg = e.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Long id = data.getModelDataId();
                if (findEntity(id) == null) {
                    LOG.log(Level.INFO, "The PersistentStudentModelData with " + id + " no longer exists.", e);
                    throw new PersistenceException(e);
                }
            }
            throw new PersistenceException(e);
        } finally {
            if (em != null) {
                em.close();
            }
        }
        return data;
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
            PersistentStudentModelData model = null;
            try {
                model = em.getReference(PersistentStudentModelData.class, id);
                model.getModelDataId();
            } catch (EntityNotFoundException e) {
                LOG.log(Level.FINE, "The PersistentStudentModelData with " + id + " no longer exists.", e);
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

    public static List<PersistentStudentModelData> findEntities() {
        return findEntities(true, -1, -1);
    }

    public static List<PersistentStudentModelData> findEntities(int maxResults, int firstResult) {
        return findEntities(false, maxResults, firstResult);
    }

    private static List<PersistentStudentModelData> findEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(PersistentStudentModelData.class));
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


    public static PersistentStudentModelData findEntity(Long id) throws PersistenceException {
        EntityManager em = getEntityManager();
        try {
            return em.find(PersistentStudentModelData.class, id);
         } catch (PersistenceException e) {
            LOG.log(Level.FINE, "The PersistentStudentModelData with " + id + " was not found.", e);
            throw e;
       } finally {
            em.close();
        }
    }

    public static int getEntityCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<PersistentStudentModelData> rt = cq.from(PersistentStudentModelData.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }


}
