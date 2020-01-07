package fi.dwo.gwt.lib.rest.CallManagers;

import com.google.gwt.core.client.GWT;
import fi.dwo.gwt.lib.rest.GwtRestVars;
import fi.dwo.gwt.lib.rest.client.RestCallers.PublicStatusRestCaller;
import fi.dwo.gwt.lib.rest.util.PromiseCallback;
import nl.uu.fi.dwo.rest.dom.entities.DomHeartBeat;

import org.fusesource.restygwt.client.MethodCallback;
import org.osgi.util.promise.Promise;

public class PublicStatusManager {

    PublicStatusRestCaller caller = GWT.create(PublicStatusRestCaller.class);

    GwtRestVars instance;

    public Promise<DomHeartBeat> getHeartBeat() {
        PromiseCallback<DomHeartBeat> defer = new PromiseCallback<DomHeartBeat>();
        this.getHeartBeat(defer);
        return defer.getPromise();
    }

    private void getHeartBeat(MethodCallback<DomHeartBeat> asyncCallback) {
        caller.getHeartBeat(asyncCallback);
    }
    
    public PublicStatusManager() {
        instance = GwtRestVars.instance();
    }
}
