package fi.dwo.server.PersistentDataManagers.core;

import fi.dwo.commons.persistence.entities.PersistentClassCourse;
import fi.dwo.commons.persistence.entities.PersistentSchoolClass;
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
 * Manages class courses in the persistent storage.
 *
 * @author G.A.J. van der Plas
 */
public class ClassCourseManager {

    private static final Logger LOG = Logger.getLogger(ClassCourseManager.class.getName());

    private static EntityManager getEntityManager() {
        EntityManager em = DwoEmfFactory.getEntityManager();
        return em;
    }

    /**
     * Create.
     *
     * @param classCourse
     */
    public static void create(PersistentClassCourse classCourse) throws PersistenceException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            em.persist(classCourse);
            em.getTransaction().commit();
        }
        catch (Exception e) {
            LOG.log(Level.SEVERE, "Can't create the PersistentClassCourse.", e);
            throw new PersistenceException(e);
        }
        finally {
            if (em != null) {
                em.close();
            }
        }
    }

    /**
     * Update
     *
     * @param classCourse
     */
    public static void edit(PersistentClassCourse classCourse) throws PersistenceException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            classCourse = em.merge(classCourse);
            em.getTransaction().commit();
        }
        catch (Exception e) {
            String msg = e.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Long id = classCourse.getClassCourseID();
                if (findEntity(id) == null) {
                    LOG.log(Level.FINE, "The PersistentClassCourse with " + id + " no longer exists.", e);
                    throw new PersistenceException(e);
                }
            }
            throw new PersistenceException(e);
        }
        finally {
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
            PersistentClassCourse classCourse = null;
            try {
                classCourse = em.getReference(PersistentClassCourse.class, id);
                classCourse.getClassCourseID();
            }
            catch (EntityNotFoundException e) {
                LOG.log(Level.FINE, "The PersistentClassCourse with " + id + " no longer exists.", e);
                throw new PersistenceException(e);
            }
            em.remove(classCourse);
            em.getTransaction().commit();
        }
        finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public static List<PersistentClassCourse> findEntities() {
        return findEntities(true, -1, -1);
    }

    public static List<PersistentClassCourse> findEntities(int maxResults, int firstResult) {
        return findEntities(false, maxResults, firstResult);
    }

    private static List<PersistentClassCourse> findEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(PersistentClassCourse.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        }
        finally {
            em.close();
        }
    }

    public static List<PersistentClassCourse> findEntities(PersistentSchoolClass c) {
        EntityManager em = getEntityManager();
        try {
            javax.persistence.Query q = em.createNamedQuery("PersistentClassCourse.findByClassID");
            q.setParameter("classID", c.getClassID());
            List<PersistentClassCourse> list = q.getResultList();
            LOG.log(Level.FINE, "ClassCourse-manager retrieved {0} PersistentClassCourse with userid {1}", new Object[]{list.size(), c.getClassID()});
            return list;
        }
        finally {
            em.close();
        }
    }

    public static PersistentClassCourse findEntity(Long id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(PersistentClassCourse.class, id);
        }
        finally {
            em.close();
        }
    }

    public static int getEntityCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<PersistentClassCourse> rt = cq.from(PersistentClassCourse.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        }
        finally {
            em.close();
        }
    }
}
