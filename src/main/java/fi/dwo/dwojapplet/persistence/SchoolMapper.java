// Source file:
// N:\\transferzone\\intern\\Afstudeerders_basw_thijsk\\April\\Implementatie\\fi\\dwo\\client\\persistence\\SchoolMapper.java
package fi.dwo.dwojapplet.persistence;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import fi.dwo.commons.exceptions.PersistenceException;
import fi.dwo.commons.persistence.MySQLPersistenceId;
import fi.dwo.commons.persistence.entities.PersistentSchool;
import fi.dwo.dwojapplet.domain.School;
import fi.dwo.dwojapplet.domain.SchoolClass;
import nl.uu.fi.dwo.lms.jclient.lib.rest.managers.SecureDwoAdminSchoolManager;
import nl.uu.fi.dwo.lms.jclient.lib.rest.managers.SecureTeacherFromToManager;
import nl.uu.fi.dwo.rest.dom.entities.DomSchool;
import nl.uu.fi.dwo.rest.dom.entities.DomSchool4DwoAdmin;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolFrom;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;

class SchoolMapper  {
    private static final Logger LOG = Logger.getLogger(SchoolMapper.class.getName());

    //private final SchoolGroupMapper SCHOOLGROUP_MAPPER = new SchoolGroupMapper();
    private Map<Integer, School> objects = new HashMap<>();

    // lazy evaluation.
    // DIT STAAT NU AAN!
    class LazySchool extends School {

        Boolean export;
        
        @Override
        public void setExport(boolean export) {
          this.export = export;
        }

        @Override
        public boolean isExport() {
          if (export == null) {
            try {
              List<DomSchoolFrom> list = SecureTeacherFromToManager.getExports();
              PersistenceId aId = PersistentSchool.buildPersistenceId(Long.valueOf(getSchoolID()));
              export = list.stream().anyMatch(item -> item.getId().equals(aId));
            } catch (Exception e) {
              return false;
            }
          }
          return export.booleanValue();
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
                setClassList(PersistenceFacade.instance().getSchoolClass(this));
            } catch (PersistenceException e) {
              LOG.log(Level.SEVERE,null,e);
              return new SchoolClass[0]; // FIXME
            }

            return super.getClassList();
        }

//        /* (non-Javadoc)
//         * @see fi.dwo.client.domain.School#getSchoolGroupList()
//         */
//        public SchoolGroup[] getSchoolGroupList() {
//            if (super.getSchoolGroupList() == null) {
//                try {
//                    setSchoolGroupList(SCHOOLGROUP_MAPPER.get(this));
//                } catch (IOException e) {
//    
//                    LOG.log(Level.SEVERE,null,e);
//                } catch (SQLException e) {
//    
//                    LOG.log(Level.SEVERE,null,e);
//                } catch (XmlRpcException e) {
//    
//                    LOG.log(Level.SEVERE,null,e);
//                } catch (PersistenceException e) {
//                  LOG.log(Level.SEVERE,null,e);
//                }
//            }
//            return super.getSchoolGroupList();
//        }

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

//    private static final String TABLENAME = "tblSchool";
//
//    private static final String IDCOL = "schoolID";
//
//    private static final String ORDERCOL = "schoolName";

    /**
     *
     */
    SchoolMapper() {

    }

    /**
     * @param oid
     * @param obj
     * @throws java.io.IOException
     * @throws org.apache.xmlrpc.applet.XmlRpcException
     * @throws java.sql.SQLException
     *
     */
    //@Override
    void put(int oid, School obj)  {
        System.err.println("SchoolMapper.put() Not yet implemented!");

    }

    /**
     * @param data
     * @return Object
     * @throws java.io.IOException
     * @throws org.apache.xmlrpc.applet.XmlRpcException
     * @throws java.sql.SQLException
     * @throws PersistenceException 
     *
     */
    //@Override
//    School getObjectFromReturn(Hashtable data) throws IOException, SQLException, XmlRpcException, PersistenceException {
//        School s = null;
//        if (data.get("schoolID") == null) { //We don't know enough to make a
//            // schoolobject
//            return null;
//        } else if (data.get("schoolID") instanceof String) { //If it is a string, it was null
//            return null;
//        } else if (objects.containsKey(data.get("schoolID"))) { // Did we knew
//            // the school?
//            s = objects.get(data.get("schoolID"));
//        } else {
//            s = new LazySchool();
//        }
//        s = update(s, data);
//        if (!objects.containsKey(new Integer(s.getSchoolID()))) {
//            objects.put(new Integer(s.getSchoolID()), s);
//        }
//        return s;
//    }

    /**
     * @param obj
     * @return Object[]
     * @throws java.io.IOException
     * @throws org.apache.xmlrpc.applet.XmlRpcException
     * @throws java.sql.SQLException
     *
     */
    //@Override
    School[] get(Object obj) throws IOException {
        if (Boolean.TRUE.equals(obj)) {
//            Hashtable h = new Hashtable();
//            h.put("export", obj);
//            return super.get(h);
          try {
            return getFromExport();
        } catch (Dwo2Exception e) {
            throw new IOException(e.getDwo2Message(), e);
          }
        }
        return get();
    }

