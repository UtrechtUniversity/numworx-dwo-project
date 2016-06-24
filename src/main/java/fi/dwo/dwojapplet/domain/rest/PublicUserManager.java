package fi.dwo.dwojapplet.domain.rest;

import com.owlike.genson.Genson;
import fi.dwo.rest.exceptions.Dwo2Exception;
import fi.dwo.dwojapplet.REST.StoredRestManager;
import fi.dwo.dwojapplet.domain.DwoHelper;
import fi.dwo.rest.dom.entities.DomContext;
import fi.dwo.rest.dom.entities.DomNewUser;
import fi.dwo.rest.dom.entities.DomSamlUser;
import fi.dwo.rest.dom.entities.DomUserFull;
import fi.dwo.rest.entities.RestNewUser;
import fi.dwo.rest.entities.RestSamlUser;
import fi.dwo.rest.exceptions.Dwo2ExceptionCode;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Base64;
import java.util.logging.Logger;

/**
 * Manages the user profile.
 *
 * @author G.A.J. van der Plas
 */
public class PublicUserManager {

    private static final Logger LOG = Logger.getLogger(PublicUserManager.class.getName());

    /**
     * DigestLogin
     *
     * @param username
     * @param password
     * @return
     * @throws Dwo2Exception
     */
    public static DomUserFull basicLogin(String username, String password) throws Dwo2Exception {
        try {
            DomUserFull user;
            //http://stackoverflow.com/questions/2793150/using-java-net-urlconnection-to-fire-and-handle-http-requests
            URL url = new URL(DwoHelper.getServerUrlPath().toString() + "rest/secure/user/account/get");
            URLConnection conn = url.openConnection();
            conn.setRequestProperty("Accept-Charset", "UTF-8");
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())));

            String output;
            StringBuilder json = new StringBuilder();
            while ((output = br.readLine()) != null) {
                json.append(output);
            }
            //decode JSON
            Genson genson = new Genson();

//          LIST EXAMPLE: List<DomUserFull> user = genson.deserialize(json.toString(), new GenericType<List<DomUserFull>>(){});
            user = genson.deserialize(json.toString(), DomUserFull.class);
//            StoredRestManager.setBasicAuthString(authString);
            //Set current user for domain
            DwoHelper.setCurrentUser(user);
            return user;
        } catch (MalformedURLException e) {
            throw new Dwo2Exception(Dwo2ExceptionCode.Rest_InternalError, "Malformed URL");

        } catch (IOException e) {
            throw new Dwo2Exception(Dwo2ExceptionCode.Rest_InternalError, "Server error");
        }
    }
////        //request a
////        //login to rest service, note there is usually not yet be a fully configured StoredRestManager.
//        DomUserFull user = null;
//        HttpURLConnection conn;
//        try {
//            SimpleDigestHttpClient client = new SimpleDigestHttpClient(DwoHelper.getServerUrlPath(), username, password);
//
//            conn = client.digestGet("rest/secure/user/account/get");
//
//            if (conn.getResponseCode() != 200) {                
//                throw new Dwo2Exception(Dwo2ExceptionCode.User_AuthenticationError, conn.getResponseMessage());
//            }
//
//            BufferedReader br = new BufferedReader(new InputStreamReader(
//                    (conn.getInputStream())));
//
//            String output;
//            StringBuilder json = new StringBuilder();
//            while ((output = br.readLine()) != null) {
//                json.append(output);
//            }
//            conn.disconnect();
//            //decode JSON
//            Genson genson = new Genson();
//
////          LIST EXAMPLE: List<DomUserFull> user = genson.deserialize(json.toString(), new GenericType<List<DomUserFull>>(){});
//            user = genson.deserialize(json.toString(), DomUserFull.class);
////            StoredRestManager.setBasicAuthString(authString);
//            //Set current user for domain
//            DwoHelper.setCurrentUser(user);
//            return user;
//        } catch (MalformedURLException e) {
//            throw new Dwo2Exception(Dwo2ExceptionCode.Rest_InternalError, "Malformed URL");
//
//        } catch (IOException e) {
//            throw new Dwo2Exception(Dwo2ExceptionCode.Rest_InternalError, "Server error");
//        }
//    }

    public static DomUserFull samlLogin(String user_id, String org_id, String authToken) throws Dwo2Exception {
        DomUserFull user;
        RestSamlUser samlRestUser = new RestSamlUser();
        DomSamlUser samlUser = new DomSamlUser();
        samlUser.setSamlUserId(user_id);
        samlUser.setSamlOrgId(org_id);
        samlUser.setAuthToken(authToken);
        samlRestUser.setDomSamlUser(samlUser);
        try {
            URL url = new URL(DwoHelper.getServerUrlPath().toString() + "rest/public/user/submitSaml"); //TODO make login
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("PUT");
            conn.setRequestProperty("Accept", "application/json");
            conn.setUseCaches(false);

            if (conn.getResponseCode() != 200) {
                throw new Dwo2Exception(Dwo2ExceptionCode.User_AuthenticationError, conn.getResponseMessage());
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())));

            String output;
            StringBuilder json = new StringBuilder();
            while ((output = br.readLine()) != null) {
                json.append(output);
                System.out.println(output);
            }
            conn.disconnect();
            //decode JSON
            Genson genson = new Genson();

            user = genson.deserialize(json.toString(), DomUserFull.class);
            String authString = user.getUserName() + ":" + user.getPassword();
            authString = "Basic " + Base64.getEncoder().encodeToString(authString.getBytes());
            StoredRestManager.setBasicAuthString(authString);
            return user;
        } catch (MalformedURLException e) {
            throw new Dwo2Exception(Dwo2ExceptionCode.Rest_InternalError, "Malformed URL");

        } catch (IOException e) {
            throw new Dwo2Exception(Dwo2ExceptionCode.Rest_InternalError, "Server error");
        }
    }

    public static boolean RegisterNewUser(DomNewUser newUserReg) throws Dwo2Exception {
        boolean r;
        RestNewUser restNewUserReg = new RestNewUser();
        restNewUserReg.setRestContext(new DomContext());
        restNewUserReg.setDomNewUser(newUserReg);

        r = StoredRestManager.getInstance().put("rest/public/user/submit", Boolean.class, restNewUserReg);

        return r;
    }

}
