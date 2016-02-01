/**
 * Copyrighted Dec 17, 2015
 */
package fi.dwo.dwojapplet.gui;

import fi.dwo.commons.dom.entities.DomRemoveTeacherFromSchoolClass;
import fi.dwo.commons.dom.entities.DomSchoolClass;
import fi.dwo.commons.dom.entities.DomTeacher;
import fi.dwo.commons.exceptions.Dwo2Exception;
import fi.dwo.dwojapplet.domain.rest.SecureTeacherSchoolClassManager;
import java.util.List;
import java.util.logging.Logger;

/**
 * Property class for ClassTeacherPanel
 *
 * @author G.A.J. van der Plas
 */
public class TeachersInSchoolClassTeacherPanelProperties {

    private static final Logger LOG = Logger.getLogger(TeachersInSchoolClassTeacherPanelProperties.class.getName());

    public TeachersInSchoolClassTeacherPanelProperties() {

    }

    public List<DomTeacher> getTeachersInSchoolClass(DomSchoolClass sc) throws Dwo2Exception {
        return SecureTeacherSchoolClassManager.GetTeachersInSchoolClass(sc);
    }
    public void removeTeacherFromSchoolClass(DomSchoolClass sc, DomTeacher t) throws Dwo2Exception {
        DomRemoveTeacherFromSchoolClass submit = new DomRemoveTeacherFromSchoolClass();
        submit.setSchoolClass(sc);
        submit.setTeacher(t);
        SecureTeacherSchoolClassManager.removeTeacherFromSchoolClass(submit);
    }
}
