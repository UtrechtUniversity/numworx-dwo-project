// Source file:
// N:\\transferzone\\intern\\Afstudeerders_basw_thijsk\\April\\Implementatie\\fi\\dwo\\client\\persistence\\ScoMapper.java
package fi.dwo.dwojapplet.persistence;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.Locale;

import org.apache.xmlrpc.applet.XmlRpcException;

import fi.dwo.dwojapplet.domain.AppletConfig;

public class AppletConfigMapper extends XmlRpcMapper {

    private static final String TABLENAME = "tblAppletConfig";

    private static final String IDCOL = "appletConfigID";

    private static final String ORDERCOL = "name";

    /**
     *
     */
    public AppletConfigMapper() {

    }

    /**
     * @param oid
     * @param obj
     * @throws org.apache.xmlrpc.applet.XmlRpcException
     *
     */
    @Override
    public void put(int oid, Object obj) throws IOException, SQLException,
            XmlRpcException {
        System.err.println("AppletConfigMapper.put() Not yet implemented!");
    }

    /**
     * @param data
     * @return Object
     * @throws org.apache.xmlrpc.applet.XmlRpcException
     *
     */
    @Override
    public Object getObjectFromReturn(Hashtable data) throws IOException, SQLException, XmlRpcException {
        AppletConfig ac = null;
        if (data.get("appletConfigID") == null) { //We don't know enough to make a
            // AppletConfigobject
            return null;
        } else if (objects.containsKey(data.get("appletConfigID"))) { // Did we know the
            // AppletConfig?
            ac = (AppletConfig) objects.get(data.get("appletConfigID"));
        } else {
            ac = new AppletConfig();
        }
        ac = (AppletConfig) update(ac, data);
        if (!objects.containsKey(new Integer(ac.getID()))) {
            objects.put(new Integer(ac.getID()), ac);
        }
        return ac;
    }

    /**
     * @param obj
     * @return Object[]
     * @throws org.apache.xmlrpc.applet.XmlRpcException
     *
     */
    @Override
    public Object[] get(Object obj) throws IOException, SQLException,
            XmlRpcException {
        if (obj instanceof Locale) {
            Locale locale = (Locale) obj;
            Hashtable map = new Hashtable();
            map.put("language", locale.getLanguage());
            return super.get(map);
        }
        return super.get();
    }

    /*
     * (non-Javadoc)
     * 
     * @see fi.dwo.client.persistence.XmlRpcMapper#getIDCol()
     */
    @Override
    protected String getIDCol() {
        return IDCOL;
    }

    /*
     * (non-Javadoc)
     * 
     * @see fi.dwo.client.persistence.XmlRpcMapper#getTableName()
     */
    @Override
    protected String getTableName() {
        return TABLENAME;
    }

    /*
     * (non-Javadoc)
     * 
     * @see fi.dwo.client.persistence.XmlRpcMapper#update(java.lang.Object,
     *      java.util.Hashtable)
     */
    @Override
    protected Object update(Object obj, Hashtable data) throws IOException, SQLException, XmlRpcException {
        AppletConfig ac = (AppletConfig) obj;
        ac.setAppletConfigID(((Integer) data.get("appletConfigID")).intValue());
        ac.setLaunchdata((String) data.get("launchdata"));
        ac.setName((String) data.get("name"));
        if (!data.get("appletID").equals("")) {
            ac.setAppletID(((Integer) data.get("appletID")).intValue());
        }
        Object lang = data.get("language");
        if (lang != null && !lang.equals("")) {
            ac.setLanguage(String.valueOf(lang));
        }
        return ac;
    }

    /* (non-Javadoc)
     * @see fi.dwo.client.persistence.XmlRpcMapper#createArray(int)
     */
    @Override
    protected Object[] createArray(int size) {
        return new AppletConfig[size];
    }

    /* (non-Javadoc)
     * @see fi.dwo.client.persistence.XmlRpcMapper#getOrderbyCol()
     */
    @Override
    protected String getOrderbyCol() {
        return ORDERCOL;
    }
}
