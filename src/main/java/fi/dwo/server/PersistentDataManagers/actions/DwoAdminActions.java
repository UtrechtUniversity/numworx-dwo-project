package fi.dwo.server.PersistentDataManagers.actions;

import java.util.List;

import fi.dwo.commons.persistence.entities.PersistentStudentModelContext;
import fi.dwo.server.PersistentDataManagers.access.DwoAdminDomainAuthorizer.Context;
import nl.uu.fi.dwo.rest.dom.entities.DomCourseFull;
import nl.uu.fi.dwo.rest.dom.entities.DomScoContextFull;
import nl.uu.fi.dwo.rest.dom.entities.DomScoData;

public interface DwoAdminActions {

  DomScoContextFull update(Context context, DomScoContextFull scoContext, DomScoData scoData,
      Boolean delete);

  DomScoContextFull add(Context context, DomScoContextFull scoContext, DomScoData scoData);

  Boolean removeSco(Context context);

  DomCourseFull add(Context context, DomCourseFull course);

  DomCourseFull update(Context context, DomCourseFull course);
  
  Boolean removeCourse(Context context);

  List<PersistentStudentModelContext> getReducedStudentModels(Context context);

  Boolean trashCourse(Context context);

  Boolean trashSco(Context context);

}
