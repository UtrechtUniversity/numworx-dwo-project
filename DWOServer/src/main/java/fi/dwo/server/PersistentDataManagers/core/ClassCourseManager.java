package fi.dwo.server.PersistentDataManagers.core;

import fi.dwo.commons.persistence.entities.PersistentClassCourse;
import fi.dwo.commons.persistence.entities.PersistentCourse;
import fi.dwo.commons.persistence.entities.PersistentSchoolClass;
import fi.dwo.server.persistence.DwoEmfFactory;
import java.util.Date;

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

import nl.uu.fi.dwo.rest.dom.entities.util.CourseType;
import nl.uu.fi.dwo.rest.dom.entities.util.ViewState;

/**
 * Manages class courses in the persistent storage.
 *
 * @author G.A.J. van der Plas
 */
public class ClassCourseManager {

    private static final Logger LOG = Logger.getLogger(ClassCourseManager.class.getName());

    private static EntityManager getEntityManager() {
        EntityManager em = DwoEmfFactory.getEntityManager();
        return em;
    }

    /**
     * Create.
     *
     * @param classCourse
     */
    public static PersistentClassCourse create(PersistentClassCourse classCourse) throws PersistenceException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            em.persist(classCourse);
//            em.flush();
            em.getTransaction().commit();
            return classCourse;
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Can't create the PersistentClassCourse.", e);
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
     * @param classCourse
     * @return
     */
    public static PersistentClassCourse edit(PersistentClassCourse classCourse) throws PersistenceException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            classCourse = em.merge(classCourse);
            em.getTransaction().commit();
            return classCourse;
        } catch (Exception e) {
            String msg = e.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Long id = classCourse.getClassCourseID();
                if (findEntity(id) == null) {
                    LOG.log(Level.FINE, "The PersistentClassCourse with " + id + " no longer exists.", e);
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
     * Updates the ViewState. It requires a fully set PersistentClassCourse with
     * exception of the index. In case it is not found it is inserted into the database.
     *
     * @param cc
     */
    public static void insertOrUpdateViewState(PersistentClassCourse cc) throws PersistenceException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            javax.persistence.TypedQuery<PersistentClassCourse> q = em.createNamedQuery("PersistentClassCourse.findByClassIDAndCourseID", PersistentClassCourse.class);
            q.setParameter("courseID", cc.getCourseID());
            q.setParameter("classID", cc.getClassID());
            List<PersistentClassCourse> list = q.getResultList();
            
            if(list.isEmpty()){
                //insert
                em.persist(cc);
            } else {
                //merge
            	list.get(0).setViewState(cc.getViewState()); // ???? missing, irrelevant?
                cc = em.merge(list.get(0));                
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            String msg = e.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                if (cc == null) {
                    LOG.log(Level.FINE, "Insert or updating PersistentClassCourse with " + cc.getClassCourseID() + " faulted.", e);
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
     * Updates the CourseType.
     *
     * @param classCourse
     */
    public static void editViewState(Long id, ViewState state) throws PersistenceException {
        EntityManager em = null;
        PersistentClassCourse cc = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            cc = em.find(PersistentClassCourse.class, id);
            cc.setViewState(state);
            cc = em.merge(cc);
            em.getTransaction().commit();
        } catch (Exception e) {
            String msg = e.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                if (cc == null) {
                    LOG.log(Level.FINE, "The PersistentClassCourse with " + id + " no longer exists.", e);
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
     * Updates the CourseType.
     *
     * @param classCourse
     */
    public static void editType(Long id, CourseType type) throws PersistenceException {
        EntityManager em = null;
        PersistentClassCourse cc = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            cc = findEntity(id);
            cc.setType(type.ordinal());
            cc = em.merge(cc);
            em.getTransaction().commit();
        } catch (Exception e) {
            String msg = e.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                if (cc == null) {
                    LOG.log(Level.FINE, "The PersistentClassCourse with " + id + " no longer exists.", e);
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
     * Updates the CourseType.
     *
     * @param classCourse
     */
    public static void editFrom(Long id, Date date) throws PersistenceException {
        EntityManager em = null;
        PersistentClassCourse cc = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            cc = findEntity(id);
            cc.setNotBefore(date);
            cc = em.merge(cc);
            em.getTransaction().commit();
        } catch (Exception e) {
            String msg = e.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                if (cc == null) {
                    LOG.log(Level.FINE, "The PersistentClassCourse with " + id + " no longer exists.", e);
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
     * Updates the CourseType.
     *
     * @param classCourse
     */
    public static void editTo(Long id, Date date) throws PersistenceException {
        EntityManager em = null;
        PersistentClassCourse cc = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            cc = findEntity(id);
            cc.setNotAfter(date);
            cc = em.merge(cc);
            em.getTransaction().commit();
        } catch (Exception e) {
            String msg = e.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                if (cc == null) {
                    LOG.log(Level.FINE, "The PersistentClassCourse with " + id + " no longer exists.", e);
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
            PersistentClassCourse classCourse = null;
            try {
                classCourse = em.getReference(PersistentClassCourse.class, id);
                classCourse.getClassCourseID();
            } catch (EntityNotFoundException e) {
                LOG.log(Level.FINE, "The PersistentClassCourse with " + id + " no longer exists.", e);
                throw e;
            }
            em.remove(classCourse);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public static List<PersistentClassCourse> findEntities() {
        return findEntities(true, -1, -1);
    }

    public static List<PersistentClassCourse> findEntities(int maxResults, int firstResult) {
        return findEntities(false, maxResults, firstResult);
    }

    private static List<PersistentClassCourse> findEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(PersistentClassCourse.class));
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

    public static List<PersistentClassCourse> findEntities(PersistentSchoolClass c) {
        EntityManager em = getEntityManager();
        try {
            javax.persistence.Query q = em.createNamedQuery("PersistentClassCourse.findByClassID");
            q.setParameter("classID", c.getClassID());
            List<PersistentClassCourse> list = q.getResultList();
            LOG.log(Level.FINE, "ClassCourse-manager retrieved {0} PersistentClassCourse with classid {1}", new Object[]{list.size(), c.getClassID()});
            return list;
        } finally {
            em.close();
        }
    }

    public static List<PersistentClassCourse> findVisibleEntities(PersistentSchoolClass c, ViewState viewState, Long profileID) {
        EntityManager em = getEntityManager();
        try {
            javax.persistence.Query q = em.createNamedQuery("PersistentClassCourse.findVisibleByProfileInClass");
            q.setParameter("classID", c.getClassID());
            q.setParameter("viewState", viewState);
            q.setParameter("dwoProfileID", profileID);
            List<PersistentClassCourse> list = q.getResultList();
            LOG.log(Level.FINE, "ClassCourse-manager retrieved {0} PersistentClassCourse with classid {1}", new Object[]{list.size(), c.getClassID()});
            return list;
        } finally {
            em.close();
        }
    }

    public static List<PersistentClassCourse> findEntities(PersistentCourse c) {
        EntityManager em = getEntityManager();
        try {
            javax.persistence.TypedQuery<PersistentClassCourse> q = em.createNamedQuery("PersistentClassCourse.findByCourseID", PersistentClassCourse.class);
            q.setParameter("courseID", c.getCourseID());
            List<PersistentClassCourse> list = q.getResultList();
            LOG.log(Level.FINE, "ClassCourse-manager retrieved {0} PersistentClassCourse with courseid {1}", new Object[]{list.size(), c.getCourseID()});
            return list;
        } finally {
            em.close();
        }
    }

    public static List<PersistentClassCourse> findEntities(PersistentSchoolClass schoolClass, PersistentCourse course) {
        EntityManager em = getEntityManager();
        try {
            javax.persistence.TypedQuery<PersistentClassCourse> q = em.createNamedQuery("PersistentClassCourse.findByClassIDAndCourseID", PersistentClassCourse.class);
            q.setParameter("courseID", course.getCourseID());
            q.setParameter("classID", schoolClass.getClassID());
            List<PersistentClassCourse> list = q.getResultList();
            LOG.log(Level.FINE, "ClassCourse-manager retrieved {0} PersistentClassCourse with courseid {1} and classId {2}", new Object[]{list.size(), course.getCourseID(), schoolClass.getClassID()});
            return list;
        } finally {
            em.close();
        }
    }

    public static PersistentClassCourse findEntity(Long id) throws PersistenceException {
        EntityManager em = getEntityManager();
        try {
            return em.find(PersistentClassCourse.class, id);
        } catch (PersistenceException e) {
            LOG.log(Level.FINE, "The PersistentClassCourse with " + id + " was not found.", e);
            throw e;
        } finally {
            em.close();
        }
    }

    public static int getEntityCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery<Long> cq = em.getCriteriaBuilder().createQuery(Long.class);
            Root<PersistentClassCourse> rt = cq.from(PersistentClassCourse.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            TypedQuery<Long> q = em.createQuery(cq);
            return (q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }

    public static void editAccessKey(Long id, String accessKey) {
        EntityManager em = null;
        PersistentClassCourse cc = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            cc = findEntity(id);
            cc.setAccessKey(accessKey);
            cc = em.merge(cc);
            em.getTransaction().commit();
        } catch (Exception e) {
            String msg = e.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                if (cc == null) {
                    LOG.log(Level.FINE, "The PersistentClassCourse with " + id + " no longer exists.", e);
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

	public static List<PersistentClassCourse> findEntities(PersistentSchoolClass schoolClass, Long profileID) {
		return findEntities(schoolClass); // FIXME nu is er toch nog een test op een bestaande course.
	}

	public static PersistentClassCourse editResults(long id, Boolean results) {
        EntityManager em = null;
        PersistentClassCourse cc = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            cc = em.find(PersistentClassCourse.class, id);
            cc.setResults(results);
            cc = em.merge(cc);
            em.getTransaction().commit();
            return cc;
        } finally {
            if (em != null) {
                em.close();
            }
        }
	}
	
	public static void uncacheResults(PersistentCourse course) {
		EntityManager em = getEntityManager();
		try {
			em.getTransaction().begin();
			Query q = em.createQuery("UPDATE PersistentClassCourse SET results = NULL WHERE courseID = :c AND results IS NOT NULL");
			q.setParameter("c", course.getCourseID());
			q.executeUpdate();
			em.getTransaction().commit();
		} finally {
			em.close();
		}
	}

	public static void uncacheResults(PersistentSchoolClass sc) {
		EntityManager em = getEntityManager();
		try {
			em.getTransaction().begin();
			Query q = em.createQuery("UPDATE PersistentClassCourse SET results = NULL WHERE classID = :c AND results IS NOT NULL");
			q.setParameter("c", sc.getClassID());
			q.executeUpdate();
			em.getTransaction().commit();
		} finally {
			em.close();
		}
	}
	
}
