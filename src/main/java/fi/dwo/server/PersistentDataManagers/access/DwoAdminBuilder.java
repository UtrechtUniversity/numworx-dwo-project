package fi.dwo.server.PersistentDataManagers.access;

import fi.dwo.server.PersistentDataManagers.access.DwoAdminDomainAuthorizer.DwoAdminState_C;
import fi.dwo.server.PersistentDataManagers.access.DwoAdminDomainAuthorizer.DwoAdminState_C_S;
import fi.dwo.server.PersistentDataManagers.access.DwoAdminDomainAuthorizer.DwoAdminState_HR_P_R_S_SG_U;
import fi.dwo.server.PersistentDataManagers.access.DwoAdminDomainAuthorizer.DwoAdminState_HR_R_S_SG_U;
import fi.dwo.server.PersistentDataManagers.core.ClassCourseManager;
import fi.dwo.server.PersistentDataManagers.core.CourseManager;
import fi.dwo.server.PersistentDataManagers.core.DwoProfileManager;
import fi.dwo.server.PersistentDataManagers.core.ScoContextManager;
import nl.uu.fi.dwo.rest.dom.entities.DomCourse;
import nl.uu.fi.dwo.rest.dom.entities.DomCourseFull;
import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfileId;
import nl.uu.fi.dwo.rest.dom.entities.DomScoContextFull;
import nl.uu.fi.dwo.rest.dom.entities.DomScoContextId;
import nl.uu.fi.dwo.rest.dom.entities.DomScoData;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContext;
import nl.uu.fi.dwo.rest.dom.entities.RoleType;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.exceptions.Dwo2ExceptionCode;
import nl.uu.fi.dwo.rest.exceptions.Dwo2RestException;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import fi.dwo.commons.persistence.MySQLPersistenceId;
import fi.dwo.commons.persistence.entities.PersistentClassCourse;
import fi.dwo.commons.persistence.entities.PersistentCourse;
import fi.dwo.commons.persistence.entities.PersistentDwoProfile;
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
  public DomCourseFull update(DomCourseFull course) throws Dwo2Exception {
    return actions.update(instance.getContext(), course);
  }

  @Override
  public Boolean removeCourse() {
    return actions.removeCourse(instance.getContext());
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

}
