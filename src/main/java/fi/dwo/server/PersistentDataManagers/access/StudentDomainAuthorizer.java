package fi.dwo.server.PersistentDataManagers.access;

import fi.dwo.commons.persistence.entities.PersistentSchoolClass;
import fi.dwo.server.PersistentDataManagers.actions.MySQLStudentActions;
import fi.dwo.server.PersistentDataManagers.actions.StudentActions;
import java.util.List;
import java.util.logging.Logger;

import javax.ws.rs.core.UriInfo;

import nl.uu.fi.dwo.rest.dom.entities.DomLRS;
import nl.uu.fi.dwo.rest.dom.entities.DomScoContextId;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContext;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContextId;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelData;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelDataScore;
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
public class StudentDomainAuthorizer {

    private static final Logger LOG = Logger.getLogger(StudentDomainAuthorizer.class.getName());
     private Context context;
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

    public static class Context {

        private AnonDomainAuthorizer.AnonPersistentContext anonCtx;
        private UserDomainAuthorizer.UserPersistentContext userCtx;
        private StudentPersistentContext studentCtx;

        public Context(UserDomainAuthorizer.Context ctx) {
            this.anonCtx = ctx.getAnonCtx();
            this.userCtx = ctx.getUserCtx();
            studentCtx = new StudentPersistentContext();
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
         * @return the studentCtx
         */
        protected StudentPersistentContext getStudentCtx() {
            return studentCtx;
        }

        /**
         * @param studentCtx the studentCtx to set
         */
        protected void setStudentCtx(StudentPersistentContext studentCtx) {
            this.studentCtx = studentCtx;
        }

    }

    public static class StudentPersistentContext {

        public PersistentSchoolClass schoolClass;

        protected StudentPersistentContext() {
        }

        protected StudentPersistentContext(StudentPersistentContext ctx) {
        }
    }

    /**
     * Creates a builder and initializes a context if given.
     *
     * @return 
     * @throws nl.uu.fi.dwo.rest.exceptions.Dwo2Exception
     */
    public static StudentState_HR_R_S_SG_U buildStudent() throws Dwo2Exception {
        return new StudentBuilder();
    }

    public interface StudentState_HR_R_S_SG_U  extends StudentDomainAuthorizer.PublicContext{

        public void setStudentModelData(DomStudentModelData data) throws Dwo2Exception;

        public DomStudentModelData getStudentModelData(DomScoContextId domScoId) throws Dwo2Exception;
        public DomStudentModelDataScore getStudentModelDataScore(DomStudentModelContextId domModelId) throws Dwo2Exception;
        public List<DomStudentModelContext> getStudentModelContextList() throws Dwo2Exception;

        public DomLRS getLRS(UriInfo info);

		public List<DomStudentModelContext> getMergedStudentModelContextList() throws Dwo2Exception;

    }

   public interface PublicContext {

        public StudentDomainAuthorizer.Context getContext();

        public void setContext(StudentDomainAuthorizer.Context context);
    }
   
    /**
     * @return the context
     */
    protected StudentDomainAuthorizer.Context getContext() {
        return context;
    }

    /**
     * @param context the context to set
     */
    protected void setContext(StudentDomainAuthorizer.Context context) {
        this.context = context;
    }

}
