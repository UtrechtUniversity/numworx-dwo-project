/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.dwo.gwt.lib.rest.CallManagers;

import fi.dwo.gwt.lib.rest.util.RestAuthenticator;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

import fi.dwo.gwt.lib.rest.client.SecuredUserSchoolLoginRestCaller;
import fi.dwo.rest.dom.entities.DomContext;
import fi.dwo.rest.dom.entities.DomNewSchoolLogin;
import fi.dwo.rest.dom.entities.DomSchoolRoleAndClass;
import fi.dwo.rest.dom.entities.DomSchoolsRolesAndClasses;
import fi.dwo.rest.entities.RestNewSchoolLogin;
import fi.dwo.rest.entities.RestSchoolRoleAndClass;
import fi.dwo.rest.exceptions.Dwo2Exception;

import java.util.logging.Logger;

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

    public void getSchoolLogins(AsyncCallback<DomSchoolsRolesAndClasses> callBack)  {
        service.getSchoolLogins(new Callback<DomSchoolsRolesAndClasses> (callBack));
    }

    public void switchToSchoolLogin(DomSchoolRoleAndClass reqSrac, AsyncCallback<DomSchoolRoleAndClass> callBack){
        RestSchoolRoleAndClass rsrc = new RestSchoolRoleAndClass();
        rsrc.setRestContext(new DomContext());
        rsrc.setDomSchoolRoleAndClass(reqSrac);
        service.switchToSchoolLogin(rsrc,new Callback<DomSchoolRoleAndClass> (callBack));
    }

    public void removeASchoolLogin(DomSchoolRoleAndClass reqSrac, AsyncCallback<Boolean> callBack){
        RestSchoolRoleAndClass rsrc = new RestSchoolRoleAndClass();
        rsrc.setRestContext(new DomContext());
        rsrc.setDomSchoolRoleAndClass(reqSrac);
        service.removeASchoolLogin(rsrc,new Callback<Boolean> (callBack));
    }
    
    public void addASchoolLogin(DomNewSchoolLogin newSchoolLogin, AsyncCallback<Boolean> callBack){
        RestNewSchoolLogin rnl = new RestNewSchoolLogin();
        rnl.setRestContext(new DomContext());
        rnl.setDomNewSchoolLogin(newSchoolLogin);
        service.addASchoolLogin(rnl,new Callback<Boolean> (callBack));
    }
}
