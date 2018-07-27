package fi.dwo.dwojapplet.persistence;

import fi.dwo.dwojapplet.domain.AppletData;
import nl.uu.fi.dwo.lms.jclient.lib.rest.managers.SecureDwoAdminAppletManager;
import nl.uu.fi.dwo.rest.dom.entities.DomAppletFull;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.List;

import org.apache.xmlrpc.applet.XmlRpcException;

class AppletDataMapper extends XmlRpcMapper<AppletData> {

    private static final String TABLENAME = "tblApplet";
    private static final String IDCOL = "appletID";
    private static final String ORDERCOL = "classname";

    @Override
    protected AppletData[] createArray(int size) {
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
    protected AppletData update(AppletData obj, Hashtable data) {
        AppletData applet = obj;
        applet.setId((((Number) data.get(IDCOL)).intValue()));
        applet.setAppletName((String) data.get("appletName"));
        applet.setClassName((String) data.get(ORDERCOL));
        applet.setJarName((String) data.get("jarname"));
        applet.setFeatures((String) data.get("features"));
        return applet;
    }

    @Override
    public AppletData[] get(Object obj) throws IOException, SQLException,
            XmlRpcException {
        return get();
    }

    @Override
    public AppletData getObjectFromReturn(Hashtable data) throws IOException,
            SQLException, XmlRpcException {
        AppletData s = null;
        if (data.get("appletID") == null) { //We don't know enough to make a
            // schoolobject
            return null;
        } else if (data.get("appletID") instanceof String) { //If it is a string, it was null
            return null;
        } else if (objects.containsKey(data.get("appletID"))) { // Did we knew
            // the applet?
            s = objects.get(data.get("appletID"));
        } else {
            s = new AppletData();
        }
        s = update(s, data);
        if (!objects.containsKey(new Integer(s.getId()))) {
            objects.put(new Integer(s.getId()), s);
        }
        return s;
    }

    @Override
    public void put(int oid, AppletData obj)  {
        System.err.println("AppletDataMapper.put() Not yet implemented!");
    }

    @Override
    public AppletData[] get() throws IOException {
      try {
        List<DomAppletFull> list = SecureDwoAdminAppletManager.getApplets();
        AppletData[] result = createArray(list.size());
        for (int i = 0; i < result.length; i++) {
          result[i] = getObjectFromReturn(list.get(i));
        }
        return result;
      } catch (Dwo2Exception e) {
        throw new IOException(e.getDwo2Message(),e);
      }
    }

    private AppletData getObjectFromReturn(DomAppletFull dom) {
      int id = PersistenceFacade.idOf(dom.getId());
      AppletData s = objects.get(id);
      if(s == null) 
        s = new AppletData();
      s.setId(id);
      s.setAppletName(dom.getAppletName());
      s.setClassName(dom.getClassname());
      s.setFeatures(dom.getFeatures());
      s.setJarName(dom.getJarname());
      objects.putIfAbsent(id, s);
      return s;
    }

}
