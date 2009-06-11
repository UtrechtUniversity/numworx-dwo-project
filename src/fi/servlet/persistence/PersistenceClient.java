package fi.servlet.persistence;
import java.util.Vector;
import java.net.URL;
import java.io.IOException;
import org.apache.xmlrpc.applet.*;

public abstract class PersistenceClient
{
	private SimpleXmlRpcClient client;
	protected Object invoke(String naam, Vector params)
	throws IOException, XmlRpcException
	{
		return client.execute(naam, params);
	}

	protected PersistenceClient(URL u)
	{
		client = new SimpleXmlRpcClient(u);

	}
}
