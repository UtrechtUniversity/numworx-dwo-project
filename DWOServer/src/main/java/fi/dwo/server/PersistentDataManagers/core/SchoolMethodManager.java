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

import fi.dwo.commons.persistence.entities.PersistentMethod;
import fi.dwo.commons.persistence.entities.PersistentSchool;
import fi.dwo.commons.persistence.entities.PersistentSchoolMethod;
import fi.dwo.commons.persistence.entities.PersistentSchoolMethodPK;
import fi.dwo.server.persistence.DwoEmfFactory;

public class SchoolMethodManager extends AbstractManager {

    private static final Logger LOG = Logger.getLogger(SchoolMethodManager.class.getName());

	public static List<PersistentSchoolMethod> findEntities() {
		return findEntities(true, 0,0);
	}
 
	private static List<PersistentSchoolMethod> findEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery<PersistentSchoolMethod> cq = em.getCriteriaBuilder().createQuery(PersistentSchoolMethod.class);
            cq.select(cq.from(PersistentSchoolMethod.class));
            TypedQuery<PersistentSchoolMethod> q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

	public static List<PersistentSchoolMethod> findEntities(PersistentSchool school) {
		EntityManager em = getEntityManager();
		try {
			TypedQuery<PersistentSchoolMethod> query = em.createNamedQuery("PersistentSchoolMethod.findBySchoolID", PersistentSchoolMethod.class);
			query.setParameter("schoolID", school.getSchoolID());
			return query.getResultList();
		} finally {
			em.close();
		}
	}

	public static PersistentSchoolMethod edit(PersistentSchoolMethod sm) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            sm.changeTimestamp();
            sm = em.merge(sm);
            em.getTransaction().commit();
            return sm;
        } catch (PersistenceException e) {
        	throw e;
        } catch (Exception e) {
            String msg = e.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                PersistentSchoolMethodPK id = sm.getId();
                if (findEntity(id) == null) {
                    LOG.log(Level.FINE, "The PersistentSchoolMethod with " + id + " no longer exists.", e);
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

	public static void destroy(PersistentSchoolMethodPK id) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            PersistentSchoolMethod method = null;
            try {
                method = em.getReference(PersistentSchoolMethod.class, id);
                method.getMethodID();
            } catch (EntityNotFoundException e) {
                LOG.log(Level.FINE, "The PersistentSchoolMethod with " + id + " no longer exists.", e);
                throw e;
            }
            em.remove(method);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
	}

	public static int getEntityCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery<Long> cq = em.getCriteriaBuilder().createQuery(Long.class);
            Root<PersistentSchoolMethod> rt = cq.from(PersistentSchoolMethod.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            TypedQuery<Long> q = em.createQuery(cq);
            return q.getSingleResult().intValue();
        } finally {
            em.close();
        }
	}
	
    public static PersistentSchoolMethod findEntity(PersistentSchoolMethodPK id) throws PersistenceException{
        return find(id, PersistentSchoolMethod.class);
    }

}
