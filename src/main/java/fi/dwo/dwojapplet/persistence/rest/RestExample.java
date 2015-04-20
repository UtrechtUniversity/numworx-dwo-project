package fi.dwo.dwojapplet.persistence.rest;

import fi.dwo.commons.persistence.entities.PersistentUser;
import java.util.List;
import java.util.logging.Logger;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;

/**
 *
 * @author Gert van der Plas <gertvdplas@gmail.com>
 */
public class RestExample {

    private static final Logger log = Logger.getLogger(RestExample.class.getName());

    private static String ErrorMessageInResponse(final Response response) {
        String message = response.toString();
        final MultivaluedMap<String, String> headers = response.getStringHeaders();
        if (headers != null) {
            final List<String> warnings = headers.get("Warning");
            if (warnings != null && warnings.size() > 0) {
                message = message + " - Warning " + warnings.get(0);
            }
        }
        return message;
    }

    public static void main(String[] args) {

        //ensures authentication for REST request.
        HttpAuthenticationFeature feature = HttpAuthenticationFeature.universalBuilder().credentialsForDigest("gert_project", "3f4bfc028f72ef8502f49fa86f0e2823").build();
        Client client = ClientBuilder.newClient().register(feature);

//        //getjson
//        GenericType<List<PersistentDwoSystemParameters>> dwoParamType = new GenericType<List<PersistentDwoSystemParameters>>() {
//        };
//        List<PersistentDwoSystemParameters> entities = client.target("http://localhost:8080")
// //               .property(HTTP_AUTHENTICATION_BASIC_USERNAME, "joe")
// //               .property(HTTP_AUTHENTICATION_BASIC_PASSWORD, "p1swd745")
//                //or
//                .property(HTTP_AUTHENTICATION_DIGEST_USERNAME, "gert_project")
//                .property(HTTP_AUTHENTICATION_DIGEST_PASSWORD, "3f4bfc028f72ef8502f49fa86f0e2823")
//                .path("/DWO/DWOServer/jax-rs/serverstatus/json")
//                .request().accept(MediaType.APPLICATION_JSON).get(dwoParamType);
//        System.out.println(entities.size());
//        for (PersistentDwoSystemParameters p : entities) {
//            System.out.println(p.getName() + " " + p.getValue());
//        }
        PersistentUser user = client.target("http://localhost:8080")
                .path("/DWO/DWOServer/rest/secure/gui/panels/userprofile/get/json")
                //                .property(HTTP_AUTHENTICATION_DIGEST_USERNAME, "gert_project")
                //                .property(HTTP_AUTHENTICATION_DIGEST_PASSWORD, "3f4bfc028f72ef8502f49fa86f0e2823")
                .request().accept(MediaType.APPLICATION_JSON).get(PersistentUser.class);
        System.out.println("username: " + user.getUsername());
        System.out.println("email: " + user.getEmail());

        user.setUsername("wim-student@medal");
        user.setEmail(user.getEmail() + "x");
        Response response = client.target("http://localhost:8080")
                .path("/DWO/DWOServer/rest/secure/gui/panels/userprofile/update/json")
                //                .property(HTTP_AUTHENTICATION_DIGEST_USERNAME, "gert_project")
                //                .property(HTTP_AUTHENTICATION_DIGEST_PASSWORD, "3f4bfc028f72ef8502f49fa86f0e2823")
                .request().put(Entity.entity(user, MediaType.APPLICATION_JSON));
        if (response.getStatus() != 200) {
            System.out.println("Code: " + response.getStatus() + ". Reason: " + ErrorMessageInResponse(response));
        } else {
            System.out.println(user.getUsername());
            System.out.println("email: " + user.getEmail());
        }
        user = client.target("http://localhost:8080")
                .path("/DWO/DWOServer/rest/secure/gui/panels/userprofile/get/json")
                //                .property(HTTP_AUTHENTICATION_DIGEST_USERNAME, "gert_project")
                //                .property(HTTP_AUTHENTICATION_DIGEST_PASSWORD, "3f4bfc028f72ef8502f49fa86f0e2823")
                .request().accept(MediaType.APPLICATION_JSON).get(PersistentUser.class);
        System.out.println("username: " + user.getUsername());
        System.out.println("email: " + user.getEmail());

    }
}
