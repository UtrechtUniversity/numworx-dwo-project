package nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses;

import java.util.List;

import javax.inject.Inject;

import org.osgi.util.promise.Promise;

import fi.dwo.gwt.lib.rest.CallManagers.SecuredStudentSchoolClassManager;
import nl.uu.fi.dwo.lms.gwtclient.gwt.DwoGlobalVars;
import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;

public class SchoolClassServiceStudent {

  DomContext context;
  SecuredStudentSchoolClassManager manager;
  
  @Inject SchoolClassServiceStudent(DwoGlobalVars vars) {
      context = new DomContext();
      context.setDomHasRole(vars.getActiveSchoolRoleAndClass().getHasRole());
      context.setRealm(vars.getCurrentLoginContext().getRealm());
      manager = new SecuredStudentSchoolClassManager();
  }

  Promise<List<DomSchoolClass>> getSchoolClasses() {
    return manager.getSchoolsClasses(context);
  }
  
  Promise<Boolean> setCurrentSchoolClass(DomSchoolClass schoolClass) {
    return manager.setActiveSchoolClass(context, schoolClass);
  }
    
  Promise<List<DomSchoolClass>> getStudentsSchoolClasses() {
    return manager.getStudentsSchoolClasses(context);
  }

}
