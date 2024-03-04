package fi.dwo.server.rest;

import java.io.StringReader;

import javax.annotation.security.RolesAllowed;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.stream.JsonParser;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;

import fi.dwo.commons.persistence.MySQLPersistenceId;
import fi.dwo.commons.persistence.entities.PersistentSchoolData;
import fi.dwo.server.PersistentDataManagers.access.AnonDomainAuthorizer;
import fi.dwo.server.PersistentDataManagers.core.SchoolDataManager;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolDataFull;
import nl.uu.fi.dwo.rest.dom.entities.RoleType;
import nl.uu.fi.dwo.rest.dom.entities.util.DelState;
import nl.uu.fi.dwo.rest.entities.RestSchool;
import nl.uu.fi.dwo.rest.entities.RestSchool4DwoAdmin;
import nl.uu.fi.dwo.rest.entities.RestSchoolDataFull;
import nl.uu.fi.dwo.rest.entities.RestSchoolFull;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.exceptions.Dwo2ExceptionCode;

@Path("/secure/dwoadmin/schooldata")
@RolesAllowed("ADMIN")
public class SecuredDwoAdminSchoolDataManager {

    @PUT
    @Produces({"application/json"})
    @Path("/get")
	public DomSchoolDataFull get(@Context SecurityContext sc, RestSchool rest) throws Dwo2Exception {
		AnonDomainAuthorizer.build().submitUser(sc).setHasRoleIfType(rest.getRestContext().getDomHasRole(), RoleType.ADMIN).buildDwoAdmin();
		Long id = MySQLPersistenceId.getNativeId(rest.getDomSchool());
		PersistentSchoolData data = SchoolDataManager.findEntity(id);
		if (data == null) {
			data = new PersistentSchoolData(id);
			data.setOptlock(0L);
		}
		if (data.getDelState() != DelState.not) data.setSchoolData("{}");
		return data.buildDomSchoolDataFull();
	}
 
    @PUT
    @Produces({"application/json"})
    @Path("/update")
    public DomSchoolDataFull update(@Context SecurityContext sc, RestSchoolDataFull rest) throws Dwo2Exception {
		AnonDomainAuthorizer.build().submitUser(sc).setHasRoleIfType(rest.getRestContext().getDomHasRole(), RoleType.ADMIN).buildDwoAdmin();
		Long id = MySQLPersistenceId.getNativeId(rest.getData());
		PersistentSchoolData data = SchoolDataManager.findEntity(id);
		if (data == null) {
			data = new PersistentSchoolData(id);
			SchoolDataManager.create(data);
		} else {
			data.setOptlock(rest.getData().getOptLock());
    	}
		String value = rest.getData().getSchoolData();
        try {
			JsonParser parser = Json.createParser(new StringReader(value));
			parser.next();
			JsonObject oldObject = parser.getObject();
		} catch (Exception e) {
			throw new Dwo2Exception(Dwo2ExceptionCode.User_IllegalAction, e.toString());
		}
        
		data.setSchoolData(value);
		data.setDelState(DelState.not);
		data = SchoolDataManager.edit(data);
		return data.buildDomSchoolDataFull();
    }
    
    @PUT
    @Produces({"application/json"})
    @Path("/remove")
    public Boolean removeSchool(@Context SecurityContext sc, RestSchoolDataFull rest) throws Dwo2Exception {
		AnonDomainAuthorizer.build().submitUser(sc).setHasRoleIfType(rest.getRestContext().getDomHasRole(), RoleType.ADMIN).buildDwoAdmin();
		Long id = MySQLPersistenceId.getNativeId(rest.getData());
		PersistentSchoolData data = SchoolDataManager.findEntity(id);
		if (data != null) {
			data.setDelState(DelState.deleted);
			data.setOptlock(rest.getData().getOptLock());
			data.setSchoolData("{}");
			SchoolDataManager.edit(data);
		}
    	return Boolean.TRUE;
    }
}
