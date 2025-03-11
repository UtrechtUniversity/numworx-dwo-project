package fi.dwo.server.PersistentDataManagers.access;

import fi.dwo.server.PersistentDataManagers.access.DwoAdminDomainAuthorizer.Context;
import fi.dwo.server.PersistentDataManagers.access.DwoAdminDomainAuthorizer.DwoAdminState_C;
import fi.dwo.server.PersistentDataManagers.access.DwoAdminDomainAuthorizer.DwoAdminState_C_S;
import fi.dwo.server.PersistentDataManagers.access.DwoAdminDomainAuthorizer.DwoAdminState_HR_P_R_S_SG_U;
import fi.dwo.server.PersistentDataManagers.access.DwoAdminDomainAuthorizer.DwoAdminState_HR_R_S_SG_U;
import fi.dwo.server.PersistentDataManagers.core.CourseManager;
import fi.dwo.server.PersistentDataManagers.core.DwoProfileManager;
import fi.dwo.server.PersistentDataManagers.core.MethodManager;
import fi.dwo.server.PersistentDataManagers.core.ScoContextManager;
import fi.dwo.server.PersistentDataManagers.core.StudentModelContextManager;
import fi.dwo.server.PersistentDataManagers.util.StudentModelContextUtilManager;
import fi.dwo.server.rest.util.Digest;
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
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelStructure;
import nl.uu.fi.dwo.rest.dom.entities.RoleType;
import nl.uu.fi.dwo.rest.dom.entities.util.GensonMapConverter;
import nl.uu.fi.dwo.rest.dom.entities.util.PublishState;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.exceptions.Dwo2ExceptionCode;
import nl.uu.fi.dwo.rest.exceptions.Dwo2RestException;

import java.io.StringReader;
import java.io.StringWriter;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.stream.JsonParser;
import javax.persistence.EntityNotFoundException;
import javax.persistence.OptimisticLockException;
import javax.persistence.RollbackException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response.Status;

import com.owlike.genson.Genson;
import com.owlike.genson.GensonBuilder;
import com.owlike.genson.ext.jaxb.JAXBBundle;

import fi.dwo.commons.persistence.MySQLPersistenceId;
import fi.dwo.commons.persistence.entities.PersistentClassCourse;
import fi.dwo.commons.persistence.entities.PersistentCourse;
import fi.dwo.commons.persistence.entities.PersistentDwoProfile;
import fi.dwo.commons.persistence.entities.PersistentMethod;
import fi.dwo.commons.persistence.entities.PersistentSchool;
import fi.dwo.commons.persistence.entities.PersistentScoContext;
import fi.dwo.commons.persistence.entities.PersistentStudentModelContext;
import fi.dwo.server.PersistentDataManagers.access.UserDomainAuthorizer;
import fi.dwo.server.PersistentDataManagers.actions.DwoAdminActions;
import fi.dwo.server.PersistentDataManagers.actions.MySQLDwoAdminActions;

