package fi.dwo.dwojapplet.persistence;

import fi.dwo.dwojapplet.domain.AppletData;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Hashtable;
import org.apache.xmlrpc.applet.XmlRpcException;

class AppletDataMapper extends XmlRpcMapper {

    private static final String TABLENAME = "tblApplet";
    private static final String IDCOL = "appletID";
    private static final String ORDERCOL = "classname";

    @Override
    protected Object[] createArray(int size) {
        return new AppletData[size];
    }

    @Override
    protected String getIDCol() {
        return IDCOL;
    }

    @Override
    protected String getOrderbyCol() {
        return ORDERCOL;
    }

    @Override
    protected String getTableName() {
        return TABLENAME;
    }

    @Override
    protected Object update(Object obj, Hashtable data) {
        AppletData applet = (AppletData) obj;
        applet.setId((((Number) data.get(IDCOL)).intValue()));
        applet.setAppletName((String) data.get("appletName"));
        applet.setClassName((String) data.get(ORDERCOL));
        applet.setJarName((String) data.get("jarname"));
        applet.setFeatures((String) data.get("features"));
        return applet;
    }

    @Override
    public Object[] get(Object obj) throws IOException, SQLException,
            XmlRpcException {
        return get();
    }

    @Override
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
        if (!objects.containsKey(new Integer(s.getId()))) {
            objects.put(new Integer(s.getId()), s);
        }
        return s;
    }

    @Override
    public void put(int oid, Object obj) throws IOException, SQLException,
            XmlRpcException {
        System.err.println("AppletDataMapper.put() Not yet implemented!");

    }

    @Override
    public Object get(int uid, Integer sgid) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
