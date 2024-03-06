package nl.uu.fi.dwo.lms.jclient.lib.rest.managers;

import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.dom.entities.DomNewSchoolClass4Student;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.util.PathId;
import nl.uu.fi.dwo.rest.entities.RestNewSchoolClass4Student;
import nl.uu.fi.dwo.rest.entities.RestSchoolClass;
import nl.uu.fi.dwo.lms.jclient.lib.rest.transport.StoredRestManager;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.uu.fi.dwo.lms.jclient.lib.rest.transport.RestAuthenticator;

/**
 * Manages the school roles and classes registered in HasRole.
 *
 * @author G.A.J. van der Plas
 */
public class SecureStudentSchoolClassManager {

  private static final Logger LOG =
      Logger.getLogger(SecureStudentSchoolClassManager.class.getName());

  public static Boolean setActiveSchoolClass(DomSchoolClass schoolClass) throws Dwo2Exception {
    RestSchoolClass rest = new RestSchoolClass();
    rest.setRestContext(getContext());
    rest.setDomSchoolClass(schoolClass);

    Boolean result = StoredRestManager.getInstance().put("rest/sec:" + PathId.getId(getContext()) + "/student/schoolclass/select",
        Boolean.class, rest);
    LOG.log(Level.FINE, "Submitted schoolclass {1} for student with username {0}.",
        new Object[] {RestAuthenticator.getInstance().getUsername(),
            rest.getDomSchoolClass().getSchoolClassName()});
    return result;
  }

  static DomContext getContext() {
    return StoredRestManager.getInstance().getContext();
  }

  public static Boolean removeSchoolClass(DomSchoolClass schoolClass) throws Dwo2Exception {
    RestSchoolClass rest = new RestSchoolClass();
    rest.setRestContext(getContext());
    rest.setDomSchoolClass(schoolClass);

    Boolean result = StoredRestManager.getInstance().put("rest/sec:" + PathId.getId(getContext()) + "/student/schoolclass/remove",
        Boolean.class, rest);
    LOG.log(Level.FINE, "Removed schoolclass with username {0} for student with id {1}.",
        new Object[] {rest.getDomSchoolClass().getId(),
            RestAuthenticator.getInstance().getUsername()});
    return result;
  }

//  public static List<DomSchoolClass> getStudentsSchoolClasses() throws Dwo2Exception {
//    List<DomSchoolClass> src;
//    src = StoredRestManager.getInstance().getList("rest/sec:" + PathId.getId(getContext()) + "/student/schoolclass/getList",
//        RestListClassTypes.DomSchoolClass);
//    LOG.log(Level.FINE, "Retrieved list of schoolclasses of the student with username {0}.",
//        new Object[] {RestAuthenticator.getInstance().getUsername()});
//    return src;
//  }

  public static Boolean registerStudentForSchoolClass(DomNewSchoolClass4Student submit)
      throws Dwo2Exception {
    RestNewSchoolClass4Student rest = new RestNewSchoolClass4Student();
    rest.setRestContext(getContext());
    rest.setDomNewSchoolClass4Student(submit);

    Boolean result = StoredRestManager.getInstance().put("rest/sec:" + PathId.getId(getContext()) + "/student/schoolclass/submit",
        Boolean.class, rest);
    LOG.log(Level.FINE, "Submitted schoolclass {1} for registration by student with username {0}.",
        new Object[] {RestAuthenticator.getInstance().getUsername(),
            rest.getDomNewSchoolClass4Student().getId()});
    return result;
  }

//  public static List<DomSchoolClass> getSchoolsClasses() throws Dwo2Exception {
//    List<DomSchoolClass> src;
//    src = StoredRestManager.getInstance().getList("rest/sec:" + PathId.getId(getContext()) + "/student/schoolclass/getSchoolsList",
//        RestListClassTypes.DomSchoolClass);
//    LOG.log(Level.FINE, "Retrieved list of schoolclasses of the school with username {0}.",
//        new Object[] {RestAuthenticator.getInstance().getUsername()});
//    return src;
//  }

//  public static DomSchoolClass getActiveSchoolClass() throws Dwo2Exception {
//    DomSchoolClass sc;
//    sc = StoredRestManager.getInstance().get("rest/sec:" + PathId.getId(getContext()) + "/student/schoolclass/getActive",
//        DomSchoolClass.class);
//    LOG.log(Level.FINE,
//        "Retrieved the active schoolclass with id {1} of the student with username {0}.",
//        new Object[] {RestAuthenticator.getInstance().getUsername(), sc.getId()});
//    return sc;
//  }
}
