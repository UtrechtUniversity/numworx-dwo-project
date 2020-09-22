package fi.dwo.server.rest;

import java.util.logging.Logger;

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

@Path("/secure/user/scoData")
public class SecuredUserScoDataManager extends SecuredCommonScoDataManager {
    private static final Logger LOG = Logger.getLogger(SecuredUserScoDataManager.class.getName());

    @PUT
    @Produces({"application/json"})    
    @Path("/getJSONLaunchDataBytes")
    public String getJSONLaunchDataBytes(@Context SecurityContext sc, RestScoContext rest) throws Dwo2Exception {
      return super.getJSONLaunchDataBytes(sc, rest);
    }

    @PUT
    @Produces({"application/json"})
    @Path("/getValues")
    public Response getValues(@Context SecurityContext sc, RestScormValues rest) throws Dwo2Exception {
      return super.getValues(sc, rest);
    }

    @PUT
    @Produces({"application/json"})
    @Path("/setValues")
    public Response setValues(@Context SecurityContext sc, RestScormValues rest, @HeaderParam("if-match") EntityTag match) throws Dwo2Exception {
      return super.setValues(sc, rest, match);
  }
	
	@PUT
	@Produces("application/json")
	@Path("/patchValues")
	public Response patchValues(@Context SecurityContext sc, RestScormValues rest, @HeaderParam("if-match") String match) throws Dwo2Exception {
	  return super.patchValues(sc, rest, match);
	}

	final static Integer NORMAL = Integer.valueOf(0);

	@Override
	boolean checkType(PersistentClassCourse pcc) {
		return NORMAL.equals(pcc.getType());
	}

}
