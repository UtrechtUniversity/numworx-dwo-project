package fi.dwo.server.PersistentDataManagers.actions;

import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import fi.dwo.commons.persistence.MySQLPersistenceId;
import fi.dwo.commons.persistence.entities.PersistentClassCourse;
import fi.dwo.commons.persistence.entities.PersistentCourse;
import fi.dwo.commons.persistence.entities.PersistentCourseData;
import fi.dwo.commons.persistence.entities.PersistentDwoProfile;
import fi.dwo.commons.persistence.entities.PersistentSchool;
import fi.dwo.commons.persistence.entities.PersistentScoContext;
import fi.dwo.server.PersistentDataManagers.core.ACLManager;
import fi.dwo.server.PersistentDataManagers.core.ClassCourseManager;
import fi.dwo.server.PersistentDataManagers.core.CourseDataManager;
import fi.dwo.server.PersistentDataManagers.core.CourseManager;
import fi.dwo.server.PersistentDataManagers.core.DwoProfileManager;
import fi.dwo.server.PersistentDataManagers.core.SchoolManager;
import fi.dwo.server.PersistentDataManagers.core.ScoContextManager;
import nl.uu.fi.dwo.rest.dom.entities.DomCourse;
import nl.uu.fi.dwo.rest.dom.entities.DomCourseFull;
import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfile;
import nl.uu.fi.dwo.rest.dom.entities.DomSchool;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.exceptions.Dwo2ExceptionCode;
import nl.uu.fi.dwo.rest.exceptions.Dwo2RestException;

public class MySQLCourseActions {
  private static final Logger LOG = Logger.getLogger(MySQLCourseActions.class.getName());

  public static DomCourseFull update(PersistentCourse pc, DomCourseFull course) {
    try {
    	PersistentCourseData pcd = CourseDataManager.findEntity(pc.getCourseID());
    	if (pcd == null) {
    		pcd = new PersistentCourseData();
    		pcd.setCourseID(pc.getCourseID());
    		pcd.setDescription(pc.getDescription());
    		pcd.setImageData(pc.getImageData());
    		CourseDataManager.create(pcd);
    	}
 // editable fields?
    if(course.getName() != null) pc.setName(course.getName());
    if(course.getDescription() != null) {
    	pcd.setDescription(course.getDescription());
    	pcd.setDescriptionbytes(null);
    	pc.setDescription(course.getDescription());
    }
    if(course.getImage() != null) pc.setImage(course.getImage()); // dwoadmin only!
    if(course.getImageData()!=null) {
    	pcd.setImageData(course.getImageData());
    	pc.setImageData(course.getImageData());
    }
    pc.setNotVisible(course.isNotVisible());
    if(course.getExport() != null)
        pc.setExport(course.getExport().booleanValue());
    if(course.getSequenceNr() != null)
        pc.setSequencenr(course.getSequenceNr());
//SCHOOL to NULL-school NOT YET
    Long schoolId = null;
    if(course.getSchoolId() != null) {
        DomSchool ds = new DomSchool(); ds.setId(course.getSchoolId());
        schoolId = MySQLPersistenceId.getNativeId(ds);
    }
    
    Long schoolID_pc = pc.getSchoolID(); // public courses have schoolid 0 in DWOJClient
    if(schoolID_pc == null) schoolID_pc = Long.valueOf(0L);
    
    if(schoolId != null && ! schoolId.equals(schoolID_pc) ) {
        Dwo2RestException exception = new Dwo2RestException(Dwo2ExceptionCode.User_IllegalAction, "You Don't Have Permission to access this.");
        LOG.log(Level.WARNING, "Assume profileadmin or dwoadmin? " + schoolID_pc + " " + schoolId, exception);
        throw exception;
    } else
//MOVE to different parent within the same school
    if(course.getParentID() != null) {      
        DomCourse parent = new DomCourse();
        parent.setId(course.getParentID());
        Long parentID = MySQLPersistenceId.getNativeId(parent);
        if(parentID.longValue() != 0L) {
            PersistentCourse parentcourse = CourseManager.findEntity(parentID);
            if ( !parentcourse.isWithChildren()) {
                String name = "null";
                throw new Dwo2RestException(Dwo2ExceptionCode.User_IllegalAction, "Wrong parent using usercode " + name + ".");
            }
        }
//verify parent exists OR parentID = 0 has "haschildren"
        
        pc.setParentID(parentID);
    }
//optimistic locking or user managed?
    if(course.getLastChangeTimeStamp() != null)
        pc.setLastChangeTimeStamp(course.getLastChangeTimeStamp()); // Optimistic locking?
    else 
        pc.setLastChangeTimeStamp(System.currentTimeMillis()); // FIXME Gert is dit de bedoeling of JPA managed?

    pc=CourseManager.edit(pc);
    pcd = CourseDataManager.edit(pcd);
    course = pc.buildDomCourseFull();
    pcd.fillDomCourseFull(course);
    return course;
} catch (Dwo2Exception e) {
    throw new Dwo2RestException(e);
}

  }
  public static DomCourseFull add(DomCourseFull course) {
    try {
//Security...
        PersistentCourse pc = new PersistentCourse();
        PersistentCourseData pcd = new PersistentCourseData();
        DomDwoProfile profile = new DomDwoProfile();
        profile.setId(course.getDwoProfileId());            
        pc.setDwoProfileID(MySQLPersistenceId.getNativeId(profile));
//editable fields?
        if(course.getName() != null) pc.setName(course.getName());
        if(course.getDescription() != null) {
        	pcd.setDescription(course.getDescription());
        	pc.setDescription(course.getDescription());
        }
        if(course.getImage() != null) pc.setImage(course.getImage()); // only if dwoadmin
        if(course.getImageData()!=null) {
        	pcd.setImageData(course.getImageData());
        	pc.setImageData(course.getImageData());
        }
        pc.setNotVisible(course.isNotVisible());
        if(course.getExport() != null)
            pc.setExport(course.getExport().booleanValue());
        if(course.getSequenceNr() != null)
            pc.setSequencenr(course.getSequenceNr());
//Missing
        if((course.getWithChildren()!= null))
            pc.setWithChildren(course.getWithChildren());
//SCHOOL:
        Long schoolId = null;
        if(course.getSchoolId() != null) {
            DomSchool ds = new DomSchool(); ds.setId(course.getSchoolId());
            schoolId = MySQLPersistenceId.getNativeId(ds);
            if(schoolId.longValue() == 0L) schoolId = null;
        }           
        pc.setSchoolID(schoolId);
//      if(schoolId != null && ! schoolId.equals(schoolID_pc) ) {
//          Dwo2RestException exception = new Dwo2RestException(Dwo2ExceptionCode.User_IllegalAction, "You Don't Have Permission to access this using usercode " + sc.getUserPrincipal().getName() + ".");
//          LOG.log(Level.WARNING, "Assume profileadmin or dwoadmin? " + schoolID_pc + " " + schoolId, exception);
//          throw exception;
//      } else
//parent within the same school
        if(course.getParentID() != null) {      
            DomCourse parent = new DomCourse();
            parent.setId(course.getParentID());
            Long parentID = MySQLPersistenceId.getNativeId(parent);
            if(parentID.longValue() != 0L) {
                PersistentCourse parentcourse = CourseManager.findEntity(parentID);
                if ( !parentcourse.isWithChildren()) {
                    String name = "null";
                    throw new Dwo2RestException(Dwo2ExceptionCode.User_IllegalAction, "Wrong parent using usercode " + name + ".");
                }
            }
//verify parent exists OR parentID = 0 has "haschildren"
            
            pc.setParentID(parentID);
        } else
            pc.setParentID(0L);
        CourseManager.create(pc);
        pcd.setCourseID(pc.getCourseID());
        CourseDataManager.create(pcd);
        course = pc.buildDomCourseFull();
        pcd.fillDomCourseFull(course);
    } catch (Dwo2Exception e) {
        throw new Dwo2RestException(e);
    }
    
    return course;

  }
  
