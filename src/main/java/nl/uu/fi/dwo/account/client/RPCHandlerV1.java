package nl.uu.fi.dwo.account.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.osgi.util.function.Function;
import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Promises;

import com.fredhat.gwt.xmlrpc.client.XmlRpcClient;
import com.fredhat.gwt.xmlrpc.client.XmlRpcRequest;
import com.google.gwt.user.client.rpc.AsyncCallback;

import fi.dwo.gwt.lib.rest.CallManagers.MD5;
import fi.dwo.gwt.lib.rest.util.PersistenceIdDecoderInterface;
import fi.dwo.gwt.lib.rest.util.PromiseCallback;
import nl.uu.fi.dwo.rest.dom.entities.DomClassCourse;
import nl.uu.fi.dwo.rest.dom.entities.DomCourse;
import nl.uu.fi.dwo.rest.dom.entities.DomCourseFull;
import nl.uu.fi.dwo.rest.dom.entities.DomCourseStudent;
import nl.uu.fi.dwo.rest.dom.entities.DomCoursesOfSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfileFull;
import nl.uu.fi.dwo.rest.dom.entities.DomSchool;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolsRolesAndClassesV2;
import nl.uu.fi.dwo.rest.dom.entities.DomUserFullwLoginContext;
import nl.uu.fi.dwo.rest.persistence.PersistenceClassType;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;

/**
 * equivalent van de PersistenceFacade voor DWO v1.0
 * 
 * @author velth101
 *
 */
public class RPCHandlerV1 {

	private static final class ListFunction<D> implements Function<List<Map<String,Object>>, List<D>> {
		private Function<Map<String, Object>, D> function;

		public ListFunction(Function<Map<String, Object>, D> function) {
			this.function = function;
		}

		@Override
		public List<D> apply(List<Map<String,Object>> t) {
			List<D> courseList = new ArrayList<D>();
			for( Map<String,Object> item: t) {
				courseList.add(function.apply(item));
			}
			return courseList;
		}
	}


	private static final List<String> SCO_KEYS = Arrays.asList("scoID", "appletID", "sconame", "description", "showscore", "sequencenr", "courseID" );
	private String server;
	private int profile;
	
	public RPCHandlerV1(String server, int profile) {
		this.server = server;
		this.profile = profile;
	}
		
	protected static int PROFILE_OFFSET = -1234;
	
	protected void login(String name, String password, AsyncCallback<Map<String,Object>> callback)
	{
		String pwmd5 = MD5.md5(password);
		loginMD5(name, pwmd5, callback);
	}

	public Promise<DomUserFullwLoginContext> login(String name, String password)
	{
		return Promises.failed(new Error());
	}
	
	public Promise<DomUserFullwLoginContext> loginMD5(String name, String password) {
		return Promises.failed(new Error());
	}
	
	public Promise<DomSchoolsRolesAndClassesV2> getSchoolLogins() {
		return Promises.failed(new Error());
	}
	
	private void loginMD5(String name, String pwmd5,
			AsyncCallback<Map<String, Object>> callback) {
		XmlRpcClient client = getClient();

		String method = "login";
		Object[] params = { name, pwmd5 };

		@SuppressWarnings({ "unchecked", "rawtypes" })
		XmlRpcRequest request = new XmlRpcRequest(client, method, params, callback);
		request.execute();
	}

//	void samlLogin(String name, String org, AsyncCallback<Map<String,Object>> callback)
//	{
//		XmlRpcClient client = getClient();
//		String method = "login_saml";
//		Object[] params = { name, org };
//
//		@SuppressWarnings({ "unchecked", "rawtypes" })
//		XmlRpcRequest request = new XmlRpcRequest(client, method, params, callback);
//		request.execute();
//	}
	
	
	public Promise<DomUserFullwLoginContext> samlLogin(String user_id, String org_id) {
		return Promises.failed(new Error());
	}

//	void getUserFromAuthToken(String authToken, AsyncCallback<Map<String,Object>> callback)
//	{
//		Throwable caught = new RuntimeException("");
//		callback.onFailure(caught);
//	}
	
	public Promise<DomUserFullwLoginContext> getUserFromAuthToken(String authToken) {
		return Promises.failed(new RuntimeException(""));
	}

	private XmlRpcClient xmlRpcClient;

	/**
	 * XML RPC Mapper voor DomDwoProfiles.
	 */
	private static final Function<Map<String, Object>, DomDwoProfileFull> TO_DWOPROFILE = new Function<Map<String,Object>, DomDwoProfileFull>() {
		
		@Override
		public DomDwoProfileFull apply(Map<String, Object> t) {
			DomDwoProfileFull result = new DomDwoProfileFull();
			result.setDwoProfileDescription(t.get("dwoProfileDescription").toString());
			result.setDwoProfileRights(t.get("dwoProfileRights").toString());
			result.setDwoProfileName(t.get("dwoProfileName").toString());
			result.setDwoProfileText(t.get("dwoProfileText").toString());
			result.setId(null); // FIXME wordt waarschijnlijk niet gebruikt! t.get(dwoProfileID)
			return result;
		}
	};
	
