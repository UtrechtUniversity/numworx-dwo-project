package fi.dwo.client.persistence.cache;

import java.io.IOException;
import java.sql.SQLException;

import org.apache.xmlrpc.applet.XmlRpcException;

import fi.dwo.client.persistence.DbAccessIF;
import fi.dwo.client.system.PersistenceException;

public class NoCache implements IStore {

	NoCache(DbAccessIF dbAccess) {
		this.dbAccess = dbAccess;
	}

	private DbAccessIF dbAccess;
	
	
	public String getValue(int uid, int scoid, String key) throws PersistenceException {
		String result = null;
		try {
			result = dbAccess.LMSGetValue(scoid, uid, key);
        } catch (IOException e) {
            throw new PersistenceException(PersistenceException.EX_IO, e);
        } catch (XmlRpcException e) {
            throw new PersistenceException(PersistenceException.EX_XML_RPC, e);
        } catch (SQLException e) {
            throw new PersistenceException(PersistenceException.EX_DB, e);
        }
		return result;
	}

	public String setValue(int uid, int scoid, String key, String value) throws PersistenceException {
        String random = Long.toHexString(Double.doubleToRawLongBits(Math.random()));
        String result;
        try {
			result = dbAccess.LMSSetValue(scoid, uid, key, value, random);
			if( result.equals(random))
			{	return "true"; // all's well
			}
		} catch (IOException e) {
			//log(e.getMessage());
            throw new PersistenceException(PersistenceException.EX_IO, e);
        } catch (XmlRpcException e) {
        	log(e.getMessage());
            throw new PersistenceException(PersistenceException.EX_XML_RPC, e);
        } catch (SQLException e) {
        	log(e.getMessage());
            throw new PersistenceException(PersistenceException.EX_DB, e);
        }
    	result = "LMSSetValue " + key + ": " + result + " <> " + random;
    	log(result);
    	throw new PersistenceException(PersistenceException.EX_DB);
    }

	private void log(String result) {
		try {
			dbAccess.log(result);
		} catch (Exception e) {
			System.err.println(result);
			e.printStackTrace();
		} 
	}

	public String commit(int uid, int scoid, String param) {
		return "true";
	}

	public void destroy() {
	}

}
