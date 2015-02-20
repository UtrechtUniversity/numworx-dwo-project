package fi.dwo.dwojapplet.persistence.cache;

import java.io.IOException;
import java.sql.SQLException;

import org.apache.xmlrpc.applet.XmlRpcException;

import fi.dwo.commons.exceptions.DwoXmlRpcException;
import fi.dwo.commons.exceptions.PersistenceException;
import fi.dwo.dwojapplet.persistence.DbAccessIF;

public class NoCache implements IStore {

    NoCache(DbAccessIF dbAccess) {
        this.dbAccess = dbAccess;
    }

    private DbAccessIF dbAccess;

    @Override
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

    @Override
    public String setValue(int uid, int scoid, String key, String value) throws PersistenceException {
        String random = Long.toHexString(Double.doubleToRawLongBits(Math.random()));
        String result;
        try {
            result = dbAccess.LMSSetValue(scoid, uid, key, value, random);
            if (result.equals(random)) {
                return "true"; // all's well
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

    @Override
    public String commit(int uid, int scoid, String param) {
        return "true";
    }

    @Override
    public void destroy() {
    }

    @Override
    public boolean changeSco(int scoid, String scoName, String description,
            boolean delete, String launchdataString, Boolean showScore) throws DwoXmlRpcException, IOException, XmlRpcException, SQLException {
        if (delete) {
            if (null != showScore) {
                return dbAccess.changeSco(scoid, scoName, description, launchdataString, showScore.booleanValue());
            } else {
                return dbAccess.changeSco(scoid, scoName, description, launchdataString);
            }
        } else {
            boolean result = dbAccess.changeSco(scoid, scoName, description, false, launchdataString);
            if (result && null != showScore) // heel onwaarschijnlijk?
            {
                dbAccess.changeSco(scoid, scoName, description, showScore.booleanValue());
            }
            return result;
        }
    }

    @Override
    public boolean changeSco(int scoid, String scoName, String description,
            boolean delete, byte[] launchdata, Boolean showScore) throws DwoXmlRpcException, IOException, XmlRpcException, SQLException {
        if (showScore == null) {
            showScore = Boolean.TRUE;
        }
        boolean result = dbAccess.changeSco(scoid, scoName, description, delete, launchdata, showScore);
        return result;
    }

    @Override
    public void clear(int scoid) {
    }

}
