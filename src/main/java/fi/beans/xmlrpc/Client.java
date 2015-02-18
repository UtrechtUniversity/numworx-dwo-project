package fi.beans.xmlrpc;

import java.io.IOException;
import java.net.URL;
import java.util.Vector;

import org.apache.xmlrpc.applet.SimpleXmlRpcClient;
import org.apache.xmlrpc.applet.XmlRpcException;

/**
 * Abstract superclass voor jxmlrpcc gegenereerde stubs.
 *
 * @author wim
 *
 *
 */
public abstract class Client {

    private SimpleXmlRpcClient client;

    protected Object invoke(String naam, Vector params)
            throws IOException, XmlRpcException {
        return client.execute(naam, params);
    }

    protected Client(URL u) {
        client = new SimpleXmlRpcClient(u);

    }

}
