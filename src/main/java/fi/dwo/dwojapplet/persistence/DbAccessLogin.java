package fi.dwo.dwojapplet.persistence;

import fi.dwo.commons.exceptions.DwoXmlRpcException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Hashtable;
import org.apache.xmlrpc.applet.XmlRpcException;

/**
 * functor pattern. Moet public zijn, anders kan de jamon monitoring software er
 * niet bij. Kan geoptimaliseerd worden als DbAccessIF extends DBAccessLogin
 */
interface DbAccessLogin {

    public Hashtable login(String a, String b)
            throws IOException, SQLException, XmlRpcException, DwoXmlRpcException;
}
