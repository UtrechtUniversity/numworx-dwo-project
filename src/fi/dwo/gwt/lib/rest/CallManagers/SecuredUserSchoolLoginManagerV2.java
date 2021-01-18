package fi.dwo.gwt.lib.rest.CallManagers;


import com.google.gwt.core.client.GWT;
import static fi.dwo.gwt.lib.rest.GwtRestVars.F;
import fi.dwo.gwt.lib.rest.client.RestCallers.SecuredUserSchoolLoginRestCallerV2;
import fi.dwo.gwt.lib.rest.util.PromiseCallback;

import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.dom.entities.DomLoginContext;
import nl.uu.fi.dwo.rest.dom.entities.DomNewSchoolLogin;
import nl.uu.fi.dwo.rest.entities.RestNewSchoolLogin;

import java.util.logging.Logger;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolRoleAndClassV2;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolsRolesAndClassesV2;
import nl.uu.fi.dwo.rest.entities.RestSchoolRoleAndClassV2;
import nl.uu.fi.dwo.rest.util.PathId;

import org.fusesource.restygwt.client.MethodCallback;
import org.osgi.util.promise.Promise;

/**
 *
 * @author Gert van der Plas
 */
public class SecuredUserSchoolLoginManagerV2 {

    private SecuredUserSchoolLoginRestCallerV2 service = GWT.create(SecuredUserSchoolLoginRestCallerV2.class);
    private static final Logger LOG = Logger.getLogger(SecuredUserSchoolLoginManagerV2.class.getName());

//    public void updateAccountData(DomUserFull updateUser, AsyncCallback<DomUserFull> callBack) {
//        RestUserFull user = new RestUserFull();
//        user.setRestContext(new DomContext());
//        user.setDomUserFull(updateUser);
//        service.updateAccountData(user, new Callback<DomUserFull>(callBack));
//    }

    public SecuredUserSchoolLoginManagerV2(){
        service = (SecuredUserSchoolLoginRestCallerV2) GWT.create(SecuredUserSchoolLoginRestCallerV2.class);
    }

    public Promise<DomSchoolsRolesAndClassesV2> getSchoolLogins() {
        PromiseCallback<DomSchoolsRolesAndClassesV2> defer = new PromiseCallback<DomSchoolsRolesAndClassesV2>();
        this.getSchoolLoginsV2(defer);
        return defer.getPromise();
    }
 
    public Promise<DomSchoolsRolesAndClassesV2> getSchoolLogins(DomContext context) {
      PromiseCallback<DomSchoolsRolesAndClassesV2> defer = new PromiseCallback<DomSchoolsRolesAndClassesV2>();
      this.getSchoolLoginsV2(context, defer);
      return defer.getPromise();
  }
        
    private void getSchoolLoginsV2(MethodCallback<DomSchoolsRolesAndClassesV2> callBack)  {
        F( (id, arg, c) -> service.getSchoolLogins(c), null, null, callBack);
    }

    private void getSchoolLoginsV2(DomContext context, MethodCallback<DomSchoolsRolesAndClassesV2> callBack)  {     
      F( (id, arg, c) -> service.getSchoolLogins(id, c), PathId.getId(context), null, callBack);
  }

    public Promise<DomSchoolRoleAndClassV2> switchToSchoolLogin(DomSchoolRoleAndClassV2 reqSrac) {
        PromiseCallback<DomSchoolRoleAndClassV2> defer = new PromiseCallback<DomSchoolRoleAndClassV2>();
        this.switchToSchoolLogin(reqSrac,defer);
        return defer.getPromise();
    }
        
    private void switchToSchoolLogin(DomSchoolRoleAndClassV2 reqSrac, MethodCallback<DomSchoolRoleAndClassV2> callBack){
        RestSchoolRoleAndClassV2 rsrc = new RestSchoolRoleAndClassV2();
        DomContext context = new DomContext();
        context.setDomHasRole(reqSrac.getHasRole());
		rsrc.setRestContext(context);
        rsrc.setDomSchoolRoleAndClass(reqSrac);
        F( (id, arg, c) -> service.switchToSchoolLogin(id, arg, c), PathId.getId(context), rsrc, callBack);
        
    }

    public Promise<Boolean> removeASchoolLogin(DomContext domContext, DomSchoolRoleAndClassV2 reqSrac) {
        PromiseCallback<Boolean> defer = new PromiseCallback<Boolean>();
        this.removeASchoolLogin(domContext, reqSrac,defer);
        return defer.getPromise();
    }
        
    
    private void removeASchoolLogin(DomContext domContext, DomSchoolRoleAndClassV2 reqSrac, MethodCallback<Boolean> callBack){
        RestSchoolRoleAndClassV2 rsrc = new RestSchoolRoleAndClassV2();
        rsrc.setRestContext(domContext);
        rsrc.setDomSchoolRoleAndClass(reqSrac);
        F( (id, arg, c) -> service.removeASchoolLogin(id, arg,c), PathId.getId(domContext), rsrc, callBack);
    }
    
    public Promise<Boolean> addASchoolLogin(DomContext context, DomNewSchoolLogin newSchoolLogin) {
        PromiseCallback<Boolean> defer = new PromiseCallback<Boolean>();
        this.addASchoolLogin(context, newSchoolLogin,defer);
        return defer.getPromise();
    }    

    private void addASchoolLogin(DomContext context, DomNewSchoolLogin newSchoolLogin, MethodCallback<Boolean> callBack){
        RestNewSchoolLogin rnl = new RestNewSchoolLogin();
        rnl.setRestContext(context);
        rnl.setDomNewSchoolLogin(newSchoolLogin);
        F( (id, arg,c ) -> service.addASchoolLogin(id, arg,c), PathId.getId(context), rnl, callBack);
    }
}
