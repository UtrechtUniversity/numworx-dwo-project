package nl.uu.fi.dwo.lms.jclient.lib.rest.managers;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;

import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Promises;

import nl.numworx.async.Async;
import nl.uu.fi.dwo.lms.jclient.lib.rest.transport.RestAuthenticator;
import nl.uu.fi.dwo.lms.jclient.lib.rest.transport.StoredRestManager;
import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.dom.entities.DomScormValues;
import nl.uu.fi.dwo.rest.entities.RestScormValues;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.util.PathId;

/**
 * high level transport of scorm values.
 * 
 * @author wim
 *
 */
public class SecuredStudentScoDataManager implements StudentScoDataManager {

  private static final Async ASYNC = new Async();
  private static final Logger LOG = Logger.getLogger(SecuredStudentScoDataManager.class.getName());
  private static StudentScoDataManager instance = new SecuredStudentScoDataManager();
  private static StudentScoDataManager mediate =
      ASYNC.mediate(instance, StudentScoDataManager.class);
  private StoredRestManager restManager;

  // public methods.
  @Inject
  public SecuredStudentScoDataManager(StoredRestManager restManager) {
    this.restManager = restManager;
  }

  public SecuredStudentScoDataManager() {
    this(StoredRestManager.getInstance());
  }

  public static DomScormValues get(DomScormValues dom) throws Dwo2Exception {
    return instance.getValues(dom);
  }

  public static Promise<DomScormValues> getAsync(DomScormValues dom) {
    try {
      return ASYNC.call(mediate.getValues(dom));
    } catch (Dwo2Exception e) {
      return Promises.failed(e);
    }
  }

  public static Boolean set(DomScormValues dom) throws Dwo2Exception {
    return instance.setValues(dom);
  }

  public static Promise<Boolean> setAsync(DomScormValues dom) {
    try {
      return ASYNC.call(mediate.setValues(dom));
    } catch (Dwo2Exception e) {
      return Promises.failed(e);
    }
  }

  @Override
  public DomScormValues getValues(DomScormValues dom) throws Dwo2Exception {
    RestScormValues rest = new RestScormValues();
    rest.setDomScormValues(dom);
    rest.setRestContext(getContext());
    DomScormValues result =
        restManager.put("rest/sec:" + PathId.getId(getContext()) + "/user/scoData/getValues", DomScormValues.class, rest);
    LOG.log(Level.FINE, "got scormvalues for the user with username {0}.",
        new Object[] {restManager.getAuthenticator().getUsername()});
    return result;
  }

  DomContext getContext() {
    return restManager.getContext();
  }

  @Override
  public Boolean setValues(DomScormValues dom) throws Dwo2Exception {
    RestScormValues rest = new RestScormValues();
    rest.setDomScormValues(dom);
    rest.setRestContext(getContext());
    Boolean result = restManager.put("rest/sec:" + PathId.getId(getContext()) + "/user/scoData/setValues", Boolean.class, rest);
    LOG.log(Level.FINE, "updated scormvalues for the user with username {0}.",
        new Object[] {restManager.getAuthenticator().getUsername()});
    return result;
  }

}
