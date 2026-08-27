package fi.dwo.server.rest;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import fi.dwo.commons.persistence.MySQLPersistenceId;
import fi.dwo.commons.persistence.entities.PersistentClassCourse;
import fi.dwo.commons.persistence.entities.PersistentCourse;
import fi.dwo.commons.persistence.entities.PersistentHasRole;
import fi.dwo.commons.persistence.entities.PersistentHasRolePK;
import fi.dwo.commons.persistence.entities.PersistentSchool;
import fi.dwo.commons.persistence.entities.PersistentSchoolClass;
import fi.dwo.commons.persistence.entities.PersistentScoContext;
import fi.dwo.commons.persistence.entities.PersistentStudentOfClass;
import fi.dwo.commons.persistence.entities.PersistentStudentOfClassPK;
import fi.dwo.commons.persistence.entities.PersistentUser;
import fi.dwo.server.PersistentDataManagers.access.AnonDomainAuthorizer;
import fi.dwo.server.PersistentDataManagers.access.CascadingPersistenceBuilder;
import fi.dwo.server.PersistentDataManagers.access.CascadingPersistenceBuilder.State_C_CC_HR_P_R_S_SC_SCO_SG_U;
import fi.dwo.server.PersistentDataManagers.access.CascadingPersistenceBuilder.State_C_CC_HR_P_R_S_SC_SG_U;
import fi.dwo.server.PersistentDataManagers.access.StudentDomainAuthorizer.StudentState_HR_R_S_SG_U;
import fi.dwo.server.PersistentDataManagers.access.UserDomainAuthorizer.UserState_HR_R_S_SG_U;
import fi.dwo.server.PersistentDataManagers.access.UserDomainAuthorizer.UserState_U;
import fi.dwo.server.PersistentDataManagers.core.ClassCourseManager;
import fi.dwo.server.PersistentDataManagers.core.CourseManager;
import fi.dwo.server.PersistentDataManagers.core.HasRoleManager;
import fi.dwo.server.PersistentDataManagers.core.SchoolClassManager;
import fi.dwo.server.PersistentDataManagers.core.ScoContextManager;
import fi.dwo.server.PersistentDataManagers.core.StudentOfClassManager;
import fi.dwo.server.PersistentDataManagers.core.UserManager;
import fi.dwo.server.PersistentDataManagers.util.HasRoleUtilManager;
import fi.dwo.server.rest.jaxrsfilters.DwoUserPrincipal;
import fi.dwo.server.rest.util.CourseBuilder;
import fi.dwo.server.rest.util.Origin;
import fi.dwo.server.rest.util.SchoolyearUtilManager;
import fi.servlet.dwomaccess.Subnet;
import nl.numworx.schoolyear.jclient.SchoolyearClient;
import nl.numworx.schoolyear.jclient.dto.Content;
import nl.numworx.schoolyear.jclient.dto.Element;
import nl.numworx.schoolyear.jclient.dto.ElementId;
import nl.numworx.schoolyear.jclient.dto.ExamDTO;
import nl.numworx.schoolyear.jclient.dto.User;
import nl.numworx.schoolyear.jclient.dto.UserDTO;
import nl.numworx.schoolyear.jclient.dto.Vault;
import nl.numworx.schoolyear.jclient.dto.WebPageUrl;
import nl.uu.fi.dwo.rest.dom.entities.DomClassCourse;
import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.dom.entities.DomCourse;
import nl.uu.fi.dwo.rest.dom.entities.DomCourseStudent;
import nl.uu.fi.dwo.rest.dom.entities.DomCoursesOfSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfileId;
import nl.uu.fi.dwo.rest.dom.entities.DomHasRole;
import nl.uu.fi.dwo.rest.dom.entities.DomMapEntry;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClassAndProfile;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClassId;
import nl.uu.fi.dwo.rest.dom.entities.DomScoContext;
import nl.uu.fi.dwo.rest.dom.entities.DomScoContextId;
import nl.uu.fi.dwo.rest.dom.entities.RoleType;
import nl.uu.fi.dwo.rest.dom.entities.util.AboType;
import nl.uu.fi.dwo.rest.dom.entities.util.CourseType;
import nl.uu.fi.dwo.rest.dom.entities.util.ViewState;
import nl.uu.fi.dwo.rest.entities.RestClassCourse;
import nl.uu.fi.dwo.rest.entities.RestCourse;
import nl.uu.fi.dwo.rest.entities.RestSchoolClass;
import nl.uu.fi.dwo.rest.entities.RestSchoolClassAndProfile;
import nl.uu.fi.dwo.rest.entities.RestScoContext;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.exceptions.Dwo2ExceptionCode;
import nl.uu.fi.dwo.rest.exceptions.Dwo2RestException;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;