    School[] getFromExport() throws Dwo2Exception {
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
          s.setSchoolID(id);
          s.setExport(true);
          objects.put(Integer.valueOf(id), s);
        }
        result[i] = s;
      }
      return result;
    }

//    /*
//     * (non-Javadoc)
//     * 
//     * @see fi.dwo.client.persistence.XmlRpcMapper#getIDCol()
//     */
//    //@Override
//    String getIDCol() {
//        return IDCOL;
//    }
//
//    /*
//     * (non-Javadoc)
//     * 
//     * @see fi.dwo.client.persistence.XmlRpcMapper#getTableName()
//     */
//    //@Override
//    String getTableName() {
//        return TABLENAME;
//    }

//    /*
//     * (non-Javadoc)
//     * 
//     * @see fi.dwo.client.persistence.XmlRpcMapper#update(java.lang.Object,
//     *      java.util.Hashtable)
//     */
//    //@Override
//    School update(School obj, Hashtable data) throws IOException, SQLException, XmlRpcException, PersistenceException {
//        School s = obj;
//        s.setSchoolID(((Integer) data.get("schoolID")).intValue());
//        s.setName((String) data.get("schoolName"));
//        s.setSchoolLogin((String) data.get("schoollogin"));
//        if (!(s instanceof LazySchool)) {
//            s.setSchoolGroupList(SCHOOLGROUP_MAPPER.get(s));
//        } else {
//            s.setSchoolGroupList(null);
//        }
//        if (data.contains("image") && (!data.get("image").equals(""))) {
//            s.setImage((String) data.get("image"));
//        }
//        if (!(s instanceof LazySchool) && s.getClassList() == null) {
//            SchoolClass[] schoolClasses;
//            schoolClasses = PersistenceFacade.instance().getSchoolClass(s);
//            s.setClassList(schoolClasses);
//        } else if (s instanceof LazySchool) {
//            s.setClassList(null);
//        }
//        
//        if (data.containsKey("aboType")) {
//          s.setAboType(AboType.values()[ (Integer) data.get("aboType")]);
//        }
//
//        //if(s.getClassList() == null) {
//        //    s.setClassList((SchoolClass[]) MapperCreator.instance(SchoolClass.class).get(s));
//        //}
//        s.setExport(Boolean.TRUE.equals(data.get("export")));
//        s.setRights((String) data.get("schoolRights"));
//        Object expire = data.get("expire");
//        if (expire instanceof Date) {
//        	    Date date = (Date) expire;
//        	    Calendar c = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
//        	    c.setTime(date);
//        	    date = new Date(c.get(Calendar.YEAR)-1900,c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
//            s.setExpire(date);
//        } else {
//            s.setExpire(null);
//        }
//        return s;
//    }

    /* (non-Javadoc)
     * @see fi.dwo.client.persistence.XmlRpcMapper#createArray(int)
     */
    //@Override
    School[] createArray(int size) {
        return new School[size];
    }

//    /* (non-Javadoc)
//     * @see fi.dwo.client.persistence.XmlRpcMapper#getOrderbyCol()
//     */
//    //@Override
//    String getOrderbyCol() {
//        return ORDERCOL;
//    }

    //@Override
    School[] get() throws IOException {
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


    School[] getObjectFromDom(Collection<? extends DomSchool> data) {
      School[] oa = createArray(data.size());
      int i = 0;
      for(Object element: data) {
        if (element instanceof DomSchool4DwoAdmin) {
          oa[i] = getObjectFromReturn((DomSchool4DwoAdmin) element);
        } else
        if (element instanceof DomSchool) {
          oa[i] = getObjectFromSchool( (DomSchool) element);
        }
        i++;
      }
      
      return oa;
    }
    
    
//    //@Override
//    School[] getObjectFromReturn(Vector data)
//        throws IOException, SQLException, XmlRpcException, PersistenceException {
//      int i;
//      School[] oa = createArray(data.size());
//      for (i = 0; i < data.size(); i++) {
//          Object element = data.elementAt(i);
//          if (element instanceof DomSchool4DwoAdmin) {
//            oa[i] = getObjectFromReturn((DomSchool4DwoAdmin) element);
//          } else
//          if (element instanceof DomSchool) {
//            oa[i] = getObjectFromSchool( (DomSchool) element);
//          } else 
//            oa[i] = getObjectFromReturn((Hashtable) element);
//      }
//      
//      return oa;
//    }

    private School getObjectFromSchool(DomSchool element) {
      int id = PersistenceFacade.idOf(element.getId());
      School s = objects.get(id);
      if(s == null) {
        s = new LazySchool();
      }
      s.setDomSchool(element);
      objects.putIfAbsent(id, s);
      return s;
    }
    
    
}
