/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.dwo.server.PersistentDataManagers.core;

import fi.dwo.commons.persistence.entities.PersistentSchoolData;
import fi.dwo.server.persistence.DwoEmfFactory;
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

/**
 * Manages schools in the persistent storage.
 *
 * @author G.A.J. van der Plas
 */
public class SchoolDataManager {

    private static final Logger LOG = Logger.getLogger(SchoolDataManager.class.getName());

    private static EntityManager getEntityManager() {
        return DwoEmfFactory.getEntityManager();
    }

    /**
     * Create.
     *
     * @param school
     */
    public static void create(PersistentSchoolData school) throws PersistenceException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            em.persist(school);
            em.getTransaction().commit();
        } catch (PersistenceException e) {
            LOG.log(Level.SEVERE, "Can't create the PersistentSchool.", e);
        	throw e;
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Can't create the PersistentSchool.", e);
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
     * @param school
     * @throws Exception
     */
    public static PersistentSchoolData edit(PersistentSchoolData school) throws PersistenceException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            school = em.merge(school);
            em.getTransaction().commit();
            return school;
        } catch (Exception e) {
            String msg = e.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Long id = school.getSchoolID();
                if (findEntity(id) == null) {
                    LOG.log(Level.FINE, "The PersistentSchoolData with " + id + " no longer exists.", e);
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
            PersistentSchoolData school = null;
            try {
                school = em.getReference(PersistentSchoolData.class, id);
                school.getSchoolID();
            } catch (EntityNotFoundException e) {
                LOG.log(Level.FINE, "The persistentSchool with " + id + " no longer exists.", e);
                throw new PersistenceException(e);
            }
            em.remove(school);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public static List<PersistentSchoolData> findEntities() {
        return findEntities(true, -1, -1);
    }

    public static List<PersistentSchoolData> findEntities(int maxResults, int firstResult) {
        return findEntities(false, maxResults, firstResult);
    }

    private static List<PersistentSchoolData> findEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery<PersistentSchoolData> cq = em.getCriteriaBuilder().createQuery(PersistentSchoolData.class);
            cq.select(cq.from(PersistentSchoolData.class));
            TypedQuery<PersistentSchoolData> q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public static PersistentSchoolData findEntity(Long id) throws PersistenceException{
        EntityManager em = getEntityManager();
        try {
            return em.find(PersistentSchoolData.class, id);
        } catch (PersistenceException e) {
            LOG.log(Level.FINE, "The PersistentSchoolData with " + id + " was not found.", e);
            throw e;
        } finally {
            em.close();
        }
    }

    public static int getEntityCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<PersistentSchoolData> rt = cq.from(PersistentSchoolData.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }

    public static List<? extends Number> findByBRIN(String brin) {
		EntityManager em = getEntityManager();
		try {
	    	Query q = em.createNativeQuery("SELECT s.schoolID from tblschooldata s where JSON_EXTRACT(s.schoolData,\"$.BRIN\") = ?");
	    	q.setParameter(1, brin);
	    	@SuppressWarnings("unchecked")
			List<? extends Number> result = q.getResultList();
			return result;
		} finally {
			em.close();
		}
	}


}
