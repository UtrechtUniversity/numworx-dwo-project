/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.dwo.server.persistence;

import fi.dwo.commons.persistence.entities.PersistentHasRole;
import fi.dwo.commons.persistence.entities.PersistentHasRolePK;
import fi.dwo.server.persistence.exceptions.NonexistentEntityException;
import fi.dwo.server.persistence.exceptions.PreexistingEntityException;
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
public class PersistentHasRoleJpaController implements Serializable {

    public PersistentHasRoleJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(PersistentHasRole persistentHasRole) throws PreexistingEntityException, Exception {
        if (persistentHasRole.getPersistentHasRolePK() == null) {
            persistentHasRole.setPersistentHasRolePK(new PersistentHasRolePK());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            em.persist(persistentHasRole);
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findPersistentHasRole(persistentHasRole.getPersistentHasRolePK()) != null) {
                throw new PreexistingEntityException("PersistentHasRole " + persistentHasRole + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(PersistentHasRole persistentHasRole) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            persistentHasRole = em.merge(persistentHasRole);
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                PersistentHasRolePK id = persistentHasRole.getPersistentHasRolePK();
                if (findPersistentHasRole(id) == null) {
                    throw new NonexistentEntityException("The persistentHasRole with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(PersistentHasRolePK id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            PersistentHasRole persistentHasRole;
            try {
                persistentHasRole = em.getReference(PersistentHasRole.class, id);
                persistentHasRole.getPersistentHasRolePK();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The persistentHasRole with id " + id + " no longer exists.", enfe);
            }
            em.remove(persistentHasRole);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<PersistentHasRole> findPersistentHasRoleEntities() {
        return findPersistentHasRoleEntities(true, -1, -1);
    }

    public List<PersistentHasRole> findPersistentHasRoleEntities(int maxResults, int firstResult) {
        return findPersistentHasRoleEntities(false, maxResults, firstResult);
    }

    private List<PersistentHasRole> findPersistentHasRoleEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(PersistentHasRole.class));
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

    public PersistentHasRole findPersistentHasRole(PersistentHasRolePK id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(PersistentHasRole.class, id);
        } finally {
            em.close();
        }
    }

    public int getPersistentHasRoleCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<PersistentHasRole> rt = cq.from(PersistentHasRole.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
