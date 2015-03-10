// Source file:
// N:\\transferzone\\intern\\Afstudeerders_basw_thijsk\\April\\Implementatie\\fi\\dwo\\client\\persistence\\ClassMapper.java
package fi.dwo.dwojapplet.persistence;

import fi.dwo.dwojapplet.domain.School;
import fi.dwo.dwojapplet.domain.SchoolClass;
import fi.dwo.dwojapplet.domain.Teacher;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Hashtable;
import org.apache.xmlrpc.applet.XmlRpcException;

class ClassMapper extends XmlRpcMapper {

    private static final String TABLENAME = "tblClass";

    private static final String IDCOL = "classID";

    private static final String ORDERCOL = "class";

    /**
     *
     */
    public ClassMapper() {

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
        System.err.println("ClassMapper.put() Not yet implemented!");

    }

    /**
     * @param data
     * @return Object
     *
     */
    @Override
    public Object getObjectFromReturn(Hashtable data) {
        SchoolClass c = null;
        if (data == null || data.get("classID") == null) { //We don't know enough to make a
            // classobject
            return null;
        } else if (data.get("classID") instanceof String) { //If it is a string, it was null
            return null;
        } else if (objects.containsKey(data.get("classID"))) { // Did we know
            // the class?
            c = (SchoolClass) objects.get(data.get("classID"));
        } else {
            c = new SchoolClass();
        }
        c = (SchoolClass) update(c, data);
        if (!objects.containsKey(new Integer(c.getID()))) {
            objects.put(new Integer(c.getID()), c);
        }
        return c;
    }

    /**
     * Returns all the SchoolClasses whith the object as restriction.
     *
     * @param obj The object who specifies the restriction. possible objects
     * are:
     * <ul>
     * <li><code>Teacher</code>: The classes of the teacher are returned;
     * <li><code>School</code>: The classes of the school are returned;
     * </ul>
     * @return The SchoolClasses who satisfy to the restriction.
     * @throws java.io.IOException
     * @throws org.apache.xmlrpc.applet.XmlRpcException
     * @throws java.sql.SQLException
     */
    @Override
    public Object[] get(Object obj) throws IOException, SQLException,
            XmlRpcException {
        Hashtable ht = new Hashtable();
        if (obj instanceof Teacher) {
            Teacher t = (Teacher) obj;
            ht.put("userID", new Integer(t.getID()));
        } else if (obj instanceof School) {
            School s = (School) obj;
            ht.put("schoolID", new Integer(s.getSchoolID()));
        }
        return super.get(ht);
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
    protected Object update(Object obj, Hashtable data) {
        SchoolClass c = (SchoolClass) obj;
        c.setClassID(((Integer) data.get("classID")).intValue());
        c.setClassName((String) data.get("class"));
        c.setIconizer(Boolean.TRUE.equals(data.get("iconizer")));
        return c;
    }

    /* (non-Javadoc)
     * @see fi.dwo.client.persistence.XmlRpcMapper#createArray(int)
     */
    @Override
    protected Object[] createArray(int size) {
        return new SchoolClass[size];
    }

    /* (non-Javadoc)
     * @see fi.dwo.client.persistence.XmlRpcMapper#getOrderbyCol()
     */
    @Override
    protected String getOrderbyCol() {
        return ORDERCOL;
    }
}
