package fi.dwo.server.PersistentDataManagers.core;

import fi.dwo.commons.persistence.entities.PersistentHasRole;
import fi.dwo.commons.persistence.entities.PersistentScoContext;
import fi.dwo.commons.persistence.entities.PersistentStudentModelContext;
import fi.dwo.commons.persistence.entities.PersistentStudentModelData;
import fi.dwo.server.persistence.DwoEmfFactory;

import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelData;
import nl.uu.fi.dwo.rest.util.DwoDateUtilities;

/**
 * Manages analytical models in the persistent storage.
 *
 * @author G.A.J. van der Plas
 */
public class StudentModelDataManager {

    private static final Logger LOG = Logger.getLogger(StudentModelDataManager.class.getName());

    private static EntityManager getEntityManager() {
        EntityManager em = DwoEmfFactory.getEntityManager();
        return em;
    }

    /**
     * Create.
     *
     * @param data
     * @return
     */
    public static PersistentStudentModelData create(PersistentStudentModelData data) throws PersistenceException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            data.setLastChangeTimeStamp(DwoDateUtilities.getCurrentDwoUnixTimeStamp());
            em.persist(data);
            em.getTransaction().commit();
            return data;
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Can't create the PersistentStudentModelData.", e);
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
     * @param data
     * @return JPA merged course
     */
    public static PersistentStudentModelData edit(PersistentStudentModelData data) throws PersistenceException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            data.setLastChangeTimeStamp(DwoDateUtilities.getCurrentDwoUnixTimeStamp());
            data = em.merge(data);
            em.getTransaction().commit();
        } catch (Exception e) {
            String msg = e.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Long id = data.getModelDataId();
                if (findEntity(id) == null) {
                    LOG.log(Level.INFO, "The PersistentStudentModelData with " + id + " no longer exists.", e);
                    throw new PersistenceException(e);
                }
            }
            throw new PersistenceException(e);
        } finally {
            if (em != null) {
                em.close();
            }
        }
        return data;
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
            PersistentStudentModelData model = null;
            try {
                model = em.getReference(PersistentStudentModelData.class, id);
                model.getModelDataId();
            } catch (EntityNotFoundException e) {
                LOG.log(Level.FINE, "The PersistentStudentModelData with " + id + " no longer exists.", e);
                throw new PersistenceException(e);
            }
            em.remove(model);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public static List<PersistentStudentModelData> findEntities() {
        return findEntities(true, -1, -1);
    }

    public static List<PersistentStudentModelData> findEntities(int maxResults, int firstResult) {
        return findEntities(false, maxResults, firstResult);
    }

    private static List<PersistentStudentModelData> findEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(PersistentStudentModelData.class));
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

    public static PersistentStudentModelData findEntity(Long id) throws PersistenceException {
        EntityManager em = getEntityManager();
        try {
            return em.find(PersistentStudentModelData.class, id);
        } catch (PersistenceException e) {
            LOG.log(Level.FINE, "The PersistentStudentModelData with " + id + " was not found.", e);
            throw e;
        } finally {
            em.close();
        }
    }

    public static int getEntityCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<PersistentStudentModelData> rt = cq.from(PersistentStudentModelData.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }

    public static DomStudentModelData insertOrUpdate(PersistentStudentModelData data) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Return null if none or more than one found.
     * 
     * @param school
     * @param hasRole
     * @param nativeScoId
     * @return 
     */
    public static PersistentStudentModelData findEntity(PersistentScoContext ctx, PersistentHasRole hasRole) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            javax.persistence.Query q = em.createNamedQuery("PersistentStudentModelData.findByUniqueKeys");
            q.setParameter("scoID", ctx.getScoID());
            q.setParameter("modelID", ctx.getModelID());
            q.setParameter("persistentHasRolePK", hasRole.getPersistentHasRolePK());
            List<PersistentStudentModelData> list = q.getResultList();
            if (list.size() != 1) {
                LOG.log(Level.FINE, "StudentModelData-manager retrieved {0} PersistentStudentModelData with modelId {1}, hasRole {2} for scoId {3}", new Object[]{list.size(), ctx.getModelID(), hasRole.getPersistentHasRolePK(),ctx.getScoID()});
                return null;
            }

            em.getTransaction().commit();
            return list.get(0);
        } catch (RuntimeException e) {
            LOG.log(Level.SEVERE, "", e);
            throw e;
        } finally {
            em.close();
        }
    }

    public static List<PersistentStudentModelData> findEntities(PersistentStudentModelContext pStudentModel, PersistentHasRole hasRole) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            javax.persistence.Query q = em.createNamedQuery("PersistentStudentModelData.findStudentScoresOfModel");
            q.setParameter("modelID", pStudentModel.getModelID());
            q.setParameter("persistentHasRolePK", hasRole.getPersistentHasRolePK());
            List<PersistentStudentModelData> list = q.getResultList();

            em.getTransaction().commit();
            return list;
        } catch (RuntimeException e) {
            LOG.log(Level.SEVERE, "", e);
            throw e;
        } finally {
            em.close();
        }
    }

	public static List<PersistentStudentModelData> findEntity(PersistentScoContext pc) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            javax.persistence.TypedQuery<PersistentStudentModelData> q = em.createNamedQuery("PersistentStudentModelData.findByScoId", PersistentStudentModelData.class);
            q.setParameter("scoID", pc.getScoID());
            List<PersistentStudentModelData> list = q.getResultList();
            em.getTransaction().commit();
            return list;
        } catch (RuntimeException e) {
            LOG.log(Level.SEVERE, "", e);
            throw e;
        } finally {
            em.close();
        }
	}

  public static List<PersistentStudentModelData> findEntities(PersistentHasRole hr) {
    EntityManager em = getEntityManager();
    try {
      em.getTransaction().begin();
      TypedQuery<PersistentStudentModelData> q = em.createNamedQuery("PersistentStudentModelData.findByHasRolePK",PersistentStudentModelData.class);
      q.setParameter("persistentHasRolePK", hr.getPersistentHasRolePK());
      List<PersistentStudentModelData> list = q.getResultList();
      em.getTransaction().commit();
      return list;
    } finally {
      em.close();
    }
  }
}
