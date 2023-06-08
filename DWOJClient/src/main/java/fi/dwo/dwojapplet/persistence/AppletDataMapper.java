package fi.dwo.dwojapplet.persistence;

import fi.dwo.commons.exceptions.PersistenceException;
import fi.dwo.commons.persistence.MySQLPersistenceId;
import fi.dwo.commons.persistence.entities.PersistentApplet;
import fi.dwo.dwojapplet.domain.AppletData;
import nl.uu.fi.dwo.lms.jclient.lib.rest.managers.PublicAppletManager;
import nl.uu.fi.dwo.rest.dom.entities.DomAppletFull;
import nl.uu.fi.dwo.rest.dom.entities.DomAppletId;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;

import java.util.Hashtable;
import java.util.Map;

class AppletDataMapper  {

    Map<Integer, AppletData> objects  = new Hashtable<>();
//    private static final String TABLENAME = "tblApplet";
//    private static final String IDCOL = "appletID";
//    private static final String ORDERCOL = "classname";


//    public AppletData[] get() throws IOException {
//      try {
//        List<DomAppletFull> list = SecureDwoAdminAppletManager.getApplets();
//        AppletData[] result = createArray(list.size());
//        for (int i = 0; i < result.length; i++) {
//          result[i] = getObjectFromReturn(list.get(i));
//        }
//        return result;
//      } catch (Dwo2Exception e) {
//        throw new IOException(e.getDwo2Message(),e);
//      }
//    }

    private AppletData getObjectFromReturn(DomAppletFull dom) throws Dwo2Exception {
      int id = MySQLPersistenceId.getNativeId(dom).intValue();
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

    /* (non-Javadoc)
     * @see fi.dwo.dwojapplet.persistence.XmlRpcMapper#get(int)
     */
    public AppletData get(int oid)
        throws PersistenceException {
      AppletData s = objects.get(oid);
      if(s != null) return s;

      PersistenceId id = PersistentApplet.buildPersistenceId((long)oid);
      DomAppletId appletid = new DomAppletId(id);
      DomAppletFull applet;
      try {
        applet = PublicAppletManager.getApplet(appletid);
        return getObjectFromReturn(applet);
      } catch (Dwo2Exception e) {
        throw new PersistenceException(PersistenceException.EX_XML_RPC,e);
      }
    }

    
    
}
