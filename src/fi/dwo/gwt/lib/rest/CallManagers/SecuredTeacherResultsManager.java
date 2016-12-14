package fi.dwo.gwt.lib.rest.CallManagers;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

import fi.dwo.gwt.lib.rest.GwtRestVars;
import fi.dwo.gwt.lib.rest.client.RestCallers.SecuredTeacherResultsRestCaller;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfile;
import nl.uu.fi.dwo.rest.dom.entities.DomResultsPerTeacher;
import nl.uu.fi.dwo.rest.entities.RestDwoProfile;
import org.fusesource.restygwt.client.Defaults;
import org.fusesource.restygwt.client.dispatcher.DefaultFilterawareDispatcher;

public class SecuredTeacherResultsManager {

    private SecuredTeacherResultsRestCaller service = GWT.create(SecuredTeacherResultsRestCaller.class);
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

    /**
     * Returns the current teacher result data. 
     * @param domContext A valid domContext for a teacher in a school.
     * @param aProfile The profile for which this is valid.
     * @param callBack 
     */    
    public void getTeachersResults(DomContext domContext, DomDwoProfile aProfile, AsyncCallback<DomResultsPerTeacher> callBack) {
        RestDwoProfile restPut = new RestDwoProfile();
        restPut.setRestContext(domContext);
        restPut.setDomDwoProfile(aProfile);
        service.getTeachersResults(restPut, new Callback<DomResultsPerTeacher>(callBack));
    }
    
}
