/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.dwo.server.PersistentDataManagers.core;

import fi.dwo.commons.persistence.entities.PersistentHasRolePK;
import fi.dwo.commons.persistence.entities.PersistentSchoolClass;
import fi.dwo.commons.persistence.entities.PersistentStudentOfClass;
import fi.dwo.commons.persistence.entities.PersistentStudentOfClassPK;
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
 * Manages a student's class membership in the persistent storage. 
 *
 * @author G.A.J. van der Plas
 */
public class StudentOfClassManager {

    private static final Logger LOG = Logger.getLogger(StudentOfClassManager.class.getName());

    private static EntityManager getEntityManager() {
        EntityManager em = DwoEmfFactory.getEntityManager();
        return em;
    }

    /**
     * Create.
     *
     * @param studentOf
     */
    public static void create(PersistentStudentOfClass studentOf) throws PersistenceException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            em.persist(studentOf);
            em.getTransaction().commit();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Can't create the PersistentStudentOfClass.", e);
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
     * @param studentOf
     * @throws Exception
     */
    public static void edit(PersistentStudentOfClass studentOf) throws PersistenceException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            studentOf = em.merge(studentOf);
            em.getTransaction().commit();
        } catch (Exception e) {
            String msg = e.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                PersistentStudentOfClassPK id = studentOf.getPersistentStudentOfClassPK();
                if (findEntity(id) == null) {
                    LOG.log(Level.FINE, "The PersistentStudentOfClass with " + id + " no longer exists.", e);
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
    public static void destroy(PersistentStudentOfClassPK id) throws PersistenceException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
                PersistentStudentOfClass student = null;
            try {
                student = em.getReference(PersistentStudentOfClass.class, id);
                student.getPersistentStudentOfClassPK();
            } catch (EntityNotFoundException e) {
                LOG.log(Level.FINE, "The PersistentStudentOfClass with " + id + " no longer exists.", e);
                throw new PersistenceException(e);
            }
            em.remove(student);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public static List<PersistentStudentOfClass> findEntities() {
        return findEntities(true, -1, -1);
    }

    public static List<PersistentStudentOfClass> findEntities(int maxResults, int firstResult) {
        return findEntities(false, maxResults, firstResult);
    }

    private static List<PersistentStudentOfClass> findEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(PersistentStudentOfClass.class));
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

    public static List<PersistentStudentOfClass> findEntities(PersistentHasRolePK key) {
        EntityManager em = getEntityManager();
        try {
            javax.persistence.Query q = em.createNamedQuery("PersistentStudentOfClass.findByHasRolePK");
            q.setParameter("userID", key.getUserID());
            q.setParameter("schoolGroupID", key.getSchoolGroupID());
            List<PersistentStudentOfClass> list = q.getResultList();
            LOG.log(Level.FINE, "PersistentStudentOfClass-manager retrieved {0} PersistentStudentOfClass with userid {1}", new Object[]{list.size(), key});
            return list;
        }
        finally {
            em.close();
        }
    }

    public static List<PersistentStudentOfClass> findEntities(PersistentSchoolClass sc) {
        EntityManager em = getEntityManager();
        try {
            javax.persistence.Query q = em.createNamedQuery("PersistentStudentOfClass.findByClassID");
            q.setParameter("classID", sc.getClassID());
            List<PersistentStudentOfClass> list = q.getResultList();
            LOG.log(Level.FINE, "PersistentStudentOfClass-manager retrieved {0} PersistentStudentOfClass with classid {1}", new Object[]{list.size(), sc.getClassID()});
            return list;
        }
        finally {
            em.close();
        }
    }    
    
    public static PersistentStudentOfClass findEntity(PersistentStudentOfClassPK id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(PersistentStudentOfClass.class, id);
        } finally {
            em.close();
        }
    }

    public static int getEntityCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<PersistentStudentOfClass> rt = cq.from(PersistentStudentOfClass.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
}
