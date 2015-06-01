/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.dwo.server.PersistentEntityManagers;

import fi.dwo.commons.persistence.entities.PersistentHasRole;
import fi.dwo.commons.persistence.entities.PersistentHasRolePK;
import fi.dwo.server.persistence.DwoEmfFactory;
import fi.dwo.server.persistence.exceptions.NonexistentEntityException;
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
 * Manages users in the persistent storage. Sample UserManager for building more
 * code. Also useful as it is being reused.
 *
 * @author G.A.J. van der Plas
 */
public class HasRoleManager {

    private static final Logger LOG = Logger.getLogger(HasRoleManager.class.getName());

    private static EntityManager getEntityManager() {
        EntityManager em = DwoEmfFactory.createEntityManager();
        return em;
    }

    /**
     * Create.
     *
     * @param role
     */
    public static void create(PersistentHasRole hasRole) throws PersistenceException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            em.persist(hasRole);
            em.getTransaction().commit();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Can't create the hasRole.", e);
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
     * @param role
     * @throws NonexistentEntityException
     * @throws Exception
     */
    public static void edit(PersistentHasRole hasRole) throws PersistenceException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            hasRole = em.merge(hasRole);
            em.getTransaction().commit();
        } catch (Exception e) {
            String msg = e.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                PersistentHasRolePK id = hasRole.getPersistentHasRolePK();
                if (findPersistentHasRole(id) == null) {
                    LOG.log(Level.FINE, "The persistentSchool with " + id + " no longer exists.", e);
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
     * @throws NonexistentEntityException
     */
    public static void destroy(PersistentHasRolePK id) throws PersistenceException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
                PersistentHasRole role = null;
            try {
                role = em.getReference(PersistentHasRole.class, id);
                role.getPersistentHasRolePK();
            } catch (EntityNotFoundException e) {
                LOG.log(Level.FINE, "The PersistentHasRole with " + id + " no longer exists.", e);
                throw new PersistenceException(e);
            }
            em.remove(role);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public static List<PersistentHasRole> findPersistentHasRoleEntities() {
        return findPersistentHasRoleEntities(true, -1, -1);
    }

    public static List<PersistentHasRole> findPersistentHasRoleEntities(int maxResults, int firstResult) {
        return findPersistentHasRoleEntities(false, maxResults, firstResult);
    }

    private static List<PersistentHasRole> findPersistentHasRoleEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(PersistentHasRole.class));
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

    public static PersistentHasRole findPersistentHasRole(PersistentHasRolePK id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(PersistentHasRole.class, id);
        } finally {
            em.close();
        }
    }

    public static int getPersistentHasRoleCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<PersistentHasRole> rt = cq.from(PersistentHasRole.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
}
