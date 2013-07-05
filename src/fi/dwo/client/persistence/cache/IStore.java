package fi.dwo.client.persistence.cache;

import java.io.IOException;
import java.sql.SQLException;

import org.apache.xmlrpc.applet.XmlRpcException;

import fi.dwo.client.system.PersistenceException;
import fi.dwo.server.persistence.DwoXmlRpcException;

public interface IStore {
	String getValue(int uid, int scoid, String key) throws PersistenceException;
	String setValue(int uid, int scoid, String key, String value) throws PersistenceException;
	String commit(int uid, int scoid, String param) throws PersistenceException;
	void destroy();
	boolean changeSco(int scoid, String scoName, String description, boolean delete,
			String launchdataString, Boolean showScore) throws DwoXmlRpcException, IOException, XmlRpcException, SQLException;
}
