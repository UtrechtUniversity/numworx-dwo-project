package fi.dwo.server.PersistentDataManagers.util;

import fi.dwo.commons.persistence.entities.PersistentSchoolClass;
import fi.dwo.commons.persistence.entities.PersistentSchoolGroup;
import fi.dwo.commons.persistence.entities.PersistentUser;
import fi.dwo.server.persistence.DwoEmfFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;

/**
 * Manages class courses in the persistent storage.
 *
 * @author G.A.J. van der Plas
 */
public class TeacherSchoolClassUtilManager {

    private static final Logger LOG = Logger.getLogger(TeacherSchoolClassUtilManager.class.getName());

    private static EntityManager getEntityManager() {
        EntityManager em = DwoEmfFactory.getEntityManager();
        return em;
    }
    
    public static List<PersistenceId> getSharedTeacherClasses(PersistentUser curTeacher, PersistentSchoolGroup sg, PersistentUser otherTeacher){
         EntityManager em = getEntityManager();
        try {
        //create list of students classes in course
        List<Long> r;
         Query q2 = em.createQuery("SELECT c.persistentTeacherOfClassPK.classID FROM PersistentTeacherOfClass c where c.persistentTeacherOfClassPK.userID=:coTeacherId and c.persistentTeacherOfClassPK.schoolGroupID=:teacherSgId and c.persistentTeacherOfClassPK.classID in  (select toc.persistentTeacherOfClassPK.classID from PersistentTeacherOfClass toc where toc.persistentTeacherOfClassPK.userID=:teacherId and toc.persistentTeacherOfClassPK.schoolGroupID =:teacherSgId)");
         q2.setParameter("teacherId", curTeacher.getId());
         q2.setParameter("teacherSgId", sg.getSchoolGroupID());
         q2.setParameter("coTeacherId", otherTeacher.getId());
         r =  q2.getResultList();
//         assertEquals("Should find one class.", teacherResults.size(), 1);
//         assertEquals("Should find class with id=2.", teacherResults.get(0), Long.valueOf(2));
            LOG.log(Level.FINE, "TeacherSchoolClassUtilManager found {0} common classes between current teacher {1} and teacher {2}.", new Object[]{r.size(), curTeacher.getUsername(), otherTeacher.getUsername()});
            List<PersistenceId> teacherResults = new ArrayList<>(r.size());
            r.stream().forEach((v)->teacherResults.add(PersistentSchoolClass.buildPersistenceId(v)));
            return teacherResults;
        } finally {
            em.close();
        }
    }       

    public static List<PersistenceId> getTeachersStudentClasses(PersistentUser curTeacher, PersistentSchoolGroup teacherSg, PersistentSchoolGroup studentSg, PersistentUser student) {
         EntityManager em = getEntityManager();
        try {
        //create list of students classes in course
        List<Long> r;
         Query q = em.createQuery("SELECT s.persistentStudentOfClassPK.classID FROM PersistentStudentOfClass s where s.persistentStudentOfClassPK.userID=:studentId and s.persistentStudentOfClassPK.schoolGroupID=:studentSgId and s.persistentStudentOfClassPK.classID in  (select toc.persistentTeacherOfClassPK.classID from PersistentTeacherOfClass toc where toc.persistentTeacherOfClassPK.userID=:teacherId and toc.persistentTeacherOfClassPK.schoolGroupID =:teacherSgId)");
         q.setParameter("teacherId", curTeacher.getId());
         q.setParameter("teacherSgId", teacherSg.getSchoolGroupID());
         q.setParameter("studentSgId", studentSg.getSchoolGroupID());
         q.setParameter("studentId", student.getId());
         r =  q.getResultList();
//         assertEquals("Should find one class.", studentResults.size(), 1);
//         assertEquals("Should find class with id=2.", studentResults.get(0), Long.valueOf(2));
            LOG.log(Level.FINE, "TeacherSchoolClassUtilManager found {0} common classes between current teacher {1} and student {2}.", new Object[]{r.size(), curTeacher.getUsername(), student.getUsername()});
            List<PersistenceId> studentsResults = new ArrayList<>(r.size());
            r.stream().forEach((v)->studentsResults.add(PersistentSchoolClass.buildPersistenceId(v)));
            return studentsResults;
        } finally {
            em.close();
        }
    }
}
