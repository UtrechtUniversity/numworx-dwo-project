/**
 * Copyrighted Dec 17, 2015
 */
package fi.dwo.dwojapplet.gui;

import nl.uu.fi.dwo.lms.jclient.lib.rest.managers.SecureSchoolAdminSchoolManager;
import nl.uu.fi.dwo.rest.dom.entities.DomLoginCheck;
import nl.uu.fi.dwo.rest.dom.entities.DomUserFull;
import nl.uu.fi.dwo.rest.dom.entities.ValidUserFieldsChecker;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.exceptions.Dwo2ExceptionCode;
import nl.uu.fi.dwo.rest.exceptions.Dwo2RestException;

import java.util.logging.Logger;

import fi.dwo.commons.system.MD5;
import fi.dwo.dwojapplet.domain.DWO;

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
        if (submit.getUserName() != null
                && !submit.getUserName().equals("")
                && submit.getPassword() != null) {
            return true;
        } else {
            return false;
        }
//    	
//    	if (! ValidUserFieldsChecker.isEmptyOrNull(submit.getInsertion()) ) 
//    		submit.setInsertion("");
//    	
//        if ( ! ValidUserFieldsChecker.isEmptyOrNull(submit.getUserName(), submit.getFamilyName(), submit.getGivenName(), submit.getEmail(), submit.getPassword()))
//        	return false;
//        if (!ValidUserFieldsChecker.isValidEmail(submit.getEmail())) {
//            return false;
//        }
//        if (!ValidUserFieldsChecker.isValidUserName(submit.getUserName())) {
//            return false;
//        }
//        return true;

    }

    // password is MD5 
    static Boolean submitNewTeacher(DomUserFull submit) throws Dwo2Exception {
    	submit.setPassword(MD5.getHashString(submit.getPassword()));
        return SecureSchoolAdminSchoolManager.submitTeacher(submit);
    }

    // password is crypt
    static Boolean submitNewTeacherv2(DomUserFull submit) throws Dwo2Exception {
    	submit.setPassword(DomLoginCheck.crypt(submit.getPassword()));
    	return SecureSchoolAdminSchoolManager.submitTeacherv2(submit, DWO.getDwoProfile());
    }

}
