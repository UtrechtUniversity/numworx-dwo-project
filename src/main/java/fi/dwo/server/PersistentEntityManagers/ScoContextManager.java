/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.dwo.server.PersistentEntityManagers;

import fi.dwo.commons.persistence.entities.PersistentScoContext;
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
 * Manages sco context data in the persistent storage.
 *
 * @author G.A.J. van der Plas
 */
public class ScoContextManager {

    private static final Logger LOG = Logger.getLogger(ScoContextManager.class.getName());

    private static EntityManager getEntityManager() {
        EntityManager em = DwoEmfFactory.createEntityManager();
        return em;
    }

    /**
     * Create.
     *
     * @param sc
     */
    public static void create(PersistentScoContext sc) throws PersistenceException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            em.persist(sc);
            em.getTransaction().commit();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Can't create the PersistentScoContext.", e);
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
     * @param sc
     * @throws Exception
     */
    public static void edit(PersistentScoContext sc) throws PersistenceException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            sc = em.merge(sc);
            em.getTransaction().commit();
        } catch (Exception e) {
            String msg = e.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = sc.getScoID();
                if (findEntity(id) == null) {
                    LOG.log(Level.FINE, "The PersistentScoContext with " + id + " no longer exists.", e);
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
                PersistentScoContext sc = null;
            try {
                sc = em.getReference(PersistentScoContext.class, id);
                sc.getScoID();
            } catch (EntityNotFoundException e) {
                LOG.log(Level.FINE, "The PersistentScoContext with " + id + " no longer exists.", e);
                throw new PersistenceException(e);
            }
            em.remove(sc);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public static List<PersistentScoContext> findEntities() {
        return findEntities(true, -1, -1);
    }

    public static List<PersistentScoContext> findEntities(int maxResults, int firstResult) {
        return findEntities(false, maxResults, firstResult);
    }

    private static List<PersistentScoContext> findEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(PersistentScoContext.class));
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

    public static PersistentScoContext findEntity(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(PersistentScoContext.class, id);
        } finally {
            em.close();
        }
    }

    public static int getEntityCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<PersistentScoContext> rt = cq.from(PersistentScoContext.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }

}
