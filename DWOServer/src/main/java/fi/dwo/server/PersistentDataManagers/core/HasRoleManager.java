package fi.dwo.server.PersistentDataManagers.core;

import fi.dwo.commons.persistence.entities.PersistentHasRole;
import fi.dwo.commons.persistence.entities.PersistentHasRolePK;
import fi.dwo.commons.persistence.entities.PersistentSchoolGroup;
import fi.dwo.commons.persistence.entities.PersistentUser;
import fi.dwo.server.persistence.DwoEmfFactory;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

/**
 * Manages the hasRole's in the persistent storage.
 *
 * @author G.A.J. van der Plas
 */
public class HasRoleManager extends AbstractManager {

    private static final Logger LOG = Logger.getLogger(HasRoleManager.class.getName());


    /**
     * Update
     *
     * @param hasRole
     * @return 
     */
    public static PersistentHasRole edit(PersistentHasRole hasRole) throws PersistenceException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            hasRole.changeTimestamp();
            hasRole = em.merge(hasRole);
            em.getTransaction().commit();
            return hasRole;
        }
        catch (Exception e) {
            String msg = e.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                PersistentHasRolePK id = hasRole.getPersistentHasRolePK();
                if (findEntity(id) == null) {
                    LOG.log(Level.FINE, "The PersistentHasRole with " + id + " no longer exists.", e);
                    throw newPersistenceException(e);
                }
            }
            throw newPersistenceException(e);
        }
        finally {
            if (em != null) {
                em.close();
            }
        }
    }

	private static PersistenceException newPersistenceException(Exception e) {
		if (e instanceof PersistenceException) return (PersistenceException) e;
		return new PersistenceException(e);
	}

    public static void editRights(PersistentHasRole hasRole) throws PersistenceException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            PersistentHasRolePK id = hasRole.getPersistentHasRolePK();
            PersistentHasRole hr = findEntity(id);
            if (hr == null) {
                LOG.log(Level.FINE, "The PersistentHasRole with " + id + " no longer exists.");
                throw new PersistenceException("The PersistentHasRole with " + id + " no longer exists.");
            }
            hr.setRights(hasRole.getRights());
            hr = em.merge(hr);
            em.getTransaction().commit();
        }
        catch (Exception e) {
            String msg = e.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                PersistentHasRolePK id = hasRole.getPersistentHasRolePK();
                if (findEntity(id) == null) {
                    LOG.log(Level.FINE, "The PersistentHasRole with " + id + " no longer exists.", e);
                    throw newPersistenceException(e);
                }
            }
            throw newPersistenceException(e);
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
    public static void destroy(PersistentHasRolePK id) throws PersistenceException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            PersistentHasRole role = null;
            try {
                role = em.getReference(PersistentHasRole.class, id);
                role.getPersistentHasRolePK();
            }
            catch (EntityNotFoundException e) {
                LOG.log(Level.FINE, "The PersistentHasRole with " + id + " no longer exists.", e);
                throw new PersistenceException(e);
            }
            em.remove(role);
            em.getTransaction().commit();
        }
        finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public static List<PersistentHasRole> findEntities() {
        return findEntities(true, -1, -1);
    }

    public static List<PersistentHasRole> findEntities(int maxResults, int firstResult) {
        return findEntities(false, maxResults, firstResult);
    }

    private static List<PersistentHasRole> findEntities(boolean all, int maxResults, int firstResult) {
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
        }
        finally {
            em.close();
        }
    }

    public static List<PersistentHasRole> findEntities(PersistentUser user) {
        EntityManager em = getEntityManager();
        try {
            javax.persistence.Query q = em.createNamedQuery("PersistentHasRole.findByUserID");
            q.setParameter("userID", user.getId());
            List<PersistentHasRole> rl = q.getResultList();
            LOG.log(Level.FINE, "PersistentHasRole-manager retrieved {0} hasRoles with userid {1}", new Object[]{rl.size(), user.getId()});
            return rl;
        }
        catch (Exception e) {
            throw newPersistenceException(e);
        }
        finally {
            em.close();
        }
    }

    public static List<PersistentHasRole> findEntities(PersistentSchoolGroup sg) {
        EntityManager em = getEntityManager();
        try {
            javax.persistence.Query q = em.createNamedQuery("PersistentHasRole.findBySchoolGroupID");
            q.setParameter("schoolGroupID", sg.getSchoolGroupID());
            List<PersistentHasRole> rl = q.getResultList();
            LOG.log(Level.FINE, "PersistentHasRole-manager retrieved {0} hasRoles with userid {1}", new Object[]{rl.size(), sg});
            return rl;
        }
        catch (Exception e) {
            throw newPersistenceException(e);
        }
        finally {
            em.close();
        }
    }

    public static PersistentHasRole findEntity(PersistentHasRolePK id) throws PersistenceException{
    	return find(id, PersistentHasRole.class);
    }

    public static int getEntityCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<PersistentHasRole> rt = cq.from(PersistentHasRole.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        }
        finally {
            em.close();
        }
    }

	public static PersistentHasRole editLastLogin(PersistentHasRole role) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            PersistentHasRolePK id = role.getPersistentHasRolePK();
            PersistentHasRole hr = findEntity(id);
            if (hr == null) {
                throw new PersistenceException("The PersistentHasRole with " + id + " no longer exists.");
            }
            Date lastLogin = role.getLastLogin();
			if (!Objects.equals(hr.getLastLogin(), lastLogin))
            {
            	hr.setLastLogin(lastLogin);
            	hr = em.merge(hr);
            }
            em.getTransaction().commit();
            return hr;
        }
        catch (Exception e) {
            throw newPersistenceException(e);
        }
        finally {
            if (em != null) {
                em.close();
            }
        }
	}
}
