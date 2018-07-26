// Source file:
// N:\\transferzone\\intern\\Afstudeerders_basw_thijsk\\April\\Implementatie\\fi\\dwo\\client\\persistence\\GroupMapper.java
package fi.dwo.dwojapplet.persistence;

import fi.dwo.dwojapplet.domain.Group;
import nl.uu.fi.dwo.lms.jclient.lib.rest.managers.PublicRoleManager;
import nl.uu.fi.dwo.rest.dom.entities.DomRole;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.List;

import org.apache.xmlrpc.applet.XmlRpcException;

class GroupMapper extends XmlRpcMapper<Group> {

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
     * @throws java.io.IOException
     * @throws org.apache.xmlrpc.applet.XmlRpcException
     * @throws java.sql.SQLException
     *
     */
    @Override
    public void put(int oid, Group obj)  {
        System.err.println("GroupMapper.put() Not yet implemented!");

    }

    @Override
    public Group[] get() throws IOException {
      try {
        List<DomRole> list = PublicRoleManager.getRoles();
        Group[] result = createArray(list.size());
        for (int i = 0; i < result.length; i++) {
          result[i] = getObjectFromReturn(list.get(i));
        }
        return result;
      } catch (Dwo2Exception e) {
        throw new IOException(e.getDwo2Message(), e);
      }
    }

    private Group getObjectFromReturn(DomRole role) {
      int id = PersistenceFacade.idOf(role.getId());
      Group g = objects.get(id);
      if(g == null) {
        g = new Group();
        g.setGroupID(id);
      }
      g.setName(role.getRoleName());
      objects.putIfAbsent(id, g);
      return g;
    }

    /**
     * @param data
     * @return Object
     *
     */
    @Override
    public Group getObjectFromReturn(Hashtable data) {
        Group g = null;
        if (data.get("groupID") == null) { //We don't know enough to make a
            // groupobject
            return null;
        } else if (objects.containsKey(data.get("groupID"))) { // Did we know
            // the group?
            g = objects.get(data.get("groupID"));
        } else {
            g = new Group();
        }
        g = update(g, data);
        if (!objects.containsKey(new Integer(g.getGroupID()))) {
            objects.put(new Integer(g.getGroupID()), g);
        }
        return g;
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
    public Group[] get(Object obj) throws IOException, SQLException,
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
    protected Group update(Group obj, Hashtable data) {
        Group g = (Group) obj;
        g.setGroupID(((Integer) data.get("groupID")).intValue());
        g.setName((String) data.get("groupname"));

        return g;
    }

    /* (non-Javadoc)
     * @see fi.dwo.client.persistence.XmlRpcMapper#createArray(int)
     */
    @Override
    protected Group[] createArray(int size) {
        return new Group[size];
    }

    /* (non-Javadoc)
     * @see fi.dwo.client.persistence.XmlRpcMapper#getOrderbyCol()
     */
    @Override
    protected String getOrderbyCol() {
        return ORDERCOL;
    }

    @Override
    public Group get(int uid, Integer sgid) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
