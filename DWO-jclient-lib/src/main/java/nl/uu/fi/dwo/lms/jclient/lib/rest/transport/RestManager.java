/* Copyrighted 2015. */
package nl.uu.fi.dwo.lms.jclient.lib.rest.transport;

import com.owlike.genson.GenericType;
import com.owlike.genson.Genson;
import com.owlike.genson.GensonBuilder;
import com.owlike.genson.JsonBindingException;
import com.owlike.genson.ext.jaxb.JAXBBundle;

import nl.uu.fi.dwo.rest.dom.entities.DomAppletConfig;
import nl.uu.fi.dwo.rest.dom.entities.DomAppletFull;
import nl.uu.fi.dwo.rest.dom.entities.DomClassCourse;
import nl.uu.fi.dwo.rest.dom.entities.DomCourse;
import nl.uu.fi.dwo.rest.dom.entities.DomCourseStudent;
import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfileFull;
import nl.uu.fi.dwo.rest.dom.entities.DomLoginContext;
import nl.uu.fi.dwo.rest.dom.entities.DomMethod;
import nl.uu.fi.dwo.rest.dom.entities.DomRole;
import nl.uu.fi.dwo.rest.dom.entities.DomSchool4DwoAdmin;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolFrom;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolAdmin;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolAdminAndHasRole;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolsRolesAndClasses;
import nl.uu.fi.dwo.rest.dom.entities.DomStudent;
import nl.uu.fi.dwo.rest.dom.entities.DomUser;
import nl.uu.fi.dwo.rest.dom.entities.DomUserFull;
import nl.uu.fi.dwo.rest.dom.entities.DomUserFullwLoginContext;
import nl.uu.fi.dwo.rest.dom.entities.DomTeacher;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.exceptions.Dwo2ExceptionCode;
import nl.uu.fi.dwo.rest.RestListClassTypes;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolsRolesAndClassesV2;
import nl.uu.fi.dwo.rest.dom.entities.DomScoContext;
import nl.uu.fi.dwo.rest.dom.entities.DomTeacherAndHasRole;
import nl.uu.fi.dwo.rest.util.Dwo2ExceptionTranslator;
import nl.uu.fi.dwo.rest.util.RestyDateTimeFormat;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContext;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentScoContext;

/**
 * This is the plain and direct restManager. Please use the {@Link StoredRestManager} to minimize
 * memory use. Note this class methods may be called asynchronous. Therefor methods from
 * RestAuthentiat and others should be handle async calls too.
 *
 * @author Gert van der Plas <gertvdplas@gmail.com>
 */
class RestManager extends RestyDateTimeFormat {

  private static final DateFormat yourDateFormat = new SimpleDateFormat(RESTY_DATETIME_FORMAT);
  private final Genson genson = new GensonBuilder()
		  .withBundle(new JAXBBundle())
		  .useDateFormat(yourDateFormat)
		  .useDateAsTimestamp(DATE_AS_TIMESTAMP)
		  .create();

  private static final Logger LOG = Logger.getLogger(RestManager.class.getName());

  private String basicAuthString;
  private final RestAuthenticator authenticator;

  RestManager(RestAuthenticator authenticator) {
    super();
    this.authenticator = authenticator;
    if (authenticator.isAuthenticated())
      setBasicAuthString(authenticator.getUsername(), authenticator.getPassword(), authenticator.getRealm());
  }

  public RestManager(RestManager org) {
	this.authenticator = org.authenticator.duplicate();
	this.basicAuthString = org.basicAuthString;
}

public RestAuthenticator getAuthenticator() {
    return authenticator;
  }

  public URL getServerUrlPath() {
    return getAuthenticator().getServerUrlPath();
  }

  public Genson getGenson() {
    return genson;
  }

  /**
   * @return the basicAuthString
   */
  public String getBasicAuthString() {
    return basicAuthString;
  }

  public void setAuthString(String data) {
	  Objects.requireNonNull(data);
	  String lower = data.toLowerCase();
	  if (lower.startsWith("bearer ") || lower.startsWith("basic "))
		  setBasicAuthString(data);
	  else 
		  throw new IllegalArgumentException(data);
  }
  /**
   * @param data
   */
  synchronized void setBasicAuthString(String data) {
    // note that reference changes in Java are atomic.
    basicAuthString = data;
  }
 
  public void setBearerAuthString(String bearer) {
    getAuthenticator().setUsername("");
    getAuthenticator().setPassword("");
    setBasicAuthString("Bearer " + bearer);    
  }

