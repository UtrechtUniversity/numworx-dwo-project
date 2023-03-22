package fi.dwo.server.PersistentDataManagers.util;

import fi.dwo.commons.persistence.entities.PersistentClassCourse;
import fi.dwo.commons.persistence.entities.PersistentCourse;
import fi.dwo.commons.persistence.entities.PersistentCourseInClass;
import fi.dwo.commons.persistence.entities.PersistentDwoProfile;
import fi.dwo.commons.persistence.entities.PersistentSchoolClass;
import fi.dwo.commons.persistence.entities.PersistentScoContext;
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
//                //fetch scoId of course leaf.
//                javax.persistence.Query q = em.createQuery(
//                        "SELECT sco.scoID FROM PersistentScoContext sco WHERE sco.courseID = :courseID");
////                javax.persistence.Query q = em.createQuery(
////                        "SELECT sco.scoID FROM PersistentClassCourse cc, PersistentCourse c, PersistentScoContext sco WHERE c.courseID=:courseID and c.withChildren=0 and cc.classID=:classID and c.courseID = cc.courseID and  sco.courseID = :courseID");
//                //while not root, update parent node
////                q.setParameter("classID", schoolClass.getClassID());
//                q.setParameter("courseID", course.getCourseID());
//                List<Long> list = q.getResultList();
//                if (list.size() >0) {
//                    //extract sco results and count them
//                    long scoID = list.get(0);
                    javax.persistence.Query results = em.createQuery(
                            "SELECT ssco.persistentHasRolePK.userID FROM PersistentCourse c, PersistentClassCourse cc, PersistentStudentOfClass soc, PersistentScoContext sco, PersistentStudentScoContext ssco WHERE cc.courseID=:courseID and cc.classID=:classID and soc.persistentStudentOfClassPK.userID = ssco.persistentHasRolePK.userID and soc.persistentStudentOfClassPK.schoolGroupID = ssco.persistentHasRolePK.schoolGroupID and soc.persistentStudentOfClassPK.classID = cc.classID and ssco.scoID = sco.scoID and sco.courseID = c.courseID and c.courseID=:courseID");
                    results.setParameter("classID", schoolClass.getClassID());
                    results.setParameter("courseID", course.getCourseID());
//                    results.setParameter("scoID", scoID);
                    List<Long> resultKeys = results.getResultList();
                    if (resultKeys.size() > 0) {
                        //there are results so update the class course
                        ClassCourseManager.findEntities(schoolClass, course).forEach((cc) -> {
                            ClassCourseManager.editViewState(cc.getClassCourseID(), ViewState.invisible);
                        });
                    } else {
                        //no results, destroy leaf
                        ClassCourseManager.findEntities(schoolClass, course).forEach((cc) -> {
                            ClassCourseManager.destroy(cc.getClassCourseID());
                        });
                    }
                } else {
                    LOG.log(Level.SEVERE, "Trying to detach a leave without a sco assigned.");
                    throw new Dwo2Exception(Dwo2ExceptionCode.Rest_InternalError, "Trying to detach a leave without a sco assigned.");
                }
                // walk up the tree and adjust nodes.
                //when parent node of  is with children and is visible or invisible and children count > 0 stop
                //if parent node has children count = 0 remove and loop.
                PersistentCourse parent = em.find(PersistentCourse.class, course.getParentID());
                while (parent != null) {
                    //find children
                    javax.persistence.Query q2 = em.createQuery(
                            "SELECT cc FROM PersistentClassCourse cc, PersistentCourse c WHERE c.parentID=:courseID and c.courseID = cc.courseID and cc.classID=:classID");
                    //while not root, update parent node
                    q2.setParameter("classID", schoolClass.getClassID());
                    q2.setParameter("courseID", parent.getCourseID());
                    List<PersistentClassCourse> list2 = q2.getResultList();
                    if (list2.size() == 0) {
                        ClassCourseManager.findEntities(schoolClass, parent).forEach((v) -> {
                            ClassCourseManager.destroy(v.getClassCourseID());
                        });
                    }else {
                        boolean invisible = true;
                        for(PersistentClassCourse pcc : list2){
                            if(pcc.getViewState() == ViewState.studentsAndTeachers){
                                invisible = false;
                                break;
                            }
                        }
                        List<PersistentClassCourse> entities = ClassCourseManager.findEntities(schoolClass, parent);
                        if (!entities.isEmpty()) {
                        if(invisible){
							ClassCourseManager.editViewState(entities.get(0).getClassCourseID(), ViewState.invisible);
                        }else{
                            ClassCourseManager.editViewState(entities.get(0).getClassCourseID(), ViewState.studentsAndTeachers);
                        }}
                    }
                    course = parent;
                    parent = em.find(PersistentCourse.class, course.getParentID());
                }
                em.getTransaction().commit();
//            }
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "This course is not a leave. ", e);
            throw new Dwo2Exception(Dwo2ExceptionCode.Rest_InternalError, e.getMessage());
        } finally {
            em.close();
        }
    }

}
