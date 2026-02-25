package fi.dwo.server.PersistentDataManagers.core;

import fi.dwo.commons.persistence.entities.PersistentACL;
import fi.dwo.commons.persistence.entities.PersistentCourse;
import fi.dwo.commons.persistence.entities.PersistentDwoProfile;
import fi.dwo.commons.persistence.entities.PersistentSchool;
import fi.dwo.server.persistence.DwoEmfFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

/**
 * Manages acls in the persistent storage. 
 *
 * @author Wim van Velthoven
 * @since 2.3.13
 */
public class ACLManager extends AbstractManager {

    private static final Logger LOG = Logger.getLogger(ACLManager.class.getName());

 
    /**
     * Update
     *
     * @param persistentApplet
     */
    public static PersistentACL edit(PersistentACL persistentAcl) throws PersistenceException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            persistentAcl.changeTimestamp();
            persistentAcl = em.merge(persistentAcl);
            em.getTransaction().commit();
            return persistentAcl;
        } catch (PersistenceException e) {
            String msg = e.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Long id = persistentAcl.getAclID();
                if (findEntity(id) == null) {
                    LOG.log(Level.FINE, "The PersistentAcl with " + id + " no longer exists.", e);
                }
            }
            throw e;
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
            PersistentACL persistentAcl = null;
            try {
                persistentAcl = em.getReference(PersistentACL.class, id);
                persistentAcl.getAclID();
            } catch (EntityNotFoundException e) {
                LOG.log(Level.FINE, "The PersistentApplet with " + id + " no longer exists.", e);
                throw (e);
            }
            em.remove(persistentAcl);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public static List<PersistentACL> findEntities() {
        return findEntities(true, -1, -1);
    }

    public static List<PersistentACL> findEntities(int maxResults, int firstResult) {
        return findEntities(false, maxResults, firstResult);
    }

    private static List<PersistentACL> findEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery<PersistentACL> cq = em.getCriteriaBuilder().createQuery(PersistentACL.class);
            cq.select(cq.from(PersistentACL.class));
            TypedQuery<PersistentACL> q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public static PersistentACL findEntity(Long id) throws PersistenceException{
    	return find(id, PersistentACL.class);
    }

    public static int getEntityCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery<Long> cq = em.getCriteriaBuilder().createQuery(Long.class);
            Root<PersistentACL> rt = cq.from(PersistentACL.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            TypedQuery<Long> q = em.createQuery(cq);
            return (q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }

    public static List<PersistentACL> findBySchool(PersistentSchool s, PersistentDwoProfile p) {
      EntityManager em = getEntityManager();
      try {
        TypedQuery<PersistentACL> q = em.createNamedQuery("PersistentACL.findBySchoolIDProfileID", PersistentACL.class);
        q.setParameter("schoolID", s.getSchoolID());
        q.setParameter("profileID", p.getDwoProfileID());
        List<PersistentACL> list = q.getResultList();
        LOG.log(Level.FINE, "ACL-manager retrieved {0} PersistentACL with schoolid {1}", new Object[]{list.size(), s.getSchoolID()});
        return list;
      }
      finally {
          em.close();
      }     
    }
  
    public static List<PersistentACL> findByCourse(PersistentCourse s) {
      EntityManager em = getEntityManager();
      try {
          TypedQuery<PersistentACL> q = em.createNamedQuery("PersistentACL.findByCourseID", PersistentACL.class);
          q.setParameter("courseID", s.getCourseID());
          List<PersistentACL> list = q.getResultList();
          LOG.log(Level.FINE, "ACL-manager retrieved {0} PersistentACL with courseid {1}", new Object[]{list.size(), s.getCourseID()});
          return list;
      }
      finally {
          em.close();
      }
  }
    
    public static List<PersistentACL> updateByCourse(PersistentCourse s, List<PersistentACL> acls) 
    throws PersistenceException {
      EntityManager em = getEntityManager();
      try {
        em.getTransaction().begin();
        TypedQuery<PersistentACL> q = em.createNamedQuery("PersistentACL.findByCourseID", PersistentACL.class);
        q.setParameter("courseID", s.getCourseID());
        List<PersistentACL> list = q.getResultList();
        for (PersistentACL item: list) {
          boolean present = acls.stream().anyMatch(a -> item.getAclID().equals(a.getAclID()) );
          if (!present) {
            Optional<PersistentACL> find = acls.stream().filter(a -> item.getEntity().equals(a.getEntity())).findAny();
            if (find.isPresent())
            {
              find.get().setAclID(item.getAclID());
              find.get().setOptlock(item.getOptlock());
            }
            else
                em.remove(item);
          }
        }
        list = new ArrayList<>(); 
        for(PersistentACL item : acls) {
           if (item.getAclID() == null) {
             item.setCourseID(s.getCourseID());
             item.setDwoProfileID(s.getDwoProfileID());
             item.setSchoolID(s.getSchoolID());
             item.changeTimestamp();
             em.persist(item);
           } else {
        	 item.changeTimestamp();
             item = em.merge(item);
           }
           list.add(item);
         }
        em.getTransaction().commit();
        acls = list;       
      } finally {
        em.close();
      }
      return acls;
    }
    
    
}