	private static final Function<Map<String,Object>, DomCourseStudent> TO_DOMCOURSE = new Function<Map<String,Object>, DomCourseStudent>() {

		@Override
		public DomCourseStudent apply(Map<String, Object> t) {
			DomCourseStudent course = new DomCourseStudent();
			course.setDescription((String)t.get("description"));
			course.setId(idOf(t.get("courseID"), PersistenceClassType.PersistentCourse));
			course.setImage((String)t.get("image"));
			course.setImageData(null);
			course.setLastChangeTimeStamp(null);
			course.setName((String)t.get("name"));
			course.setParentID(idOf(t.get("parentID"), PersistenceClassType.PersistentCourse));
			course.setSchoolId(idOf(t.get("schoolID"),PersistenceClassType.PersistentSchool));
			try {
				course.setSequenceNr(((Number)t.get("sequencenr")).longValue());
			} catch (Exception e) {
			}
			course.setTreeIndex(null);
			course.setWithChildren((Boolean)t.get("withChildren"));
			return course;
		}
		
	};

	private static final Function<List<Map<String,Object>>, List<DomCourseStudent>> TO_DOMCOURSELIST = 
		new ListFunction<DomCourseStudent>(TO_DOMCOURSE);
	
	protected static final Function<Map<String, Object>, DomClassCourse> TO_DOMCLASSCOURSE = 
			new Function<Map<String,Object>, DomClassCourse>() {

				@Override
				public DomClassCourse apply(Map<String, Object> t) {
					DomClassCourse result = new DomClassCourse();
					result.setCourseId(idOf(t.get("CourseID"),PersistenceClassType.PersistentCourse));
					result.setId(idOf(t.get("ClassCourseID"), PersistenceClassType.PersistentClassCourse));
					result.setClassId(idOf(t.get("ClassID"), PersistenceClassType.PersistentSchoolClass));
					result.setNotAfter(toDate(t.get("notAfter")));
					result.setNotBefore(toDate(t.get("notBefore")));
					result.setType(toInt(t.get("type")));
					return result;
				}

				private Integer toInt(Object object) {
					if (object instanceof Integer) return (Integer) object;
					return null;
				}

				private Date toDate(Object object) {
					if (object instanceof Date) return (Date) object;
					return null;
				}
			};
	
	private static final Function<List<Map<String, Object>>, DomCoursesOfSchoolClass> TO_DOMCOURSESOFSCHOOLCLASS = 
			new Function<List<Map<String,Object>>, DomCoursesOfSchoolClass>() {

				@Override
				public DomCoursesOfSchoolClass apply(List<Map<String, Object>> t) {
					DomCoursesOfSchoolClass result = new DomCoursesOfSchoolClass();
					Map<PersistenceId, DomClassCourse> classcoursemap = new LinkedHashMap<>();
					Map<PersistenceId, DomCourseStudent> coursemap = new LinkedHashMap<>();
					for(Map<String,Object> item: t) {
						DomCourseStudent course = TO_DOMCOURSE.apply(item);
						coursemap.put(course.getId(), course);
						DomClassCourse classcourse = TO_DOMCLASSCOURSE.apply(item);
						classcoursemap.put(classcourse.getId(), classcourse);
					}					
					result.setClassCourses(classcoursemap);
					result.setCourses(coursemap);
					result.setFetchTimeStamp(System.currentTimeMillis());
					return result;
				}
			};
	
	
	
	public XmlRpcClient getClient() {
		if( xmlRpcClient == null)
		{
			xmlRpcClient = new XmlRpcClient(server);
			xmlRpcClient.setTimeoutMillis(10000000);
		}
		return xmlRpcClient;
	}

	protected static PersistenceId idOf(Object object, PersistenceClassType type) {
		if(object == null || "".equals(object))
				return null;
		PersistenceId id = new PersistenceId();
		id.setIdString("MYSQL;" + type + ";" + object);
		return id;
	}

	public void getCourses(
			AsyncCallback<List<Map<String,Object>>> getCoursesCallback) {
		
		String method = "getCourses";
		int profileID = getProfile();
		int guestID = PROFILE_OFFSET - profileID;
		
		XmlRpcClient client = getClient();

		Object[] params = { guestID };

		XmlRpcRequest<List<Map<String,Object>>> request = new XmlRpcRequest<List<Map<String,Object>>>(client, method, params, getCoursesCallback);

		request.execute();
	}
	
