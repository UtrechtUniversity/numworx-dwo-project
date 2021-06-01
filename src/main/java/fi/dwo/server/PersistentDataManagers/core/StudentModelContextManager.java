package fi.dwo.server.PersistentDataManagers.core;

import fi.dwo.commons.persistence.entities.PersistentStudentModelContext;
import fi.dwo.commons.persistence.entities.PersistentSchool;
import fi.dwo.server.persistence.DwoEmfFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContextInfo;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelStructure;
import nl.uu.fi.dwo.rest.dom.entities.util.PublishState;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;
import nl.uu.fi.dwo.rest.util.DwoDateUtilities;

/**
 * Manages analytical models in the persistent storage. 
 *
 * @author G.A.J. van der Plas
 */
public class StudentModelContextManager {

    private static final Logger LOG = Logger.getLogger(StudentModelContextManager.class.getName());

    private static EntityManager getEntityManager() {
        EntityManager em = DwoEmfFactory.getEntityManager();
        return em;
    }

    /**
     * Create.
     *
     * @param model
     */
    public static PersistentStudentModelContext create(PersistentStudentModelContext model) throws PersistenceException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            model.setLastChangeTimeStamp(DwoDateUtilities.getCurrentDwoUnixTimeStamp());
            em.persist(model);
            em.getTransaction().commit();
            return model;
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Can't create the PersistentStudentModelContext.", e);
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
     * @param model
     * @return jpa merged course
     */
    public static PersistentStudentModelContext edit(PersistentStudentModelContext model) throws PersistenceException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            model.setLastChangeTimeStamp(DwoDateUtilities.getCurrentDwoUnixTimeStamp());            
            model = em.merge(model);
            em.getTransaction().commit();
        } catch (PersistenceException e) {
          String msg = e.getLocalizedMessage();
          if (msg == null || msg.length() == 0) {
              Long id = model.getModelID();
              if (findEntity(id) == null) {
                  LOG.log(Level.INFO, "The PersistentStudentModelContext with " + id + " no longer exists.", e);
                  throw e;
              }
          }
          throw e;
        } catch (Exception e) {
          String msg = e.getLocalizedMessage();
          if (msg == null || msg.length() == 0) {
              Long id = model.getModelID();
              if (findEntity(id) == null) {
                  LOG.log(Level.INFO, "The PersistentStudentModelContext with " + id + " no longer exists.", e);
                  throw new PersistenceException(e);
              }
          }
          throw new PersistenceException(e);
        } finally {
            if (em != null) {
                em.close();
            }
        }
        return model;
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
            PersistentStudentModelContext model = null;
            try {
                model = em.getReference(PersistentStudentModelContext.class, id);
                model.getModelID();
            } catch (EntityNotFoundException e) {
                LOG.log(Level.FINE, "The PersistentStudentModelContext with " + id + " no longer exists.", e);
                throw e;
            }
            em.remove(model);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public static List<PersistentStudentModelContext> findEntities() {
        return findEntities(true, -1, -1);
    }

    public static List<PersistentStudentModelContext> findEntities(int maxResults, int firstResult) {
        return findEntities(false, maxResults, firstResult);
    }

    private static List<PersistentStudentModelContext> findEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(PersistentStudentModelContext.class));
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
        
    public static List<PersistentStudentModelContext> findEntities(PersistentSchool s) {
        EntityManager em = getEntityManager();
        try {
            javax.persistence.Query q = em.createNamedQuery("PersistentStudentModelContext.findBySchoolID");
            q.setParameter("schoolID", s.getSchoolID());
            List<PersistentStudentModelContext> list = q.getResultList();
            LOG.log(Level.FINE, "Course-manager retrieved {0} PersistentStudentModelContext with schoolid {1}", new Object[]{list.size(), s.getSchoolID()});
            return list;
        }
        finally {
            em.close();
        }
    }
    
    @SuppressWarnings("unchecked")
	public static List<PersistentStudentModelContext> findReducedEntities(PersistentSchool s) {
        EntityManager em = getEntityManager();
        try {
        	Query q = em.createNativeQuery("SELECT modelID, schoolID, json_extract(model, \"$.info.title\"), optlock, lastChangeTimeStamp, publishState, json_extract(model, \"$.owner\"), json_extract(model, \"$.timestamp\"), json_extract(model, \"$.info.id\"), json_extract(model, \"$.activeMethod.idString\") FROM tblstudentmodelcontext WHERE schoolID = ?" );
        	q.setParameter(1, s.getSchoolID());
        	List<Object[]> result = q.getResultList();
        	List<PersistentStudentModelContext> list = new ArrayList<>();        	
        	for(Object[] item: result) {
        		try {
					PersistentStudentModelContext sc = new PersistentStudentModelContext();
					sc.setModelID( ((Number) item[0]).longValue());
					sc.setSchoolID( ((Number) item[1]).longValue());
					String title = (String) item[2];
					sc.setModelStructure(new DomStudentModelStructure());
					Map<String, String> aTitle = toJSONObject(title);
					sc.getModelStructure().setInfo(new DomStudentModelContextInfo(aTitle, null));
					sc.setOptlock(((Number) item[3]).longValue());
					sc.setLastChangeTimeStamp((Long) item[4]);
					sc.setPublishState(PublishState.values()[((Number)item[5]).intValue()]);
	// optional				
					sc.getModelStructure().setOwner( toString( item[6] ));
					sc.getModelStructure().setTimestamp(toLong(item[7])); 
				    sc.getModelStructure().getInfo().setId(toString(item[8]));
				    sc.getModelStructure().setActiveMethod(toPersistenceId(item[9]));
					list.add(sc);
				} catch (ParseException e) {
					LOG.log(Level.SEVERE, "findReducedEntities", e);
				}
        	}
        	return list;
        } finally {
        	em.close();
        }
    }

	private static Map<String, String> toJSONObject(String title) throws ParseException {
		if (title == null || "null".equals(title))
			return null;
		JSONParser parser = new JSONParser();
		Map<String, String> aTitle = (JSONObject) parser.parse(title);
		return aTitle;
	}
    

    private static Long toLong(Object object) {
		if (object == null || "null".equals(object))
			return null;
		return Long.valueOf(object.toString());
    }

	private static String toString(Object object) throws ParseException {
		if (object == null || "null".equals(object))
			return null;
		return new JSONParser().parse(object.toString()).toString();
	}
	private static PersistenceId toPersistenceId(Object object) throws ParseException {
		object = toString(object);
		if (object == null || "null".equals(object))
			return null;
		return new PersistenceId(object.toString());
	}
	

	public static PersistentStudentModelContext findEntity(Long id) throws PersistenceException {
        EntityManager em = getEntityManager();
        try {
            return em.find(PersistentStudentModelContext.class, id);
         } catch (PersistenceException e) {
            LOG.log(Level.FINE, "The PersistentStudentModelContext with " + id + " was not found.", e);
            throw e;
       } finally {
            em.close();
        }
    }

    public static int getEntityCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery<Long> cq = em.getCriteriaBuilder().createQuery(Long.class);
            Root<PersistentStudentModelContext> rt = cq.from(PersistentStudentModelContext.class);
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
            Root<PersistentStudentModelContext> rt = cq.from(PersistentStudentModelContext.class);
            Predicate p = em.getCriteriaBuilder().equal(rt.get("schoolID"), school.getSchoolID());           
            cq.select(em.getCriteriaBuilder().count(rt));
            cq.where(p);
            TypedQuery<Long> q = em.createQuery(cq);
            return q.getSingleResult();
        } finally {
            em.close();
        }
	}


}
