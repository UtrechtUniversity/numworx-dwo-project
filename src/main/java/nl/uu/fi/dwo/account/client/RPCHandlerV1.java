package nl.uu.fi.dwo.account.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.osgi.util.function.Function;
import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Promises;

//import com.fredhat.gwt.xmlrpc.client.XmlRpcClient;
//import com.fredhat.gwt.xmlrpc.client.XmlRpcRequest;
import com.google.gwt.json.client.JSONValue;
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
import nl.uu.fi.dwo.rest.dom.entities.DomMapEntry;
import nl.uu.fi.dwo.rest.dom.entities.DomResultsPerStudentCourse;
import nl.uu.fi.dwo.rest.dom.entities.DomSchool;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolsRolesAndClassesV2;
import nl.uu.fi.dwo.rest.dom.entities.DomScoContext;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentScoContext;
import nl.uu.fi.dwo.rest.dom.entities.DomUserFullwLoginContext;
import nl.uu.fi.dwo.rest.dom.entities.util.CourseType;
import nl.uu.fi.dwo.rest.persistence.PersistenceClassType;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;

/**
 * equivalent van de PersistenceFacade voor DWO v1.0
 * 
 * @author velth101
 *
 */
@Deprecated
public abstract class RPCHandlerV1 {

	private int profile;
	
    /**
     *
     * @param server
     * @param profile
     */
    RPCHandlerV1(int profile) {
		this.profile = profile;
	}
		
    /**
     *
     */
    //protected static int PROFILE_OFFSET = -1234;
	
    /**
     *
     * @param name
     * @param password
     * @param callback
     */
//    protected abstract void login(String name, String password, AsyncCallback<Map<String,Object>> callback);

//    /**
//     *
//     * @param name
//     * @param password
//     * @return
//     */
//    public Promise<DomUserFullwLoginContext> login(String name, String password)
//	{
//		return Promises.failed(new Error());
//	}
//	
//    /**
//     *
//     * @param name
//     * @param password
//     * @return
//     */
//    public Promise<DomUserFullwLoginContext> loginMD5(String name, String password) {
//		return Promises.failed(new Error());
//	}
//	
//    /**
//     *
//     * @return
//     */
//    public Promise<DomSchoolsRolesAndClassesV2> getSchoolLogins() {
//		return Promises.failed(new Error());
//	}
//	
//	
//    /**
//     *
//     * @param user_id
//     * @param org_id
//     * @return
//     */
//    public Promise<DomUserFullwLoginContext> samlLogin(String user_id, String org_id) {
//		return Promises.failed(new Error());
//	}
//	
//    /**
//     *
//     * @param authToken
//     * @return
//     */
//    public Promise<DomUserFullwLoginContext> getUserFromAuthToken(String authToken) {
//		return Promises.failed(new RuntimeException(""));
//	}


	/**
     *
     * @param object
     * @param type
     * @return
     */
    protected static PersistenceId idOf(Object object, PersistenceClassType type) {
		if(object == null || "".equals(object))
				return null;
		PersistenceId id = new PersistenceId();
		id.setIdString("MYSQL;" + type + ";" + object);
		return id;
	}

    /**
     *
     * @param getCoursesCallback
     */
//    public abstract void getCourses(
//			AsyncCallback<List<Map<String,Object>>> getCoursesCallback);
//	
    /**
     *
     * @return
     */
//    public Promise<List<DomCourseStudent>> getCourses() {
//		PromiseCallback<List<Map<String,Object>>> defer = new PromiseCallback<>();
//		getCourses(defer);
//		return defer.getPromise().map(TO_DOMCOURSELIST);
//	}
	
    /**
     *
     * @param schoolID
     * @param getCoursesCallback
     */
//    public abstract void getCoursesSchool(Object schoolID, AsyncCallback<List<Map<String,Object>>> getCoursesCallback);
	
    /**
     *
     * @param school
     * @return
     */
//    public Promise<List<DomCourseStudent>> getCoursesSchool(DomSchool school) {
//		PromiseCallback<List<Map<String,Object>>> defer = new PromiseCallback<>();
//		Object id = PersistenceIdDecoderInterface.instance.idOf(school.getId(), PersistenceClassType.PersistentSchool);
//		getCoursesSchool(id, defer);
//		return defer.getPromise().map(TO_DOMCOURSELIST);
//	}
		
    /**
     *
     * @param classid
     * @param getCoursesCallback
     */
//    public abstract void getCoursesClass(Object classid, AsyncCallback<List<Map<String,Object>>> getCoursesCallback);

    /**
     *
     * @param schoolclass
     * @return
     */
//    public Promise<DomCoursesOfSchoolClass> getCoursesClass(final DomSchoolClass schoolclass) {
//		PromiseCallback<List<Map<String,Object>>> defer = new PromiseCallback<>();
//		Object id = PersistenceIdDecoderInterface.instance.idOf(schoolclass.getId(), PersistenceClassType.PersistentSchoolClass);
//		getCoursesClass(id, defer);
//		return defer.getPromise().map(TO_DOMCOURSESOFSCHOOLCLASS).map(new Function<DomCoursesOfSchoolClass, DomCoursesOfSchoolClass>() {
//
//			@Override
//			public DomCoursesOfSchoolClass apply(DomCoursesOfSchoolClass t) {
//				t.setSchoolClass(schoolclass);
//				return t;
//			}
//		});
//	}
	
//    /**
//     *
//     * @param callback
//     * @return
//     */
//    protected  AsyncCallback<List<Map<String,Object>>> filterProfile(final AsyncCallback<List<Map<String,Object>>> callback) {
//		return new AsyncCallback<List<Map<String,Object>>>() {
//
//			@Override
//			public void onFailure(Throwable caught) {
//				callback.onFailure(caught);
//				
//			}
//
//			@Override
//			public void onSuccess(List<Map<String, Object>> result) {
//				Iterator<Map<String, Object>> i = result.iterator();
//				while (i.hasNext()) {
//					Map<java.lang.String, java.lang.Object> map = (Map<java.lang.String, java.lang.Object>) i
//							.next();
//					final Integer dwoProfile = getProfile();
//					if(! map.get("dwoProfileID").equals( dwoProfile))
//						i.remove();
//				}
//				callback.onSuccess(result);
//			}
//			
//		};
//	}
	