	public Promise<List<DomCourseStudent>> getCourses() {
		PromiseCallback<List<Map<String,Object>>> defer = new PromiseCallback<>();
		getCourses(defer);
		return defer.getPromise().map(TO_DOMCOURSELIST);
	}
	
	public void getCoursesSchool(Object schoolID, AsyncCallback<List<Map<String,Object>>> getCoursesCallback) {
		String method = "getTable";
		HashMap<String,Object> g = new HashMap<String,Object>();
		g.put("parentID", 0);
		g.put("schoolID", schoolID);
		g.put("dwoProfileID", getProfile());
		Object[] params = {"tblCourse", g, "name" };
		XmlRpcClient client = getClient();
		XmlRpcRequest<List<Map<String,Object>>> request = new XmlRpcRequest<List<Map<String,Object>>>(client, method, params, getCoursesCallback);
		request.execute();
	}
	
	public Promise<List<DomCourseStudent>> getCoursesSchool(DomSchool school) {
		PromiseCallback<List<Map<String,Object>>> defer = new PromiseCallback<>();
		Object id = PersistenceIdDecoderInterface.instance.idOf(school.getId(), PersistenceClassType.PersistentSchool);
		getCoursesSchool(id, defer);
		return defer.getPromise().map(TO_DOMCOURSELIST);
	}
		
	public void getCoursesClass(Object classid, AsyncCallback<List<Map<String,Object>>> getCoursesCallback) {
		String method = "getCoursesForClass";
		Object[] params = { classid };
		XmlRpcClient client = getClient();
		XmlRpcRequest<List<Map<String,Object>>> request = new XmlRpcRequest<List<Map<String,Object>>>(client, method, params, filterProfile(getCoursesCallback));
		request.execute();
	}

	public Promise<DomCoursesOfSchoolClass> getCoursesClass(final DomSchoolClass schoolclass) {
		PromiseCallback<List<Map<String,Object>>> defer = new PromiseCallback<>();
		Object id = PersistenceIdDecoderInterface.instance.idOf(schoolclass.getId(), PersistenceClassType.PersistentSchoolClass);
		getCoursesClass(id, defer);
		return defer.getPromise().map(TO_DOMCOURSESOFSCHOOLCLASS).map(new Function<DomCoursesOfSchoolClass, DomCoursesOfSchoolClass>() {

			@Override
			public DomCoursesOfSchoolClass apply(DomCoursesOfSchoolClass t) {
				t.setSchoolClass(schoolclass);
				return t;
			}
		});
	}
	
	
	protected  AsyncCallback<List<Map<String,Object>>> filterProfile(final AsyncCallback<List<Map<String,Object>>> callback) {
		return new AsyncCallback<List<Map<String,Object>>>() {

			@Override
			public void onFailure(Throwable caught) {
				callback.onFailure(caught);
				
			}

			@Override
			public void onSuccess(List<Map<String, Object>> result) {
				Iterator<Map<String, Object>> i = result.iterator();
				while (i.hasNext()) {
					Map<java.lang.String, java.lang.Object> map = (Map<java.lang.String, java.lang.Object>) i
							.next();
					final Integer dwoProfile = getProfile();
					if(! map.get("dwoProfileID").equals( dwoProfile))
						i.remove();
				}
				callback.onSuccess(result);
			}
			
		};
	}
	
	final int getProfile() {
		return profile;
	}

	public void getCourses(Object id, AsyncCallback<List<Map<String,Object>>> getCoursesCallback) {
		HashMap<String, Object> g = new HashMap<String,Object>();
		g.put("parentID", id);
		String method = "getTable";
		Object[] params = {"tblCourse", g, "name" };
		XmlRpcClient client = getClient();
		XmlRpcRequest<List<Map<String,Object>>> request = new XmlRpcRequest<List<Map<String,Object>>>(client, method, params, getCoursesCallback);
		request.execute();
	}

	public void getScos(Object id, AsyncCallback<List<Map<String,Object>>> getScosCallback) {
		HashMap<String, Object> g = new HashMap<String,Object>();
		g.put("courseID", id);
		String method = "getTable";
		Object[] params = {"tblSco", SCO_KEYS, g, "sequencenr" };
		XmlRpcClient client = getClient();
		XmlRpcRequest<List<Map<String,Object>>> request = new XmlRpcRequest<List<Map<String,Object>>>(client, method, params, getScosCallback);
		request.execute();
	}

	@Deprecated
	public void getDwoProfile(AsyncCallback<Map<String,Object>> getProfileCallback) {
		String method = "getRecord";
		Object[] params = { "tblDwoProfile", "dwoProfileID", getProfile() };
		XmlRpcClient client = getClient();
		XmlRpcRequest<Map<String,Object>> request = new XmlRpcRequest<Map<String,Object>>(client, method, params, getProfileCallback);
		request.execute();
	}
	
