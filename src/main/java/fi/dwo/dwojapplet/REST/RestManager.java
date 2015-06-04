/* Copyrighted 2015. */
package fi.dwo.dwojapplet.REST;

import fi.dwo.commons.exceptions.Dwo2ExceptionCode;
import fi.dwo.commons.exceptions.Dwo2RestException;
import fi.dwo.commons.rest.RestClassType;
import fi.dwo.commons.persistence.entities.PersistentRole;
import fi.dwo.commons.persistence.entities.PersistentUser;
import fi.dwo.commons.rest.entities.SchoolRoleAndClass;
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
 * This is the plain and direct restManager. Please use the
 * {@Link StoredRestManager} to minimize memory use.
 *
 * @author Gert van der Plas <gertvdplas@gmail.com>
 */
class RestManager {

    protected static final Logger LOG = Logger.getLogger(RestManager.class.getName());

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
    public <T> T get(String path, Class<T> c) throws Dwo2RestException {
        Response response = webTargetRest.path(path).request().get();
        if (response.getStatus() != 200) {
            LOG.log(Level.WARNING, "Code: {0}. Reason{1}", new Object[]{response.getStatus(), response.getStatusInfo().getReasonPhrase()});
            Dwo2RestException e = new Dwo2RestException((String) response.getEntity());
            LOG.log(Level.WARNING, "Dwo2Code: {0}. Dwo2Reason{1}", new Object[]{e.getDwo2Code().name(),e.getDwo2Message()});
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
     * @param c Class type to return.
     * @return A list of Class c.
     * @throws RestException
     */
    public <T> List<T> getList(String path, RestClassType type) throws Dwo2RestException {
        Response response = webTargetRest.path(path).request().get();
        if (response.getStatus() != 200) {
            LOG.log(Level.WARNING, "Code: {0}. Reason{1}", new Object[]{response.getStatus(), response.getStatusInfo().getReasonPhrase()});
            Dwo2RestException e = new Dwo2RestException((String) response.getEntity());
            LOG.log(Level.WARNING, "Dwo2Code: {0}. Dwo2Reason{1}", new Object[]{e.getDwo2Code().name(),e.getDwo2Message()});
            throw e;
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
                case SchoolsRolesAndClasses:
                    GenericType<ArrayList<SchoolRoleAndClass>> pSRCType = new GenericType<ArrayList<SchoolRoleAndClass>>() {
                    };
                    return (List<T>) response.readEntity(pSRCType);
                default:
                    String msg = "Error trying to get an unsupported dataType.";
                    LOG.log(Level.SEVERE, msg);
                    throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError,msg);
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
    public <T> T put(String path, Class<T> c, Object o) throws Dwo2RestException {
        Response response = webTargetRest.path(path).request().put(Entity.entity(o, MediaType.APPLICATION_JSON));
        if (response.getStatus() != 200) {
             LOG.log(Level.WARNING, "Code: {0}. Reason{1}", new Object[]{response.getStatus(), response.getStatusInfo().getReasonPhrase()});
            String json = (String) response.readEntity(String.class);
            Dwo2RestException e = new Dwo2RestException(json);
            LOG.log(Level.WARNING, "Dwo2Code: {0}. Dwo2Reason{1}", new Object[]{e.getDwo2Code().name(),e.getDwo2Message()});
            throw e;
       } else {
            T r = response.readEntity(c);
            return (r);
        }
    }
    //

}
