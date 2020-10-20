package nl.uu.fi.dwo.lms.jclient.lib.rest.managers;

import java.util.List;

import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Promises;

import nl.uu.fi.dwo.lms.jclient.lib.rest.transport.StoredRestManager;
import nl.numworx.async.Async;
import nl.uu.fi.dwo.lms.jclient.lib.rest.transport.RestAuthenticator;
import nl.uu.fi.dwo.rest.RestListClassTypes;
import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.dom.entities.DomCourse;
import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfile;
import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfileId;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClassId;
import nl.uu.fi.dwo.rest.dom.entities.DomScoContext;
import nl.uu.fi.dwo.rest.dom.entities.DomScoContextId;
import nl.uu.fi.dwo.rest.dom.entities.DomScoData;
import nl.uu.fi.dwo.rest.entities.RestCourse;
import nl.uu.fi.dwo.rest.entities.RestScoContext;
import nl.uu.fi.dwo.rest.entities.RestScoContextId;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.util.PathId;

public class PublicScoContextManager implements ScoContextManager {

  public static ScoContextManager instance = new PublicScoContextManager();

  private PublicScoContextManager() {};

  static final Async async = new Async();
  static final ScoContextManager mediate = async.mediate(instance, ScoContextManager.class);

  public static Promise<DomScoContext> getAsync(DomScoContext domScoId, DomDwoProfile profile,
      DomSchoolClassId schoolClass) {
    try {
      return async.call(mediate.get(domScoId, profile, schoolClass));
    } catch (Dwo2Exception e) {
      return Promises.failed(e);
    }
  }

  public static Promise<List<DomScoContext>> getScosAsync(DomCourse parent, DomDwoProfile profile,
      DomSchoolClassId schoolClass) {
    try {
      return async.call(mediate.getScos(parent, profile, schoolClass));
    } catch (Dwo2Exception e) {
      return Promises.failed(e);
    }
  }

  public static Promise<List<DomScoContext>> getTrashAsync(DomCourse parent, DomDwoProfile profile) {
  	try {
  		return async.call(mediate.getTrash(parent,profile));
  	} catch (Dwo2Exception e) {
  		return Promises.failed(e);
  	}
  }
  /**
   * Retrieve a deeplink sco. Only public scos from a non-limited profile.
   *
   * @param domScoId
   * @return
   * @throws Dwo2Exception
   */
  public DomScoContext get(DomScoContext domScoId, DomDwoProfile profile,
      DomSchoolClassId schoolClass) throws Dwo2Exception {
    RestScoContext rest = new RestScoContext();
    rest.setRestContext(getContext());
    rest.setDomDwoProfile(profile);
    rest.setDomScoContext(domScoId);
    rest.setSchoolClassID(schoolClass);
    DomScoContext result =
        StoredRestManager.getInstance().put(pfx() + "/scoContext/get", DomScoContext.class, rest);
    return result;
  }

  private DomContext getContext() {
    return RestAuthenticator.getInstance().getContext();
  }

  private String pfx() {
    if (RestAuthenticator.getInstance().isAuthenticated()) {
      return "rest/sec:" + PathId.getId(getContext()) + "/user";
    }
    return "rest/public";
  }

  /**
   * Get the scos of a course. Only public courses are allowed from a non-limited profile.
   *
   * @param course
   * @return ordered list of scos
   * @throws Dwo2Exception
   */
  public List<DomScoContext> getScos(DomCourse course, DomDwoProfile profile,
      DomSchoolClassId schoolClass) throws Dwo2Exception {
    RestCourse rest = new RestCourse();
    rest.setDomDwoProfile(profile);
    rest.setDomCourse(course);
    rest.setRestContext(getContext());
    rest.setSchoolClassID(schoolClass);
    List<DomScoContext> result = StoredRestManager.getInstance()
        .getPutList(pfx() + "/scoContext/getScos", RestListClassTypes.DomScoContext, rest);
    return result;
  }

  public List<DomScoContext> getTrash(DomCourse course, DomDwoProfile profile) throws Dwo2Exception {
	    RestCourse rest = new RestCourse();
	    rest.setDomDwoProfile(profile);
	    rest.setDomCourse(course);
	    rest.setRestContext(getContext());
	    rest.setSchoolClassID(null);
	    List<DomScoContext> result = StoredRestManager.getInstance()
	        .getPutList(pfx() + "/scoContext/getTrashedScos", RestListClassTypes.DomScoContext, rest);
	  return result;
  }

  @Override
  public DomScoData getData(DomScoContextId domScoId, DomDwoProfileId profile,
      DomSchoolClassId schoolClass) throws Dwo2Exception {
    RestScoContextId rest = new RestScoContextId();
    rest.setRestContext(getContext());
    rest.setDomDwoProfile(profile);
    rest.setDomScoContext(domScoId);
    rest.setSchoolClassID(schoolClass);
    DomScoData result =
        StoredRestManager.getInstance().put(pfx() + "/scoContext/getData", DomScoData.class, rest);
    return result;
  }

  public static Promise<DomScoData> getDataAsync(DomScoContextId domScoId, DomDwoProfileId profile,
  DomSchoolClassId schoolClass) {
    try {
      return async.call(mediate.getData(domScoId,profile,schoolClass));
    } catch(Dwo2Exception e) {
      return Promises.failed(e);
    }
  }
}
