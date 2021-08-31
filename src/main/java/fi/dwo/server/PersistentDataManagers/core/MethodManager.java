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

import com.owlike.genson.Genson;
import com.owlike.genson.GensonBuilder;

import fi.dwo.commons.persistence.entities.PersistentMethod;
import fi.dwo.commons.persistence.entities.PersistentSchool;
import fi.dwo.server.persistence.DwoEmfFactory;
import nl.uu.fi.dwo.rest.dom.entities.DomMethod;

public class MethodManager {
    private static final Logger LOG = Logger.getLogger(MethodManager.class.getName());
    private static final Long NUL = Long.valueOf(0L);

	private MethodManager() {
	}

	private static EntityManager getEntityManager() {
        EntityManager em = DwoEmfFactory.getEntityManager();
        return em;
    }
    /**
     * Create.
     *
     * @param method
     */
    public static void create(PersistentMethod method) throws PersistenceException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            em.persist(method);
            em.getTransaction().commit();
        } catch (PersistenceException e0) {
            LOG.log(Level.SEVERE, "Can't create the role.", e0);
            throw e0;
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Can't create the role.", e);
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
     * @param method
     * @return 
     * @throws Exception
     */
    public static PersistentMethod edit(PersistentMethod method) throws PersistenceException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            method = em.merge(method);
            em.getTransaction().commit();
            return method;
        } catch (PersistenceException e) {
        	throw e;
        } catch (Exception e) {
            String msg = e.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                String id = method.getMethodID();
                if (findEntity(id) == null) {
                    LOG.log(Level.FINE, "The PersistentMethod with " + id + " no longer exists.", e);
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
    public static void destroy(String id) throws PersistenceException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            PersistentMethod method = null;
            try {
                method = em.getReference(PersistentMethod.class, id);
                method.getMethodID();
            } catch (EntityNotFoundException e) {
                LOG.log(Level.FINE, "The PersistentMethod with " + id + " no longer exists.", e);
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

    public static List<PersistentMethod> findEntities() {
        return findEntities(true, -1, -1);
    }

    public static List<PersistentMethod> findEntities(int maxResults, int firstResult) {
        return findEntities(false, maxResults, firstResult);
    }

    private static List<PersistentMethod> findEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery<PersistentMethod> cq = em.getCriteriaBuilder().createQuery(PersistentMethod.class);
            cq.select(cq.from(PersistentMethod.class));
            TypedQuery<PersistentMethod> q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public static PersistentMethod findEntity(String id) throws PersistenceException{
        EntityManager em = getEntityManager();
        try {
            return em.find(PersistentMethod.class, id);
        } catch (PersistenceException e) {
            LOG.log(Level.FINE, "The PersistentRole with " + id + " was not found.", e);
            throw e;
        } finally {
            em.close();
        }
    }

    public static int getEntityCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery<Long> cq = em.getCriteriaBuilder().createQuery(Long.class);
            Root<PersistentMethod> rt = cq.from(PersistentMethod.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            TypedQuery<Long> q = em.createQuery(cq);
            return q.getSingleResult().intValue();
        } finally {
            em.close();
        }
    }

    static Genson genson = new GensonBuilder().setSkipNull(true).exclude("standard").create();
    
    
    public static DomMethod toDom(PersistentMethod p) {
    	DomMethod dm = genson.deserialize(p.getMethod(), DomMethod.class);
    	dm.setId(p.buildPersistenceId());
    	dm.setOptLock(p.getOptlock());
    	dm.standard = NUL.equals(p.getSchoolID());
    	return dm;
    }

    public static PersistentMethod toValue(DomMethod m, PersistentSchool school) {
    	PersistentMethod p = new PersistentMethod();
    	DomMethod dm = new DomMethod();
    	dm.method = m.method;
    	dm.chapters = m.chapters;
    	dm.edges = m.edges;
    	dm.books = m.books;
    	p.setMethod(genson.serialize(dm));
    	p.setOptlock(m.getOptLock());
    	p.setSchoolID(m.standard ? NUL : school.getSchoolID());
    	p.setMethodID(m.getId().getIdString());
    	return p;
    }

	public static List<PersistentMethod> findEntities(PersistentSchool school) {
		EntityManager em = getEntityManager();
		try {
			TypedQuery<PersistentMethod> query = em.createNamedQuery("PersistentMethod.findBySchoolID", PersistentMethod.class);
			query.setParameter("schoolID", school.getSchoolID());
			return query.getResultList();
		} finally {
			em.close();
		}
	}
    
    
}
