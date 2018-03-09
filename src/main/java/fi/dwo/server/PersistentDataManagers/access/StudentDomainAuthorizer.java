package fi.dwo.server.PersistentDataManagers.access;

import fi.dwo.server.PersistentDataManagers.actions.StudentActions;
import java.util.logging.Logger;
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
    private StudentActions studentActions;

    public class StudentPersistentContext extends UserPersistentContext {
        
        public StudentPersistentContext() {
            
        }

        public StudentPersistentContext(UserPersistentContext ctx) {
            super(ctx);
        }
    }

    public interface StudentState_HR_R_S_SG_U extends UserState_HR_R_S_SG_U {
        StudentState_HR_R_S_SG_U setStudent() throws Dwo2Exception;

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
