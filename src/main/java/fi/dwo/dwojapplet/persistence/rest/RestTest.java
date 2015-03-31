package fi.dwo.dwojapplet.persistence.rest;

import fi.dwo.commons.persistence.entities.DwoSystemParameters;
import java.util.logging.Logger;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

/**
 *
 * @author Gert van der Plas <gertvdplas@gmail.com>
 */
public class RestTest {

    private static final Logger log = Logger.getLogger(RestTest.class.getName());

    public static void main(String[] args) {

        Client client = ClientBuilder.newClient();

        // Test the POST method
//        Customer customer = new Customer();
//        Address address = new Address();
//        customer.setAddress(address);
//
//        customer.setId(1);
//        customer.setFirstname("Duke");
//        customer.setLastname("OfJava");
//        address.setNumber(1);
//        address.setStreet("Duke's Drive");
//        address.setCity("JavaTown");
//        address.setZip("1234");
//        address.setState("JA");
//        address.setCountry("USA");
//        customer.setEmail("duke@java.net");
//        customer.setPhone("12341234");
//
//        ClientResponse response = 
//                webResource.type("application/xml").post(ClientResponse.class,
//                customer);
//
//        logger.info("POST status: {0}" + response.getStatus());
//        if (response.getStatus() == 201) {
//            logger.info("POST succeeded");
//        } else {
//            logger.info("POST failed");
//        }
        // Test the GET method using content negotiation
        
        DwoSystemParameters entity = 
                client.target("view-source:http://localhost:8080/DWO/DWOServer/jax-rs")
                        .path("serverstatus")
                        .request().get(DwoSystemParameters.class);
        
        System.out.println(entity.toString());
//
//        // Test the DELETE method
//        response = webResource.path("1").delete(ClientResponse.class);
//
//        logger.log(Level.INFO, "DELETE status: {0}", response.getStatus());
//        if (response.getStatus() == 204) {
//            logger.info("DELETE succeeded (no content)");
//        } else {
//            logger.info("DELETE failed");
//        }
//
//        response = webResource.path("1").accept(MediaType.APPLICATION_XML)
//                .get(ClientResponse.class);
//        logger.log(Level.INFO, "GET status: {0}", response.getStatus());
//        if (response.getStatus() == 204) {
//            logger.info("After DELETE, the GET request returned no content.");
//        } else {
//            logger.info("Failed, after DELETE, GET returned a response.");
//        }
//    }
    }
}
