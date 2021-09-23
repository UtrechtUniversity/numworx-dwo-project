package fi.dwo.server.PersistentDataManagers.access;

import fi.dwo.commons.persistence.entities.PersistentStudentModelContext;
import fi.dwo.server.PersistentDataManagers.access.UserDomainAuthorizer.UserPersistentContext;
import fi.dwo.server.PersistentDataManagers.actions.MySQLSchoolAdminTeacherActions;
import fi.dwo.server.PersistentDataManagers.actions.SchoolAdminTeacherActions;

import java.util.List;
import java.util.logging.Logger;
import nl.uu.fi.dwo.rest.dom.entities.DomScoContextId;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContext;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContextId;
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
public class SchoolAdminTeacherDomainAuthorizer {

    private static final Logger LOG = Logger.getLogger(SchoolAdminTeacherDomainAuthorizer.class.getName());
         private SchoolAdminTeacherDomainAuthorizer.Context context;

    protected SchoolAdminTeacherPersistentContext schoolAdminTeacherCtx;
    protected SchoolAdminTeacherActions schoolAdminTeacherActions = new MySQLSchoolAdminTeacherActions();

    public static class Context {

        private AnonDomainAuthorizer.AnonPersistentContext anonCtx;
        private UserDomainAuthorizer.UserPersistentContext userCtx;
        private SchoolAdminTeacherPersistentContext schooladminTeacherCtx;

        public Context(UserDomainAuthorizer.Context ctx) {
            this.anonCtx = ctx.getAnonCtx();
            this.userCtx = ctx.getUserCtx();
            this.schooladminTeacherCtx = new SchoolAdminTeacherPersistentContext();

        }

        /**
         * @return the anonCtx
         */
        public AnonDomainAuthorizer.AnonPersistentContext getAnonCtx() {
            return anonCtx;
        }

        /**
         * @return the userCtx
         */
        public UserDomainAuthorizer.UserPersistentContext getUserCtx() {
            return userCtx;
        }

        /**
         * @param anonCtx the anonCtx to set
         */
        protected void setAnonCtx(AnonDomainAuthorizer.AnonPersistentContext anonCtx) {
            this.anonCtx = anonCtx;
        }

        /**
         * @param userCtx the userCtx to set
         */
        protected void setUserCtx(UserDomainAuthorizer.UserPersistentContext userCtx) {
            this.userCtx = userCtx;
        }

        /**
         * @return the schooladminTeacherCtx
         */
        protected SchoolAdminTeacherPersistentContext getSchooladminTeacherCtx() {
            return schooladminTeacherCtx;
        }

        /**
         * @param schooladminTeacherCtx the schooladminTeacherCtx to set
         */
        protected void setSchooladminTeacherCtx(SchoolAdminTeacherPersistentContext schooladminTeacherCtx) {
            this.schooladminTeacherCtx = schooladminTeacherCtx;
        }

    }

    public static class SchoolAdminTeacherPersistentContext  {

        public SchoolAdminTeacherPersistentContext() {

        }

        public SchoolAdminTeacherPersistentContext(UserPersistentContext ctx) {
        }
    }

    public interface SchoolAdminTeacherState_HR_R_S_SG_U  {
        //TeacherState_HR_R_S_SG_U setTeacher() throws Dwo2Exception;
        public PersistentStudentModelContext getStudentModel(DomScoContextId ctxId) throws Dwo2Exception;
        public TeacherDomainAuthorizer.TeacherState_HR_R_S_SG_U setTeacher() throws Dwo2Exception;
		public int countStudents(DomScoContextId sco) throws Dwo2Exception;
		public List<DomStudentModelContext> getReducedStudentModels()  throws Dwo2Exception;
		public DomStudentModelContext getStudentModel(DomStudentModelContextId domStudentModelContext)  throws Dwo2Exception;

    }

    protected SchoolAdminTeacherDomainAuthorizer() {
        super();
    }

    /**
     * @return the context
     */
    protected SchoolAdminTeacherDomainAuthorizer.Context getContext() {
        return context;
    }

    /**
     * @param context the context to set
     */
    protected void setContext(SchoolAdminTeacherDomainAuthorizer.Context context) {
        this.context = context;
    }

}
