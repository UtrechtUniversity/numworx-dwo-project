package fi.dwo.server.PersistentDataManagers.util;

import fi.dwo.commons.persistence.entities.PersistentClassCourse;
import fi.dwo.commons.persistence.entities.PersistentCourse;
import fi.dwo.commons.persistence.entities.PersistentCourseInClass;
import fi.dwo.commons.persistence.entities.PersistentDwoProfile;
import fi.dwo.commons.persistence.entities.PersistentHasRolePK;
import fi.dwo.commons.persistence.entities.PersistentSchoolClass;
import fi.dwo.server.PersistentDataManagers.core.ClassCourseManager;
import fi.dwo.server.persistence.DwoEmfFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import nl.uu.fi.dwo.rest.dom.entities.util.ViewState;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.exceptions.Dwo2ExceptionCode;

/**
 * Manages class courses in the persistent storage.
 *
 * @author G.A.J. van der Plas
 */
public class CourseInClassManager {

    private static final Logger LOG = Logger.getLogger(CourseInClassManager.class.getName());

    private static EntityManager getEntityManager() {
        EntityManager em = DwoEmfFactory.getEntityManager();
        return em;
    }
//
//    /**
//     * Create.
//     *
//     * @param classCourse
//     */
//    public static void create(PersistentCourseInClass courseInClass) throws PersistenceException {
//        EntityManager em = null;
//        try {
//            em = getEntityManager();
//            em.getTransaction().begin();
//            em.persist(courseInClass);
////            em.flush();
//            em.getTransaction().commit();
//        } catch (Exception e) {
//            LOG.log(Level.SEVERE, "Can't create the PersistentClassCourse.", e);
//            throw new PersistenceException(e);
//        } finally {
//            if (em != null) {
//                em.close();
//            }
//        }
//    }
//
//    /**
//     * Update
//     *
//     * @param classCourse
//     * @return 
//     */
//    public static void edit(PersistentCourseInClass courseInClass) throws PersistenceException {
//        EntityManager em = null;
//        try {
//            em = getEntityManager();
//            em.getTransaction().begin();
//             = em.merge(courseInClass.getClassCourse());
//            em.getTransaction().commit();
//        } catch (Exception e) {
//            String msg = e.getLocalizedMessage();
//            if (msg == null || msg.length() == 0) {
//                Long id = courseInClass..getClassCourse().getClassCourseID();
//                if (findEntity(id) == null) {
//                    LOG.log(Level.FINE, "The PersistentClassCourse with " + id + " no longer exists.", e);
//                    throw new PersistenceException(e);
//                }
//            }
//            throw new PersistenceException(e);
//        } finally {
//            if (em != null) {
//                em.close();
//            }
//        }
//    }
//
//    /**
//     * Updates the CourseType.
//     *
//     * @param classCourse
//     */
//    public static void editViewState(Long id, ViewState state) throws PersistenceException {
//        EntityManager em = null;
//        PersistentClassCourse cc = null;
//        try {
//            em = getEntityManager();
//            em.getTransaction().begin();
//            cc = findEntity(id);
//            cc.setViewState(state);
//            cc = em.merge(cc);
//            em.getTransaction().commit();
//        } catch (Exception e) {
//            String msg = e.getLocalizedMessage();
//            if (msg == null || msg.length() == 0) {
//                if (cc == null) {
//                    LOG.log(Level.FINE, "The PersistentClassCourse with " + id + " no longer exists.", e);
//                    throw new PersistenceException(e);
//                }
//            }
//            throw new PersistenceException(e);
//        } finally {
//            if (em != null) {
//                em.close();
//            }
//        }
//    }
//
//    /**
//     * Updates the CourseType.
//     *
//     * @param classCourse
//     */
//    public static void editType(Long id, CourseType type) throws PersistenceException {
//        EntityManager em = null;
//        PersistentClassCourse cc = null;
//        try {
//            em = getEntityManager();
//            em.getTransaction().begin();
//            cc = findEntity(id);
//            cc.setType(type.ordinal());
//            cc = em.merge(cc);
//            em.getTransaction().commit();
//        } catch (Exception e) {
//            String msg = e.getLocalizedMessage();
//            if (msg == null || msg.length() == 0) {
//                if (cc == null) {
//                    LOG.log(Level.FINE, "The PersistentClassCourse with " + id + " no longer exists.", e);
//                    throw new PersistenceException(e);
//                }
//            }
//            throw new PersistenceException(e);
//        } finally {
//            if (em != null) {
//                em.close();
//            }
//        }
//    }
//
//    /**
//     * Updates the CourseType.
//     *
//     * @param classCourse
//     */
//    public static void editFrom(Long id, Date date) throws PersistenceException {
//        EntityManager em = null;
//        PersistentClassCourse cc = null;
//        try {
//            em = getEntityManager();
//            em.getTransaction().begin();
//            cc = findEntity(id);
//            cc.setNotBefore(date);
//            cc = em.merge(cc);
//            em.getTransaction().commit();
//        } catch (Exception e) {
//            String msg = e.getLocalizedMessage();
//            if (msg == null || msg.length() == 0) {
//                if (cc == null) {
//                    LOG.log(Level.FINE, "The PersistentClassCourse with " + id + " no longer exists.", e);
//                    throw new PersistenceException(e);
//                }
//            }
//            throw new PersistenceException(e);
//        } finally {
//            if (em != null) {
//                em.close();
//            }
//        }
//    }
//
//    /**
//     * Updates the CourseType.
//     *
//     * @param classCourse
//     */
//    public static void editTo(Long id, Date date) throws PersistenceException {
//        EntityManager em = null;
//        PersistentClassCourse cc = null;
//        try {
//            em = getEntityManager();
//            em.getTransaction().begin();
//            cc = findEntity(id);
//            cc.setNotAfter(date);
//            cc = em.merge(cc);
//            em.getTransaction().commit();
//        } catch (Exception e) {
//            String msg = e.getLocalizedMessage();
//            if (msg == null || msg.length() == 0) {
//                if (cc == null) {
//                    LOG.log(Level.FINE, "The PersistentClassCourse with " + id + " no longer exists.", e);
//                    throw new PersistenceException(e);
//                }
//            }
//            throw new PersistenceException(e);
//        } finally {
//            if (em != null) {
//                em.close();
//            }
//        }
//    }
//
//    /**
//     * Removes a user from the persistent store.
//     *
//     * @param id
//     */
//    public static void destroy(Long id) throws PersistenceException {
//        EntityManager em = null;
//        try {
//            em = getEntityManager();
//            em.getTransaction().begin();
//            PersistentClassCourse classCourse = null;
//            try {
//                classCourse = em.getReference(PersistentClassCourse.class, id);
//                classCourse.getClassCourseID();
//            } catch (EntityNotFoundException e) {
//                LOG.log(Level.FINE, "The PersistentClassCourse with " + id + " no longer exists.", e);
//                throw new PersistenceException(e);
//            }
//            em.remove(classCourse);
//            em.getTransaction().commit();
//        } finally {
//            if (em != null) {
//                em.close();
//            }
//        }
//    }
//
//    public static List<PersistentClassCourse> findLeaveEntities() {
//        return findLeaveEntities(true, -1, -1);
//    }
//
//    public static List<PersistentClassCourse> findLeaveEntities(int maxResults, int firstResult) {
//        return findLeaveEntities(false, maxResults, firstResult);
//    }
//
//    private static List<PersistentClassCourse> findLeaveEntities(boolean all, int maxResults, int firstResult) {
//        EntityManager em = getEntityManager();
//        try {
//            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
//            cq.select(cq.from(PersistentClassCourse.class));
//            Query q = em.createQuery(cq);
//            if (!all) {
//                q.setMaxResults(maxResults);
//                q.setFirstResult(firstResult);
//            }
//            return q.getResultList();
//        } finally {
//            em.close();
//        }
//    }
//
//    public static List<PersistentClassCourse> findLeaveEntities(PersistentSchoolClass c) {
//        EntityManager em = getEntityManager();
//        try {
//            javax.persistence.Query q = em.createNamedQuery("PersistentClassCourse.findByClassID");
//            q.setParameter("classID", c.getClassID());
//            List<PersistentClassCourse> list = q.getResultList();
//            LOG.log(Level.FINE, "ClassCourse-manager retrieved {0} PersistentClassCourse with classid {1}", new Object[]{list.size(), c.getClassID()});
//            return list;
//        } finally {
//            em.close();
//        }
//    }
//
//    public static List<PersistentClassCourse> findVisibleEntities(PersistentSchoolClass c, ViewState viewState) {
//        EntityManager em = getEntityManager();
//        try {
//            javax.persistence.Query q = em.createNamedQuery("PersistentClassCourse.findVisibleByClassID");
//            q.setParameter("classID", c.getClassID());
//            q.setParameter("viewState", viewState);
//            List<PersistentClassCourse> list = q.getResultList();
//            LOG.log(Level.FINE, "ClassCourse-manager retrieved {0} PersistentClassCourse with classid {1}", new Object[]{list.size(), c.getClassID()});
//            return list;
//        } finally {
//            em.close();
//        }
//    }
//
//    public static List<PersistentClassCourse> findLeaveEntities(PersistentCourse c) {
//        EntityManager em = getEntityManager();
//        try {
//            javax.persistence.TypedQuery<PersistentClassCourse> q = em.createNamedQuery("PersistentClassCourse.findByCourseID", PersistentClassCourse.class);
//            q.setParameter("courseID", c.getCourseID());
//            List<PersistentClassCourse> list = q.getResultList();
//            LOG.log(Level.FINE, "ClassCourse-manager retrieved {0} PersistentClassCourse with courseid {1}", new Object[]{list.size(), c.getCourseID()});
//            return list;
//        } finally {
//            em.close();
//        }
//    }
//
//    public static List<PersistentClassCourse> findLeaveEntities(PersistentSchoolClass schoolClass, PersistentCourse course) {
//        EntityManager em = getEntityManager();
//        try {
//            javax.persistence.TypedQuery<PersistentClassCourse> q = em.createNamedQuery("PersistentClassCourse.findByClassIDAndCourseID", PersistentClassCourse.class);
//            q.setParameter("courseID", course.getCourseID());
//            q.setParameter("classID", schoolClass.getClassID());
//            List<PersistentClassCourse> list = q.getResultList();
//            LOG.log(Level.FINE, "ClassCourse-manager retrieved {0} PersistentClassCourse with courseid {1} and classId {2}", new Object[]{list.size(), course.getCourseID(), schoolClass.getClassID()});
//            return list;
//        } finally {
//            em.close();
//        }
//    }
//
//    public static PersistentClassCourse findEntity(Long id) throws PersistenceException {
//        EntityManager em = getEntityManager();
//        try {
//            return em.find(PersistentClassCourse.class, id);
//        } catch (PersistenceException e) {
//            LOG.log(Level.FINE, "The PersistentClassCourse with " + id + " was not found.", e);
//            throw e;
//        } finally {
//            em.close();
//        }
//    }
//
//    public static int getEntityCount() {
//        EntityManager em = getEntityManager();
//        try {
//            CriteriaQuery<Long> cq = em.getCriteriaBuilder().createQuery(Long.class);
//            Root<PersistentClassCourse> rt = cq.from(PersistentClassCourse.class);
//            cq.select(em.getCriteriaBuilder().count(rt));
//            TypedQuery<Long> q = em.createQuery(cq);
//            return (q.getSingleResult()).intValue();
//        } finally {
//            em.close();
//        }
//    }
//
//	public static void editAccessKey(Long id, String accessKey) {
//        EntityManager em = null;
//        PersistentClassCourse cc = null;
//        try {
//            em = getEntityManager();
//            em.getTransaction().begin();
//            cc = findEntity(id);
//            cc.setAccessKey(accessKey);
//            cc = em.merge(cc);
//            em.getTransaction().commit();
//        } catch (Exception e) {
//            String msg = e.getLocalizedMessage();
//            if (msg == null || msg.length() == 0) {
//                if (cc == null) {
//                    LOG.log(Level.FINE, "The PersistentClassCourse with " + id + " no longer exists.", e);
//                    throw new PersistenceException(e);
//                }
//            }
//            throw new PersistenceException(e);
//        } finally {
//            if (em != null) {
//                em.close();
//            }
//        }
//	}

