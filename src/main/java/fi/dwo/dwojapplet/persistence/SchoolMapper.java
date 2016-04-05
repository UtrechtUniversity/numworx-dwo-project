// Source file:
// N:\\transferzone\\intern\\Afstudeerders_basw_thijsk\\April\\Implementatie\\fi\\dwo\\client\\persistence\\SchoolMapper.java
package fi.dwo.dwojapplet.persistence;

import fi.dwo.dwojapplet.domain.School;
import fi.dwo.dwojapplet.domain.SchoolClass;
import fi.dwo.dwojapplet.domain.SchoolGroup;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;
import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.xmlrpc.applet.XmlRpcException;

class SchoolMapper extends XmlRpcMapper {
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
                setClassList((SchoolClass[]) MapperCreator.instance(SchoolClass.class).get(this));
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
                    setSchoolGroupList((SchoolGroup[]) MapperCreator.instance(SchoolGroup.class).get(this));
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
    public void put(int oid, Object obj) throws IOException, SQLException,
            XmlRpcException {
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
    public Object getObjectFromReturn(Hashtable data) throws IOException, SQLException, XmlRpcException {
        School s = null;
        if (data.get("schoolID") == null) { //We don't know enough to make a
            // schoolobject
            return null;
        } else if (data.get("schoolID") instanceof String) { //If it is a string, it was null
            return null;
        } else if (objects.containsKey(data.get("schoolID"))) { // Did we knew
            // the school?
            s = (School) objects.get(data.get("schoolID"));
        } else {
            s = new LazySchool();
        }
        s = (School) update(s, data);
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
    public Object[] get(Object obj) throws IOException, SQLException,
            XmlRpcException {
        if (Boolean.TRUE.equals(obj)) {
            Hashtable h = new Hashtable();
            h.put("export", obj);
            return super.get(h);
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
    protected Object update(Object obj, Hashtable data) throws IOException, SQLException, XmlRpcException {
        School s = (School) obj;
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

        //if(s.getClassList() == null) {
        //    s.setClassList((SchoolClass[]) MapperCreator.instance(SchoolClass.class).get(s));
        //}
        s.setExport(Boolean.TRUE.equals(data.get("export")));
        s.setRights((String) data.get("schoolRights"));
        Object expire = data.get("expire");
        if (expire instanceof Date) {
            s.setExpire((Date) expire);
        } else {
            s.setExpire(null);
        }
        return s;
    }

    /* (non-Javadoc)
     * @see fi.dwo.client.persistence.XmlRpcMapper#createArray(int)
     */
    @Override
    protected Object[] createArray(int size) {
        return new School[size];
    }

    /* (non-Javadoc)
     * @see fi.dwo.client.persistence.XmlRpcMapper#getOrderbyCol()
     */
    @Override
    protected String getOrderbyCol() {
        return ORDERCOL;
    }
}
