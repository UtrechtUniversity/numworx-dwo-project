package fi.dwo.server.PersistentDataManagers.core;

import fi.dwo.commons.persistence.entities.PersistentCourse;
import fi.dwo.commons.persistence.entities.PersistentDwoProfile;
import fi.dwo.commons.persistence.entities.PersistentSchool;
import fi.dwo.server.persistence.DwoEmfFactory;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.persistence.RollbackException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

/**
 * Manages courses in the persistent storage. 
 *
 * @author G.A.J. van der Plas
 */
public class CourseManager {

    private static final Logger LOG = Logger.getLogger(CourseManager.class.getName());

    private static EntityManager getEntityManager() {
        EntityManager em = DwoEmfFactory.getEntityManager();
        return em;
    }

    /**
     * Create.
     *
     * @param course
     */
    public static void create(PersistentCourse course) throws PersistenceException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            em.persist(course);
            em.getTransaction().commit();
        }
        catch(RollbackException e) {
          LOG.log(Level.WARNING, "Can't create the PersistentCourse.", e);
          throw e;
        }
        catch(PersistenceException e)
        {
          LOG.log(Level.SEVERE, "Can't create the PersistentCourse.", e);
          throw e;
        }
        catch (Exception e) {
            LOG.log(Level.SEVERE, "Can't create the PersistentCourse.", e);
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
     * @param course
     * @return jpa merged course
     */
    public static PersistentCourse edit(PersistentCourse course) throws PersistenceException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            course = em.merge(course);
            em.getTransaction().commit();
        } 
        catch (RollbackException e) {
          throw e;
        } catch (Exception e) {
            String msg = e.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Long id = course.getCourseID();
                if (findEntity(id) == null) {
                    LOG.log(Level.INFO, "The PersistentCourse with " + id + " no longer exists.", e);
                    throw new PersistenceException(e);
                }
            }
            throw new PersistenceException(e);
        } finally {
            if (em != null) {
                em.close();
            }
        }
        return course;
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
            PersistentCourse course = null;
            try {
                course = em.getReference(PersistentCourse.class, id);
                course.getCourseID();
            } catch (EntityNotFoundException e) {
                LOG.log(Level.FINE, "The PersistentCourse with " + id + " no longer exists.", e);
                throw new PersistenceException(e);
            }
            em.remove(course);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public static List<PersistentCourse> findEntities() {
        return findEntities(true, -1, -1);
    }

    public static List<PersistentCourse> findEntities(int maxResults, int firstResult) {
        return findEntities(false, maxResults, firstResult);
    }

    private static List<PersistentCourse> findEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(PersistentCourse.class));
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

    public static List<PersistentCourse> findChildrenOf(PersistentCourse c) {
        EntityManager em = getEntityManager();
        try {
            javax.persistence.Query q = em.createNamedQuery("PersistentCourse.findByParentID");
            q.setParameter("parentID", c.getCourseID());
            List<PersistentCourse> list = q.getResultList();
            LOG.log(Level.FINE, "Course-manager retrieved {0} PersistentCourse children of course with id {1}", new Object[]{list.size(), c.getCourseID()});
            return list;
        }
        finally {
            em.close();
        }
    }
    public static List<PersistentCourse> findTrashedChildrenOf(PersistentCourse c) {
        EntityManager em = getEntityManager();
        try {
            javax.persistence.Query q = em.createNamedQuery("PersistentCourse.findByParentIDTrash");
            q.setParameter("parentID", c.getCourseID());
            List<PersistentCourse> list = q.getResultList();
            LOG.log(Level.FINE, "Course-manager retrieved {0} PersistentCourse children of course with id {1}", new Object[]{list.size(), c.getCourseID()});
            return list;
        }
        finally {
            em.close();
        }
    }

    public static List<PersistentCourse> findChildrenOf(PersistentDwoProfile p,PersistentCourse c) {
        EntityManager em = getEntityManager();
        try {
            javax.persistence.Query q = em.createNamedQuery("PersistentCourse.findByParentIDAndProfileID");
            q.setParameter("parentID", c.getCourseID());
            q.setParameter("dwoProfileID", p.getDwoProfileID());
            List<PersistentCourse> list = q.getResultList();
            LOG.log(Level.FINE, "Course-manager retrieved {0} PersistentCourse children of course with id {1} in profile {2}.", new Object[]{list.size(), c.getCourseID(), p.getDwoProfileID()});
            return list;
        }
        finally {
            em.close();
        }
    }
    
    public static List<PersistentCourse> findChildrenOf(PersistentDwoProfile p, PersistentSchool school) {
        EntityManager em = getEntityManager();
        try {
        	Long schoolID = school == null ? null : school.getSchoolID();
        	
        	javax.persistence.Query q;
        	if (schoolID == null)
        		q = em.createNamedQuery("PersistentCourse.findByNullSchoolAndProfileID");
        	else {
        	 q= em.createNamedQuery("PersistentCourse.findBySchoolAndProfileID");
        	 q.setParameter("schoolID", schoolID);
        	}
        	q.setParameter("dwoProfileID", p.getDwoProfileID());
        	List<PersistentCourse> list = q.getResultList();
            LOG.log(Level.FINE, "Course-manager retrieved {0} PersistentCourse children of school with id {1} in profile {2}.", new Object[]{list.size(), schoolID, p.getDwoProfileID()});
            return list;
        }
        finally {
            em.close();
        }    	
    }
    
    public static List<PersistentCourse> findTrashedChildrenOf(PersistentDwoProfile p, PersistentSchool school) {
    	EntityManager em = getEntityManager();
    	try {
    		TypedQuery<PersistentCourse> q = em.createNamedQuery("PersistentCourse.findBySchoolAndProfileIDTrash", PersistentCourse.class);
    		q.setParameter("schoolID", school.getSchoolID());
    		q.setParameter("dwoProfileID", p.getDwoProfileID());
    		return q.getResultList();
    	} finally {
    		em.close();
    	}
    }
    
    
    public static List<PersistentCourse> findExportsOf(PersistentSchool school, PersistentDwoProfile p) {
      EntityManager em = getEntityManager();
      try {
          Long schoolID = school.getSchoolID();
          
          javax.persistence.TypedQuery<PersistentCourse> q;
          q = em.createNamedQuery("PersistentCourse.findByExportOfSchoolID", PersistentCourse.class);
          q.setParameter("dwoProfileID", p.getDwoProfileID());
          q.setParameter("schoolID", schoolID);
          List<PersistentCourse> list = q.getResultList();
          LOG.log(Level.FINE, "Course-manager retrieved {0} PersistentCourse children of school with id {1} in profile {2}.", new Object[]{list.size(), schoolID, p.getDwoProfileID()});
          return list;
      }
      finally {
          em.close();
      }
      
    }
        
    public static List<Long> findEntityIDs(PersistentSchool s) {
    	EntityManager em = getEntityManager();
    	try {
    		TypedQuery<Long> q = em.createQuery("select p.courseID from PersistentCourse p where p.schoolID = :schoolID", Long.class);
    		q.setParameter("schoolID",  s.getSchoolID());
    		List<Long> list = q.getResultList();
    		return list;
    	} finally {
    		em.close();
    	}
    }
        
    public static List<PersistentCourse> findEntities(PersistentSchool s, int limit) {
        EntityManager em = getEntityManager();
        try {
            javax.persistence.Query q = em.createNamedQuery("PersistentCourse.findBySchoolID");
            q.setParameter("schoolID", s.getSchoolID());
            if(limit > 0 ) q.setMaxResults(limit);
            List<PersistentCourse> list = q.getResultList();
            LOG.log(Level.FINE, "Course-manager retrieved {0} PersistentCourse with schoolid {1}", new Object[]{list.size(), s.getSchoolID()});
            return list;
        }
        finally {
            em.close();
        }
    }

    public static List<PersistentCourse> findEntities(Long profileID, Long schoolID) {
        EntityManager em = getEntityManager();
        try {
            javax.persistence.Query q = em.createNamedQuery("PersistentCourse.findByProfileAndSchoolID");
            q.setParameter("dwoProfileID", profileID);
            q.setParameter("schoolID", schoolID);
            List<PersistentCourse> list = q.getResultList();
            LOG.log(Level.FINE, "Course-manager retrieved {0} PersistentCourses with profileId {1} and schoolId {2}", new Object[]{list.size(), profileID, schoolID});
            return list;
        }
        finally {
            em.close();
        }
    }

    public static PersistentCourse findEntity(Long id) throws PersistenceException {
        EntityManager em = getEntityManager();
        try {
            return em.find(PersistentCourse.class, id);
         } catch (PersistenceException e) {
            LOG.log(Level.FINE, "The PersistentCourse with " + id + " was not found.", e);
            throw e;
       } finally {
            em.close();
        }
    }

    public static int getEntityCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<PersistentCourse> rt = cq.from(PersistentCourse.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }

    public static Long getEntityCount(PersistentSchool school) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery<Long> cq = em.getCriteriaBuilder().createQuery(Long.class);
            Root<PersistentCourse> rt = cq.from(PersistentCourse.class);
            Predicate p = em.getCriteriaBuilder().equal(rt.get("schoolID"), school.getSchoolID());           
            cq.select(em.getCriteriaBuilder().count(rt));
            cq.where(p);
            TypedQuery<Long> q = em.createQuery(cq);
            return q.getSingleResult();
        } finally {
            em.close();
        }
	}

    /**
     * returns null if no applet with that name was found.courseName@param appletName
     * @param courseName
     * @return 
     */
    public static PersistentCourse findByCourseName(String courseName) {
        EntityManager em = DwoEmfFactory.getEntityManager();
        PersistentCourse course = null;
        try {
            javax.persistence.Query q = em.createNamedQuery("PersistentCourse.findByCourseName");
            q.setParameter("name", courseName);
            course = (PersistentCourse) q.getSingleResult();
            LOG.log(Level.FINE, "PersistentCourse-manager retrieved user with course name {0}", new Object[]{course.getName()});
        }catch(NoResultException e){
            return null;
        }finally {
            em.close();
        }
        return course;
    }

    public static List<PersistentCourse> findVisibleEntities(Long profileID) {
      EntityManager em = getEntityManager();
      try {
          javax.persistence.TypedQuery<PersistentCourse> q = em.createNamedQuery("PersistentCourse.findAllByNullSchoolAndProfileID", PersistentCourse.class);
          q.setParameter("dwoProfileID", profileID);
          List<PersistentCourse> list = q.getResultList();
          LOG.log(Level.FINE, "Course-manager retrieved {0} PersistentCourses with profileId {1}", new Object[]{list.size(), profileID});
          Set<Long> visible = list.stream().map(PersistentCourse::getCourseID).collect(Collectors.toSet());
          visible.add(0L);
          Set<PersistentCourse> invisible;
          do {
            invisible = list.parallelStream()
                .filter(c -> ! visible.contains(c.getParentID()))
                .collect(Collectors.toSet());
            list.removeAll(invisible);
            invisible.forEach(c -> visible.remove(c.getCourseID()));
          } while(! invisible.isEmpty());
          
          return list;
      }
      finally {
          em.close();
      }
     
    }

}
