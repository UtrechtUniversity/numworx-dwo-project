package fi.dwo.server.PersistentDataManagers.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

import fi.dwo.commons.persistence.entities.PersistentCourse;
import fi.dwo.commons.persistence.entities.PersistentHasRole;
import fi.dwo.commons.persistence.entities.PersistentSchoolClass;
import fi.dwo.commons.persistence.entities.PersistentScoContext;
import fi.dwo.commons.persistence.entities.PersistentStudentOfClass;
import fi.dwo.commons.persistence.entities.PersistentStudentOfClassPK;
import fi.dwo.commons.persistence.entities.PersistentStudentScoContext;
import fi.dwo.commons.persistence.entities.PersistentTeacherOfClass;
import fi.dwo.server.PersistentDataManagers.cache.HasRoleCache;
import fi.dwo.server.PersistentDataManagers.core.ClassCourseManager;
import fi.dwo.server.PersistentDataManagers.core.HasRoleManager;
import fi.dwo.server.PersistentDataManagers.core.SchoolClassManager;
import fi.dwo.server.PersistentDataManagers.core.ScoContextManager;
import fi.dwo.server.PersistentDataManagers.core.StudentOfClassManager;
import fi.dwo.server.PersistentDataManagers.core.StudentScoContextManager;
import fi.dwo.server.PersistentDataManagers.core.TeacherOfClassManager;
import fi.dwo.server.persistence.DwoEmfFactory;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

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
                phr.setSchoolClass(null);
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
                HasRoleCache.remove(phr);
                HasRoleManager.edit(phr);
            }
            StudentOfClassManager.destroy(socId);
            ClassCourseManager.uncacheResults(schoolClass);
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
                phr.setSchoolClass(schoolClass);
                HasRoleCache.remove(phr);
                phr = HasRoleManager.edit(phr); // TODO met try/catch?
            }
            ClassCourseManager.uncacheResults(schoolClass);
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
	private static final ExecutorService executor = Executors.newSingleThreadExecutor();
	private static final Long NUL = Long.valueOf(0L);
	public static Boolean asyncCleanupSchoolclass(Boolean work, PersistentSchoolClass cls) {
		if (work) {
			Runnable run = () -> {
				HashMap<Long, Long> map = countStudentScoForClassCourse(cls.getClassID(), ViewState.students);
				for (Map.Entry<Long, Long> entry : map.entrySet()) {
					if (NUL.equals(entry.getValue())) {
						ClassCourseManager.destroy(entry.getKey());
					}
				}
			};
			executor.execute(run);
		}
		
		return work;
		
	}
	
	public static boolean hasNoResults(PersistentCourse pc, PersistentSchoolClass sc, boolean trash) {
        List<PersistentScoContext> scos = ScoContextManager.findEntities(pc);
        if (trash) scos.addAll(ScoContextManager.findTrashedEntities(pc));
        List<PersistentStudentOfClass> students = StudentOfClassManager.findEntities(sc);
        if( ! students.isEmpty() && scos.size() > 0 ) { // kan >1 zijn, als de bovenstaande clear goed z'n best doet.
          long sgId = students.get(0).getPersistentStudentOfClassPK().getSchoolGroupID().longValue();
          Set<Long> users = students.stream().map(s -> s.getPersistentStudentOfClassPK().getUserID()).collect(Collectors.toSet());
          for ( PersistentScoContext scoContext: scos) {
//Bulk: all students results of a school
            List<PersistentStudentScoContext> ss = StudentScoContextManager.findEntities(scoContext, sgId);
            boolean match = ss.stream().anyMatch(pss -> 
                {
                  Long uid = pss.getPersistentHasRolePK().getUserID();
                  return users.contains(uid);
                }
                
                );
            
            if (match) return false;
          }
        }
        return true;
	}
	
	
}
