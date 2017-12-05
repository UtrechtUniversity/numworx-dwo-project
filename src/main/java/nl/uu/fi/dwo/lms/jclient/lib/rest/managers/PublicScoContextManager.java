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
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClassId;
import nl.uu.fi.dwo.rest.dom.entities.DomScoContext;
import nl.uu.fi.dwo.rest.entities.RestCourse;
import nl.uu.fi.dwo.rest.entities.RestScoContext;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;

public class PublicScoContextManager implements ScoContextManager {

    public static ScoContextManager instance = new PublicScoContextManager();

    private PublicScoContextManager() {
    }
    ;
	static final Async async = new Async();
    static ScoContextManager mediate = async.mediate(instance, ScoContextManager.class);

    public static Promise<DomScoContext> getAsync(DomScoContext domScoId, DomDwoProfile profile, DomSchoolClassId schoolClass) {
        try {
            return async.call(mediate.get(domScoId, profile, schoolClass));
        } catch (Dwo2Exception e) {
            return Promises.failed(e);
        }
    }

    public static Promise<List<DomScoContext>> getScosAsync(DomCourse parent, DomDwoProfile profile, DomSchoolClassId schoolClass) {
        try {
            return async.call(mediate.getScos(parent, profile, schoolClass));
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
    public DomScoContext get(DomScoContext domScoId, DomDwoProfile profile, DomSchoolClassId schoolClass) throws Dwo2Exception {
        RestScoContext rest = new RestScoContext();
        rest.setRestContext(RestAuthenticator.getInstance().getContext());
        rest.setDomDwoProfile(profile);
        rest.setDomScoContext(domScoId);
        rest.setSchoolClassID(schoolClass);
        DomScoContext result = StoredRestManager.getInstance().put(pfx() + "/scoContext/get", DomScoContext.class, rest);
        return result;
    }

    private DomContext getContext() {
        return RestAuthenticator.getInstance().getContext();
    }

    private String pfx() {
        if (RestAuthenticator.getInstance().isAuthenticated()) {
            return "rest/secure/user";
        }
        return "rest/public";
    }

    /**
     * Get the scos of a course. Only public courses are allowed from a
     * non-limited profile.
     *
     * @param course
     * @return ordered list of scos
     * @throws Dwo2Exception
     */
    public List<DomScoContext> getScos(DomCourse course, DomDwoProfile profile, DomSchoolClassId schoolClass) throws Dwo2Exception {
        RestCourse rest = new RestCourse();
        rest.setDomDwoProfile(profile);
        rest.setDomCourse(course);
        rest.setRestContext(RestAuthenticator.getInstance().getContext());
        rest.setSchoolClassID(schoolClass);
        List<DomScoContext> result = StoredRestManager.getInstance().getPutList(pfx() + "/scoContext/getScos", RestListClassTypes.DomScoContext, rest);
        return result;
    }

}