  /**
   * @param username
   * @param password
   */
  public synchronized void setBasicAuthString(String username, String password, String realm) {
    getAuthenticator().setPassword(password);
    getAuthenticator().setUsername(username);
    getAuthenticator().setRealm(realm);
// note that reference changes in Java are atomic.
    basicAuthString = getAuthenticator().getBasicAuthentication();
  }

  /**
   * GET operation to the restful server.
   *
   * @param <T>
   * @param path sub context path servlet.
   * @param c Class type to return.
   * @return A list of class c objects.
   * @throws nl.uu.fi.dwo.rest.exceptions.Dwo2Exception
   */
  protected <T> T get(String path, Class<T> c) throws Dwo2Exception {
	StringBuilder json = new StringBuilder();
    try {
      URL url = new URL(authenticator.getServerUrlPath(), path); // TODO make login

      HttpURLConnection conn = (HttpURLConnection) url.openConnection();
      conn.setRequestMethod("GET");
      conn.setRequestProperty("Accept", "application/json");
      if (getBasicAuthString() != null) {
        conn.setRequestProperty("Authorization", getBasicAuthString());
      }
      conn.setUseCaches(false);

      if (conn.getResponseCode() != 200) {
        LOG.log(Level.WARNING, "Code: {0}. Reason: {1}",
            new Object[] {conn.getResponseCode(), conn.getResponseMessage()});
        Dwo2Exception e;
        if (conn.getResponseCode() == 404) {
        	throw new Dwo2Exception(Dwo2ExceptionCode.Rest_ResourceNotFound, path);
        }
        if (conn.getResponseCode() == 400) {// Dwo2Exception
          BufferedReader br = new BufferedReader(
              new InputStreamReader((conn.getErrorStream()), StandardCharsets.UTF_8));

          String output;
          while ((output = br.readLine()) != null) {
            json.append(output);
          }
          conn.disconnect();
          e = new Dwo2Exception(Dwo2ExceptionTranslator.decodeCodeInJSON(json.toString()),
              Dwo2ExceptionTranslator.decodeMessageInJSON(json.toString()));
        } else if (conn.getResponseCode() == 401) { 
          e = new Dwo2Exception(Dwo2ExceptionCode.User_AuthenticationError, conn.getResponseMessage());
        } else {
          // non-servlet generated exception has been sent. Convert to Dwo2RestException.
          e = new Dwo2Exception(Dwo2ExceptionCode.Rest_InterfaceError, conn.getResponseMessage());
        }
        LOG.log(Level.WARNING, "Dwo2Code: {0}. Dwo2Reason: {1}",
            new Object[] {e.getDwo2Code().name(), e.getDwo2Message()});
        throw e;
      }

      BufferedReader br = new BufferedReader(
          new InputStreamReader((conn.getInputStream()), StandardCharsets.UTF_8)); // XXX force
                                                                                   // UTF-8 encoding

      String output;
      while ((output = br.readLine()) != null) {
        json.append(output);
      }
      conn.disconnect();
      // decode JSON
      // List<DomUserFull> user = genson.deserialize(json.toString(), new
      // GenericType<List<DomUserFull>>(){});
      LOG.log(Level.FINEST, "Received: {0}", new Object[] {json.toString()});
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
    } catch(JsonBindingException e) { // komt voor, geen idee waarom....
    	LOG.log(Level.SEVERE, "error in json: " + json, e);
    	throw new Dwo2Exception(Dwo2ExceptionCode.Rest_InterfaceError, e.getMessage());
    }
  }

