package fi.dwo.gwt.lib.rest.CallManagers;

import com.google.gwt.core.client.GWT;
import fi.dwo.gwt.lib.rest.GwtRestVars;
import static fi.dwo.gwt.lib.rest.GwtRestVars.F;
import fi.dwo.gwt.lib.rest.client.RestCallers.SecuredTeacherResultsRestCaller;
import fi.dwo.gwt.lib.rest.util.PromiseCallback;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfile;
import nl.uu.fi.dwo.rest.dom.entities.DomResultsPerTeacher;
import nl.uu.fi.dwo.rest.entities.RestClearStudentDataForScoAndClass;
import nl.uu.fi.dwo.rest.entities.RestDwoProfile;
import nl.uu.fi.dwo.rest.entities.RestResultsPerTeacher;
import nl.uu.fi.dwo.rest.util.PathId;

import org.fusesource.restygwt.client.Defaults;
import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.client.dispatcher.DefaultFilterawareDispatcher;
import org.osgi.util.promise.Promise;

public class SecuredTeacherResultsManager {

    private SecuredTeacherResultsRestCaller service;
    private static final Logger LOG = Logger.getLogger(SecuredTeacherResultsManager.class.getName());

    public SecuredTeacherResultsManager() {
        String url = GwtRestVars.instance().getServer();
        init(url);
    }

    private void init(String url) {
        Defaults.setServiceRoot(url);
        Defaults.setDispatcher(DefaultFilterawareDispatcher.singleton());
        service = (SecuredTeacherResultsRestCaller) GWT.create(SecuredTeacherResultsRestCaller.class);
        LOG.log(Level.INFO, "" + service);
    }

    public Promise<DomResultsPerTeacher> getTeachersResults(DomContext domContext, DomDwoProfile aProfile) {
        PromiseCallback<DomResultsPerTeacher> defer = new PromiseCallback<DomResultsPerTeacher>();
        this.getTeachersResults(domContext, aProfile, defer);
        return defer.getPromise();
    }

    /**
     * Returns the current teacher result data.
     *
     * @param domContext A valid domContext for a teacher in a school.
     * @param aProfile The profile for which this is valid.
     * @param callBack
     */
    private void getTeachersResults(DomContext domContext, DomDwoProfile aProfile, MethodCallback<DomResultsPerTeacher> callBack) {
        RestDwoProfile restPut = new RestDwoProfile(aProfile, domContext);
        F(service::getTeachersResults,PathId.getId(domContext), restPut, callBack);
    }

    public Promise<DomResultsPerTeacher> selectedTeachersResults(DomContext context, DomDwoProfile profile, DomResultsPerTeacher dom) {
        RestResultsPerTeacher rest = new RestResultsPerTeacher(context, profile, dom);
        return F(service::selectedTeachersResult,PathId.getId(context), rest);
    }
    
    public Promise<Boolean> clearStudentResults(RestClearStudentDataForScoAndClass rest) {
        PromiseCallback<Boolean> defer = new PromiseCallback<Boolean>();
        this.clearStudentResults(rest, defer);
        return defer.getPromise();
    }

    /**
     *
     * @param rest
     * @param callBack
     */
    private void clearStudentResults(RestClearStudentDataForScoAndClass rest, MethodCallback<Boolean> callBack) {
      DomContext context = rest.getRestContext();
      F(service::clearStudentResults,PathId.getId(context),rest, callBack);
    }

    public Promise<DomResultsPerTeacher> createStudentResults(RestClearStudentDataForScoAndClass rest) {
    	return F(service::createStudentResults,PathId.getId(rest.getRestContext()), rest);
    }
}
