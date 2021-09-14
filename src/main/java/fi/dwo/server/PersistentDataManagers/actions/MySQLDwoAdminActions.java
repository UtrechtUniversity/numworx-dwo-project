package fi.dwo.server.PersistentDataManagers.actions;

import java.util.List;

import fi.dwo.commons.persistence.entities.PersistentCourse;
import fi.dwo.commons.persistence.entities.PersistentSchool;
import fi.dwo.commons.persistence.entities.PersistentScoContext;
import fi.dwo.commons.persistence.entities.PersistentScoData;
import fi.dwo.commons.persistence.entities.PersistentStudentModelContext;
import fi.dwo.server.PersistentDataManagers.access.DwoAdminDomainAuthorizer.Context;
import fi.dwo.server.PersistentDataManagers.access.DwoAdminDomainAuthorizer.DwoAdminPersistentContext;
import fi.dwo.server.PersistentDataManagers.core.ScoDataManager;
import fi.dwo.server.PersistentDataManagers.core.StudentModelContextManager;
import nl.uu.fi.dwo.rest.dom.entities.DomCourseFull;
import nl.uu.fi.dwo.rest.dom.entities.DomScoContextFull;
import nl.uu.fi.dwo.rest.dom.entities.DomScoData;

public class MySQLDwoAdminActions implements DwoAdminActions {

  @Override
  public DomScoContextFull update(Context context, DomScoContextFull scoContext, DomScoData scoData,
      Boolean delete) {
    DwoAdminPersistentContext adminCtx = context.getAdminCtx();
    PersistentScoContext pc = adminCtx.scoContext;
    PersistentScoData    sd = adminCtx.scoData = ScoDataManager.findEntity(pc.getScoID());
    boolean b = delete == null ? true : delete.booleanValue();
    return MySQLScoContextActions.update(pc, sd, scoContext, scoData, b);
  }

  @Override
  public DomScoContextFull add(Context context, DomScoContextFull scoContext, DomScoData scoData) {
    DwoAdminPersistentContext adminCtx = context.getAdminCtx();
    PersistentCourse c = adminCtx.course;
    return MySQLScoContextActions.add(c, scoContext, scoData);
  }

  @Override
  public Boolean removeSco(Context context) {
    PersistentScoContext pc = context.getAdminCtx().scoContext;
    if(pc == null) {
      return Boolean.FALSE;
    }
    PersistentCourse c = context.getAdminCtx().course;
    MySQLScoContextActions.remove(pc,c);
    return Boolean.TRUE;
  }

  @Override
  public DomCourseFull add(Context context, DomCourseFull course) {
    course = MySQLCourseActions.add(course);
    return course;
  }

  @Override
  public DomCourseFull update(Context context, DomCourseFull course) {
    PersistentCourse c = context.getAdminCtx().course;
    return MySQLCourseActions.update(c, course);
  }

  @Override
  public Boolean removeCourse(Context context) {
    PersistentCourse course = context.getAdminCtx().course;
    if(course == null) {
      return Boolean.TRUE;
    } else {
      MySQLCourseActions.remove(course);
      return Boolean.TRUE;
    }
  }

  public List<PersistentStudentModelContext> getReducedStudentModels(Context context) {
    PersistentSchool dummy = new PersistentSchool(0L);
	List<PersistentStudentModelContext> pModels = StudentModelContextManager.findReducedEntities(dummy);
    return pModels;
  }

}
