package fi.dwo.dwojapplet.REST;

import fi.dwo.dwojapplet.domain.rest.RestException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * RestManager wraps communications and handles web server exceptions. 
 * 
 * @author G.A.J. van der Plas
 */
public class RestManager {
    private static final Logger log = Logger.getLogger(RestManager.class.getName());

    private static RestManager instance = new RestManager();
    
    private static WebTarget webTargetRest;
    

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
        Response response = webTargetRest.path(path)
                .request().get();
        if (response.getStatus() != 200) {
            log.log(Level.WARNING,"Code: {0}. Reason{1}", new Object[]{response.getStatus(),response.getStatusInfo().getReasonPhrase()});
            return null;
        } else {
            return  response.readEntity(c);
        }
    }
    
//    /** 
//     * GET operation to the restful server.
//     * @param <T>
//     * @param path sub context path servlet.
//     * @param c Class type to return.
//     * @return A list of Class c.
//     * @throws RestException 
//     */
//    public <T,E> List<E> getList(String path, Class<E> c, Class<List<E> o) throws RestException {
//        Response response = webTargetRest.path(path)
//                .request().get();
//        if (response.getStatus() != 200) {
//            log.log(Level.WARNING,"Code: {0}. Reason{1}", new Object[]{response.getStatus(),response.getStatusInfo().getReasonPhrase()});
//            return null;
//        } else {
//        GenericType<ArrayList<E>> oClass = new GenericType<ArrayList<E>>() {};
//            return  response.readEntity(oClass);
//        }
//    }
    
       
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
        Response response = webTargetRest.path(path)
                .request().put(Entity.entity(o, MediaType.APPLICATION_JSON));
        if (response.getStatus() != 200) {
            log.log(Level.WARNING,"Code: {0}. Reason{1}", new Object[]{response.getStatus(),response.getStatusInfo().getReasonPhrase()});
            return null;
        } else {
            return  response.readEntity(c);
        }
    }
    
//    
}
