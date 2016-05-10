package fi.dwo.gwt.lib.rest.CallManagers;


import org.fusesource.restygwt.client.Defaults;
import org.fusesource.restygwt.client.Method;
import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.client.dispatcher.DefaultFilterawareDispatcher;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import fi.dwo.gwt.lib.rest.DwoGlobalVars;
import fi.dwo.gwt.lib.rest.client.DWO2RestCaller;
import fi.dwo.rest.dom.entities.DomContext;

import fi.dwo.rest.dom.entities.DomLoginCheck;
import fi.dwo.rest.dom.entities.DomUserFull;
import fi.dwo.rest.entities.RestLoginCheck;
import fi.dwo.rest.entities.RestUserFull;

public class SecuredUserAccountManager {

    private DWO2RestCaller service;

    public SecuredUserAccountManager() {
        this(DwoGlobalVars.instance().getServer());
    }

    public SecuredUserAccountManager(String url) {
        Defaults.setServiceRoot(url);
        Defaults.setDispatcher(DefaultFilterawareDispatcher.singleton());
        DefaultFilterawareDispatcher.singleton().addFilter(DwoGlobalVars.instance().getAuthenticator());
        service = GWT.create(DWO2RestCaller.class);

    }
    
/********************************************************************************
*   Extra login functions, For Resty
* 
********************************************************************************/
    /**
     * 
     * 
     * @param username
     * @param password
     * @param callback 
     */
    
    public void loginCheck(final String username, final String password, final AsyncCallback<Boolean> callback) {
        DomLoginCheck domLoginCheck = new DomLoginCheck();
        domLoginCheck.setUsername(username);
        domLoginCheck.setPassword(DomLoginCheck.crypt(password));
        RestLoginCheck restLoginCheck = new RestLoginCheck();
        restLoginCheck.setDomLoginCheck(domLoginCheck);
        DwoGlobalVars.instance().getAuthenticator().setCredentials(null, null);
        service.loginCheck(restLoginCheck, new MethodCallback<Boolean>() {

            @Override
            public void onSuccess(Method method, Boolean response) {
                if (Boolean.TRUE.equals(response)) {
                    DwoGlobalVars.instance().getAuthenticator().setCredentials(username, password);
                }
                callback.onSuccess(response);
            }

            @Override
            public void onFailure(Method method, Throwable exception) {
                callback.onFailure(exception);
            }
        });

    }

//    void getSchoolLogins(AsyncCallback<DomSchoolsRolesAndClasses> callback) {
//        service.getSchoolLogins(new Callback<DomSchoolsRolesAndClasses>(callback));
//    }
//
//    void toProfile(DomSchoolsRolesAndClasses result, Map<String, Object> profile) {
//        DomSchoolRoleAndClass active = result.getActiveSchoolRoleAndClass();
//        PersistenceId userId = active.getUserId();
//        PersistenceId classId = active.getSchoolClassId();
//        PersistenceId schoolId = active.getSchoolId();
//        PersistenceId sgId = active.getSchoolGroupId();
//
//        profile.put("userID", PersistenceIdDecoderInterface.instance.idOf(userId, PersistenceClassType.PersistentUser));
//        profile.put("iconizer", active.getIconizer());
//        profile.put("classID", classId == null ? ""
//                : PersistenceIdDecoderInterface.instance.idOf(classId, PersistenceClassType.PersistentSchoolClass));
//        profile.put("schoolID", schoolId == null ? ""
//                : PersistenceIdDecoderInterface.instance.idOf(schoolId, PersistenceClassType.PersistentSchool));
//        profile.put("schoolName", active.getSchoolName());
//        profile.put("groupname", active.getRoleName());
//        profile.put("class", active.getSchoolClassName());
//        profile.put("groupID", PersistenceIdDecoderInterface.instance.idOf(active.getRoleId(), PersistenceClassType.PersistentRole));
//        profile.put("schoolGroupID", PersistenceIdDecoderInterface.instance.idOf(sgId, PersistenceClassType.PersistentSchoolGroup));
//    }
//
//    public void toProfile(DomUserFull result, Map<String, Object> profile) {
//        profile.put("firstname", result.getGivenName());
//        profile.put("middlename", result.getInsertion());
//        profile.put("lastname", result.getFamilyName());
//        profile.put("userID", PersistenceIdDecoderInterface.instance.idOf(result.getId(), PersistenceClassType.PersistentUser));
//        profile.put("username", result.getUserName());
//    }
    public void login(String name, String password, final AsyncCallback<DomUserFull> callback) {
        final String pwmd5 = MD5.md5(password);
        GWT.log(pwmd5);

        loginCheck(name, pwmd5, new AsyncCallback<Boolean>() {

            @Override
            public void onSuccess(Boolean result) {
                if (Boolean.TRUE.equals(result)) {
                    getAccountData(new AsyncCallback<DomUserFull>() {

                        @Override
                        public void onFailure(Throwable caught) {
                            callback.onFailure(caught);
                        }

                        @Override
                        public void onSuccess(DomUserFull result) {
                            callback.onSuccess(result);
                        }
                    });
                } else {
                    callback.onFailure(new RuntimeException("LoginException"));
                }
            }

            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }
        });

    }

/********************************************************************************
*   Interface login stuff
* 
********************************************************************************/

    /**
     * 
     * @param updateUser
     * @param callBack 
     */
    public void updateAccountData(DomUserFull updateUser, AsyncCallback<DomUserFull> callBack) {
        RestUserFull user = new RestUserFull();
        user.setRestContext(new DomContext());
        user.setDomUserFull(updateUser);
        service.updateAccountData(user, new Callback<DomUserFull>(callBack));
    }

    /**
     * 
     * @param callBack 
     */
    public void getAccountData(AsyncCallback<DomUserFull> callBack) {
        service.getAccountData(new Callback<DomUserFull>(callBack));
    }

}
