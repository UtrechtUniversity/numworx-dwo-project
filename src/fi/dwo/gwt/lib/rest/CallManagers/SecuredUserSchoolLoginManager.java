/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.dwo.gwt.lib.rest.CallManagers;

import fi.dwo.gwt.lib.rest.util.RestAuthenticator;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import fi.dwo.gwt.lib.rest.DwoGlobalVars;
import fi.dwo.gwt.lib.rest.client.DWO2RestCaller;
import fi.dwo.gwt.lib.rest.util.PersistenceIdDecoderInterface;
import fi.dwo.rest.dom.entities.DomLoginCheck;
import fi.dwo.rest.dom.entities.DomSchoolRoleAndClass;
import fi.dwo.rest.dom.entities.DomSchoolsRolesAndClasses;
import fi.dwo.rest.dom.entities.DomUserFull;
import fi.dwo.rest.entities.RestLoginCheck;
import fi.dwo.rest.persistence.PersistenceClassType;
import fi.dwo.rest.persistence.PersistenceId;
import java.util.Map;
import org.fusesource.restygwt.client.Defaults;
import org.fusesource.restygwt.client.Method;
import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.client.dispatcher.DefaultFilterawareDispatcher;

/**
 *
 * @author Gert van der Plas
 */
public class SecuredUserSchoolLoginManager {

    private RestAuthenticator auth = new RestAuthenticator();
    private DWO2RestCaller service;

    public SecuredUserSchoolLoginManager() {
        this(DwoGlobalVars.instance().getServer());
    }

    public SecuredUserSchoolLoginManager(String url) {
        Defaults.setServiceRoot(url);
        Defaults.setDispatcher(DefaultFilterawareDispatcher.singleton());
        DefaultFilterawareDispatcher.singleton().addFilter(auth);
        service = GWT.create(DWO2RestCaller.class);

    }

    public void setAuthentication(String u, String p) {
        auth.setCredentials(u, p);
    }

    public void loginCheck(final String username, final String password, final AsyncCallback<Boolean> callback) {
        DomLoginCheck domLoginCheck = new DomLoginCheck();
        domLoginCheck.setUsername(username);
        domLoginCheck.setPassword(DomLoginCheck.crypt(password));
        RestLoginCheck restLoginCheck = new RestLoginCheck();
        restLoginCheck.setDomLoginCheck(domLoginCheck);
        setAuthentication(null, null);
        service.loginCheck(restLoginCheck, new MethodCallback<Boolean>() {

            @Override
            public void onSuccess(Method method, Boolean response) {
                if (Boolean.TRUE.equals(response)) {
                    setAuthentication(username, password);
                }
                callback.onSuccess(response);
            }

            @Override
            public void onFailure(Method method, Throwable exception) {
                callback.onFailure(exception);
            }
        });

    }

    public void getCurrentUser(AsyncCallback<DomUserFull> callback) {
        service.getAccountData(new Callback<DomUserFull>(callback));
    }

    void getSchoolLogins(AsyncCallback<DomSchoolsRolesAndClasses> callback) {
        service.getSchoolLogins(new Callback<DomSchoolsRolesAndClasses>(callback));
    }

    void toProfile(DomSchoolsRolesAndClasses result, Map<String, Object> profile) {
        DomSchoolRoleAndClass active = result.getActiveSchoolRoleAndClass();
        PersistenceId userId = active.getUserId();
        PersistenceId classId = active.getSchoolClassId();
        PersistenceId schoolId = active.getSchoolId();
        PersistenceId sgId = active.getSchoolGroupId();

        profile.put("userID", PersistenceIdDecoderInterface.instance.idOf(userId, PersistenceClassType.PersistentUser));
        profile.put("iconizer", active.getIconizer());
        profile.put("classID", classId == null ? ""
                : PersistenceIdDecoderInterface.instance.idOf(classId, PersistenceClassType.PersistentSchoolClass));
        profile.put("schoolID", schoolId == null ? ""
                : PersistenceIdDecoderInterface.instance.idOf(schoolId, PersistenceClassType.PersistentSchool));
        profile.put("schoolName", active.getSchoolName());
        profile.put("groupname", active.getRoleName());
        profile.put("class", active.getSchoolClassName());
        profile.put("groupID", PersistenceIdDecoderInterface.instance.idOf(active.getRoleId(), PersistenceClassType.PersistentRole));
        profile.put("schoolGroupID", PersistenceIdDecoderInterface.instance.idOf(sgId, PersistenceClassType.PersistentSchoolGroup));
    }

    public void toProfile(DomUserFull result, Map<String, Object> profile) {
        profile.put("firstname", result.getGivenName());
        profile.put("middlename", result.getInsertion());
        profile.put("lastname", result.getFamilyName());
        profile.put("userID", PersistenceIdDecoderInterface.instance.idOf(result.getId(), PersistenceClassType.PersistentUser));
        profile.put("username", result.getUserName());
    }

    public void login(String name, String password, final AsyncCallback<DomUserFull> callback) {
        final String pwmd5 = MD5.md5(password);
        GWT.log(pwmd5);

        loginCheck(name, pwmd5, new AsyncCallback<Boolean>() {

            @Override
            public void onSuccess(Boolean result) {
                if (Boolean.TRUE.equals(result)) {
                    getCurrentUser(new AsyncCallback<DomUserFull>() {

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
        
}
