package fi.dwo.server.PersistentDataManagers.util;

import fi.dwo.commons.persistence.entities.PersistentClassCourse;
import fi.dwo.commons.persistence.entities.PersistentSchoolClass;
import fi.dwo.commons.persistence.entities.PersistentStudentScoContext;
import fi.dwo.server.persistence.DwoEmfFactory;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;

/**
 * Manages class courses in the persistent storage.
 *
 * @author G.A.J. van der Plas
 */
public class StudentScoInClassManager {

    private static final Logger LOG = Logger.getLogger(StudentScoInClassManager.class.getName());

    private static EntityManager getEntityManager() {
        EntityManager em = DwoEmfFactory.getEntityManager();
        return em;
    }
    
    public static List<PersistentStudentScoContext> findEntities(PersistentClassCourse classCourse){
         EntityManager em = getEntityManager();
        try {
            javax.persistence.Query q = em.createQuery("SELECT ss FROM PersistentStudentScoContext ss, PersistentScoContext s, PersistentCourse c, PersistentClassCourse cc "
                    + "where cc.courseID = c.courseID and s.courseID=c.courseID and ss.scoID = s.scoID and cc.classCourseID = :classCourseID");
            q.setParameter("classCourseID", classCourse.getClassCourseID());
            List<PersistentStudentScoContext> list = q.getResultList();
            LOG.log(Level.FINE, "StudentScoInClassManager retrieved {0} PersistentStudentScoContext for classId {1}", new Object[]{list.size(), classCourse.getClassID()});
            return list;
        } finally {
            em.close();
        }
    }       
    
    public static List<PersistentStudentScoContext> findEntities(PersistentSchoolClass schoolClass){
         EntityManager em = getEntityManager();
        try {
            javax.persistence.Query q = em.createQuery("SELECT ss FROM PersistentStudentScoContext ss, PersistentScoContext s, PersistentClassCourse cc where  s.courseID=cc.courseID and ss.scoID = s.scoID and cc.classID = :classID and (ss.userid, ss.groupid) in (select (userid, groupid) from tblstudentof soc where cc.classid = soc.classid)");
            q.setParameter("classID", schoolClass.getClassID());
            List<PersistentStudentScoContext> list = q.getResultList();
            LOG.log(Level.FINE, "StudentScoInClassManager retrieved {0} PersistentStudentScoContext for classId {1}", new Object[]{list.size(), schoolClass.getClassID()});
            return list;
        } finally {
            em.close();
        }
    }       
}
