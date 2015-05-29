/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.dwo.server.PersistentEntityManagers;

import fi.dwo.commons.persistence.entities.PersistentUser;
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
public class UserManager {

    private static final Logger LOG = Logger.getLogger(UserManager.class.getName());

    private static EntityManager getEntityManager() {
        EntityManager em = DwoEmfFactory.createEntityManager();
        return em;
    }

    /**
     * Create.
     *
     * @param persistentUser
     */
    public static void create(PersistentUser persistentUser) throws PersistenceException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            em.persist(persistentUser);
            em.getTransaction().commit();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Can't create the user.", e);
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
     * @param persistentUser
     * @throws NonexistentEntityException
     * @throws Exception
     */
    public static void edit(PersistentUser persistentUser) throws PersistenceException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            persistentUser = em.merge(persistentUser);
            em.getTransaction().commit();
        } catch (Exception e) {
            String msg = e.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = persistentUser.getUserID();
                if (findPersistentUser(id) == null) {
                    LOG.log(Level.FINE, "The persistentUser with " + id + " no longer exists.", e);
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
    public static void destroy(Integer id) throws PersistenceException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            PersistentUser persistentUser = null;
            try {
                persistentUser = em.getReference(PersistentUser.class, id);
                persistentUser.getUserID();
            } catch (EntityNotFoundException e) {
                LOG.log(Level.FINE, "The persistentUser with " + id + " no longer exists.", e);
                throw new PersistenceException(e);
            }
            em.remove(persistentUser);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public static List<PersistentUser> findPersistentUserEntities() {
        return findPersistentUserEntities(true, -1, -1);
    }

    public static List<PersistentUser> findPersistentUserEntities(int maxResults, int firstResult) {
        return findPersistentUserEntities(false, maxResults, firstResult);
    }

    private static List<PersistentUser> findPersistentUserEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(PersistentUser.class));
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

    public static PersistentUser findPersistentUser(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(PersistentUser.class, id);
        } finally {
            em.close();
        }
    }

    public static int getPersistentUserCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<PersistentUser> rt = cq.from(PersistentUser.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }

    public static PersistentUser findByUserName(String userName) {
        EntityManager em = DwoEmfFactory.createEntityManager();
        PersistentUser user = null;
        try {
            javax.persistence.Query q = em.createNamedQuery("PersistentUser.findByUsername");
            q.setParameter("username", userName);
            user = (PersistentUser) q.getSingleResult();
            LOG.log(Level.INFO, "User-manager retrieved user with username {0}", new Object[]{user.getUsername()});
        } finally {
            em.close();
        }
        return user;
    }

    /**
     * User manager. Resolves links.
     *
     * @param sc
     * @return Returns null if there was an error or login failed.
     */
    public static PersistentUser login(String userName, String passwd) {
        PersistentUser user = null;
        EntityManager em = getEntityManager();
        try {
            javax.persistence.Query q = em.createNamedQuery("PersistentUser.findByUsername");
            q.setParameter("username", userName);
            user = (PersistentUser) q.getSingleResult();
            if (user.getPasswd().compareTo(passwd) != 0) {
                return null;
            }
            LOG.log(Level.INFO, "Login accepted for user with username {0}", new Object[]{userName});
        } finally {
            em.close();
        }
        return user;
    }

}