    public static List<PersistentCourseInClass> findLeaveEntities(PersistentSchoolClass schoolClass, PersistentDwoProfile profile) throws Dwo2Exception {
        EntityManager em = getEntityManager();
        try {
            javax.persistence.Query q = em.createQuery("SELECT a, b FROM PersistentClassCourse a, PersistentCourse b where b.withChildren = 0 and a.courseID = b.courseID and a.classID=:classID and b.dwoProfileID=:dwoProfileID");
            q.setParameter("classID", schoolClass.getClassID());
            q.setParameter("dwoProfileID", profile.getDwoProfileID());
            List<Object[]> list = q.getResultList();
            List<PersistentCourseInClass> result = new ArrayList<PersistentCourseInClass>(list.size());
            for (Object[] o : list) {
                result.add(PersistentCourseInClass.build(((PersistentClassCourse) o[0]), ((PersistentCourse) o[1])));
            }

            LOG.log(Level.FINE, "CourseInClassManager retrieved {0} PersistentCourseInClass for classId {1}", new Object[]{list.size(), schoolClass.getClassID()});
            return result;
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Internal server error ", e);
            throw new Dwo2Exception(Dwo2ExceptionCode.Rest_InternalError, e.getMessage());
        } finally {
            em.close();
        }
    }

    public static void detachLeaveAndUpdateTree(PersistentSchoolClass schoolClass, PersistentCourse course) throws Dwo2Exception {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            //check for results if none remove leave else make it invisible
            course = em.find(PersistentCourse.class, course.getCourseID());
            if (!course.isWithChildren()) {
                //fetch scoId.
                javax.persistence.Query q = em.createQuery(
                        "SELECT sco.scoid FROM PersistentClassCourse cc, PersistentCourse c, PersistentScoContext sco WHERE c.courseID=:courseID and c.withChildren=0 and cc.classID=:classID and c.courseID = cc.courseID and  sco.courseID = :courseID");
                //while not root, update parent node
                q.setParameter("classID", schoolClass.getClassID());
                q.setParameter("courseID", course.getCourseID());
                List<Long> list = q.getResultList(); 
                if (list.size()== 1) {
                    //extract sco and count results
                    long scoID = list.get(0);
                javax.persistence.Query results = em.createQuery(
                        "SELECT ssco.persistentHasRolePK FROM PersistentClassCourse cc, PersistentStudentOfClass soc, PersistentStudentScoContext ssco WHERE cc.courseID=:courseID and cc.classID=:classID and ssco.scoID = :scoID");               
                q.setParameter("classID", schoolClass.getClassID());
                q.setParameter("courseID", course.getCourseID());
                q.setParameter("scoID", scoID);
                List<PersistentHasRolePK> resultKeys = results.getResultList();
                if(resultKeys.size()>0){
                    //there are results so update the class course
                    ClassCourseManager.findEntities(schoolClass, course).forEach((cc) -> {
                        ClassCourseManager.editViewState(cc.getClassCourseID(), ViewState.invisible);
                    });                    
                } else {
                    //no results, destroy leave
                    ClassCourseManager.findEntities(schoolClass, course).forEach((cc) -> {
                        ClassCourseManager.destroy(cc.getClassCourseID());
                    });
                }                                
            } else {
                LOG.log(Level.SEVERE, "Trying to detach a non-leave ");
                throw new Dwo2Exception(Dwo2ExceptionCode.Rest_InternalError, "Trying to detach a non-leave ");
            }
            // walk up the tree and adjust nodes.
            //when parent node of  is with children and is visible or invisible and children count > 0 stop
            //if parent node has children count = 0 remove and loop.
            PersistentCourse parent = em.find(PersistentCourse.class, course.getParentID());
            while(parent!=null){
                //find children
                javax.persistence.Query q2 = em.createQuery(
                        "SELECT cc FROM PersistentClassCourse cc, PersistentCourse c  WHERE c.parentID=:courseID and c.courseID = cc.courseID and cc.classID=:classID");
                //while not root, update parent node
                q2.setParameter("classID", schoolClass.getClassID());
                q2.setParameter("courseID", parent.getCourseID());
                List<Object[]> list2 = q2.getResultList();
                if(list2.size()==0){
                    ClassCourseManager.findEntities(schoolClass, parent).forEach((v)->{
                                        ClassCourseManager.destroy(v.getClassCourseID());
                    });
                }
                course = parent;
                parent = em.find(PersistentCourse.class, course.getParentID());
            }
            em.getTransaction().commit();
            }
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Internal server error ", e);
            throw new Dwo2Exception(Dwo2ExceptionCode.Rest_InternalError, e.getMessage());
        } finally {
            em.close();
        }
    }

}
