/* Copyrighted 2015. */
package fi.dwo.dwojapplet.REST;

import fi.dwo.commons.dom.entities.DomRole;
import fi.dwo.commons.dom.entities.DomSchoolClass;
import fi.dwo.commons.dom.entities.DomSchoolsRolesAndClasses;
import fi.dwo.commons.dom.entities.DomUser;
import fi.dwo.commons.dom.entities.DomTeacher;
import fi.dwo.commons.exceptions.Dwo2Exception;
import fi.dwo.commons.exceptions.Dwo2ExceptionCode;
import fi.dwo.commons.exceptions.Dwo2RestException;
import fi.dwo.commons.rest.RestListClassTypes;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * This is the plain and direct restManager. Please use the
 * {@Link StoredRestManager} to minimize memory use.
 *
 * @author Gert van der Plas <gertvdplas@gmail.com>
 */
class RestManager {
//TODO Reduce code by implementing an WebException handler
//TODO Handle  non exception 400 errors gracefully using Dwo2Exception.
    protected static final Logger LOG = Logger.getLogger(RestManager.class.getName());

    protected static final RestManager instance = new RestManager();
    protected static WebTarget webTargetRest;

    /**
     * @return the instance
     */
    public static RestManager getInstance() {
        return instance;
    }

    /**
     * @return the webTargetRest
     */
    public static WebTarget getWebTargetRest() {
        return webTargetRest;
    }

    /**
     * @param aWebTargetRest the webTargetRest to set
     */
    public synchronized static void  setWebTargetRest(WebTarget aWebTargetRest) {
        webTargetRest = aWebTargetRest;
    }

    /**
     * GET operation to the restful server.
     *
     * @param <T>
     * @param path sub context path servlet.
     * @param c Class type to return.
     * @return A list of class c objects.
     * @throws fi.dwo.commons.exceptions.Dwo2Exception
     */
    public <T> T get(String path, Class<T> c) throws Dwo2Exception {
        CacheControl cache = new CacheControl();
        cache.setNoCache(true);
        cache.isNoStore();
        Response response;
        try{
            response = webTargetRest.path(path).request().cacheControl(cache).get();
        }catch(javax.ws.rs.ProcessingException e){
            //catch time-outs
            if(e.getMessage().contains("java.net.SocketTimeoutException: connect timed out")){
            throw new Dwo2Exception(Dwo2ExceptionCode.Rest_ConnectionTimeout, "Connection time-out.");
            }else{
                throw e;
            }
        }
        if (response.getStatus() != 200) {
            LOG.log(Level.WARNING, "Code: {0}. Reason{1}", new Object[]{response.getStatus(), response.getStatusInfo().getReasonPhrase()});
            Dwo2Exception e;
            if (response.getStatus() == 400) {
                //Assuming server side servlet generated exception has been sent.
                String json = (String) response.readEntity(String.class);
                e = new Dwo2Exception(Dwo2RestException.decodeCodeInJSON(json), Dwo2RestException.decodeMessageInJSON(json));
            } else {
                //non-servlet generated exception has been sent. Convert to Dwo2RestException.
                //TODO To filter these for the user and suggest a course of action.
                e = new Dwo2Exception(Dwo2ExceptionCode.Rest_InterfaceError, response.getStatusInfo().getReasonPhrase());
            }
            LOG.log(Level.WARNING, "Dwo2Code: {0}. Dwo2Reason{1}", new Object[]{e.getDwo2Code().name(), e.getDwo2Message()});
            throw e;
        } else {
            return response.readEntity(c);
        }
    }

