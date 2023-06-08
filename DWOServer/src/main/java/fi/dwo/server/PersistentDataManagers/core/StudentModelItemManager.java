package fi.dwo.server.PersistentDataManagers.core;

import fi.dwo.commons.persistence.entities.PersistentStudentModelItem;
import fi.dwo.commons.persistence.entities.PersistentStudentModelContext;
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
import nl.uu.fi.dwo.rest.util.DwoDateUtilities;

/**
 * Manages analytical models in the persistent storage. 
 *
 * @author G.A.J. van der Plas
 */
public class StudentModelItemManager {

    private static final Logger LOG = Logger.getLogger(StudentModelItemManager.class.getName());

    private static EntityManager getEntityManager() {
        EntityManager em = DwoEmfFactory.getEntityManager();
        return em;
    }

    /**
     * Create.
     *
     * @param model
     */
    public static PersistentStudentModelItem create(PersistentStudentModelItem model) throws PersistenceException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            model.setLastChangeTimeStamp(DwoDateUtilities.getCurrentDwoUnixTimeStamp());
            em.persist(model);
            em.getTransaction().commit();
            return model;
        } catch (PersistenceException e) {
            LOG.log(Level.SEVERE, "Can't create the PersistentStudentModelItem.", e);
        	throw e;
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Can't create the PersistentStudentModelItem.", e);
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
     * @param model
     * @return jpa merged course
     */
    public static PersistentStudentModelItem edit(PersistentStudentModelItem model) throws PersistenceException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            model.setLastChangeTimeStamp(DwoDateUtilities.getCurrentDwoUnixTimeStamp());            
            model = em.merge(model);
            em.getTransaction().commit();
        } catch (PersistenceException e) {
          String msg = e.getLocalizedMessage();
          if (msg == null || msg.length() == 0) {
              Long id = model.getModelID();
              if (findEntity(id) == null) {
                  LOG.log(Level.INFO, "The PersistentStudentModelItem with " + id + " no longer exists.", e);
                  throw e;
              }
          }
          throw e;
        } catch (Exception e) {
          String msg = e.getLocalizedMessage();
          if (msg == null || msg.length() == 0) {
              Long id = model.getModelID();
              if (findEntity(id) == null) {
                  LOG.log(Level.INFO, "The PersistentStudentModelItem with " + id + " no longer exists.", e);
                  throw new PersistenceException(e);
              }
          }
          throw new PersistenceException(e);
        } finally {
            if (em != null) {
                em.close();
            }
        }
        return model;
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
            PersistentStudentModelItem model = null;
            try {
                model = em.getReference(PersistentStudentModelItem.class, id);
                model.getModelID();
            } catch (EntityNotFoundException e) {
                LOG.log(Level.FINE, "The PersistentStudentModelItem with " + id + " no longer exists.", e);
                throw e;
            }
            em.remove(model);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public static List<PersistentStudentModelItem> findEntities() {
        return findEntities(true, -1, -1);
    }

    public static List<PersistentStudentModelItem> findEntities(int maxResults, int firstResult) {
        return findEntities(false, maxResults, firstResult);
    }

    private static List<PersistentStudentModelItem> findEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(PersistentStudentModelItem.class));
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
        
    public static List<PersistentStudentModelItem> findEntities(PersistentStudentModelContext s) {
        EntityManager em = getEntityManager();
        try {
            javax.persistence.TypedQuery q = em.createNamedQuery("PersistentStudentModelItem.findByModelID", PersistentStudentModelItem.class);
            q.setParameter("modelID", s.getModelID());
            List<PersistentStudentModelItem> list = q.getResultList();
            LOG.log(Level.FINE, "Course-manager retrieved {0} PersistentStudentModelContext with schoolid {1}", new Object[]{list.size(), s.getSchoolID()});
            return list;
        }
        finally {
            em.close();
        }
    }


    public static PersistentStudentModelItem findEntity(Long id) throws PersistenceException {
        EntityManager em = getEntityManager();
        try {
            return em.find(PersistentStudentModelItem.class, id);
         } catch (PersistenceException e) {
            LOG.log(Level.FINE, "The PersistentStudentModelItem with " + id + " was not found.", e);
            throw e;
       } finally {
            em.close();
        }
    }

    public static int getEntityCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery<Long> cq = em.getCriteriaBuilder().createQuery(Long.class);
            Root<PersistentStudentModelItem> rt = cq.from(PersistentStudentModelItem.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }


}