@Path("/secure/student/coursesofschoolclass")
public class SecuredStudentCoursesOfSchoolClassManager {
  final static Integer EXAM = Integer.valueOf(1);
  final static Integer KIOSK = Integer.valueOf(CourseType.kiosk.ordinal());
  private static final String PUBLIC_COURSE_GET_IMAGE = "../../../public/course/getImage";

  private static final Logger LOG =
      Logger.getLogger(SecuredStudentCoursesOfSchoolClassManager.class.getName());
  // private static final Object INVISIBLE = Integer.valueOf(2);

  @PUT
  @Produces({"application/json"})
  @Path("/get")
  public DomCoursesOfSchoolClass get(@Context SecurityContext sc, RestSchoolClassAndProfile rest, @Context HttpServletRequest request)
      throws Dwo2Exception {
    // verify user is student of class
    PersistentHasRole phr = null;
    PersistentHasRolePK phrPK =
        MySQLPersistenceId.getNativeId(rest.getRestContext().getDomHasRole());
    PersistentSchool school = null;
    PersistentSchoolClass schoolClass = null;
    UserState_HR_R_S_SG_U hstate = AnonDomainAuthorizer.build().submitUser(sc).setHasRole(rest.getRestContext().getDomHasRole());
    // check if user has matching hasRole
    try {
      PersistentUser u = hstate.getUser();
      if (!u.getId().equals(phrPK.getUserID())) {
        throw new Dwo2Exception();
      }
      phr = hstate.getHasRole();
      school = hstate.getSchool();
    } catch (Dwo2Exception ex) {
      LOG.log(Level.WARNING,
          "Username {0}: ILLEGAL USER-OPERATION: Trying to access student functionality by user with usercode {0}.",
          new Object[] {sc.getUserPrincipal().getName()});
      throw new Dwo2RestException(Dwo2ExceptionCode.User_IllegalAction,
          "You Don't Have Permission to access this using usercode "
              + sc.getUserPrincipal().getName() + ".");
    }
    DomSchoolClassAndProfile dom = rest.getDomSchoolClassAndProfile();
    // fetch schoolclass from parameter
    Long classID = MySQLPersistenceId.getNativeId(dom.getDomSchoolClass());
    schoolClass = SchoolClassManager.findEntity(classID);

    // verify if user is in class
    PersistentStudentOfClassPK key = new PersistentStudentOfClassPK();
    key.setClassID(schoolClass.getClassID());
    key.setSchoolGroupID(phr.getPersistentHasRolePK().getSchoolGroupID());
    key.setUserID(phr.getPersistentHasRolePK().getUserID());
    PersistentStudentOfClass soc = StudentOfClassManager.findEntity(key);
    if (soc == null) {
      return null;
    }

    // verify if schoolClass is in school
    if (schoolClass == null || !schoolClass.getSchoolID().equals(school.getSchoolID())) {
      LOG.log(Level.WARNING,
          "Username {0}: ILLEGAL USER-OPERATION: Active schoolClass {2} from a different school that registered for hasRole in school {1} with usercode {0}.",
          new Object[] {sc.getUserPrincipal().getName(), school.getSchoolID(),
              schoolClass.getClassID()});
      throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError,
          "Database error using usercode " + sc.getUserPrincipal().getName() + ".");
    }

