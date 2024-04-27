package fi.dwo.server.rest;

import javax.ws.rs.HeaderParam;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import fi.dwo.commons.persistence.MySQLPersistenceId;
import fi.dwo.commons.persistence.entities.PersistentClassCourse;
import fi.dwo.commons.persistence.entities.PersistentCourse;
import fi.dwo.commons.persistence.entities.PersistentSchoolClass;
import fi.dwo.commons.persistence.entities.PersistentScoContext;
import fi.dwo.server.PersistentDataManagers.core.CourseManager;
import fi.dwo.server.PersistentDataManagers.core.SchoolClassManager;
import fi.dwo.server.PersistentDataManagers.core.ScoContextManager;
import nl.uu.fi.dwo.rest.dom.entities.DomScoContext;
import nl.uu.fi.dwo.rest.dom.entities.util.CourseType;
import nl.uu.fi.dwo.rest.entities.RestScoContext;
import nl.uu.fi.dwo.rest.entities.RestScormValues;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;

@Path("/secure/student/exam/scoData")
public class SecuredStudentExamScoDataManager extends SecuredCommonScoDataManager {

  @PUT 
  @Produces("application/json")
  @Path("/patchValues")
  public Response patchValues(@Context SecurityContext sc, RestScormValues rest, @HeaderParam("if-match") String match,
                              @HeaderParam("X-ClassCourseID") String ccid, @HeaderParam("X-TOTP") String totp) throws Dwo2Exception {
    verifyTOTP(sc, ccid, totp, courseOf(rest), classOf(rest));
    return super.patchValues(sc, rest, match);
  }

  private PersistentCourse courseOf(RestScormValues rest) throws Dwo2Exception {
    Long id = MySQLPersistenceId.getNativeId(rest.getDomScormValues().getScoContext());
    PersistentScoContext ctx = ScoContextManager.findEntity(id);   
    return CourseManager.findEntity(ctx.getCourseID());
  }

  private PersistentSchoolClass classOf(RestScormValues rest) throws Dwo2Exception {
    Long id = MySQLPersistenceId.getNativeId( rest.getDomScormValues().getSchoolClassID() );
    return SchoolClassManager.findEntity(id);
  }

  @PUT
  @Produces({"application/json"})    
  @Path("/getJSONLaunchDataBytes")
  public String getJSONLaunchDataBytes(@Context SecurityContext sc, RestScoContext rest,
                                       @HeaderParam("X-ClassCourseID") String ccid, @HeaderParam("X-TOTP") String totp) throws Dwo2Exception {
    verifyTOTP(sc, ccid, totp, courseOf(rest), classOf(rest));
    return super.getJSONLaunchDataBytes(sc, rest);
  }

  private PersistentSchoolClass classOf(RestScoContext rest) throws Dwo2Exception {
    Long id = MySQLPersistenceId.getNativeId(rest.getSchoolClassID());
    return SchoolClassManager.findEntity(id);
  }

  private void verifyTOTP(SecurityContext sc, String ccid, String totp, PersistentCourse courseOf, PersistentSchoolClass classOf) throws Dwo2Exception {
    SecuredUserAccountManager.verifyTOTP(sc, ccid, totp, courseOf, classOf);
    
  }

  private PersistentCourse courseOf(RestScoContext rest) throws Dwo2Exception {
    DomScoContext sco = rest.getDomScoContext();
    Long id = MySQLPersistenceId.getNativeId(sco);
    PersistentScoContext ctx = ScoContextManager.findEntity(id);   
    return CourseManager.findEntity(ctx.getCourseID());
  }

  @PUT
  @Produces({"application/json"})
  @Path("/getValues")
  public Response getValues(@Context SecurityContext sc, RestScormValues rest,@HeaderParam("X-ClassCourseID") String ccid, @HeaderParam("X-TOTP") String totp) throws Dwo2Exception {
    verifyTOTP(sc, ccid, totp, courseOf(rest), classOf(rest));
    return super.getValues(sc, rest);
  }

  @PUT
  @Produces({"application/json"})
  @Path("/setValues")
  public Response setValues(@Context SecurityContext sc, RestScormValues rest, @HeaderParam("if-match") EntityTag match,
                            @HeaderParam("X-ClassCourseID") String ccid, @HeaderParam("X-TOTP") String totp) throws Dwo2Exception {
    verifyTOTP(sc, ccid, totp, courseOf(rest), classOf(rest));
    return super.setValues(sc, rest, match);
  }

  	final static Integer EXAM = Integer.valueOf(CourseType.assesment.ordinal());
  	final static Integer KIOSK = Integer.valueOf(CourseType.kiosk.ordinal());

	@Override
	boolean checkType(PersistentClassCourse pcc) {
		Integer type = pcc.getType();
		return EXAM.equals(type) || KIOSK.equals(type);
	}

}
