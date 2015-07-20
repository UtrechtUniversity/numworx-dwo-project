/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.dwo.server.PersistentEntityManagers;

import fi.dwo.commons.persistence.entities.PersistentCourse;
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
 * Manages courses in the persistent storage. 
 *
 * @author G.A.J. van der Plas
 */
public class CourseManager {

    private static final Logger LOG = Logger.getLogger(CourseManager.class.getName());

    private static EntityManager getEntityManager() {
        EntityManager em = DwoEmfFactory.createEntityManager();
        return em;
    }

    /**
     * Create.
     *
     * @param course
     */
    public static void create(PersistentCourse course) throws PersistenceException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            em.persist(course);
            em.getTransaction().commit();
        } catch (Exception e) {
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
     * @throws Exception
     */
    public static void edit(PersistentCourse course) throws PersistenceException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            course = em.merge(course);
            em.getTransaction().commit();
        } catch (Exception e) {
            String msg = e.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = course.getCourseID();
                if (findEntity(id) == null) {
                    LOG.log(Level.FINE, "The PersistentCourse with " + id + " no longer exists.", e);
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
            PersistentCourse course = null;
            try {
                course = em.getReference(PersistentCourse.class, id);
                course.getCourseID();
            } catch (EntityNotFoundException e) {
                LOG.log(Level.FINE, "The PersistentCourse with " + id + " no longer exists.", e);
                throw new PersistenceException(e);
            }
            em.remove(course);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public static List<PersistentCourse> findEntities() {
        return findEntities(true, -1, -1);
    }

    public static List<PersistentCourse> findEntities(int maxResults, int firstResult) {
        return findEntities(false, maxResults, firstResult);
    }

    private static List<PersistentCourse> findEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(PersistentCourse.class));
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

    public static PersistentCourse findEntity(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(PersistentCourse.class, id);
        } finally {
            em.close();
        }
    }

    public static int getEntityCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<PersistentCourse> rt = cq.from(PersistentCourse.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }

    /**
     * returns null if no applet with that name was found.courseName@param appletName
     * @return 
     */
    public static PersistentCourse findByUserName(String courseName) {
        EntityManager em = DwoEmfFactory.createEntityManager();
        PersistentCourse course = null;
        try {
            javax.persistence.Query q = em.createNamedQuery("PersistentCourse.findByCourseName");
            q.setParameter("name", courseName);
            course = (PersistentCourse) q.getSingleResult();
            LOG.log(Level.FINE, "PersistentCourse-manager retrieved user with course name {0}", new Object[]{course.getName()});
        }catch(NoResultException e){
            return null;
        }catch(Exception e){
            throw new PersistenceException(e);
        }finally {
            em.close();
        }
        return course;
    }

}
