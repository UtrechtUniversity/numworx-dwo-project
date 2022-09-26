package nl.uu.fi.dwo.mobile.client.ui;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.osgi.util.function.Function;
import org.osgi.util.promise.Deferred;
import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Promises;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;

import fi.dwo.gwt.lib.rest.CallManagers.XapiManager;
import nl.uu.fi.dwo.mobile.DWOplayer;
import nl.uu.fi.dwo.mobile.client.DWOplayerParameters;
import nl.uu.fi.dwo.rest.dom.entities.DomClassCourse;
import nl.uu.fi.dwo.rest.dom.entities.DomCourseStudent;
import nl.uu.fi.dwo.rest.dom.entities.DomCoursesOfSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfileFull;
import nl.uu.fi.dwo.rest.dom.entities.DomResultsPerStudentCourse;
import nl.uu.fi.dwo.rest.dom.entities.DomSchool;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolsRolesAndClassesV2;
import nl.uu.fi.dwo.rest.dom.entities.DomScoContext;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContext;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContextId;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelDataScore;
import nl.uu.fi.dwo.rest.dom.entities.DomUserFullwLoginContext;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;

public class DummyRPCHandler implements RPCHandler {
 
	private final DWOplayerParameters PARAMETERS;

  @Inject public DummyRPCHandler(DWOplayerParameters p) {
	  this.PARAMETERS = p;
    }

  @Override
	public Promise<Void> logout() {
		return null;

	}

	@Override
	public Promise<DomDwoProfileFull> getDwoProfile() {
		return null;
	}

	@Override
	public Promise<DomCoursesOfSchoolClass> getCoursesClass(DomSchoolClass schoolClass) {
		return null;
	}

	@Override
	public Promise<List<DomCourseStudent>> getCoursesSchool(DomSchool school) {
		return null;
	}

	@Override
	public Promise<List<DomCourseStudent>> getCourses() {
		return null;
	}

//	@Override
//	public XmlRpcClient getClient() {
//		return null;
//	}

	@Override
	public Promise<DomCourseStudent> getCourse(Object id) {
		return null;
	}

	@Override
	public Promise<DomScoContext> getSco(Object id) {
		return null;
	}

	@Override
	public Promise<DomResultsPerStudentCourse> getUserResults(Object courseID, Object userID) {
		return null;
	}

	@Override
	public Promise<List<DomScoContext>> getScos(Object id) {
		return null;
	}

	@Override
	public Promise<List<DomCourseStudent>> getCourses(Object parentID) {
		return null;
	}

	@Override
	public Promise<DomSchoolsRolesAndClassesV2> getSchoolLogins() {
		return null;
	}

	@Override
	public Promise<DomUserFullwLoginContext> samlLogin(String user_id, String org_id) {
		return null;
	}

	@Override
	public Promise<DomUserFullwLoginContext> getUserFromAuthToken(String authToken) {
		return null;
	}

	@Override
	public Promise<DomUserFullwLoginContext> login(String username, String password) {
		return null;
	}

	@Override
	public Promise<DomUserFullwLoginContext> loginMD5(String username, String password) {
		return null;
	}

	@Override
	public Promise<Map<String, String>> getValues(Object scoID, Collection<String> keys) {
		return null;
	}

	@Override
	public Promise<?> setValues(Object scoID, Map<String, String> values) {
		return null;
	}

	@Override
	public Promise<Void> startExam(DomClassCourse classCourse, String password) {
		return Promises.resolved(null);
	}

	@Override
	public boolean inExam(DomClassCourse classCourse) {
		return true;
	}
 
/** 
 * Not so dummy implementation.
 * @param scoID sco id
 */
	@Override
	public Promise<JSONValue> getJSONLaunchDataBytes(Object scoID) {
		final Deferred<String> defer = new Deferred<String>();
		String url = PARAMETERS.getLaunchData() + scoID;
		RequestBuilder rb = new RequestBuilder(RequestBuilder.GET, url);
		rb.setTimeoutMillis(1000000);
		try
		{
			rb.sendRequest(null, new RequestCallback()
			{
	
				@Override
				public void onResponseReceived(Request request, Response response)
				{
					String responseText = response.getText();
					if (response.getStatusCode() == 200)
					{
						defer.resolve(responseText);;
					} else {
						defer.fail(new RequestException("response empty"));
					}
				}
	
				@Override
				public void onError(Request request, Throwable exception)
				{
					defer.fail(exception);
				}
			});
	
		}
		catch (RequestException e)
		{
			defer.fail(e);
		}
		return defer.getPromise().map(new Function<String,JSONValue>() {

			@Override
			public JSONValue apply(String t) {
				return JSONParser.parseStrict(t);
			}});
	}

	@Override
	public Promise<DomStudentModelContext> getStudentModel(PersistenceId id) {
		return Promises.resolved(null);
	}

	@Override
	public Promise<List<DomStudentModelContext>> getStudentModels() {
		return Promises.resolved(Collections.emptyList());
	}

	@Override
	public Promise<DomStudentModelDataScore> getStudentModelDataScore(DomStudentModelContextId id) {
		return Promises.failed(new IllegalArgumentException());
	}

	@Override
	public Promise<DomCoursesOfSchoolClass> getCourseClass(Object course, DomSchoolClass schoolClass) {
		return null;
	}

	@Override
	public Promise<DomCoursesOfSchoolClass> getScoContextClass(Object sco, DomSchoolClass schoolClass) {
		return null;
	}

  @Override
  public Promise<XapiManager> getLRS() {
    return Promises.failed(new NullPointerException("Not implemented"));
  }

  @Override
  public Promise<JSONValue> getCourseDescription(Object file) {
    return null;
  }

@Override
public Promise<JSONValue> refreshExam() {
	// TODO Auto-generated method stub
	return null;
}

@Override
public Promise<List<DomSchoolClass>> getStudentsSchoolClasses() {
	// TODO Auto-generated method stub
	return null;
}

@Override
public Promise<Boolean> setActiveSchoolClass(DomSchoolClass schoolClass) {
	// TODO Auto-generated method stub
	return null;
}

@Override
public Promise<DomCoursesOfSchoolClass> getClassCourse(Object id) {
  // TODO Auto-generated method stub
  return null;
}

@Override
public Promise<JSONValue> getProfileDescription() {
	// TODO Auto-generated method stub
	return null;
}

}
