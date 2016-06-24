package fi.dwo.dwojapplet.REST;

import fi.dwo.commons.system.MD5;
import fi.dwo.commons.util.RandomPasswordGenerator;
import fi.dwo.rest.exceptions.Dwo2Exception;
import fi.dwo.rest.exceptions.Dwo2ExceptionCode;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

/**
 *
 * @author G.A.J. van der Plas
 */
public class SimpleDigestHttpClient {

    private String nonce = null;
    private String cnonce = null;
    private volatile long nonceCounter = 1;
    private String username = null;
    private String password = null;
    private URL hostUrl = null;

    /** BROKEN CODE 
     */
    public SimpleDigestHttpClient(URL HostUrl, String aUsername, String aPassword) throws IOException {
        username = aUsername;
        password = aPassword;
        hostUrl = HostUrl;
        //generate random cnonce just once 
        RandomPasswordGenerator generator = RandomPasswordGenerator
                .instance();
        cnonce = generator.Generate(5);

    }

    synchronized public HttpURLConnection digestGet(String path) throws IOException, Dwo2Exception {
        URL url = new URL(hostUrl, path);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        if (nonce == null) {
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");
            conn.setUseCaches(false);
            conn.connect();
            if (conn.getResponseCode() != 401) {
                throw new Dwo2Exception(Dwo2ExceptionCode.Rest_InterfaceError, conn.getResponseMessage());
            }
            //expected something like:
            //Digest realm="DWO.nl",
            //qop="auth", nonce="1466692564790:cd52e54a879ebd2ed290735d2ab81f5f",
            //opaque="5AF4D59F74F810FC2644FDD72C1FCFE8"
            String digestField = conn.getHeaderField("WWW-Authenticate");
            String[] digestFields = digestField.split(",");
            HashMap<String, String> digestMap = new HashMap<>();
            for (String field : digestFields) {
                String[] duple = new String[2];
                duple = field.split("=");
                digestMap.put(duple[0].trim(), duple[1].trim().split("\"")[1]); //strip quotes
            }
            digestMap.size();
            // set nonce     
            if (digestMap.get("nextnonce") != null) {
                nonce = digestMap.get("nextnonce");
            } else {
                nonce = digestMap.get("nonce");
            }
            conn.disconnect();
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");
            String HA1 = MD5.getHashString(username + ":" + digestMap.get("Digest realm") + ":" + password);
            String HA2 = MD5.getHashString("GET:" + "/dwo/"+path);
            String response = MD5.getHashString(HA1 + ":" + nonce + ":" + HA2);

            //build Authorization header field.
            StringBuilder authString = new StringBuilder();
            authString.append("Digest username=")
                    .append("\"").append(username).append("\", ")
                    .append("realm=\"").append(digestMap.get("Digest realm")).append("\", ")
                    .append("nonce=\"").append(digestMap.get("nonce")).append("\", ")
                    .append("uri=\"").append("/dwo/"+path).append("\", ")
                    .append("qop=\"").append(digestMap.get("qop")).append("\", ")
                    .append("nc=\"").append(nonceCounter++).append("\", ")
                    .append("cnonce=\"").append(cnonce).append("\", ")
                    .append("response=\"").append(response).append(", ")
                    .append("opaque=").append(digestMap.get("opaque")).append(" ");
            conn.setRequestProperty("Authorization", authString.toString());
            conn.setUseCaches(false);
            conn.connect();

            //Authorization: Digest username="me",
            //         realm="DWO account credentials required.",
            //         nonce="dcd98b7102dd2f0e8b11d0f600bfb0c093",
            //         uri="/dir/index.html",
            //         qop=auth,
            //         nc=00000001,
            //         cnonce="0a4f113b",
            //         response="6629fae49393a05397450978507c4ef1",
            //         opaque="5ccc069c403ebaf9f0171e9517f40e41"            
        }
        //test if we have a none. then return conn
        //if no none then get it
        //and open an authenticated connection and return
        return conn;
    }

}
