package fi.dwo.server.PersistentDataManagers.core;

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

import fi.dwo.commons.persistence.entities.PersistentHasRolePK;
import fi.dwo.commons.persistence.entities.PersistentSchoolClass;
import fi.dwo.commons.persistence.entities.PersistentStudentModelContext;
import fi.dwo.commons.persistence.entities.PersistentStudentModelOfClass;
import fi.dwo.commons.persistence.entities.PersistentStudentModelOfClassPK;
import fi.dwo.commons.persistence.entities.PersistentStudentOfClass;
import fi.dwo.commons.persistence.entities.PersistentStudentOfClassPK;
import fi.dwo.server.persistence.DwoEmfFactory;

public class StudentModelOfClassManager {

	private static final Logger LOG = Logger.getLogger(StudentModelOfClassManager.class.getName());

    private static EntityManager getEntityManager() {
        EntityManager em = DwoEmfFactory.getEntityManager();
        return em;
    }

    /**
     * Create.
     *
     * @param studentOf
     */
    public static void create(PersistentStudentModelOfClass studentOf) throws PersistenceException {
        EntityManager em = null;
// assert userid not nul(l)     
        final Long classID = studentOf.getId().getClassID();
		if (classID == null || classID.longValue() == 0) {
			throw new PersistenceException("StudentModelOfClass.classid=" + classID);
		}
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            em.persist(studentOf);
            em.getTransaction().commit();
        }
        catch (PersistenceException e) {
          LOG.log(Level.WARNING, "Can't create the PersistentStudentModelOfClass.", e);
          throw (e);
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Can't create the PersistentStudentModelOfClass.", e);
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
     * @param studentOf
     * @throws Exception
     */
    public static void edit(PersistentStudentModelOfClass studentOf) throws PersistenceException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            studentOf = em.merge(studentOf);
            em.getTransaction().commit();
        } catch (Exception e) {
            String msg = e.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                PersistentStudentModelOfClassPK id = studentOf.getId();
                if (findEntity(id) == null) {
                    LOG.log(Level.FINE, "The PersistentStudentOfClass with " + id + " no longer exists.", e);
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
     * @param persistentStudentModelOfClassPK
     */
    public static void destroy(PersistentStudentModelOfClassPK persistentStudentModelOfClassPK) throws PersistenceException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
                PersistentStudentModelOfClass student = null;
            try {
                student = em.getReference(PersistentStudentModelOfClass.class, persistentStudentModelOfClassPK);
                student.getId();
            } catch (EntityNotFoundException e) {
                LOG.log(Level.FINE, "The PersistentStudentOfClass with " + persistentStudentModelOfClassPK + " no longer exists.", e);
                throw (e);
            }
            em.remove(student);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public static List<PersistentStudentModelOfClass> findEntities() {
        return findEntities(true, -1, -1);
    }

    public static List<PersistentStudentModelOfClass> findEntities(int maxResults, int firstResult) {
        return findEntities(false, maxResults, firstResult);
    }

    private static List<PersistentStudentModelOfClass> findEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery<PersistentStudentModelOfClass> cq = em.getCriteriaBuilder().createQuery(PersistentStudentModelOfClass.class);
            cq.select(cq.from(PersistentStudentModelOfClass.class));
            TypedQuery<PersistentStudentModelOfClass> q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public static List<PersistentStudentModelOfClass> findEntities(PersistentStudentModelContext key) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<PersistentStudentModelOfClass> q = em.createNamedQuery("PersistentStudentModelOfClass.findByModelID", PersistentStudentModelOfClass.class);
            q.setParameter("modelID", key.getModelID());
            q.setParameter("schoolID", key.getSchoolID());
            List<PersistentStudentModelOfClass> list = q.getResultList();
            LOG.log(Level.FINE, "PersistentStudentOfClass-manager retrieved {0} PersistentStudentOfClass with userid {1}", new Object[]{list.size(), key});
            return list;
        }
        finally {
            em.close();
        }
    }

    public static List<PersistentStudentModelOfClass> findEntities(PersistentSchoolClass sc) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<PersistentStudentModelOfClass> q = em.createNamedQuery("PersistentStudentModelOfClass.findByClassID", PersistentStudentModelOfClass.class);
            q.setParameter("classID", sc.getClassID());
            q.setParameter("schoolID", sc.getSchoolID());
            List<PersistentStudentModelOfClass> list = q.getResultList();
            LOG.log(Level.FINE, "PersistentStudentModelOfClass-manager retrieved {0} PersistentStudentModelOfClass with classid {1}", new Object[]{list.size(), sc.getClassID()});
            return list;
        }
        finally {
            em.close();
        }
    }    
    
    public static PersistentStudentModelOfClass findEntity(PersistentStudentModelOfClassPK id) throws PersistenceException{
        EntityManager em = getEntityManager();
        try {
            return em.find(PersistentStudentModelOfClass.class, id);
        } catch (PersistenceException e) {
            LOG.log(Level.FINE, "The PersistentStudentOfClass with " + id + " was not found.", e);
            throw e;
        } finally {
            em.close();
        }
    }

    public static int getEntityCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery<Long> cq = em.getCriteriaBuilder().createQuery(Long.class);
            Root<PersistentStudentModelOfClass> rt = cq.from(PersistentStudentModelOfClass.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            TypedQuery<Long> q = em.createQuery(cq);
            return q.getSingleResult().intValue();
        } finally {
            em.close();
        }
    }

}
