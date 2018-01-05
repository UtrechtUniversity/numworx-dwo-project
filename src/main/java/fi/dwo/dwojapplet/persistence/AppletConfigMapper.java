// Source file:
// N:\\transferzone\\intern\\Afstudeerders_basw_thijsk\\April\\Implementatie\\fi\\dwo\\client\\persistence\\ScoMapper.java
package fi.dwo.dwojapplet.persistence;

import fi.dwo.commons.persistence.DbAccessIF;
import fi.dwo.dwojapplet.domain.AppletConfig;
import fi.dwo.dwojapplet.domain.DWO;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Locale;
import java.util.Vector;

import org.apache.xmlrpc.applet.XmlRpcException;

class AppletConfigMapper extends XmlRpcMapper {

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
     * @throws java.io.IOException
     * @throws org.apache.xmlrpc.applet.XmlRpcException
     * @throws java.sql.SQLException
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
     * @throws java.io.IOException
     * @throws org.apache.xmlrpc.applet.XmlRpcException
     * @throws java.sql.SQLException
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
     * @throws java.io.IOException
     * @throws org.apache.xmlrpc.applet.XmlRpcException
     * @throws java.sql.SQLException
     *
     */
    @Override
    public Object[] get(Object obj) throws IOException, SQLException,
            XmlRpcException {
        if (obj instanceof Locale) {
            Locale locale = (Locale) obj;
            Hashtable map = new Hashtable();
            map.put("language", locale.getLanguage());
            return filteredGet(map);
        }
        return super.get();
    }

	private Object[] filteredGet(Hashtable map) throws IOException, XmlRpcException, SQLException {
		DbAccessIF dbAccess = DbAccessCreator.instance();
		Vector<Hashtable> data = dbAccess.getTable(getTableName(), map, getOrderbyCol());
		Iterator<Hashtable> iter = data.iterator();
// Filter non profile configs
		while (iter.hasNext()) {
			Hashtable hashtable = iter.next();
			Object o = hashtable.get("dwoProfileID");
			if(o == null) continue;  // not in database
			if("".equals(o)) continue; // null = global config
			if(o.equals(Integer.valueOf(DWO.getDwoProfileID())))
				continue; // local to profile
			iter.remove();
		}
		
		int i;
		Object[] oa = createArray(data.size());
		for (i = 0; i < data.size(); i++) {
		    oa[i] = getObjectFromReturn((Hashtable) data.elementAt(i));
		}
		
		return oa;
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

    @Override
    public Object get(int uid, Integer sgid) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