    /**
     * GET operation to the restful server.
     *
     * @param <T>
     * @param path sub context path servlet.
     * @param type
     * @return A list of Class c.
     * @throws fi.dwo.commons.exceptions.Dwo2Exception
     */
    public <T> List<T> getList(String path, RestListClassTypes type) throws Dwo2RestException, Dwo2Exception {
        CacheControl cache = new CacheControl();
        cache.setNoCache(true);
        cache.isNoStore();
        Response response;
        try{
            response = webTargetRest.path(path).request().cacheControl(cache).get();
        }catch(javax.ws.rs.ProcessingException e){
            //catch time-outs
            if(e.getMessage().contains("java.net.SocketTimeoutException: connect timed out")){
            throw new Dwo2Exception(Dwo2ExceptionCode.Rest_ConnectionTimeout, "Connection time-out.");
            }else{
                throw e;
            }
        }

        if (response.getStatus() != 200) {
            LOG.log(Level.WARNING, "Code: {0}. Reason{1}", new Object[]{response.getStatus(), response.getStatusInfo().getReasonPhrase()});
            Dwo2Exception e;
            if (response.getStatus() == 400) {
                //Assuming server side servlet generated exception has been sent.
                String json = (String) response.readEntity(String.class);
                e = new Dwo2Exception(Dwo2RestException.decodeCodeInJSON(json), Dwo2RestException.decodeMessageInJSON(json));
            } else {
                //non-servlet generated exception has been sent. Convert to Dwo2RestException.
                //TODO To filter these for the user and suggest a course of action.
                e = new Dwo2Exception(Dwo2ExceptionCode.Rest_InterfaceError, response.getStatusInfo().getReasonPhrase());
            }
            LOG.log(Level.WARNING, "Dwo2Code: {0}. Dwo2Reason{1}", new Object[]{e.getDwo2Code().name(), e.getDwo2Message()});
            throw e;
        } else {
            switch (type) {
                case DomUser:
                    GenericType<ArrayList<DomUser>> pUserType = new GenericType<ArrayList<DomUser>>() {
                    };
                    return (List<T>) response.readEntity(pUserType);
                case DomRole:
                    GenericType<ArrayList<DomRole>> pRoleType = new GenericType<ArrayList<DomRole>>() {
                    };
                    return (List<T>) response.readEntity(pRoleType);
                case DomSchoolClass:
                    GenericType<ArrayList<DomSchoolClass>> pScType = new GenericType<ArrayList<DomSchoolClass>>() {
                    };                    
                    return (List<T>) response.readEntity(pScType);
                case DomSchoolsRolesAndClasses:
                    GenericType<ArrayList<DomSchoolsRolesAndClasses>> pSRCType = new GenericType<ArrayList<DomSchoolsRolesAndClasses>>() {
                    };                    
                    return (List<T>) response.readEntity(pSRCType);
                default:
                    String msg = "Programming error, trying to get an unsupported dataType.";
                    LOG.log(Level.SEVERE, msg);
                    throw new Dwo2Exception(Dwo2ExceptionCode.Rest_InternalError, msg);
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
     * @throws fi.dwo.commons.exceptions.Dwo2Exception
     */
    public <T> List<T> getPutList(String path, RestListClassTypes type, Object o) throws Dwo2RestException, Dwo2Exception {
        CacheControl cache = new CacheControl();
        cache.setNoCache(true);
        cache.isNoStore();
        Response response;
        try{
            response = webTargetRest.path(path).request().cacheControl(cache).put(Entity.entity(o, MediaType.APPLICATION_JSON));//fix call add objects
        }catch(javax.ws.rs.ProcessingException e){
            //catch time-outs
            if(e.getMessage().contains("java.net.SocketTimeoutException: connect timed out")){
            throw new Dwo2Exception(Dwo2ExceptionCode.Rest_ConnectionTimeout, "Connection time-out.");
            }else{
                throw e;
            }
        }

        if (response.getStatus() != 200) {
            LOG.log(Level.WARNING, "Code: {0}. Reason{1}", new Object[]{response.getStatus(), response.getStatusInfo().getReasonPhrase()});
            Dwo2Exception e;
            if (response.getStatus() == 400) {
                //Assuming server side servlet generated exception has been sent.
                String json = (String) response.readEntity(String.class);
                e = new Dwo2Exception(Dwo2RestException.decodeCodeInJSON(json), Dwo2RestException.decodeMessageInJSON(json));
            } else {
                //non-servlet generated exception has been sent. Convert to Dwo2RestException.
                //TODO To filter these for the user and suggest a course of action.
                e = new Dwo2Exception(Dwo2ExceptionCode.Rest_InterfaceError, response.getStatusInfo().getReasonPhrase());
            }
            LOG.log(Level.WARNING, "Dwo2Code: {0}. Dwo2Reason{1}", new Object[]{e.getDwo2Code().name(), e.getDwo2Message()});
            throw e;
        } else {
            switch (type) {
                case DomUser:
                    GenericType<ArrayList<DomUser>> pUserType = new GenericType<ArrayList<DomUser>>() {
                    };
                    return (List<T>) response.readEntity(pUserType);
                case DomTeacher:
                    GenericType<ArrayList<DomTeacher>> pTeacherType = new GenericType<ArrayList<DomTeacher>>() {
                    };
                    return (List<T>) response.readEntity(pTeacherType);
                case DomRole:
                    GenericType<ArrayList<DomRole>> pRoleType = new GenericType<ArrayList<DomRole>>() {
                    };
                    return (List<T>) response.readEntity(pRoleType);
                case DomSchoolsRolesAndClasses:
                    GenericType<ArrayList<DomSchoolsRolesAndClasses>> pSRCType = new GenericType<ArrayList<DomSchoolsRolesAndClasses>>() {
                    };                    
                    return (List<T>) response.readEntity(pSRCType);
                default:
                    String msg = "Programming error, trying to get an unsupported dataType.";
                    LOG.log(Level.SEVERE, msg);
                    throw new Dwo2Exception(Dwo2ExceptionCode.Rest_InternalError, msg);
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
     * @throws fi.dwo.commons.exceptions.Dwo2Exception
     */
    public <T> T put(String path, Class<T> c, Object o) throws Dwo2Exception {
        CacheControl cache = new CacheControl();
        cache.setNoCache(true);
        cache.isNoStore();
        Response response;        
        try{
            response = webTargetRest.path(path).request().cacheControl(cache).put(Entity.entity(o, MediaType.APPLICATION_JSON));

        }catch(javax.ws.rs.ProcessingException e){
            //catch time-outs
            if(e.getMessage().contains("java.net.SocketTimeoutException: connect timed out")){
            throw new Dwo2Exception(Dwo2ExceptionCode.Rest_ConnectionTimeout, "Connection time-out.");
            }else{
                throw e;
            }
        }
        
        if (response.getStatus() != 200) {
            LOG.log(Level.WARNING, "Code: {0}. Reason{1}", new Object[]{response.getStatus(), response.getStatusInfo().getReasonPhrase()});
            Dwo2Exception e;
            if (response.getStatus() == 400) {
                //Assuming server side servlet generated exception has been sent.
                String json = (String) response.readEntity(String.class);
                e = new Dwo2Exception(Dwo2RestException.decodeCodeInJSON(json), Dwo2RestException.decodeMessageInJSON(json));
            } else {
                //non-servlet generated exception has been sent. Convert to Dwo2RestException.
                //TODO To filter these for the user and suggest a course of action.
                e = new Dwo2Exception(Dwo2ExceptionCode.Rest_InterfaceError, response.getStatusInfo().getReasonPhrase());
            }
            LOG.log(Level.WARNING, "Dwo2Code: {0}. Dwo2Reason{1}", new Object[]{e.getDwo2Code().name(), e.getDwo2Message()});
            throw e;

        } else {
            T r = response.readEntity(c);
            return (r);
        }
    }
    //
}
