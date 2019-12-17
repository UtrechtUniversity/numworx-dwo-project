package fi.dwo.server.rest;

import javax.ws.rs.HeaderParam;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import fi.dwo.commons.persistence.entities.PersistentClassCourse;
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
    return super.patchValues(sc, rest, match);
  }

  @PUT
  @Produces({"application/json"})    
  @Path("/getJSONLaunchDataBytes")
  public String getJSONLaunchDataBytes(@Context SecurityContext sc, RestScoContext rest,
                                       @HeaderParam("X-ClassCourseID") String ccid, @HeaderParam("X-TOTP") String totp) throws Dwo2Exception {
    return super.getJSONLaunchDataBytes(sc, rest);
  }

  @PUT
  @Produces({"application/json"})
  @Path("/getValues")
  public Response getValues(@Context SecurityContext sc, RestScormValues rest,@HeaderParam("X-ClassCourseID") String ccid, @HeaderParam("X-TOTP") String totp) throws Dwo2Exception {
    return super.getValues(sc, rest);
  }

  @PUT
  @Produces({"application/json"})
  @Path("/setValues")
  public Response setValues(@Context SecurityContext sc, RestScormValues rest, @HeaderParam("if-match") EntityTag match) throws Dwo2Exception {
    return super.setValues(sc, rest, match);
  }

  	final static Integer EXAM = Integer.valueOf(1);

	@Override
	boolean checkType(PersistentClassCourse pcc) {
		return EXAM.equals(pcc.getType());
	}

}
