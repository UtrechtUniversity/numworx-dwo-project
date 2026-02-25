package fi.dwo.server.PersistentDataManagers.core;

import fi.dwo.commons.persistence.entities.PersistentCourseData;
import fi.dwo.server.persistence.DwoEmfFactory;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceException;
import javax.persistence.RollbackException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

/**
 * Manages courses in the persistent storage. 
 *
 * @author G.A.J. van der Plas
 */
public class CourseDataManager extends AbstractManager {

    private static final Logger LOG = Logger.getLogger(CourseDataManager.class.getName());

    /**
     * Update
     *
     * @param course
     * @return jpa merged course
     */
    public static PersistentCourseData edit(PersistentCourseData course) throws PersistenceException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            course.changeTimestamp();
            course = em.merge(course);
            em.getTransaction().commit();
        } 
        catch (PersistenceException e) {
          throw e;
        } catch (Exception e) {
            throw new PersistenceException(e);
        } finally {
            if (em != null) {
                em.close();
            }
        }
        return course;
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
            PersistentCourseData course = null;
            try {
                course = em.getReference(PersistentCourseData.class, id);
                course.getCourseID();
            } catch (EntityNotFoundException e) {
                LOG.log(Level.FINE, "The PersistentCourse with " + id + " no longer exists.", e);
                return;
            }
            em.remove(course);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public static List<PersistentCourseData> findEntities() {
        return findEntities(true, -1, -1);
    }

    public static List<PersistentCourseData> findEntities(int maxResults, int firstResult) {
        return findEntities(false, maxResults, firstResult);
    }

    private static List<PersistentCourseData> findEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery<PersistentCourseData> cq = em.getCriteriaBuilder().createQuery(PersistentCourseData.class);
            cq.select(cq.from(PersistentCourseData.class));
            TypedQuery<PersistentCourseData> q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public static PersistentCourseData findEntity(Long id) throws PersistenceException {
    	return find(id, PersistentCourseData.class);
    }

    public static int getEntityCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery<Long> cq = em.getCriteriaBuilder().createQuery(Long.class);
            Root<PersistentCourseData> rt = cq.from(PersistentCourseData.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            TypedQuery<Long> q = em.createQuery(cq);
            return q.getSingleResult().intValue();
        } finally {
            em.close();
        }
    }


}
