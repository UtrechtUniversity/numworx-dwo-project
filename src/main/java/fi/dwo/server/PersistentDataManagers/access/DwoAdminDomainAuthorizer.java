package fi.dwo.server.PersistentDataManagers.access;

import java.util.List;
import java.util.logging.Logger;

import fi.dwo.commons.persistence.entities.PersistentCourse;
import fi.dwo.commons.persistence.entities.PersistentDwoProfile;
import fi.dwo.commons.persistence.entities.PersistentScoContext;
import fi.dwo.commons.persistence.entities.PersistentScoData;
import fi.dwo.commons.persistence.entities.PersistentStudentModelContext;
import nl.uu.fi.dwo.rest.dom.entities.DomCourse;
import nl.uu.fi.dwo.rest.dom.entities.DomCourseFull;
import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfileId;
import nl.uu.fi.dwo.rest.dom.entities.DomMethod;
import nl.uu.fi.dwo.rest.dom.entities.DomScoContextFull;
import nl.uu.fi.dwo.rest.dom.entities.DomScoContextId;
import nl.uu.fi.dwo.rest.dom.entities.DomScoData;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContext;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContextId;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContextPatch;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;

public class DwoAdminDomainAuthorizer {

  public interface DwoAdminState_C {

    DomScoContextFull add(DomScoContextFull scoContext, DomScoData scoData) throws Dwo2Exception;
    DomCourseFull update(DomCourseFull course) throws Dwo2Exception;
    Boolean removeCourse();

  }

  public interface DwoAdminState_C_S {
    DomScoContextFull update(DomScoContextFull scoContext, DomScoData scoData, Boolean delete);

    Boolean removeSco();

  }

  public static class DwoAdminPersistentContext {
    public PersistentDwoProfile profile;
    public PersistentScoContext scoContext;
    public PersistentScoData    scoData;
    public PersistentCourse     course;
  }


  private static final Logger LOG = Logger.getLogger(DwoAdminDomainAuthorizer.class.getName());

  public static class Context {

    private AnonDomainAuthorizer.AnonPersistentContext anonCtx;
    private UserDomainAuthorizer.UserPersistentContext userCtx;
    private DwoAdminDomainAuthorizer.DwoAdminPersistentContext adminCtx;
    
    Context(UserDomainAuthorizer.Context ctx) {
      setUserCtx(ctx.getUserCtx());
      anonCtx = ctx.getAnonCtx();
      setAdminCtx(new DwoAdminPersistentContext());
    }

    public DwoAdminDomainAuthorizer.DwoAdminPersistentContext getAdminCtx() {
      return adminCtx;
    }

    public void setAdminCtx(DwoAdminDomainAuthorizer.DwoAdminPersistentContext adminCtx) {
      this.adminCtx = adminCtx;
    }

    public UserDomainAuthorizer.UserPersistentContext getUserCtx() {
      return userCtx;
    }

    public void setUserCtx(UserDomainAuthorizer.UserPersistentContext userCtx) {
      this.userCtx = userCtx;
    }

  }

  public interface DwoAdminState_HR_R_S_SG_U  {
    DwoAdminState_HR_P_R_S_SG_U addDwoProfile(DomDwoProfileId id) throws Dwo2Exception;

	DomStudentModelContext updateStudentModel(DomStudentModelContext domStudentModelContext) throws Dwo2Exception;
	DomStudentModelContext patchStudentModel(DomStudentModelContextPatch domPatch) throws Dwo2Exception;

	DomStudentModelContext getStudentModel(DomStudentModelContextId domStudentModelContext) throws Dwo2Exception;   
  }
 
  public interface DwoAdminState_HR_P_R_S_SG_U {
    DwoAdminState_C_S addScoContext(DomScoContextId scoContext) throws Dwo2Exception;
    DwoAdminState_C   addCourse(DomCourse course) throws Dwo2Exception;
    DomCourseFull     add(DomCourseFull course) throws Dwo2Exception;
	List<DomStudentModelContext> getReducedStudentModels() throws Dwo2Exception;
	List<DomMethod> getMethods() throws Dwo2Exception;
  }
  
  
  private Context context;

  public void setContext(Context context) {
    this.context = context;    
  }

  public Context getContext() {
    return context;
  }

}
