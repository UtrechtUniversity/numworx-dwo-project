package fi.dwo.client.persistence;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Hashtable;

import org.apache.xmlrpc.applet.XmlRpcException;

import fi.dwo.client.domain.AppletData;
import fi.dwo.client.domain.School;
import fi.dwo.client.persistence.SchoolMapper.LazySchool;
import fi.dwo.client.system.PersistenceException;

public class AppletDataMapper extends XmlRpcMapper {

    private static final String TABLENAME = "tblApplet";
    private static final String IDCOL = "appletID";
    private static final String ORDERCOL = "classname";

    protected Object[] createArray(int size) {
		return new AppletData[size];
	}

	protected String getIDCol() {
		return IDCOL;
	}

	protected String getOrderbyCol() {
		return ORDERCOL;
	}

	protected String getTableName() {
		return TABLENAME;
	}

	protected Object update(Object obj, Hashtable data) {
		AppletData applet = (AppletData)obj;
		applet.setId((((Number) data.get(IDCOL)).intValue()));
		applet.setAppletName((String)data.get("appletName"));
		applet.setClassName( (String)data.get(ORDERCOL));
		applet.setJarName((String)data.get("jarname"));
		return applet;
	}

	public Object[] get(Object obj) throws IOException, SQLException,
			XmlRpcException {
		return get();
	}

	public Object getObjectFromReturn(Hashtable data) throws IOException,
			SQLException, XmlRpcException {
        AppletData s = null;
        if (data.get("appletID") == null) { //We don't know enough to make a
                                            // schoolobject
            return null;
        } else if (data.get("appletID") instanceof String) { //If it is a string, it was null
            return null;
        } else if (objects.containsKey(data.get("appletID"))) { // Did we knew
                                                                // the applet?
            s = (AppletData) objects.get(data.get("appletID"));
        } else {
            s = new AppletData();
        }
        s = (AppletData) update(s, data);
        if(!objects.containsKey(new Integer(s.getId()))) {
            objects.put(new Integer(s.getId()), s);
        }
        return s;
	}

	public void put(int oid, Object obj) throws IOException, SQLException,
			XmlRpcException {
        System.err.println("AppletDataMapper.put() Not yet implemented!");

	}

}
