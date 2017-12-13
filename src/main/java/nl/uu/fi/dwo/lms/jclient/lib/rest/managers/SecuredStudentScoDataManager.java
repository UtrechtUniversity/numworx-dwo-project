package nl.uu.fi.dwo.lms.jclient.lib.rest.managers;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Promises;

import nl.numworx.async.Async;
import nl.uu.fi.dwo.lms.jclient.lib.rest.transport.RestAuthenticator;
import nl.uu.fi.dwo.lms.jclient.lib.rest.transport.StoredRestManager;
import nl.uu.fi.dwo.rest.dom.entities.DomScormValues;
import nl.uu.fi.dwo.rest.entities.RestScormValues;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;

/**
 * high level transport of scorm values.
 * @author wim
 *
 */
public class SecuredStudentScoDataManager implements StudentScoDataManager {

	private static final Async ASYNC = new Async();
	private static final Logger LOG = Logger.getLogger(SecuredStudentScoDataManager.class.getName());
	private static StudentScoDataManager instance = new SecuredStudentScoDataManager();
	private static StudentScoDataManager mediate = ASYNC.mediate(instance, StudentScoDataManager.class);

	// public methods.
	
	public static DomScormValues get(DomScormValues dom) throws Dwo2Exception {
		return instance.getValues(dom);
	}
	public static Promise<DomScormValues> getAsync(DomScormValues dom) {
		try {
			return ASYNC.call(mediate.getValues(dom));
		} catch(Dwo2Exception e) {
			return Promises.failed(e);
		}
	}
	
	public static DomScormValues set(DomScormValues dom) throws Dwo2Exception {
		return instance.setValues(dom);
	}

	public static Promise<DomScormValues> setAsync(DomScormValues dom) {
		try {
			return ASYNC.call(mediate.setValues(dom));
		} catch(Dwo2Exception e) {
			return Promises.failed(e);
		}
	}
	
	
	
	@Override
	public DomScormValues getValues(DomScormValues dom) throws Dwo2Exception {
		RestScormValues rest = new RestScormValues();
		rest.setDomScormValues(dom);
		rest.setRestContext(RestAuthenticator.getInstance().getContext());
		DomScormValues result = StoredRestManager.getInstance().put("rest/secure/user/scoData/getValues",DomScormValues.class, rest);
        LOG.log(Level.FINE, "got scormvalues for the user with username {0}.", new Object[]{RestAuthenticator.getInstance().getUsername()});
		return result;
	}

	@Override
	public DomScormValues setValues(DomScormValues dom) throws Dwo2Exception {
		RestScormValues rest = new RestScormValues();
		rest.setDomScormValues(dom);
		rest.setRestContext(RestAuthenticator.getInstance().getContext());
		DomScormValues result = StoredRestManager.getInstance().put("rest/secure/user/scoData/setValues",DomScormValues.class, rest);
        LOG.log(Level.FINE, "updated scormvalues for the user with username {0}.", new Object[]{RestAuthenticator.getInstance().getUsername()});
		return result;
	}

}