    /**
     *
     * @return
     */
    final int getProfile() {
		return profile;
	}

    /**
     *
     * @param id
     * @param getCoursesCallback
     */
//    public abstract void getCourses(Object id, AsyncCallback<List<Map<String,Object>>> getCoursesCallback);

    /**
     *
     * @param id
     * @return
     */
//    public Promise<List<DomCourseStudent>> getCourses(Object id) {
//		PromiseCallback<List<Map<String,Object>>> defer = new PromiseCallback<>();
//		getCourses(id, defer);
//		return defer.getPromise().map(TO_DOMCOURSELIST);
//	}
	
    /**
     *
     * @param id
     * @param getScosCallback
     */
//    abstract void getScos(Object id, AsyncCallback<List<Map<String,Object>>> getScosCallback);
	
    /**
     *
     * @param id
     * @return
     */
//    public Promise<List<DomScoContext>> getScos(Object id) {
//		PromiseCallback<List<Map<String,Object>>> defer = new PromiseCallback<>();
//		getScos(id, defer);
//		return defer.getPromise().map(TO_DOMSCOCONTEXTLIST);
//	}
	
    /**
     *
     * @param getProfileCallback
     * @deprecated
     */
//    @Deprecated
//	abstract void getDwoProfile(AsyncCallback<Map<String,Object>> getProfileCallback);
	
    /**
     *
     * @param courseID
     * @param getCourseCallback
     */
//    abstract void getCourse(Object courseID, AsyncCallback<Map<String, Object>> getCourseCallback);

    /**
     *
     * @param courseID
     * @return
     */
//    public Promise<DomCourseStudent> getCourse(Object courseID) {
//		PromiseCallback<Map<String,Object>> defer = new PromiseCallback<>();
//		getCourse(courseID, defer);
//		return defer.getPromise().map(TO_DOMCOURSE);
//	}

    /**
     *
     * @param scoID
     * @param callback
     */
//    abstract void getSco(Object scoID, AsyncCallback<Map<String,Object>> callback);

//    /**
//     *
//     * @param scoID
//     * @return
//     */
//    public Promise<DomScoContext> getSco(Object scoID) {
//		PromiseCallback<Map<String,Object>> defer = new PromiseCallback<>();
//		getSco(scoID, defer);
//		return defer.getPromise().map(TO_DOMSCOCONTEXT);
//	}
	
// In Mc2 new String()

    /**
     *
     * @param courseID
     * @return
     */
	protected Object objectToKey(Object courseID) {
		return new Integer(courseID.toString());
	}
	
    /**
     *
     * @param <T>
     * @param userID
     * @param getClassesCallback
     */
//    public abstract <T> void getClasses(Object userID, AsyncCallback<T> getClassesCallback);
	
    /**
     *
     * @param <T>
     * @param classID
     * @param getStudentsCallback
     */
//    abstract public <T> void getStudents(int classID, AsyncCallback<T> getStudentsCallback);
	
    /**
     *
     * @param courseID
     * @param userID
     * @param getUserResultsCallback
     */
//    public abstract void getUserResults(Object courseID, Object userID, AsyncCallback<List<Map<String,Object>>> getUserResultsCallback);

    /**
     *
     * @param courseID
     * @param userID
     * @return
     */
//    public Promise<DomResultsPerStudentCourse> getUserResults(Object courseID, Object userID) {
//		PromiseCallback<List<Map<String,Object>>> defer = new PromiseCallback<>();
//		getUserResults(courseID, userID, defer);
//		return defer.getPromise().map(TO_RESULTS_PER_STUDENTCOURSE);
//	}
	
    /**
     *
     */
    public Promise<Void> logout() {
		return Promises.resolved(null);
	}
	
	
	/**
	 * Get the DomDwoProfile.
	 * TODO voor Gert: in V2 is dit veeel makkelijker via de (TODO) PublicDwoProfileManager?
	 * @return a promise for the DwoProfile.
	 */
	
//	public Promise<DomDwoProfileFull> getDwoProfile() {
//		PromiseCallback<Map<String,Object>> defer = new PromiseCallback<>();
//		getDwoProfile(defer);
//		return defer.getPromise().map(TO_DWOPROFILE);
//	}
	
//	public Promise<Map<String,String>> getValues(Object scoID, Collection<String> keys) {
//		return Promises.failed(new IllegalArgumentException());
//	}
//	
//	public Promise<?> setValues(Object scoID, Map<String, String> values) {
//		return Promises.failed(new IllegalArgumentException());
//	}
//	
//	public Promise<JSONValue> getJSONLaunchDataBytes(Object scoID) {
//		return Promises.failed(new IllegalArgumentException()); // TODO legacy implementation
//	}
//	
//	public Promise<JSONValue> getCourseDescription(Object scoID) {
//		return Promises.failed(new IllegalArgumentException()); // TODO legacy implementation
//	}
}
