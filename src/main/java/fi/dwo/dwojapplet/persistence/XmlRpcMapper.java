// Source file:
// N:\\transferzone\\intern\\Afstudeerders_basw_thijsk\\April\\Implementatie\\fi\\dwo\\client\\persistence\\XmlRpcMapper.java
package fi.dwo.dwojapplet.persistence;

import fi.dwo.commons.exceptions.PersistenceException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.Vector;
import org.apache.xmlrpc.applet.XmlRpcException;

/**
 * This class contains basic functionality for the mappers who communicate with
 * the DbAccessIF.
 *
 * @author M.J.B. Kupers
 *
 */
abstract class XmlRpcMapper implements MapperIF {

    protected Hashtable objects = new Hashtable();

    @Override
    public void removeObject(int key) {
        objects.remove(new Integer(key));
    }

    @Override
    public void removeAllObjects() {
        objects.clear();
    }

    /**
     * Returns alle the objects representing the specified mapper.
     *
     * @return All the objects representing the specified mapper.
     * @throws java.io.IOException
     * @throws java.sql.SQLException
     * @see fi.dwo.client.persistence.MapperIF#get()
     */
    @Override
    public Object[] get() throws IOException, XmlRpcException, SQLException {
        DbAccessIF dbAccess = DbAccessCreator.instance();
        return getObjectFromReturn(dbAccess.getTable(getTableName(), getOrderbyCol()));
    }

    /**
     * Returns all the objects with the wheredef as restriction.
     *
     * @param wheredef The restriction-values mapped on their columnnames.
     * @return All the objects who satisfies the wheredef.
     * @throws IOException
     * @throws XmlRpcException
     * @throws SQLException
     */
    public Object[] get(Hashtable wheredef) throws IOException, XmlRpcException, SQLException {
        DbAccessIF dbAccess = DbAccessCreator.instance();
        return getObjectFromReturn(dbAccess.getTable(getTableName(), wheredef, getOrderbyCol()));
    }

    /**
     * Returns the object with the specified ID.
     *
     * @param oid The ID of the object to return.
     * @return The object representing the oid.
     * @throws java.io.IOException
     * @throws java.sql.SQLException
     * @throws org.apache.xmlrpc.applet.XmlRpcException
     */
    @Override
    public Object get(int oid) throws IOException, XmlRpcException,
            SQLException {
        if (objects.containsKey(new Integer(oid))) {
            return objects.get(new Integer(oid));
        } else {
            DbAccessIF dbAccess = DbAccessCreator.instance();
            Object obj = getObjectFromReturn(dbAccess.getRecord(getTableName(),
                    getIDCol(), oid));
            if (obj != null) {
                //               objects.put(new Integer(oid), obj);
            }
            return obj;
        }
    }

    /**
     *
     */
    public XmlRpcMapper() {

    }

    protected abstract Object update(Object obj, Hashtable data) throws IOException, SQLException, XmlRpcException, PersistenceException;

    protected abstract Object[] createArray(int size);

    /**
     * Returns the ID column of the object corresponding to this mapper. e.g.
     * The CourseMapper returns "courseID"
     *
     * @return The ID column of the object corresponding to this mapper.
     */
    protected abstract String getIDCol();

    /**
     * Returns the TableName of the object corresponding to this mapper. e.g.
     * The CourseMapper returns "tblCourse"
     *
     * @return The TableName of the object corresponding to this mapper.
     */
    protected abstract String getTableName();

    /**
     * Returns the column name, that is default to sort the results. e.g. The
     * CourseMapper returns "coursename"
     *
     * @return the column name, that is default to sort the results.
     */
    protected abstract String getOrderbyCol();
    /* (non-Javadoc)
     * @see fi.dwo.client.persistence.MapperIF#getObjectFromReturn(java.util.Vector)
     */

    @Override
    public Object[] getObjectFromReturn(Vector data) throws IOException,
            SQLException, XmlRpcException {
        int i;
        Object[] oa = createArray(data.size());
        for (i = 0; i < data.size(); i++) {
            oa[i] = getObjectFromReturn((Hashtable) data.elementAt(i));
        }

        return oa;
    }
}
