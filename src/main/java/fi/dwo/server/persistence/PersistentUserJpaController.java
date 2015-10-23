/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.dwo.server.persistence;

import fi.dwo.commons.persistence.entities.PersistentUser;
import fi.dwo.server.persistence.exceptions.NonexistentEntityException;
import java.io.Serializable;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

/**
 *
 * @author G.A.J. van der Plas
 */
public class PersistentUserJpaController implements Serializable {

    public PersistentUserJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(PersistentUser persistentUser) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            em.persist(persistentUser);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(PersistentUser persistentUser) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            persistentUser = em.merge(persistentUser);
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Long id = persistentUser.getUserID();
                if (findPersistentUser(id) == null) {
                    throw new NonexistentEntityException("The persistentUser with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Long id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            PersistentUser persistentUser;
            try {
                persistentUser = em.getReference(PersistentUser.class, id);
                persistentUser.getUserID();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The persistentUser with id " + id + " no longer exists.", enfe);
            }
            em.remove(persistentUser);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<PersistentUser> findPersistentUserEntities() {
        return findPersistentUserEntities(true, -1, -1);
    }

    public List<PersistentUser> findPersistentUserEntities(int maxResults, int firstResult) {
        return findPersistentUserEntities(false, maxResults, firstResult);
    }

    private List<PersistentUser> findPersistentUserEntities(boolean all, int maxResults, int firstResult) {
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

    public PersistentUser findPersistentUser(Long id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(PersistentUser.class, id);
        } finally {
            em.close();
        }
    }

    public int getPersistentUserCount() {
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
    
}
