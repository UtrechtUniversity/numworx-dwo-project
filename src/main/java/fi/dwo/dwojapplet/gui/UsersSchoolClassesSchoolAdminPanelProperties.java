/**
 * Copyrighted Dec 17, 2015
 */
package fi.dwo.dwojapplet.gui;

import fi.dwo.commons.dom.entities.DomSchoolClass;
import fi.dwo.commons.dom.entities.DomStudent;
import fi.dwo.commons.dom.entities.DomTeacher;
import fi.dwo.commons.dom.entities.DomUser;
import fi.dwo.commons.exceptions.Dwo2Exception;
import fi.dwo.dwojapplet.domain.rest.SecureSchoolAdminSchoolManager;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Property class for ClassTeacherPanel
 *
 * @author G.A.J. van der Plas
 */
public class UsersSchoolClassesSchoolAdminPanelProperties {

    private static final Logger LOG = Logger.getLogger(UsersSchoolClassesSchoolAdminPanelProperties.class.getName());

    public UsersSchoolClassesSchoolAdminPanelProperties() {

    }

    public List<DomSchoolClass> getTeachersSchoolClasses(DomTeacher teacher) throws Dwo2Exception {
        return SecureSchoolAdminSchoolManager.GetTeachersSchoolClasses(teacher);
    }

    public List<DomSchoolClass> getStudentsSchoolClasses(DomStudent student) throws Dwo2Exception {
        return SecureSchoolAdminSchoolManager.GetStudentsSchoolClasses(student);
    }

    public List<DomSchoolClass> getOtherSchoolClasses(DomUser domUser, UsersSchoolClassesSchoolAdminPanel.UserType userType) {
        return new ArrayList<DomSchoolClass>();
    }

        
}