  public static void remove(PersistentCourse pc) {
      List<PersistentCourse> children = CourseManager.findChildrenOf(pc);
      children.addAll(CourseManager.findTrashedChildrenOf(pc));
      children.stream().forEach(MySQLCourseActions::remove);
      List<PersistentScoContext> scos = ScoContextManager.findEntities(pc);
      scos.addAll(ScoContextManager.findTrashedEntities(pc));
      scos.stream().forEach(sco -> {MySQLScoContextActions.remove(sco,pc);});
      List<PersistentClassCourse> cc = ClassCourseManager.findEntities(pc);
      cc.forEach(ccc -> ClassCourseManager.destroy(ccc.getClassCourseID()));
      ACLManager.updateByCourse(pc, Collections.emptyList());
      CourseManager.destroy(pc.getCourseID());
      CourseDataManager.destroy(pc.getCourseID());
      if (pc.getTrashID() == 0) {
	 // sequencenr doorschuiven.
	      relocateCourses(pc);
      }
   }
  
  public static void trash(PersistentCourse pc) {
	  long trashid = pc.getTrashID();
	  pc.setTrashID(System.currentTimeMillis());
	  CourseManager.edit(pc);
	  if (trashid == 0)
		  relocateCourses(pc);
  }

  private static void relocateCourses(PersistentCourse pc) {
	List<PersistentCourse> children;
	long parentID = pc.getParentID();
	  Long sequencenr = pc.getSequencenr();
	  if(sequencenr == null) return;
// update sequencenr of siblings.
	  long pcseq = sequencenr.longValue();
	  if(parentID != 0) {
	    PersistentCourse parent = CourseManager.findEntity(parentID);
	    children = CourseManager.findChildrenOf(parent);
	  } else {
	    PersistentDwoProfile profile = DwoProfileManager.findEntity(pc.getDwoProfileID());
	    PersistentSchool school = 
	        pc.getSchoolID() == null || pc.getSchoolID() == 0L ? null :
	        SchoolManager.findEntity(pc.getSchoolID());
	    children = CourseManager.findChildrenOf(profile, school);
	  }
	  for(PersistentCourse c: children) {
	    Long seq = c.getSequencenr();
	    if(seq != null && seq.longValue() > pcseq) {
	      c.setSequencenr(seq-1);
	      CourseManager.edit(c);
	    }
	  }
}
}
