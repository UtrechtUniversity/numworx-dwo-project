// Source file:
// N:\\transferzone\\intern\\Afstudeerders_basw_thijsk\\April\\Implementatie\\fi\\dwo\\client\\persistence\\ScoMapper.java

package fi.dwo.dwojapplet.persistence;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Hashtable;

import org.apache.xmlrpc.applet.XmlRpcException;

import fi.dwo.client.domain.DwoProfile;

public class DwoProfileMapper extends XmlRpcMapper {

    private static final String TABLENAME = "tblDwoProfile";

    private static final String IDCOL = "dwoProfileID";

    private static final String ORDERCOL = "dwoProfileName";

    /**

     */
    public DwoProfileMapper() {

    }

    /**
     * @param oid
     * @param obj

     */
    public void put(int oid, Object obj) throws IOException, SQLException,
            XmlRpcException {
        System.err.println("DwoProfileMapper.put() Not yet implemented!");
    }

    /**
     * @param data
     * @return Object

     */
    public Object getObjectFromReturn(Hashtable data) throws IOException, SQLException, XmlRpcException {
        DwoProfile dp = null;
        if (data.get("dwoProfileID") == null) { //We don't know enough to make a
                                         // DwoProfileobject
            return null;
        } else if (objects.containsKey(data.get("dwoProfileID"))) { // Did we know the
                                                             // DwoProfile?
            dp = (DwoProfile) objects.get(data.get("dwoProfileID"));
        } else {
            dp = new DwoProfile();
        }
        dp = (DwoProfile) update(dp, data);
        if(!objects.containsKey(new Integer(dp.getID()))) {
            objects.put(new Integer(dp.getID()), dp);
        }
        return dp;
    }

    /**
     * @param obj
     * @return Object[]

     */
    public Object[] get(Object obj) throws IOException, SQLException,
            XmlRpcException {
        return super.get();
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
    protected Object update(Object obj, Hashtable data) throws IOException, SQLException, XmlRpcException {
        DwoProfile dp = (DwoProfile) obj;
        dp.setID(((Integer) data.get("dwoProfileID")).intValue());
        dp.setName((String) data.get("dwoProfileName"));
        dp.setDescription((String) data.get("dwoProfileDescription"));
        dp.setText((String) data.get("dwoProfileText"));
        dp.setRights((String)data.get("dwoProfileRights"));
        return dp;
    }

    /* (non-Javadoc)
     * @see fi.dwo.client.persistence.XmlRpcMapper#createArray(int)
     */
    protected Object[] createArray(int size) {
       return new DwoProfile[size];
    }

    /* (non-Javadoc)
     * @see fi.dwo.client.persistence.XmlRpcMapper#getOrderbyCol()
     */
    protected String getOrderbyCol() {
        return ORDERCOL;
    }
}