class DwoAdminBuilder
    implements
      DwoAdminState_HR_R_S_SG_U,
      DwoAdminState_HR_P_R_S_SG_U,
      DwoAdminState_C_S, DwoAdminState_C {
  private static final Logger LOG = Logger.getLogger(DwoAdminBuilder.class.getName());

  private DwoAdminDomainAuthorizer instance;
  private DwoAdminActions actions;

  DwoAdminBuilder() {
    instance = new DwoAdminDomainAuthorizer();
    actions = new MySQLDwoAdminActions();
  }

  public DwoAdminState_HR_R_S_SG_U init(UserDomainAuthorizer.Context ctx) throws Dwo2Exception {
    this.instance.setContext(new DwoAdminDomainAuthorizer.Context(ctx));
    if (ctx.getUserCtx().roleType != null && ctx.getUserCtx().roleType == RoleType.ADMIN) {
      return this;
    } else {
      String msg =
          MessageFormat.format("Username {0}: ILLEGAL USER-OPERATION: It is not in a admin role.",
              new Object[] {ctx.getUserCtx().getUser().getUsername()});
      LOG.log(Level.WARNING, msg);
      throw new Dwo2Exception(Dwo2ExceptionCode.Rest_InternalError, msg);
    }
  }

  @Override
  public DwoAdminState_HR_P_R_S_SG_U addDwoProfile(DomDwoProfileId p) throws Dwo2Exception {
	if (p == null) 
		return this;  
    Long profileId = MySQLPersistenceId.getNativeId(p);
    PersistentDwoProfile profile = DwoProfileManager.findEntity(profileId);
    if (profile == null) {
      String msg =
          MessageFormat.format("Username {0}: ILLEGAL USER-OPERATION: Profile {1} does not exists.",
              new Object[] {instance.getContext().getUserCtx().user.getUsername(), p.getId()});
      LOG.log(Level.WARNING, msg);
      throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, msg);
    }
    this.instance.getContext().getAdminCtx().profile = profile;
    return this;
  }

  @Override
  public DwoAdminState_C_S addScoContext(DomScoContextId s) throws Dwo2Exception {
    if (s == null) {
      String msg =
          MessageFormat.format("Username {0}: ILLEGAL USER-OPERATION: ScoContext not set.",
              new Object[] {instance.getContext().getUserCtx().user.getUsername()});
      LOG.log(Level.WARNING, msg);
      throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, msg);
    }
    // fetch course and class course from sco
    PersistentScoContext sco = ScoContextManager.findEntity(MySQLPersistenceId.getNativeId(s));
    if (sco == null) {
      String msg =
          MessageFormat.format("Username {0}: ILLEGAL USER-OPERATION: ScoContext {1} not found.",
              new Object[] {instance.getContext().getUserCtx().user.getUsername(), s.getId()});
      LOG.log(Level.WARNING, msg);
      throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, msg);
    }
    if (instance.getContext().getAdminCtx().course == null) {
      PersistentCourse c = CourseManager.findEntity(sco.getCourseID());
      if (c == null || !c.getDwoProfileID()
          .equals(instance.getContext().getAdminCtx().profile.getDwoProfileID())) {
        String msg = MessageFormat.format(
            "Username {0}: ILLEGAL USER-OPERATION: Course {1} not found.",
            new Object[] {instance.getContext().getUserCtx().user.getUsername(), c.getCourseID()});
        LOG.log(Level.WARNING, msg);
        throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, msg);
      }
      instance.getContext().getAdminCtx().course = c;
    }
    instance.getContext().getAdminCtx().scoContext = sco;
    return this;
  }

  @Override
  public DomScoContextFull update(DomScoContextFull scoContext, DomScoData scoData,
      Boolean delete) {
    return actions.update(instance.getContext(), scoContext, scoData, delete);
  }

  @Override
  public DwoAdminState_C addCourse(DomCourse c) throws Dwo2Exception {
    Long courseId = MySQLPersistenceId.getNativeId(c);
    PersistentCourse course = CourseManager.findEntity(courseId);
    if (course == null || course.getDwoProfileID().longValue() != instance.getContext().getAdminCtx().profile.getDwoProfileID().longValue()) { // XXX expliciet unboxen
        LOG.log(Level.WARNING, "Username {0}: ILLEGAL USER-OPERATION: Requested course {2} is not available in the profile {1} with usercode {0}.", new Object[]{this.instance.getContext().getUserCtx().user.getUsername(), instance.getContext().getAdminCtx().profile.getDwoProfileID(), c.getId()});
        throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, "Database error using usercode " + this.instance.getContext().getUserCtx().user.getUsername() + ".");
    }
    //verify if course is in NULL school
    if (course.getSchoolID() != null) {
        LOG.log(Level.WARNING, "Username {0}: ILLEGAL USER-OPERATION: Requested course {2} is from a different school that is registered for hasRole in school {1} with usercode {0}.", new Object[]{this.instance.getContext().getUserCtx().user.getUsername(), instance.getContext().getUserCtx().school.getSchoolID(), (course != null) ? course.getSchoolID() : "course==null"});
        throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, "Database error using usercode " + this.instance.getContext().getUserCtx().user.getUsername() + ".");
    }

    this.instance.getContext().getAdminCtx().course = course;
    return this;
  }

  @Override
  public DomScoContextFull add(DomScoContextFull scoContext, DomScoData scoData)
      throws Dwo2Exception {
    return actions.add(instance.getContext(), scoContext, scoData);
  }

  @Override
  public Boolean removeSco() {
    return actions.removeSco(instance.getContext());
  }
  @Override
  public Boolean trashSco() {
    return actions.trashSco(instance.getContext());
  }

  @Override
  public DomCourseFull update(DomCourseFull course) throws Dwo2Exception {
    return actions.update(instance.getContext(), course);
  }

  @Override
  public Boolean removeCourse() {
    return actions.removeCourse(instance.getContext());
  }

  @Override
  public Boolean trashCourse() {
    return actions.trashCourse(instance.getContext());
  }

  @Override
  public DomCourseFull add(DomCourseFull course) throws Dwo2Exception {
    return actions.add(instance.getContext(), course);
  }

  @Override
  public List<DomStudentModelContext> getReducedStudentModels() throws Dwo2Exception {
      List<PersistentStudentModelContext> pModels = actions.getReducedStudentModels(instance.getContext());
      List<DomStudentModelContext> result = new ArrayList<>(pModels.size());
      pModels.forEach((m) -> result.add(m.buildDomStudentModelContext()));
      return result;
  }

  @Override
  public DomStudentModelContext updateStudentModel(DomStudentModelContext model) throws Dwo2Exception {
      try {            
          Long id = MySQLPersistenceId.getNativeId(model);
          PersistentStudentModelContext pModel = StudentModelContextManager.findEntity(id);
          if ( pModel == null) {
            throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, "Illegal operation");
          }
          if ( model.getOptLock() != null && !pModel.getOptlock() .equals (model.getOptLock())) {
            LOG.log(Level.WARNING, "Username {0}: ILLEGAL USER-OPERATION: Requested studentmode {2} is from a different optlock that is registered for hasRole in school {1} with usercode {0}.", new Object[]{this.instance.getContext().getUserCtx().getUser().getUsername(), instance.getContext().getUserCtx().getSchool().getSchoolID(), (pModel != null) ? pModel.getSchoolID() : "model==null"});     
            throw new WebApplicationException(Status.CONFLICT);
          } else 
          if (model.getOptLock() != null) {
            pModel.setOptlock(model.getOptLock());
          }
          pModel.setModelStructure(model.getModelStructure());
//          pModel.setPublishState(model.getPublishState());
//          
//          if (PublishState.overt == model.getPublishState()) {
//          	pModel.setSchoolID(Long.valueOf(0)); // NUL not NULL
//          }
          
          //return instance.teacherActions.updateStudentModel(instance.getContext(), pModel).buildDomStudentModelContext();
          
          return StudentModelContextUtilManager.edit(pModel).buildDomStudentModelContext(); // FIXME netjes maken!
      } catch (RollbackException|OptimisticLockException rb) {
      	LOG.log(Level.SEVERE, "conflict", rb);
          throw new WebApplicationException(Status.CONFLICT);
      } catch (Dwo2Exception e) {
          String msg = MessageFormat.format("Username {0}: Internal error: {1}", new Object[]{instance.getContext().getUserCtx().getUser().getUsername(), e.getMessage()});
          LOG.log(Level.WARNING, msg, e);
          throw new Dwo2RestException(e.getDwo2Code(), msg);
      }
  }

  @Override
  public DomStudentModelContext patchStudentModel(DomStudentModelContextPatch domPatch) throws Dwo2Exception {
  	PersistentStudentModelContext result = getStudentModel(instance.getContext(), domPatch);

  	if (result.getOptlock().equals(domPatch.getOptLock()) && result.getLastChangeTimeStamp()==domPatch.getLastChangeTimeStamp()) {
  		
//  		if (domPatch.getPublishState() != null) {
//  			result.setPublishState(domPatch.getPublishState());
//  			if (domPatch.getPublishState() == PublishState.overt)
//  				result.setSchoolID(Long.valueOf(0));
//  		}
// patch
  		String value = domPatch.getPatch();
  		String digest = domPatch.getDigest();
  		Genson g = new GensonBuilder().withBundle(new JAXBBundle())
  				.withConverters(new GensonMapConverter()).create(); // met de juiste opties
  		String oldValue = g.serialize(result.getModelStructure());
          JsonParser parser = Json.createParser(new StringReader(oldValue));
          parser.next();
          JsonObject oldObject = parser.getObject();
          parser = Json.createParser(new StringReader(value));
          parser.next();
          JsonArray  patch     = parser.getArray();
          JsonObject newObject = Json.createPatch(patch).apply(oldObject);
          if (digest != null) {
            String patched = new Digest().digest(newObject);
            if( !digest.equals(patched)) {
              LOG.severe("patch digest error " + patched + " " + digest);
              throw new WebApplicationException(Status.PRECONDITION_FAILED);
            }
          }
          StringWriter newValue = new StringWriter();
          Json.createWriter(newValue).write(newObject);
          DomStudentModelStructure deserialize = g.deserialize(newValue.toString(), DomStudentModelStructure.class);
  		result.setModelStructure(deserialize);

  		try {
  			DomStudentModelContext context = StudentModelContextUtilManager.edit(result).buildDomStudentModelContext();
  			context.setModelStructure(null);
  			return context;
          } catch (RollbackException|OptimisticLockException|EntityNotFoundException e) {
          	throw new WebApplicationException(Status.CONFLICT);
          }
  	}
  	throw new WebApplicationException(Status.CONFLICT);
  }

  @Override
  public DomStudentModelContext getStudentModel(DomStudentModelContextId id) throws Dwo2Exception {
  	PersistentStudentModelContext result = getStudentModel(instance.getContext(), id);
  	DomStudentModelContext dom = result.buildDomStudentModelContext();
  	dom.setPublishState(PublishState.review);
	return dom;
  }

  
  private PersistentStudentModelContext getStudentModel(Context context, DomStudentModelContextId model) throws Dwo2Exception {
      Long id = MySQLPersistenceId.getNativeId(model);
      PersistentStudentModelContext pModel = StudentModelContextManager.findEntity(id);
      if ( pModel == null) {
        throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, "Illegal operation");
      }
      StudentModelContextUtilManager.merge(pModel);
      return pModel;
  }

  @Override
  public List<DomMethod> getMethods() throws Dwo2Exception {
    PersistentSchool school = new PersistentSchool(0L);
    PersistentDwoProfile profile = instance.getContext().getAdminCtx().profile;    
    List<PersistentMethod> methods = MethodManager.findEntities(school, profile);
    return methods.stream().map(MethodManager::toDom).collect(Collectors.toList());
  }

