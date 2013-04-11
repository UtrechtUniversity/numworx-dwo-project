// Generated code, do not edit
package fi.servlet.dwomaccess;

import java.util.Vector;
import java.net.URL;
import fi.beans.xmlrpc.Client;
import org.apache.xmlrpc.applet.XmlRpcException;
import java.io.IOException;

public class PartialScoreClient extends Client implements fi.servlet.dwomaccess.PartialScoreIF {

	public PartialScoreClient(URL u) {
		super(u);
	}

    public java.util.Vector getScoreMapList(int a, int b) throws IOException, XmlRpcException
    {
        Vector vv = new Vector(2);
        vv.addElement( new Integer(a));
        vv.addElement( new Integer(b));
        Object object = invoke("getScoreMapList", vv);
        return (java.util.Vector)object;
    }

    public java.lang.String getLaunchData(int a) throws IOException, XmlRpcException
    {
        Vector vv = new Vector(1);
        vv.addElement( new Integer(a));
        Object object = invoke("getLaunchData", vv);
        return (java.lang.String)object;
    }

    public java.lang.String getCourseDescription(int a) throws IOException, XmlRpcException
    {
        Vector vv = new Vector(1);
        vv.addElement( new Integer(a));
        Object object = invoke("getCourseDescription", vv);
        return (java.lang.String)object;
    }

}
