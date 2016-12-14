/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.dwo.gwt.lib.rest.CallManagers;

import fi.dwo.gwt.lib.rest.util.RestAuthenticator;
import com.google.gwt.core.client.GWT;
import fi.dwo.gwt.lib.rest.GwtRestVars;
import fi.dwo.gwt.lib.rest.client.RestCallers.DWO2RestCaller;
import org.fusesource.restygwt.client.Defaults;
import org.fusesource.restygwt.client.dispatcher.DefaultFilterawareDispatcher;

/**
 *
 * @author Gert van der Plas
 */
public class SecuredUserSchoolLoginManager_old {

    private RestAuthenticator auth = RestAuthenticator.instance;
    private DWO2RestCaller service;

    public SecuredUserSchoolLoginManager_old() {
        this(GwtRestVars.instance().getServer());
    }

    public SecuredUserSchoolLoginManager_old(String url) {
        Defaults.setServiceRoot(url);
        Defaults.setDispatcher(DefaultFilterawareDispatcher.singleton());    
        service = GWT.create(DWO2RestCaller.class);

    }

//    public void setAuthentication(String u, String p) {
//        auth.setCredentials(u, p);
//    }
//
//    public void loginCheck(final String username, final String password, final AsyncCallback<Boolean> callback) {
//        DomLoginCheck domLoginCheck = new DomLoginCheck();
//        domLoginCheck.setUsername(username);
//        domLoginCheck.setPassword(DomLoginCheck.crypt(password));
//        RestLoginCheck restLoginCheck = new RestLoginCheck();
//        restLoginCheck.setDomLoginCheck(domLoginCheck);
//        setAuthentication(null, null);
//        service.loginCheck(restLoginCheck, new MethodCallback<Boolean>() {
//
//            @Override
//            public void onSuccess(Method method, Boolean response) {
//                if (Boolean.TRUE.equals(response)) {
//                    setAuthentication(username, password);
//                }
//                callback.onSuccess(response);
//            }
//
//            @Override
//            public void onFailure(Method method, Throwable exception) {
//                callback.onFailure(exception);
//            }
//        });
//
//    }
//
//    public void getCurrentUser(AsyncCallback<DomUserFull> callback) {
//        service.getAccountData(new Callback<DomUserFull>(callback));
//    }
//
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
//
//    public void login(String name, String password, final AsyncCallback<DomUserFull> callback) {
//        final String pwmd5 = MD5.md5(password);
//        GWT.log(pwmd5);
//
//        loginCheck(name, pwmd5, new AsyncCallback<Boolean>() {
//
//            @Override
//            public void onSuccess(Boolean result) {
//                if (Boolean.TRUE.equals(result)) {
//                    getCurrentUser(new AsyncCallback<DomUserFull>() {
//
//                        @Override
//                        public void onFailure(Throwable caught) {
//                            callback.onFailure(caught);
//                        }
//
//                        @Override
//                        public void onSuccess(DomUserFull result) {
//                            callback.onSuccess(result);
//                        }
//                    });
//                } else {
//                    callback.onFailure(new RuntimeException("LoginException"));
//                }
//            }
//
//            @Override
//            public void onFailure(Throwable caught) {
//                callback.onFailure(caught);
//            }
//        });
//
//    }
//        
}
