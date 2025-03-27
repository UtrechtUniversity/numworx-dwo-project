package fi.dwo.server.PersistentDataManagers.core;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fi.dwo.commons.persistence.entities.PersistentMFA;
import fi.dwo.server.persistence.DwoEmfFactory;

public class MFAManager {
    private static final Logger LOG = Logger.getLogger(MFAManager.class.getName());

    private static EntityManager getEntityManager() {
        EntityManager em = DwoEmfFactory.getEntityManager();
        return em;
    }

    /**
     * Create.
     *
     * @param mfa persistentMFA
     */
    public static void create(PersistentMFA mfa) throws PersistenceException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            em.persist(mfa);
            em.getTransaction().commit();
        } 
        catch (PersistenceException e) {
          LOG.log(Level.SEVERE, "Can't create the PersistentMFA.", e);
          throw e;
        }
        catch (Exception e) {
            LOG.log(Level.SEVERE, "Can't create the PersistentMFA.", e);
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
     * @param mfa persistentApplet
     */
    public static PersistentMFA edit(PersistentMFA mfa) throws PersistenceException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            mfa = em.merge(mfa);
            em.getTransaction().commit();
            return mfa;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    /**
     * Removes a mfa from the persistent store.
     *
     * @param id
     */
    public static void destroy(Long id) throws PersistenceException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            PersistentMFA persistentMFA = null;
            try {
                persistentMFA = em.getReference(PersistentMFA.class, id);
                persistentMFA.getUserID();
            } catch (EntityNotFoundException e) {
            	return; // already removed
            }
            em.remove(persistentMFA);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public static List<PersistentMFA> findEntities() {
        return findEntities(true, -1, -1);
    }

    public static List<PersistentMFA> findEntities(int maxResults, int firstResult) {
        return findEntities(false, maxResults, firstResult);
    }

    private static List<PersistentMFA> findEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery<PersistentMFA> cq = em.getCriteriaBuilder().createQuery(PersistentMFA.class);
            cq.select(cq.from(PersistentMFA.class));
            TypedQuery<PersistentMFA> q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public static PersistentMFA findEntity(Long id) throws PersistenceException{
        EntityManager em = getEntityManager();
        try {
            return em.find(PersistentMFA.class, id);
        } catch (PersistenceException e) {
            LOG.log(Level.FINE, "The PersistentMFA with " + id + " was not found.", e);
            throw e;
        } finally {
            em.close();
        }
    }

    public static int getEntityCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery<Long> cq = em.getCriteriaBuilder().createQuery(Long.class);
            Root<PersistentMFA> rt = cq.from(PersistentMFA.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            TypedQuery<Long> q = em.createQuery(cq);
            return (q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }

  
   
    
    

}
