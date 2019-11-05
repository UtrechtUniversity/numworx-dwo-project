// Source file:
// N:\\transferzone\\intern\\Afstudeerders_basw_thijsk\\April\\Implementatie\\fi\\dwo\\client\\persistence\\XmlRpcMapper.java
package fi.dwo.dwojapplet.persistence;

import fi.dwo.commons.exceptions.PersistenceException;
import fi.dwo.commons.persistence.DbAccessIF;
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
abstract class XmlRpcMapper<T>  {

     Hashtable<Integer, T> objects = new Hashtable<>();

     void removeObject(int key) {
        objects.remove(new Integer(key));
    }

     void removeAllObjects() {
        objects.clear();
    }

    /**
     * Returns all the objects with the wheredef as restriction.
     *
     * @param wheredef The restriction-values mapped on their columnnames.
     * @return All the objects who satisfies the wheredef.
     * @throws IOException
     * @throws XmlRpcException
     * @throws SQLException
     * @throws PersistenceException 
     */
    T[] get(Hashtable wheredef) throws IOException, XmlRpcException, SQLException, PersistenceException {
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
     * @throws PersistenceException 
     */
    T get(int oid) throws IOException, XmlRpcException,
            SQLException, PersistenceException {
        if (objects.containsKey(new Integer(oid))) {
            return objects.get(new Integer(oid));
        } else {
            DbAccessIF dbAccess = DbAccessCreator.instance();
            T obj = getObjectFromReturn(dbAccess.getRecord(getTableName(),
                    getIDCol(), oid));
            return obj;
        }
    }

    abstract T getObjectFromReturn(Hashtable record) throws IOException, SQLException, XmlRpcException, PersistenceException;

    /**
     *
     */
    XmlRpcMapper() {

    }

     abstract T[] createArray(int size);

    /**
     * Returns the ID column of the object corresponding to this mapper. e.g.
     * The CourseMapper returns "courseID"
     *
     * @return The ID column of the object corresponding to this mapper.
     */
     abstract String getIDCol();

    /**
     * Returns the TableName of the object corresponding to this mapper. e.g.
     * The CourseMapper returns "tblCourse"
     *
     * @return The TableName of the object corresponding to this mapper.
     */
     abstract String getTableName();

    /**
     * Returns the column name, that is default to sort the results. e.g. The
     * CourseMapper returns "coursename"
     *
     * @return the column name, that is default to sort the results.
     */
     abstract String getOrderbyCol();

    /* (non-Javadoc)
     * @see fi.dwo.client.persistence.MapperIF#getObjectFromReturn(java.util.Vector)
     */

     T[] getObjectFromReturn(Vector data) throws IOException,
            SQLException, XmlRpcException, PersistenceException {
        int i;
        T[] oa = createArray(data.size());
        for (i = 0; i < data.size(); i++) {
            oa[i] = getObjectFromReturn((Hashtable) data.elementAt(i));
        }

        return oa;
    }
}
