package fi.dwo.client.persistence;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Hashtable;

import org.apache.xmlrpc.applet.XmlRpcException;

import fi.dwo.server.persistence.DwoXmlRpcException;

/**
 * functor pattern.
 * Kan geoptimaliseerd worden als DbAccessIF extends DBAccessLogin
 */
interface DbAccessLogin {
	Hashtable login(String a, String b)
	throws IOException, SQLException, XmlRpcException, DwoXmlRpcException;
}