@Override
public boolean addProfile(DomStudentModelContext domStudentModelContext) throws Dwo2Exception {
	PersistentDwoProfile profile = instance.getContext().getAdminCtx().profile;
	Long id = MySQLPersistenceId.getNativeId(domStudentModelContext);
	PersistentStudentModelContext context = StudentModelContextManager.findEntity(id);
	StudentModelContextManager.addProfile(context, profile);
	return true;
}

@Override
public boolean removeProfile(DomStudentModelContext domStudentModelContext) throws Dwo2Exception {
	PersistentDwoProfile profile = instance.getContext().getAdminCtx().profile;
	Long id = MySQLPersistenceId.getNativeId(domStudentModelContext);
	PersistentStudentModelContext context = StudentModelContextManager.findEntity(id);
	StudentModelContextManager.removeProfile(context, profile);
	return true;
}

@Override
public boolean addProfile(DomMethod domMethod) throws Dwo2Exception {
	PersistentDwoProfile profile = instance.getContext().getAdminCtx().profile;
	String id = domMethod.getId().getIdString();
	PersistentMethod context = MethodManager.findEntity(id);
	MethodManager.addProfile(context, profile);
	return true;
}

@Override
public boolean removeProfile(DomMethod domMethod) throws Dwo2Exception {
	PersistentDwoProfile profile = instance.getContext().getAdminCtx().profile;
	String id = domMethod.getId().getIdString();
	PersistentMethod context = MethodManager.findEntity(id);
	MethodManager.removeProfile(context, profile);
	return true;
}

@Override
public PersistentDwoProfile getDwoProfile() {
	return instance.getContext().getAdminCtx().profile;
}

}
