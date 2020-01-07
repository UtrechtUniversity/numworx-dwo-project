package fi.dwo.gwt.lib.rest.CallManagers;


import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

import fi.dwo.gwt.lib.rest.client.RestCallers.SecuredUserSchoolLoginRestCaller;
import fi.dwo.gwt.lib.rest.util.PromiseCallback;
import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.dom.entities.DomNewSchoolLogin;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolRoleAndClassV2;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolsRolesAndClassesV2;
import nl.uu.fi.dwo.rest.entities.RestNewSchoolLogin;
import nl.uu.fi.dwo.rest.entities.RestSchoolRoleAndClassV2;

import java.util.logging.Logger;

import org.fusesource.restygwt.client.MethodCallback;
import org.osgi.util.promise.Promise;

/**
 *
 * @author Gert van der Plas
 */
public class SecuredUserSchoolLoginManager {

    private SecuredUserSchoolLoginRestCaller service = GWT.create(SecuredUserSchoolLoginRestCaller.class);
    private static final Logger LOG = Logger.getLogger(SecuredUserSchoolLoginManager.class.getName());

//    public void updateAccountData(DomUserFull updateUser, AsyncCallback<DomUserFull> callBack) {
//        RestUserFull user = new RestUserFull();
//        user.setRestContext(new DomContext());
//        user.setDomUserFull(updateUser);
//        service.updateAccountData(user, new Callback<DomUserFull>(callBack));
//    }

    public Promise<DomSchoolsRolesAndClassesV2> getSchoolLogins() {
        PromiseCallback<DomSchoolsRolesAndClassesV2> defer = new PromiseCallback<DomSchoolsRolesAndClassesV2>();
        this.getSchoolLogins(defer);
        return defer.getPromise();
    }
    
    private void getSchoolLogins(MethodCallback<DomSchoolsRolesAndClassesV2> callBack)  {
        service.getSchoolLogins(callBack);
    }

    public Promise<DomSchoolRoleAndClassV2> switchToSchoolLogin(DomSchoolRoleAndClassV2 reqSrac) {
        PromiseCallback<DomSchoolRoleAndClassV2> defer = new PromiseCallback<DomSchoolRoleAndClassV2>();
        this.switchToSchoolLogin(reqSrac,defer);
        return defer.getPromise();
    }
    
    private void switchToSchoolLogin(DomSchoolRoleAndClassV2 reqSrac, MethodCallback<DomSchoolRoleAndClassV2> callBack){
        RestSchoolRoleAndClassV2 rsrc = new RestSchoolRoleAndClassV2();
        rsrc.setRestContext(new DomContext());
        rsrc.setDomSchoolRoleAndClass(reqSrac);
        service.switchToSchoolLogin(rsrc,callBack);
    }

    public Promise<Boolean> removeASchoolLogin(DomSchoolRoleAndClassV2 reqSrac) {
        PromiseCallback<Boolean> defer = new PromiseCallback<Boolean>();
        this.removeASchoolLogin(reqSrac,defer);
        return defer.getPromise();
    }
        
    
    private void removeASchoolLogin(DomSchoolRoleAndClassV2 reqSrac, MethodCallback<Boolean> callBack){
        RestSchoolRoleAndClassV2 rsrc = new RestSchoolRoleAndClassV2();
        rsrc.setRestContext(new DomContext());
        rsrc.setDomSchoolRoleAndClass(reqSrac);
        service.removeASchoolLogin(rsrc,callBack);
    }

    public Promise<Boolean> addASchoolLogin(DomNewSchoolLogin newSchoolLogin) {
        PromiseCallback<Boolean> defer = new PromiseCallback<Boolean>();
        this.addASchoolLogin(newSchoolLogin,defer);
        return defer.getPromise();
    }
            
    private void addASchoolLogin(DomNewSchoolLogin newSchoolLogin, MethodCallback<Boolean> callBack){
        RestNewSchoolLogin rnl = new RestNewSchoolLogin();
        rnl.setRestContext(new DomContext());
        rnl.setDomNewSchoolLogin(newSchoolLogin);
        service.addASchoolLogin(rnl,callBack);
    }
}
