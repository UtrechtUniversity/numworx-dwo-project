/**
 * Copyrighted Sep 24, 2015
 */

package fi.dwo.server.PersistentEntityManagers;

import fi.dwo.commons.persistence.entities.PersistentHasRolePK;
import fi.dwo.commons.persistence.entities.PersistentSamlUser;
import fi.dwo.commons.persistence.entities.PersistentStudentScoContext;
import fi.dwo.commons.persistence.entities.PersistentUser;
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
 * Manages users in the persistent storage. Sample UserManager for building more
 * code. Also useful as it is being reused.
 *
 * @author G.A.J. van der Plas
 */
public class SamlUserManager {

    private static final Logger LOG = Logger.getLogger(SamlUserManager.class.getName());

    private static EntityManager getEntityManager() {
        EntityManager em = DwoEmfFactory.getEntityManager();
        return em;
    }

    /**
     * Create.
     *
     * @param persistentUser
     */
    public static void create(PersistentSamlUser persistentUser) throws PersistenceException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            em.persist(persistentUser);
            em.getTransaction().commit();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Can't create the PersistentSamlUser.", e);
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
     * @throws Exception
     */
    public static void edit(PersistentSamlUser persistentUser) throws PersistenceException, Exception {
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
                if (findEntity(id) == null) {
                    LOG.log(Level.FINE, "The PersistentSamlUser with " + id + " no longer exists.", e);
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
            PersistentSamlUser persistentUser = null;
            try {
                persistentUser = em.getReference(PersistentSamlUser.class, id);
                persistentUser.getUserID();
            } catch (EntityNotFoundException e) {
                LOG.log(Level.FINE, "The PersistentSamlUser with " + id + " no longer exists.", e);
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

    public static List<PersistentSamlUser> findEntities() {
        return findEntities(true, -1, -1);
    }

    public static List<PersistentSamlUser> findEntities(int maxResults, int firstResult) {
        return findEntities(false, maxResults, firstResult);
    }

    private static List<PersistentSamlUser> findEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(PersistentSamlUser.class));
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
    

    public static List<PersistentSamlUser> findEntities(PersistentUser user) {
        EntityManager em = getEntityManager();
        try {
            javax.persistence.Query q = em.createNamedQuery("PersistentSamlUser.findByUserID");
            q.setParameter("userID", user.getUserID());
            List<PersistentSamlUser> list = q.getResultList();
            LOG.log(Level.FINE, "SamlUser-manager retrieved {0} PersistentSamlUser with userid {1}", new Object[]{list.size(), user.getUserID()});
            return list;
        }
        finally {
            em.close();
        }
    }    

    public static PersistentSamlUser findEntity(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(PersistentSamlUser.class, id);
        } finally {
            em.close();
        }
    }

    public static int getEntityCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<PersistentSamlUser> rt = cq.from(PersistentSamlUser.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }


    /**
     * User manager. Returns null if login validation failed.
     *
     * @param userName
     * @param passwd
     * @return 
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
