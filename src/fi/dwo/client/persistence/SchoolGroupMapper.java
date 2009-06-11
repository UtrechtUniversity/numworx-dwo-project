// Source file:
// N:\\transferzone\\intern\\Afstudeerders_basw_thijsk\\April\\Implementatie\\fi\\dwo\\client\\persistence\\GroupMapper.java

package fi.dwo.client.persistence;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Hashtable;

import org.apache.xmlrpc.applet.XmlRpcException;

import fi.dwo.client.domain.SchoolGroup;
import fi.dwo.client.domain.School;

public class SchoolGroupMapper extends XmlRpcMapper {

    private static final String TABLENAME = "tblSchoolGroup";

    private static final String IDCOL = "schoolGroupID";

    private static final String ORDERCOL = "groupID";
    
    /**

     */
    public SchoolGroupMapper() {

    }

    /**
     * @param oid
     * @param obj

     */
    public void put(int oid, Object obj) throws IOException, SQLException,
            XmlRpcException {
        System.err.println("GroupMapper.put() Not yet implemented!");

    }

    /**
     * @param data
     * @return Object

     */
    public Object getObjectFromReturn(Hashtable data) {
        SchoolGroup g = null;
        if (data.get("schoolGroupID") == null) { //We don't know enough to make a
                                           // groupobject
            return null;
        } else if (objects.containsKey(data.get("schoolGroupID"))) { // Did we know
                                                               // the group?
            g = (SchoolGroup) objects.get(data.get("schoolGroupID"));
        } else {
            g = new SchoolGroup();
        }
        g = (SchoolGroup) update(g, data);
        if(!objects.containsKey(new Integer(g.getSchoolGroupID()))) {
            objects.put(new Integer(g.getSchoolGroupID()), g);
        }
        return g;
    }

    /**
     * @param obj
     * @return Object[]

     */
    public Object[] get(Object obj) throws IOException, SQLException,
            XmlRpcException {
        Hashtable ht = new Hashtable();
        if(obj instanceof School) {
            School sc = (School) obj;
            ht.put("schoolID", new Integer(sc.getSchoolID()));
        }
        return super.get(ht);
    }

    /*
     * (non-Javadoc)
     * 
     * @see fi.dwo.client.persistence.XmlRpcMapper#getIDCol()
     */
    protected String getIDCol() {
        return IDCOL;
    }

    /*
     * (non-Javadoc)
     * 
     * @see fi.dwo.client.persistence.XmlRpcMapper#getTableName()
     */
    protected String getTableName() {
        return TABLENAME;
    }

    /*
     * (non-Javadoc)
     * 
     * @see fi.dwo.client.persistence.XmlRpcMapper#update(java.lang.Object,
     *      java.util.Hashtable)
     */
    protected Object update(Object obj, Hashtable data) {
        SchoolGroup g = (SchoolGroup) obj;
        g.setSchoolGroupID(((Integer) data.get("schoolGroupID")).intValue());
        g.setGroupID(((Integer) data.get("groupID")).intValue());
        g.setSchoolID(((Integer) data.get("schoolID")).intValue());
        g.setPasswd((String) data.get("passwd"));

        return g;
    }

    /* (non-Javadoc)
     * @see fi.dwo.client.persistence.XmlRpcMapper#createArray(int)
     */
    protected Object[] createArray(int size) {
        return new SchoolGroup[size];
    }

    /* (non-Javadoc)
     * @see fi.dwo.client.persistence.XmlRpcMapper#getOrderbyCol()
     */
    protected String getOrderbyCol() {
        return ORDERCOL;
    }
}