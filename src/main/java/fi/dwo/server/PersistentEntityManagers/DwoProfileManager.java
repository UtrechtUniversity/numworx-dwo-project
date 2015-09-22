/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.dwo.server.PersistentEntityManagers;

import fi.dwo.commons.persistence.entities.PersistentDwoProfile;
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
 * Manages DwoProfiles in the persistent storage. 
 *
 * @author G.A.J. van der Plas
 */
public class DwoProfileManager {

    private static final Logger LOG = Logger.getLogger(DwoProfileManager.class.getName());

    private static EntityManager getEntityManager() {
        EntityManager em = DwoEmfFactory.getEntityManager();
        return em;
    }

    /**
     * Create.
     *
     * @param dwoProfile
     */
    public static void create(PersistentDwoProfile dwoProfile) throws PersistenceException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            em.persist(dwoProfile);
            em.getTransaction().commit();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Can't create the PersistentDwoProfile.", e);
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
     * @param dwoProfile
     * @throws Exception
     */
    public static void edit(PersistentDwoProfile dwoProfile) throws PersistenceException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            dwoProfile = em.merge(dwoProfile);
            em.getTransaction().commit();
        } catch (Exception e) {
            String msg = e.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = dwoProfile.getDwoProfileID();
                if (findEntity(id) == null) {
                    LOG.log(Level.FINE, "The PersistentDwoProfile with " + id + " no longer exists.", e);
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
            PersistentDwoProfile dwoProfile = null;
            try {
                dwoProfile = em.getReference(PersistentDwoProfile.class, id);
                dwoProfile.getDwoProfileID();
            } catch (EntityNotFoundException e) {
                LOG.log(Level.FINE, "The PersistentDwoProfile with " + id + " no longer exists.", e);
                throw new PersistenceException(e);
            }
            em.remove(dwoProfile);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public static List<PersistentDwoProfile> findEntities() {
        return findEntities(true, -1, -1);
    }

    public static List<PersistentDwoProfile> findEntities(int maxResults, int firstResult) {
        return findEntities(false, maxResults, firstResult);
    }

    private static List<PersistentDwoProfile> findEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(PersistentDwoProfile.class));
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

    public static PersistentDwoProfile findEntity(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(PersistentDwoProfile.class, id);
        } finally {
            em.close();
        }
    }

    public static int getEntityCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<PersistentDwoProfile> rt = cq.from(PersistentDwoProfile.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }

}
