package fi.dwo.server.PersistentDataManagers.core;

import fi.dwo.commons.persistence.entities.PersistentLogData;
import fi.dwo.commons.persistence.entities.PersistentLoginDataPK;
import fi.dwo.commons.persistence.entities.PersistentTeacherOfClass;
import fi.dwo.server.persistence.DwoEmfFactory;
import java.util.ArrayList;
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
 *
 * @author G.A.J. van der Plas
 */
public class LoginDataManager {

    private static final Logger LOG = Logger.getLogger(LoginDataManager.class.getName());

    private static EntityManager getEntityManager() {
        EntityManager em = DwoEmfFactory.getEntityManager();
        return em;
    }

    /**
     * Create.
     *
     * @param submit
     */
    public static void create(PersistentLogData submit) throws PersistenceException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            em.persist(submit);
            em.getTransaction().commit();
        }
        catch (Exception e) {
            LOG.log(Level.SEVERE, "Can't create the LoginDataManager.", e);
            throw new PersistenceException(e);
        }
        finally {
            if (em != null) {
                em.close();
            }
        }
    }

    /**
     * Update
     *
     * @param submit
     */
    public static void edit(PersistentLogData submit) throws PersistenceException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            submit = em.merge(submit);
            em.getTransaction().commit();
        }
        catch (Exception e) {
            String msg = e.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                PersistentLoginDataPK id = submit.getPersistentLoginDataPK();
                if (findEntity(id) == null) {
                    LOG.log(Level.FINE, "The PersistentLoginData with " + id + " no longer exists.", e);
                    throw new PersistenceException(e);
                }
            }
            throw new PersistenceException(e);
        }
        finally {
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
    public static void destroy(PersistentLoginDataPK id) throws PersistenceException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            PersistentLogData data = null;
            try {
                data = em.getReference(PersistentLogData.class, id);
                data.getPersistentLoginDataPK();
            }
            catch (EntityNotFoundException e) {
                LOG.log(Level.FINE, "The PersistentLoginData with " + id + " no longer exists.", e);
                throw new PersistenceException(e);
            }
            em.remove(data);
            em.getTransaction().commit();
        }
        finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public static List<PersistentLogData> findEntities(long fromTimeStamp, long toTimeStamp) {
        EntityManager em = getEntityManager();
        try {
            javax.persistence.Query q = em.createNamedQuery("PersistentLoginData.findByTimeStampRange");
            q.setParameter("fromTimestamp", fromTimeStamp);
            q.setParameter("toTimestamp", toTimeStamp);
            List<PersistentLogData> list = q.getResultList();
            LOG.log(Level.FINE, "PersistentLoginData-manager retrieved {0} PersistentLoginData with timestamps from {1} to {2}", new Object[]{list.size(), fromTimeStamp, toTimeStamp});
            return list;
        }
        catch (Exception e) {
            LOG.log(Level.FINE, "Exception retrieving PersistentLoginData.", e);
            return new ArrayList<>();
        }
        finally {
            em.close();
        }
    }

    public static List<PersistentLogData> findEntities(int fromTimeStamp, long toTimeStamp, String username) {
        EntityManager em = getEntityManager();
        try {
            javax.persistence.Query q = em.createNamedQuery("PersistentLoginData.findByTimeStampRangeAndUsername");
            q.setParameter("fromTimestamp", fromTimeStamp);
            q.setParameter("toTimestamp", toTimeStamp);
            List<PersistentLogData> list = q.getResultList();
            LOG.log(Level.FINE, "PersistentLoginData-manager retrieved {0} PersistentLoginData with timestamps from {1} to {2} and username {3}", new Object[]{list.size(), fromTimeStamp, toTimeStamp, username});
            return list;
        }
        catch (Exception e) {
            LOG.log(Level.FINE, "Exception retrieving PersistentLoginData.", e);
            return new ArrayList<>();
        }
        finally {
            em.close();
        }
    }

    public static PersistentLogData findEntity(PersistentLoginDataPK id) {
        EntityManager em = getEntityManager();

        try {
            return em.find(PersistentLogData.class, id);
        }
        finally {
            em.close();
        }
    }

    public static int getEntityCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<PersistentTeacherOfClass> rt = cq.from(PersistentTeacherOfClass.class
            );
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        }
        finally {
            em.close();
        }
    }
}
