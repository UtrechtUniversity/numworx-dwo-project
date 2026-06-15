/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.dwo.server.PersistentDataManagers.core;

import fi.dwo.commons.persistence.entities.PersistentHasRolePK;
import fi.dwo.commons.persistence.entities.PersistentSchoolGroup;
import fi.dwo.commons.persistence.entities.PersistentScoContext;
import fi.dwo.commons.persistence.entities.PersistentStudentScoContext;
import fi.dwo.commons.persistence.entities.PersistentUser;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

/**
 * Manages a student's studentScoContext data in the persistent storage.
 *
 * @author G.A.J. van der Plas
 */
public class StudentScoContextManager extends AbstractManager {

    private static final Logger LOG = Logger.getLogger(StudentScoContextManager.class.getName());

    /**
     * Create.
     *
     * @param studentsco
     */
    public static void create(PersistentStudentScoContext studentsco) throws PersistenceException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            // uniqueness constraint: scoid + userid + sgid, not in database!      
            PersistentHasRolePK userkey = studentsco.getPersistentHasRolePK();
            Long scokey = studentsco.getScoID();
            TypedQuery<PersistentStudentScoContext> q = em.createNamedQuery("PersistentStudentScoContext.findByScoIDandHasRolePK", PersistentStudentScoContext.class);
            q.setParameter("scoID", scokey);
            q.setParameter("keyID", userkey);
            q.setLockMode(LockModeType.PESSIMISTIC_WRITE);
            List<PersistentStudentScoContext> list = q.getResultList();
            if (list.isEmpty()) {
            	studentsco.changeTimestamp();
            	em.persist(studentsco);
            	LOG.info("create studentsco " + studentsco.getStudentSco() + " for " + studentsco.getScoID() + " and " + studentsco.getPersistentHasRolePK().getUserID());
        	} else {
        		PersistentStudentScoContext i = list.get(0);
        		studentsco.setClassID(i.getClassID());
        		studentsco.setCompletionStatus(i.getCompletionStatus());
        		studentsco.setCreateDate(i.getCreateDate());
        		studentsco.setCreateTime(i.getCreateTime());
        		studentsco.setLastChangeTimeStamp(i.getLastChangeTimeStamp());
        		studentsco.setLocation(i.getLocation());
        		studentsco.setOptlock(i.getOptlock());
        		studentsco.setScore(i.getScore());
        		studentsco.setSessionTime(i.getSessionTime());
        		studentsco.setStudentSco(i.getStudentSco());
        		studentsco.setTotalTime(i.getTotalTime());
            	LOG.info("use studentsco " + studentsco.getStudentSco() + " for " + studentsco.getScoID() + " and " + studentsco.getPersistentHasRolePK().getUserID());
        	}
            em.getTransaction().commit();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Can't create the PersistentStudentScoContext.", e);
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
     * @throws PersistenceException
     */
    public static PersistentStudentScoContext edit(PersistentStudentScoContext studentOf) throws PersistenceException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            studentOf = em.merge(studentOf);
            studentOf.changeTimestamp();
            em.getTransaction().commit();
            return studentOf;
        } catch(PersistenceException e) { 
          LOG.log(Level.SEVERE, "Can't edit the PersistentStudentScoContext.", e);
          throw e;
        }
        catch (Exception e) {
            String msg = e.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Long id = studentOf.getScoID();
                if (findEntity(id) == null) {
                    LOG.log(Level.FINE, "The PersistentStudentScoContext with " + id + " no longer exists.", e);
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
            PersistentStudentScoContext ssc = null;
            try {
                ssc = em.getReference(PersistentStudentScoContext.class, id);
                ssc.getStudentSco();
            } catch (EntityNotFoundException e) {
                LOG.log(Level.FINE, "The PersistentStudentScoContext with " + id + " no longer exists.", e);
                throw e;
            }
            em.remove(ssc);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public static List<PersistentStudentScoContext> findEntities() {
        return findEntities(true, -1, -1);
    }

    public static List<PersistentStudentScoContext> findEntities(int maxResults, int firstResult) {
        return findEntities(false, maxResults, firstResult);
    }

    private static List<PersistentStudentScoContext> findEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(PersistentStudentScoContext.class));
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

    public static List<PersistentStudentScoContext> findEntities(PersistentScoContext scoContext) {
        EntityManager em = getEntityManager();
        try {
            javax.persistence.Query q = em.createNamedQuery("PersistentStudentScoContext.findByScoID");
            q.setParameter("scoID", scoContext.getScoID());
            List<PersistentStudentScoContext> list = q.getResultList();
            LOG.log(Level.FINE, "StudentScoContextManager-manager retrieved {0} PersistentStudentScoContext with scoId {1}", new Object[]{list.size(), scoContext.getScoID()});
            return list;
        } finally {
            em.close();
        }
    }

    public static List<PersistentStudentScoContext> findEntities(PersistentScoContext scoContext, long sgId) {
        EntityManager em = getEntityManager();
        try {
        	em.getTransaction().begin();
            javax.persistence.Query q = em.createNamedQuery("PersistentStudentScoContext.findByScoIDandSchoolGroupID");
            q.setParameter("scoID", scoContext.getScoID());
            q.setParameter("sgID", sgId);
            List<PersistentStudentScoContext> list = q.getResultList();
            for(PersistentStudentScoContext pssc: list) em.refresh(pssc);
            LOG.log(Level.FINE, "StudentScoContextManager-manager retrieved {0} PersistentStudentScoContext with scoId {1} and sgId {2}", new Object[]{list.size(), scoContext.getScoID(), sgId});
            em.getTransaction().commit();
            return list;
        } catch(RuntimeException e) {
        	LOG.log(Level.SEVERE, "", e);
        	throw e;
        } finally {
            em.close();
        }
    }
    
    public static List<PersistentStudentScoContext> findEntities(PersistentScoContext scoContext, PersistentHasRolePK key) {
        EntityManager em = getEntityManager();
        try {
        	em.getTransaction().begin();
            javax.persistence.Query q = em.createNamedQuery("PersistentStudentScoContext.findByScoIDandHasRolePK");
            q.setParameter("scoID", scoContext.getScoID());
            q.setParameter("keyID", key);
            List<PersistentStudentScoContext> list = q.getResultList();
            for(PersistentStudentScoContext pssc: list) em.refresh(pssc);
            LOG.log(Level.FINE, "StudentScoContextManager-manager retrieved {0} PersistentStudentScoContext with scoId {1} and key {2}", new Object[]{list.size(), scoContext.getScoID(), key.toString()});
            em.getTransaction().commit();
            return list;
        } catch(RuntimeException e) {
        	LOG.log(Level.SEVERE, "", e);
        	throw e;
        } finally {
            em.close();
        }
    }

   public static void destroyEntities(PersistentScoContext scoContext, PersistentHasRolePK key) {
        EntityManager em = getEntityManager();
        try {
            javax.persistence.Query q = em.createNamedQuery("PersistentStudentScoContext.deleteByScoIDandHasRolePK");
            q.setParameter("scoID", scoContext.getScoID());
            q.setParameter("keyID", key);
            List<PersistentStudentScoContext> list = q.getResultList();
            LOG.log(Level.FINE, "StudentScoContextManager-manager retrieved {0} PersistentStudentScoContext with scoId {1} and key {2}", new Object[]{list.size(), scoContext.getScoID(), key.toString()});
        } finally {
            em.close();
        }
    }
   
    public static List<PersistentStudentScoContext> findEntities(PersistentHasRolePK key) {
        EntityManager em = getEntityManager();
        try {
            javax.persistence.Query q = em.createNamedQuery("PersistentStudentScoContext.findByHasRolePK");
            q.setParameter("keyID", key);
            List<PersistentStudentScoContext> list = q.getResultList();
            LOG.log(Level.FINE, "StudentScoContextManager-manager retrieved {0} PersistentStudentScoContext with key {1}", new Object[]{list.size(), key.toString()});
            return list;
        } finally {
            em.close();
        }
    }
    
    public static List<PersistentStudentScoContext> findEntities(PersistentUser key) {
      EntityManager em = getEntityManager();
      try {
          javax.persistence.Query q = em.createNamedQuery("PersistentStudentScoContext.findByUserID");
          q.setParameter("userID", key.getId());
          List<PersistentStudentScoContext> list = q.getResultList();
          LOG.log(Level.FINE, "StudentScoContextManager-manager retrieved {0} PersistentStudentScoContext with key {1}", new Object[]{list.size(), key.toString()});
          return list;
      } finally {
          em.close();
      }
  }

    public static PersistentStudentScoContext findEntity(Long id)  throws PersistenceException{
        return find(id, PersistentStudentScoContext.class);
    }

    public static int getEntityCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery<Long> cq = em.getCriteriaBuilder().createQuery(Long.class);
            Root<PersistentStudentScoContext> rt = cq.from(PersistentStudentScoContext.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
    public static long getEntityCount(PersistentScoContext sco, PersistentHasRolePK role) {
      EntityManager em = getEntityManager();
      try {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<Long> cq = builder.createQuery(Long.class);
        Root<PersistentStudentScoContext> rt = cq.from(PersistentStudentScoContext.class);
        cq.select(em.getCriteriaBuilder().count(rt));
        Predicate exprSco = builder.equal(rt.get("scoID"), sco.getScoID());
        Path<Object> hasRolePath = rt.get("persistentHasRolePK");
        Predicate exprUser = builder.notEqual(hasRolePath.get("userID"), role.getUserID());
        Predicate exprSG   = builder.notEqual(hasRolePath.get("schoolGroupID"), role.getSchoolGroupID());
        Predicate or = builder.or(exprUser, exprSG);
        cq.where(exprSco,or);
        TypedQuery<Long> q = em.createQuery(cq);
        return q.getSingleResult().longValue();        
      } finally {
        em.close();
      }
    }
    
    public static Long getEntityCount(PersistentSchoolGroup role) {
        EntityManager em = getEntityManager();
        try {
          CriteriaBuilder builder = em.getCriteriaBuilder();
          CriteriaQuery<Long> cq = builder.createQuery(Long.class);
          Root<PersistentStudentScoContext> rt = cq.from(PersistentStudentScoContext.class);
          cq.select(builder.count(rt));
          Path<Object> hasRolePath = rt.get("persistentHasRolePK");
          Predicate exprSG   = builder.equal(hasRolePath.get("schoolGroupID"), role.getSchoolGroupID());
          cq.where(exprSG);
          TypedQuery<Long> q = em.createQuery(cq);
          return q.getSingleResult();        
        } finally {
          em.close();
        }
      }

}
