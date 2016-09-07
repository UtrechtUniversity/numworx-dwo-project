/* Copyrighted 2015. */
package fi.dwo.dwojapplet.REST;

import com.owlike.genson.GenericType;
import com.owlike.genson.Genson;

import static fi.dwo.dwojapplet.REST.RestManager.getBasicAuthString;
import fi.dwo.dwojapplet.domain.DwoHelper;
import fi.dwo.rest.dom.entities.DomRole;
import fi.dwo.rest.dom.entities.DomSchool4DwoAdmin;
import fi.dwo.rest.dom.entities.DomSchoolClass;
import fi.dwo.rest.dom.entities.DomSchoolAdmin;
import fi.dwo.rest.dom.entities.DomSchoolsRolesAndClasses;
import fi.dwo.rest.dom.entities.DomStudent;
import fi.dwo.rest.dom.entities.DomUser;
import fi.dwo.rest.dom.entities.DomTeacher;
import fi.dwo.rest.exceptions.Dwo2Exception;
import fi.dwo.rest.exceptions.Dwo2ExceptionCode;
import fi.dwo.rest.RestListClassTypes;
import static fi.dwo.rest.RestListClassTypes.DomSchool4DwoAdmin;
import fi.dwo.rest.dom.entities.DomTeacherAndHasRole;
import fi.dwo.rest.util.Dwo2ExceptionTranslator;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This is the plain and direct restManager. Please use the
 * {@Link StoredRestManager} to minimize memory use. Note this class
 * methods may be called asynchronous. Therefor methods from DWOHelper and
 * others should be handle async calls too.
 *
 * @author Gert van der Plas <gertvdplas@gmail.com>
 */
class RestManager {

    private static final Logger LOG = Logger.getLogger(RestManager.class.getName());

    private static final RestManager instance = new RestManager();
    private static String basicAuthString;

    /**
     * @return the instance
     */
    public static RestManager getInstance() {
        return instance;
    }

    /**
     * @return the basicAuthString
     */
    public static String getBasicAuthString() {
        return basicAuthString;
    }

    /**
     * @param data
     */
    public synchronized static void setBasicAuthString(String data) {
        // note that reference changes in Java are atomic.
        basicAuthString = data;
    }

    /**
     * @param username
     * @param password
     */
    public synchronized static void setBasicAuthString(String username, String password) {
        String authString = username + ":" + password;
        // note that reference changes in Java are atomic.
        basicAuthString = "Basic " + Base64.getEncoder().encodeToString(authString.getBytes());
    }

    /**
     * GET operation to the restful server.
     *
     * @param <T>
     * @param path sub context path servlet.
     * @param c Class type to return.
     * @return A list of class c objects.
     * @throws fi.dwo.rest.exceptions.Dwo2Exception
     */
    public <T> T get(String path, Class<T> c) throws Dwo2Exception {
        try {
            URL url = new URL(DwoHelper.getServerUrlPath().toString() + path); //TODO make login

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestProperty("Authorization", getBasicAuthString());
            conn.setUseCaches(false);

            if (conn.getResponseCode() != 200) {
                LOG.log(Level.WARNING, "Code: {0}. Reason: {1}", new Object[]{conn.getResponseCode(), conn.getResponseMessage()});
                Dwo2Exception e;
                if (conn.getResponseCode() == 400) {//Dwo2Exception
                    BufferedReader br = new BufferedReader(new InputStreamReader(
                            (conn.getErrorStream()), StandardCharsets.UTF_8));

                    String output;
                    StringBuilder json = new StringBuilder();
                    while ((output = br.readLine()) != null) {
                        json.append(output);
                    }
                    conn.disconnect();
                    e = new Dwo2Exception(Dwo2ExceptionTranslator.decodeCodeInJSON(json.toString()), Dwo2ExceptionTranslator.decodeMessageInJSON(json.toString()));
                } else {
                    //non-servlet generated exception has been sent. Convert to Dwo2RestException.
                    e = new Dwo2Exception(Dwo2ExceptionCode.Rest_InterfaceError, conn.getResponseMessage());
                }
                LOG.log(Level.WARNING, "Dwo2Code: {0}. Dwo2Reason: {1}", new Object[]{e.getDwo2Code().name(), e.getDwo2Message()});
                throw e;
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream()), StandardCharsets.UTF_8)); // XXX force UTF-8 encoding