	public void getCourse(Object courseID, AsyncCallback<Map<String, Object>> getCourseCallback) {
		String method = "getRecord";
		Object[] params = { "tblCourse", "courseID", objectToKey(courseID) };
		XmlRpcClient client = getClient();
		XmlRpcRequest<Map<String, Object>> request = new XmlRpcRequest<Map<String, Object>>(client, method, params, getCourseCallback);
		request.execute();
	}

	public void getSco(Object scoID, AsyncCallback<Map<String,Object>> callback) 
	{
		String method = "getRecord";
		Object[] params = { "tblScoContext", "scoID", objectToKey(scoID) };
		XmlRpcClient client = getClient();
		XmlRpcRequest<Map<String,Object>> request = new XmlRpcRequest<Map<String,Object>>(client, method, params, callback);
		request.execute();
	}
	
	
// In Mc2 new String()
	protected Object objectToKey(Object courseID) {
		return new Integer(courseID.toString());
	}
	
	
	public <T> void getClasses(Object userID, AsyncCallback<T> getClassesCallback) {
		HashMap<String, Object> g = new HashMap<String,Object>();
		g.put("userID", userID);
		String method = "getTable";
		Object[] params = {"tblClass", g, "class"};
		XmlRpcClient client = getClient();
		XmlRpcRequest<T> request = new XmlRpcRequest<T>(client, method, params, getClassesCallback);
		request.execute();
	}
	
	public <T> void getStudents(int classID, AsyncCallback<T> getStudentsCallback) {
		HashMap<String, Object> g = new HashMap<String,Object>();
		g.put("classID", classID);
		String method = "getTable";
		Object[] params = {"tblUser", g, "username"};
		XmlRpcClient client = getClient();
		XmlRpcRequest<T> request = new XmlRpcRequest<T>(client, method, params, getStudentsCallback);
		request.execute();
	}
	
	public void getUserResults(Object courseID, Object userID, AsyncCallback<List<Map<String,Object>>> getUserResultsCallback) {
		Object[] params = { courseID, userID };
		XmlRpcClient client = getClient();
		XmlRpcRequest<List<Map<String,Object>>> request = new XmlRpcRequest<List<Map<String,Object>>>(client, "getUserResults", params, getUserResultsCallback);
		request.execute();
	}
	
//	private <T> void getCourseSequence0(Object schoolID, AsyncCallback<T> callback) {
//		HashMap<String,Object> g = new HashMap<String,Object>();
//		g.put("classID", 0);
//		if(schoolID == null) schoolID = Integer.valueOf(0);
//		g.put("schoolID", schoolID);
//		g.put("profileID", getDwoProfile());
//		String method = "getTable";
//		Object[] params = { "tblCourseSequence", g, "sequencenr" };
//		XmlRpcClient client = getClient();
//		XmlRpcRequest<T> request = new XmlRpcRequest<T>(client, method, params, callback);
//		request.execute();
//	}
	
//	Map<Object, Integer> courseSortMap = new HashMap<Object,Integer>();
	
//	class CourseSortCallback implements AsyncCallback<List<Map<String,Object>>> {
//
//		Runnable runner;
//		
//		@Override
//		public void onFailure(Throwable caught) {
//			runner.run();
//		}
//
//		@Override
//		public void onSuccess(List<Map<String, Object>> result) {
//			for (Iterator<Map<String, Object>> iterator = result.iterator(); iterator.hasNext();) {
//				Map<String, Object> map = iterator.next();
//				Object id = map.get("courseID");
//				Number n  = (Number) map.get("sequencenr");
//				courseSortMap.put(id, n.intValue());
//			}
//			runner.run();
//		}
//		
//	}
	
	public void getCourseSequence(final Object schoolID, final Runnable runner) {

//		if(!GWT.isProdMode()) {
			runner.run();
			return;
//		}
//		final CourseSortCallback csc = new CourseSortCallback();
//		if(schoolID != null || !"".equals(schoolID)) {
//			Runnable rnull = new Runnable() {
//
//				@Override
//				public void run() {
//					csc.runner = runner;
//					getCourseSequence0(schoolID, csc);
//				}
//				
//			};
//			csc.runner = rnull;
//		} else {
//			csc.runner = runner;
//		}
//		getCourseSequence0(null, csc);
	}

	public void logout() {
	}
	
	
	/**
	 * Get the DomDwoProfile.
	 * TODO voor Gert: in V2 is dit veeel makkelijker via de (TODO) PublicDwoProfileManager?
	 * @return a promise for the DwoProfile.
	 */
	
	public Promise<DomDwoProfileFull> getDwoProfile() {
		PromiseCallback<Map<String,Object>> defer = new PromiseCallback<>();
		getDwoProfile(defer);
		return defer.getPromise().map(TO_DWOPROFILE);
	}
	
}
