package fi.dwo.server.PersistentDataManagers.core;

import fi.dwo.commons.persistence.entities.PersistentStudentModelContext;
import fi.dwo.commons.persistence.entities.PersistentDwoProfile;
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
public class StudentModelContextManager extends AbstractManager {

    private static final Logger LOG = Logger.getLogger(StudentModelContextManager.class.getName());

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
            model.changeTimestamp();        
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
	public static List<PersistentStudentModelContext> findReducedEntities(PersistentSchool s, PersistentDwoProfile profile) {
        EntityManager em = getEntityManager();
        try {
        	Query q = em.createNativeQuery("SELECT m.modelID, m.schoolID, json_extract(m.model, \"$.info.title\"), m.optlock, m.lastChangeTimeStamp, m.publishState, json_extract(m.model, \"$.owner\"), json_extract(m.model, \"$.timestamp\"), json_extract(m.model, \"$.info.id\"), json_extract(m.model, \"$.activeMethod.idString\") FROM tblstudentmodelcontext m left join tblstudentmodelperprofile p using (modelID) WHERE (m.schoolID = ?) or (m.schoolID = 0 AND (m.dwoProfileID = ? or p.dwoProfileID = ?))" );
        	q.setParameter(1, s.getSchoolID());
        	q.setParameter(2, profile.getDwoProfileID());
        	q.setParameter(3, profile.getDwoProfileID());
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
        return find(id, PersistentStudentModelContext.class);
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

	public static void addProfile(PersistentStudentModelContext m, PersistentDwoProfile profile) {
		EntityManager em = getEntityManager();
		try {
			em.getTransaction().begin();
			m = em.merge(m);
			profile = em.find(PersistentDwoProfile.class, profile.getDwoProfileID());
// HIER protectie tegen dubbelen
			if (profile.getDwoProfileID().equals(m.getDwoProfileID()))
				return;
			m.getProfiles().add(profile);
			profile.getStudentModels().add(m);
			profile.changeTimestamp();
			m.changeTimestamp();
			em.getTransaction().commit();
		} finally {
			em.close();
		}		
	}

	public static void removeProfile(PersistentStudentModelContext m, PersistentDwoProfile profile) {
		EntityManager em = getEntityManager();
		try {
			em.getTransaction().begin();
			m = em.merge(m);
			profile = em.merge(profile);
			if (m.getProfiles().remove(profile) )
				m.changeTimestamp();
			if (profile.getStudentModels().remove(m))
				profile.changeTimestamp();
			em.getTransaction().commit();
		} finally {
			em.close();
		}		
	}

}
