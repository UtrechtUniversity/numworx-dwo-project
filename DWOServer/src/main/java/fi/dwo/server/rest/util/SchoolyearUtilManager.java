package fi.dwo.server.rest.util;

import java.io.StringReader;
import java.net.URI;
import java.net.URL;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonString;

import fi.dwo.commons.persistence.MySQLPersistenceId;
import fi.dwo.commons.persistence.entities.PersistentSchool;
import fi.dwo.commons.persistence.entities.PersistentSchoolData;
import fi.dwo.server.PersistentDataManagers.core.SchoolDataManager;
import fi.dwo.server.PersistentDataManagers.util.SchoolDataUtilManager;
import nl.numworx.schoolyear.jclient.SchoolyearClient;
import nl.uu.fi.dwo.rest.dom.entities.DomSchool;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.exceptions.Dwo2ExceptionCode;

public class SchoolyearUtilManager {

	public static SchoolyearClient build(PersistentSchoolData data) {
		JsonString auth, endpoint;
		SchoolyearClient.Builder builder = new SchoolyearClient.Builder();
	   	JsonReader reader = Json.createReader(new StringReader(data.getSchoolData()));
    	JsonObject object = reader.readObject();
    	reader.close();
    	auth = object.getJsonString("Sy-Api-Key");
    	endpoint = object.getJsonString("Sy-Api-Endpoint");
    	if (auth != null)
    		builder.setKey(auth.getString());
    	if (endpoint != null) {
    		builder.setUrl(URI.create(endpoint.getString()));
    	}    	
    	return builder.build();
	}
	
	public static SchoolyearClient build(PersistentSchool school) throws Dwo2Exception {
		if (school.hasKiosk()) {
			PersistentSchoolData data = SchoolDataUtilManager.find(school);
			if (data == null) data = new PersistentSchoolData();
			return build(data);
		}
		throw new Dwo2Exception(Dwo2ExceptionCode.Client_InternalError, "no permission");
	}
	
	public static SchoolyearClient build(DomSchool school) throws Dwo2Exception {
		if (school.hasKiosk()) {
			Long id = MySQLPersistenceId.getNativeId(school);
			PersistentSchoolData data = SchoolDataUtilManager.find(new PersistentSchool(id));
			if (data == null) data = new PersistentSchoolData();
			return build(data);			
		}
		throw new Dwo2Exception(Dwo2ExceptionCode.Client_InternalError, "no permission");
	}
	
}