            String output;
            StringBuilder json = new StringBuilder();
            while ((output = br.readLine()) != null) {
                json.append(output);
            }
            conn.disconnect();
            //decode JSON
            Genson genson = new Genson();
//            List<DomUserFull> user = genson.deserialize(json.toString(), new GenericType<List<DomUserFull>>(){});
            LOG.log(Level.FINEST, "Received: {0}", new Object[]{json.toString()});
            T result = genson.deserialize(json.toString(), c);
            return result;
        } catch (MalformedURLException e) {
            throw new Dwo2Exception(Dwo2ExceptionCode.Rest_InternalError, "Malformed URL");

        } catch (IOException e) {
            if (e.getClass().equals(java.net.ConnectException.class)) {
                throw new Dwo2Exception(Dwo2ExceptionCode.Rest_ConnectionTimeout, e.getMessage());
            } else {
                throw new Dwo2Exception(Dwo2ExceptionCode.Rest_InternalError, e.getMessage());
            }
        }
    }

    /**
     * GET operation to the restful server.
     *
     * @param <T>
     * @param path sub context path servlet.
     * @param type
     * @return A list of Class c.
     * @throws fi.dwo.rest.exceptions.Dwo2Exception
     */
    public <T> List<T> getList(String path, RestListClassTypes type) throws Dwo2Exception {
        try {
            URL url = new URL(DwoHelper.getServerUrlPath().toString() + path); //TODO make login

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestProperty("Accept-Encoding", "application/json");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Authorization", getBasicAuthString());
            conn.setUseCaches(false);

            if (conn.getResponseCode() != 200) {
                LOG.log(Level.WARNING, "Code: {0}. Reason{1}", new Object[]{conn.getResponseCode(), conn.getResponseMessage()});
                Dwo2Exception e;
                if (conn.getResponseCode() == 400) {//Dwo2Exception
                    BufferedReader br = new BufferedReader(new InputStreamReader(
                            (conn.getErrorStream()), StandardCharsets.UTF_8));

                    String output;
                    StringBuilder json = new StringBuilder();
                    while ((output = br.readLine()) != null) {
                        json.append(output);
                    }
                    conn.disconnect();
                    e = new Dwo2Exception(Dwo2ExceptionTranslator.decodeCodeInJSON(json.toString()), Dwo2ExceptionTranslator.decodeMessageInJSON(json.toString()));
                } else {
                    //non-servlet generated exception has been sent. Convert to Dwo2RestException.
                    e = new Dwo2Exception(Dwo2ExceptionCode.Rest_InterfaceError, conn.getResponseMessage());
                }
                LOG.log(Level.WARNING, "Dwo2Code: {0}. Dwo2Reason: {1}", new Object[]{e.getDwo2Code().name(), e.getDwo2Message()});
                throw e;
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream()), StandardCharsets.UTF_8));

            String output;
            StringBuilder json = new StringBuilder();
            while ((output = br.readLine()) != null) {
                json.append(output);
            }
            conn.disconnect();
            //decode JSON
            Genson genson = new Genson();
            LOG.log(Level.FINEST, "Received: {0}", new Object[]{json.toString()});
            switch (type) {
                case DomUser:
                    return (List<T>) genson.deserialize(json.toString(), new GenericType<List<DomUser>>() {
                    });
                case DomRole:
                    return (List<T>) genson.deserialize(json.toString(), new GenericType<List<DomRole>>() {
                    });
                case DomStudent:
                    return (List<T>) genson.deserialize(json.toString(), new GenericType<List<DomStudent>>() {
                    });
                case DomTeacher:
                    return (List<T>) genson.deserialize(json.toString(), new GenericType<List<DomTeacher>>() {
                    });
                case DomSchoolAdmin:
                    return (List<T>) genson.deserialize(json.toString(), new GenericType<List<DomSchoolAdmin>>() {
                    });
                case DomSchool4DwoAdmin:
                    return (List<T>) genson.deserialize(json.toString(), new GenericType<List<DomSchool4DwoAdmin>>() {
                    });
                case DomSchoolClass:
                    return (List<T>) genson.deserialize(json.toString(), new GenericType<List<DomSchoolClass>>() {
                    });
                case DomSchoolsRolesAndClasses:
                    return (List<T>) genson.deserialize(json.toString(), new GenericType<List<DomSchoolsRolesAndClasses>>() {
                    });
                default:
                    String msg = "Programming error, trying to get an unsupported dataType.";
                    LOG.log(Level.SEVERE, msg);
                    throw new Dwo2Exception(Dwo2ExceptionCode.Rest_InternalError, msg);
            }
        } catch (MalformedURLException e) {
            throw new Dwo2Exception(Dwo2ExceptionCode.Rest_InternalError, "Malformed URL");
        } catch (IOException e) {
            if (e.getClass().equals(java.net.ConnectException.class)) {
                throw new Dwo2Exception(Dwo2ExceptionCode.Rest_ConnectionTimeout, e.getMessage());
            } else {
                throw new Dwo2Exception(Dwo2ExceptionCode.Rest_InternalError, e.getMessage());
            }
        }
    }

    /**
     * GET operation to the restful server.
     *
     * @param <T>
     * @param path sub context path servlet.
     * @param c Class type to return.
     * @param o object of Class type c being send.
     * @return A list of class c objects.
     * @throws fi.dwo.rest.exceptions.Dwo2Exception
     */
    public <T> T put(String path, Class<T> c, Object o) throws Dwo2Exception { //due to genson now c is superflous
        try {
            URL url = new URL(DwoHelper.getServerUrlPath().toString() + path); //TODO make login
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            DataOutputStream outStream = null;
            conn.setRequestMethod("PUT");
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestProperty("Accept-Encoding", "application/json");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Authorization", getBasicAuthString());
            conn.setRequestProperty("Accept-Charset", "UTF-8");
//            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            outStream = new DataOutputStream(conn.getOutputStream());
            Genson genson = new Genson();
//            List<DomUserFull> user = genson.deserialize(json.toString(), new GenericType<List<DomUserFull>>(){});
            String jsonOut = genson.serialize(o);
            LOG.log(Level.FINEST, "Sending: {0}", new Object[]{jsonOut.toString()});
            outStream.write(jsonOut.getBytes("UTF-8"));
            outStream.close();
            if (conn.getResponseCode() != 200) {
                LOG.log(Level.WARNING, "Code: {0}. Reason{1}", new Object[]{conn.getResponseCode(), conn.getResponseMessage()});
                Dwo2Exception e;
                if (conn.getResponseCode() == 400) {//Dwo2Exception
//                    String json = conn.getResponseMessage();
                    BufferedReader br = new BufferedReader(new InputStreamReader(
                            (conn.getErrorStream()), StandardCharsets.UTF_8));

                    String output;
                    StringBuilder json = new StringBuilder();
                    while ((output = br.readLine()) != null) {
                        json.append(output);
                    }
                    conn.disconnect();
                    e = new Dwo2Exception(Dwo2ExceptionTranslator.decodeCodeInJSON(json.toString()), Dwo2ExceptionTranslator.decodeMessageInJSON(json.toString()));
                } else {
                    //non-servlet generated exception has been sent. Convert to Dwo2RestException.
                    e = new Dwo2Exception(Dwo2ExceptionCode.Rest_InterfaceError, conn.getResponseMessage());
                }
                LOG.log(Level.WARNING, "Dwo2Code: {0}. Dwo2Reason: {1}", new Object[]{e.getDwo2Code().name(), e.getDwo2Message()});
                throw e;
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream()), StandardCharsets.UTF_8));

            String output;
            StringBuilder json = new StringBuilder();
            while ((output = br.readLine()) != null) {
                json.append(output);
            }
            conn.disconnect();
            //decode JSON
