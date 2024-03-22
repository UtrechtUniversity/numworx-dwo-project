package nl.uu.fi.dwo.lms.jclient.lib.rest.managers;

import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.dom.entities.DomHasRole;
import nl.uu.fi.dwo.rest.dom.entities.DomSchool4DwoAdmin;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolAdminAndHasRole;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolFull;
import nl.uu.fi.dwo.rest.dom.entities.DomStatistics;
import nl.uu.fi.dwo.rest.dom.entities.DomTeacherAndHasRole;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.util.PathId;
import nl.uu.fi.dwo.rest.RestListClassTypes;
import nl.uu.fi.dwo.rest.entities.RestContext;
import nl.uu.fi.dwo.rest.entities.RestHasRole;
import nl.uu.fi.dwo.rest.entities.RestSchool4DwoAdmin;
import nl.uu.fi.dwo.rest.entities.RestSchoolFull;
import nl.uu.fi.dwo.lms.jclient.lib.rest.transport.StoredRestManager;
import java.util.List;

/**
 * Manages the school roles and classes registered in HasRole.
 *
 * @author G.A.J. van der Plas
 */
public class SecureDwoAdminSchoolManager implements SchoolManager {

  public static List<DomSchool4DwoAdmin> getSchoolList() throws Dwo2Exception {
    List<DomSchool4DwoAdmin> src;
    RestContext rest = new RestContext();
    rest.setRestContext(getContext());
    src = StoredRestManager.getInstance().getPutList("rest/sec:" + PathId.getId(getContext()) + "/dwoadmin/school/getList",
        RestListClassTypes.DomSchool4DwoAdmin, rest);
    return src;
  }

  public static DomSchoolFull getSchool(DomSchool4DwoAdmin submit) throws Dwo2Exception {
    RestSchool4DwoAdmin rest = new RestSchool4DwoAdmin();
    rest.setRestContext(getContext());
    rest.setDomSchool4DwoAdmin(submit);
    DomSchoolFull result = StoredRestManager.getInstance().put("rest/sec:" + PathId.getId(getContext()) + "/dwoadmin/school/get",
        DomSchoolFull.class, rest);
    return result;
  }

  public static DomStatistics getStatistics(DomSchool4DwoAdmin submit) throws Dwo2Exception {
	    RestSchool4DwoAdmin rest = new RestSchool4DwoAdmin();
	    rest.setRestContext(getContext());
	    rest.setDomSchool4DwoAdmin(submit);
	    DomStatistics result = StoredRestManager.getInstance().put("rest/sec:" + PathId.getId(getContext()) + "/dwoadmin/school/statistics",
	    		DomStatistics.class, rest);
	    return result;
	  
  }
  
  static DomContext getContext() {
    return StoredRestManager.getInstance().getContext();
  }

  public Boolean updateSchool(DomSchoolFull submit) throws Dwo2Exception {
    RestSchoolFull rest = new RestSchoolFull();
    rest.setRestContext(getContext());
    rest.setDomSchoolFull(submit);
    Boolean result = StoredRestManager.getInstance().put("rest/sec:" + PathId.getId(getContext()) + "/dwoadmin/school/update",
        Boolean.class, rest);
    return result;
  }

  public Boolean removeSchool(DomSchool4DwoAdmin submit) throws Dwo2Exception {
    RestSchool4DwoAdmin rest = new RestSchool4DwoAdmin();
    rest.setRestContext(getContext());
    rest.setDomSchool4DwoAdmin(submit);
    Boolean result = StoredRestManager.getInstance().put("rest/sec:" + PathId.getId(getContext()) + "/dwoadmin/school/remove",
        Boolean.class, rest);
    return result;
  }

  public static Boolean submitSchool(DomSchoolFull submit) throws Dwo2Exception {
    RestSchoolFull rest = new RestSchoolFull();
    rest.setRestContext(getContext());
    rest.setDomSchoolFull(submit);
    Boolean result = StoredRestManager.getInstance().put("rest/sec:" + PathId.getId(getContext()) + "/dwoadmin/school/submit",
        Boolean.class, rest);
    return result;
  }

  public static List<DomTeacherAndHasRole> getTeachersAndHasRoleInSchool(
      DomSchool4DwoAdmin domSchool) throws Dwo2Exception {
    RestSchool4DwoAdmin rest = new RestSchool4DwoAdmin();
    rest.setRestContext(getContext());
    rest.setDomSchool4DwoAdmin(domSchool);
    List<DomTeacherAndHasRole> result;
    result = StoredRestManager.getInstance().getPutList(
        "rest/sec:" + PathId.getId(getContext()) + "/dwoadmin/school/getTeachersAndHasRoleInSchool",
        RestListClassTypes.DomTeacherAndHasRole, rest);
    return result;
  }

  public static List<DomSchoolAdminAndHasRole> getSchoolAdminsAndHasRoleInSchool(
	      DomSchool4DwoAdmin domSchool) throws Dwo2Exception {
	    RestSchool4DwoAdmin rest = new RestSchool4DwoAdmin();
	    rest.setRestContext(getContext());
	    rest.setDomSchool4DwoAdmin(domSchool);
	    List<DomSchoolAdminAndHasRole> result;
	    result = StoredRestManager.getInstance().getPutList(
	        "rest/sec:" + PathId.getId(getContext()) + "/dwoadmin/school/getSchoolAdminsAndHasRoleInSchool",
	        RestListClassTypes.DomSchoolAdminAndHasRole, rest);
	    return result;
	  }

  
  public static Boolean updateHasRoleRights(DomHasRole hr) throws Dwo2Exception {
    RestHasRole rest = new RestHasRole();
    rest.setRestContext(getContext());
    rest.setDomHasRole(hr);

    Boolean result = StoredRestManager.getInstance()
        .put("rest/sec:" + PathId.getId(getContext()) + "/dwoadmin/school/updateHasRoleRights", Boolean.class, rest);
    return result;
  }

  public DomSchoolFull addSchool(DomSchoolFull school) throws Dwo2Exception {
    RestSchoolFull rest = new RestSchoolFull();
    rest.setRestContext(getContext());
    rest.setDomSchoolFull(school);

    school = StoredRestManager.getInstance().put("rest/sec:" + PathId.getId(getContext()) + "/dwoadmin/school/add",
        DomSchoolFull.class, rest);
    return school;
  }

}
