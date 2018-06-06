package nl.uu.fi.dwo.lms.jclient.lib.rest.managers;

import nl.uu.fi.dwo.rest.dom.entities.DomHasRole;
import nl.uu.fi.dwo.rest.dom.entities.DomSchool4DwoAdmin;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolFull;
import nl.uu.fi.dwo.rest.dom.entities.DomTeacherAndHasRole;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.RestListClassTypes;
import nl.uu.fi.dwo.rest.entities.RestHasRole;
import nl.uu.fi.dwo.rest.entities.RestSchool4DwoAdmin;
import nl.uu.fi.dwo.rest.entities.RestSchoolFull;
import nl.uu.fi.dwo.lms.jclient.lib.rest.transport.StoredRestManager;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.uu.fi.dwo.lms.jclient.lib.rest.transport.RestAuthenticator;

/**
 * Manages the school roles and classes registered in HasRole.
 *
 * @author G.A.J. van der Plas
 */
public class SecureDwoAdminSchoolManager implements SchoolManager {

  private static final Logger LOG = Logger.getLogger(SecureDwoAdminSchoolManager.class.getName());

  public static List<DomSchool4DwoAdmin> getSchoolList() throws Dwo2Exception {
    List<DomSchool4DwoAdmin> src;
    src = StoredRestManager.getInstance().getList("rest/secure/dwoadmin/school/getList",
        RestListClassTypes.DomSchool4DwoAdmin);
    LOG.log(Level.FINE, "Retrieved list of schools for the dwoadmin with username {0}.",
        new Object[] {RestAuthenticator.getInstance().getUsername()});
    return src;
  }

  public static DomSchoolFull getSchool(DomSchool4DwoAdmin submit) throws Dwo2Exception {
    RestSchool4DwoAdmin rest = new RestSchool4DwoAdmin();
    rest.setRestContext(RestAuthenticator.getInstance().getContext());
    rest.setDomSchool4DwoAdmin(submit);
    DomSchoolFull result = StoredRestManager.getInstance().put("rest/secure/dwoadmin/school/get",
        DomSchoolFull.class, rest);
    LOG.log(Level.FINE, "Retrieved full school with login {1} for dwoadmin with userename {0}.",
        new Object[] {RestAuthenticator.getInstance().getUsername(),
            rest.getDomSchool4DwoAdmin().getId()});
    return result;
  }

  public Boolean updateSchool(DomSchoolFull submit) throws Dwo2Exception {
    RestSchoolFull rest = new RestSchoolFull();
    rest.setRestContext(RestAuthenticator.getInstance().getContext());
    rest.setDomSchoolFull(submit);
    Boolean result = StoredRestManager.getInstance().put("rest/secure/dwoadmin/school/update",
        Boolean.class, rest);
    LOG.log(Level.FINE, "Updated data for school {1} by username {0}.",
        new Object[] {RestAuthenticator.getInstance().getUsername(), submit.getId()});
    return result;
  }

  public Boolean removeSchool(DomSchool4DwoAdmin submit) throws Dwo2Exception {
    RestSchool4DwoAdmin rest = new RestSchool4DwoAdmin();
    rest.setRestContext(RestAuthenticator.getInstance().getContext());
    rest.setDomSchool4DwoAdmin(submit);

    Boolean result = StoredRestManager.getInstance().put("rest/secure/dwoadmin/school/remove",
        Boolean.class, rest);
    LOG.log(Level.FINE, "Submitted school {1} for removal by user with username {0}.",
        new Object[] {RestAuthenticator.getInstance().getUsername(), submit.getId()});
    return result;
  }

  public static Boolean submitSchool(DomSchoolFull submit) throws Dwo2Exception {
    RestSchoolFull rest = new RestSchoolFull();
    rest.setRestContext(RestAuthenticator.getInstance().getContext());
    rest.setDomSchoolFull(submit);

    Boolean result = StoredRestManager.getInstance().put("rest/secure/dwoadmin/school/submit",
        Boolean.class, rest);
    LOG.log(Level.FINE,
        "Submitted school with login {1} to be added by dwoadmin with username {0}.",
        new Object[] {RestAuthenticator.getInstance().getUsername(), submit.getId()});
    return result;
  }

  public static List<DomTeacherAndHasRole> getTeachersAndHasRoleInSchool(
      DomSchool4DwoAdmin domSchool) throws Dwo2Exception {
    RestSchool4DwoAdmin rest = new RestSchool4DwoAdmin();
    rest.setRestContext(RestAuthenticator.getInstance().getContext());
    rest.setDomSchool4DwoAdmin(domSchool);
    List<DomTeacherAndHasRole> result;
    result = StoredRestManager.getInstance().getPutList(
        "rest/secure/dwoadmin/school/getTeachersAndHasRoleInSchool",
        RestListClassTypes.DomTeacherAndHasRole, rest);
    LOG.log(Level.FINE,
        "Retrieved list of teachers and hasRoles in the school {1} for the dwoadmin with username {0}.",
        new Object[] {RestAuthenticator.getInstance().getUsername(), domSchool.getId()});
    return result;
  }

  public static Boolean updateHasRoleRights(DomHasRole hr) throws Dwo2Exception {
    RestHasRole rest = new RestHasRole();
    rest.setRestContext(RestAuthenticator.getInstance().getContext());
    rest.setDomHasRole(hr);

    Boolean result = StoredRestManager.getInstance()
        .put("rest/secure/dwoadmin/school/updateHasRoleRights", Boolean.class, rest);
    LOG.log(Level.FINE,
        "Submitted school with login {1} to be added by dwoadmin with username {0}.",
        new Object[] {RestAuthenticator.getInstance().getUsername(), hr.getId()});
    return result;
  }

  public DomSchoolFull addSchool(DomSchoolFull school) throws Dwo2Exception {
    RestSchoolFull rest = new RestSchoolFull();
    rest.setRestContext(RestAuthenticator.getInstance().getContext());
    rest.setDomSchoolFull(school);

    school = StoredRestManager.getInstance().put("rest/secure/dwoadmin/school/add",
        DomSchoolFull.class, rest);
    LOG.log(Level.FINE, "Submitted school with id {1} to be added by dwoadmin with username {0}.",
        new Object[] {RestAuthenticator.getInstance().getUsername(), school.getId()});
    return school;
  }

}
