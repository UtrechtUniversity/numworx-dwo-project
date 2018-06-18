package nl.uu.fi.dwo.lms.jclient.lib.rest.managers;

import nl.uu.fi.dwo.lms.jclient.lib.rest.transport.StoredRestManager;

import java.util.ArrayList;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.uu.fi.dwo.lms.jclient.lib.rest.transport.RestAuthenticator;
import nl.uu.fi.dwo.rest.dom.entities.DomClearStudentDataForScoAndClass;
import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfile;
import nl.uu.fi.dwo.rest.dom.entities.DomMapEntry;
import nl.uu.fi.dwo.rest.dom.entities.DomResultsPerTeacher;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentScoContext;
import nl.uu.fi.dwo.rest.dom.entities.DomTeacherScormValues;
import nl.uu.fi.dwo.rest.entities.RestClearStudentDataForScoAndClass;
import nl.uu.fi.dwo.rest.entities.RestDwoProfile;
import nl.uu.fi.dwo.rest.entities.RestTeacherScormValues;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.util.PathId;

/**
 *
 * @author G.A.J. van der Plas
 */
public class SecuredTeacherResultsManager {
  private static final Logger LOG = Logger.getLogger(SecuredTeacherResultsManager.class.getName());

  public static DomResultsPerTeacher getTeachersResults(DomDwoProfile profile)
      throws Dwo2Exception {
    RestDwoProfile rest = new RestDwoProfile();
    rest.setDomDwoProfile(profile);
    rest.setRestContext(getContext());
    DomResultsPerTeacher src;
    src = StoredRestManager.getInstance().put("rest/sec:" + PathId.getId(getContext()) + "/teacher/results/getTeachersResults",
        DomResultsPerTeacher.class, rest);
    LOG.log(Level.FINE, "Retrieved teacher results for the teacher with username {0}.",
        new Object[] {RestAuthenticator.getInstance().getUsername()});
    return src;
  }

  static DomContext getContext() {
    return RestAuthenticator.getInstance().getContext();
  }

  public static Boolean clearStudentResults(DomClearStudentDataForScoAndClass dom)
      throws Dwo2Exception {
    RestClearStudentDataForScoAndClass rest = new RestClearStudentDataForScoAndClass();
    rest.setClearStudentDataForScoAndClass(dom);;
    rest.setRestContext(getContext());
    Boolean src;
    src = StoredRestManager.getInstance().put("rest/sec:" + PathId.getId(getContext()) + "/teacher/results/clearStudentResults",
        Boolean.class, rest);
    LOG.log(Level.FINE, "Retrieved teacher results for the teacher with username {0}.",
        new Object[] {RestAuthenticator.getInstance().getUsername()});
    return src;
  }

  public static DomResultsPerTeacher createStudentResults(DomClearStudentDataForScoAndClass dom)
      throws Dwo2Exception {
    RestClearStudentDataForScoAndClass rest = new RestClearStudentDataForScoAndClass();
    rest.setClearStudentDataForScoAndClass(dom);;
    rest.setRestContext(getContext());
    DomResultsPerTeacher src;
    src = StoredRestManager.getInstance().put("rest/sec:" + PathId.getId(getContext()) + "/teacher/results/createStudentResults",
        DomResultsPerTeacher.class, rest);
    LOG.log(Level.FINE, "Created teacher results for the teacher with username {0}.",
        new Object[] {RestAuthenticator.getInstance().getUsername()});
    return src;
  }

  public static Boolean setValues(DomStudentScoContext ssc, Map<String, String> map) throws Dwo2Exception {
    RestTeacherScormValues rest = new RestTeacherScormValues();
    DomTeacherScormValues values = new DomTeacherScormValues();
    rest.setDomTeacherScormValues(values);
    rest.setRestContext(getContext());
    values.setStudentScoContext(ssc);
    ArrayList<DomMapEntry<String,String>> list = new ArrayList<DomMapEntry<String,String>>(map.size());
    for(Map.Entry<String, String> entry: map.entrySet()) {
        list.add(new DomMapEntry<String,String>(entry));
    }
    values.setValues(list);
    StoredRestManager.getInstance().put("rest/sec:" + PathId.getId(getContext()) + "/teacher/scormValues/set",
      DomStudentScoContext.class, rest);
    return true;
  }

}
