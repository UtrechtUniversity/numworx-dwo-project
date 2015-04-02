package fi.dwo.dwojapplet.persistence.rest;

import fi.dwo.commons.persistence.entities.DwoSystemParameters;
import java.util.List;
import java.util.logging.Logger;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import org.glassfish.jersey.client.ClientConfig;

/**
 *
 * @author Gert van der Plas <gertvdplas@gmail.com>
 */
public class RestExample {

    private static final Logger log = Logger.getLogger(RestExample.class.getName());

    public static void main(String[] args) {

        Client client = ClientBuilder.newClient();
        
        
        //getjson
        GenericType<List<DwoSystemParameters>> dwoParamType = new GenericType<List<DwoSystemParameters>>() {
        };
        List<DwoSystemParameters> entities = ClientBuilder.newClient().target("http://localhost:8080")
                .path("/DWO/DWOServer/jax-rs/serverstatus/json")
                .request().accept(MediaType.APPLICATION_JSON).get(dwoParamType);
        System.out.println(entities.size());
        for (DwoSystemParameters p : entities) {
            System.out.println(p.getName() + " " + p.getValue());
        }

        // broken, needs media type converter with xml support, might work for a single type.
//        GenericType<List<DwoSystemParameters>> dwoParamColType = new GenericType<List<DwoSystemParameters>>() {
//        };
//        List<DwoSystemParameters> entitiesCol = ClientBuilder.newClient().target("http://localhost:8080")
//                .path("/DWO/DWOServer/jax-rs/serverstatus/xml")
//                .request().accept(MediaType.APPLICATION_XML_TYPE).get(dwoParamColType);
//        System.out.println(entitiesCol.getClass());
//        for (DwoSystemParameters p : entities) {
//            System.out.println(p.getName() + " " + p.getValue());
//        }
        //get html
//        GenericType<List<DwoSystemParameters>> dwoParamType = new GenericType<List<DwoSystemParameters>>() {
//        };
        String out = client.target("http://localhost:8080")
                .path("/DWO/DWOServer/jax-rs/serverstatus/html")
                .request().accept(MediaType.TEXT_HTML).get(String.class);
        System.out.println(out);

    }
}