    String IPRANGE = System.getProperty("ENV_IPRANGE", "");
    String host = request.getRemoteAddr();
    boolean inrange = Subnet.netMatchRange(IPRANGE, host);

    // end verification
    DomCoursesOfSchoolClass result = new DomCoursesOfSchoolClass();

    Long profileID = MySQLPersistenceId.getNativeId(dom.getDomDwoProfile());

    // List<PersistentClassCourse> listClassCourse = ClassCourseManager.findEntities(schoolClass);
    List<PersistentClassCourse> listClassCourse =
        ClassCourseManager.findVisibleEntities(schoolClass, ViewState.studentsAndTeachers, profileID);
    List<PersistentClassCourse> l2 = ClassCourseManager.findVisibleEntities(schoolClass, ViewState.onlyStudents, profileID);
    List<PersistentClassCourse> l3 = ClassCourseManager.findVisibleEntities(schoolClass, ViewState.studentsOrTeachers, profileID);
    listClassCourse.addAll(l2);
    listClassCourse.addAll(l3);
    
    Map<PersistenceId, DomClassCourse> classCourseMap = new HashMap<>();
    Map<PersistenceId, DomCourseStudent> courseMap = new HashMap<>();
    Date NOW = new Date();
    URI uri = URI.create(request.getRequestURL().toString());
    String pfx = uri.resolve(PUBLIC_COURSE_GET_IMAGE).toString();
    CourseBuilder cb = new CourseBuilder(pfx, rest.getRestContext().getDomHasRole(),false);
   listClassCourse.stream().forEach((scc) -> {
      // if (scc.getViewState().equals(ViewState.invisible))
      // return;
      // FIXME after and before
      if (scc.getNotAfter() != null) {
        if (NOW.after(scc.getNotAfter())) return;
      }
      if (scc.getNotBefore() != null) {
        if (NOW.before(scc.getNotBefore())) return;
      }
      if (inExam(scc) && !inrange) return;
      
      Long courseID = scc.getCourseID();
      PersistentCourse course = CourseManager.findEntity(courseID);
      if (course == null) {
        LOG.log(Level.SEVERE,
            "course null for courseid = " + courseID + " sccid = " + scc.getClassCourseID());
      } else {
        if (course.getTrashID() == 0L && profileID.equals(course.getDwoProfileID())) {
          DomClassCourse dcc = scc.buildDomClassCourse();
          classCourseMap.put(dcc.getId(), dcc);
          DomCourseStudent dcs = cb.apply(course);
          courseMap.put(dcs.getId(), dcs);
        }
      }
    });

