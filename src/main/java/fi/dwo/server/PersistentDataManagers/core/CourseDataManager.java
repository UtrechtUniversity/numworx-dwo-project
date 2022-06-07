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
public class CourseDataManager {

    private static final Logger LOG = Logger.getLogger(CourseDataManager.class.getName());

    private static EntityManager getEntityManager() {
        EntityManager em = DwoEmfFactory.getEntityManager();
        return em;
    }

    /**
     * Create.
     *
     * @param course
     */
    public static void create(PersistentCourseData course) throws PersistenceException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            em.persist(course);
            em.getTransaction().commit();
        }
        catch(RollbackException e) {
          LOG.log(Level.WARNING, "Can't create the PersistentCourse.", e);
          throw e;
        }
        catch(PersistenceException e)
        {
          LOG.log(Level.SEVERE, "Can't create the PersistentCourse.", e);
          throw e;
        }
        catch (Exception e) {
            LOG.log(Level.SEVERE, "Can't create the PersistentCourse.", e);
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
     * @param course
     * @return jpa merged course
     */
    public static PersistentCourseData edit(PersistentCourseData course) throws PersistenceException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            course = em.merge(course);
            em.getTransaction().commit();
        } 
        catch (RollbackException e) {
          throw e;
        } catch (Exception e) {
            String msg = e.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Long id = course.getCourseID();
                if (findEntity(id) == null) {
                    LOG.log(Level.INFO, "The PersistentCourseData with " + id + " no longer exists.", e);
                    throw new PersistenceException(e);
                }
            }
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
                throw e;
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
        EntityManager em = getEntityManager();
        try {
            return em.find(PersistentCourseData.class, id);
         } catch (PersistenceException e) {
            LOG.log(Level.FINE, "The PersistentCourse with " + id + " was not found.", e);
            throw e;
       } finally {
            em.close();
        }
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
