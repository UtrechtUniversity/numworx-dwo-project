package fi.dwo.gwt.lib.rest.CallManagers;

import com.google.gwt.core.client.GWT;

import fi.dwo.gwt.lib.rest.GwtRestVars;
import fi.dwo.gwt.lib.rest.client.RestCallers.PublicUserRestCaller;
import fi.dwo.gwt.lib.rest.util.PromiseCallback;
import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.dom.entities.DomNewUser;
import nl.uu.fi.dwo.rest.entities.RestNewUser;

import org.fusesource.restygwt.client.MethodCallback;
import org.osgi.util.promise.Promise;

public class PublicUserManager {

    PublicUserRestCaller caller = GWT.create(PublicUserRestCaller.class);

    public Promise<Boolean> RegisterNewUser(DomNewUser domNewUser) {
        PromiseCallback<Boolean> defer = new PromiseCallback<Boolean>();
        this.RegisterNewUser(domNewUser, defer);
        return defer.getPromise();
    }

    private void RegisterNewUser(DomNewUser domNewUser,
            MethodCallback<Boolean> asyncCallback) {
        RestNewUser user = new RestNewUser();
        user.setDomNewUser(domNewUser);
        user.setRestContext(new DomContext());
        instance.setCurrentUser(null,null);
        caller.submitNewUser(user, asyncCallback);

    }

    GwtRestVars instance;

    public PublicUserManager() {
        instance = GwtRestVars.instance();
    }
}
