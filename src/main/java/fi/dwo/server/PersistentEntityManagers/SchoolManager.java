/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.dwo.server.PersistentEntityManagers;

import fi.dwo.commons.persistence.entities.PersistentSchool;
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

/**
 * Manages users in the persistent storage. Sample UserManager for building more
 * code. Also useful as it is being reused.
 *
 * @author G.A.J. van der Plas
 */
public class SchoolManager {

    private static final Logger LOG = Logger.getLogger(SchoolManager.class.getName());

    private static EntityManager getEntityManager() {
        EntityManager em = DwoEmfFactory.createEntityManager();
        return em;
    }

    /**
     * Create.
     *
     * @param school
     */
    public static void create(PersistentSchool school) throws PersistenceException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            em.persist(school);
            em.getTransaction().commit();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Can't create the school.", e);
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
    public static void edit(PersistentSchool school) throws PersistenceException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            school = em.merge(school);
            em.getTransaction().commit();
        } catch (Exception e) {
            String msg = e.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = school.getSchoolID();
                if (findPersistentSchool(id) == null) {
                    LOG.log(Level.FINE, "The persistentSchool with " + id + " no longer exists.", e);
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
    public static void destroy(Integer id) throws PersistenceException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
                PersistentSchool school = null;
            try {
                school = em.getReference(PersistentSchool.class, id);
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

    public static List<PersistentSchool> findPersistentSchoolEntities() {
        return findPersistentSchoolEntities(true, -1, -1);
    }

    public static List<PersistentSchool> findPersistentSchoolEntities(int maxResults, int firstResult) {
        return findPersistentSchoolEntities(false, maxResults, firstResult);
    }

    private static List<PersistentSchool> findPersistentSchoolEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(PersistentSchool.class));
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

    public static PersistentSchool findPersistentSchool(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(PersistentSchool.class, id);
        } finally {
            em.close();
        }
    }

    public static int getPersistentSchoolCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<PersistentSchool> rt = cq.from(PersistentSchool.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }

    public static PersistentSchool findBySchoolName(String schoolName) {
        EntityManager em = DwoEmfFactory.createEntityManager();
        PersistentSchool school = null;
        try {
            javax.persistence.Query q = em.createNamedQuery("PersistentSchool.findBySchoolName");
            q.setParameter("schoolname", schoolName);
            school = (PersistentSchool) q.getSingleResult();
            LOG.log(Level.FINE, "School-manager retrieved school with school {0}", new Object[]{school.getSchoolName()});
        } finally {
            em.close();
        }
        return school;
    }


}
