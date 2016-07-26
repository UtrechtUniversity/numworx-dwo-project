package fi.dwo.server.PersistentDataManagers.core;

import fi.dwo.commons.persistence.entities.PersistentLoginContext;
import fi.dwo.commons.persistence.entities.PersistentUser;
import fi.dwo.server.persistence.DwoEmfFactory;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

/**
 * Manages LoginContext in the persistent storage. Sample UserManager for building more
 * code. Also useful as it is being reused. The registered timestamp is null if no session
 * is active.
 *
 * @author G.A.J. van der Plas
 */
public class LoginContextManager {

    private static final Logger LOG = Logger.getLogger(LoginContextManager.class.getName());

    private static EntityManager getEntityManager() {
        EntityManager em = DwoEmfFactory.getEntityManager();
        return em;
    }

    /**
     * Create.
     *
     * @param loginContext
     */
    public static void create(PersistentLoginContext loginContext) throws PersistenceException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            em.persist(loginContext);
            em.getTransaction().commit();
        }catch (EntityExistsException ex){
            LOG.log(Level.SEVERE, "Can't create the PersistentLoginContext.", ex);
            throw ex;
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Can't create the PersistentLoginContext.", e);
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
     * @param loginContext
     * @throws Exception
     */
    public static void edit(PersistentLoginContext loginContext) throws PersistenceException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            loginContext = em.merge(loginContext);
            em.getTransaction().commit();
        } catch (Exception e) {
            String msg = e.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Long id = loginContext.getId();
                if (findEntity(id) == null) {
                    LOG.log(Level.FINE, "The LoginContext with " + id + " no longer exists.", e);
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
            PersistentLoginContext loginContext = null;
            try {
                loginContext = em.getReference(PersistentLoginContext.class, id);
                loginContext.getId();
            } catch (EntityNotFoundException e) {
                LOG.log(Level.FINE, "The PersistentLoginContext with " + id + " no longer exists.", e);
                throw new PersistenceException(e);
            }
            em.remove(loginContext);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public static List<PersistentUser> findEntities() {
        return findEntities(true, -1, -1);
    }

    public static List<PersistentUser> findEntities(int maxResults, int firstResult) {
        return findEntities(false, maxResults, firstResult);
    }

    private static List<PersistentUser> findEntities(boolean all, int maxResults, int firstResult) {
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



    public static List<PersistentLoginContext> findEntities(Long userId) {
        EntityManager em = DwoEmfFactory.getEntityManager();
        List<PersistentLoginContext> loginContextList = null;
        try {
            javax.persistence.Query q = em.createNamedQuery("PersistentLoginContext.findByUserID");
            q.setParameter("userID", userId);
            loginContextList = (List<PersistentLoginContext>) q.getResultList();
            LOG.log(Level.FINE, "PersistentUser-manager retrieved {0} user with schoolGroupId {1}", new Object[]{loginContextList.size(), userId});
        }catch(NoResultException e){
            return null;
        }catch(Exception e){
            throw new PersistenceException(e);
        }finally {
            em.close();
        }
        return loginContextList;
    }
    
    
    public static PersistentLoginContext findEntity(Long id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(PersistentLoginContext.class, id);
        } finally {
            em.close();
        }
    }

    public static int getEntityCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<PersistentLoginContext> rt = cq.from(PersistentLoginContext.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }

}
