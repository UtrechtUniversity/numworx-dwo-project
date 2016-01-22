/*Copyrighted 2015. */
package fi.dwo.dwojapplet.gui.underconstruction;

import fi.dwo.commons.dom.entities.DomNewSchoolClass4Student;
import fi.dwo.commons.dom.entities.DomSchoolClass;
import fi.dwo.commons.exceptions.Dwo2Exception;
import fi.dwo.commons.exceptions.Dwo2ExceptionCode;
import fi.dwo.dwojapplet.domain.rest.SecureStudentSchoolClassManager;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 *
 * @author Gert van der Plas <gertvdplas@gmail.com>
 */
public class SchoolClassRegister4StudentProperties {

    private static final Logger LOG = Logger.getLogger(SchoolClassRegister4StudentProperties.class.getName());
    private List<DomSchoolClass> scList;

    public void init() {
        try {
            scList = SecureStudentSchoolClassManager.getStudentsSchoolClasses();
        }
        catch (Dwo2Exception ex) {
            LOG.log(Level.SEVERE, ex.getMessage());
        }
    }

    public List<DomSchoolClass> getStudentsSchoolClasses() {
        return scList;
    }

    public List<DomSchoolClass> getSchoolsClasses() throws Dwo2Exception {
        throw new Dwo2Exception(Dwo2ExceptionCode.Rest_InternalError, "Not implemented");
    }

    public void registerStudentForSchoolClass(DomNewSchoolClass4Student sc) throws Dwo2Exception {
        SecureStudentSchoolClassManager.registerStudentForSchoolClass(sc);
    }
}
