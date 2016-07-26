package fi.dwo.dwojapplet.domain.rest;

import com.owlike.genson.Genson;
import fi.dwo.rest.dom.entities.DomContext;
import fi.dwo.rest.dom.entities.DomUserFull;
import fi.dwo.rest.exceptions.Dwo2Exception;
import fi.dwo.rest.entities.RestUserFull;
import fi.dwo.dwojapplet.REST.StoredRestManager;
import fi.dwo.dwojapplet.domain.DwoHelper;
import fi.dwo.rest.dom.entities.DomLoginContext;
import fi.dwo.rest.entities.RestLoginContext;
import fi.dwo.rest.exceptions.Dwo2ExceptionCode;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Authenticator;
import java.net.CookieManager;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Manages the user profile.
 *
 * @author G.A.J. van der Plas
 */
public class SecureUserAccountManager {

    private static final Logger LOG = Logger.getLogger(SecureUserAccountManager.class.getName());

    /**
     * Returns the current user 'logged in'. The information is extracted from
     * the security context which depends on the credentials used for accessing
     * the rest interface. Technically it should be equal to the data in the
     * DwoHelper.
     *
     * @return
     * @throws fi.dwo.rest.exceptions.Dwo2Exception
     */
    public static DomUserFull getAccountData() throws Dwo2Exception {
        DomUserFull user;
        user = StoredRestManager.getInstance().get("rest/secure/user/account/get", DomUserFull.class);
        return user;
    }

    public static DomLoginContext getLoginContext(String username, String password) throws Dwo2Exception {
            DomLoginContext loginContext;
            Authenticator.setDefault(null);
            CookieManager.setDefault(null);
            try{
            URL url = new URL(DwoHelper.getServerUrlPath().toString() + "rest/secure/user/account/getLoginContext"); //TODO make basicLogin            
            String authString = username + ":" + password;
            authString = "Basic " + Base64.getEncoder().encodeToString(authString.getBytes());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestProperty("Authorization", authString);
            conn.addRequestProperty("Cookie", "");
            conn.setUseCaches(false);
            conn.setAllowUserInteraction(false);
            conn.connect();

            if (conn.getResponseCode() != 200) {
                throw new Dwo2Exception(Dwo2ExceptionCode.User_AuthenticationError, conn.getResponseMessage());
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())));

            String output;
            StringBuilder json = new StringBuilder();
            while ((output = br.readLine()) != null) {
                json.append(output);
            }
            br.close();
            conn.disconnect();
//            Authenticator.setDefault(null);
            //decode JSON
            Genson genson = new Genson();

//          LIST EXAMPLE: List<DomUserFull> user = genson.deserialize(json.toString(), new GenericType<List<DomUserFull>>(){});
            loginContext = genson.deserialize(json.toString(), DomLoginContext.class);        
            return loginContext;
        } catch (MalformedURLException e) {
            throw new Dwo2Exception(Dwo2ExceptionCode.Rest_InternalError, "Malformed URL");

        } catch (IOException e) {
            if(e.getClass().equals(java.net.ConnectException.class)){
                throw new Dwo2Exception(Dwo2ExceptionCode.Rest_ConnectionTimeout, e.getMessage());
            }else{
            throw new Dwo2Exception(Dwo2ExceptionCode.Rest_InternalError, e.getMessage());
            }
        }
            

    }
        
        
//    /**
//     * Login for a user. Registers service that the user is logging in. As the
//     * REST interface is stateless this is merely for gathering statistics.
//     *
//     * @return
//     * @throws fi.dwo.rest.exceptions.Dwo2Exception
//     */
//    public static DomUserFull loginUser() throws Dwo2Exception {
//        DomUserFull user;
//        user = StoredRestManager.getInstance().get("rest/secure/user/account/login", DomUserFull.class);
//        return user;
//    }

    /**
     * Registers that the user logs out. When doing basic authentication basicAuthLogout is 
     * recommended to be used.
     *
     * @return
     * @throws fi.dwo.rest.exceptions.Dwo2Exception
     */
    public static Boolean logoutUser(DomLoginContext domLoginContext) throws Dwo2Exception {
        Boolean result;
        RestLoginContext submit = new RestLoginContext();
        submit.setRestContext(new DomContext());
        submit.setDomLoginContext(domLoginContext);
        result = StoredRestManager.getInstance().put("rest/secure/user/account/logout", Boolean.class, submit);
        //ensures basic auth data and cookies are wiped from Java Browser-like framework
        Authenticator.setDefault(null);
        CookieManager.setDefault(null);
        StoredRestManager.setBasicAuthString(null);
        return result;
    }

    /**
     * Registers that the user logs out, clears client-side cookies and authenticator data,
     * and clears the session server-side.
     *
     * @return
     * @throws fi.dwo.rest.exceptions.Dwo2Exception
     */
    public static Boolean basicAuthLogout(DomLoginContext domLoginContext) throws Dwo2Exception {
        Boolean result;
        RestLoginContext submit = new RestLoginContext();
        submit.setRestContext(new DomContext());
        submit.setDomLoginContext(domLoginContext);
        result = StoredRestManager.getInstance().put("rest/secure/user/account/basicAuthLogout", Boolean.class, submit);
        //ensures basic auth data and cookies are wiped from Java Browser-like framework
        Authenticator.setDefault(null);
        CookieManager.setDefault(null);
        StoredRestManager.setBasicAuthString(null);
        return result;
    }
    
    /**
     * Updates the user profile of a user.
     *
     * Fields updated are email, password and the full name of the user. The
     * full name exists out of the first, insertion and family name.
     *
     * @param user
     * @return
     * @throws fi.dwo.rest.exceptions.Dwo2Exception
     */
    public static DomUserFull updateAccountData(DomUserFull user) throws Dwo2Exception {
        RestUserFull restUser = new RestUserFull();
        restUser.setRestContext(new DomContext());
        restUser.setDomUserFull(user);

        user = StoredRestManager.getInstance().put("rest/secure/user/account/update", DomUserFull.class, restUser);
//        Client client = ClientBuilder.newClient().register(feature);
//        WebTarget target = client.target(DwoHelper.getServerUrlPath().toString());
//        StoredRestManager.setWebTargetAndCredentials(target);
        StoredRestManager.setBasicAuthString(null);

        DwoHelper.setCurrentUser(user);
        LOG.log(Level.FINE, "Updated user profile of username {0}.", new Object[]{restUser.getDomUserFull().getUserName()});
        return user;
    }

    /**
     * Updates the user profile of a user.
     *
     * Fields updated are email, password and the full name of the user. The
     * full name exists out of the first, insertion and family name.
     *
     * @throws fi.dwo.rest.exceptions.Dwo2Exception
     */
    public static Boolean removeAccountData() throws Dwo2Exception {
        Boolean b;
        b = StoredRestManager.getInstance().get("rest/secure/user/account/remove", Boolean.class);
        return b;
    }
}