  /**
   * GET operation to the restful server.
   *
   * @param <T>
   * @param path sub context path servlet.
   * @param type
   * @return A list of Class c.
   * @throws nl.uu.fi.dwo.rest.exceptions.Dwo2Exception
   */
  protected <T> List<T> getList(String path, RestListClassTypes type) throws Dwo2Exception {
    try {
      URL url = new URL(authenticator.getServerUrlPath(), path); // TODO make login

      HttpURLConnection conn = (HttpURLConnection) url.openConnection();
      conn.setRequestMethod("GET");
      conn.setRequestProperty("Accept", "application/json");
      conn.setRequestProperty("Accept-Encoding", "application/json");
      conn.setRequestProperty("Content-Type", "application/json");
      if (getBasicAuthString() != null) {
        conn.setRequestProperty("Authorization", getBasicAuthString());
      }
      conn.setUseCaches(false);

      if (conn.getResponseCode() != 200) {
        LOG.log(Level.WARNING, "Code: {0}. Reason{1}",
            new Object[] {conn.getResponseCode(), conn.getResponseMessage()});
        Dwo2Exception e;
        if (conn.getResponseCode() == 400) {// Dwo2Exception
          BufferedReader br = new BufferedReader(
              new InputStreamReader((conn.getErrorStream()), StandardCharsets.UTF_8));

          String output;
          StringBuilder json = new StringBuilder();
          while ((output = br.readLine()) != null) {
            json.append(output);
          }
          conn.disconnect();
          e = new Dwo2Exception(Dwo2ExceptionTranslator.decodeCodeInJSON(json.toString()),
              Dwo2ExceptionTranslator.decodeMessageInJSON(json.toString()));
        } else if (conn.getResponseCode() == 401) { 
          e = new Dwo2Exception(Dwo2ExceptionCode.User_AuthenticationError, conn.getResponseMessage());
       } else {
          // non-servlet generated exception has been sent. Convert to Dwo2RestException.
          e = new Dwo2Exception(Dwo2ExceptionCode.Rest_InterfaceError, conn.getResponseMessage());
        }
        LOG.log(Level.WARNING, "Dwo2Code: {0}. Dwo2Reason: {1}",
            new Object[] {e.getDwo2Code().name(), e.getDwo2Message()});
        throw e;
      }

      BufferedReader br = new BufferedReader(
          new InputStreamReader((conn.getInputStream()), StandardCharsets.UTF_8));

      String output;
      StringBuilder json = new StringBuilder();
      while ((output = br.readLine()) != null) {
        json.append(output);
      }
      conn.disconnect();
      // decode JSON
      LOG.log(Level.FINEST, "Received: {0}", new Object[] {json.toString()});
      switch (type) {
        case DomUser:
          return (List<T>) genson.deserialize(json.toString(), new GenericType<List<DomUser>>() {});
        case DomUserFull:
            return (List<T>) genson.deserialize(json.toString(), new GenericType<List<DomUserFull>>() {});
        case DomRole:
          return (List<T>) genson.deserialize(json.toString(), new GenericType<List<DomRole>>() {});
        case DomStudent:
          return (List<T>) genson.deserialize(json.toString(),
              new GenericType<List<DomStudent>>() {});
        case DomTeacher:
          return (List<T>) genson.deserialize(json.toString(),
              new GenericType<List<DomTeacher>>() {});
        case DomSchoolAdmin:
          return (List<T>) genson.deserialize(json.toString(),
              new GenericType<List<DomSchoolAdmin>>() {});
        case DomSchoolFrom:
          return (List<T>) genson.deserialize(json.toString(),
              new GenericType<List<DomSchoolFrom>>() {});
        case DomSchool4DwoAdmin:
          return (List<T>) genson.deserialize(json.toString(),
              new GenericType<List<DomSchool4DwoAdmin>>() {});
        case DomStudentModelContext:
          return (List<T>) genson.deserialize(json.toString(),
              new GenericType<List<DomStudentModelContext>>() {});
        case DomSchoolClass:
          return (List<T>) genson.deserialize(json.toString(),
              new GenericType<List<DomSchoolClass>>() {});
        case DomSchoolsRolesAndClasses:
          return (List<T>) genson.deserialize(json.toString(),
              new GenericType<List<DomSchoolsRolesAndClasses>>() {});
        case DomDwoProfile:
          return (List<T>) genson.deserialize(json.toString(),
              new GenericType<List<DomDwoProfileFull>>() {});
        case DomSchoolsRolesAndClassesV2:
          return (List<T>) genson.deserialize(json.toString(),
              new GenericType<List<DomSchoolsRolesAndClassesV2>>() {});
        case DomClassCourse:
        	return (List<T>) genson.deserialize(json.toString(), new GenericType<List<DomClassCourse>>() {});
        case DomAppletConfig:
          return (List<T>) genson.deserialize(json.toString(),
              new GenericType<List<DomAppletConfig>>() {});
        case DomUserFullwLoginContext:
          return (List<T>) genson.deserialize(json.toString(), new GenericType<List<DomUserFullwLoginContext>>() {});
        case DomLoginContext:
          return (List<T>) genson.deserialize(json.toString(), new GenericType<List<DomLoginContext>>() {});
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
   * @throws nl.uu.fi.dwo.rest.exceptions.Dwo2Exception
   */
  protected <T> T put(String path, Class<T> c, Object o) throws Dwo2Exception { // due to genson now c
                                                                             // is superflous
    try {
      URL url = new URL(authenticator.getServerUrlPath(), path); // TODO make login
      HttpURLConnection conn = (HttpURLConnection) url.openConnection();
      DataOutputStream outStream = null;
      conn.setRequestMethod("PUT");
      conn.setRequestProperty("Accept", "application/json");
      conn.setRequestProperty("Accept-Encoding", "application/json");
      conn.setRequestProperty("Content-Type", "application/json");
      if (getBasicAuthString() != null) {
        conn.setRequestProperty("Authorization", getBasicAuthString());
      }
      conn.setRequestProperty("Accept-Charset", "UTF-8");
      // conn.setDoInput(true);
      conn.setDoOutput(true);
      conn.setUseCaches(false);
      outStream = new DataOutputStream(conn.getOutputStream());
      // List<DomUserFull> user = genson.deserialize(json.toString(), new
      // GenericType<List<DomUserFull>>(){});
      String jsonOut = genson.serialize(o);
      LOG.log(Level.FINEST, "Sending: {0}", new Object[] {jsonOut.toString()});
      outStream.write(jsonOut.getBytes("UTF-8"));
      outStream.close();
      int responseCode = conn.getResponseCode();
      if (responseCode == 204) 
    	  return null; // No content
	  if (responseCode != 200) {
        LOG.log(Level.WARNING, "Code: {0}. Reason {1}",
            new Object[] {responseCode, conn.getResponseMessage()});
        Dwo2Exception e;
        if (responseCode == 400) {// Dwo2Exception
          // String json = conn.getResponseMessage();
          BufferedReader br = new BufferedReader(
              new InputStreamReader((conn.getErrorStream()), StandardCharsets.UTF_8));

          String output;
          StringBuilder json = new StringBuilder();
          while ((output = br.readLine()) != null) {
            json.append(output);
          }
          conn.disconnect();
          e = new Dwo2Exception(Dwo2ExceptionTranslator.decodeCodeInJSON(json.toString()),
              Dwo2ExceptionTranslator.decodeMessageInJSON(json.toString()));
        } else if (responseCode == 409) { 
            e = new Dwo2Exception(Dwo2ExceptionCode.Rest_ObjectModified, conn.getResponseMessage());
        } else if (responseCode == 401) { 
            e = new Dwo2Exception(Dwo2ExceptionCode.User_AuthenticationError, conn.getResponseMessage());
        } else {
          // non-servlet generated exception has been sent. Convert to Dwo2RestException.
          e = new Dwo2Exception(Dwo2ExceptionCode.Rest_InterfaceError, conn.getResponseMessage());
        }
        LOG.log(Level.WARNING, "Dwo2Code: {0}. Dwo2Reason: {1}",
            new Object[] {e.getDwo2Code().name(), e.getDwo2Message()});
        throw e;
      }

      BufferedReader br = new BufferedReader(
          new InputStreamReader((conn.getInputStream()), StandardCharsets.UTF_8));

      String output;
      StringBuilder json = new StringBuilder();
      while ((output = br.readLine()) != null) {
        json.append(output);
      }
      conn.disconnect();
      // decode JSON
      // DomResultsPerTeacher user = genson.deserialize(json.toString(), new
      // GenericType<DomResultsPerTeacher>(){});
      LOG.log(Level.FINEST, "Received: {0}", new Object[] {json.toString()});

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
   * @throws nl.uu.fi.dwo.rest.exceptions.Dwo2Exception
   */
  protected <T> List<T> getPutList(String path, RestListClassTypes type, Object o)
      throws Dwo2Exception {
    try {
      URL url = new URL(authenticator.getServerUrlPath(), path); // TODO make login
      HttpURLConnection conn = (HttpURLConnection) url.openConnection();
      DataOutputStream outStream = null;
      conn.setRequestMethod("PUT");
      conn.setRequestProperty("Accept", "application/json");
      conn.setRequestProperty("Accept-Encoding", "application/json");
      conn.setRequestProperty("Content-Type", "application/json");
      if (getBasicAuthString() != null) {
        conn.setRequestProperty("Authorization", getBasicAuthString());
      }
      conn.setDoInput(true);
      conn.setDoOutput(true);
      conn.setUseCaches(false);
      outStream = new DataOutputStream(conn.getOutputStream());
      // List<DomUserFull> user = genson.deserialize(json.toString(), new
      // GenericType<List<DomUserFull>>(){});
      String jsonOut = genson.serialize(o);
      LOG.log(Level.FINEST, "Sending: {0}", new Object[] {jsonOut.toString()});
      outStream.write(jsonOut.getBytes());
      outStream.close();
      if (conn.getResponseCode() != 200) {
        LOG.log(Level.WARNING, "Code: {0}. Reason {1}",
            new Object[] {conn.getResponseCode(), conn.getResponseMessage()});
        Dwo2Exception e;
        if (conn.getResponseCode() == 400) {// Dwo2Exception
          BufferedReader br = new BufferedReader(
              new InputStreamReader((conn.getErrorStream()), StandardCharsets.UTF_8));

          String output;
          StringBuilder json = new StringBuilder();
          while ((output = br.readLine()) != null) {
            json.append(output);
          }
          conn.disconnect();
          e = new Dwo2Exception(Dwo2ExceptionTranslator.decodeCodeInJSON(json.toString()),
              Dwo2ExceptionTranslator.decodeMessageInJSON(json.toString()));
        } else if (conn.getResponseCode() == 401) { 
          e = new Dwo2Exception(Dwo2ExceptionCode.User_AuthenticationError, conn.getResponseMessage());
        } else {
          // non-servlet generated exception has been sent. Convert to Dwo2RestException.
          e = new Dwo2Exception(Dwo2ExceptionCode.Rest_InterfaceError, conn.getResponseMessage());
        }
        LOG.log(Level.WARNING, "Dwo2Code: {0}. Dwo2Reason {1}",
            new Object[] {e.getDwo2Code().name(), e.getDwo2Message()});
        throw e;
      }

      BufferedReader br = new BufferedReader(
          new InputStreamReader((conn.getInputStream()), StandardCharsets.UTF_8));

      String output;
      StringBuilder json = new StringBuilder();
      while ((output = br.readLine()) != null) {
        json.append(output);
      }
      conn.disconnect();
      LOG.log(Level.FINEST, "Received: {0}", new Object[] {json.toString()});
      // decode JSON
      // List<DomUserFull> user = genson.deserialize(json.toString(), new
      // GenericType<List<DomUserFull>>(){});

      switch (type) {
        case DomUser:
          return (List<T>) genson.deserialize(json.toString(), new GenericType<List<DomUser>>() {});
        case DomUserFull:
            return (List<T>) genson.deserialize(json.toString(), new GenericType<List<DomUserFull>>() {});
        case DomRole:
          return (List<T>) genson.deserialize(json.toString(), new GenericType<List<DomRole>>() {});
        case DomStudent:
          return (List<T>) genson.deserialize(json.toString(),
              new GenericType<List<DomStudent>>() {});
        case DomCourse:
          return (List<T>) genson.deserialize(json.toString(),
              new GenericType<List<DomCourse>>() {});
        case DomCourseStudent:
          return (List<T>) genson.deserialize(json.toString(),
              new GenericType<List<DomCourseStudent>>() {});
        case DomTeacher:
          return (List<T>) genson.deserialize(json.toString(),
              new GenericType<List<DomTeacher>>() {});
        case DomSchoolAdmin:
          return (List<T>) genson.deserialize(json.toString(),
              new GenericType<List<DomSchoolAdmin>>() {});
        case DomSchool4DwoAdmin:
          return (List<T>) genson.deserialize(json.toString(),
              new GenericType<List<DomSchool4DwoAdmin>>() {});
        case DomSchoolClass:
          return (List<T>) genson.deserialize(json.toString(),
              new GenericType<List<DomSchoolClass>>() {});
        case DomSchoolsRolesAndClasses:
          return (List<T>) genson.deserialize(json.toString(),
              new GenericType<List<DomSchoolsRolesAndClasses>>() {});
        case DomStudentModelContext:
          return (List<T>) genson.deserialize(json.toString(),
              new GenericType<List<DomStudentModelContext>>() {});
        case DomTeacherAndHasRole:
          return (List<T>) genson.deserialize(json.toString(),
              new GenericType<List<DomTeacherAndHasRole>>() {});
        case DomSchoolAdminAndHasRole:
            return (List<T>) genson.deserialize(json.toString(),
                new GenericType<List<DomSchoolAdminAndHasRole>>() {});
        case DomStudentScoContext:
          return (List<T>) genson.deserialize(json.toString(), new GenericType<List<DomStudentScoContext>>() {});
        case DomApplet:
          return (List<T>) genson.deserialize(json.toString(), new GenericType<List<DomAppletFull>>() {});
        case DomSchoolFrom:
          return (List<T>) genson.deserialize(json.toString(), new GenericType<List<DomSchoolFrom>>() {});         
        case DomAppletConfig:
            return (List<T>) genson.deserialize(json.toString(),
                new GenericType<List<DomAppletConfig>>() {});
        case DomScoContext:
          return (List<T>) genson.deserialize(json.toString(),
            new GenericType<List<DomScoContext>>() {});
        case DomDwoProfile:
            return (List<T>) genson.deserialize(json.toString(),
                    new GenericType<List<DomDwoProfileFull>>() {});
        case DomMethod:
        	return (List<T>) genson.deserialize(json.toString(), new GenericType<List<DomMethod>>() { });
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
