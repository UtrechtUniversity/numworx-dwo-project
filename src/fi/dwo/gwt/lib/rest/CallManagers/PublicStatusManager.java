package fi.dwo.gwt.lib.rest.CallManagers;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

import fi.dwo.gwt.lib.rest.GwtRestVars;
import fi.dwo.gwt.lib.rest.client.RestCallers.PublicStatusRestCaller;
import fi.dwo.gwt.lib.rest.util.PromiseCallback;
import nl.uu.fi.dwo.rest.dom.entities.DomHeartBeat;
import org.osgi.util.promise.Promise;

public class PublicStatusManager {

    PublicStatusRestCaller caller = GWT.create(PublicStatusRestCaller.class);

    GwtRestVars instance;

    public Promise<DomHeartBeat> getHeartBeat() {
        PromiseCallback<DomHeartBeat> defer = new PromiseCallback<DomHeartBeat>();
        this.getHeartBeat(defer);
        return defer.getPromise();
    }

    public void getHeartBeat(AsyncCallback<DomHeartBeat> asyncCallback) {
        caller.getHeartBeat(new Callback<DomHeartBeat>(asyncCallback));
    }
    
    public PublicStatusManager() {
        instance = GwtRestVars.instance();
    }
}