//            List<DomUserFull> user = genson.deserialize(json.toString(), new GenericType<List<DomUserFull>>(){});
            LOG.log(Level.FINEST, "Received: {0}", new Object[]{json.toString()});
            T result = genson.deserialize(json.toString(), c);
            return result;
        } catch (MalformedURLException e) {
            throw new Dwo2Exception(Dwo2ExceptionCode.Rest_InternalError, "Malformed URL");

        } catch (IOException e) {
            if (e.getClass().equals(java.net.ConnectException.class)) {
                throw new Dwo2Exception(Dwo2ExceptionCode.Rest_ConnectionTimeout, e.getMessage());
            } else {
                throw new Dwo2Exception(Dwo2ExceptionCode.Rest_InternalError, e.getMessage());
            }
        }
    }

    /**
     * GET operation to the restful server.
     *
     * @param <T>
     * @param path sub context path servlet.
     * @param type
     * @param o
     * @return A list of Class c.
     * @throws fi.dwo.rest.exceptions.Dwo2Exception
     */
    public <T> List<T> getPutList(String path, RestListClassTypes type, Object o) throws Dwo2Exception {
        try {
            URL url = new URL(DwoHelper.getServerUrlPath().toString() + path); //TODO make login
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            DataOutputStream outStream = null;
            conn.setRequestMethod("PUT");
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestProperty("Accept-Encoding", "application/json");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Authorization", getBasicAuthString());
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            outStream = new DataOutputStream(conn.getOutputStream());
            Genson genson = new Genson();
//            List<DomUserFull> user = genson.deserialize(json.toString(), new GenericType<List<DomUserFull>>(){});
            String jsonOut = genson.serialize(o);
            LOG.log(Level.FINEST, "Sending: {0}", new Object[]{jsonOut.toString()});
            outStream.write(jsonOut.getBytes());
            outStream.close();
            if (conn.getResponseCode() != 200) {
                LOG.log(Level.WARNING, "Code: {0}. Reason{1}", new Object[]{conn.getResponseCode(), conn.getResponseMessage()});
                Dwo2Exception e;
                if (conn.getResponseCode() == 400) {//Dwo2Exception
                    BufferedReader br = new BufferedReader(new InputStreamReader(
                            (conn.getErrorStream()), StandardCharsets.UTF_8));

                    String output;
                    StringBuilder json = new StringBuilder();
                    while ((output = br.readLine()) != null) {
                        json.append(output);
                    }
                    conn.disconnect();
                    e = new Dwo2Exception(Dwo2ExceptionTranslator.decodeCodeInJSON(json.toString()), Dwo2ExceptionTranslator.decodeMessageInJSON(json.toString()));
                } else {
                    //non-servlet generated exception has been sent. Convert to Dwo2RestException.
                    e = new Dwo2Exception(Dwo2ExceptionCode.Rest_InterfaceError, conn.getResponseMessage());
                }
                LOG.log(Level.WARNING, "Dwo2Code: {0}. Dwo2Reason{1}", new Object[]{e.getDwo2Code().name(), e.getDwo2Message()});
                throw e;
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream()), StandardCharsets.UTF_8));

            String output;
            StringBuilder json = new StringBuilder();
            while ((output = br.readLine()) != null) {
                json.append(output);
            }
            conn.disconnect();
            LOG.log(Level.FINEST, "Received: {0}", new Object[]{json.toString()});
            //decode JSON
