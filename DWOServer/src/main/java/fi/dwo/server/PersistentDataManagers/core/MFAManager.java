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

public class MFAManager extends AbstractManager {
    private static final Logger LOG = Logger.getLogger(MFAManager.class.getName());


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
            mfa.changetTimestamp();
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
    	return find(id, PersistentMFA.class);
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
