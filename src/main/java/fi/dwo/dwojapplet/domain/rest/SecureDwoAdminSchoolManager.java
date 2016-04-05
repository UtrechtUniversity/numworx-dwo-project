package fi.dwo.dwojapplet.domain.rest;

import fi.dwo.rest.dom.entities.DomContext;
import fi.dwo.rest.dom.entities.DomHasRole;
import fi.dwo.rest.dom.entities.DomSchool4DwoAdmin;
import fi.dwo.rest.dom.entities.DomSchoolFull;
import fi.dwo.rest.dom.entities.DomTeacherAndHasRole;
import fi.dwo.rest.exceptions.Dwo2Exception;
import fi.dwo.rest.RestListClassTypes;
import fi.dwo.rest.entities.RestHasRole;
import fi.dwo.rest.entities.RestSchool4DwoAdmin;
import fi.dwo.rest.entities.RestSchoolFull;
import fi.dwo.dwojapplet.REST.StoredRestManager;
import fi.dwo.dwojapplet.domain.DwoHelper;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Manages the school roles and classes registered in HasRole.
 *
 * @author G.A.J. van der Plas
 */
public class SecureDwoAdminSchoolManager {

    private static final Logger LOG = Logger.getLogger(SecureDwoAdminSchoolManager.class.getName());

    public static List<DomSchool4DwoAdmin> getSchoolList() throws Dwo2Exception {
        List<DomSchool4DwoAdmin> src;
        src = StoredRestManager.getInstance().getList("/rest/secure/dwoadmin/school/getList", RestListClassTypes.DomSchool4DwoAdmin);
        LOG.log(Level.FINE, "Retrieved list of schoolsfor the dwoadmin with id {0}.", new Object[]{DwoHelper.getCurrentUser().getId()});
        return src;
    }

    public static DomSchoolFull getSchool(DomSchool4DwoAdmin submit) throws Dwo2Exception {
        RestSchool4DwoAdmin sts = new RestSchool4DwoAdmin();
        sts.setRestContext(new DomContext());
        sts.setDomSchool4DwoAdmin(submit);
        DomSchoolFull result = StoredRestManager.getInstance().put("/rest/secure/dwoadmin/school/get", DomSchoolFull.class, sts);
        LOG.log(Level.FINE, "Retrieved full school with login {1} for dwoadmin with id {0}.", new Object[]{DwoHelper.getCurrentUser().getId(), sts.getDomSchool4DwoAdmin().getId()});
        return result;
    }

    public static Boolean updateSchool(DomSchoolFull submit) throws Dwo2Exception {
        RestSchoolFull sts = new RestSchoolFull();
        sts.setRestContext(new DomContext());
        sts.setDomSchoolFull(submit);
        Boolean result = StoredRestManager.getInstance().put("/rest/secure/dwoadmin/school/update", Boolean.class, sts);
        LOG.log(Level.FINE, "Updated data for school {1} by user {0}.", new Object[]{DwoHelper.getCurrentUser().getId(), submit.getId()});
        return result;
    }

    public static Boolean removeSchool(DomSchool4DwoAdmin submit) throws Dwo2Exception {
        RestSchool4DwoAdmin restSchool = new RestSchool4DwoAdmin();
        restSchool.setRestContext(new DomContext());
        restSchool.setDomSchool4DwoAdmin(submit);

        Boolean result = StoredRestManager.getInstance().put("/rest/secure/dwoadmin/school/remove", Boolean.class, restSchool);
        LOG.log(Level.FINE, "Submitted school {1} for removal by user with id {0}.", new Object[]{DwoHelper.getCurrentUser().getId(), submit.getId()});
        return result;
    }

    public static Boolean submitSchool(DomSchoolFull submit) throws Dwo2Exception {
        RestSchoolFull sts = new RestSchoolFull();
        sts.setRestContext(new DomContext());
        sts.setDomSchoolFull(submit);

        Boolean result = StoredRestManager.getInstance().put("/rest/secure/dwoadmin/school/submit", Boolean.class, sts);
        LOG.log(Level.FINE, "Submitted school with login {1} to be added by dwoadmin with id {0}.", new Object[]{DwoHelper.getCurrentUser().getId(), submit.getId()});
        return result;
    }

    public static List<DomTeacherAndHasRole> getTeachersAndHasRoleInSchool(DomSchool4DwoAdmin domSchool) throws Dwo2Exception {
        RestSchool4DwoAdmin restSchool = new RestSchool4DwoAdmin();
        restSchool.setRestContext(new DomContext());
        restSchool.setDomSchool4DwoAdmin(domSchool);
        List<DomTeacherAndHasRole> result;
        result = StoredRestManager.getInstance().getPutList("/rest/secure/dwoadmin/school/getTeachersAndHasRoleInSchool", RestListClassTypes.DomTeacherAndHasRole, restSchool);
        LOG.log(Level.FINE, "Retrieved list of teachers and hasRoles in the school {1} for the dwoadmin with id {0}.", new Object[]{DwoHelper.getCurrentUser().getId(), domSchool.getId()});
        return result;
    }

    public static Boolean updateHasRoleRights(DomHasRole hr) throws Dwo2Exception {
        RestHasRole sts = new RestHasRole();
        sts.setRestContext(new DomContext());
        sts.setDomHasRole(hr);

        Boolean result = StoredRestManager.getInstance().put("/rest/secure/dwoadmin/school/updateHasRoleRights", Boolean.class, sts);
        LOG.log(Level.FINE, "Submitted school with login {1} to be added by dwoadmin with id {0}.", new Object[]{DwoHelper.getCurrentUser().getId(), hr.getId()});
        return result;
    }

}
