/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.dwo.server.PersistentEntityManagers;

import fi.dwo.commons.persistence.entities.PersistentHasRolePK;
import fi.dwo.commons.persistence.entities.PersistentSchoolClass;
import fi.dwo.commons.persistence.entities.PersistentTeacherOfClass;
import fi.dwo.commons.persistence.entities.PersistentTeacherOfClassPK;
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
 * Manages a teacher's class membership in the persistent storage. 
 *
 * @author G.A.J. van der Plas
 */
public class TeacherOfClassManager {

    private static final Logger LOG = Logger.getLogger(TeacherOfClassManager.class.getName());

    private static EntityManager getEntityManager() {
        EntityManager em = DwoEmfFactory.getEntityManager();
        return em;
    }

    /**
     * Create.
     *
     * @param teacherOf
     */
    public static void create(PersistentTeacherOfClass teacherOf) throws PersistenceException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            em.persist(teacherOf);
            em.getTransaction().commit();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Can't create the PersistentTeacherOfClass.", e);
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
     * @param teacherOf
     * @throws Exception
     */
    public static void edit(PersistentTeacherOfClass teacherOf) throws PersistenceException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            teacherOf = em.merge(teacherOf);
            em.getTransaction().commit();
        } catch (Exception e) {
            String msg = e.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                PersistentTeacherOfClassPK id = teacherOf.getPersistentTeacherOfClassPK();
                if (findEntity(id) == null) {
                    LOG.log(Level.FINE, "The PersistentTeacherOfClass with " + id + " no longer exists.", e);
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
    public static void destroy(PersistentTeacherOfClassPK id) throws PersistenceException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
                PersistentTeacherOfClass teacherOf = null;
            try {
                teacherOf = em.getReference(PersistentTeacherOfClass.class, id);
                teacherOf.getPersistentTeacherOfClassPK();
            } catch (EntityNotFoundException e) {
                LOG.log(Level.FINE, "The PersistentTeacherOfClass with " + id + " no longer exists.", e);
                throw new PersistenceException(e);
            }
            em.remove(teacherOf);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public static List<PersistentTeacherOfClass> findEntities() {
        return findEntities(true, -1, -1);
    }

    public static List<PersistentTeacherOfClass> findEntities(int maxResults, int firstResult) {
        return findEntities(false, maxResults, firstResult);
    }

    private static List<PersistentTeacherOfClass> findEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(PersistentTeacherOfClass.class));
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
    
    public static List<PersistentTeacherOfClass> findEntities(PersistentHasRolePK key) {
        EntityManager em = getEntityManager();
        try {
            javax.persistence.Query q = em.createNamedQuery("PersistentTeacherOfClass.findByHasRolePK");
            q.setParameter("userID", key.getUserID());
            q.setParameter("schoolGroupID", key.getSchoolGroupID());
            List<PersistentTeacherOfClass> list = q.getResultList();
            LOG.log(Level.FINE, "PersistentTeacherOfClass-manager retrieved {0} PersistentTeacherOfClass with userid {1}", new Object[]{list.size(), key});
            return list;
        }
        finally {
            em.close();
        }
    }

    public static List<PersistentTeacherOfClass> findEntities(PersistentSchoolClass sc) {
        EntityManager em = getEntityManager();
        try {
            javax.persistence.Query q = em.createNamedQuery("PersistentTeacherOfClass.findByClassID");
            q.setParameter("classID", sc.getClassID());
            List<PersistentTeacherOfClass> list = q.getResultList();
            LOG.log(Level.FINE, "PersistentTeacherOfClass-manager retrieved {0} PersistentTeacherOfClass with classid {1}", new Object[]{list.size(), sc.getClassID()});
            return list;
        }
        finally {
            em.close();
        }
    }        
    
    public static PersistentTeacherOfClass findEntity(PersistentTeacherOfClassPK id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(PersistentTeacherOfClass.class, id);
        } finally {
            em.close();
        }
    }

    public static int getEntityCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<PersistentTeacherOfClass> rt = cq.from(PersistentTeacherOfClass.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
}