    result.setSchoolClass(schoolClass.buildDomSchoolClass());
    result.setClassCourses(classCourseMap.entrySet().stream()
        .map((e) -> new DomMapEntry<PersistenceId, DomClassCourse>(e))
        .collect(Collectors.toList()));
    result.setCourses(courseMap.entrySet().stream()
        .map((e) -> new DomMapEntry<PersistenceId, DomCourseStudent>(e))
        .collect(Collectors.toList()));
    result.setFetchTimeStamp(Long.valueOf(NOW.getTime()));
    return result;

  }

  // FIXME zie ook SecuredStudentExamScoDataManager: duplicate code.
  private boolean inExam(PersistentClassCourse scc) {
	Integer type = scc.getType();
	return EXAM.equals(type) || KIOSK.equals(type);
  }

  @PUT
  @Path("/getCourse")
  public DomCoursesOfSchoolClass getCourse(@Context SecurityContext sc, RestCourse rest, @Context HttpServletRequest request) {
    try {
      DomCourse courseid = rest.getDomCourse();
      DomSchoolClassId classid = rest.getSchoolClassID();
      DomDwoProfileId profileid = rest.getDomDwoProfile();
      DomHasRole hr = rest.getRestContext().getDomHasRole();
      // security
      String username = sc.getUserPrincipal().getName();
      State_C_CC_HR_P_R_S_SC_SG_U s =
          CascadingPersistenceBuilder.user(username).addHasRoleIfType(hr, RoleType.STUDENT)
              .addSchoolClass(classid).addProfile(profileid).addCourse(courseid);
      // s contains all data.
      PersistentClassCourse pcc = s.getClassCourse();
      
      
      PersistentCourse pc = s.getCourse();
      PersistentSchoolClass psc = s.getSchoolClass();
      
// FIXME StudentState_HR_R_S_SG_U state = AnonDomainAuthorizer.build().submitUser(sc).setHasRole(hr).buildStudent().setDwoProfile(profileid).setSchoolClass(classid);
     
      
      
      
// FIXME security logic: public courses are ALWAYS accessible. Move to State?
      DomCoursesOfSchoolClass result = getCourseForStudent(pcc, pc, psc, s.getSchool(), request,hr);
      return result;
    } catch (Dwo2Exception e) {
      throw new Dwo2RestException(e);
    }
  }

  private DomCoursesOfSchoolClass getCourseForStudent(PersistentClassCourse pcc,
      PersistentCourse pc, PersistentSchoolClass psc, PersistentSchool school, HttpServletRequest request, DomHasRole hr) {
    Date NOW = new Date();
    DomCoursesOfSchoolClass result = new DomCoursesOfSchoolClass();
    if(pcc == null && pc.getSchoolID() == null) {
      pcc = new PersistentClassCourse();
      pcc.setClassCourseID(0);
      pcc.setAccessKey(null);
      pcc.setClassID(psc.getClassID());
      pcc.setCourseID(pc.getCourseID());
      pcc.setLastChangeTimeStamp(NOW.getTime());
      pcc.setType(0);
      pcc.setViewState(ViewState.none);
      if (psc != null && pc.getParentID() != 0) {
    	  PersistentCourse parent = CourseManager.findEntity(pc.getParentID());
    	  if (parent.isNotVisible()) {
    		  pcc.setDwoProfileID(pc.getDwoProfileID());
    		  ClassCourseManager.create(pcc);
    	  }
      }   
    } else if (pcc != null && pc.getSchoolID() == null && pcc.getViewState() == ViewState.invisible && school.getAboType() == AboType.premium) {
  	  long parentpid = pc.getParentID();
  	  if (parentpid != 0L) {
  		  PersistentCourse parentcourse = CourseManager.findEntity(parentpid);
  		  if (parentcourse.isNotVisible()) {
  			  pcc.setViewState(ViewState.students);
  			  pcc = ClassCourseManager.edit(pcc);
  		  }
  	  }
    }

    if (pcc != null && pcc.getNotAfter() != null) {
      if (NOW.after(pcc.getNotAfter())) pcc = null;
    }
    if (pcc != null && pcc.getNotBefore() != null) {
      if (NOW.before(pcc.getNotBefore())) pcc = null;
    }
// FIXME Hier over nadenken
//    if (pcc != null 
//    		&& (pcc.getViewState() != ViewState.students)
//    		&& (pcc.getViewState() != ViewState.studentsAndTeachers)
//    ) pcc = null;

    if (pcc == null) {
      result.setClassCourses(Collections.emptyList());
      result.setCourses(Collections.emptyList());
      result.setScoContexts(Collections.emptyList());
    } else {
    	if (!Boolean.TRUE.equals(pcc.hasResults()))
    	{
    		// hier NPE als pcc niet persistent is.
    		if (pcc.getClassCourseID() != 0)
    			pcc = ClassCourseManager.editResults(pcc.getClassCourseID(), Boolean.TRUE);
    		else { // niet persistent 
    			pcc.setResults(Boolean.TRUE);
    		}
    	}  	
      DomClassCourse dcc = pcc.buildDomClassCourse();
      URI uri = URI.create(request.getRequestURL().toString());
      String pfx = uri.resolve(PUBLIC_COURSE_GET_IMAGE).toString();
      CourseBuilder cb = new CourseBuilder(pfx, hr,false);
	  DomCourseStudent dcs = cb.apply(pc);    		  
      DomMapEntry<PersistenceId, DomClassCourse> ecc =
          new DomMapEntry<PersistenceId, DomClassCourse>(dcc.getId(), dcc);
      DomMapEntry<PersistenceId, DomCourseStudent> ecs =
          new DomMapEntry<PersistenceId, DomCourseStudent>(dcs.getId(), dcs);
      result.setClassCourses(Collections.singletonList(ecc));
      result.setCourses(Collections.singletonList(ecs));
      if (!inExam(pcc)) { // no sco's for exams
        // fetch studentScoContexts
        List<PersistentScoContext> list = ScoContextManager.findEntities(pc);
        List<DomMapEntry<PersistenceId, DomScoContext>> scos;
  // FIXME NO icons yet!
        scos = list.stream().map(p -> p.buildDomScoContext()).sorted(new DomScoContextComparator()).map(p -> new DomMapEntry<>(p.getId(), p)).collect(Collectors.toList());
        result.setScoContexts(scos);       
      }
    }

    result.setSchoolClass(psc.buildDomSchoolClass());
    result.setFetchTimeStamp(Long.valueOf(NOW.getTime()));
    return result;
  }

  @PUT
  @Path("/getScoContext")
  public DomCoursesOfSchoolClass getScoContext(@Context SecurityContext sc, RestScoContext rest) {
    DomCoursesOfSchoolClass result = new DomCoursesOfSchoolClass();
    try {
      DomHasRole hr = rest.getRestContext().getDomHasRole();
      DomSchoolClassId dsc = rest.getSchoolClassID();
      DomDwoProfileId profile = rest.getDomDwoProfile();
      DomScoContextId scoid = rest.getDomScoContext();
      String username = sc.getUserPrincipal().getName();
      State_C_CC_HR_P_R_S_SC_SCO_SG_U s =
          CascadingPersistenceBuilder.user(username).addHasRoleIfType(hr, RoleType.STUDENT)
              .addSchoolClass(dsc).addProfile(profile).addScoContext(scoid);
      // s contains all data.
      Date NOW = new Date();
      PersistentClassCourse pcc = s.getClassCourse();
      PersistentCourse pc = s.getCourse();
      PersistentSchoolClass psc = s.getSchoolClass();
      PersistentScoContext psco = s.getScoContext();
// FIXME security logic: public courses are ALWAYS accessible. Move to State?
      if(pcc == null && pc.getSchoolID() == null) {
    	  pcc = new PersistentClassCourse();
    	  pcc.setClassCourseID(0);
    	  pcc.setAccessKey(null);
    	  pcc.setClassID(psc.getClassID());
    	  pcc.setCourseID(pc.getCourseID());
    	  pcc.setLastChangeTimeStamp(NOW.getTime());
    	  pcc.setType(0);
    	  pcc.setViewState(ViewState.none);
    	  long parentpid = pc.getParentID();
    	  if (parentpid != 0 && s.getSchool().getAboType() == AboType.premium) {
    		  PersistentCourse parentcourse = CourseManager.findEntity(parentpid);
    		  if (parentcourse.isNotVisible()) {
    			  pcc.setDwoProfileID(s.getDwoProfile().getDwoProfileID());
    			  ClassCourseManager.create(pcc);
    		  }
    	  }
      } 
      // else if invisible en eigenlijk als boven, dan edit to ViewState.students
      else if (pcc != null && pc.getSchoolID() == null && pcc.getViewState() == ViewState.invisible && s.getSchool().getAboType() == AboType.premium) {
    	  long parentpid = pc.getParentID();
    	  if (parentpid != 0L) {
    		  PersistentCourse parentcourse = CourseManager.findEntity(parentpid);
    		  if (parentcourse.isNotVisible()) {
    			  pcc.setViewState(ViewState.students);
    			  pcc = ClassCourseManager.edit(pcc);
    		  }
    	  }
      }

      if (pcc != null && pcc.getNotAfter() != null) {
        if (NOW.after(pcc.getNotAfter())) pcc = null;
      }
      if (pcc != null && pcc.getNotBefore() != null) {
        if (NOW.before(pcc.getNotBefore())) pcc = null;
      }
// FIXME nog over denken: 
      
      //if (pcc != null && (pcc.getViewState() != ViewState.studentsAndTeachers) && pcc.getViewState() != ViewState.students) pcc = null;
      if (pcc != null && inExam(pcc)) pcc = null; // No deeplink for exams

      if (pcc == null) {
        result.setClassCourses(Collections.emptyList());
        result.setCourses(Collections.emptyList());
        result.setScoContexts(Collections.emptyList());
      } else {
    	if (! Boolean.TRUE.equals(pcc.hasResults())) { pcc = ClassCourseManager.editResults(pcc.getClassCourseID(), Boolean.TRUE); }
    	  
        DomClassCourse dcc = pcc.buildDomClassCourse();
        dcc.setType(pcc.getType()); // geen fratsen voor een student!
        DomCourseStudent dcs = pc.buildDomCourseStudent();
        DomScoContext dsco = psco.buildDomScoContext();
        DomMapEntry<PersistenceId, DomClassCourse> ecc =
            new DomMapEntry<>(dcc.getId(), dcc);
        DomMapEntry<PersistenceId, DomCourseStudent> ecs =
            new DomMapEntry<>(dcs.getId(), dcs);
        result.setClassCourses(Collections.singletonList(ecc));
        result.setCourses(Collections.singletonList(ecs));
        DomMapEntry<PersistenceId, DomScoContext> scocontext =
            new DomMapEntry<>(dsco.getId(), dsco);
        result.setScoContexts(Collections.singletonList(scocontext));
      }

      result.setSchoolClass(psc.buildDomSchoolClass());
      result.setFetchTimeStamp(Long.valueOf(NOW.getTime()));
      return result;
    } catch (Dwo2Exception e) {
      throw new Dwo2RestException(e);
    }
  }

  @PUT
  @Path("getClassCourse")
  public DomCoursesOfSchoolClass getClassCourse(@Context SecurityContext sc, RestClassCourse rest, @Context HttpServletRequest request) throws Dwo2Exception {
    UserState_HR_R_S_SG_U state = AnonDomainAuthorizer.build().submitUser(sc)
      .setHasRole(rest.getRestContext().getDomHasRole());
      state.buildStudent();
    Long pid = MySQLPersistenceId.getNativeId(rest.getDomDwoProfile());
    Long ccid = MySQLPersistenceId.getNativeId(rest.getDomClassCourse());
    PersistentClassCourse cc = ClassCourseManager.findEntity(ccid);
    if (cc == null) {
      LOG.log(Level.WARNING, "classcourse not found  " + ccid);
      throw new Dwo2Exception(Dwo2ExceptionCode.Client_InternalError, "not found");
    }
    PersistentCourse course = CourseManager.findEntity(cc.getCourseID());
    PersistentSchoolClass schoolclass = SchoolClassManager.findEntity(cc.getClassID());
    PersistentStudentOfClass okay = StudentOfClassManager.findEntity(new PersistentStudentOfClassPK(state.getUser().getId(), schoolclass.getClassID(), state.getSchoolGroup().getSchoolGroupID()));
    if (okay == null || !course.getDwoProfileID().equals(pid)) {
      throw new Dwo2Exception(Dwo2ExceptionCode.User_AuthorizationError, "not authorized");
    }
      return getCourseForStudent(cc, course, schoolclass,state.getSchool(), request, rest.getRestContext().getDomHasRole());
  }

  @GET
  @Path("getURL")
  @RolesAllowed({"STUDENT"})
  public Response getURL(@Context SecurityContext sc, @QueryParam("id") String pid, @QueryParam("base") String base, @QueryParam("locale") String locale) throws Dwo2Exception, IOException {
	  UserState_U state = AnonDomainAuthorizer.build().submitUser(sc);
	  DwoUserPrincipal principal = (DwoUserPrincipal) sc.getUserPrincipal();
	  UserState_HR_R_S_SG_U hrstate = state.setHasRoleIfType(principal.getHr().buildDomHasRole(), RoleType.STUDENT);
	  PersistentUser user = state.getUser();
	  DomClassCourse cc = new DomClassCourse();
	  cc.setId(new PersistenceId(pid));
	  Long id = MySQLPersistenceId.getNativeId(cc);
	  PersistentClassCourse pcc = ClassCourseManager.findEntity(id);
	  String url = base + "exam/?id=" +id;
	  String origin = Origin.ORIGINS[0];
	  SecuredUserAccountManager account = new SecuredUserAccountManager();
	  if (pcc.getType() == CourseType.kiosk.ordinal()) {
		  SchoolyearClient client = SchoolyearUtilManager.build(hrstate.getSchool());
		  ExamDTO exam = new ExamDTO();
		  exam.id = pcc.getSyExamID();
		  UserDTO u = new UserDTO();
		  u.federated_user_id = user.getId().toString();
		  u.personal_information = new User();
		  u.personal_information.email = user.getEmail();
		  u.personal_information.first_name = user.getGivenName();
		  u.personal_information.org_code = withoutRealm(user.getUsername());
		  
		  String insertion = Objects.toString( user.getInsertion(), ""); // null!
		  u.personal_information.last_name = (insertion + " " + user.getLastname()).trim();
		  
		  u.vault = new Vault();
		  u.vault.content = new Content();
		  String uuid = UUID.randomUUID().toString();
		  WebPageUrl wpu = new WebPageUrl();
		  RestSchoolClass rest = new RestSchoolClass();
		  rest.setDomSchoolClass(new DomSchoolClass());
		  rest.setRestContext(new DomContext());
		  rest.getRestContext().setDomHasRole(principal.getHr().buildDomHasRole());
		  rest.getDomSchoolClass().setId(PersistentSchoolClass.buildPersistenceId(pcc.getClassID()));
		  String bearer = account.getBearerToken(sc, rest);
		  bearer = bearer.replace("\"", "");
		  url = origin + base + "exam/toets.jsp?id=" +id;;
		  if (locale != null) {
			  url = url += "&locale=" + URLEncoder.encode(locale);
		  }
		  wpu.url = url + "&a=" + URLEncoder.encode(bearer);
		  
		  Element element = new Element();
		  element.url = wpu;
		  element.type = WebPageUrl.TYPE;
		  element.origin = "api_key";
		  Map <String, Element> elements = new HashMap<>();
		  u.vault.content.elements = elements; elements.put(uuid, element);
		  u.vault.content.entry_points = Collections.singletonList(new ElementId(uuid));
		  url = origin + base + "exam/logout.html";
		  wpu = new WebPageUrl();
		  wpu.url = url;
		  element = new Element();
		  element.url = wpu;
		  element.type = WebPageUrl.TYPE;
		  element.origin = "api_key";
		  uuid = UUID.randomUUID().toString();
		  elements.put(uuid, element);
		  u.vault.content.exit_points = Collections.singletonList(new ElementId(uuid));
		  //url = wpu.url;
		  url = client.createWorkspace(exam, u).onboarding_url;
	  } else if (pcc.getType() == CourseType.assesment.ordinal()) {
		  RestSchoolClass rest = new RestSchoolClass();
		  rest.setDomSchoolClass(new DomSchoolClass());
		  rest.setRestContext(new DomContext());
		  rest.getRestContext().setDomHasRole(principal.getHr().buildDomHasRole());
		  rest.getDomSchoolClass().setId(PersistentSchoolClass.buildPersistenceId(pcc.getClassID()));
		  String bearer = account.getBearerToken(sc, rest);
		  bearer = bearer.replace("\"", "");
		  url = url + "&a=" + URLEncoder.encode(bearer);
	  }
	  Map<String,String> entity = Collections.singletonMap("url", url);
	  return Response.ok().type(MediaType.APPLICATION_JSON_TYPE).entity(entity).build();
  }

private String withoutRealm(String username) {
	int index = username.indexOf('@');
	if (index > 0) return username.substring(0, index);
	return username;
}
  
}
