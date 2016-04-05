/*Copyrighted 2015. */
package fi.dwo.dwojapplet.gui;

import fi.dwo.rest.dom.entities.DomHasRole;
import fi.dwo.rest.dom.entities.DomSchool4DwoAdmin;
import fi.dwo.rest.dom.entities.DomTeacherAndHasRole;
import fi.dwo.rest.exceptions.Dwo2Exception;
import fi.dwo.dwojapplet.domain.rest.SecureDwoAdminSchoolManager;
import java.util.List;
import java.util.logging.Logger;

/**
 *
 *
 * @author Gert van der Plas
 */
public class RightsDialogProperties {

    private static final Logger LOG = Logger.getLogger(RightsDialogProperties.class.getName());

    public List<DomTeacherAndHasRole>  getTeachersAndHasRoleInSchool(DomSchool4DwoAdmin school) throws Dwo2Exception {
            return SecureDwoAdminSchoolManager.getTeachersAndHasRoleInSchool(school);
    }

    public Boolean update(DomHasRole hr) throws Dwo2Exception {
            return SecureDwoAdminSchoolManager.updateHasRoleRights(hr);
    }
    
}
