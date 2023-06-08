package nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses;

import java.util.List;

import javax.inject.Inject;

import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Promises;

import fi.dwo.gwt.lib.rest.CallManagers.SecuredStudentSchoolClassManager;
import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.dom.entities.DomNewSchoolClass4Student;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.exceptions.Dwo2ExceptionCode;

public class SchoolClassServiceStudent {

  DomContext context;
  SecuredStudentSchoolClassManager manager;
  
  @Inject SchoolClassServiceStudent(DomContext ctx) {
      context = ctx;
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

  Promise<Boolean> removeSchoolClass(DomSchoolClass schoolClass) {
    return manager.removeSchoolClass(context, schoolClass);
  }

  Promise<Boolean> addSchoolClass(DomSchoolClass schoolClass, String registrationKey) {
    
    DomNewSchoolClass4Student submit = new DomNewSchoolClass4Student(schoolClass);
    submit.setRegistrationKey(registrationKey);
    return manager.registerStudentForSchoolClass(context, submit);
  }
  
  Promise<DomSchoolClass> getActiveSchoolClass() {
    return manager.getActiveSchoolClass(context).recoverWith(p -> {
      Throwable t = p.getFailure();
      if (t instanceof Dwo2Exception) {
        Dwo2Exception e = (Dwo2Exception) t;
        if ( e.getDwo2Code() == Dwo2ExceptionCode.Rest_Active_SchoolClass_Not_Set) return Promises.resolved(null);
      }
      return null;
    });
  }
}
