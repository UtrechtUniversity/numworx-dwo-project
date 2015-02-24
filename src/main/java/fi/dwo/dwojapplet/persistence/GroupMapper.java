// Source file:
// N:\\transferzone\\intern\\Afstudeerders_basw_thijsk\\April\\Implementatie\\fi\\dwo\\client\\persistence\\GroupMapper.java
package fi.dwo.dwojapplet.persistence;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Hashtable;

import org.apache.xmlrpc.applet.XmlRpcException;

import fi.dwo.dwojapplet.domain.Group;

public class GroupMapper extends XmlRpcMapper {

    private static final String TABLENAME = "tblGroup";

    private static final String IDCOL = "groupID";

    private static final String ORDERCOL = "groupname";

    /**
     *
     */
    public GroupMapper() {

    }

    /**
     * @param oid
     * @param obj
     * @throws org.apache.xmlrpc.applet.XmlRpcException
     * @throws java.sql.SQLException
     *
     */
    @Override
    public void put(int oid, Object obj) throws IOException, SQLException,
            XmlRpcException {
        System.err.println("GroupMapper.put() Not yet implemented!");

    }

    /**
     * @param data
     * @return Object
     *
     */
    @Override
    public Object getObjectFromReturn(Hashtable data) {
        Group g = null;
        if (data.get("groupID") == null) { //We don't know enough to make a
            // groupobject
            return null;
        } else if (objects.containsKey(data.get("groupID"))) { // Did we know
            // the group?
            g = (Group) objects.get(data.get("groupID"));
        } else {
            g = new Group();
        }
        g = (Group) update(g, data);
        if (!objects.containsKey(new Integer(g.getGroupID()))) {
            objects.put(new Integer(g.getGroupID()), g);
        }
        return g;
    }

    /**
     * @param obj
     * @return Object[]
     * @throws org.apache.xmlrpc.applet.XmlRpcException
     * @throws java.sql.SQLException
     *
     */
    @Override
    public Object[] get(Object obj) throws IOException, SQLException,
            XmlRpcException {
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
    protected Object update(Object obj, Hashtable data) {
        Group g = (Group) obj;
        g.setGroupID(((Integer) data.get("groupID")).intValue());
        g.setName((String) data.get("groupname"));

        return g;
    }

    /* (non-Javadoc)
     * @see fi.dwo.client.persistence.XmlRpcMapper#createArray(int)
     */
    @Override
    protected Object[] createArray(int size) {
        return new Group[size];
    }

    /* (non-Javadoc)
     * @see fi.dwo.client.persistence.XmlRpcMapper#getOrderbyCol()
     */
    @Override
    protected String getOrderbyCol() {
        return ORDERCOL;
    }
}
