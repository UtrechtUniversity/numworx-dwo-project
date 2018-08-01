// Source file:
// N:\\transferzone\\intern\\Afstudeerders_basw_thijsk\\April\\Implementatie\\fi\\dwo\\client\\persistence\\SchoolMapper.java
package fi.dwo.dwojapplet.persistence;

import fi.dwo.commons.persistence.MySQLPersistenceId;
import fi.dwo.dwojapplet.domain.School;
import fi.dwo.dwojapplet.domain.SchoolClass;
import fi.dwo.dwojapplet.domain.SchoolGroup;
import nl.uu.fi.dwo.lms.jclient.lib.rest.managers.SecureDwoAdminSchoolManager;
import nl.uu.fi.dwo.lms.jclient.lib.rest.managers.SecureTeacherFromToManager;
import nl.uu.fi.dwo.rest.dom.entities.DomSchool4DwoAdmin;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolFrom;
import nl.uu.fi.dwo.rest.dom.entities.util.AboType;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.xmlrpc.applet.XmlRpcException;

class SchoolMapper extends XmlRpcMapper<School> {
    private static final Logger LOG = Logger.getLogger(SchoolMapper.class.getName());

	// lazy evaluation.
    // DIT STAAT NU AAN!
    static class LazySchool extends School {

        /* (non-Javadoc)
         * @see fi.dwo.client.domain.School#getPasswd(int)
         */
        public String getPasswd(int groupID) {
            getSchoolGroupList();
            return super.getPasswd(groupID);
        }

        /* (non-Javadoc)
         * @see fi.dwo.client.domain.School#getClassList()
         */
        public SchoolClass[] getClassList() {
            SchoolClass[] classes = super.getClassList();
            if (classes != null) {
                return classes;
            }
            try {
                setClassList(MapperCreator.instance(SchoolClass.class).get(this));
            } catch (IOException e) {

                LOG.log(Level.SEVERE,null,e);
                return new SchoolClass[0];  // FIXME fatal, non fatal, retryable?
            } catch (SQLException e) {

                LOG.log(Level.SEVERE,null,e);
                return new SchoolClass[0]; // FIXME
            } catch (XmlRpcException e) {

                LOG.log(Level.SEVERE,null,e);
                return new SchoolClass[0]; // FIXME
            }

            return super.getClassList();
        }

        /* (non-Javadoc)
         * @see fi.dwo.client.domain.School#getSchoolGroupList()
         */
        public SchoolGroup[] getSchoolGroupList() {
            if (super.getSchoolGroupList() == null) {
                try {
                    setSchoolGroupList(MapperCreator.instance(SchoolGroup.class).get(this));
                } catch (IOException e) {
    
                    LOG.log(Level.SEVERE,null,e);
                } catch (SQLException e) {
    
                    LOG.log(Level.SEVERE,null,e);
                } catch (XmlRpcException e) {
    
                    LOG.log(Level.SEVERE,null,e);
                }
            }
            return super.getSchoolGroupList();
        }

        /**
         * assert classList != null.
         */
        public void addClass(SchoolClass c) {
			//getClassList();
            //super.addClass(c);
            super.setClassList(null);
        }

        /**
         * assert classList != null.
         */
        public void deleteClass(SchoolClass schoolClass) {
			//getClassList();
            //super.deleteClass(schoolClass);
            super.setClassList(null);
        }

    }

    private static final String TABLENAME = "tblSchool";

    private static final String IDCOL = "schoolID";

    private static final String ORDERCOL = "schoolName";

