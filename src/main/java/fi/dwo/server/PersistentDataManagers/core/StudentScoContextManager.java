/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.dwo.server.PersistentDataManagers.core;

import fi.dwo.commons.persistence.entities.PersistentHasRolePK;
import fi.dwo.commons.persistence.entities.PersistentScoContext;
import fi.dwo.commons.persistence.entities.PersistentStudentScoContext;
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
 * Manages a student's studentScoContext data in the persistent storage. 
 *
 * @author G.A.J. van der Plas
 */
public class StudentScoContextManager {

    private static final Logger LOG = Logger.getLogger(StudentScoContextManager.class.getName());

    private static EntityManager getEntityManager() {
        EntityManager em = DwoEmfFactory.getEntityManager();
        return em;
    }

    /**
     * Create.
     *
     * @param studentOf
     */
    public static void create(PersistentStudentScoContext studentOf) throws PersistenceException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            em.persist(studentOf);
            em.getTransaction().commit();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Can't create the PersistentStudentScoContext.", e);
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
    public static PersistentStudentScoContext edit(PersistentStudentScoContext studentOf) throws PersistenceException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            studentOf = em.merge(studentOf);
            em.getTransaction().commit();
            return studentOf;
        } catch (Exception e) {
            String msg = e.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Long id = studentOf.getScoID();
                if (findEntity(id) == null) {
                    LOG.log(Level.FINE, "The PersistentStudentScoContext with " + id + " no longer exists.", e);
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
                PersistentStudentScoContext ssc = null;
            try {
                ssc = em.getReference(PersistentStudentScoContext.class, id);
                ssc.getStudentSco();
            } catch (EntityNotFoundException e) {
                LOG.log(Level.FINE, "The PersistentStudentScoContext with " + id + " no longer exists.", e);
                throw new PersistenceException(e);
            }
            em.remove(ssc);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public static List<PersistentStudentScoContext> findEntities() {
        return findEntities(true, -1, -1);
    }

    public static List<PersistentStudentScoContext> findEntities(int maxResults, int firstResult) {
        return findEntities(false, maxResults, firstResult);
    }

    private static List<PersistentStudentScoContext> findEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(PersistentStudentScoContext.class));
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

    public static List<PersistentStudentScoContext> findEntities(PersistentScoContext scoContext) {
        EntityManager em = getEntityManager();
        try {
            javax.persistence.Query q = em.createNamedQuery("PersistentStudentScoContext.findByScoID");
            q.setParameter("scoID", scoContext.getScoID());
            List<PersistentStudentScoContext> list = q.getResultList();
            LOG.log(Level.FINE, "StudentScoContextManager-manager retrieved {0} PersistentStudentScoContext with scoId {1}", new Object[]{list.size(), scoContext.getScoID()});
            return list;
        }
        finally {
            em.close();
        }
    }

public static List<PersistentStudentScoContext> findEntities(PersistentScoContext scoContext, PersistentHasRolePK key) {
        EntityManager em = getEntityManager();
        try {
            javax.persistence.Query q = em.createNamedQuery("PersistentStudentScoContext.findByScoIDandHasRolePK");
            q.setParameter("scoID", scoContext.getScoID());
            q.setParameter("keyID", key);
            List<PersistentStudentScoContext> list = q.getResultList();
            LOG.log(Level.FINE, "StudentScoContextManager-manager retrieved {0} PersistentStudentScoContext with scoId {1} and key {2}", new Object[]{list.size(), scoContext.getScoID(),key.toString()});
            return list;
        }
        finally {
            em.close();
        }
    }    

    public static List<PersistentStudentScoContext> findEntities(PersistentHasRolePK key) {
        EntityManager em = getEntityManager();
        try {
            javax.persistence.Query q = em.createNamedQuery("PersistentStudentScoContext.findByHasRolePK");
            q.setParameter("keyID", key);
            List<PersistentStudentScoContext> list = q.getResultList();
            LOG.log(Level.FINE, "StudentScoContextManager-manager retrieved {0} PersistentStudentScoContext with key {1}", new Object[]{list.size(), key.toString()});
            return list;
        }
        finally {
            em.close();
        }
    }
    
    public static PersistentStudentScoContext findEntity(Long id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(PersistentStudentScoContext.class, id);
        } finally {
            em.close();
        }
    }

    public static int getEntityCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<PersistentStudentScoContext> rt = cq.from(PersistentStudentScoContext.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
}
