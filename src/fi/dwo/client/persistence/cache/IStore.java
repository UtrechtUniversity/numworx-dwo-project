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
	/**
	 * update SCO data in the store.
	 * @param scoid
	 * @param scoName
	 * @param description
	 * @param delete
	 * @param launchdataString base64 of zipped java serialized map
	 * @param showScore
	 * @return
	 * @throws DwoXmlRpcException
	 * @throws IOException
	 * @throws XmlRpcException
	 * @throws SQLException
	 * @deprecated we gaan naar de andere changeSco
	 */
	boolean changeSco(int scoid, String scoName, String description, boolean delete,
			String launchdataString, Boolean showScore) throws DwoXmlRpcException, IOException, XmlRpcException, SQLException;
	
	/**
	 * update SCO data in the store.
	 * @param scoid
	 * @param scoName
	 * @param description
	 * @param delete
	 * @param launchdata bytearray of zipped json object
	 * @param showScore
	 * @return
	 * @throws DwoXmlRpcException
	 * @throws IOException
	 * @throws XmlRpcException
	 * @throws SQLException
	 */
	boolean changeSco(int scoid, String scoName, String description, boolean delete,
			byte[] launchdata, Boolean showScore) throws DwoXmlRpcException, IOException, XmlRpcException, SQLException;
}
