// Source file:
// N:\\transferzone\\intern\\Afstudeerders_basw_thijsk\\April\\Implementatie\\fi\\dwo\\client\\persistence\\ScoMapper.java
package fi.dwo.dwojapplet.persistence;

import fi.beans.private_base64code.StringCodeObject;
import fi.dwo.commons.exceptions.PersistenceException;
import fi.dwo.commons.persistence.MySQLPersistenceId;
import fi.dwo.commons.persistence.entities.PersistentCourse;
import fi.dwo.dwojapplet.domain.Course;
import fi.dwo.dwojapplet.domain.DWO;
import fi.dwo.dwojapplet.domain.Sco;
import nl.uu.fi.dwo.lms.jclient.lib.rest.managers.PublicScoContextManager;
import nl.uu.fi.dwo.rest.dom.entities.DomCourse;
import nl.uu.fi.dwo.rest.dom.entities.DomScoContext;
import nl.uu.fi.dwo.rest.dom.entities.DomScoContextId;
import nl.uu.fi.dwo.rest.dom.entities.DomScoData;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;

import org.apache.xmlrpc.applet.XmlRpcException;
import org.json.simple.JSONValue;
import org.osgi.util.promise.Promise;

class ScoMapper  {
    private static final Logger LOG = Logger.getLogger(ScoMapper.class.getName());
    private Map<Integer, Sco> objects = new Hashtable<>();

    class LazySco extends Sco {

        Promise<DomScoData> pdata;
        DomScoContextId domScoId;
    
        LazySco(DomScoContext item) {
          domScoId = item;
          //pdata = PublicScoContextManager.getDataAsync(domScoId, DWO.getDwoProfile(), null);
        }

        @Override
        public Hashtable getLaunchdata() {
            if (this.launchdata != null) {
                return launchdata;
            }
            if (pdata == null) {
              pdata = PublicScoContextManager.getDataAsync(domScoId, DWO.getDwoProfile(), null);
            }
            String l = "";
            try {
              l = pdata.getValue().getLaunchdata();
            } catch (InvocationTargetException e) {
              LOG.log(Level.SEVERE, "getLaunchdata", e);
            } catch (InterruptedException e) {
             }

            if (l != null && !l.isEmpty()) {
              ClassLoader cl = null;
              try {
                Class<?> clazz = PersistenceFacade.instance().getAppletClass(getAppletID());
                cl = clazz.getClassLoader();
              } catch (PersistenceException e) {
              }
              launchdata = (Hashtable) StringCodeObject.decodeStringToObject(l, cl);
            }    
            return super.getLaunchdata();
        }

        @Override
        public String getDescription() {
          if (super.getDescription() == null) {
            if (pdata == null) {
              pdata = PublicScoContextManager.getDataAsync(domScoId, DWO.getDwoProfile(), null);
            }
            try {
              setDescription(pdata.getValue().getDescription());
            } catch (InvocationTargetException e) {
              LOG.log(Level.SEVERE, "getDescription", e);
            } catch (InterruptedException e) {
            }
          }          
          return super.getDescription();
        }

    }

    /**
     *
     */
    ScoMapper() {

    }

    /**
     * @param oid
     * @param obj
     * @throws java.io.IOException
     * @throws java.sql.SQLException
     *
     */
    void put(int oid, Sco obj)  {
        objects.put(Integer.valueOf(oid), obj);
    }

   	Sco get(int oid) throws PersistenceException {
		return objects.get(oid);
	}
   	
    /* (non-Javadoc)
     * @see fi.dwo.client.persistence.XmlRpcMapper#createArray(int)
     */
    protected Sco[] createArray(int size) {
        return new Sco[size];
    }



    
    /* (non-Javadoc)
     * @see fi.dwo.client.persistence.XmlRpcMapper#removeAllObjects()
     */
    void removeAllObjects() {
        objects.clear();
    }

    /* (non-Javadoc)
     * @see fi.dwo.client.persistence.XmlRpcMapper#removeObject(int)
     */
    void removeObject(int key) {
    }

    public Sco[] get(Course course) throws PersistenceException {
      DomCourse parent = new DomCourse(PersistentCourse.buildPersistenceId(Long.valueOf(course.getID())));
      Promise<List<DomScoContext>> p = PublicScoContextManager.getScosAsync(parent, DWO.getDwoProfile(), null);
      
      try {
        return toSco(course, p.getValue());
      } catch (InvocationTargetException e) {
        throw new PersistenceException(PersistenceException.EX_XML_RPC, e);
      } catch (InterruptedException e) {
        return null;
      }
    }
    
    public Sco[] getTrash(Course course) throws PersistenceException {
      DomCourse parent = new DomCourse(PersistentCourse.buildPersistenceId(Long.valueOf(course.getID())));
      Promise<List<DomScoContext>> p = PublicScoContextManager.getTrashAsync(parent, DWO.getDwoProfile());
      
      try {
        return toSco(course, p.getValue());
      } catch (InvocationTargetException e) {
        throw new PersistenceException(PersistenceException.EX_XML_RPC, e);
      } catch (InterruptedException e) {
        return null;
      }
     
    }

    public Sco[] toSco(Course parent, List<DomScoContext> value) throws PersistenceException {
      Sco[] result = createArray(value.size());
      int i = 0;
      for(DomScoContext item: value) {
        int id;
        try {
          id = MySQLPersistenceId.getNativeId(item).intValue();
        } catch (Dwo2Exception e) {
          throw new PersistenceException(PersistenceException.EX_XML_RPC, e);
        }
        Sco s = objects.get(id);
        if (s == null) {
          s = new LazySco(item);
        }
        s.setScoID(id);
        s.setName(item.getScoName());
        s.setSequencenr(item.getSequencenr());
        s.setAppletID(PersistenceFacade.idOf(item.getAppletId()));
        s.setCourse(parent);
        s.setShowScore(!Boolean.TRUE.equals(item.getShowScore())); // Reverse logic, 
        s.setShowDocent(item.getShowDocent());
        s.setCourseChanged(false);
        objects.putIfAbsent(id, s);
        result[i++] = s;
      }
      return result;
    }

}
