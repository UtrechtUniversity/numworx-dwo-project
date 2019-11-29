package fi.dwo.server.rest;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.sql.Date;
import java.sql.Time;
import java.util.Hashtable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.stream.JsonParser;
import javax.persistence.PersistenceException;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import com.owlike.genson.Genson;

import fi.beans.dwomaccess.JSONEncoder;
import fi.beans.private_base64code.StringCodeObject;
import fi.beans.scorm2xml.Scorm2Xml;
import fi.dwo.commons.persistence.MySQLPersistenceId;
import fi.dwo.commons.persistence.entities.PersistentClassCourse;
import fi.dwo.commons.persistence.entities.PersistentCourse;
import fi.dwo.commons.persistence.entities.PersistentHasRole;
import fi.dwo.commons.persistence.entities.PersistentHasRolePK;
import fi.dwo.commons.persistence.entities.PersistentSchoolClass;
import fi.dwo.commons.persistence.entities.PersistentScoContext;
import fi.dwo.commons.persistence.entities.PersistentScoData;
import fi.dwo.commons.persistence.entities.PersistentStudentModelData;
import fi.dwo.commons.persistence.entities.PersistentStudentScoContext;
import fi.dwo.commons.persistence.entities.PersistentStudentScoData;
import fi.dwo.commons.persistence.entities.PersistentUser;
import fi.dwo.commons.util.UEscape;
import fi.dwo.server.PersistentDataManagers.core.ClassCourseManager;
import fi.dwo.server.PersistentDataManagers.core.CourseManager;
import fi.dwo.server.PersistentDataManagers.core.HasRoleManager;
import fi.dwo.server.PersistentDataManagers.core.ScoContextManager;
import fi.dwo.server.PersistentDataManagers.core.ScoDataManager;
import fi.dwo.server.PersistentDataManagers.core.StudentModelDataManager;
import fi.dwo.server.PersistentDataManagers.core.StudentScoContextManager;
import fi.dwo.server.PersistentDataManagers.core.StudentScoDataManager;
import fi.dwo.server.PersistentDataManagers.core.UserManager;
import fi.dwo.server.persistence.CmiConvert;
import nl.uu.fi.dwo.rest.dom.entities.DomHasRole;
import nl.uu.fi.dwo.rest.dom.entities.DomMapEntry;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClassId;
import nl.uu.fi.dwo.rest.dom.entities.DomScormValues;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelStructureScore;
import nl.uu.fi.dwo.rest.dom.entities.RoleType;
import nl.uu.fi.dwo.rest.dom.entities.util.ViewState;
import nl.uu.fi.dwo.rest.entities.RestScoContext;
import nl.uu.fi.dwo.rest.entities.RestScormValues;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.exceptions.Dwo2ExceptionCode;
import nl.uu.fi.dwo.rest.exceptions.Dwo2RestException;

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

}
