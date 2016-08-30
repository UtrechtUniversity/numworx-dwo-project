/**
 * Copyrighted Dec 17, 2015
 */
package fi.dwo.dwojapplet.gui;

import fi.dwo.dwojapplet.domain.rest.SecureSchoolAdminSchoolManager;
import fi.dwo.rest.dom.entities.DomUserFull;
import fi.dwo.rest.dom.entities.ValidUserFieldsChecker;
import fi.dwo.rest.exceptions.Dwo2Exception;
import fi.dwo.rest.exceptions.Dwo2ExceptionCode;
import fi.dwo.rest.exceptions.Dwo2RestException;

import java.util.logging.Logger;

/**
 * Property class for ClassTeacherPanel
 *
 * @author G.A.J. van der Plas
 */
public class NewTeacherSchoolAdminPanelProperties {

    private static final Logger LOG = Logger.getLogger(NewTeacherSchoolAdminPanelProperties.class.getName());
    
//    public NewSingleSchoolStudentsTeacherPanelProperties() {
//
//    }
//
//    public static List<DomSchoolClass> getTeachersSchoolClasses() throws Dwo2Exception {
//        return SecureTeacherSchoolClassManager.getTeachersSchoolClasses();
//    }
//
//    public static Boolean submitSingleSchoolStudent(DomNewSingleSchoolStudent submit) throws Dwo2Exception {
//        return SecureTeacherSchoolClassManager.submitSingleSchoolStudent(submit);
//    }

    public static Boolean IsValidUserDataInput(DomUserFull submit) throws Dwo2Exception {
// from submit new user
    	
    	if (! ValidUserFieldsChecker.isEmptyOrNull(submit.getInsertion()) ) 
    		submit.setInsertion("");
    	
        if ( ! ValidUserFieldsChecker.isEmptyOrNull(submit.getUserName(), submit.getFamilyName(), submit.getGivenName(), submit.getEmail(), submit.getPassword()))
        	return false;
        if (!ValidUserFieldsChecker.isValidEmail(submit.getEmail())) {
            return false;
        }
        if (!ValidUserFieldsChecker.isValidUserName(submit.getUserName())) {
            return false;
        }
        return true;

    }

    static Boolean submitNewTeacher(DomUserFull submit) throws Dwo2Exception {
        return SecureSchoolAdminSchoolManager.submitTeacher(submit);
    }
}
