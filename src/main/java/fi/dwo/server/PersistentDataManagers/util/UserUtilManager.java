/**
 * Copyrighted Sep 17, 2015
 */
package fi.dwo.server.PersistentDataManagers.util;

import fi.dwo.commons.exceptions.Dwo2Exception;
import fi.dwo.commons.exceptions.Dwo2ExceptionCode;
import fi.dwo.commons.persistence.entities.PersistentSchoolClass;
import fi.dwo.commons.persistence.entities.PersistentStudentOfClass;
import fi.dwo.commons.persistence.entities.PersistentTeacherOfClass;
import fi.dwo.commons.persistence.entities.PersistentUser;
import fi.dwo.server.PersistentDataManagers.core.StudentOfClassManager;
import fi.dwo.server.PersistentDataManagers.core.TeacherOfClassManager;
import fi.dwo.server.PersistentDataManagers.core.UserManager;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Checks if a user is in a certain role context.
 *
 * @author G.A.J. van der Plas
 */
public class UserUtilManager {

    private static final Logger LOG = Logger.getLogger(UserUtilManager.class.getName());

    public static List<PersistentUser> getUsersforStudentsInSchoolClass(PersistentSchoolClass schoolClass) throws Dwo2Exception {
        if (schoolClass == null || schoolClass.getSchoolID()<0) {
            LOG.log(Level.SEVERE, "SchoolClass or school is invalid.");
            throw new Dwo2Exception(Dwo2ExceptionCode.Rest_InternalError, "Illegal parameters.");
        }

        List<PersistentStudentOfClass> socList = StudentOfClassManager.findEntities(schoolClass);
        List<PersistentUser> stdList = new ArrayList<PersistentUser>(socList.size());
        
        for(PersistentStudentOfClass soc : socList){
            PersistentUser u = UserManager.findEntity(soc.getPersistentStudentOfClassPK().getUserID());
            if(u == null) {
            LOG.log(Level.INFO, "Student with userId {0} could not be found.", new Object[]{soc.getPersistentStudentOfClassPK().getUserID()});
        }
            stdList.add(u);
        }
        return  stdList;
    }

    public static List<PersistentUser> getUsersforTeachersInSchoolClass(PersistentSchoolClass schoolClass) throws Dwo2Exception {
        if (schoolClass == null || schoolClass.getSchoolID()<0) {
            LOG.log(Level.SEVERE, "SchoolClass or school is invalid.");
            throw new Dwo2Exception(Dwo2ExceptionCode.Rest_InternalError, "Illegal parameters.");
        }

        List<PersistentTeacherOfClass> socList = TeacherOfClassManager.findEntities(schoolClass);
        List<PersistentUser> stdList = new ArrayList<PersistentUser>(socList.size());
        
        for(PersistentTeacherOfClass soc : socList){
            PersistentUser u = UserManager.findEntity(soc.getPersistentTeacherOfClassPK().getUserID());
            if(u == null) {
            LOG.log(Level.INFO, "Student with userId {0} could not be found.", new Object[]{soc.getPersistentTeacherOfClassPK().getUserID()});
        }
            stdList.add(u);
        }
        return  stdList;
    }
    
}
