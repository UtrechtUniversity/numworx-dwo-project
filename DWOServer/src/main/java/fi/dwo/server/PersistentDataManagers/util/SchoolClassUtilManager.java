package fi.dwo.server.PersistentDataManagers.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

import fi.dwo.commons.persistence.entities.PersistentHasRole;
import fi.dwo.commons.persistence.entities.PersistentSchoolClass;
import fi.dwo.commons.persistence.entities.PersistentStudentOfClass;
import fi.dwo.commons.persistence.entities.PersistentStudentOfClassPK;
import fi.dwo.commons.persistence.entities.PersistentTeacherOfClass;
import fi.dwo.server.PersistentDataManagers.core.HasRoleManager;
import fi.dwo.server.PersistentDataManagers.core.SchoolClassManager;
import fi.dwo.server.PersistentDataManagers.core.StudentOfClassManager;
import fi.dwo.server.PersistentDataManagers.core.TeacherOfClassManager;
import fi.dwo.server.persistence.DwoEmfFactory;

import java.util.logging.Level;
import java.util.logging.Logger;

import nl.uu.fi.dwo.rest.dom.entities.util.ViewState;
import nl.uu.fi.dwo.rest.exceptions.Dwo2ExceptionCode;
import nl.uu.fi.dwo.rest.exceptions.Dwo2RestException;
import nl.uu.fi.dwo.rest.util.DwoDateUtilities;

public class SchoolClassUtilManager {

    private static final Logger LOG = Logger.getLogger(SchoolClassUtilManager.class.getName());

    public static Boolean removeStudentFromSchoolClass(PersistentHasRole phr,
            PersistentSchoolClass schoolClass) {
        try {
            PersistentStudentOfClassPK socId = new PersistentStudentOfClassPK(phr.getPersistentHasRolePK().getUserID(), schoolClass.getClassID(), phr.getPersistentHasRolePK().getSchoolGroupID());
            if (phr.getClassID() != null && socId != null && phr.getClassID().equals(socId.getClassID())) {

// Switch to a better class rather than null                	
                phr.setClassID(null);
// Strategy: take last/newest
                List<PersistentStudentOfClass> candidates = StudentOfClassManager.findEntities(phr.getPersistentHasRolePK());
                candidates = new ArrayList<>(candidates);
                candidates.sort((PersistentStudentOfClass o1, PersistentStudentOfClass o2) -> {
                    java.util.Date d1 = o1.getRegisterDate();
                    java.util.Date d2 = o2.getRegisterDate();
                    int c = d1.compareTo(d2);
                    if (c == 0) {
                        c = o1.getPersistentStudentOfClassPK().getClassID().compareTo(o2.getPersistentStudentOfClassPK().getClassID());
                    }
                    return c;
                });
                ListIterator<PersistentStudentOfClass> iterator = candidates.listIterator(candidates.size());
                while (iterator.hasPrevious()) {
                    PersistentStudentOfClass last = iterator.previous();
                    final Long lastID = last.getPersistentStudentOfClassPK().getClassID();
                    if (!lastID.equals(schoolClass.getClassID())) {
                        phr.setClassID(lastID);
                        break;
                    }
                }

                HasRoleManager.edit(phr);
            }
            StudentOfClassManager.destroy(socId);
        } catch (PersistenceException e) {
            return false;
        }
        return true;
    }

    public static Boolean registerStudentForSchoolClass(PersistentHasRole phr,
            PersistentSchoolClass schoolClass) {
        try {
            PersistentStudentOfClassPK socId = new PersistentStudentOfClassPK(phr.getPersistentHasRolePK().getUserID(), schoolClass.getClassID(), phr.getPersistentHasRolePK().getSchoolGroupID());
            PersistentStudentOfClass soc = new PersistentStudentOfClass();
            soc.setPersistentStudentOfClassPK(socId);
            soc.setRegisterDate(DwoDateUtilities.getCurrentDwoDate());
            StudentOfClassManager.create(soc);

            if (phr.getClassID() == null) {
                phr.setClassID(schoolClass.getClassID());
                HasRoleManager.edit(phr); // TODO met try/catch?
            }

        } catch (PersistenceException e) {
            return false;
        }
        return true;
    }

    public static List<PersistentSchoolClass> getSchoolClassesOfTeacher(PersistentHasRole phr) {
        try {
            List<PersistentTeacherOfClass> tocList = TeacherOfClassManager.findEntities(phr.getPersistentHasRolePK());
            LOG.log(Level.FINE, "Fetched all {0} schoolClasses of user {1] as teacher. ", new Object[]{tocList.size(), phr.getPersistentHasRolePK().getUserID()});
            List<PersistentSchoolClass> schoolClassList = new ArrayList<PersistentSchoolClass>(tocList.size());
            for(PersistentTeacherOfClass teacherOf:tocList){
                PersistentSchoolClass s = SchoolClassManager.findEntity(teacherOf.getPersistentTeacherOfClassPK().getClassID());
                schoolClassList.add(s);
            }
                return schoolClassList;
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Unexpected exception", e);
            throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, "An exception occured while fetching the schoolclasses.");
        }
    }

/*    
 SELECT cc.ClassCourseID, count(ssc.studentsco) FROM tblclasscourse cc
join tblcourse c using(courseid)
left join tblscocontext sc on (cc.courseid = sc.courseid)
left join tblstudentof so on (cc.classid = so.classid)
left join tblstudentscocontext ssc on (so.userid = ssc.userid and sc.scoid = ssc.scoid and ssc.schoolGroupId = so.schoolgroupid)
where cc.classid = 2  and withchildren = 0 group by cc.classcourseid, cc.courseid
    
*/
	public static HashMap<Long, Long> countStudentScoForClassCourse(Long schoolclassid, ViewState viewstate) {

		EntityManager em = DwoEmfFactory.getEntityManager();
		
		Query q = em.createQuery(
"SELECT cc.classCourseID, sc.scoID, so.persistentStudentOfClassPK.userID, ssc.studentSco  FROM PersistentClassCourse cc JOIN PersistentCourse c ON (cc.courseID = c.courseID) "
+ "LEFT JOIN PersistentScoContext sc ON (cc.courseID = sc.courseID) "
+ "LEFT JOIN PersistentStudentOfClass so ON (cc.classID = so.persistentStudentOfClassPK.classID) "
+ "LEFT JOIN PersistentStudentScoContext ssc ON (ssc.scoID = sc.scoID AND ssc.persistentHasRolePK.userID = so.persistentStudentOfClassPK.userID AND ssc.persistentHasRolePK.schoolGroupID = so.persistentStudentOfClassPK.schoolGroupID) "
+ "WHERE c.withChildren = false AND cc.classID = :classID AND cc.viewState = :type "
//+ "GROUP BY cc.classCourseID, cc.courseID"
);
		q.setParameter("classID", schoolclassid);
		q.setParameter("type", viewstate);
		@SuppressWarnings("unchecked")
		List<Object[]> result = q.getResultList();
		HashMap<Long, Long> map = new HashMap<>(); 
		for(Object[] ar : result) {
			Long key =  (Long) ar[0];
			Long count = map.computeIfAbsent(key, k -> 0L);
			Long value = (Long) ar[3];
			if (value != null)
				map.put(key, count + 1L);
		}
		return map;
	}

}
