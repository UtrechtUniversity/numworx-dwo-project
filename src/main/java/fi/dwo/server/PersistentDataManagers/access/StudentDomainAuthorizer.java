package fi.dwo.server.PersistentDataManagers.access;

import fi.dwo.server.PersistentDataManagers.actions.MySQLStudentActions;
import fi.dwo.server.PersistentDataManagers.actions.StudentActions;
import java.util.logging.Logger;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelData;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;

/**
 * Builder to retrieve persistence data in a cascading way an verify access and
 * dynamic model rules. This builder is fluid builder. Technically the class
 * forms a state machine where the interfaces denote the possible transitions
 * (edges in a directed graph). Thus a regular language for the security access
 * can be built.
 *
 * @author G.A.J. van der Plas
 */
public class StudentDomainAuthorizer extends UserDomainAuthorizer {

    private static final Logger LOG = Logger.getLogger(StudentDomainAuthorizer.class.getName());
    protected StudentPersistentContext studentCtx;
    private StudentActions studentActions = new MySQLStudentActions();

    /**
     * @return the studentActions
     */
    protected StudentActions getStudentActions() {
        return studentActions;
    }

    /**
     * @param studentActions the studentActions to set
     */
    protected void setStudentActions(StudentActions studentActions) {
        this.studentActions = studentActions;
    }

    public class StudentPersistentContext extends UserPersistentContext {
        
        public StudentPersistentContext() {
            
        }

        public StudentPersistentContext(UserPersistentContext ctx) {
            super(ctx);
        }
    }
    /** Creates a builder and initializes a context if given. */    
    public static StudentState_HR_R_S_SG_U buildStudent() throws Dwo2Exception {
        return new StudentBuilder();
    }

    public interface StudentState_HR_R_S_SG_U extends UserState_HR_R_S_SG_U {
        public void updateStudentModelData(DomStudentModelData data) throws Dwo2Exception;

    }
//
//    protected StudentDomainAuthorizer() {
//        super();
//    }
//
//    protected StudentDomainAuthorizer(UserDomainAuthorizer userAuth) {
//        super();
//        studentCtx = new StudentPersistentContext(userAuth.userCtx);
//        //schoolAdminTeacherActions = new SchoolAdminTeacherActions();
//    }

    public interface Build {
//
//        PersistentHasRole getHasRole();
//
//        PersistentUser getUser();
//
//        PersistentSchool getSchool();
//
//        RoleType getRoleType();
//        

    }

}