    /**
     *
     */
    public SchoolMapper() {

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
    public void put(int oid, School obj)  {
        System.err.println("SchoolMapper.put() Not yet implemented!");

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
    public School getObjectFromReturn(Hashtable data) throws IOException, SQLException, XmlRpcException {
        School s = null;
        if (data.get("schoolID") == null) { //We don't know enough to make a
            // schoolobject
            return null;
        } else if (data.get("schoolID") instanceof String) { //If it is a string, it was null
            return null;
        } else if (objects.containsKey(data.get("schoolID"))) { // Did we knew
            // the school?
            s = objects.get(data.get("schoolID"));
        } else {
            s = new LazySchool();
        }
        s = update(s, data);
        if (!objects.containsKey(new Integer(s.getSchoolID()))) {
            objects.put(new Integer(s.getSchoolID()), s);
        }
        return s;
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
    public School[] get(Object obj) throws IOException, SQLException,
            XmlRpcException {
        if (Boolean.TRUE.equals(obj)) {
//            Hashtable h = new Hashtable();
//            h.put("export", obj);
//            return super.get(h);
          try {
            List<DomSchoolFrom> list = SecureTeacherFromToManager.getExports();
            School[] result = createArray(list.size());
            for (int i = 0; i < result.length; i++) {
              int id = MySQLPersistenceId.getNativeId(list.get(i)).intValue();
              School s;
              if (objects.containsKey(Integer.valueOf(id))) {
                s = objects.get(Integer.valueOf(id));
                s.setName(list.get(i).getSchoolName());
              } else {
                s = new LazySchool();
                s.setName(list.get(i).getSchoolName());
                objects.put(Integer.valueOf(id), s);
              }
              result[i] = s;
            }
          } catch (Dwo2Exception e) {
            throw new IOException(e.getDwo2Message(), e);
          }
        }
        return get();
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
    protected School update(School obj, Hashtable data) throws IOException, SQLException, XmlRpcException {
        School s = obj;
        s.setSchoolID(((Integer) data.get("schoolID")).intValue());
        s.setName((String) data.get("schoolName"));
        s.setSchoolLogin((String) data.get("schoollogin"));
        if (!(s instanceof LazySchool)) {
            s.setSchoolGroupList((SchoolGroup[]) MapperCreator.instance(SchoolGroup.class).get(s));
        } else {
            s.setSchoolGroupList(null);
        }
        if (data.contains("image") && (!data.get("image").equals(""))) {
            s.setImage((String) data.get("image"));
        }
        if (!(s instanceof LazySchool) && s.getClassList() == null) {
            s.setClassList((SchoolClass[]) MapperCreator.instance(SchoolClass.class).get(s));
        } else if (s instanceof LazySchool) {
            s.setClassList(null);
        }
        
        if (data.containsKey("aboType")) {
          s.setAboType(AboType.values()[ (Integer) data.get("aboType")]);
        }

        //if(s.getClassList() == null) {
        //    s.setClassList((SchoolClass[]) MapperCreator.instance(SchoolClass.class).get(s));
        //}
        s.setExport(Boolean.TRUE.equals(data.get("export")));
        s.setRights((String) data.get("schoolRights"));
        Object expire = data.get("expire");
        if (expire instanceof Date) {
        	    Date date = (Date) expire;
        	    Calendar c = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        	    c.setTime(date);
        	    date = new Date(c.get(Calendar.YEAR)-1900,c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
            s.setExpire(date);
        } else {
            s.setExpire(null);
        }
        return s;
    }

    /* (non-Javadoc)
     * @see fi.dwo.client.persistence.XmlRpcMapper#createArray(int)
     */
    @Override
    protected School[] createArray(int size) {
        return new School[size];
    }

    /* (non-Javadoc)
     * @see fi.dwo.client.persistence.XmlRpcMapper#getOrderbyCol()
     */
    @Override
    protected String getOrderbyCol() {
        return ORDERCOL;
    }

    @Override
    public School[] get() throws IOException {
      try {
        List<DomSchool4DwoAdmin> list = SecureDwoAdminSchoolManager.getSchoolList();
        School[] schools = createArray(list.size());
        for (int i = 0; i < schools.length; i++) {
          schools[i] = getObjectFromReturn(list.get(i));
        }
        return schools;
      } catch (Dwo2Exception e) {
          throw new IOException(e.getDwo2Message(),e);
      }
    }

    private School getObjectFromReturn(DomSchool4DwoAdmin full) {
      int id = PersistenceFacade.idOf(full.getId());
      School s = objects.get(id);
      if(s == null) {
        s = new LazySchool();
      }
      s.setDomSchool(full);
      s.setSchoolLogin(full.getSchoolLogin());   
      objects.putIfAbsent(id, s);
      return s;
    }
    
    
}
