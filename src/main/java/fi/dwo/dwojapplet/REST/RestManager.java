/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.dwo.dwojapplet.REST;

import fi.dwo.commons.persistence.PersistenceClassType;
import fi.dwo.commons.persistence.entities.PersistentRole;
import fi.dwo.commons.persistence.entities.PersistentUser;
import fi.dwo.dwojapplet.domain.rest.RestException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * This is the plain and direct restManager. Please use the {@Link StoredRestManager} to 
 * minimize memory use.
 *
 * @author Gert van der Plas <gertvdplas@gmail.com>
 */
class RestManager {
    protected static final Logger log = Logger.getLogger(RestManager.class.getName());
    
    
    protected static final StoredRestManager instance = new StoredRestManager();
    protected static WebTarget webTargetRest;

    /**
     * @return the instance
     */
    public static StoredRestManager getInstance() {
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
    public static void setWebTargetRest(WebTarget aWebTargetRest) {
        webTargetRest = aWebTargetRest;
    }

    /**
     * GET operation to the restful server.
     *
     * @param <T>
     * @param path sub context path servlet.
     * @param c Class type to return.
     * @return A list of class c objects.
     * @throws RestException
     */
    public <T> T get(String path, Class<T> c) throws RestException {
        Response response = webTargetRest.path(path).request().get();
        if (response.getStatus() != 200) {
            log.log(Level.WARNING, "Code: {0}. Reason{1}", new Object[]{response.getStatus(), response.getStatusInfo().getReasonPhrase()});
            return null;
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
     * @param c Class type to return.
     * @return A list of Class c.
     * @throws RestException
     */
    public <T> List<T> getList(String path, PersistenceClassType type) throws RestException {
        Response response = webTargetRest.path(path).request().get();
        if (response.getStatus() != 200) {
            log.log(Level.WARNING, "Code: {0}. Reason{1}", new Object[]{response.getStatus(), response.getStatusInfo().getReasonPhrase()});
            return null;
        } else {
            switch (type) {
                case PersistentUser:
                    GenericType<ArrayList<PersistentUser>> pUserType = new GenericType<ArrayList<PersistentUser>>() {
                    };
                    return (List<T>) response.readEntity(pUserType);
                case PersistentRole:
                    GenericType<ArrayList<PersistentRole>> pRoleType = new GenericType<ArrayList<PersistentRole>>() {
                    };
                    return (List<T>) response.readEntity(pRoleType);
                default:
                    String msg = "Error trying to get an unsupported dataType.";
                    log.log(Level.SEVERE, msg);
                    throw new RestException(msg);
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
     * @throws RestException
     */
    public <T> T put(String path, Class<T> c, Object o) throws RestException {
        Response response = webTargetRest.path(path).request().put(Entity.entity(o, MediaType.APPLICATION_JSON));
        if (response.getStatus() != 200) {
            log.log(Level.WARNING, "Code: {0}. Reason{1}", new Object[]{response.getStatus(), response.getStatusInfo().getReasonPhrase()});
            return null;
        } else {
            return response.readEntity(c);
        }
    }
    //
    
}
