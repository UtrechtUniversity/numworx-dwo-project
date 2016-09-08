/**
 * Copyrighted Sep 17, 2015
 */
package fi.dwo.server.PersistentDataManagers.util;

import fi.dwo.commons.persistence.entities.PersistentSchool;
import fi.dwo.rest.exceptions.Dwo2Exception;
import fi.dwo.rest.exceptions.Dwo2ExceptionCode;
import fi.dwo.commons.persistence.entities.PersistentSchoolClass;
import fi.dwo.commons.persistence.entities.PersistentSchoolGroup;
import fi.dwo.commons.persistence.entities.PersistentStudentOfClass;
import fi.dwo.commons.persistence.entities.PersistentTeacherOfClass;
import fi.dwo.commons.persistence.entities.PersistentUser;
import fi.dwo.rest.dom.entities.RoleType;
import fi.dwo.rest.exceptions.Dwo2RestException;
import fi.dwo.server.PersistentDataManagers.core.SchoolGroupManager;
import fi.dwo.server.PersistentDataManagers.core.StudentOfClassManager;
import fi.dwo.server.PersistentDataManagers.core.TeacherOfClassManager;
import fi.dwo.server.PersistentDataManagers.core.UserManager;
import fi.dwo.server.persistence.DwoEmfFactory;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;

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
    
    /**
     * untested function
     * 
     * 
     * @param school
     * @param role
     * @return
     * @throws Dwo2Exception 
     */
        public static List<PersistentUser> getUsersInSchoolwithRole(PersistentSchool school, RoleType role) throws Dwo2Exception {
        EntityManager em = DwoEmfFactory.getEntityManager();
        if (school == null || role==null) {
            LOG.log(Level.SEVERE, "user or school is invalid.");
            throw new Dwo2Exception(Dwo2ExceptionCode.Rest_InternalError, "Illegal parameters.");
        }
        PersistentSchoolGroup sg = SchoolGroupManager.findBySchoolAndRole(school, role);
        try {
            //           school = SchoolManager.findBySchoolLogin(newUserReg.getSchoolLogin());
            javax.persistence.Query q = em.createQuery(" select u from PersistentUser u join PersistentHasRole h on h.persistentHasRolePK.userID=u.userID where hr.schoolGroupID = :schoolGroupId");
            q.setParameter("schoolGroupId", sg.getSchoolGroupID());
            List<PersistentUser> userList =  q.getResultList();
            LOG.log(Level.FINE, "School-manager retrieved {0} users in school {1} with role {2}.", new Object[]{userList.size(), school.getSchoolID(), role.name()});
            return userList;
        }
        catch (Exception ex) {
            String msg = MessageFormat.format("Failed retrieving users in school {0} with role {1}.", new Object[]{school.getSchoolID(), role.name()});
            LOG.log(Level.WARNING, msg, ex);
            throw new Dwo2RestException(Dwo2ExceptionCode.Rest_Registration_School_authentication_failed, msg);
        }
        finally {
            em.close();
        }
    }

}
