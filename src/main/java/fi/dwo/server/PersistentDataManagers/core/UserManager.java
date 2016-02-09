/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.dwo.server.PersistentDataManagers.core;

import fi.dwo.commons.persistence.entities.PersistentSchoolGroup;
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
 * Manages users in the persistent storage. Sample UserManager for building more
 * code. Also useful as it is being reused.
 *
 * @author G.A.J. van der Plas
 */
public class UserManager {

    private static final Logger LOG = Logger.getLogger(UserManager.class.getName());

    private static EntityManager getEntityManager() {
        EntityManager em = DwoEmfFactory.getEntityManager();
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
        }catch (EntityExistsException ex){
            LOG.log(Level.SEVERE, "Can't create the PersistentUser.", ex);
            throw ex;
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Can't create the PersistentUser.", e);
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
    public static void edit(PersistentUser persistentUser) throws PersistenceException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            persistentUser = em.merge(persistentUser);
            em.getTransaction().commit();
        } catch (Exception e) {
            String msg = e.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Long id = persistentUser.getUserID();
                if (findEntity(id) == null) {
                    LOG.log(Level.FINE, "The PersistentUser with " + id + " no longer exists.", e);
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
            PersistentUser persistentUser = null;
            try {
                persistentUser = em.getReference(PersistentUser.class, id);
                persistentUser.getUserID();
            } catch (EntityNotFoundException e) {
                LOG.log(Level.FINE, "The PersistentUser with " + id + " no longer exists.", e);
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



    public static List<PersistentUser> findEntities(PersistentSchoolGroup sg) {
        EntityManager em = DwoEmfFactory.getEntityManager();
        List<PersistentUser> userList = null;
        try {
            javax.persistence.Query q = em.createNamedQuery("PersistentUser.findBySchoolGroupID");
            q.setParameter("schoolGroupID", sg.getSchoolGroupID());
            userList = (List<PersistentUser>) q.getResultList();
            LOG.log(Level.FINE, "PersistentUser-manager retrieved {0} user with schoolGroupId {1}", new Object[]{userList.size(), sg.getGroupID()});
        }catch(NoResultException e){
            return null;
        }catch(Exception e){
            throw new PersistenceException(e);
        }finally {
            em.close();
        }
        return userList;
    }
    
    
    public static PersistentUser findEntity(Long id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(PersistentUser.class, id);
        } finally {
            em.close();
        }
    }

    public static int getEntityCount() {
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

    /**
     * returns null if no user with that name was found.
     * 
     * @param userName
     * @return 
     */
    public static PersistentUser findByUserName(String userName) {
        EntityManager em = DwoEmfFactory.getEntityManager();
        PersistentUser user = null;
        try {
            javax.persistence.Query q = em.createNamedQuery("PersistentUser.findByUsername");
            q.setParameter("username", userName);
            user = (PersistentUser) q.getSingleResult();
            LOG.log(Level.FINE, "PersistentUser-manager retrieved user with username {0}", new Object[]{user.getUsername()});
        }catch(NoResultException e){
            return null;
        }catch(Exception e){
            throw new PersistenceException(e);
        }finally {
            em.close();
        }
        return user;
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
