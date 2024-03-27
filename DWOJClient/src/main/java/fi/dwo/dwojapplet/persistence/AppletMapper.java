/*
 * Created on Mar 2, 2005
 *
 */
package fi.dwo.dwojapplet.persistence;

import fi.beans.loader.Loader;
import fi.beans.mainframe.JApplet;
import fi.dwo.commons.exceptions.PersistenceException;
import fi.dwo.dwojapplet.domain.AppletData;
import fi.dwo.dwojapplet.domain.DwoHelper;
import fi.dwo.dwojapplet.gui.wiskopdr.WiskOpdrCache;

import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author M.J.B. Kupers
 *
 */
class AppletMapper  {

   private static final Logger LOG = Logger.getLogger(AppletMapper.class.getName());
   Hashtable<Integer, Class<JApplet>> objects = new Hashtable<>();

    /* (non-Javadoc)
     * @see fi.dwo.client.persistence.XmlRpcMapper#get(int)
     */
    Class<JApplet> get(int oid) throws PersistenceException {
        Class<JApplet> object = objects.get(oid);
        if (object != null) return object;
        AppletData data = PersistenceFacade.instance().getAppletData(oid);
        String className = data.getClassName();
        String jarname = data.getJarName();
        try {
          if(false) return (Class<JApplet>) Class.forName(className); // if debugging.
//caching:
          if(jarname.equals(WiskOpdrCache.WISKOPDR_JAR) && className.equals(WiskOpdrCache.WISKOPDR))
              object = (Class<JApplet>) WiskOpdrCache.getInstance();
          else
              object = (Class<JApplet>) Loader.create(jarname).loadClass(className);
      } catch (ClassNotFoundException e1) {
          //try loading the jar locally (might be updated).
          LOG.log(Level.FINE, "Can't load class {0} in jar {1} from remote server. ", new Object[]{className, jarname});
          if (DwoHelper.isSecure()) {
              try {
                 object  = (Class<JApplet>) Class.forName(className);
              } catch (ClassNotFoundException e2) {
                  e1 = e2;
              }
          }
          LOG.log(Level.SEVERE, "load applet", e1);
          throw new PersistenceException(-1, e1);
      }
       if (object != null) {
            objects.put(oid, object);
        }
        return object;
    }


}
