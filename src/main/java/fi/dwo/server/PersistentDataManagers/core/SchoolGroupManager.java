package fi.dwo.server.PersistentDataManagers.core;

import fi.dwo.rest.dom.entities.RoleType;
import fi.dwo.commons.persistence.entities.PersistentSchool;
import fi.dwo.commons.persistence.entities.PersistentSchoolGroup;
import fi.dwo.server.persistence.DwoEmfFactory;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

/**
 * Manages school groups in the persistent storage.
 *
 * @author G.A.J. van der Plas
 */
public class SchoolGroupManager {

    private static final Logger LOG = Logger.getLogger(SchoolGroupManager.class.getName());

    private static EntityManager getEntityManager() {
        EntityManager em = DwoEmfFactory.getEntityManager();
        return em;
    }

    /**
     * Create.
     *
     * @param entity
     */
    public static void create(PersistentSchoolGroup entity) throws PersistenceException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            em.persist(entity);
            em.getTransaction().commit();
        }
        catch (Exception e) {
            LOG.log(Level.SEVERE, "Can't create the PersistentSchoolGroup.", e);
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
     * @param entity
     * @throws Exception
     */
    public static void edit(PersistentSchoolGroup entity) throws PersistenceException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            entity = em.merge(entity);
            em.getTransaction().commit();
        }
        catch (Exception e) {
            String msg = e.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Long id = entity.getSchoolGroupID();
                if (findEntity(id) == null) {
                    LOG.log(Level.FINE, "The PersistentSchoolGroup with " + id + " no longer exists.", e);
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
    public static void destroy(Long id) throws PersistenceException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            PersistentSchoolGroup entity = null;
            try {
                entity = em.getReference(PersistentSchoolGroup.class, id);
                entity.getSchoolGroupID();
            }
            catch (EntityNotFoundException e) {
                LOG.log(Level.FINE, "The PersistentSchoolGroup with " + id + " no longer exists.", e);
                throw new PersistenceException(e);
            }
            em.remove(entity);
            em.getTransaction().commit();
        }
        finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public static List<PersistentSchoolGroup> findEntities() {
        return findEntities(true, -1, -1);
    }

    public static List<PersistentSchoolGroup> findEntities(int maxResults, int firstResult) {
        return findEntities(false, maxResults, firstResult);
    }

    private static List<PersistentSchoolGroup> findEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(PersistentSchoolGroup.class));
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

    public static List<PersistentSchoolGroup> findEntities(PersistentSchool school) {
        EntityManager em = getEntityManager();
        try {
            javax.persistence.Query q = em.createNamedQuery("PersistentSchoolGroup.findBySchoolID");
            q.setParameter("schoolID", school.getSchoolID());
            List<PersistentSchoolGroup> list = q.getResultList();
            LOG.log(Level.FINE, "SchoolGroup-manager retrieved {0} PersistentSchoolGroup with schoolid {1}", new Object[]{list.size(), school.getSchoolID()});
            return list;
        }
        finally {
            em.close();
        }
    }

    public static List<PersistentSchoolGroup> findEntity(PersistentSchool school) {
        EntityManager em = getEntityManager();
        try {
            javax.persistence.Query q = em.createNamedQuery("PersistentSchoolGroup.findBySchoolID");
            q.setParameter("schoolID", school.getSchoolID());
            List<PersistentSchoolGroup> list = q.getResultList();
            LOG.log(Level.FINE, "SchoolGroup-manager retrieved {0} PersistentSchoolGroup with schoolid {1}", new Object[]{list.size(), school.getSchoolID()});
            return list;
        }
        finally {
            em.close();
        }
    }

     public static PersistentSchoolGroup findEntity(PersistentSchool school, RoleType roleType) {
        EntityManager em = getEntityManager();
        try {
            javax.persistence.Query q = em.createNamedQuery("PersistentSchoolGroup.findBySchoolIDAndRole");
            q.setParameter("schoolID", school.getSchoolID());
            q.setParameter("rolename", roleType.name());
            PersistentSchoolGroup result = (PersistentSchoolGroup) q.getSingleResult();
            LOG.log(Level.FINE, "SchoolGroup-manager retrieved PersistentSchoolGroup of schoolid {0} with schoolgroupid {1} for RoleType {2}", new Object[]{school.getSchoolID(), result.getSchoolGroupID(), roleType.name()});
            return result;
        } catch (NoResultException noResult) {
        	return null; // FIXME or rethrow exception
        }
        finally {
            em.close();
        }
    }
   
    
    public static PersistentSchoolGroup findEntity(Long id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(PersistentSchoolGroup.class, id);
        }
        finally {
            em.close();
        }
    }

    public static int getEntityCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<PersistentSchoolGroup> rt = cq.from(PersistentSchoolGroup.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        }
        finally {
            em.close();
        }
    }

    public static PersistentSchoolGroup findBySchoolAndRole(PersistentSchool school, RoleType role) {
        EntityManager em = DwoEmfFactory.getEntityManager();
        try {
            javax.persistence.Query q = em.createNamedQuery("PersistentSchoolGroup.findBySchoolIDAndRole");
            q.setParameter("schoolID", school.getSchoolID());
            q.setParameter("rolename", role.name());
            PersistentSchoolGroup sg = (PersistentSchoolGroup) q.getSingleResult();
            LOG.log(Level.FINE, "PersistentSchool-manager retrieved schoolgroup {0} for null-school students.", new Object[]{sg.getSchoolGroupID()});
            return sg;
        }
        catch (NoResultException e) {
            return null;
        }
        finally {
            em.close();
        }
    }

}
