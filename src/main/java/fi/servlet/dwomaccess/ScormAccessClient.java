// Generated code, do not edit
package fi.servlet.dwomaccess;

import java.util.Vector;
import java.net.URL;
import fi.beans.xmlrpc.Client;
import org.apache.xmlrpc.applet.XmlRpcException;
import java.io.IOException;

public class ScormAccessClient extends Client implements fi.servlet.dwomaccess.ScormAccessIF {

	public ScormAccessClient(URL u) {
		super(u);
	}

        @Override
    public boolean Commit(int a, int b, java.util.Hashtable c) throws IOException, XmlRpcException
    {
        Vector vv = new Vector(3);
        vv.addElement( new Integer(a));
        vv.addElement( new Integer(b));
        vv.addElement(c);
        Object object = invoke("Commit", vv);
        return ((Boolean)object).booleanValue();
    }

        @Override
    public java.util.Hashtable Initialize(int a, int b) throws IOException, XmlRpcException
    {
        Vector vv = new Vector(2);
        vv.addElement( new Integer(a));
        vv.addElement( new Integer(b));
        Object object = invoke("Initialize", vv);
        return (java.util.Hashtable)object;
    }

}
