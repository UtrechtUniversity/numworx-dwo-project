package nl.uu.fi.dwo.mobile.client.ui;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.osgi.util.function.Function;
import org.osgi.util.promise.Deferred;
import org.osgi.util.promise.Promise;

import com.fredhat.gwt.xmlrpc.client.XmlRpcClient;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;

import fi.dwo.gwt.lib.rest.util.PromiseCallback;
import nl.uu.fi.dwo.mobile.DWOplayer;
import nl.uu.fi.dwo.mobile.client.text.Text;
import nl.uu.fi.dwo.rest.dom.entities.DomCourseStudent;
import nl.uu.fi.dwo.rest.dom.entities.DomCoursesOfSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfileFull;
import nl.uu.fi.dwo.rest.dom.entities.DomResultsPerStudentCourse;
import nl.uu.fi.dwo.rest.dom.entities.DomSchool;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolsRolesAndClassesV2;
import nl.uu.fi.dwo.rest.dom.entities.DomScoContext;
import nl.uu.fi.dwo.rest.dom.entities.DomUserFullwLoginContext;

class DummyRPCHandler implements RPCHandler {

	@Override
	public void logout() {
		// TODO Auto-generated method stub

	}

	@Override
	public Promise<DomDwoProfileFull> getDwoProfile() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Promise<DomCoursesOfSchoolClass> getCoursesClass(DomSchoolClass schoolClass) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Promise<List<DomCourseStudent>> getCoursesSchool(DomSchool school) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Promise<List<DomCourseStudent>> getCourses() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public XmlRpcClient getClient() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Promise<DomCourseStudent> getCourse(Object id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Promise<DomScoContext> getSco(Object id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Promise<DomResultsPerStudentCourse> getUserResults(Object courseID, Object userID) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Promise<List<DomScoContext>> getScos(Object id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Promise<List<DomCourseStudent>> getCourses(Object parentID) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Promise<DomSchoolsRolesAndClassesV2> getSchoolLogins() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Promise<DomUserFullwLoginContext> samlLogin(String user_id, String org_id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Promise<DomUserFullwLoginContext> getUserFromAuthToken(String authToken) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Promise<DomUserFullwLoginContext> login(String username, String password) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Promise<DomUserFullwLoginContext> loginMD5(String username, String password) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Promise<Map<String, String>> getValues(Object scoID, Collection<String> keys) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Promise<?> setValues(Object scoID, Map<String, String> values) {
		// TODO Auto-generated method stub
		return null;
	}

/** 
 * Not so dummy implementation.
 * @param scoID sco id
 */
	@Override
	public Promise<JSONValue> getJSONLaunchDataBytes(Object scoID) {
		final Deferred<String> defer = new Deferred<String>();
		String url = DWOplayer.PARAMETERS.getLaunchData() + scoID;
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

}