//            List<DomUserFull> user = genson.deserialize(json.toString(), new GenericType<List<DomUserFull>>(){});

            switch (type) {
                case DomUser:
                    return (List<T>) genson.deserialize(json.toString(), new GenericType<List<DomUser>>() {
                    });
                case DomRole:
                    return (List<T>) genson.deserialize(json.toString(), new GenericType<List<DomRole>>() {
                    });
                case DomStudent:
                    return (List<T>) genson.deserialize(json.toString(), new GenericType<List<DomStudent>>() {
                    });
                case DomTeacher:
                    return (List<T>) genson.deserialize(json.toString(), new GenericType<List<DomTeacher>>() {
                    });
                case DomSchoolAdmin:
                    return (List<T>) genson.deserialize(json.toString(), new GenericType<List<DomSchoolAdmin>>() {
                    });
                case DomSchool4DwoAdmin:
                    return (List<T>) genson.deserialize(json.toString(), new GenericType<List<DomSchool4DwoAdmin>>() {
                    });
                case DomSchoolClass:
                    return (List<T>) genson.deserialize(json.toString(), new GenericType<List<DomSchoolClass>>() {
                    });
                case DomSchoolsRolesAndClasses:
                    return (List<T>) genson.deserialize(json.toString(), new GenericType<List<DomSchoolsRolesAndClasses>>() {
                    });
                case DomTeacherAndHasRole:
                    return (List<T>) genson.deserialize(json.toString(), new GenericType<List<DomTeacherAndHasRole>>() {
                    });
                default:
                    String msg = "Programming error, trying to get an unsupported dataType.";
                    LOG.log(Level.SEVERE, msg);
                    throw new Dwo2Exception(Dwo2ExceptionCode.Rest_InternalError, msg);
            }
        } catch (MalformedURLException e) {
            throw new Dwo2Exception(Dwo2ExceptionCode.Rest_InternalError, "Malformed URL");
        } catch (IOException e) {
            if (e.getClass().equals(java.net.ConnectException.class)) {
                throw new Dwo2Exception(Dwo2ExceptionCode.Rest_ConnectionTimeout, e.getMessage());
            } else {
                throw new Dwo2Exception(Dwo2ExceptionCode.Rest_InternalError, e.getMessage());
            }
        }
    }
}
